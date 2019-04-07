package platform;

import core.Auction;
import core.Bid;
import core.Item;
import com.mysql.cj.jdbc.MysqlDataSource;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;


public final class DatabaseAccess {
    private MysqlDataSource dataSource = new MysqlDataSource();


    public DatabaseAccess() {
        dataSource.setUser("5104COMP");
        dataSource.setPassword("HQr$32spd");//Having this in code completely defeats the point of it being a reasonably strong password
        dataSource.setServerName("flinn.dev");
        dataSource.setDatabaseName("testDB");

    }

    private static void CloseQueryItems(Connection con, PreparedStatement ps, ResultSet rs) {
        try {
            rs.close();
        } catch (Exception e) {
        }
        try {
            ps.close();
        } catch (Exception e) {
        }
        try {
            con.close();
        } catch (Exception e) {
        }
    }

    private static void CloseQueryItems(Connection con, PreparedStatement ps) {
        try {
            ps.close();
        } catch (Exception e) {
        }
        try {
            con.close();
        } catch (Exception e) {
        }
    }

    private static void CloseQueryItems(PreparedStatement ps, ResultSet rs) {
        try {
            rs.close();
        } catch (Exception e) {
        }
        try {
            ps.close();
        } catch (Exception e) {
        }
    }

    private static void CloseQueryItems(PreparedStatement ps) {
        try {
            ps.close();
        } catch (Exception e) {
        }
    }

