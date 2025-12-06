package core;

public class BiddingResult {
    private final int playerBid;
    private final int dealerBid;
    private final boolean playerWin;
    private final boolean isTie;

    public BiddingResult(int playerBid, int dealerBid, boolean tie, boolean playerWin) {
        this.playerBid = playerBid;
        this.dealerBid = dealerBid;
        this.isTie = tie;
        this.playerWin = playerWin;
    }

    public static BiddingResult tie(int dealerBid){
        return new BiddingResult(0, dealerBid, true, false);
    }

    public static BiddingResult win(int playerBid, int dealerBid){
        return new BiddingResult(playerBid, dealerBid, false, true);
    }

    public static BiddingResult lose(int playerBid, int dealerBid){
        return new BiddingResult(playerBid, dealerBid, false, false);
    }

    public int getPlayerBid() {
        return playerBid;
    }

    public int getDealerBid() {
        return dealerBid;
    }

    public boolean isTie() {
        return isTie;
    }

    public boolean isPlayerWin() {
        return playerWin;
    }
}
