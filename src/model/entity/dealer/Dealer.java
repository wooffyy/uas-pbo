package model.entity.dealer;

import java.util.List;
import java.util.Random;
import model.card.Card;
import model.card.SpecialCard;

public abstract class Dealer {
    private int tricksWon;
    private String name;
    protected Random rng;
    private int rankModifier = 0;

    public Dealer(String name, Random rng) {
        this.name = name;
        this.rng = rng;
        this.tricksWon = 0;
    }

    public abstract int bid(SpecialCard biddingItem, int round, int playerBid);

    public abstract int getMaxBid();

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

    // --- SKILL FLAGS ---
    public boolean isTactician() {
        return false;
    }

    public boolean isEconomist() {
        return false;
    }

    public boolean isFinalBoss() {
        return false;
    }

    public int modifyPlayerCardValue(int originalValue) {
        return originalValue;
    }
}
