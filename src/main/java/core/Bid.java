package core;


import java.util.Date;

public class Bid {
    private int bidID;
    private double amount;
    private int bidUserID;
    private Date bidDT;

    public Bid(int bidID, double amount, int bidUserID, Date bidDT) {
        this.bidID = bidID;
        this.amount = amount;
        this.bidUserID = bidUserID;
        this.bidDT = bidDT;
    }

    public int getBidID() {
        return bidID;
    }

    public double getAmount() {
        return amount;
    }

    public int getBidUserID() {
        return bidUserID;
    }

    public Date getBidDT() {
        return bidDT;
    }
}