    public boolean CheckAvailability(String username) throws SQLException {
        Connection con = dataSource.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT * FROM user WHERE userName = ?");
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            CloseQueryItems(con, ps, rs);
            return false;
        } else {
            CloseQueryItems(con, ps, rs);
            return true;
        }

    }

    public int[] GetSystemStats() throws SQLException {
        Connection con = dataSource.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT(SELECT COUNT(*) FROM user) AS c1,(SELECT COUNT(*) FROM auction) AS c2," +
                " (SELECT COUNT(*) FROM auction WHERE status = 'ACTIVE') AS c3, (SELECT COUNT(*) FROM bid) AS c4;");
        ResultSet rs = ps.executeQuery();
        int counts[] = new int[4];
        rs.first();
        counts[0] = rs.getInt("c1");
        counts[1] = rs.getInt("c2");
        counts[2] = rs.getInt("c3");
        counts[3] = rs.getInt("c4");
        CloseQueryItems(con, ps, rs);
        return counts;
    }

    public UserStat GetUserStats(int userID) throws SQLException {
        Connection con = dataSource.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT (SELECT userName FROM user WHERE userID = ?) AS c1, " +
                "(SELECT COUNT(*) FROM auction WHERE userID = ? AND status = 'ACTIVE') AS c2," +
                " (SELECT COUNT(*) FROM bid INNER JOIN auction a ON bid.auctionID = a.auctionID WHERE a.status = 'ACTIVE' AND bid.userID = ?) AS c3");
        ps.setInt(1,userID);
        ps.setInt(2,userID);
        ps.setInt(3,userID);
        ResultSet rs = ps.executeQuery();
        rs.first();
        UserStat userStat = new UserStat(rs.getString("c1"), rs.getInt("c2"), rs.getInt("c3"));//name,open auction,open bid
        CloseQueryItems(con, ps, rs);
        return userStat;
    }

    public class UserStat {
        public UserStat(String username, int auctions, int bid) {
            this.username = username;
            this.auctions = auctions;
            this.bid = bid;
        }
        String username;
        int auctions;
        int bid;
    }


    private String HashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        byte[] hash = digest.digest(password.getBytes());
        String encoded = Base64.getEncoder().encodeToString(hash);
        return encoded;
    }

    public void CreateUser(String username, String password) throws SQLException, NoSuchAlgorithmException {
        Connection con = dataSource.getConnection();
        PreparedStatement ps = con.prepareStatement("INSERT INTO user(userName, password) VALUES (?,?)");
        ps.setString(1, username);
        ps.setString(2, HashPassword(password));
        ps.executeUpdate();
        CloseQueryItems(con, ps);
    }

    public Integer LoginUser(String username, String password) throws SQLException, NoSuchAlgorithmException {
        Connection con = dataSource.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT userID FROM user WHERE userName = ? AND password = ?");
        ps.setString(1, username);
        ps.setString(2, HashPassword(password));
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int rt = rs.getInt(1);
            CloseQueryItems(con, ps, rs);
            return rt;
        } else {
            CloseQueryItems(con, ps, rs);
            return null;
        }

    }

    public List<Item> getUserItems(int userID) throws SQLException{
        List<Item> list = new ArrayList<>();
        Connection con = dataSource.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT * FROM item WHERE userID = ?");
        ps.setInt(1,userID);
        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            list.add(new Item(rs.getInt("itemID"), rs.getString("itemName"),
                    Item.ItemDamage.valueOf(rs.getString("itemCond")),
                    (rs.getString("itemDesc") != null) ? rs.getString("itemDesc") : "NULL"));
        }
        return list;
    }

    public List<Auction> getActiveAuctions() throws SQLException {
        List<Auction> list = new ArrayList<>();
        Connection con = dataSource.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT * FROM auction WHERE status = 'ACTIVE'");
        return fillAuctionInfo(list, con, ps);
    }

    public List<Auction> checkWonAuctions(int userID) throws SQLException {
        List<Auction> list = new ArrayList<>();
        Connection con = dataSource.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT * FROM auction WHERE status = 'SOLD' AND userID = ? AND winnerNotified = 0");
        ps.setInt(1, userID);
        return fillAuctionInfo(list, con, ps);
    }

    public void markWinnerNotified(Auction auction) throws SQLException {
        Connection con = dataSource.getConnection();
        PreparedStatement ps = con.prepareStatement("UPDATE auction SET winnerNotified = 1 WHERE auctionID = ?");
        ps.setInt(1, auction.getAuctionID());
        ps.executeUpdate();
    }

    private List<Auction> fillAuctionInfo(List<Auction> list, Connection con, PreparedStatement ps) throws SQLException {
        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            int id = rs.getInt("auctionID");
            int user = rs.getInt("userID");
            Auction.AuctionState state = Auction.AuctionState.valueOf(rs.getString("status"));
            List<Bid> bids = new ArrayList<>();
            PreparedStatement bps = con.prepareStatement("SELECT * FROM bid WHERE auctionID = ?");
            bps.setInt(1,id);
            ResultSet brs = bps.executeQuery();
            while(brs.next()) {
                int b_id = brs.getInt("bidID");
                double amount = brs.getDouble("bidAmount");
                int b_user = brs.getInt("userID");
                Date time = brs.getDate("bidDT");
                bids.add(new Bid(b_id, amount, b_user, time));
            }
            CloseQueryItems(bps, brs);
            Item item;
            int itemID = rs.getInt("itemID");
            PreparedStatement ips = con.prepareStatement("SELECT * FROM item WHERE itemID = ?");
            ips.setInt(1,itemID);
            ResultSet irs = ips.executeQuery();
            if(irs.next()) {
                int i_id = irs.getInt("itemID");
                String desc = irs.getString("itemDesc");
                String name = irs.getString("itemName");
                Item.ItemDamage dmg = Item.ItemDamage.valueOf(irs.getString("itemCond"));
                item = new Item(i_id, name, dmg, desc);
            } else {
                throw new SQLException("Item not found");
            }
            double startP = rs.getDouble("startPrice");
            double resP = rs.getDouble("reservePrice");
            Timestamp ts = rs.getTimestamp("closeDate");
            Date close = ts;
            list.add(new Auction(id, user, state, item, bids, startP, resP, close));
        }
        CloseQueryItems(con, ps, rs);
        return list;
    }

    public void createItem(String name, String desc, Item.ItemDamage dmg, int userID) throws SQLException {
        Connection con = dataSource.getConnection();
        PreparedStatement ps = con.prepareStatement("INSERT INTO item(userID, itemName, itemDesc, itemCond) VALUES(?,?,?,?)");
        ps.setInt(1,userID);
        ps.setString(2, name);
        ps.setString(3, desc);
        ps.setString(4, dmg.toString());
        ps.executeUpdate();
        CloseQueryItems(con, ps);
    }

    public void createAuction(int userID, double startPrice, double reservePrice, int length, int itemID) throws SQLException {
        Connection con = dataSource.getConnection();
        PreparedStatement ps = con.prepareStatement("INSERT INTO auction(userID, itemID, startPrice, reservePrice, closeDate, status)" +
                " VALUES(?,?,?,?,DATE_ADD(NOW(), INTERVAL " + length +" DAY), ?)");
        ps.setInt(1, userID);
        ps.setInt(2,itemID);
        ps.setDouble(3, startPrice);
        ps.setDouble(4, reservePrice);
        ps.setString(5, "ACTIVE");
        ps.executeUpdate();
        CloseQueryItems(con, ps);
    }

    public void placeBid(int auctionID,int userID, double bid) throws SQLException{
        Connection con = dataSource.getConnection();
        PreparedStatement ps = con.prepareStatement("INSERT INTO bid(userID, auctionID, bidAmount) VALUES(?,?,?)");
        ps.setInt(1, userID);
        ps.setInt(2, auctionID);
        ps.setDouble(3, bid);
        ps.executeUpdate();
        CloseQueryItems(con, ps);
    }

    public void updateDatabaseInfo() throws SQLException{
        List<Auction> auctions = getActiveAuctions();
        Connection con = dataSource.getConnection();
        Date date = Date.from(Instant.now());

        for(Auction a : auctions) {
            if(date.toInstant().isAfter(a.getCloseDate().toInstant())) {
                System.out.println("DB RECORD UPDATE");
                double highbid = 0;
                for(Bid b : a.getAuctionBids()) {
                    if (b.getAmount() > highbid) {
                        highbid = b.getAmount();
                    }
                }
                Auction.AuctionState state;
                int notify;//0 = tell user, 1 = dont
                if(highbid > a.getReservePrice()) {
                    state = Auction.AuctionState.SOLD;
                    notify = 0;
                } else {
                    state = Auction.AuctionState.FAILED;
                    notify = 1;
                }
                PreparedStatement ps = con.prepareStatement("UPDATE auction SET status = ?, winnerNotified = ? WHERE auctionID = ?");
                ps.setString(1,state.toString());
                ps.setInt(2, notify);
                ps.setInt(3, a.getAuctionID());
                ps.executeUpdate();
                CloseQueryItems(ps);
            }
        }
        con.close();
    }

    public Auction getAuction(int auctionID) throws SQLException{
        List<Auction> list = new ArrayList<>();
        Connection con = dataSource.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT * FROM auction WHERE auctionID = ?");
        ps.setInt(1,auctionID);
        return fillAuctionInfo(list, con, ps).get(0);
    }
}