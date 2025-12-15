package model.entity.dealer;

import java.util.Random;
import model.card.Card;

public class FinalBossDealer extends BossDealer {

    public FinalBossDealer(Random rng) {
        super("FINAL ENFORCER", rng);
    }

    @Override
    public boolean overrideTrickWinner(
            Card playerCard,
            Card bossCard,
            boolean defaultWinner,
            int playerValue,
            int bossValue
    ) {
        // Dominance aktif sejak awal
        if (bossCard.getSuit() == lastBossSuit) {
            int diff = playerValue - bossValue;
            return diff >= 3;
        }
        return defaultWinner;
    }

    @Override
    public String getActiveSkillName() {
        return "Dominance";
    }

    @Override
    public boolean isSkillActive() {
        return true;
    }
}
