package model.entity.dealer;

import core.Rules;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import model.card.Card;
import model.card.NormalCard;
import model.card.Suit;

public class FinalBossDealer extends BossDealer {

    public FinalBossDealer(Random rng) {
        super("Future Alit", rng);
    }

    @Override
    public boolean isFinalBoss() {
        return true;
    }

    @Override
    public int getMaxBid() {
        return 200;
    }

    @Override
    public boolean overrideTrickWinner(
            Card playerCard,
            Card bossCard,
            boolean defaultWinner,
            int playerValue,
            int bossValue) {
        // Dominance aktif sejak awal
        // If boss plays the lastBossSuit, it's always considered higher
        // UNLESS player's card is much stronger (value difference >= 3)
        if (bossCard.getSuit() == lastBossSuit) {
            int diff = playerValue - bossValue;
            return diff >= 3; // Player wins if their card is significantly stronger
        }
        return defaultWinner;
    }

    @Override
    public String getActiveSkillName() {
        return "Dominance";
    }

    @Override
    public boolean isSkillActive() {
        return true; // Dominance is always active
    }

    @Override
    protected int getEffectiveDealerCardValue(Card dealerCard) {
        // Create a temporary card to apply the dealer's own rankModifier for evaluation
        Card tempCard = new NormalCard(dealerCard.getSuit(), dealerCard.getRank());
        tempCard.modifyRank(this.getRankModifier()); // Apply the persistent rank modifier from the dealer
        return Rules.scoreCard(tempCard);
    }

    @Override
    public Card chooseCard(Card playerCard, List<Card> dealerHand) {
        if (dealerHand.isEmpty())
            return null;

        // If dealer leads (playerCard is null), play the lowest card
        if (playerCard == null) {
            return dealerHand.stream()
                    .min(Comparator.comparingInt(Card::getValue))
                    .orElse(null);
        }

        Suit leadSuit = playerCard.getSuit();
        int effectivePlayerValue = Rules.scoreCard(playerCard); // FinalBossDealer does not modify player card value

        // 1. Filter cards that follow suit
        List<Card> followSuitCards = dealerHand.stream()
                .filter(card -> card.getSuit() == leadSuit)
                .collect(Collectors.toList());

        // 2. Try to play a card of the lastBossSuit first (Dominance effect)
        if (lastBossSuit != null) {
            List<Card> dominanceCards = followSuitCards.stream()
                    .filter(card -> card.getSuit() == lastBossSuit)
                    .collect(Collectors.toList());

            if (!dominanceCards.isEmpty()) {
                // Find a card that wins under Dominance rule
                List<Card> winningDominanceCards = dominanceCards.stream()
                        .filter(card -> {
                            int effectiveBossValue = getEffectiveDealerCardValue(card);
                            // Player wins if their card is significantly stronger (diff >= 3)
                            // So, boss wins if (effectivePlayerValue - effectiveBossValue) < 3
                            return (effectivePlayerValue - effectiveBossValue) < 3;
                        })
                        .collect(Collectors.toList());

                if (!winningDominanceCards.isEmpty()) {
                    // Play the lowest winning card under Dominance
                    return winningDominanceCards.stream()
                            .min(Comparator.comparingInt(this::getEffectiveDealerCardValue))
                            .orElseThrow(() -> new IllegalStateException("Should find a card if list is not empty"));
                }
            }
        }

        // 3. If no winning Dominance card, or Dominance not applicable, fall back to
        // standard winning logic
        // Try to find a winning card that follows suit
        if (!followSuitCards.isEmpty()) {
            List<Card> winningFollowSuitCards = followSuitCards.stream()
                    .filter(card -> getEffectiveDealerCardValue(card) > effectivePlayerValue)
                    .collect(Collectors.toList());

            if (!winningFollowSuitCards.isEmpty()) {
                // Play the lowest winning card that follows suit
                return winningFollowSuitCards.stream()
                        .min(Comparator.comparingInt(this::getEffectiveDealerCardValue))
                        .orElseThrow(() -> new IllegalStateException("Should find a card if list is not empty"));
            }

            // If no winning cards, play the lowest card that follows suit
            return followSuitCards.stream()
                    .min(Comparator.comparingInt(this::getEffectiveDealerCardValue))
                    .orElseThrow(() -> new IllegalStateException("Should find a card if list is not empty"));
        }

        // 4. If no cards that follow suit, discard the lowest overall card
        return dealerHand.stream()
                .min(Comparator.comparingInt(this::getEffectiveDealerCardValue))
                .orElseThrow(() -> new IllegalStateException("Hand cannot be empty at this point"));
    }
}
