package core.shop;

import model.card.SpecialCard;
import model.card.EffectType;
import model.card.EffectTrigger;
import model.card.Rarity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShopCardPool {

        private static final Random random = new Random();

        /*
         * =========================
         * SHOP CONFIG
         * =========================
         */

        // Chance muncul di shop
        private static final double COMMON_CHANCE = 0.60;
        private static final double RARE_CHANCE = 0.25;
        private static final double SUPER_RARE_CHANCE = 0.10;
        private static final double LEGENDARY_CHANCE = 0.05;

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
                COMMON_CARDS.add(card(1, "Rank Riser", EffectType.RANK_RISER,
                                "Boost rank when losing a trick", 6));

                COMMON_CARDS.add(card(2, "Heart Harvester", EffectType.HEART_HARVESTER,
                                "Reduce debt for each Heart captured", 4));

                COMMON_CARDS.add(card(3, "Spade Sneak", EffectType.SPADE_SNEAK,
                                "Ignore suit rule with Spades", 6));

                COMMON_CARDS.add(card(4, "Club Crusher", EffectType.CLUB_CRUSHER,
                                "Gain extra points from Clubs", 4));

                COMMON_CARDS.add(card(6, "Low Lifter", EffectType.LOW_LIFTER,
                                "Boost low-rank cards", 6));

                COMMON_CARDS.add(card(7, "Ten Taker", EffectType.TEN_TAKER,
                                "Bonus points when 10 is involved", 6));

                // ===== RARE =====
                RARE_CARDS.add(card(101, "Suit Swapper", EffectType.SUIT_SWAPPER,
                                "Force suit matching under conditions", 12));

                RARE_CARDS.add(card(102, "High Hijack", EffectType.HIGH_HIJACK,
                                "Gain points from face cards", 9));

                RARE_CARDS.add(card(103, "Trick Thief", EffectType.TRICK_THIEF,
                                "Chance to retrigger a won trick", 11));

                RARE_CARDS.add(card(104, "Void Viper", EffectType.VOID_VIPER,
                                "Ignore suit rule freely", 12));

                RARE_CARDS.add(card(105, "Point Parasite", EffectType.POINT_PARASITE,
                                "Multiply score when already high", 10));

                // ===== SUPER RARE =====
                SUPER_RARE_CARDS.add(card(201, "Lead Leech", EffectType.LEAD_LEECH,
                                "Player always leads the trick", 18));

                SUPER_RARE_CARDS.add(card(202, "Rank Rampage", EffectType.RANK_RAMPAGE,
                                "Massive rank boost when losing", 18));

                SUPER_RARE_CARDS.add(card(203, "Cascade Capture", EffectType.CASCADE_CAPTURE,
                                "Bonus points on win streak", 18));

                // ===== LEGENDARY =====
                LEGENDARY_CARDS.add(card(301, "Trump Tyrant", EffectType.TRUMP_TYRANT,
                                "Force win if you follow/not follow suit correctly", 25));

                LEGENDARY_CARDS.add(card(302, "Dealer Doom", EffectType.DEALER_DOOM,
                                "Significantly weaken dealer ranks", 30));

                LEGENDARY_CARDS.add(card(303, "Infinite Tricks", EffectType.INFINITE_TRICKS,
                                "Win again and again...", 25));
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

        private static SpecialCard card(int id, String name, EffectType type, String desc, int price) {
                return new SpecialCard(
                                id,
                                name,
                                type,
                                EffectTrigger.ON_ROUND,
                                price,
                                null, // rarity di-set saat spawn
                                desc);
        }
}
