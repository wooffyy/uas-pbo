package model.entity.dealer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import model.card.Card;
import model.card.SpecialCard;

// Superclass musuh
abstract public class Dealer {
    private List<Card> hand;
    private int tricksWon;
    private String name;
    private Random rng;
    
    public Dealer(String name, Random rng){
        this.name = name;
        this.rng = rng;
        this.hand = new ArrayList<>();
        this.tricksWon = 0;
    }

    public abstract int bid(SpecialCard biddingItem, int round);
    public abstract Card chooseCard(Card playerCard, List<Card> playerHand);

    public void resetHand(List<Card> newHand){
        this.hand = newHand;
    }

    public void addTricks() {
        this.tricksWon++;
    }

    public List<Card> getHand() { return this.hand; }

    public String getName() { return this.name; }

    public int getTricksWon() { return this.tricksWon; }

    public void removeCardFromHand(Card card) {
        this.hand.remove(card);
    }

    public boolean hasCard(Card card) {
        return this.hand.contains(card);
    }

    public void resetTricks() {
        this.tricksWon = 0;
    }
}
