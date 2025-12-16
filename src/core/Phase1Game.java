package core;

import model.state.GameState;
import model.card.Card;
import model.card.Deck;
import model.card.EffectTrigger;
import model.entity.Player;
import model.entity.dealer.Dealer;
import model.entity.dealer.BossDealer;

import java.util.ArrayList;
import java.util.List;

public class Phase1Game {

    private final GameState gameState;
    private Player player;
    private Dealer dealer;
    private Deck deck;

    private int tricksWon;
    private int tricksLost;
    private int winStreak;
    private int totalPoints;
    private int trickCount;
    private final List<Card> capturedCards = new ArrayList<>();

    public Phase1Game(GameState gameState) {
        this.gameState = gameState;
    }

    public void start() {
        deck = new Deck();
        deck.shuffle(gameState.getSeed());

        this.tricksWon = 0;
        this.tricksLost = 0;
        this.winStreak = 0;
        this.totalPoints = 0;
        this.trickCount = 0;
        this.capturedCards.clear();

        player = new Player();
        dealer = gameState.getCurrentDealer();

        if (dealer instanceof BossDealer boss) {
            boss.resetPhase1State();
        }

        deck.deal(player.getHand(), dealer.getHand());

        // Apply BEFORE_STAGE effects
        EffectContext stageCtx = new EffectContext(gameState, gameState.getRound(), totalPoints);
        gameState.getInventory().applyEffects(stageCtx, EffectTrigger.BEFORE_STAGE);
    }

    public TrickResult playCard(Card playerCard) throws GameException {
        if (isForcedHighestCardViolation(playerCard)) {
            throw new GameException("Forced Commitment aktif: Anda harus memainkan kartu tertinggi yang tersedia!");
        }

        trickCount++;

        if (dealer instanceof BossDealer boss) {
            boss.onTrickStart(trickCount);
        }

        // BEFORE_ROUND effects
        EffectContext beforeRoundCtx = new EffectContext(gameState, playerCard, null, false, trickCount, gameState.getRound(), totalPoints, winStreak);
        TrickModifier beforeRoundModifier = gameState.getInventory().applyEffects(beforeRoundCtx, EffectTrigger.BEFORE_ROUND);
        playerCard.modifyRank(beforeRoundModifier.getPlayerRankBoost());

        Card dealerCard = dealer.chooseCard(playerCard, player.getHand());

        if (dealer instanceof BossDealer boss) {
            boss.onBossPlayCard(dealerCard);
        }

        // ON_ROUND effects
        EffectContext onRoundCtx = new EffectContext(gameState, playerCard, dealerCard, false, trickCount, gameState.getRound(), totalPoints, winStreak);
        TrickModifier onRoundModifier = gameState.getInventory().applyEffects(onRoundCtx, EffectTrigger.ON_ROUND);
        playerCard.modifyRank(onRoundModifier.getPlayerRankBoost());

        int playerValue = Rules.scoreCard(playerCard);
        int dealerValue = Rules.scoreCard(dealerCard);

        if (dealer instanceof BossDealer boss) {
            playerValue = boss.modifyPlayerCardValue(playerValue);
        }

        boolean playerWin = Rules.isPlayerTrickWinner(playerCard, dealerCard, onRoundModifier.isIgnoreSuitRule());
        if (onRoundModifier.isForceWin()) {
            playerWin = true;
        }

        if (dealer instanceof BossDealer boss) {
            playerWin = boss.overrideTrickWinner(playerCard, dealerCard, playerWin, playerValue, dealerValue);
        }
        
        boolean retrigger = onRoundModifier.isRetrigger() && playerWin;
        if (retrigger) {
             System.out.println("Retrigger activated!");
        }

        if (playerWin) {
            tricksWon++;
            winStreak++;
            capturedCards.add(playerCard);
            capturedCards.add(dealerCard);
            int trickPoints = playerValue + dealerValue;

            // AFTER_ROUND effects
            EffectContext afterRoundCtx = new EffectContext(gameState, playerCard, dealerCard, true, trickCount, gameState.getRound(), totalPoints, winStreak);
            TrickModifier afterRoundModifier = gameState.getInventory().applyEffects(afterRoundCtx, EffectTrigger.AFTER_ROUND);
            trickPoints *= afterRoundModifier.getPointMultiplier();
            trickPoints += afterRoundModifier.getFlatPointsBonus();
            totalPoints += trickPoints;
        } else {
            tricksLost++;
            winStreak = 0;
        }

        player.getHand().remove(playerCard);
        dealer.removeCardFromHand(dealerCard);

        gameState.getInventory().tickCooldowns();

        return new TrickResult(playerWin, playerCard, dealerCard, retrigger);
    }

    private boolean isForcedHighestCardViolation(Card chosenCard) {
        if (!(dealer instanceof BossDealer boss)) return false;
        if (!boss.forceHighestCard()) return false;

        Card highestSameSuit = null;
        for (Card c : player.getHand()) {
            if (c.getSuit() != chosenCard.getSuit()) continue;
            if (highestSameSuit == null || Rules.scoreCard(c) > Rules.scoreCard(highestSameSuit)) {
                highestSameSuit = c;
            }
        }

        if (highestSameSuit == null) return false;
        return Rules.scoreCard(chosenCard) < Rules.scoreCard(highestSameSuit);
    }

    public boolean isWin() {
        return tricksWon > tricksLost;
    }

    public int getReward() {
        // The final points for the stage are now calculated.
        // We set it in the game state so that AFTER_STAGE effects can modify it.
        gameState.setScorePhase1(totalPoints);
        return totalPoints;
    }

    public List<Card> getCapturedCards() {
        return capturedCards;
    }
}
