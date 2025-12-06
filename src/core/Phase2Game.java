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

    public void rollBiddingItem(){
        biddingItem = shop.rollBiddingItem(gameState.getRound());
    }

    public BiddingResult bid(int playerBid) throws GameException {
        if (biddingItem == null){
            throw new GameException("No bidding item");
        }

        if (playerBid <= 0){
            throw new GameException("Invalid bid");
        }

        if (playerBid > gameState.getMoney()) {
            throw new GameException("Not enough money");
        }

        int dealerBid = gameState.getCurrentDealer().bid(biddingItem, gameState.getRound());
        
        BiddingResult result;

        if (playerBid == dealerBid){
            result = BiddingResult.tie(dealerBid);
        } else if (playerBid > dealerBid){
            gameState.addInventory(biddingItem);
            gameState.setMoney(gameState.getMoney() - playerBid);
            result = BiddingResult.win(playerBid, dealerBid);
        } else {
            result = BiddingResult.lose(playerBid, dealerBid);
        }

        biddingItem = null;
        return result;
    }

    public List<SpecialCard> getShopItems() {
        return shopItems;
    }

    public SpecialCard getBiddingItem() {
        return biddingItem;
    }
}
