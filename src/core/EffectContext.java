package core;

import model.card.Card;
import model.state.GameState;

import java.util.List;
import java.util.Random;

public class EffectContext {
    private final GameState gameState;
    private Card playerCard;
    private Card dealerCard;
    private Boolean playerWin;
    private Integer roundInStage;
    private Integer stage;
    private Integer stagePoints;
    private Integer winStreak;
    private final Random random;
    private List<Card> capturedCards;

    // Constructor for stage-level effects
    public EffectContext(GameState gameState, int stage, int stagePoints, List<Card> capturedCards) {
        this.gameState = gameState;
        this.stage = stage;
        this.stagePoints = stagePoints;
        this.random = new Random(gameState.getSeed());
        this.capturedCards = capturedCards;
    }

    // Constructor for round-level effects
    public EffectContext(GameState gameState, Card playerCard, Card dealerCard, boolean playerWin, int roundInStage, int stage, int stagePoints, int winStreak) {
        this.gameState = gameState;
        this.playerCard = playerCard;
        this.dealerCard = dealerCard;
        this.playerWin = playerWin;
        this.roundInStage = roundInStage;
        this.stage = stage;
        this.stagePoints = stagePoints;
        this.winStreak = winStreak;
        this.random = new Random(gameState.getSeed());
    }

    public GameState getGameState() {
        return gameState;
    }

    public Card getPlayerCard() {
        return playerCard;
    }

    public Card getDealerCard() {
        return dealerCard;
    }

    public boolean isPlayerWin() {
        return playerWin != null && playerWin;
    }

    public int getRoundInStage() {
        return roundInStage != null ? roundInStage : 0;
    }

    public int getStage() {
        return stage != null ? stage : 0;
    }

    public int getStagePoints() {
        return stagePoints != null ? stagePoints : 0;
    }

    public int getWinStreak() {
        return winStreak != null ? winStreak : 0;
    }

    public Random getRandom() {
        return random;
    }

    public List<Card> getCapturedCards() {
        return capturedCards;
    }
}
