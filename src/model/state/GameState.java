package model.state;

import core.Rules;
import model.card.SpecialCard;
import model.entity.Player;
import model.entity.dealer.Dealer;

/**
 * Menyimpan progres game (state only, no game logic).
 */
public class GameState {

    /* ================= CORE ENTITIES ================= */

    private Player player;
    private Dealer currentDealer;

    /* ================= PROGRESSION ================= */

    private int round;              // Stage ke berapa
    private int scorePhase1;         // Opsional (kalau mau dipakai)

    /* ================= PLAYER STATUS ================= */

    private int playerHealth;
    private int playerMoney;

    /* ================= ECONOMY ================= */

    private int debt;
    private double interestRate;

    /* ================= INVENTORY ================= */

    private PlayerInventory inventory;

    /* ================= FLAGS ================= */

    private boolean gameOver;
    private boolean playerLeads;

    /* ================= RNG ================= */

    private long seed;

    /* ================= CONSTRUCTOR ================= */

    public GameState() {
        this.inventory = new PlayerInventory();
    }

    public GameState(Player player, Dealer dealer) {
        this.player = player;
        this.currentDealer = dealer;
        this.inventory = new PlayerInventory();
    }

    /* ================= RUN INITIALIZATION ================= */

    public void startNewRun() {
        this.round = 1;
        this.scorePhase1 = 0;

        this.playerHealth = 3;
        this.playerMoney = 0;

        this.debt = 10000;
        this.interestRate = 0.05;

        this.inventory.clear();

        this.gameOver = false;
        this.playerLeads = false;

        this.seed = System.currentTimeMillis();
    }

    /* ================= DEALER ================= */

    public Dealer getCurrentDealer() {
        return currentDealer;
    }

    public void setCurrentDealer(Dealer dealer) {
        this.currentDealer = dealer;
    }

    /* ================= ROUND ================= */

    public int getRound() {
        return round;
    }

    public void increaseRound() {
        this.round++;
    }

    /* ================= PLAYER HEALTH ================= */

    public int getPlayerHealth() {
        return playerHealth;
    }

    public void decreaseLife() {
        this.playerHealth--;
        if (this.playerHealth <= 0) {
            this.gameOver = true;
        }
    }

    public boolean isDead() {
        return playerHealth <= 0;
    }

    /* ================= MONEY ================= */

    public int getMoney() {
        return playerMoney;
    }

    public void addMoney(int amount) {
        this.playerMoney += amount;
    }

    public void setMoney(int amount) {
        this.playerMoney = amount;
    }

    /* ================= DEBT ================= */

    public int getDebt() {
        return debt;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public int applyDebtInterest() {
        int added = Rules.calculateInterest(debt, interestRate);
        debt += added;
        return debt;
    }

    public boolean payDebt(int amount) {
        if (playerMoney < amount) return false;

        playerMoney -= amount;
        debt -= amount;
        if (debt < 0) debt = 0;

        return true;
    }

    /* ================= INVENTORY ================= */

    public PlayerInventory getInventory() {
        return inventory;
    }

    public void addInventory(SpecialCard card) {
        inventory.add(card);
    }

    /* ================= GAME STATE ================= */

    public boolean isGameOver() {
        return gameOver;
    }

    /* ================= RNG ================= */

    public long getSeed() {
        return seed;
    }
}
