package model.card;

// Enum inner-scope / Value card
// Enum inner-scope / Value card
public enum Rank {
    TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10), JACK(10), QUEEN(10), KING(10), ACE(11);

    private final int value;

    Rank(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public int getPower() {
        return switch (this) {
            case ACE -> 14;
            case KING -> 13;
            case QUEEN -> 12;
            case JACK -> 11;
            default -> this.value;
        };
    }

    public int getValueForMoney() {
        return switch (this) {
            case ACE -> 11;
            case KING, QUEEN, JACK -> 10;
            default -> this.value;
        };
    }

    public boolean isFaceCard() {
        return this == JACK || this == QUEEN || this == KING;
    }
}
