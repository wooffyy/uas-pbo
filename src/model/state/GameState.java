package model.state;

import core.Rules;
import model.card.*;
import model.entity.Player;
import model.entity.dealer.Dealer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class GameState {

    // Core Entities
    private Player player;
    private Dealer currentDealer;

    // Card Game State
    private List<Card> deck;
    private List<Card> playerHand;
    private List<Card> dealerHand;
    private Card playerPlayedCard; // Card currently in play
    private Card dealerPlayedCard; // Card currently in play
    private Card currentLeadCard; // New: The card that led the current trick
    private boolean dealerLeadsTrick; // New flag to determine who starts the trick

    // Progression
    private int round;
    private int scorePhase1;
    private int moneyFromTricks;

    // Player Status
    private int playerHealth;
    private int playerMoney;

    // Economy
    private double debt;
    private double interestRate;
    private double lastInterestAdded;

    // Inventory
    private PlayerInventory inventory;

    // Flags
    private boolean gameOver;
    private boolean playerLeads; // This might be redundant with dealerLeadsTrick, consider consolidating
    private boolean phase1Won; // Track if the player won the last Phase 1

    // RNG
    private long seed;

    public GameState() {
        this.inventory = new PlayerInventory();
        this.deck = new ArrayList<>();
        this.playerHand = new ArrayList<>();
        this.dealerHand = new ArrayList<>();
        this.dealerLeadsTrick = true; // Dealer starts the first trick by default
    }

    // Getters for Card Game State
    public List<Card> getDeck() {
        return deck;
    }

    public List<Card> getPlayerHand() {
        return playerHand;
    }

    public List<Card> getDealerHand() {
        return dealerHand;
    }

    public Card getPlayerPlayedCard() {
        return playerPlayedCard;
    }

    public Card getDealerPlayedCard() {
        return dealerPlayedCard;
    }

    public Card getCurrentLeadCard() {
        return currentLeadCard;
    } // New getter

    public boolean isDealerLeadsTrick() {
        return dealerLeadsTrick;
    }

    public void setPlayerPlayedCard(Card card) {
        this.playerPlayedCard = card;
    }

    public void setDealerPlayedCard(Card card) {
        this.dealerPlayedCard = card;
    }

    public void setCurrentLeadCard(Card card) {
        this.currentLeadCard = card;
    } // New setter

    public void setDealerLeadsTrick(boolean dealerLeadsTrick) {
        this.dealerLeadsTrick = dealerLeadsTrick;
    }

    public void startNewRun() {
        this.round = 1;
        this.scorePhase1 = 0;
        this.playerHealth = 3;
        this.playerMoney = 0;
        this.debt = 1000;
        this.interestRate = 0.10;
        this.inventory.clear();
        this.gameOver = false;
        this.phase1Won = false;
        this.playerLeads = false; // Keep for now, but might be removed
        this.dealerLeadsTrick = true; // Dealer starts the first trick of a new run
        this.seed = System.currentTimeMillis();
        this.moneyFromTricks = 0;

        // Clear hands and deck for the new run
        deck.clear();
        playerHand.clear();
        dealerHand.clear();
        playerPlayedCard = null;
        dealerPlayedCard = null;
        currentLeadCard = null; // Reset lead card
    }

    // Other existing methods...
    public Dealer getCurrentDealer() {
        return currentDealer;
    }

    public void setCurrentDealer(Dealer dealer) {
        this.currentDealer = dealer;
    }

    public int getRound() {
        return round;
    }

    public void increaseRound() {
        this.round++;
        this.interestRate += 0.10; // Increase interest by 10% each stage
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getScorePhase1() {
        return scorePhase1;
    }

    public void setScorePhase1(int score) {
        this.scorePhase1 = score;
    }

    public int getPlayerHealth() {
        return playerHealth;
    }

    public void decreaseLife() {
        this.playerHealth--;
        if (this.playerHealth <= 0) {
            this.gameOver = true;
        }
    }

    public void setPlayerHealth(int playerHealth) {
        this.playerHealth = playerHealth;
    }

    public boolean isDead() {
        return playerHealth <= 0;
    }

    public int getMoney() {
        return playerMoney;
    }

    public void addMoney(int amount) {
        this.playerMoney += amount;
    }

    public void setMoney(int amount) {
        this.playerMoney = amount;
    }

    public double getDebt() {
        return debt;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public double applyDebtInterest() {
        double added = Rules.calculateInterest(debt, interestRate);
        double newDebt = this.debt + added;

        // Round the new total debt to 3 decimal places using HALF_UP rounding
        BigDecimal newDebtBd = new BigDecimal(String.valueOf(newDebt));
        BigDecimal roundedNewDebtBd = newDebtBd.setScale(3, RoundingMode.HALF_UP);

        // Store the rounded interest amount for display purposes
        this.lastInterestAdded = roundedNewDebtBd.doubleValue() - this.debt;

        // Update the debt to the new rounded value
        this.debt = roundedNewDebtBd.doubleValue();

        return this.debt;
    }

    public boolean payDebt(int amount) {
        if (playerMoney < amount)
            return false;
        playerMoney -= amount;
        debt -= amount;
        if (debt < 0)
            debt = 0;
        return true;
    }

    public void setDebt(double debt) {
        this.debt = debt;
        if (this.debt < 0) {
            this.debt = 0;
        }
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public PlayerInventory getInventory() {
        return inventory;
    }

    public PlayerInventory getPlayerInventory() {
        return inventory;
    }

    public void setInventory(PlayerInventory inventory) {
        this.inventory = inventory;
    }

    public void addInventory(SpecialCard card) {
        inventory.add(card);
    }

    public void decreaseMoney(int amount) {
        this.playerMoney -= amount;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setPlayerLeads(boolean playerLeads) {
        this.playerLeads = playerLeads;
    } // Consider removing

    public boolean isPhase1Won() {
        return phase1Won;
    }

    public void setPhase1Won(boolean phase1Won) {
        this.phase1Won = phase1Won;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public double getLastInterestAdded() {
        return lastInterestAdded;
    }

    public void setLastInterestAdded(double lastInterestAdded) {
        this.lastInterestAdded = lastInterestAdded;
    }

    public int getMoneyFromTricks() {
        return moneyFromTricks;
    }

    public void addMoneyFromTricks(int amount) {
        this.moneyFromTricks += amount;
    }

    public void resetMoneyFromTricks() {
        this.moneyFromTricks = 0;
    }
}
