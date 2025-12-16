package core;

import model.card.Card;
import model.card.Suit;

// Static final config → Encapsulation numeric rule
public final class Rules {
    private Rules() {}

    public static boolean isPlayerTrickWinner(Card playerCard, Card dealerCard) {
        // By default, the suit rule is not ignored.
        return isPlayerTrickWinner(playerCard, dealerCard, false);
    }

    public static boolean isPlayerTrickWinner(Card playerCard, Card dealerCard, boolean ignoreSuitRule) {
        Suit leadSuit = dealerCard.getSuit();

        boolean playerFollowSuit = playerCard.getSuit() == leadSuit;
        boolean higherValue = playerCard.getValue() > dealerCard.getValue();

        // The player automatically loses if they don't follow suit, UNLESS the rule is ignored
        if (!playerFollowSuit && !ignoreSuitRule) {
            return false;
        }

        return higherValue;
    }

    public static int scoreCard(Card card){
        return switch (card.getRank()){
            case ACE -> 1;
            case JACK, QUEEN, KING -> 10;
            default -> card.getRank().getValue();
        };
    }

    public static int calculateInterest (int debt, double rate){
        return (int) Math.ceil(debt * rate);
    }

}
