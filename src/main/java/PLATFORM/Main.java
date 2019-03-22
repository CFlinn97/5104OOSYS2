package PLATFORM;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {
        AuctionSystem auction = new AuctionSystem();
        auction.begin();
    }

}
