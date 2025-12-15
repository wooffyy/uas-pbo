package core;

import model.state.GameState;
import model.card.Card;
import model.card.Deck;
import model.card.Suit;
import model.entity.Player;
import model.entity.dealer.Dealer;
import model.entity.dealer.BossDealer;

// Logic Trick-taking (German Whist-style)
public class Phase1Game {

    private final GameState gameState;
    private Player player;
    private Dealer dealer;
    private Deck deck;

    private int tricksWon;
    private int tricksLost;
    private int totalPoints;
    private int trickCount;

    public Phase1Game(GameState gameState) {
        this.gameState = gameState;
    }

    /* =====================================================
     * START PHASE 1
     * ===================================================== */
    public void start() {
        deck = new Deck();
        deck.shuffle(gameState.getSeed());

        this.tricksWon = 0;
        this.tricksLost = 0;
        this.totalPoints = 0;
        this.trickCount = 0;

        player = new Player();
        dealer = gameState.getCurrentDealer();

        // 🔴 RESET STATE BOSS (WAJIB)
        if (dealer instanceof BossDealer boss) {
            boss.resetPhase1State();
        }

        deck.deal(player.getHand(), dealer.getHand());
    }

    /* =====================================================
     * MAIN GAME LOOP — MAIN 1 TRICK
     * ===================================================== */
    public TrickResult playCard(Card playerCard) throws GameException {

        // 🔴 FASE 3 — FORCED COMMITMENT VALIDATION
        if (isForcedHighestCardViolation(playerCard)) {
            throw new GameException(
                    "Forced Commitment aktif: Anda harus memainkan kartu tertinggi yang tersedia!"
            );
        }

        trickCount++;

        // 🔴 NOTIFY BOSS TRICK START
        if (dealer instanceof BossDealer boss) {
            boss.onTrickStart(trickCount);
        }

        // Dealer memilih kartu
        Card dealerCard = dealer.chooseCard(playerCard, player.getHand());

        // 🔴 SIMPAN SUIT BOSS (FINAL BOSS)
        if (dealer instanceof BossDealer boss) {
            boss.onBossPlayCard(dealerCard);
        }

        // ===== HITUNG NILAI KARTU =====
        int playerValue = Rules.scoreCard(playerCard);
        int dealerValue = Rules.scoreCard(dealerCard);

        // 🔴 STAGE 3 — ECONOMIST (Value Drain)
        if (dealer instanceof BossDealer boss) {
            playerValue = boss.modifyPlayerCardValue(playerValue);
        }

        // ===== RULES DEFAULT =====
        boolean playerWin = Rules.isPlayerTrickWinner(playerCard, dealerCard);

        // 🔴 STAGE 4 — FINAL BOSS (Dominance)
        if (dealer instanceof BossDealer boss) {
            playerWin = boss.overrideTrickWinner(
                    playerCard,
                    dealerCard,
                    playerWin,
                    playerValue,
                    dealerValue
            );
        }

        // ===== UPDATE SCORE =====
        if (playerWin) {
            tricksWon++;
            totalPoints += (playerValue + dealerValue);
        } else {
            tricksLost++;
        }

        // (opsional tapi logis)
        player.getHand().remove(playerCard);
        dealer.removeCardFromHand(dealerCard);

        return new TrickResult(playerWin, playerCard, dealerCard);
    }

    /* =====================================================
     * FASE 3 — VALIDASI FORCED COMMITMENT
     * ===================================================== */
    private boolean isForcedHighestCardViolation(Card chosenCard) {

        // Tidak ada boss
        if (!(dealer instanceof BossDealer boss)) return false;

        // Skill tidak aktif
        if (!boss.forceHighestCard()) return false;

        // Cari kartu tertinggi dengan suit yang sama
        Card highestSameSuit = null;

        for (Card c : player.getHand()) {
            if (c.getSuit() != chosenCard.getSuit()) continue;

            if (highestSameSuit == null ||
                    Rules.scoreCard(c) > Rules.scoreCard(highestSameSuit)) {
                highestSameSuit = c;
            }
        }

        // Tidak punya kartu follow suit → bebas
        if (highestSameSuit == null) return false;

        // Melanggar jika bukan kartu tertinggi
        return Rules.scoreCard(chosenCard) < Rules.scoreCard(highestSameSuit);
    }

    /* =====================================================
     * RESULT & REWARD
     * ===================================================== */
    public boolean isWin() {
        return tricksWon > tricksLost;
    }

    public int getReward() {
        return totalPoints;
    }
}
