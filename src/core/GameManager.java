package core;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import javax.swing.Timer;
import model.card.*;
import model.entity.dealer.Dealer;
import model.entity.dealer.EconomistBoss;
import model.entity.dealer.FinalBossDealer;
import model.entity.dealer.TacticianBoss;
import model.state.GameState;
import ui.UIWindow;

public class GameManager {

    private final GameState gameState;
    private final Shop shop;
    private final UIWindow ui;
    private final Phase1Game phase1;
    private final Phase2Game phase2;

    private static GameManager instance;

    public GameManager(GameState initialState, UIWindow ui, List<SpecialCard> itemPool) {
        instance = this;
        this.gameState = initialState;
        this.shop = new Shop(itemPool, System.currentTimeMillis());
        this.ui = ui;
        this.phase1 = new Phase1Game(gameState);
        this.phase2 = new Phase2Game(gameState, shop);
    }

    public void playCard(Card card) {
        // Player can only play if:
        // 1. No player card is currently on the table (playerPlayedCard is null)
        // 2. It's the player's turn to lead (dealerLeadsTrick is false) OR the dealer
        // has already played a card
        if (gameState.getPlayerPlayedCard() != null
                || (gameState.isDealerLeadsTrick() && gameState.getDealerPlayedCard() == null)) {
            return;
        }

        // --- BOSS SKILL: TACTICIAN (Forced Commitment) ---
        // Trigger: Tricks 4 & 5 (Indices 3 & 4)
        Dealer dealer = gameState.getCurrentDealer();
        int currentTrickIndex = phase1.getTricksWon() + phase1.getTricksLost() + 1; // 1-based index

        if (dealer != null && dealer.isTactician()) {
            // Logic: "After 3 tricks" -> Tricks 4 and 5.
            if (currentTrickIndex == 4 || currentTrickIndex == 5) {
                List<Card> hand = gameState.getPlayerHand();
                Card maxCard = null;

                // If following suit, must play highest of that suit
                if (gameState.isDealerLeadsTrick()) {
                    Suit leadSuit = gameState.getCurrentLeadCard().getSuit();
                    List<Card> follow = hand.stream().filter(c -> c.getSuit() == leadSuit).collect(Collectors.toList());
                    if (!follow.isEmpty()) {
                        maxCard = follow.stream().max(Comparator.comparingInt(Rules::scoreCard))
                                .orElse(null);
                    }
                }

                // If not following suit (or leading), must play highest overall
                if (maxCard == null) {
                    maxCard = hand.stream().max(Comparator.comparingInt(Rules::scoreCard)).orElse(null);
                }

                // Logic: You must play a card that is EQUAL to the max value (allow ties)
                // If the played card is WEAKER than the max card, block it.
                if (maxCard != null && Rules.scoreCard(card) < Rules.scoreCard(maxCard)) {
                    ui.showNotification(
                            "TACTICIAN SKILL: Forced Commitment!\nYou must play your highest available card ("
                                    + maxCard.getName() + " or equivalent)!");
                    return;
                }
            }
        }

        gameState.getPlayerHand().remove(card);
        gameState.setPlayerPlayedCard(card);

        // If player leads, set this card as the lead card
        if (!gameState.isDealerLeadsTrick()) {
            gameState.setCurrentLeadCard(card);
        }

        // If player leads, now dealer plays
        if (!gameState.isDealerLeadsTrick()) {
            // Dealer dealer = gameState.getCurrentDealer(); // Already got above
            Card dealerCard = dealer.chooseCard(card, gameState.getDealerHand());

            if (dealerCard != null) {
                gameState.getDealerHand().remove(dealerCard);
                gameState.setDealerPlayedCard(dealerCard);
            }
        }

        ui.getPhase1Panel().refresh();

        // Resolve trick after a short delay
        Timer timer = new Timer(1500, e -> resolveTrick(card, gameState.getDealerPlayedCard()));
        timer.setRepeats(false);
        timer.start();
    }

    private void playDealerLeadCard() {
        Dealer dealer = gameState.getCurrentDealer();
        // Dealer leads, so playerCard is null
        Card dealerCard = dealer.chooseCard(null, gameState.getDealerHand());

        if (dealerCard != null) {
            gameState.getDealerHand().remove(dealerCard);
            gameState.setDealerPlayedCard(dealerCard);
            gameState.setCurrentLeadCard(dealerCard); // Set dealer's card as lead card
        }
        ui.getPhase1Panel().refresh();
    }

