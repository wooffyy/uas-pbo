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
    public SpecialCard() {
    }

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
        if (!canActivate())
            return null;

        TrickModifier modifier = new TrickModifier();
        GameState gameState = ctx.getGameState();
        boolean triggered = false;

        switch (effectType) {
            // BEFORE_STAGE
            case LEAD_LEECH:
                gameState.setPlayerLeads(true);
                gameState.setDealerLeadsTrick(false); // Explicitly override dealer lead
                triggered = true;
                break;
            case DEALER_DOOM:
                modifier.addDealerRankBoost(-2);
                modifier.addNotificationMessage(name + ": -2 Dealer Rank");
                triggered = true;
                break;

            // AFTER_ROUND
            case HEART_HARVESTER:
                // Logic: IF (PlayersWin == TRUE AND PlayerCard.suit == HEART) -2 DEBT
                if (ctx.isPlayerWin() && ctx.getPlayerCard().getSuit() == Suit.HEARTS) {
                     gameState.setDebt(gameState.getDebt() - 2);
                     modifier.addNotificationMessage(name + ": -2 Debt (Won with Heart)");
                     triggered = true;
                }
                break;
            case CLUB_CRUSHER:
                // Logic: IF (PlayersWin == TRUE AND PlayerCard.suit == CLUB) +2 POINT
                if (ctx.isPlayerWin() && ctx.getPlayerCard().getSuit() == Suit.CLUBS) {
                    gameState.setScorePhase1(gameState.getScorePhase1() + 2);
                    modifier.addNotificationMessage(name + ": +2 Points (Won with Club)");
                    triggered = true;
                }
                break;
            case DIAMOND_DEALER:
                // Logic: IF (PlayersWin == TRUE AND PlayerCard.suit == DIAMOND) IF (INTEREST != 0) -0,1 INTEREST
                if (ctx.isPlayerWin() && ctx.getPlayerCard().getSuit() == Suit.DIAMONDS) {
                    double currentRate = gameState.getInterestRate();
                    if (currentRate > 0) {
                        gameState.setInterestRate(Math.max(0, currentRate - 0.001));
                        modifier.addNotificationMessage(name + ": -0.1% Interest (Won with Diamond)");
                        triggered = true;
                    }
                }
                break;
            case HIGH_HIJACK:
                // Logic: IF (PlayerWin == TRUE AND DealerCard.rank == K OR Q OR J) +10 POINT
                if (ctx.isPlayerWin() && ctx.getDealerCard().getRank().isFaceCard()) {
                    gameState.setScorePhase1(gameState.getScorePhase1() + 10);
                    modifier.addNotificationMessage(name + ": +10 Points (Captured Face Card)");
                    triggered = true;
                }
                break;
             case TRICK_THIEF:
                // IF (PlayersWin == TRUE) 2 x (DealerCard.value + PlayerCard.value) by 0.2 chance
                if (ctx.isPlayerWin() && ctx.getRandom().nextDouble() < 0.2) {
                     modifier.addPointMultiplier(2.0); 
                     modifier.addNotificationMessage(name + ": Double Points!");
                     triggered = true;
                }
                break;
            case TEN_TAKER:
                // IF (PlayerWin == TRUE AND DealerCard.rank == 10) +20% (DealerCard.value + PlayerCard.value)
                if (ctx.isPlayerWin() && ctx.getDealerCard().getRank().getValue() == 10) {
                    modifier.addPointMultiplier(1.2);
                    modifier.addNotificationMessage(name + ": +20% Points");
                    triggered = true;
                }
                break;
            case CASCADE_CAPTURE:
                // IF (RoundWinStreak > 3) +20 (DealerCard.value + PlayerCard.value)
                if (ctx.getWinStreak() > 3) {
                    modifier.addFlatPointsBonus(20);
                    modifier.addNotificationMessage(name + ": +20 Bonus Points");
                    triggered = true;
                }
                break;

            // AFTER_STAGE
            case POINT_PARASITE:
                int moneyFromTricks = gameState.getMoneyFromTricks();
                if (moneyFromTricks > 50) {
                    int bonus = (int) (moneyFromTricks * 0.3);
                    gameState.addMoney(bonus); // Award bonus money
                    // Note: We don't update ScorePhase1 anymore as it's separate from money
                    modifier.addNotificationMessage(name + ": +30% Bonus (" + bonus + " Money)");
                    triggered = true;
                }
                break;

            // ON_ROUND
             case LOW_LIFTER:
                // IF (PlayerCard.rank == 2 OR 3 OR 4 OR 5) PlayerCard.rank +2
                if (ctx.getPlayerCard().getRank().getValue() >= 2 && ctx.getPlayerCard().getRank().getValue() <= 5) {
                    modifier.addPlayerRankBoost(2);
                    modifier.addNotificationMessage(name + ": +2 Rank Boost");
                    triggered = true;
                }
                break;
            case RANK_RISER:
                // IF (PlayerCard.rank < DealerCard.rank) +1 PlayerCard.rank Cooldown = 3 round
                if (ctx.getPlayerCard().getPower() < ctx.getDealerCard().getPower()) {
                    modifier.addPlayerRankBoost(1);
                    setCooldownRound(3);
                    modifier.addNotificationMessage(name + ": +1 Rank Boost");
                    triggered = true;
                }
                break;
            case SPADE_SNEAK:
                // IF (PlayerCard.suit == SPADE AND PlayerCard.rank > DealerCard.rank) Ignore DealerCard.suit Cooldown = 1 stage
                if (ctx.getPlayerCard().getSuit() == Suit.SPADES
                        && ctx.getPlayerCard().getPower() > ctx.getDealerCard().getPower()) {
                    modifier.setIgnoreSuitRule(true);
                    setCooldownStage(1);
                    modifier.addNotificationMessage(name + ": Ignored Suit Rule");
                    triggered = true;
                }
                break;
            case SUIT_SWAPPER:
                // IF (PlayerCard.suit != DealerCard.suit AND PlayerCard.rank > DealerCard.rank) PlayerCard.suit = DealerCard.suit
                if (ctx.getPlayerCard().getSuit() != ctx.getDealerCard().getSuit()
                        && ctx.getPlayerCard().getPower() > ctx.getDealerCard().getPower()) {
                    modifier.setForceFollowSuit(true); 
                    modifier.addNotificationMessage(name + ": Forced Follow Suit");
                    triggered = true;
                }
                break;
            case VOID_VIPER:
                // IF (PlayerCard.suit != DealerCard.suit) Ignore Suit
                if (ctx.getPlayerCard().getSuit() != ctx.getDealerCard().getSuit()) {
                    modifier.setIgnoreSuitRule(true);
                    setCooldownRound(3); 
                    modifier.addNotificationMessage(name + ": Suit Ignored");
                    triggered = true;
                }
                break;
            case RANK_RAMPAGE:
                 // IF (PlayerCard.rank < DealerCard.rank) +3 PlayerCard.rank Cooldown = 3 round
                if (ctx.getPlayerCard().getPower() < ctx.getDealerCard().getPower()) {
                    modifier.addPlayerRankBoost(3);
                    setCooldownRound(3);
                    modifier.addNotificationMessage(name + ": +3 Rank Boost");
                    triggered = true;
                }
                break;
            case TRUMP_TYRANT:
                 // IF (PlayerCard.suit != DealerCard.suit) -> Force Win!
                 if (ctx.getPlayerCard().getSuit() != ctx.getDealerCard().getSuit()) {
                    modifier.setForceWin(true);
                    setCooldownRound(3);
                    modifier.addNotificationMessage(name + ": Tyrant Win!");
                    triggered = true;
                }
                break;
            case INFINITE_TRICKS:
                // IF (PlayersWin = TRUE) 2 x (DealerCard.value + PlayerCard.value)
                if (ctx.isPlayerWin()) {
                     modifier.addPointMultiplier(2.0);
                     modifier.addNotificationMessage(name + ": Infinite Tricks (Double Points)");
                     triggered = true;
                }
                break;
        }

        if (triggered) {
            return modifier;
        }
        return null; // Return null if not triggered to avoid empty modifiers
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
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public EffectType getEffectType() {
        return effectType;
    }

    public EffectTrigger getEffectTrigger() {
        return effectTrigger;
    }

    public int getPrice() {
        return price;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }

    public void setRarity(Rarity rarity) {
        this.rarity = rarity;
    }
}
