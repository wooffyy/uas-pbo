package model.entity.dealer;

import java.util.Random;

public class EconomistBoss extends BossDealer {

    public EconomistBoss(Random rng) {
        super("ECONOMIST", rng);
    }

    @Override
    public int modifyPlayerCardValue(int originalValue) {
        if (trickCount >= 5) {
            return Math.max(1, originalValue - 1);
        }
        return originalValue;
    }

    @Override
    public String getActiveSkillName() {
        return "Value Drain";
    }

    @Override
    public boolean isSkillActive() {
        return trickCount >= 5;
    }
}
