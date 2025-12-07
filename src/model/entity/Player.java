package model.entity;

import java.util.ArrayList;
import java.util.List;
import model.card.Card;
import model.card.SpecialCard;
import model.state.PlayerInventory;

// Menyimpan statistik: nyawa, hutang, kartu ditangkap
public class Player {
    private int health;
    private int debt;
    private int money;

    private List<Card> hand;
    private PlayerInventory inventory;

    public Player() {
        this.hand = new ArrayList<>();
        this.inventory = new PlayerInventory();
    }

    public int getHealth() { return health; }

    public void setHealth(int health) {
        this.health = health;
    }

    public void decreaseHealth(int amount) {
        this.health -= amount;
    }

    public int getDebt() { return debt; }

    public void setDebt(int debt) {
        this.debt = debt;
    }

    public void decreaseDebt(int amount) {
        this.debt -= amount;
    }

    public int getMoney() { return money; }

    public void setMoney(int money) {
        this.money = money;
    }

    public void increaseMoney(int amount) {
        this.money += amount;
    }

    public void decreaseMoney(int amount) {
        this.money -= amount;
    }

    public List<Card> getHand() { return hand; }

    public void setHand(List<Card> hand) {
        this.hand = hand;
    }

    public void removeCardFromHand(Card card) {
        this.hand.remove(card);
    }

    public PlayerInventory getInventory() { return inventory; }
    public void addSpecialCardToInventory(SpecialCard card) {
        this.inventory.add(card);
    }
}
