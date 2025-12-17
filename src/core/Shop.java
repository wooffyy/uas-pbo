package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import model.card.Rarity;
import model.card.SpecialCard;

// Logic Shop di phase 2
public class Shop {

    private final List<SpecialCard> itemPool;
    private final Random random;

    public Shop(List<SpecialCard> itemPool, long seed) {
        this.itemPool = new ArrayList<>(itemPool);
        this.random = new Random(seed);
    }

    public List<SpecialCard> rollShopItems(int count, int round) {
        Collections.shuffle(itemPool, random);

        List<SpecialCard> items = new ArrayList<>();

        for (int i = 0; i < count && i < itemPool.size(); i++) {
            SpecialCard base = itemPool.get(random.nextInt(itemPool.size()));
            SpecialCard copy = new SpecialCard(base);
            copy.setPrice(scalePrice(base.getPrice(), round));
            items.add(copy);
        }

        return items;
    }

    public List<SpecialCard> rollBiddingOptions(int round) {
        List<SpecialCard> options = new ArrayList<>();

        // Helper to roll specific rarity
        options.add(rollByRarity(Rarity.RARE, round));
        options.add(rollByRarity(Rarity.SUPER_RARE, round));
        options.add(rollByRarity(Rarity.LEGENDARY, round));

        return options;
    }

    private SpecialCard rollByRarity(Rarity rarity, int round) {
        List<SpecialCard> pool = itemPool.stream()
                .filter(c -> c.getRarity() == rarity)
                .collect(Collectors.toList());

        if (pool.isEmpty()) {
            // Fallback: pick any and force rarity (or just return random)
            // Ideally pools shouldn't be empty.
            SpecialCard fallback = itemPool.get(random.nextInt(itemPool.size()));
            SpecialCard copy = new SpecialCard(fallback);
            copy.setPrice(20); // Base price fixed at 20 as per request
            return copy;
        }

        SpecialCard base = pool.get(random.nextInt(pool.size()));
        SpecialCard copy = new SpecialCard(base);
        // User request: "harga dasar tiap kartu adalah $20" regarding the bidding base
        // price
        copy.setPrice(20);
        return copy;
    }

    // Kept for compatibility if needed, but not used in new flow
    public SpecialCard rollBiddingItem(int round) {
        return rollByRarity(Rarity.RARE, round);
    }

    private int scalePrice(int basePrice, int round) {
        return (int) (basePrice * (1 + 0.1 * (round - 1)));
    }
}
