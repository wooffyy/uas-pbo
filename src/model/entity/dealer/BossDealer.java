package model.entity.dealer;

import java.util.Random;
import model.card.Card;
import model.card.Suit;
import model.card.SpecialCard;
import model.card.NormalCard; // New import
import core.Rules; // New import

/**
 * Base class untuk semua Boss Dealer.
 * Berisi hook skill Phase 1 (trick-taking).
 *
 * KONTRAK:
 * - Skill hanya aktif di Phase 1
 * - Default = tidak ada efek
 */
public abstract class BossDealer extends Dealer {

    /** Trick ke berapa (dimulai dari 1) */
    protected int trickCount = 0;

    /** Suit terakhir yang dimainkan Boss (untuk Final Boss) */
    protected Suit lastBossSuit = null;

    public BossDealer(String name, Random rng) {
        super(name, rng);
    }

    // Helper method to get effective value of dealer's card (considering its rankModifier)
    protected int getEffectiveDealerCardValue(Card dealerCard) {
        // Create a temporary card to apply the dealer's own rankModifier for evaluation
        Card tempCard = new NormalCard(dealerCard.getSuit(), dealerCard.getRank());
        tempCard.modifyRank(this.getRankModifier()); // Apply the persistent rank modifier from the dealer
        return Rules.scoreCard(tempCard);
    }

    /* ======================================================
     * LIFECYCLE HOOK (DIPANGGIL Phase1Game)
     * ====================================================== */

    /**
     * Dipanggil SETIAP AWAL trick.
     * Aman untuk trigger skill berbasis timing.
     */
    public void onTrickStart(int trickCount) {
        this.trickCount = trickCount;
    }

    /**
     * Dipanggil saat Boss memainkan kartu.
     * Digunakan oleh Final Boss (Dominance).
     */
    public void onBossPlayCard(Card bossCard) {
        this.lastBossSuit = bossCard.getSuit();
    }

    /**
     * Reset state boss saat Phase 1 dimulai.
     * WAJIB dipanggil di awal Phase 1.
     */
    public void resetPhase1State() {
        this.trickCount = 0;
        this.lastBossSuit = null;
    }

    /* ======================================================
     * SKILL HOOK (OVERRIDE SESUAI STAGE)
     * ====================================================== */

    /**
     * STAGE 2 — Forced Commitment
     * Jika true, player dipaksa memainkan kartu tertinggi
     * (jika follow suit tersedia).
     */
    public boolean forceHighestCard() {
        return false;
    }

    /**
     * STAGE 3 — Value Drain
     * Modifikasi nilai kartu player (default: tidak berubah).
     */
    public int modifyPlayerCardValue(int originalValue) {
        return originalValue;
    }

    /**
     * STAGE 4 — Dominance
     * Override hasil trick jika perlu.
     *
     * @param playerCard kartu player
     * @param bossCard   kartu boss
     * @param defaultWinner hasil normal dari Rules
     * @param playerValue nilai kartu player (setelah modifier)
     * @param bossValue nilai kartu boss
     * @return true jika player MENANG, false jika KALAH
     */
    public boolean overrideTrickWinner(
            Card playerCard,
            Card bossCard,
            boolean defaultWinner,
            int playerValue,
            int bossValue
    ) {
        return defaultWinner;
    }

    /* ======================================================
     * METADATA (OPSIONAL UNTUK UI)
     * ====================================================== */

    /** Nama skill boss (untuk UI) */
    public String getActiveSkillName() {
        return "";
    }

    /** Apakah skill sedang aktif (untuk indikator UI) */
    public boolean isSkillActive() {
        return false;
    }
}
