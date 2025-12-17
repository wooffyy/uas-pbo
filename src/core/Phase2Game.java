package core;

import java.util.List;
import model.card.SpecialCard;
import model.state.GameState;

// Logic Bidding dan Shop 
public class Phase2Game {
    private final GameState gameState;
    private final Shop shop;
    private List<SpecialCard> shopItems;
    private SpecialCard biddingItem;
    private List<SpecialCard> biddingOptions;

    public Phase2Game(GameState gameState, Shop shop) {
        this.gameState = gameState;
        this.shop = shop;
    }

    public void loadShop() {
        shopItems = shop.rollShopItems(3, gameState.getRound());
    }

    public boolean buyItem(SpecialCard item) throws GameException {
        if (!shopItems.contains(item)) {
            throw new GameException("Item not in shop");
        }

        if (gameState.getMoney() < item.getPrice()) {
            throw new GameException("Not enough money");
        }

        gameState.addInventory(item);
        gameState.setMoney(gameState.getMoney() - item.getPrice());
        shopItems.remove(item);
        return true;
    }

    public void rollBiddingItem() {
        biddingOptions = shop.rollBiddingOptions(gameState.getRound());
        biddingItem = null; // Wait for selection
    }

    public void selectBiddingItem(SpecialCard item) {
        this.biddingItem = item;
    }

    public BiddingResult bid(int playerBid) throws GameException {
        if (biddingItem == null) {
            throw new GameException("No bidding item selected");
        }

        if (playerBid <= 0) {
            throw new GameException("Invalid bid");
        }

        int dealerBid = gameState.getCurrentDealer().bid(biddingItem, gameState.getRound());

        // User request: overlay "uang anda kurang" if money < dealerBid?
        // Logic: Compare bids. If Player > Dealer, then check if Player handles money.
        // Actually, bid logic implies: Player says $100. Dealer says $120. Player
        // loses.
        // Or: Player says $130. Dealer says $120. Player wins. THEN check if Player has
        // $130.

        BiddingResult result;

        if (playerBid == dealerBid) {
            result = BiddingResult.tie(dealerBid);
        } else if (playerBid > dealerBid) {
            // If player wins the bid, they MUST pay.
            if (playerBid > gameState.getMoney()) {
                // Return a special exception or status that UI can handle to show overlay
                // Treating "Not enough money" as a fail condition here.
                throw new GameException("NOT_ENOUGH_MONEY_FOR_BID");
            }
            gameState.addInventory(biddingItem);
            gameState.setMoney(gameState.getMoney() - playerBid);
            result = BiddingResult.win(playerBid, dealerBid);
        } else {
            result = BiddingResult.lose(playerBid, dealerBid);
        }

        // Do not clear biddingItem immediately if we want to show result UI
        return result;
    }

    public List<SpecialCard> getShopItems() {
        return shopItems;
    }

    public List<SpecialCard> getBiddingOptions() {
        return biddingOptions;
    }

    public SpecialCard getBiddingItem() {
        return biddingItem;
    }
}
