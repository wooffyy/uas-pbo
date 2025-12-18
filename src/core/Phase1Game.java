package core;

import model.card.Card;
import model.state.GameState;

import java.util.ArrayList;
import java.util.List;

public class Phase1Game {

    private final GameState gameState;
    private int tricksWon;
    private int tricksLost;
    private int totalPoints;
    private final List<Card> capturedCards = new ArrayList<>();
    private final List<Card> playerTrickPile = new ArrayList<>();
    private final List<Card> dealerTrickPile = new ArrayList<>();
    private final int targetTricks = 7; // New: Target tricks to win Phase 1

    public Phase1Game(GameState gameState) {
        this.gameState = gameState;
    }

    public void start() {
        reset();
        // The rest of the start logic (dealing cards) is now in GameManager
    }

    public void reset() {
        this.tricksWon = 0;
        this.tricksLost = 0;
        this.totalPoints = 0;
        this.capturedCards.clear();
        this.playerTrickPile.clear();
        this.dealerTrickPile.clear();
    }

    public void playerWinsTrick(Card playerCard, Card dealerCard) {
        tricksWon++;
        if (playerCard != null) playerTrickPile.add(playerCard);
        if (dealerCard != null) playerTrickPile.add(dealerCard);
        // You can add point calculation logic here if needed
    }

    public void dealerWinsTrick(Card playerCard, Card dealerCard) {
        tricksLost++;
        if (playerCard != null) dealerTrickPile.add(playerCard);
        if (dealerCard != null) dealerTrickPile.add(dealerCard);
    }

    public boolean isWin() { // Removed @Override annotation
        // Win condition: win at least targetTricks
        return tricksWon >= targetTricks;
    }

    public boolean isLoss() {
        return tricksLost >= targetTricks;
    }

    public int getReward() {
        // Example reward: 100 points for each trick won
        totalPoints = tricksWon * 100;
        gameState.setScorePhase1(totalPoints);
        return totalPoints;
    }

    public List<Card> getCapturedCards() {
        // This could be used for special effects, for now, it's the player's pile
        return playerTrickPile;
    }

    public int getTricksWon() {
        return tricksWon;
    }

    public int getTricksLost() {
        return tricksLost;
    }

    public int getTargetTricks() {
        return targetTricks;
    }
}
