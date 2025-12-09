package model.card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// Gunakan Java Collections + shuffle & draw
public class Deck {
    private List<Card> cards;

    public Deck() {
        cards = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new NormalCard(suit, rank));
            }
        }
    }

    public void shuffle(long seed) {
        Collections.shuffle(cards, new Random(seed));
    }

    // non-seeded shuffle, just in case
    // public void shuffle() {
    //     Collections.shuffle(cards);
    // }

    public void deal(List<Card> playerHand, List<Card> dealerHand) {
        for (int i = 0; i < 13; i++) {
            playerHand.add(draw());
            dealerHand.add(draw());
        }
    }

    public Card draw() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("Deck is empty");
        }
        return cards.remove(0);
    }

    public int size() {
        return cards.size();
    }
}
