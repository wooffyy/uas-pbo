package core;

import model.card.Card;
import model.card.Suit;
import model.state.GameState;

public class TrickContext {

    // ===== KARTU =====
    private Card dealerCard;
    private Card playerCard;

    // ===== STATE MENANG/KALAH =====
    private boolean playerWin;
    private boolean retrigger;          // main kartu ekstra
    private int retriggerCount = 0;     // untuk Infinite Tricks

    // ===== MODIFIER RULE =====
    private boolean ignoreFollowSuit;
    private boolean forceFollowSuit;
    private boolean forceWin;
    private boolean dealerRankDebuffed;

    // ===== SCORING =====
    private int trickPoints;            // base point trick ini
    private double pointMultiplier;     // Ten Taker, Parasite, dll

    // ===== META =====
    private final GameState gameState;
    private final int roundIndex;        // round ke-n di stage ini
    private final int winStreak;         // consecutive wins

    // ===== CONSTRUCTOR =====
    public TrickContext(
            GameState gameState,
            Card dealerCard,
            Card playerCard,
            int roundIndex,
            int winStreak
    ) {
        this.gameState = gameState;
        this.dealerCard = dealerCard;
        this.playerCard = playerCard;
        this.roundIndex = roundIndex;
        this.winStreak = winStreak;

        this.trickPoints = 0;
        this.pointMultiplier = 1.0;
    }

    
    // GETTER DASAR
    

    public Card getDealerCard() {
        return dealerCard;
    }

    public Card getPlayerCard() {
        return playerCard;
    }

    public GameState getGameState() {
        return gameState;
    }

    public int getRoundIndex() {
        return roundIndex;
    }

    public int getWinStreak() {
        return winStreak;
    }

    
    // RULE MODIFIER
    

    public void ignoreFollowSuit() {
        this.ignoreFollowSuit = true;
    }

    public boolean isIgnoreFollowSuit() {
        return ignoreFollowSuit;
    }

    public void forceFollowSuit() {
        this.forceFollowSuit = true;
    }

    public boolean isForceFollowSuit() {
        return forceFollowSuit;
    }

    public void forceWin() {
        this.forceWin = true;
    }

    public boolean isForceWin() {
        return forceWin;
    }

    public void debuffDealerRank(int amount) {
        dealerCard.modifyRank(-amount);
        dealerRankDebuffed = true;
    }

    
    // KARTU MODIFIER
    

    public void increasePlayerRank(int amount) {
        playerCard.modifyRank(amount);
    }

    public void swapPlayerSuitToDealer() {
        playerCard.setSuit(dealerCard.getSuit());
    }

    
    // RESULT CONTROL
    

    public void setPlayerWin(boolean win) {
        this.playerWin = win;
    }

    public boolean isPlayerWin() {
        return playerWin || forceWin;
    }

    public void triggerRetrigger() {
        retrigger = true;
        retriggerCount++;
    }

    public boolean isRetrigger() {
        return retrigger;
    }

    public int getRetriggerCount() {
        return retriggerCount;
    }

    
    // SCORING
    

    public void addTrickPoints(int points) {
        this.trickPoints += points;
    }

    public void multiplyPoints(double multiplier) {
        this.pointMultiplier *= multiplier;
    }

    public int getFinalTrickPoints() {
        return (int) Math.round(trickPoints * pointMultiplier);
    }

    
    // HELPERS UNTUK EFFECT
    

    public boolean isPlayerLowerRank() {
        return playerCard.getValue() < dealerCard.getValue();
    }

    public boolean isPlayerHigherRank() {
        return playerCard.getValue() > dealerCard.getValue();
    }

    public boolean isSameSuit() {
        return playerCard.getSuit() == dealerCard.getSuit();
    }

    public boolean isPlayerOffSuit() {
        return playerCard.getSuit() != dealerCard.getSuit();
    }

    public boolean dealerSuitIs(Suit suit) {
        return dealerCard.getSuit() == suit;
    }
}
