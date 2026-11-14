package edu.uic.cs342.poker.model;

import java.util.ArrayList;

public class GameService {

    public static final int MIN_BET = 5;
    public static final int MAX_BET = 25;

    public static final int STARTING_BANKROLL = 100;

    // Cheapest legal round: minimum ante plus the matching play wager.
    private static final int MIN_ROUND_COST = 2 * MIN_BET;

    public enum Phase { PLACE_WAGERS, PLAY_OR_FOLD, ROUND_OVER, GAME_OVER }

    public enum Result { FOLDED, DEALER_NOT_QUALIFIED, WIN, LOSE, PUSH }

    // Net money change for one player in one round, split by bet type.
    public record PlayerOutcome(Result result, int anteAndPlayNet, int pairPlusNet) {
        public int net() {
            return anteAndPlayNet + pairPlusNet;
        }
    }

    private final Dealer dealer = new Dealer();
    private Player[] players;
    private boolean[] decided;
    private PlayerOutcome[] outcomes;
    private ArrayList<Card> dealerHand;
    private boolean dealerQualified;
    private Phase phase;

    public GameService() {
        startNewGame();
    }

    public void startNewGame() {
        players = new Player[] { new Player(), new Player() };
        decided = new boolean[2];
        outcomes = new PlayerOutcome[2];
        dealerHand = new ArrayList<>();
        dealerQualified = false;
        dealer.shuffleDeck();
        phase = Phase.PLACE_WAGERS;
    }

    public void startNextRound() {
        requirePhase(Phase.ROUND_OVER);
        for (Player player : players) {
            player.resetRound();
        }
        decided = new boolean[2];
        outcomes = new PlayerOutcome[2];
        dealerHand = new ArrayList<>();
        dealerQualified = false;
        dealer.shuffleDeck();
        phase = Phase.PLACE_WAGERS;
    }

    // Validates both players' wagers first, then stores them. If either is
    // invalid, nothing is stored and an IllegalArgumentException is thrown
    // with a message that is safe to show to the user.
    public void placeWagers(int p1Ante, int p1PairPlus, int p2Ante, int p2PairPlus) {
        requirePhase(Phase.PLACE_WAGERS);
        validateWagers(1, p1Ante, p1PairPlus);
        validateWagers(2, p2Ante, p2PairPlus);

        players[0].setAnteBet(p1Ante);
        players[0].setPairPlusBet(p1PairPlus);
        players[1].setAnteBet(p2Ante);
        players[1].setPairPlusBet(p2PairPlus);
    }

    private void validateWagers(int playerNumber, int ante, int pairPlus) {
        String who = "Player " + playerNumber + ": ";

        if (ante < MIN_BET || ante > MAX_BET) {
            throw new IllegalArgumentException(
                    who + "Ante must be between $" + MIN_BET + " and $" + MAX_BET + ".");
        }
        if (pairPlus != 0 && (pairPlus < MIN_BET || pairPlus > MAX_BET)) {
            throw new IllegalArgumentException(
                    who + "Pair Plus must be $0 or between $" + MIN_BET + " and $" + MAX_BET + ".");
        }
        // Worst case this round: lose the ante, the matching play wager, and Pair Plus.
        if (2 * ante + pairPlus > getBalance(playerNumber)) {
            throw new IllegalArgumentException(
                    who + "not enough money for these wagers (balance $"
                            + getBalance(playerNumber) + ").");
        }
    }


    public void dealCards() {
        requirePhase(Phase.PLACE_WAGERS);
        if (players[0].getAnteBet() == 0 || players[1].getAnteBet() == 0) {
            throw new IllegalStateException("Wagers must be placed before dealing.");
        }

        players[0].setHand(dealer.dealHand());
        players[1].setHand(dealer.dealHand());
        dealerHand = dealer.dealHand();
        phase = Phase.PLAY_OR_FOLD;
    }


    public void recordPlay(int playerNumber) {
        Player player = markDecided(playerNumber);
        player.setPlayBet(player.getAnteBet()); // play wager always equals the ante
    }

