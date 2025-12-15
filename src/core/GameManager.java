package core;

import java.util.List;
import java.util.Random;

import model.card.SpecialCard;
import model.state.GameState;
import model.entity.dealer.Dealer;
import model.entity.dealer.BossDealer;
import model.entity.dealer.TacticianBoss;
import model.entity.dealer.EconomistBoss;
import model.entity.dealer.FinalBossDealer;

import ui.UIWindow;

/**
 * Mengontrol flow seluruh game (State Machine)
 * Versi refactor: boss & stage terhubung dengan benar
 */
public class GameManager {

    private final GameState gameState;
    private final Shop shop;
    private final UIWindow ui;
    private final Phase1Game phase1;
    private final Phase2Game phase2;

    public GameManager(GameState initialState, UIWindow ui, List<SpecialCard> itemPool) {
        this.gameState = initialState;
        this.shop = new Shop(itemPool, System.currentTimeMillis());
        this.ui = ui;
        this.phase1 = new Phase1Game(gameState);
        this.phase2 = new Phase2Game(gameState, shop);
    }

    /* =====================================================
     * GAME FLOW
     * ===================================================== */

    public void startGame() {
        ui.showMainMenu();
    }

    /**
     * Mulai 1 run baru (Stage 1)
     */
    public void startRun() {
        gameState.startNewRun();

        // Set boss sesuai stage awal
        setDealerForCurrentStage();

        // WAJIB: start Phase 1 logic
        phase1.start();

        startPhase1();
    }

    private void startPhase1() {
        ui.showPhase1Panel(phase1);
    }

    /**
     * Dipanggil UI setelah Phase 1 selesai
     */
    public void onPhase1Finish() {
        if (phase1.isWin()) {
            gameState.addMoney(phase1.getReward());
        } else {
            gameState.decreaseLife();
            if (gameState.isDead()) {
                gameOver();
                return;
            }
        }

        proceedDebtPayment();
    }

    /* =====================================================
     * PHASE 2
     * ===================================================== */

    private void proceedDebtPayment() {
        gameState.applyDebtInterest();

        ui.showDebtPanel(amount -> {
            gameState.payDebt(amount);

            if (gameState.isGameOver()) {
                gameOver();
            } else {
                startPhase2();
            }
        });
    }

    private void startPhase2() {
        phase2.loadShop();
        phase2.rollBiddingItem();
        ui.showPhase2Panel(phase2);
    }

    /**
     * Dipanggil UI setelah Phase 2 selesai
     */
    public void onPhase2Finish() {
        gameState.increaseRound();

        // Ganti boss sesuai stage baru
        setDealerForCurrentStage();

        phase1.start();
        startPhase1();
    }

    /* =====================================================
     * STAGE → BOSS FACTORY (INTI REFACTOR)
     * ===================================================== */

    private void setDealerForCurrentStage() {
        int stage = gameState.getRound();
        Dealer dealer = createDealerForStage(stage);
        gameState.setCurrentDealer(dealer);
    }

    private Dealer createDealerForStage(int stage) {
        long seed = gameState.getSeed();
        Random rng = new Random(seed + stage); // beda tiap stage, tapi deterministik

        return switch (stage) {
            case 1 -> new Dealer("INTRO BOSS", rng) {
                @Override
                public int bid(SpecialCard biddingItem, int round) {
                    return 0; // tutorial / no bidding pressure
                }

                @Override
                public model.card.Card chooseCard(
                        model.card.Card playerCard,
                        List<model.card.Card> playerHand
                ) {
                    return getHand().get(0); // dummy logic
                }
            };

            case 2 -> new TacticianBoss(rng);
            case 3 -> new EconomistBoss(rng);
            case 4 -> new FinalBossDealer(rng);

            default -> new FinalBossDealer(rng);
        };
    }

    /* =====================================================
     * GAME OVER
     * ===================================================== */

    private void gameOver() {
        ui.showGameOverPanel();
    }
}
