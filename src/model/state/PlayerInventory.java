package model.state;

import core.EffectContext;
import core.TrickModifier;
import model.card.EffectTrigger;
import model.card.SpecialCard;

import java.util.ArrayList;
import java.util.List;

public class PlayerInventory {
    private final List<SpecialCard> cards = new ArrayList<>();

    public void clear() {
        cards.clear();
    }

    public void add(SpecialCard card) {
        cards.add(card);
    }

    public void addCard(SpecialCard card) {
        add(card);
    }

    public List<SpecialCard> getCards() {
        return cards;
    }

    public TrickModifier applyEffects(EffectContext context, EffectTrigger trigger) {
        TrickModifier combinedModifier = new TrickModifier();
        for (SpecialCard card : cards) {
            if (card.getEffectTrigger() == trigger) {
                TrickModifier modifier = card.applyEffect(context);
                if (modifier != null) {
                    combinedModifier = TrickModifier.combine(combinedModifier, modifier);
                }
            }
        }
        return combinedModifier;
    }

    public void tickCooldowns() {
        for (SpecialCard card : cards) {
            card.tickCooldownRound();
            card.tickCooldownStage();
        }
    }
}
