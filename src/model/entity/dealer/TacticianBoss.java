package model.entity.dealer;

import java.util.Random;

public class TacticianBoss extends BossDealer {

    public TacticianBoss(Random rng) {
        super("TACTICIAN", rng);
    }

    @Override
    public boolean forceHighestCard() {
        // Setelah 3 trick pertama → trick 4 & 5
        return trickCount >= 4 && trickCount <= 5;
    }

    @Override
    public String getActiveSkillName() {
        return "Forced Commitment";
    }

    @Override
    public boolean isSkillActive() {
        return forceHighestCard();
    }
}
