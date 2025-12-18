package core;

public class BiddingResult {

    public enum Status {
        WIN,
        LOSE,
        TIE,
        ONGOING
    }

    private final int playerBid;
    private final int dealerBid;
    private final Status status;

    public BiddingResult(int playerBid, int dealerBid, Status status) {
        this.playerBid = playerBid;
        this.dealerBid = dealerBid;
        this.status = status;
    }

    public static BiddingResult tie(int dealerBid){
        return new BiddingResult(0, dealerBid, Status.TIE);
    }

    public static BiddingResult win(int playerBid, int dealerBid){
        return new BiddingResult(playerBid, dealerBid, Status.WIN);
    }

    public static BiddingResult lose(int playerBid, int dealerBid){
        return new BiddingResult(playerBid, dealerBid, Status.LOSE);
    }

    public static BiddingResult ongoing(int playerBid, int dealerBid) {
        return new BiddingResult(playerBid, dealerBid, Status.ONGOING);
    }

    public int getPlayerBid() {
        return playerBid;
    }

    public int getDealerBid() {
        return dealerBid;
    }

    public Status getStatus() {
        return status;
    }

    public boolean isPlayerWin() {
        return status == Status.WIN;
    }
}
