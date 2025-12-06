package core;
import model.card.Card;

public class TrickResult {
    private final boolean playerWin;
    private final Card playerCard;
    private final Card dealerCard;
    
    public TrickResult(boolean playerWin, Card playerCard, Card dealerCard) {
        this.playerWin = playerWin;
        this.playerCard = playerCard;
        this.dealerCard = dealerCard;
    }

    public boolean isPlayerWin() {
        return playerWin;
    }

    public Card getPlayerCard() {
        return playerCard;
    }

    public Card getDealerCard() {
        return dealerCard;
    }
}