    private void resolveTrick(Card playerCard, Card dealerCard) {
        Dealer dealer = gameState.getCurrentDealer();
        int currentTrickNum = phase1.getTricksWon() + phase1.getTricksLost() + 1;

        // BOSS NOTIFICATIONS (ONCE PER SKILL TRIGGER)
        if (dealer.isEconomist() && currentTrickNum == 5) {
            ui.showNotification("ECONOMIST SKILL: Value Drain!\nFrom now on, your card values are reduced by 1!");
        } else if (dealer.isFinalBoss() && currentTrickNum == 1) {
            ui.showNotification("FINAL BOSS SKILL: Dominance!\nIf suits match, you need +3 rank difference to win!");
        }

        // --- SPECIAL CARD LOGIC (ON_ROUND) ---
        // 1. Pre-calculation Context (for modifying Rank, forcing Win/Loss etc.)
        // Note: winStreak is current streak before this trick result
        EffectContext ctxPre = new EffectContext(
            gameState,
            playerCard,
            dealerCard,
            false, 
            currentTrickNum,
            gameState.getRound(),
            gameState.getScorePhase1(),
            phase1.getWinStreak()
        );

        TrickModifier modPre = gameState.getInventory().applyEffects(ctxPre, EffectTrigger.ON_ROUND);
        
        // Show Pre-notifications
        if (modPre != null) {
            for (String msg : modPre.getNotificationMessages()) {
                ui.getPhase1Panel().showNotification(msg);
            }
        }

        // Use the new Rules.getTrickWinner method with modifier
        boolean playerWins = Rules.getTrickWinner(gameState.getCurrentLeadCard(), playerCard, dealerCard, dealer,
                currentTrickNum, modPre);

        if (playerWins) {
            phase1.playerWinsTrick(playerCard, dealerCard);
            gameState.setDealerLeadsTrick(false); // Player wins, player leads next trick
        } else {
            phase1.dealerWinsTrick(playerCard, dealerCard);
            gameState.setDealerLeadsTrick(true); // Dealer wins, dealer leads next trick
        }

        // Logic Check: LEAD_LEECH
        // If player has Lead Leech, they ALWAYS lead the next trick regardless of who won.
        if (gameState.getInventory().hasEffect(EffectType.LEAD_LEECH)) {
            gameState.setDealerLeadsTrick(false);
        }

        // --- SPECIAL CARD LOGIC (Post-Win determination) ---
        // Apply ON_ROUND again (for effects depending on win, e.g. Infinite Tricks)
        // And AFTER_ROUND effects
        EffectContext ctxPost = new EffectContext(
             gameState,
             playerCard,
             dealerCard,
             playerWins,
             currentTrickNum,
             gameState.getRound(),
             gameState.getScorePhase1(),
             phase1.getWinStreak() 
        );
        
        TrickModifier modPostOnRound = gameState.getInventory().applyEffects(ctxPost, EffectTrigger.ON_ROUND);
        TrickModifier modAfter = gameState.getInventory().applyEffects(ctxPost, EffectTrigger.AFTER_ROUND);
        
        TrickModifier finalMod = TrickModifier.combine(modPre, TrickModifier.combine(modPostOnRound, modAfter));
        
        // Show Post-notifications
         if (modPostOnRound != null) {
            for (String msg : modPostOnRound.getNotificationMessages()) {
                ui.getPhase1Panel().showNotification(msg);
            }
        }
         if (modAfter != null) {
            for (String msg : modAfter.getNotificationMessages()) {
                ui.getPhase1Panel().showNotification(msg);
            }
        }

        if (playerWins) {
            // If player followed suit, they get money
            boolean followedSuit = (playerCard.getSuit() == gameState.getCurrentLeadCard().getSuit());
            if (finalMod != null && finalMod.isForceFollowSuit()) {
                followedSuit = true;
            }

            if (followedSuit) {
                // Calculate money value, considering Boss skills (e.g. Economist -1 value)
                int playerValue = Rules.getCardValueForMoney(playerCard);
                if (dealer != null) {
                    playerValue = dealer.modifyPlayerCardValue(playerValue);
                }

                int moneyEarned = playerValue + Rules.getCardValueForMoney(dealerCard);
                
                // Apply Multiplier and Flat Bonus from SpecialCards
                if (finalMod != null) {
                    if (finalMod.getPointMultiplier() != 1.0) {
                         moneyEarned = (int) (moneyEarned * finalMod.getPointMultiplier());
                    }
                    moneyEarned += finalMod.getFlatPointsBonus();
                }

                // Ensure non-negative money
                moneyEarned = Math.max(0, moneyEarned);

                gameState.addMoney(moneyEarned);
                gameState.addMoneyFromTricks(moneyEarned);
            }
        }
        
        System.out.println("Player plays " + playerCard.getName() + ", Dealer plays "
                + (dealerCard != null ? dealerCard.getName() : "nothing") + ". Player wins: " + playerWins);

        // Clear played cards and lead card
        gameState.setPlayerPlayedCard(null);
        gameState.setDealerPlayedCard(null);
        gameState.setCurrentLeadCard(null);

        // Check for win/loss conditions
        if (phase1.isWin() || phase1.isLoss()) {
            onPhase1Finish();
        }
        // Check if round is over (no cards left in hand)
        else if (gameState.getPlayerHand().isEmpty()) {
            onPhase1Finish();
        } else {
            // Start next trick
            startTrick();
        }
    }

