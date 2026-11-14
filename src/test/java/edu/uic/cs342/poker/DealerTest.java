package edu.uic.cs342.poker;

import edu.uic.cs342.poker.model.Card;
import edu.uic.cs342.poker.model.Dealer;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DealerTest {

    @Test
    void dealHandReturnsExactlyThreeCards() {
        assertEquals(3, new Dealer().dealHand().size());
    }

    @Test
    void oneRoundOfThreeHandsHasNineDistinctCards() {
        Dealer dealer = new Dealer();
        Set<String> seen = new HashSet<>();
        for (int hand = 0; hand < 3; hand++) {
            for (Card card : dealer.dealHand()) {
                seen.add(card.toString());
            }
        }
        assertEquals(9, seen.size());
    }

    @Test
    void shuffleDeckLetsTheDealerPlayManyRounds() {
        Dealer dealer = new Dealer();
        assertDoesNotThrow(() -> {
            for (int round = 0; round < 20; round++) {
                dealer.shuffleDeck();
                dealer.dealHand();
                dealer.dealHand();
                dealer.dealHand();
            }
        });
    }

    @Test
    void deckRunsOutWithoutReshufflingBetweenRounds() {
        Dealer dealer = new Dealer();
        for (int i = 0; i < 17; i++) {
            dealer.dealHand(); // 17 hands = 51 cards
        }
        // only 1 card is left, so the 18th hand can't be completed
        assertThrows(IllegalStateException.class, dealer::dealHand);
    }
}