package model.card;

public enum Rarity {
    COMMON, RARE, SUPER_RARE, LEGENDARY;

    public java.awt.Color getColor() {
        switch (this) {
            case COMMON:
                return java.awt.Color.WHITE;
            case RARE:
                return java.awt.Color.CYAN;
            case SUPER_RARE:
                return java.awt.Color.MAGENTA;
            case LEGENDARY:
                return java.awt.Color.ORANGE;
            default:
                return java.awt.Color.WHITE;
        }
    }
}