    public void startGame() {
        ui.switchView(UIWindow.MENU_VIEW);
    }

    public void startRun() {
        gameState.startNewRun();
        setDealerForCurrentStage();
        setupNewRound();
        phase1.start();
        startPhase1(); // Call startPhase1() first to switch view
        startTrick(); // Then start the first trick
    }

    private void startTrick() {
        // Update BossDealer trick count
        Dealer dealer = gameState.getCurrentDealer();
        int currentTrickNum = phase1.getTricksWon() + phase1.getTricksLost() + 1;

        if (dealer instanceof model.entity.dealer.BossDealer) {
            ((model.entity.dealer.BossDealer) dealer).onTrickStart(currentTrickNum);
        }

        if (gameState.isDealerLeadsTrick()) {
            playDealerLeadCard();
        } else {
            // Player leads, just refresh UI to enable player input
            ui.getPhase1Panel().refresh();
        }
    }

    private void startPhase1() {
        ui.switchView(UIWindow.PHASE1_VIEW);
        // The refresh here is important for initial state after view switch
        if (ui.getPhase1Panel() != null) {
            ui.getPhase1Panel().refresh();
        }
    }

    public void onPhase1Finish() {
        phase1.getReward();

        // Award money regardless of win/loss
        // gameState.addMoney(gameState.getScorePhase1());

        if (phase1.isWin()) {
            gameState.setPhase1Won(true);
            EffectContext afterStageCtx = new EffectContext(
                    gameState,
                    gameState.getRound(),
                    gameState.getScorePhase1(),
                    phase1.getCapturedCards());
            TrickModifier mod = gameState.getInventory().applyEffects(afterStageCtx, EffectTrigger.AFTER_STAGE);
            
            if (mod != null) {
                for (String msg : mod.getNotificationMessages()) {
                    ui.getPhase1Panel().showNotification(msg);
                }
            }
        } else {
            gameState.setPhase1Won(false);
            gameState.decreaseLife();
            if (gameState.isDead()) {
                gameOver();
                return;
            }
        }

        // Apply interest before showing results
        gameState.applyDebtInterest();

        // Instead of proceeding to debt payment, show the result panel
        ui.switchView(UIWindow.PHASE1_RESULT_VIEW);
        ui.getPhase1ResultPanel().updateResults();
    }

    public void continueToPhase2() {
        // This method is called from Phase1ResultPanel
        if (gameState.isDead()) {
            gameOver(); // If player is dead, go to game over (menu)
        } else {
            proceedDebtPayment(); // Otherwise, continue to debt payment and then Phase 2
        }
    }

    private void proceedDebtPayment() {
        startPhase2();
    }

    private void startPhase2() {
        phase2.loadShop();
        phase2.rollBiddingItem();
        ui.switchView(UIWindow.PHASE2_VIEW);

        // Refresh the shop UI to ensure it displays new items
        if (ui.getPhase2ShopPanel() != null) {
            ui.getPhase2ShopPanel().resetShop();
        }
    }

    public void onPhase2Finish() {
        gameState.increaseRound();
        double currentInterestRate = gameState.getInterestRate();
        gameState.setInterestRate(currentInterestRate + 0.001);
        setDealerForCurrentStage();
        setupNewRound();
        phase1.start();
        startPhase1(); // Call startPhase1() first to switch view
        startTrick(); // Then start the first trick of the new round
    }

