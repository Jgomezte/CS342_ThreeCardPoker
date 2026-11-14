package edu.uic.cs342.poker;

import edu.uic.cs342.poker.model.Card;
import edu.uic.cs342.poker.model.Rank;
import edu.uic.cs342.poker.model.Suit;
import edu.uic.cs342.poker.model.ThreeCardLogic;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static edu.uic.cs342.poker.model.Rank.*;
import static edu.uic.cs342.poker.model.Suit.*;
import static org.junit.jupiter.api.Assertions.*;

public class ThreeCardLogicTest {
    private static Card card(String code) {
        Rank rank = switch (code.charAt(0)) {
            case '2' -> TWO;
            case '3' -> THREE;
            case '4' -> FOUR;
            case '5' -> FIVE;
            case '6' -> SIX;
            case '7' -> SEVEN;
            case '8' -> EIGHT;
            case '9' -> NINE;
            case 'T' -> TEN;
            case 'J' -> JACK;
            case 'Q' -> QUEEN;
            case 'K' -> KING;
            case 'A' -> ACE;
            default -> throw new IllegalArgumentException("Bad rank in " + code);
        };
        Suit suit = switch (code.charAt(1)) {
            case 'C' -> CLUBS;
            case 'D' -> DIAMONDS;
            case 'H' -> HEARTS;
            case 'S' -> SPADES;
            default -> throw new IllegalArgumentException("Bad suit in " + code);
        };
        return new Card(rank, suit);
    }

    private static ArrayList<Card> hand(String a, String b, String c) {
        return new ArrayList<>(List.of(card(a), card(b), card(c)));
    }


    @Test
    void evalHand_straightFlush() {
        assertEquals(ThreeCardLogic.EVAL_STRAIGHT_FLUSH,
                ThreeCardLogic.evalHand(hand("TH", "JH", "QH")));
    }

    @Test
    void evalHand_threeOfAKind() {
        assertEquals(ThreeCardLogic.EVAL_THREE_OF_A_KIND,
                ThreeCardLogic.evalHand(hand("7C", "7D", "7H")));
    }

    @Test
    void evalHand_straight() {
        // mixed suits so it can't be mistaken for a straight flush
        assertEquals(ThreeCardLogic.EVAL_STRAIGHT,
                ThreeCardLogic.evalHand(hand("QH", "KD", "AC")));
    }

    @Test
    void evalHand_flush() {
        // same suit, not consecutive
        assertEquals(ThreeCardLogic.EVAL_FLUSH,
                ThreeCardLogic.evalHand(hand("2S", "5S", "9S")));
    }

    @Test
    void evalHand_pair() {
        assertEquals(ThreeCardLogic.EVAL_PAIR,
                ThreeCardLogic.evalHand(hand("9S", "9D", "2C")));
    }

    @Test
    void evalHand_highCard() {
        assertEquals(ThreeCardLogic.EVAL_HIGH_CARD,
                ThreeCardLogic.evalHand(hand("2S", "5D", "9C")));
    }

    @Test
    void evalHand_kingAceTwoDoesNotWrapAround() {
        // straights can't wrap past the Ace, so K-A-2 is only a high card
        assertEquals(ThreeCardLogic.EVAL_HIGH_CARD,
                ThreeCardLogic.evalHand(hand("KS", "AD", "2C")));
    }

    @Test
    void ppWinnings_straightFlushPays40To1() {
        assertEquals(200, ThreeCardLogic.evalPPWinnings(hand("TH", "JH", "QH"), 5));
    }

    @Test
    void ppWinnings_threeOfAKindPays30To1() {
        assertEquals(150, ThreeCardLogic.evalPPWinnings(hand("7C", "7D", "7H"), 5));
    }

    @Test
    void ppWinnings_straightPays6To1() {
        assertEquals(30, ThreeCardLogic.evalPPWinnings(hand("QH", "KD", "AC"), 5));
    }

    @Test
    void ppWinnings_flushPays3To1() {
        assertEquals(15, ThreeCardLogic.evalPPWinnings(hand("2S", "5S", "9S"), 5));
    }

    @Test
    void ppWinnings_pairPays1To1() {
        assertEquals(10, ThreeCardLogic.evalPPWinnings(hand("9S", "9D", "2C"), 10));
    }

    @Test
    void ppWinnings_highCardPaysNothing() {
        assertEquals(0, ThreeCardLogic.evalPPWinnings(hand("2S", "5D", "9C"), 25));
    }

    @Test
    void compare_higherCategoryBeatsLower_playerWins() {
        // dealer pair vs player straight
        assertEquals(ThreeCardLogic.CMP_PLAYER_WINS,
                ThreeCardLogic.compareHands(hand("9S", "9D", "2C"), hand("4H", "5D", "6C")));
    }

    @Test
    void compare_higherCategoryBeatsLower_dealerWins() {
        // dealer flush vs player pair
        assertEquals(ThreeCardLogic.CMP_DEALER_WINS,
                ThreeCardLogic.compareHands(hand("2S", "5S", "9S"), hand("9H", "9D", "2C")));
    }

    @Test
    void compare_higherThreeOfAKindWins() {
        // same category, decided by the rank of the triple
        assertEquals(ThreeCardLogic.CMP_DEALER_WINS,
                ThreeCardLogic.compareHands(hand("9C", "9D", "9H"), hand("8C", "8D", "8H")));
    }

    @Test
    void compare_samePairDecidedByKicker() {
        // both pairs of 9s: player's 7 kicker beats dealer's 4
        assertEquals(ThreeCardLogic.CMP_PLAYER_WINS,
                ThreeCardLogic.compareHands(hand("9S", "9D", "4C"), hand("9H", "9C", "7D")));
    }

    @Test
    void compare_aceLowStraightIsLowestStraight() {
        // A-2-3 loses to 2-3-4, because the Ace counts low here
        assertEquals(ThreeCardLogic.CMP_PLAYER_WINS,
                ThreeCardLogic.compareHands(hand("AH", "2D", "3C"), hand("2C", "3D", "4H")));
    }

    @Test
    void compare_highCardDecidedBySecondCard() {
        // both Ace-high: dealer's King beats player's Queen
        assertEquals(ThreeCardLogic.CMP_DEALER_WINS,
                ThreeCardLogic.compareHands(hand("AS", "KD", "3C"), hand("AH", "QD", "JC")));
    }

    @Test
    void compare_sameRanksDifferentSuitsTie() {
        // suits never break ties
        assertEquals(ThreeCardLogic.CMP_TIE,
                ThreeCardLogic.compareHands(hand("KS", "7D", "2C"), hand("KH", "7C", "2D")));
    }
}

