package core;

import model.state.GameState;
import ui.UIWindow;

// Mengontrol flow seluruh game (State Machine)
public class GameManager {
    private final GameState gameState;
    private final UIWindow ui;
    private final Phase1Game phase1;
    private final Phase2Game phase2;

    public GameManager(GameState initialState, UIWindow ui) {
        this.gameState = initialState;
        this.ui = ui;
        this.phase1 = new Phase1Game(gameState);
        this.phase2 = new Phase2Game(gameState);
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

    public void onPhase1Finish(){
        if (!phase1.isWin()) {
            gameState.decreaseLife();
            if (gameState.isDead()){
                gameOver();
                return;
            }
        }

        debtPayment();
    }

    private void debtPayment();{
        gameState.applyDebtInterest();
        ui.showDebtPanel(ammount -> {
            gameState.payDebt(ammount);
            startPhase2();
        });
    }

    private void startPhase2(){
        ui.showPhase2Panel(phase2);
    }

    public void onPhase2Finish(){
        gameState.increaseRound();
    }

    private void gameOver(){
        ui.showGameOverPanel();
    }
}
