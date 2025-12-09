package model.card;

public class NormalCard extends Card {
    public NormalCard(Suit suit, Rank rank) {
        super(suit.name() + "of" + rank.name(), suit, rank);
    }

    @Override
    public int getValue() {
        return getRank().getValue();
    }

    @Override
    public boolean isSpecial() {
        return false;
    }
}
