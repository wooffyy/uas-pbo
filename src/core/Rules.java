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
        // In this simplified rule, if neither follows suit, the lead card's player wins.
        // However, since we are comparing playerCard and dealerCard, and neither followed suit,
        // the winner is determined by who played the lead card.
        // For simplicity, if neither follows suit, the one who played the highest card of any suit wins.
        // But a more common rule is that the lead player wins if no one follows suit and no trump.
        // Let's assume for now that if neither follows suit, the one who played the highest card overall wins.
        // This needs clarification if a specific rule is desired.
        // For now, let's say the lead player (who played leadCard) wins if no one follows suit.
        // This implies the leadCard is either playerCard or dealerCard.
        // If playerCard was leadCard, player wins. If dealerCard was leadCard, dealer wins.
        // Since leadCard is always the first card played, we need to know who played it.
        // Let's simplify: if neither follows suit, the one who played the highest card overall wins.
        // This is a common simplified rule for non-trump games when no one follows suit.
        if (playerCard != null && dealerCard != null) {
            return playerCard.getRank().getValue() > dealerCard.getRank().getValue();
        } else if (playerCard != null) { // Only player played (dealer passed or error)
            return true;
        } else if (dealerCard != null) { // Only dealer played (player passed or error)
            return false;
        }
        return false; // Should not happen in a normal trick
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
