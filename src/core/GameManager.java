package core;

import model.card.*;
import model.entity.dealer.Dealer;
import model.entity.dealer.EconomistBoss;
import model.entity.dealer.FinalBossDealer;
import model.entity.dealer.TacticianBoss;
import model.state.GameState;
import ui.UIWindow;

import javax.swing.Timer;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Optional;
import java.util.stream.Collectors;

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

        gameState.getPlayerHand().remove(card);
        gameState.setPlayerPlayedCard(card);

        // If player leads, set this card as the lead card
        if (!gameState.isDealerLeadsTrick()) {
            gameState.setCurrentLeadCard(card);
        }

        // If player leads, now dealer plays
        if (!gameState.isDealerLeadsTrick()) {
            Dealer dealer = gameState.getCurrentDealer();
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
        // Use the new Rules.getTrickWinner method
        boolean playerWins = Rules.getTrickWinner(gameState.getCurrentLeadCard(), playerCard, dealerCard);

        if (playerWins) {
            phase1.playerWinsTrick(playerCard, dealerCard);
            gameState.setDealerLeadsTrick(false); // Player wins, player leads next trick

            // If player followed suit, they get money
            if (playerCard.getSuit() == gameState.getCurrentLeadCard().getSuit()) {
                int moneyEarned = Rules.getCardValueForMoney(playerCard) + Rules.getCardValueForMoney(dealerCard);
                gameState.addMoney(moneyEarned);
                gameState.addMoneyFromTricks(moneyEarned);
            }
        } else {
            phase1.dealerWinsTrick(playerCard, dealerCard);
            gameState.setDealerLeadsTrick(true); // Dealer wins, dealer leads next trick
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
        gameState.addMoney(gameState.getScorePhase1());

        if (phase1.isWin()) {
            gameState.setPhase1Won(true);
            EffectContext afterStageCtx = new EffectContext(
                    gameState,
                    gameState.getRound(),
                    gameState.getScorePhase1(),
                    phase1.getCapturedCards());
            gameState.getInventory().applyEffects(afterStageCtx, EffectTrigger.AFTER_STAGE);
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
    }

    public void onPhase2Finish() {
        gameState.increaseRound();
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
        // Ensure dealer leads the first trick of the new round
        gameState.setDealerLeadsTrick(true);
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
                public int bid(SpecialCard biddingItem, int round) {
                    // Logic: Cap at 40, minimum base price + small random
                    int base = biddingItem.getPrice();
                    int max = 40;
                    return Math.min(max, base + new Random().nextInt(10));
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
