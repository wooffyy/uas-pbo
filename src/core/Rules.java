package core;

import model.card.Card;
import model.card.Suit;

// Static final config → Encapsulation numeric rule
public final class Rules {
    private Rules() {}

    /**
     * Determines the winner of a trick based on standard trick-taking rules (follow suit, highest rank).
     * Assumes no trump suit for now.
     *
     * @param leadCard The first card played in the trick (determines the lead suit).
     * @param playerCard The card played by the player.
     * @param dealerCard The card played by the dealer.
     * @return true if the player wins the trick, false if the dealer wins.
     */
    public static boolean getTrickWinner(Card leadCard, Card playerCard, Card dealerCard) {
        Suit leadSuit = leadCard.getSuit();

        boolean playerFollowsSuit = (playerCard != null && playerCard.getSuit() == leadSuit);
        boolean dealerFollowsSuit = (dealerCard != null && dealerCard.getSuit() == leadSuit);

        // Case 1: Both follow suit
        if (playerFollowsSuit && dealerFollowsSuit) {
            return playerCard.getRank().getValue() > dealerCard.getRank().getValue();
        }
        // Case 2: Only player follows suit
        else if (playerFollowsSuit) {
            return true; // Player wins because dealer did not follow suit
        }
        // Case 3: Only dealer follows suit
        else if (dealerFollowsSuit) {
            return false; // Dealer wins because player did not follow suit
        }
        // Case 4: Neither follows suit (both "slough" or "discard")
        // The player who led the trick wins.
        // We can determine the leader by checking if the player's card is the lead card.
        return playerCard == leadCard;
    }

    public static int getCardValueForMoney(Card card) {
        if (card == null) {
            return 0;
        }
        return card.getRank().getValueForMoney();
    }

    public static int scoreCard(Card card){
        return switch (card.getRank()){
            case ACE -> 14; // Ace is high
            case KING -> 13;
            case QUEEN -> 12;
            case JACK -> 11;
            default -> card.getRank().getValue(); // For 2-10, value is its number
        };
    }

    public static int calculateInterest (int debt, double rate){
        return (int) Math.ceil(debt * rate);
    }

}
