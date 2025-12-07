package core;

import model.card.Card;
import model.card.Suit;

// Static final config → Encapsulation numeric rule
public final class Rules {
    private Rules() {}

    public static boolean isPlayerTrickWinner(Card playerCard, Card dealerCard) {
        Suit leadSuit = dealerCard.getSuit();

        boolean playerFollowSuit = playerCard.getSuit() == leadSuit;
        boolean higherValue = playerCard.getValue() > dealerCard.getValue();

        if (!playerFollowSuit) {
            return false;  // auto lose
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
