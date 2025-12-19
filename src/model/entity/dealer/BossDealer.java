package model.entity.dealer;

import core.Rules;
import java.util.Random;
import model.card.Card;
import model.card.NormalCard;
import model.card.SpecialCard;
import model.card.Suit;

/**
 * Base class for all Boss Dealers. Contains Phase 1 skill hooks.
 *
 * Contract:
 * - Skills are only active in Phase 1
 * - Default behavior = no effect
 */
public abstract class BossDealer extends Dealer {

    /** Current trick number (1-based) */
    protected int trickCount = 0;

    /** Last suit played by Boss (used for Final Boss logic) */
    protected Suit lastBossSuit = null;

    public BossDealer(String name, Random rng) {
        super(name, rng);
    }

    @Override
    public int bid(SpecialCard biddingItem, int round, int playerBid) {
        // Standard boss re-bid logic: bid 2-10 more than player, up to the boss's max
        int rebidAmount = 2 + rng.nextInt(9);
        int dealerBid = playerBid + rebidAmount;
        return Math.min(dealerBid, getMaxBid());
    }

    protected int getEffectiveDealerCardValue(Card dealerCard) {
        Card tempCard = new NormalCard(dealerCard.getSuit(), dealerCard.getRank());
        tempCard.modifyRank(this.getRankModifier());
        return Rules.scoreCard(tempCard);
    }

    /**
     * Called at the start of every trick.
     * Safe for timing-based skill triggers.
     */
    public void onTrickStart(int trickCount) {
        this.trickCount = trickCount;
    }

    /**
     * Called when Boss plays a card.
     */
    public void onBossPlayCard(Card bossCard) {
        this.lastBossSuit = bossCard.getSuit();
    }

    /**
     * Resets boss state at the start of Phase 1.
     */
    public void resetPhase1State() {
        this.trickCount = 0;
        this.lastBossSuit = null;
    }

    /**
     * STAGE 2 — Forced Commitment
     * If true, player is forced to play their highest available card.
     */
    public boolean forceHighestCard() {
        return false;
    }

    /**
     * STAGE 3 — Value Drain
     * Modifies player card value.
     */
    public int modifyPlayerCardValue(int originalValue) {
        return originalValue;
    }

    /**
     * STAGE 4 — Dominance
     * Overrides trick winner if necessary.
     */
    public boolean overrideTrickWinner(
            Card playerCard,
            Card bossCard,
            boolean defaultWinner,
            int playerValue,
            int bossValue) {
        return defaultWinner;
    }

    /** Boss skill name for UI */
    public String getActiveSkillName() {
        return "";
    }

    /** Whether the skill is currently active (for UI indicators) */
    public boolean isSkillActive() {
        return false;
    }
}
