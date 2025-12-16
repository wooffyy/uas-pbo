package core;
import model.card.Card;

public class TrickResult {
    private final boolean playerWin;
    private final Card playerCard;
    private final Card dealerCard;
    private final boolean retrigger;
    
    public TrickResult(boolean playerWin, Card playerCard, Card dealerCard, boolean retrigger) {
        this.playerWin = playerWin;
        this.playerCard = playerCard;
        this.dealerCard = dealerCard;
        this.retrigger = retrigger;
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

    public boolean isRetrigger() {
        return retrigger;
    }
}
