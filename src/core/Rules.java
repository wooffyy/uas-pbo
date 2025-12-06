package core;

import model.card.Card;
import model.card.Suit;

// Static final config → Encapsulation numeric rule
public final class Rules {
    private Rules() {}

    public static boolean isPlayerTrickWinner(Card player, Card dealer) {
        Suit leadSuit = dealer.getSuit();

        boolean playerFollowSuit = player.getSuit() == leadSuit;
        boolean higherValue = player.getValue() > dealer.getValue();

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
