package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
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

        for (int i = 0; i < count; i++) {
            SpecialCard base = itemPool.get(random.nextInt(itemPool.size()));
            SpecialCard copy = base.clone();
            copy.setPrice(scalePrice(base.getBasePrice(), round));
            items.add(copy);
        }

        return items;
    }

    public SpecialCard rollBiddingItem(int round) {
        List<SpecialCard> rares = itemPool.stream()
            .filter(c -> c.getRarity() == Rarity.RARE)
            .toList();

        SpecialCard base = rares.get(random.nextInt(rares.size()));
        SpecialCard copy = base.clone();
        copy.setPrice(scalePrice(base.getBasePrice(), round));
        return copy;
    }

    // belum tentu dipakai
    private int scalePrice(int basePrice, int round) {
        return (int)(basePrice * (1 + 0.1 * (round - 1)));
    }
}

