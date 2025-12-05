package core;

import java.util.List;
import model.card.SpecialCard;
import model.state.GameState;

// Logic Bidding dan Shop 
public class Phase2Game {
    private final GameState gameState;
    private List<SpecialCard> shopItems;
    private SpecialCard biddingItem;

    public Phase2Game(GameState gameState) {
        this.gameState = gameState;
    }
    
    public void loadShop() {
        shopItems = gameState.getShop().rollShopItems(gameState.getRound());
    }

    public boolean buyItem(SpecialCard item) throws GameException {
        if (gameState.getMoney() < item.getPrice()) {
            throw new Exception("Not enough money");
        }

        gameState.addInventory(item);
        gameState.setMoney(gameState.getMoney() - item.getPrice());
        return true;
    }

    public void rollBiddingItem(){
        biddingItem = gameState.getShop().rollBiddingItem();
    }

    public BiddingResult bid(int playerBid) throws GameException {
        if (playerBid > gameState.getMoney()) {
            throw new Exception("Not enough money");
        }

        int dealerBid = gameState.getCurrentDealer().bid(biddingItem, gameState.getRound());

        if (playerBid == dealerBid){
            return BiddingResult.tie(dealerBid);
        }

        if (playerBid > dealerBid){
            gameState.addInventory(biddingItem);
            gameState.setMoney(gameState.getMoney() - playerBid);
            return BiddingResult.win(playerBid, dealerBid);
        } else {
            return BiddingResult.lose(playerBid, dealerBid);
        }
    }
}
