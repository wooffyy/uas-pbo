package core;

import model.card.Card;
import model.card.Suit;

// Static final config → Encapsulation numeric rule
public final class Rules {
    private Rules() {
    }

    /**
     * Determines the winner of a trick based on standard trick-taking rules (follow
     * suit, highest rank).
     * Assumes no trump suit for now.
     *
     * @param leadCard   The first card played in the trick (determines the lead
     *                   suit).
     * @param playerCard The card played by the player.
     * @param dealerCard The card played by the dealer.
     * @return true if the player wins the trick, false if the dealer wins.
     */
    public static boolean getTrickWinner(Card leadCard, Card playerCard, Card dealerCard) {
        return getTrickWinner(leadCard, playerCard, dealerCard, null, 0);
    }

    public static boolean getTrickWinner(Card leadCard, Card playerCard, Card dealerCard,
            model.entity.dealer.Dealer dealer, int trickNumber) {
        Suit leadSuit = leadCard.getSuit();

        int playerValue = playerCard.getRank().getValue();
        int dealerValue = dealerCard.getRank().getValue();

        // --- BOSS SKILL: ECONOMIST (Value Drain) ---
        // Trigger: Trick 5 onwards (trickNumber >= 5)
        if (dealer != null && dealer.isEconomist() && trickNumber >= 5) {
            playerValue = Math.max(1, playerValue - 1);
            // Notification handled in GameManager
        }

        boolean playerFollowsSuit = (playerCard != null && playerCard.getSuit() == leadSuit);
        boolean dealerFollowsSuit = (dealerCard != null && dealerCard.getSuit() == leadSuit);

        // --- BOSS SKILL: FINAL BOSS (Dominance) ---
        // Trigger: Always active
        // Logic: If Boss leads/plays, that suit becomes "Trump" effectively for
        // ties/close calls?
        // Requirement: "If Boss plays Suit X, Boss card always higher UNLESS Player
        // value >= Boss value + 3"
        if (dealer != null && dealer.isFinalBoss()) {
            // If both follow suit OR both don't (same category)
            if (playerFollowsSuit == dealerFollowsSuit) {
                // Player needs to beat Dealer by at least 3
                return playerValue >= dealerValue + 3;
            }
        }

        // Standard Rules
        if (playerFollowsSuit && dealerFollowsSuit) {
            return playerValue > dealerValue;
        } else if (playerFollowsSuit) {
            return true;
        } else if (dealerFollowsSuit) {
            return false;
        }

        // Both discard: Leader wins
        return playerCard == leadCard;
    }

    public static int getCardValueForMoney(Card card) {
        if (card == null) {
            return 0;
        }
        return card.getRank().getValueForMoney();
    }

    public static int scoreCard(Card card) {
        return switch (card.getRank()) {
            case ACE -> 14; // Ace is high
            case KING -> 13;
            case QUEEN -> 12;
            case JACK -> 11;
            default -> card.getRank().getValue(); // For 2-10, value is its number
        };
    }

    public static int calculateInterest(int debt, double rate) {
        return (int) Math.ceil(debt * rate);
    }

}
