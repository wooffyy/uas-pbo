package model.entity.dealer;

import core.Rules;
import model.card.Card;
import model.card.SpecialCard;
import model.card.Suit;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class EconomistBoss extends BossDealer {

    public EconomistBoss(Random rng) {
        super("ECONOMIST", rng);
    }

    @Override
    public int bid(SpecialCard biddingItem, int round) {
        return 1;
    }

    @Override
    public int modifyPlayerCardValue(int originalValue) {
        if (trickCount >= 5) {
            return Math.max(1, originalValue - 1);
        }
        return originalValue;
    }

    @Override
    public String getActiveSkillName() {
        return "Value Drain";
    }

    @Override
    public boolean isSkillActive() {
        return trickCount >= 5;
    }

    // Helper method to get effective value of player's card (considering EconomistBoss skill)
    private int getEffectivePlayerCardValue(Card playerCard) {
        return this.modifyPlayerCardValue(Rules.scoreCard(playerCard));
    }

    @Override
    public Card chooseCard(Card playerCard, List<Card> dealerHand) {
        if (dealerHand.isEmpty()) return null;

        // If dealer leads (playerCard is null), play the lowest card
        if (playerCard == null) {
            return dealerHand.stream()
                    .min(Comparator.comparingInt(Card::getValue))
                    .orElse(null);
        }

        Suit leadSuit = playerCard.getSuit();
        int effectivePlayerValue = getEffectivePlayerCardValue(playerCard);

        // 1. Filter cards that follow suit
        List<Card> followSuitCards = dealerHand.stream()
                .filter(card -> card.getSuit() == leadSuit)
                .collect(Collectors.toList());

        // 2. Try to find a winning card that follows suit, playing the lowest such card
        if (!followSuitCards.isEmpty()) {
            List<Card> winningFollowSuitCards = followSuitCards.stream()
                    .filter(card -> getEffectiveDealerCardValue(card) > effectivePlayerValue)
                    .collect(Collectors.toList());

            if (!winningFollowSuitCards.isEmpty()) {
                // Play the lowest winning card that follows suit (economical)
                return winningFollowSuitCards.stream()
                        .min(Comparator.comparingInt(this::getEffectiveDealerCardValue))
                        .orElseThrow(() -> new IllegalStateException("Should find a card if list is not empty"));
            }

            // If no winning cards, play the lowest card that follows suit
            return followSuitCards.stream()
                    .min(Comparator.comparingInt(this::getEffectiveDealerCardValue))
                    .orElseThrow(() -> new IllegalStateException("Should find a card if list is not empty"));
        }

        // 3. If no cards that follow suit, play the lowest overall card as a discard
        return dealerHand.stream()
                .min(Comparator.comparingInt(this::getEffectiveDealerCardValue))
                .orElseThrow(() -> new IllegalStateException("Hand cannot be empty at this point"));
    }
}
