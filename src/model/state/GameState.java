package model.state;

import core.Rules;
import model.card.SpecialCard;
import model.entity.Dealer;
import model.entity.Player;

// Progress game untuk load/save
public class GameState {
    private Player player;
    private Dealer currentDealer;

    private int round;
    private int scorePhase1;

    private int debt;
    private double interestRate;
    private int playerMoney;
    private int playerHealth;

    private PlayerInventory inventory;

    private boolean gameOver;
    private boolean playerLeads;

    private long seed;

    public GameState(Player player, Dealer dealer) {
        this.player = player;
        this.currentDealer = dealer;
        this.inventory = new PlayerInventory();
    }

    public void startNewRun() {
        this.round = 1;
        this.scorePhase1 = 0;

        this.playerHealth = 3;
        this.playerMoney = 0;
        this.inventory.clear();

        this.debt = 10000;
        this.interestRate = 0.05;

        this.playerLeads = false;
        this.gameOver = false;

        this.seed = System.currentTimeMillis();
    }

    public int applyDebtInterest() {
        int added = Rules.calculateInterest(this.debt, this.interestRate);
        this.debt += added;
        return this.debt;
    }

    public boolean payDebt(double amount) {
        if (this.playerMoney < amount) {
            return false;
        }

        this.playerMoney -= amount;
        this.debt -= amount;
        if (this.debt < 0) this.debt = 0;

        return true;
    }

    public int decreaseLife() {
        this.playerHealth -= 1;
        return this.playerHealth;
    }

    public boolean isDead() {
        return this.playerHealth <= 0;
    }

    public void increaseRound() {
        this.round += 1;
    }

    public long getSeed() {
        return this.seed;
    }

    public Dealer getCurrentDealer() {
        return this.currentDealer;
    }

    public void addMoney(int amount) {
        this.playerMoney += amount;
    }

    public boolean isGameOver() {
        return this.gameOver;
    }

    public void startNewRound() {
        this.round++;
        this.scorePhase1 = 0;
    }

    public int getRound() {
        return this.round;
    }

    public int getMoney() {
        return this.playerMoney;
    }

    public void addInventory(SpecialCard card) {
        this.inventory.add(card);
    }

    public PlayerInventory getInventory() {
        return this.inventory;
    }

    public void setMoney(int amount) {
        this.playerMoney += amount;
    }
}