package core.shop;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import model.card.EffectTrigger;
import model.card.EffectType;
import model.card.Rarity;
import model.card.SpecialCard;

public class ShopCardPool {

        private static final Random random = new Random();

        /*
         * =========================
         * SHOP CONFIG
         * =========================
         */

        // Chance muncul di shop (NO LEGENDARY IN SHOP!)
        private static final double COMMON_CHANCE = 0.70;
        private static final double RARE_CHANCE = 0.20;
        private static final double SUPER_RARE_CHANCE = 0.10;

        /*
         * =========================
         * CARD POOLS
         * =========================
         */

        private static final List<SpecialCard> COMMON_CARDS = new ArrayList<>();
        private static final List<SpecialCard> RARE_CARDS = new ArrayList<>();
        private static final List<SpecialCard> SUPER_RARE_CARDS = new ArrayList<>();
        private static final List<SpecialCard> LEGENDARY_CARDS = new ArrayList<>();

        static {
                // ===== COMMON =====
                COMMON_CARDS.add(card(1, "Rank Riser", EffectType.RANK_RISER, EffectTrigger.ON_ROUND,
                                "+1 rank played card when the card is lower than dealer (once per 3 trick)", 6));

                COMMON_CARDS.add(card(2, "Heart Harvester", EffectType.HEART_HARVESTER, EffectTrigger.AFTER_ROUND,
                                "When Hearts captured, -2 debt", 4));

                COMMON_CARDS.add(card(3, "Spade Sneak", EffectType.SPADE_SNEAK, EffectTrigger.ON_ROUND,
                                "When Spades played but the rank is lower than dealer, ignore follow suit (once per Boss)", 6));

                COMMON_CARDS.add(card(4, "Club Crusher", EffectType.CLUB_CRUSHER, EffectTrigger.AFTER_ROUND,
                                "When Club captured, +2 money", 4));

                COMMON_CARDS.add(card(5, "Diamond Dealer", EffectType.DIAMOND_DEALER, EffectTrigger.AFTER_ROUND,
                                "When Diamond captured, -0,1 interest", 5));

                COMMON_CARDS.add(card(6, "Low Lifter", EffectType.LOW_LIFTER, EffectTrigger.ON_ROUND,
                                "+2 rank for 2, 3, 4, and 5 card", 6));

                COMMON_CARDS.add(card(7, "Ten Taker", EffectType.TEN_TAKER, EffectTrigger.AFTER_ROUND,
                                "When 10 captured, +30% bonus from the total money earned from the trick.", 6));

                // ===== RARE =====
                RARE_CARDS.add(card(101, "Suit Swapper", EffectType.SUIT_SWAPPER, EffectTrigger.ON_ROUND,
                                "Swap the played card's suit into the dealer suit (Ignore Suit)", 12));

                RARE_CARDS.add(card(102, "High Hijack", EffectType.HIGH_HIJACK, EffectTrigger.AFTER_ROUND,
                                "When K, Q, or J captured, +10 money", 9));

                RARE_CARDS.add(card(103, "Trick Thief", EffectType.TRICK_THIEF, EffectTrigger.ON_ROUND,
                                "1 in 5 chance to x2 total money earned from the trick", 11));

                RARE_CARDS.add(card(104, "Void Viper", EffectType.VOID_VIPER, EffectTrigger.ON_ROUND,
                                "Ignore suit for once per Boss", 12));

                RARE_CARDS.add(card(105, "Point Parasite", EffectType.POINT_PARASITE, EffectTrigger.AFTER_STAGE,
                                "if the total money earned after stage is more than 50, +30% bonus", 10));

                // ===== SUPER RARE =====
                SUPER_RARE_CARDS.add(card(201, "Lead Leech", EffectType.LEAD_LEECH, EffectTrigger.BEFORE_STAGE,
                                "Make player always lead", 18));

                SUPER_RARE_CARDS.add(card(202, "Rank Rampage", EffectType.RANK_RAMPAGE, EffectTrigger.ON_ROUND,
                                "+3 rank played card when the card is lower than dealer (once per 3 trick)", 18));

                SUPER_RARE_CARDS.add(card(203, "Cascade Capture", EffectType.CASCADE_CAPTURE, EffectTrigger.AFTER_ROUND,
                                "Win 3+ times in a row, +20 money for each subsequent trick", 18));

                // ===== LEGENDARY =====
                LEGENDARY_CARDS.add(card(301, "Trump Tyrant", EffectType.TRUMP_TYRANT, EffectTrigger.ON_ROUND,
                                "Player card always highest and ignore follow suit for once per 3 trick", 25));

                LEGENDARY_CARDS.add(card(302, "Dealer Doom", EffectType.DEALER_DOOM, EffectTrigger.ON_ROUND,
                                "Dealer card always -2 rank", 30));

                LEGENDARY_CARDS.add(card(303, "Infinite Tricks", EffectType.INFINITE_TRICKS, EffectTrigger.ON_ROUND,
                                "Always x2 total money earned from the trick", 25));
        }

        /*
         * =========================
         * PUBLIC API
         * =========================
         */

        public static List<SpecialCard> getAllCards() {
                List<SpecialCard> all = new ArrayList<>();
                // We return copies to ensure safety
                for (SpecialCard c : COMMON_CARDS)
                        all.add(copy(c, Rarity.COMMON));
                for (SpecialCard c : RARE_CARDS)
                        all.add(copy(c, Rarity.RARE));
                for (SpecialCard c : SUPER_RARE_CARDS)
                        all.add(copy(c, Rarity.SUPER_RARE));
                for (SpecialCard c : LEGENDARY_CARDS)
                        all.add(copy(c, Rarity.LEGENDARY));
                return all;
        }

        /**
         * Generate N cards for shop display.
         */
        public static List<SpecialCard> rollShopCards(int amount) {
                List<SpecialCard> result = new ArrayList<>();

                for (int i = 0; i < amount; i++) {
                        result.add(rollSingleCard());
                }

                return result;
        }

        public static List<SpecialCard> roll(int amount) {
                return rollShopCards(amount);
        }

        /**
         * Roll a single card based on rarity chance.
         */
        public static SpecialCard rollSingleCard() {
                double roll = random.nextDouble();

                if (roll < COMMON_CHANCE) {
                        return copy(randomFrom(COMMON_CARDS), Rarity.COMMON);
                } else if (roll < COMMON_CHANCE + RARE_CHANCE) {
                        return copy(randomFrom(RARE_CARDS), Rarity.RARE);
                } else {
                        return copy(randomFrom(SUPER_RARE_CARDS), Rarity.SUPER_RARE);
                }
        }

        /*
         * =========================
         * INTERNAL HELPERS
         * =========================
         */

        private static SpecialCard randomFrom(List<SpecialCard> pool) {
                return pool.get(random.nextInt(pool.size()));
        }

        /**
         * IMPORTANT:
         * - Clone card
         * - Set rarity (price comes from base)
         * - Avoid shared instance bug
         */
        private static SpecialCard copy(SpecialCard base, Rarity rarity) {
                SpecialCard card = new SpecialCard(base);
                card.setRarity(rarity);
                // Price is already set in the base card
                return card;
        }

        private static SpecialCard card(int id, String name, EffectType type, EffectTrigger trigger, String desc,
                        int price) {
                return new SpecialCard(
                                id,
                                name,
                                type,
                                trigger,
                                price,
                                null, // rarity di-set saat spawn
                                desc);
        }
}