    private void setupNewRound() {
        List<Card> deck = gameState.getDeck();
        List<Card> playerHand = gameState.getPlayerHand();
        List<Card> dealerHand = gameState.getDealerHand();

        deck.clear();
        playerHand.clear();
        dealerHand.clear();
        gameState.setPlayerPlayedCard(null);
        gameState.setDealerPlayedCard(null);
        gameState.setCurrentLeadCard(null); // Reset lead card
        phase1.reset();
        gameState.resetMoneyFromTricks();

        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                deck.add(new NormalCard(suit, rank));
            }
        }

        Collections.shuffle(deck, new Random(gameState.getSeed() + gameState.getRound()));

        for (int i = 0; i < 13; i++) {
            playerHand.add(deck.remove(0));
            dealerHand.add(deck.remove(0));
        }
        // Ensure dealer leads the first trick of the new round (default)
        gameState.setDealerLeadsTrick(true);

        // Apply BEFORE_STAGE effects (e.g. Lead Leech can change dealerLeadsTrick)
        EffectContext beforeStageCtx = new EffectContext(gameState, gameState.getRound(), 0, null);
        TrickModifier mod = gameState.getInventory().applyEffects(beforeStageCtx, EffectTrigger.BEFORE_STAGE);
        
        // Show notifications if any
        if (mod != null && ui.getPhase1Panel() != null) {
            for (String msg : mod.getNotificationMessages()) {
                 ui.getPhase1Panel().showNotification(msg);
            }
        }
    }

    private void setDealerForCurrentStage() {
        int stage = gameState.getRound();
        Dealer dealer = createDealerForStage(stage);
        gameState.setCurrentDealer(dealer);
    }

    private Dealer createDealerForStage(int stage) {
        long seed = gameState.getSeed();
        Random rng = new Random(seed + stage);

        return switch (stage) {
            case 1 -> new Dealer("INTRO BOSS", rng) {
                @Override
                public int bid(SpecialCard biddingItem, int round, int playerBid) {
                    // New logic: re-bid 2-10 more than player, max 20
                    int rebidAmount = 2 + rng.nextInt(9); // 2-10
                    int dealerBid = playerBid + rebidAmount;
                    return Math.min(dealerBid, 20);
                }

                @Override
                public int getMaxBid() {
                    return 20;
                }

                @Override
                public Card chooseCard(Card playerCard, List<Card> dealerHand) {
                    if (dealerHand.isEmpty())
                        return null;

                    // If dealer leads (playerCard is null), play the lowest card
                    if (playerCard == null) {
                        return dealerHand.stream()
                                .min(Comparator.comparingInt(Card::getValue))
                                .orElse(null);
                    }

                    Suit leadSuit = playerCard.getSuit();
                    // Filter cards that follow suit
                    List<Card> followSuitCards = dealerHand.stream()
                            .filter(c -> c.getSuit() == leadSuit)
                            .collect(Collectors.toList());

                    // If can follow suit
                    if (!followSuitCards.isEmpty()) {
                        // Try to win with a higher card of the same suit
                        Optional<Card> winningCard = followSuitCards.stream()
                                .filter(c -> c.getRank().getValue() > playerCard.getRank().getValue())
                                .min(Comparator.comparingInt(Card::getValue)); // Play lowest winning card

                        if (winningCard.isPresent()) {
                            return winningCard.get();
                        } else {
                            // If cannot win, play the lowest card of the same suit
                            return followSuitCards.stream()
                                    .min(Comparator.comparingInt(Card::getValue))
                                    .orElse(null);
                        }
                    } else {
                        // If cannot follow suit, play the lowest card overall (discard)
                        return dealerHand.stream()
                                .min(Comparator.comparingInt(Card::getValue))
                                .orElse(null);
                    }
                }
            };
            case 2 -> new TacticianBoss(rng);
            case 3 -> new EconomistBoss(rng);
            case 4 -> new FinalBossDealer(rng);
            default -> new FinalBossDealer(rng);
        };
    }

    private void gameOver() {
        ui.switchView(UIWindow.GAME_OVER_VIEW);
    }

    public GameState getGameState() {
        return gameState;
    }

    public void startBidding() {
        phase2.rollBiddingItem();
        ui.switchView(UIWindow.BIDDING_VIEW);
    }

    public static GameManager getInstance() {
        return instance;
    }

    public Phase1Game getPhase1Game() {
        return phase1;
    }

    public Phase2Game getPhase2Game() {
        return phase2;
    }
}
