package core;

import java.util.List;

public class Auction {
    public enum AuctionState {
    ACTIVE,
    FAILED,
    SOLD,
    CLOSED
}

    public Auction(int auctionID, int userID, AuctionState auctionState, List<Bid> auctionBids) {
        this.auctionID = auctionID;
        this.userID = userID;
        this.auctionState = auctionState;
        this.auctionBids = auctionBids;
    }

    private int auctionID;
    private int userID;
    private AuctionState auctionState;
    private List<Bid> auctionBids;

    public int getAuctionID() {
        return auctionID;
    }

    public int getUserID() { return userID; }

    public AuctionState getAuctionState() {
        return auctionState;
    }

    public List<Bid> getAuctionBids() {
        return auctionBids;
    }

    public void PlaceBid() {

    }

    public void Close() {

    }
}
