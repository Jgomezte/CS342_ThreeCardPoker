package edu.uic.cs342.poker;

import edu.uic.cs342.poker.model.Card;
import edu.uic.cs342.poker.model.Deck;
import edu.uic.cs342.poker.model.Rank;
import edu.uic.cs342.poker.model.Suit;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {

    // deals all 52 cards, in the order they come off the deck
    private static List<Card> drain(Deck deck) {
        List<Card> dealt = new ArrayList<>();
        for (int i = 0; i < 52; i++) {
            dealt.add(deck.deal());
        }
        return dealt;
    }

    private static List<String> names(List<Card> cards) {
        return cards.stream().map(Card::toString).collect(Collectors.toList());
    }

    @Test
    void newDeckHas52UniqueCards() {
        Set<String> unique = new HashSet<>(names(drain(new Deck())));
        assertEquals(52, unique.size());
    }

    @Test
    void newDeckHas13OfEachSuitAndFourOfEachRank() {
        List<Card> cards = drain(new Deck());

        Map<Suit, Long> bySuit = cards.stream()
                .collect(Collectors.groupingBy(Card::getSuit, Collectors.counting()));
        assertEquals(4, bySuit.size());
        bySuit.values().forEach(count -> assertEquals(13L, count));

        Map<Rank, Long> byRank = cards.stream()
                .collect(Collectors.groupingBy(Card::getRank, Collectors.counting()));
        assertEquals(13, byRank.size());
        byRank.values().forEach(count -> assertEquals(4L, count));
    }

    @Test
    void shuffleKeepsTheSame52Cards() {
        Set<String> before = new HashSet<>(names(drain(new Deck())));

        Deck shuffled = new Deck();
        shuffled.shuffle();
        Set<String> after = new HashSet<>(names(drain(shuffled)));

        assertEquals(before, after);
    }

    @Test
    void shuffleChangesTheDealingOrder() {
        List<String> unshuffled = names(drain(new Deck()));

        Deck shuffled = new Deck();
        shuffled.shuffle();

        // the chance of a shuffle matching the original order is about 1 in 10^67
        assertNotEquals(unshuffled, names(drain(shuffled)));
    }

    @Test
    void dealingFromAnEmptyDeckThrows() {
        Deck deck = new Deck();
        drain(deck);
        assertThrows(IllegalStateException.class, deck::deal);
    }

    @Test
    void resetRestoresAFullDeckAfterPartialDealing() {
        Deck deck = new Deck();
        for (int i = 0; i < 10; i++) {
            deck.deal();
        }

        deck.reset();

        assertEquals(52, new HashSet<>(names(drain(deck))).size());
    }
}