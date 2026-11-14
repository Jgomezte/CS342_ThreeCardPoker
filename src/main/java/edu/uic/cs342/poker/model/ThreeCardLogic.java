package edu.uic.cs342.poker.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ThreeCardLogic {

    public static final int EVAL_HIGH_CARD = 0;
    public static final int EVAL_STRAIGHT_FLUSH = 1;
    public static final int EVAL_THREE_OF_A_KIND = 2;
    public static final int EVAL_STRAIGHT = 3;
    public static final int EVAL_FLUSH = 4;
    public static final int EVAL_PAIR = 5;

    public static final int CMP_TIE = 0;
    public static final int CMP_DEALER_WINS = 1;
    public static final int CMP_PLAYER_WINS = 2;


    public static int evalHand(ArrayList<Card> hand) {
        return toCode(rankOf(hand));
    }

    public static int evalPPWinnings(ArrayList<Card> hand, int bet) {
        return bet * pairPlusMultiplier(rankOf(hand));
    }

    public static int compareHands(ArrayList<Card> dealer, ArrayList<Card> player) {
        int result = compareStrength(player, dealer); // positive means player is better
        if (result > 0) return CMP_PLAYER_WINS;
        if (result < 0) return CMP_DEALER_WINS;
        return CMP_TIE;
    }

    public static boolean dealerQualifies(ArrayList<Card> dealerHand) {
        if (rankOf(dealerHand) != HandRank.HIGH_CARD) {
            return true;
        }
        int highest = 0;
        for (Card card : dealerHand) {
            highest = Math.max(highest, card.getRank().getValue());
        }
        return highest >= Rank.QUEEN.getValue();
    }

    private static HandRank rankOf(List<Card> hand) {
        if (hand.size() != 3) {
            throw new IllegalArgumentException("A hand must have exactly 3 cards");
        }

        boolean flush = isFlush(hand);
        boolean straight = isStraight(hand);
        int distinctRanks = countDistinctRanks(hand);

        if (straight && flush) return HandRank.STRAIGHT_FLUSH;
        if (distinctRanks == 1) return HandRank.THREE_OF_A_KIND;
        if (straight) return HandRank.STRAIGHT;
        if (flush) return HandRank.FLUSH;
        if (distinctRanks == 2) return HandRank.PAIR;
        return HandRank.HIGH_CARD;
    }

    private static int toCode(HandRank rank) {
        return switch (rank) {
            case STRAIGHT_FLUSH -> EVAL_STRAIGHT_FLUSH;
            case THREE_OF_A_KIND -> EVAL_THREE_OF_A_KIND;
            case STRAIGHT -> EVAL_STRAIGHT;
            case FLUSH -> EVAL_FLUSH;
            case PAIR -> EVAL_PAIR;
            case HIGH_CARD -> EVAL_HIGH_CARD;
        };
    }

    private static int pairPlusMultiplier(HandRank rank) {
        return switch (rank) {
            case STRAIGHT_FLUSH -> 40;
            case THREE_OF_A_KIND -> 30;
            case STRAIGHT -> 6;
            case FLUSH -> 3;
            case PAIR -> 1;
            case HIGH_CARD -> 0;
        };
    }

    private static int compareStrength(List<Card> a, List<Card> b) {
        HandRank rankA = rankOf(a);
        HandRank rankB = rankOf(b);

        if (rankA != rankB) {
            return rankA.compareTo(rankB);
        }

        List<Integer> tieA = tiebreakValues(a, rankA);
        List<Integer> tieB = tiebreakValues(b, rankB);

        for (int i = 0; i < tieA.size(); i++) {
            int cmp = Integer.compare(tieA.get(i), tieB.get(i));
            if (cmp != 0) {
                return cmp;
            }
        }
        return 0;
    }

    private static List<Integer> tiebreakValues(List<Card> hand, HandRank rank) {
        List<Integer> values = new ArrayList<>();
        for (Card card : hand) {
            values.add(card.getRank().getValue());
        }
        values.sort(Collections.reverseOrder());

        List<Integer> result = new ArrayList<>();

        switch (rank) {
            case THREE_OF_A_KIND -> result.add(values.get(0));

            case PAIR -> {
                if (values.get(0).equals(values.get(1))) {
                    result.add(values.get(0));
                    result.add(values.get(2));
                } else {
                    result.add(values.get(1));
                    result.add(values.get(0));
                }
            }

            case STRAIGHT, STRAIGHT_FLUSH -> {
                boolean aceLow = values.get(0) == 14 && values.get(1) == 3 && values.get(2) == 2;
                result.add(aceLow ? 3 : values.get(0));
            }

            default -> result.addAll(values);
        }

        return result;
    }

    private static boolean isFlush(List<Card> hand) {
        Suit suit = hand.get(0).getSuit();
        for (Card card : hand) {
            if (card.getSuit() != suit) {
                return false;
            }
        }
        return true;
    }

    private static boolean isStraight(List<Card> hand) {
        List<Integer> values = new ArrayList<>();
        for (Card card : hand) {
            values.add(card.getRank().getValue());
        }
        Collections.sort(values);

        int a = values.get(0);
        int b = values.get(1);
        int c = values.get(2);

        boolean normalStraight = (b == a + 1) && (c == b + 1);
        boolean aceLowStraight = (a == 2 && b == 3 && c == 14);

        return normalStraight || aceLowStraight;
    }

    private static int countDistinctRanks(List<Card> hand) {
        Set<Rank> ranks = new HashSet<>();
        for (Card card : hand) {
            ranks.add(card.getRank());
        }
        return ranks.size();
    }
}