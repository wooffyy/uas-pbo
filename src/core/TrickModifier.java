package core;

import java.util.ArrayList;
import java.util.List;

public class TrickModifier {
    private int playerRankBoost = 0;
    private int dealerRankBoost = 0; // New field
    private boolean ignoreSuitRule = false;
    private boolean forceFollowSuit = false;
    private boolean forceWin = false;
    private boolean retrigger = false;
    private int retriggerCount = 0; // For cards like Infinite Tricks
    private double pointMultiplier = 1.0;
    private int flatPointsBonus = 0;

    private List<String> notificationMessages = new ArrayList<>();

    public int getPlayerRankBoost() {
        return playerRankBoost;
    }

    public void addPlayerRankBoost(int boost) {
        this.playerRankBoost += boost;
    }

    public int getDealerRankBoost() {
        return dealerRankBoost;
    }

    public void addDealerRankBoost(int boost) {
        this.dealerRankBoost += boost;
    }

    public boolean isIgnoreSuitRule() {
        return ignoreSuitRule;
    }

    public void setIgnoreSuitRule(boolean ignoreSuitRule) {
        this.ignoreSuitRule = ignoreSuitRule;
    }

    public boolean isForceFollowSuit() {
        return forceFollowSuit;
    }

    public void setForceFollowSuit(boolean forceFollowSuit) {
        this.forceFollowSuit = forceFollowSuit;
    }

    public boolean isForceWin() {
        return forceWin;
    }

    public void setForceWin(boolean forceWin) {
        this.forceWin = forceWin;
    }

    public boolean isRetrigger() {
        return retrigger;
    }

    public void setRetrigger(boolean retrigger) {
        this.retrigger = retrigger;
    }

    public int getRetriggerCount() {
        return retriggerCount;
    }
    
    public void setRetriggerCount(int retriggerCount) {
        this.retriggerCount = retriggerCount;
    }

    public double getPointMultiplier() {
        return pointMultiplier;
    }

    public void addPointMultiplier(double multiplier) {
        this.pointMultiplier *= multiplier;
    }

    public int getFlatPointsBonus() {
        return flatPointsBonus;
    }

    public void addFlatPointsBonus(int bonus) {
        this.flatPointsBonus += bonus;
    }

    public List<String> getNotificationMessages() {
        return notificationMessages;
    }

    public void addNotificationMessage(String message) {
        this.notificationMessages.add(message);
    }

    public static TrickModifier combine(TrickModifier m1, TrickModifier m2) {
        if (m1 == null) return m2;
        if (m2 == null) return m1;

        TrickModifier combined = new TrickModifier();
        combined.addPlayerRankBoost(m1.getPlayerRankBoost() + m2.getPlayerRankBoost());
        combined.addDealerRankBoost(m1.getDealerRankBoost() + m2.getDealerRankBoost());
        combined.setIgnoreSuitRule(m1.isIgnoreSuitRule() || m2.isIgnoreSuitRule());
        combined.setForceFollowSuit(m1.isForceFollowSuit() || m2.isForceFollowSuit());
        combined.setForceWin(m1.isForceWin() || m2.isForceWin());
        combined.setRetrigger(m1.isRetrigger() || m2.isRetrigger());
        combined.setRetriggerCount(m1.getRetriggerCount() + m2.getRetriggerCount());
        combined.addPointMultiplier(m1.getPointMultiplier() * m2.getPointMultiplier());
        combined.addFlatPointsBonus(m1.getFlatPointsBonus() + m2.getFlatPointsBonus());
        
        combined.getNotificationMessages().addAll(m1.getNotificationMessages());
        combined.getNotificationMessages().addAll(m2.getNotificationMessages());

        return combined;
    }
}