    public void recordFold(int playerNumber) {
        Player player = markDecided(playerNumber);
        player.setFolded(true);
    }

    public boolean allPlayersDecided() {
        return decided[0] && decided[1];
    }

    private Player markDecided(int playerNumber) {
        requirePhase(Phase.PLAY_OR_FOLD);
        int index = indexOf(playerNumber);
        if (decided[index]) {
            throw new IllegalStateException("Player " + playerNumber + " has already decided.");
        }
        decided[index] = true;
        return players[index];
    }

    // Reveals the dealer's hand (see isDealerHandRevealed), works out each
    // player's result, and updates their cumulative winnings.
    public void settleRound() {
        requirePhase(Phase.PLAY_OR_FOLD);
        if (!allPlayersDecided()) {
            throw new IllegalStateException("Both players must choose Play or Fold first.");
        }

        dealerQualified = ThreeCardLogic.dealerQualifies(dealerHand);

        for (int i = 0; i < 2; i++) {
            outcomes[i] = settle(players[i]);
            players[i].addWinnings(outcomes[i].net());
        }

        boolean someoneBroke = getBalance(1) < MIN_ROUND_COST || getBalance(2) < MIN_ROUND_COST;
        phase = someoneBroke ? Phase.GAME_OVER : Phase.ROUND_OVER;
    }

    private PlayerOutcome settle(Player player) {
        // A folded player forfeits both the ante and Pair Plus.
        if (player.isFolded()) {
            return new PlayerOutcome(Result.FOLDED, -player.getAnteBet(), -player.getPairPlusBet());
        }

        // Pair Plus is judged on the player's own hand, independent of the dealer.
        int pairPlusNet = 0;
        if (player.getPairPlusBet() > 0) {
            int winnings = ThreeCardLogic.evalPPWinnings(player.getHand(), player.getPairPlusBet());
            pairPlusNet = winnings > 0 ? winnings : -player.getPairPlusBet();
        }

        // Dealer doesn't qualify: play wager returned.
        if (!dealerQualified) {
            return new PlayerOutcome(Result.DEALER_NOT_QUALIFIED, 0, pairPlusNet);
        }

        // Dealer qualifies: compare hands. Ante and play both pay 1:1.
        int wagered = player.getAnteBet() + player.getPlayBet();
        int comparison = ThreeCardLogic.compareHands(dealerHand, player.getHand());

        return switch (comparison) {
            case ThreeCardLogic.CMP_PLAYER_WINS -> new PlayerOutcome(Result.WIN, wagered, pairPlusNet);
            case ThreeCardLogic.CMP_DEALER_WINS -> new PlayerOutcome(Result.LOSE, -wagered, pairPlusNet);
            default -> new PlayerOutcome(Result.PUSH, 0, pairPlusNet);
        };
    }


    public Phase getPhase() {
        return phase;
    }

    public boolean isGameOver() {
        return phase == Phase.GAME_OVER;
    }

    // The controller should show the dealer's cards face up only when this is true.
    public boolean isDealerHandRevealed() {
        return phase == Phase.ROUND_OVER || phase == Phase.GAME_OVER;
    }

    public boolean isDealerQualified() {
        return dealerQualified;
    }

    public Player getPlayer(int playerNumber) {
        return players[indexOf(playerNumber)];
    }

    public ArrayList<Card> getDealerHand() {
        return new ArrayList<>(dealerHand);
    }

    // null until the round has been settled
    public PlayerOutcome getOutcome(int playerNumber) {
        return outcomes[indexOf(playerNumber)];
    }

    // Starting bankroll plus cumulative winnings (which can be negative).
    public int getBalance(int playerNumber) {
        return STARTING_BANKROLL + players[indexOf(playerNumber)].getTotalWinnings();
    }


    private int indexOf(int playerNumber) {
        if (playerNumber != 1 && playerNumber != 2) {
            throw new IllegalArgumentException("Player number must be 1 or 2.");
        }
        return playerNumber - 1;
    }

    private void requirePhase(Phase expected) {
        if (phase != expected) {
            throw new IllegalStateException("Expected phase " + expected + " but it is " + phase + ".");
        }
    }

}