package core;

import java.util.List;
import model.card.SpecialCard;
import model.entity.dealer.Dealer;
import model.state.GameState;

// Logic Bidding dan Shop
public class Phase2Game {
    private final GameState gameState;
    private final Shop shop;
    private List<SpecialCard> shopItems;
    private List<SpecialCard> biddingOptions;

    // State for the current auction
    private SpecialCard currentBiddingItem;
    private int currentPlayerBid;
    private int currentDealerBid;

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
        gameState.decreaseMoney(item.getPrice());
        shopItems.remove(item);
        return true;
    }

    public void rollBiddingItem() {
        biddingOptions = shop.rollBiddingOptions(gameState.getRound());
        currentBiddingItem = null; // Wait for selection
    }

    public void selectBiddingItem(SpecialCard item) {
        this.currentBiddingItem = item;
        // Reset auction state whenever a new item is selected
        this.currentPlayerBid = 0;
        this.currentDealerBid = 0;
    }

    public BiddingResult placePlayerBid(int newPlayerBid) throws GameException {
        if (currentBiddingItem == null) {
            throw new GameException("No bidding item selected");
        }
        if (currentDealerBid > 0 && newPlayerBid <= currentDealerBid) {
            throw new GameException("You must bid higher than the dealer's last bid of $" + currentDealerBid);
        }
        if (newPlayerBid > gameState.getMoney()) {
            throw new GameException("You cannot afford this bid.");
        }

        Dealer dealer = gameState.getCurrentDealer();
        int dealerMaxBid = dealer.getMaxBid();

        // Player auto-wins if their bid meets or exceeds the dealer's absolute max
        if (newPlayerBid >= dealerMaxBid) {
            this.currentPlayerBid = newPlayerBid;
            return BiddingResult.win(this.currentPlayerBid, this.currentDealerBid); // Player wins outright
        }

        // Dealer makes a counter-offer
        int dealerCounterBid = dealer.bid(currentBiddingItem, gameState.getRound(), newPlayerBid);

        // If dealer's counter is less than or equal to player's bid, player wins
        // (dealer reached their limit or logic decided not to bid higher)
        if (dealerCounterBid <= newPlayerBid) {
            this.currentPlayerBid = newPlayerBid;
            this.currentDealerBid = dealerCounterBid;
            return BiddingResult.win(this.currentPlayerBid, this.currentDealerBid);
        }

        // Auction continues, return the new state
        this.currentPlayerBid = newPlayerBid;
        this.currentDealerBid = dealerCounterBid;
        return BiddingResult.ongoing(this.currentPlayerBid, this.currentDealerBid);
    }

    public BiddingResult playerSkips() {
        // Player skips, they lose the auction.
        return BiddingResult.lose(this.currentPlayerBid, this.currentDealerBid);
    }


    public List<SpecialCard> getShopItems() {
        return shopItems;
    }

    public List<SpecialCard> getBiddingOptions() {
        return biddingOptions;
    }

    public SpecialCard getBiddingItem() {
        return currentBiddingItem;
    }

    public int getDealerBid() {
        return this.currentDealerBid;
    }
}
