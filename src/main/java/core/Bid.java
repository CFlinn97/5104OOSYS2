package core;

import java.time.LocalDateTime;

public class Bid {
    public int getBidID() {
        return bidID;
    }

    public double getAmount() {
        return amount;
    }

    public int getBidUserID() {
        return bidUserID;
    }

    public LocalDateTime getBidDT() {
        return bidDT;
    }

    private int bidID;
    private double amount;
    private int bidUserID;
    private LocalDateTime bidDT;


    public Bid(int bidID, double amount, int bidUserID, LocalDateTime bidDT) {
        this.bidID = bidID;
        this.amount = amount;
        this.bidUserID = bidUserID;
        this.bidDT = bidDT;
    }
}
