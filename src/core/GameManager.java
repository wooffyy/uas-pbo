package core;

import java.util.List;
import model.card.SpecialCard;
import model.state.GameState;
import ui.UIWindow;

// Mengontrol flow seluruh game (State Machine)
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

    public void startGame(){
        ui.showMainMenu();
    }

    public void startRun(){
        gameState.startNewRun();
        startPhase1();
    }

    private void startPhase1(){
        ui.showPhase1Panel(phase1);
    }

    public void onPhase1Finish() {
        if (phase1.isWin()) {
            int reward = phase1.getReward();
            gameState.addMoney(reward);
        } else {
            gameState.decreaseLife();
            if (gameState.isDead()) {
                gameOver();
                return;
            }
        }

        debtPayment();
    }


    private void debtPayment(){
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


    private void startPhase2(){
        phase2.loadShop();
        phase2.rollBiddingItem();
        ui.showPhase2Panel(phase2);
    }

    public void onPhase2Finish(){
        gameState.increaseRound();
        phase1.start();
        startPhase1();
    }

    private void gameOver(){
        ui.showGameOverPanel();
    }
}
