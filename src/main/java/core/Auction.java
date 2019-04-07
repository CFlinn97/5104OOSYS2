package core;

import java.util.Date;
import java.util.List;

public class Auction {
    private static double minBidDifference = 2.00;
    private int auctionID;
    private int userID;
    private AuctionState auctionState;
    private Item auctionItem;
    private List<Bid> auctionBids;
    private double startPrice;
    private double reservePrice;
    private Date closeDate;
    public Auction(int auctionID, int userID, AuctionState auctionState, Item auctionItem, List<Bid> auctionBids,
                   double startPrice, double reservePrice, Date closeDate) {
        this.auctionID = auctionID;
        this.userID = userID;
        this.auctionState = auctionState;
        this.auctionItem = auctionItem;
        this.auctionBids = auctionBids;
        this.startPrice = startPrice;
        this.reservePrice = reservePrice;
        this.closeDate = closeDate;
    }

    //TODO Close Auction
    //TODO Block Auction
    public double getStartPrice() {
        return startPrice;
    }

    public double getReservePrice() {
        return reservePrice;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public int getAuctionID() {
        return auctionID;
    }

    public List<Bid> getAuctionBids() {
        return auctionBids;
    }

    public Item getAuctionItem() {
        return auctionItem;
    }

    public Bid getHighestBid() {
        Bid high = (auctionBids.size() == 0) ? null : auctionBids.get(0);
        for (Bid b : auctionBids) {
            if (b.getAmount() < high.getAmount()) {
                high = b;
            }
        }
        return high;
    }

    public double getLowestPossibleBid() {
        Bid bid = getHighestBid();
        if (bid == null) {
            return startPrice + minBidDifference;
        } else {
            return bid.getAmount() + minBidDifference;
        }

    }

    public enum AuctionState {
        ACTIVE,
        FAILED,
        SOLD,
        CLOSED
    }

}
