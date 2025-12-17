package model.entity.dealer;

import core.Rules;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import model.card.Card;
import model.card.NormalCard;
import model.card.SpecialCard;
import model.card.Suit; // Import Rules class

public class TacticianBoss extends BossDealer {

    public TacticianBoss(Random rng) {
        super("TACTICIAN", rng);
    }

    @Override
    public int bid(SpecialCard biddingItem, int round) {
        return 5;
    }

    @Override
    public boolean forceHighestCard() {
        // Setelah 3 trick pertama → trick 4 & 5
        return trickCount >= 4 && trickCount <= 5;
    }

    @Override
    public String getActiveSkillName() {
        return "Forced Commitment";
    }

    @Override
    public boolean isSkillActive() {
        return forceHighestCard();
    }

    @Override
    protected int getEffectiveDealerCardValue(Card dealerCard) {
        // Create a temporary card to apply the dealer's own rankModifier for evaluation
        Card tempCard = new NormalCard(dealerCard.getSuit(), dealerCard.getRank());
        tempCard.modifyRank(this.getRankModifier()); // Apply the persistent rank modifier from the dealer
        return Rules.scoreCard(tempCard);
    }

    @Override
    public Card chooseCard(Card playerCard, List<Card> playerHand) {
        Suit leadSuit = playerCard.getSuit();
        int effectivePlayerValue = Rules.scoreCard(playerCard); // TacticianBoss does not modify player card value

        List<Card> hand = getHand();

        // 1. Filter cards that follow suit
        List<Card> followSuitCards = hand.stream()
                .filter(card -> card.getSuit() == leadSuit)
                .collect(Collectors.toList());

        // 2. Determine strategy based on forceHighestCard()
        boolean isForcedCommitmentActive = forceHighestCard();

        // Try to find a winning card that follows suit
        List<Card> winningFollowSuitCards = followSuitCards.stream()
                .filter(dealerCard -> getEffectiveDealerCardValue(dealerCard) > effectivePlayerValue)
                .collect(Collectors.toList());

        if (!winningFollowSuitCards.isEmpty()) {
            if (isForcedCommitmentActive) {
                // During Forced Commitment, play the lowest winning card to conserve higher cards if possible
                return winningFollowSuitCards.stream()
                        .min(Comparator.comparingInt(this::getEffectiveDealerCardValue))
                        .orElseThrow(() -> new IllegalStateException("Should find a card if list is not empty"));
            } else {
                // Outside Forced Commitment, still play the lowest winning card (general good strategy)
                 return winningFollowSuitCards.stream()
                        .min(Comparator.comparingInt(this::getEffectiveDealerCardValue))
                        .orElseThrow(() -> new IllegalStateException("Should find a card if list is not empty"));
            }
        }

        // If no winning cards, but there are cards to follow suit
        if (!followSuitCards.isEmpty()) {
            // Play the lowest card that follows suit
            return followSuitCards.stream()
                    .min(Comparator.comparingInt(this::getEffectiveDealerCardValue))
                    .orElseThrow(() -> new IllegalStateException("Should find a card if list is not empty"));
        }

        // If no cards that follow suit, discard the lowest overall card
        return hand.stream()
                .min(Comparator.comparingInt(this::getEffectiveDealerCardValue))
                .orElseThrow(() -> new IllegalStateException("Hand cannot be empty at this point"));
    }
}
