package util;

import model.card.*;

import java.util.ArrayList;
import java.util.List;

public class SpecialCardDB {

    public static List<SpecialCard> getAllCards() {
        List<SpecialCard> cards = new ArrayList<>();

        cards.add(new SpecialCard(1, "Rank Riser", EffectType.RANK_RISER, EffectTrigger.ON_ROUND, 6, Rarity.COMMON, "1 rank played card when the card is lower than dealer (once / 3 round)"));
        cards.add(new SpecialCard(2, "Heart Harvester", EffectType.HEART_HARVESTER, EffectTrigger.AFTER_STAGE, 4, Rarity.COMMON, "Hearts captured: -2 debt each"));
        cards.add(new SpecialCard(3, "Spade Sneak", EffectType.SPADE_SNEAK, EffectTrigger.ON_ROUND, 6, Rarity.COMMON, "Spades: Ignore follow suit once / stage"));
        cards.add(new SpecialCard(4, "Club Crusher", EffectType.CLUB_CRUSHER, EffectTrigger.AFTER_STAGE, 4, Rarity.COMMON, "Club captured: +2 point each"));
        cards.add(new SpecialCard(5, "Diamond Dealer", EffectType.DIAMOND_DEALER, EffectTrigger.AFTER_STAGE, 5, Rarity.COMMON, "Diamond captured: -0,1 interest each"));
        cards.add(new SpecialCard(6, "Low Lifter", EffectType.LOW_LIFTER, EffectTrigger.BEFORE_ROUND, 6, Rarity.COMMON, "2-5 cards: +2 rank vs dealer"));
        cards.add(new SpecialCard(7, "Ten Taker", EffectType.TEN_TAKER, EffectTrigger.AFTER_ROUND, 6, Rarity.COMMON, "Capture 10: + 20% points total number from that trick"));
        cards.add(new SpecialCard(8, "Suit Swapper", EffectType.SUIT_SWAPPER, EffectTrigger.ON_ROUND, 12, Rarity.RARE, "Swap suit lo ke suit dealer (force follow +win)"));
        cards.add(new SpecialCard(9, "High Hijack", EffectType.HIGH_HIJACK, EffectTrigger.AFTER_STAGE, 9, Rarity.COMMON, "J/Q/K: + 10 point from dealer per win"));
        cards.add(new SpecialCard(10, "Trick Thief", EffectType.TRICK_THIEF, EffectTrigger.ON_ROUND, 11, Rarity.RARE, "Win trick: 20% chance retrigger (play extra)"));
        cards.add(new SpecialCard(11, "Void Viper", EffectType.VOID_VIPER, EffectTrigger.ON_ROUND, 12, Rarity.RARE, "Can't follow: Still compare rank (no auto-lose) (once / 3 round)"));
        cards.add(new SpecialCard(12, "Point Parasite", EffectType.POINT_PARASITE, EffectTrigger.AFTER_STAGE, 10, Rarity.COMMON, "Total points >50/stage: +30% all captured pts"));
        cards.add(new SpecialCard(13, "Lead Leech", EffectType.LEAD_LEECH, EffectTrigger.BEFORE_STAGE, 18, Rarity.RARE, "Player lead pertama (swap dealer turn)"));
        cards.add(new SpecialCard(14, "Rank Rampage", EffectType.RANK_RAMPAGE, EffectTrigger.ON_ROUND, 16, Rarity.RARE, "+3 rank played card when the card is lower than dealer ( (once / 3 round)"));
        cards.add(new SpecialCard(15, "Cascade Capture", EffectType.CASCADE_CAPTURE, EffectTrigger.AFTER_ROUND, 18, Rarity.RARE, "Win 3+ consecutive: +20 pts each subsequent trick"));
        cards.add(new SpecialCard(16, "Trump Tyrant", EffectType.TRUMP_TYRANT, EffectTrigger.ON_ROUND, 60, Rarity.LEGENDARY, "Player suit selalu trump (always highest, ignore follow, once/3 round)"));
        cards.add(new SpecialCard(17, "Dealer Doom", EffectType.DEALER_DOOM, EffectTrigger.BEFORE_STAGE, 50, Rarity.LEGENDARY, "Dealer cards -2 rank always"));
        cards.add(new SpecialCard(18, "Infinite Tricks", EffectType.INFINITE_TRICKS, EffectTrigger.ON_ROUND, 30, Rarity.LEGENDARY, "Win trick: Retrigger full (play 2 cards, capture 3)"));

        return cards;
    }
}
