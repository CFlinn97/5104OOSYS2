package core;

import java.util.List;

public class Auction {
    public enum AuctionState {
    ACTIVE,
    FAILED,
    SOLD,
    CLOSED
}

    public Auction(int auctionID, AuctionState auctionState, List<Bid> auctionBids, boolean blocked) {
        this.auctionID = auctionID;
        this.auctionState = auctionState;
        this.auctionBids = auctionBids;
        this.blocked = blocked;
    }

    private int auctionID;
    private AuctionState auctionState;
    private List<Bid> auctionBids;
    private boolean blocked;

    public int getAuctionID() {
        return auctionID;
    }

    public AuctionState getAuctionState() {
        return auctionState;
    }

    public List<Bid> getAuctionBids() {
        return auctionBids;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void PlaceBid() {

    }

    public void Close() {

    }
}
