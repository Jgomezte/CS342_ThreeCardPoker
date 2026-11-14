package edu.uic.cs342.poker.controller;

import edu.uic.cs342.poker.model.Card;
import edu.uic.cs342.poker.model.GameService;
import edu.uic.cs342.poker.model.Rank;
import edu.uic.cs342.poker.model.Suit;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class PlayController {

    // ---------- FXML fields ----------

    @FXML private Label phaseLabel;
    @FXML private Label feedbackLabel;

    @FXML private Label p1TotalLabel;
    @FXML private Label p2TotalLabel;

    @FXML private StackPane dealerCard1, dealerCard2, dealerCard3;
    @FXML private StackPane p1Card1, p1Card2, p1Card3;
    @FXML private StackPane p2Card1, p2Card2, p2Card3;

    @FXML private Button dealButton;
    @FXML private Button p1PlayButton, p1FoldButton;
    @FXML private Button p2PlayButton, p2FoldButton;
    @FXML private TextField p1PairPlusField, p1AnteField;
    @FXML private TextField p2PairPlusField, p2AnteField;

    @FXML private StackPane infoOverlay;
    @FXML private VBox infoPage1;
    @FXML private VBox infoPage2;
    @FXML private Button infoPrevBtn;
    @FXML private Button infoNextBtn;
    @FXML private Label infoPageIndicator;


    private static final int BET_STEP = 5;

    private static final String HIDDEN_CLASS = "card-hidden";
    private static final String ERROR_STYLE = "-fx-text-fill: #ff8a80;";

    private GameService game;
    private StackPane[] dealerCards;
    private StackPane[] p1Cards;
    private StackPane[] p2Cards;

    @FXML
    private void initialize() {
        game = new GameService();
        dealerCards = new StackPane[] { dealerCard1, dealerCard2, dealerCard3 };
        p1Cards = new StackPane[] { p1Card1, p1Card2, p1Card3 };
        p2Cards = new StackPane[] { p2Card1, p2Card2, p2Card3 };

        resetWagerFields();
        prepareWagerPhase();
        updateTotals();
    }


    @FXML
    private void handleDeal() {
        try {
            switch (game.getPhase()) {
                case PLACE_WAGERS -> dealRound();
                case ROUND_OVER -> {
                    game.startNextRound();
                    prepareWagerPhase();
                }
                case GAME_OVER -> {
                    game.startNewGame();
                    resetWagerFields();
                    prepareWagerPhase();
                    updateTotals();
                }
                default -> throw new IllegalStateException(
                        "Both players must choose Play or Fold first.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            showError(e.getMessage());
        }
    }

    private void dealRound() {
        game.placeWagers(
                parseAmount(p1AnteField, false, "Player 1 Ante"),
                parseAmount(p1PairPlusField, true, "Player 1 Pair Plus"),
                parseAmount(p2AnteField, false, "Player 2 Ante"),
                parseAmount(p2PairPlusField, true, "Player 2 Pair Plus"));
        game.dealCards();

        showHand(p1Cards, game.getPlayer(1).getHand());
        showHand(p2Cards, game.getPlayer(2).getHand());
        for (StackPane slot : dealerCards) {
            showFaceDown(slot);
        }

        setWagerControlsDisabled(true);
        dealButton.setDisable(true);
        setDecisionButtonsDisabled(1, false);
        setDecisionButtonsDisabled(2, false);

        updatePhaseLabel();
        showInfo("Each player: look at your cards, then choose PLAY or FOLD.");
    }


    @FXML private void handleP1Play() { decide(1, true); }
    @FXML private void handleP1Fold() { decide(1, false); }
    @FXML private void handleP2Play() { decide(2, true); }
    @FXML private void handleP2Fold() { decide(2, false); }

    private void decide(int playerNumber, boolean play) {
        try {
            if (play) {
                game.recordPlay(playerNumber);
            } else {
                game.recordFold(playerNumber);
            }
            setDecisionButtonsDisabled(playerNumber, true);

            if (game.allPlayersDecided()) {
                finishRound();
            } else {
                showInfo("Player " + playerNumber + (play ? " plays." : " folds.")
                        + " Waiting for the other player.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            showError(e.getMessage());
        }
    }

    private void finishRound() {
        game.settleRound();

        if (game.isDealerHandRevealed()) {
            showHand(dealerCards, game.getDealerHand());
        }
        updateTotals();
        updatePhaseLabel();

        StringBuilder message = new StringBuilder();
        message.append(game.isDealerQualified()
                ? "Dealer qualifies."
                : "Dealer does not qualify (needs Queen-high or better).");
        message.append("\n").append(describe(1));
        message.append("\n").append(describe(2));

        if (game.isGameOver()) {
            for (int n = 1; n <= 2; n++) {
                if (game.getBalance(n) < 2 * GameService.MIN_BET) {
                    message.append("\nPlayer ").append(n).append(" is out of money.");
                }
            }
            message.append("\nGame over.");
            dealButton.setText("NEW GAME");
        } else {
            dealButton.setText("NEXT ROUND");
        }
        dealButton.setDisable(false);
        showInfo(message.toString());
    }

    private String describe(int n) {
        GameService.PlayerOutcome outcome = game.getOutcome(n);

        String text = switch (outcome.result()) {
            case FOLDED -> "folded and forfeits the Ante and Pair Plus";
            case DEALER_NOT_QUALIFIED -> "Ante and Play are returned";
            case WIN -> "beats the dealer";
            case LOSE -> "loses to the dealer";
            case PUSH -> "ties the dealer";
        };

        StringBuilder line = new StringBuilder("Player " + n + " " + text);
        if (outcome.result() != GameService.Result.FOLDED && outcome.pairPlusNet() != 0) {
            line.append(", Pair Plus ").append(signed(outcome.pairPlusNet()));
        }
        line.append(" (net ").append(signed(outcome.net())).append(")");
        return line.toString();
    }


    @FXML private void handleP1AnteUp() { adjust(p1AnteField, +1, true); }
    @FXML private void handleP1AnteDown() { adjust(p1AnteField, -1, true); }
    @FXML private void handleP1PairPlusUp() { adjust(p1PairPlusField, +1, false); }
    @FXML private void handleP1PairPlusDown() { adjust(p1PairPlusField, -1, false); }
    @FXML private void handleP2AnteUp() { adjust(p2AnteField, +1, true); }
    @FXML private void handleP2AnteDown() { adjust(p2AnteField, -1, true); }
    @FXML private void handleP2PairPlusUp() { adjust(p2PairPlusField, +1, false); }
    @FXML private void handleP2PairPlusDown() { adjust(p2PairPlusField, -1, false); }

    private void adjust(TextField field, int direction, boolean isAnte) {
        if (game.getPhase() != GameService.Phase.PLACE_WAGERS) {
            return; // wagers are locked once the cards are dealt
        }

        int current;
        try {
            current = parseAmount(field, true, "Wager");
        } catch (IllegalArgumentException e) {
            current = isAnte ? GameService.MIN_BET : 0;
        }

        int next = current + direction * BET_STEP;
        if (!isAnte && next > 0 && next < GameService.MIN_BET) {
            next = direction > 0 ? GameService.MIN_BET : 0; // Pair Plus is $0 or $5 and up
        }
        int min = isAnte ? GameService.MIN_BET : 0;
        next = Math.max(min, Math.min(GameService.MAX_BET, next));

        field.setText("$" + next);
    }

    private void resetWagerFields() {
        p1AnteField.setText("$" + GameService.MIN_BET);
        p2AnteField.setText("$" + GameService.MIN_BET);
        p1PairPlusField.setText("$0");
        p2PairPlusField.setText("$0");
    }


    private void prepareWagerPhase() {
        for (StackPane slot : p1Cards) {
            showBlank(slot);
        }
        for (StackPane slot : p2Cards) {
            showBlank(slot);
        }
        for (StackPane slot : dealerCards) {
            showFaceDown(slot);
        }

        setWagerControlsDisabled(false);
        setDecisionButtonsDisabled(1, true);
        setDecisionButtonsDisabled(2, true);
        dealButton.setText("DEAL");
        dealButton.setDisable(false);

        updatePhaseLabel();
        showInfo("Place an Ante ($" + GameService.MIN_BET + "-$" + GameService.MAX_BET
                + ") and an optional Pair Plus bet, then press DEAL.");
    }

    private void setWagerControlsDisabled(boolean disabled) {
        p1AnteField.setDisable(disabled);
        p1PairPlusField.setDisable(disabled);
        p2AnteField.setDisable(disabled);
        p2PairPlusField.setDisable(disabled);
    }

    private void setDecisionButtonsDisabled(int playerNumber, boolean disabled) {
        if (playerNumber == 1) {
            p1PlayButton.setDisable(disabled);
            p1FoldButton.setDisable(disabled);
        } else {
            p2PlayButton.setDisable(disabled);
            p2FoldButton.setDisable(disabled);
        }
    }

    private void updateTotals() {
        p1TotalLabel.setText("Total: " + money(game.getBalance(1)));
        p2TotalLabel.setText("Total: " + money(game.getBalance(2)));
    }

    private void updatePhaseLabel() {
        String name = switch (game.getPhase()) {
            case PLACE_WAGERS -> "PLACE WAGERS";
            case PLAY_OR_FOLD -> "PLAY OR FOLD";
            case ROUND_OVER -> "ROUND OVER";
            case GAME_OVER -> "GAME OVER";
        };
        phaseLabel.setText("PHASE: " + name);
    }

    private void showInfo(String message) {
        feedbackLabel.setStyle("");
        feedbackLabel.setText(message);
    }

    private void showError(String message) {
        feedbackLabel.setStyle(ERROR_STYLE);
        feedbackLabel.setText(message);
    }


    private void showHand(StackPane[] slots, List<Card> hand) {
        for (int i = 0; i < slots.length; i++) {
            showFaceUp(slots[i], hand.get(i));
        }
    }

    private void showFaceUp(StackPane slot, Card card) {
        slot.getStyleClass().remove(HIDDEN_CLASS);
        Label label = new Label(rankSymbol(card.getRank()) + suitSymbol(card.getSuit()));
        label.getStyleClass().add("card-text");
        if (card.getSuit() == Suit.HEARTS || card.getSuit() == Suit.DIAMONDS) {
            label.setStyle("-fx-text-fill: #c0392b;"); // red suits
        }
        slot.getChildren().setAll(label);
    }

    private void showFaceDown(StackPane slot) {
        if (!slot.getStyleClass().contains(HIDDEN_CLASS)) {
            slot.getStyleClass().add(HIDDEN_CLASS);
        }
        Label label = new Label("?");
        label.getStyleClass().add("card-back-text");
        slot.getChildren().setAll(label);
    }

    private void showBlank(StackPane slot) {
        slot.getStyleClass().remove(HIDDEN_CLASS);
        Label label = new Label("");
        label.getStyleClass().add("card-text");
        slot.getChildren().setAll(label);
    }

    private static String rankSymbol(Rank rank) {
        return switch (rank) {
            case JACK -> "J";
            case QUEEN -> "Q";
            case KING -> "K";
            case ACE -> "A";
            default -> String.valueOf(rank.getValue());
        };
    }

    private static String suitSymbol(Suit suit) {
        return switch (suit) {
            case CLUBS -> "♣";
            case DIAMONDS -> "♦";
            case HEARTS -> "♥";
            case SPADES -> "♠";
        };
    }


    // Accepts "5", "$5", " $ 5 ". Throws a user-readable IllegalArgumentException otherwise.
    private int parseAmount(TextField field, boolean blankMeansZero, String name) {
        String text = field.getText() == null ? "" : field.getText().replace("$", "").trim();
        if (text.isEmpty()) {
            if (blankMeansZero) {
                return 0;
            }
            throw new IllegalArgumentException(name + " is required.");
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " must be a whole number of dollars.");
        }
    }

    private static String money(int amount) {
        return amount < 0 ? "-$" + (-amount) : "$" + amount;
    }

    private static String signed(int amount) {
        if (amount > 0) return "+$" + amount;
        if (amount < 0) return "-$" + (-amount);
        return "$0";
    }


    @FXML
    private void handleBackToMenu(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/uic/cs342/poker/fxml/welcome.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(
                getClass().getResource("/edu/uic/cs342/poker/css/style.css").toExternalForm());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }


    @FXML
    private void handleOpenInfo() {
        showInfoPage(1);
        infoOverlay.setManaged(true);
        infoOverlay.setVisible(true);
    }

    @FXML
    private void handleCloseInfo() {
        infoOverlay.setVisible(false);
        infoOverlay.setManaged(false);
    }

    @FXML
    private void handleInfoNextPage() {
        showInfoPage(2);
    }

    @FXML
    private void handleInfoPrevPage() {
        showInfoPage(1);
    }

    private void showInfoPage(int page) {
        boolean isPage1 = (page == 1);
        infoPage1.setVisible(isPage1);
        infoPage1.setManaged(isPage1);

        infoPage2.setVisible(!isPage1);
        infoPage2.setManaged(!isPage1);

        infoPrevBtn.setVisible(!isPage1);
        infoNextBtn.setVisible(isPage1);
        infoPageIndicator.setText("Page " + page + " of 2");
    }
}