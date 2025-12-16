package model.card;

import core.EffectContext;
import core.TrickModifier;
import model.state.GameState;

public class SpecialCard {

    // IDENTITAS
    private int id;
    private String name;
    private EffectType effectType;
    private EffectTrigger effectTrigger;
    private int price;
    private Rarity rarity;
    private String description;

    // STATE INTERNAL
    private int cooldownRound = 0;
    private int cooldownStage = 0;
    private boolean isAvailable = true;

    // CONSTRUCTOR
    public SpecialCard(int id, String name, EffectType effectType, EffectTrigger effectTrigger,
                       int price, Rarity rarity, String description) {
        this.id = id;
        this.name = name;
        this.effectType = effectType;
        this.effectTrigger = effectTrigger;
        this.price = price;
        this.rarity = rarity;
        this.description = description;
    }

    // default constructor (untuk load DB)
    public SpecialCard() {}

    // copy constructor
    public SpecialCard(SpecialCard other) {
        this.id = other.id;
        this.name = other.name;
        this.effectType = other.effectType;
        this.effectTrigger = other.effectTrigger;
        this.price = other.price;
        this.rarity = other.rarity;
        this.description = other.description;
        this.cooldownRound = 0;
        this.cooldownStage = 0;
        this.isAvailable = true;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public TrickModifier applyEffect(EffectContext ctx) {
        if (!canActivate()) return null;

        TrickModifier modifier = new TrickModifier();
        GameState gameState = ctx.getGameState();

        switch (effectType) {
            // BEFORE_STAGE
            case LEAD_LEECH:
                gameState.setPlayerLeads(true);
                break;
            case DEALER_DOOM:
                gameState.getCurrentDealer().applyRankModifier(-2);
                break;

            // AFTER_STAGE
            case HEART_HARVESTER:
                if (ctx.getCapturedCards() != null) {
                    for (Card card : ctx.getCapturedCards()) {
                        if (card.getSuit() == Suit.HEARTS) {
                            gameState.setDebt(gameState.getDebt() - 2);
                        }
                    }
                }
                break;
            case CLUB_CRUSHER:
                if (ctx.getCapturedCards() != null) {
                    int pointsToAdd = 0;
                    for (Card card : ctx.getCapturedCards()) {
                        if (card.getSuit() == Suit.CLUBS) {
                            pointsToAdd += 2;
                        }
                    }
                    gameState.setScorePhase1(gameState.getScorePhase1() + pointsToAdd);
                }
                break;
            case HIGH_HIJACK:
                if (ctx.getCapturedCards() != null) {
                    int pointsToAdd = 0;
                    for (Card card : ctx.getCapturedCards()) {
                        if (card.getRank().isFaceCard()) { 
                            pointsToAdd += 10;
                        }
                    }
                    gameState.setScorePhase1(gameState.getScorePhase1() + pointsToAdd);
                }
                break;
            case POINT_PARASITE:
                if (gameState.getScorePhase1() > 50) {
                   gameState.setScorePhase1((int) (gameState.getScorePhase1() * 1.3));
                }
                break;

            // BEFORE_ROUND
            case LOW_LIFTER:
                if (ctx.getPlayerCard().getRank().getValue() >= 2 && ctx.getPlayerCard().getRank().getValue() <= 5) {
                    modifier.addPlayerRankBoost(2);
                }
                break;

            // ON_ROUND
            case RANK_RISER:
                if (ctx.getPlayerCard().getValue() < ctx.getDealerCard().getValue()) {
                    modifier.addPlayerRankBoost(1);
                    setCooldownRound(3);
                }
                break;
            case SPADE_SNEAK:
                if (ctx.getPlayerCard().getSuit() == Suit.SPADES && ctx.getPlayerCard().getValue() > ctx.getDealerCard().getValue()) {
                    modifier.setIgnoreSuitRule(true);
                    setCooldownStage(1);
                }
                break;
            case SUIT_SWAPPER:
                if (ctx.getPlayerCard().getSuit() != ctx.getDealerCard().getSuit() && ctx.getPlayerCard().getValue() > ctx.getDealerCard().getValue()) {
                    modifier.setForceFollowSuit(true);
                }
                break;
            case TRICK_THIEF:
                if (ctx.isPlayerWin() && ctx.getRandom().nextDouble() < 0.2) {
                    modifier.setRetrigger(true);
                }
                break;
            case VOID_VIPER:
                if (ctx.getPlayerCard().getSuit() != ctx.getDealerCard().getSuit()) {
                    modifier.setIgnoreSuitRule(true);
                }
                break;
            case RANK_RAMPAGE:
                if (ctx.getPlayerCard().getValue() < ctx.getDealerCard().getValue()) {
                    modifier.addPlayerRankBoost(3);
                    setCooldownRound(3);
                }
                break;
            case TRUMP_TYRANT:
                if (ctx.getPlayerCard().getSuit() != ctx.getDealerCard().getSuit() && ctx.getPlayerCard().getValue() < ctx.getDealerCard().getValue()) {
                    modifier.setForceWin(true);
                    setCooldownRound(3);
                }
                break;
            case INFINITE_TRICKS:
                if (ctx.isPlayerWin()) {
                    modifier.setRetrigger(true);
                    modifier.setRetriggerCount(1); // just a flag
                }
                break;

            // AFTER_ROUND
            case TEN_TAKER:
                if (ctx.isPlayerWin() && (ctx.getPlayerCard().getRank().getValue() == 10 || ctx.getDealerCard().getRank().getValue() == 10)) {
                    modifier.addPointMultiplier(1.2);
                }
                break;
            case CASCADE_CAPTURE:
                if (ctx.getWinStreak() >= 3) {
                    modifier.addFlatPointsBonus(20);
                }
                break;
        }

        return modifier;
    }

    // COOLDOWN SYSTEM
    public void tickCooldownRound() {
        if (cooldownRound > 0) {
            cooldownRound--;
        }
    }

    public void tickCooldownStage() {
        if (cooldownStage > 0) {
            cooldownStage--;
        }
    }

    public void resetCooldowns() {
        cooldownRound = 0;
        cooldownStage = 0;
    }

    private boolean canActivate() {
        return isAvailable && cooldownRound == 0 && cooldownStage == 0;
    }

    private void setCooldownRound(int rounds) {
        this.cooldownRound = rounds;
    }

    private void setCooldownStage(int stages) {
        this.cooldownStage = stages;
    }

    // GETTER / SETTER
    public int getId() { return id; }
    public String getName() { return name; }
    public EffectType getEffectType() { return effectType; }
    public EffectTrigger getEffectTrigger() { return effectTrigger; }
    public int getPrice() { return price; }
    public Rarity getRarity() { return rarity; }
    public String getDescription() { return description; }
    public boolean isAvailable() { return isAvailable; }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }
}
