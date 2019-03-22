package CORE;

import java.util.List;

public class Auction {

    private int auctionID;

    enum AuctionState {
        PENDING,
        ACTIVE,
        FAILED,
        SOLD,
        CLOSED
    }
    AuctionState auctionState = null;//temp

    private List<Bid> auctionBids;


    public void PlaceBid() {

    }

    public void Verify() {

    }

    public void Close() {

    }

    public Boolean IsBlocked() {
     return true;
    }

    public void SetBlocked() {

    }
}
