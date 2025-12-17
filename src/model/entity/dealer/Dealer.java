package model.entity.dealer;

import model.card.Card;
import model.card.SpecialCard;

import java.util.List;
import java.util.Random;

public abstract class Dealer {
    private int tricksWon;
    private String name;
    private Random rng;
    private int rankModifier = 0;

    public Dealer(String name, Random rng) {
        this.name = name;
        this.rng = rng;
        this.tricksWon = 0;
    }

    public abstract int bid(SpecialCard biddingItem, int round);

    // The dealer's hand is now passed in as an argument
    public abstract Card chooseCard(Card playerCard, List<Card> dealerHand);

    public void applyRankModifier(int rankModifier) {
        this.rankModifier = rankModifier;
    }

    public int getRankModifier() {
        return this.rankModifier;
    }

    public void addTricks() {
        this.tricksWon++;
    }

    public String getName() {
        return this.name;
    }

    public int getTricksWon() {
        return this.tricksWon;
    }

    public void resetTricks() {
        this.tricksWon = 0;
    }
}
