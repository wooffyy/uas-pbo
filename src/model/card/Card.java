package model.card;

// Abstract class untuk kartu biasa & spesial
abstract public class Card {
    private String name;
    private Suit suit;
    private Rank rank;

    public Card(String name, Suit suit, Rank rank){
        this.name = name;
        this.suit = suit;
        this.rank = rank;
    }

    public abstract int getValue();
    public abstract boolean isSpecial();

    public String getName() { return this.name; }
    public Suit getSuit() { return this.suit; }
    public Rank getRank() { return this.rank; }
}
