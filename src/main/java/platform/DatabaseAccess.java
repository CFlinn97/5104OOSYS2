package platform;

import core.Auction;
import core.Bid;
import core.Item;
import com.mysql.cj.jdbc.MysqlDataSource;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


public final class DatabaseAccess {
    private Connection connection;
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

    private static void CloseQueryItems(PreparedStatement ps) {
        try {
            ps.close();
        } catch (Exception e) {
        }
    }

    public boolean CheckAvailability(String username) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        con = dataSource.getConnection();
        ps = con.prepareStatement("SELECT * FROM user WHERE userName = ?");
        ps.setString(1, username);
        rs = ps.executeQuery();
        if (rs.next()) {
            CloseQueryItems(con, ps, rs);
            return false;
        } else {
            CloseQueryItems(con, ps, rs);
            return true;
        }

    }

    public int[] GetSystemStats() throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        con = dataSource.getConnection();
        ps = con.prepareStatement("SELECT(SELECT COUNT(*) FROM user) AS c1,(SELECT COUNT(*) FROM auction) AS c2," +
                " (SELECT COUNT(*) FROM auction WHERE status = 'ACTIVE') AS c3, (SELECT COUNT(*) FROM bid) AS c4;");
        rs = ps.executeQuery();
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
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        con = dataSource.getConnection();
        ps = con.prepareStatement("SELECT(SELECT userName FROM user WHERE userID = ?) as c1, (SELECT COUNT(*) FROM auction WHERE userID = ?) as c2," +
                "(SELECT COUNT(*) FROM bid INNER JOIN bid_auction ba on bid.bidID = ba.bidID INNER JOIN auction a on" +
                " ba.auctionID = a.auctionID AND status = 'ACTIVE' INNER JOIN user_bid ub on bid.bidID = ub.bidID AND ub.userID = ?) as c3;");
        ps.setInt(1,userID);
        ps.setInt(2,userID);
        ps.setInt(3,userID);
        rs = ps.executeQuery();
        rs.first();
        UserStat userStat = new UserStat(rs.getString("c1"), rs.getInt("c2"), rs.getInt("c3"));

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
        Connection con = null;
        PreparedStatement ps = null;

        con = dataSource.getConnection();
        ps = con.prepareStatement("INSERT INTO user(userName, password) VALUES (?,?)");
        ps.setString(1, username);
        ps.setString(2, HashPassword(password));
        ps.executeUpdate();
        CloseQueryItems(con, ps);
    }

    public Integer LoginUser(String username, String password) throws SQLException, NoSuchAlgorithmException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        con = dataSource.getConnection();
        ps = con.prepareStatement("SELECT userID FROM user WHERE userName = ? AND password = ?");
        ps.setString(1, username);
        ps.setString(2, HashPassword(password));
        rs = ps.executeQuery();
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
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Item> list = new ArrayList<>();
        con = dataSource.getConnection();
        ps = con.prepareStatement("SELECT * FROM item INNER JOIN user_item ui on item.itemID = ui.itemID AND userID = ?");
        ps.setInt(1,userID);
        rs = ps.executeQuery();
        while(rs.next()) {
            list.add(new Item(rs.getInt("itemID"), rs.getString("itemName"),
                    Item.ItemDamage.valueOf(rs.getString("itemCond")),
                    (rs.getString("itemDesc") != null) ? rs.getString("itemDesc") : "NULL"));
        }
        return list;
    }

    public List<Auction> getActiveAuctions() throws SQLException {
        PreparedStatement ps = null;
        Connection con = null;
        ResultSet rs = null;
        List<Auction> list = new ArrayList<>();
        con = dataSource.getConnection();
        ps = con.prepareStatement("SELECT * FROM auction");
        rs = ps.executeQuery();
        while(rs.next()) {
            PreparedStatement ips = null;
            ResultSet irs = null;
            List<Bid> bids = new ArrayList<>();
            ips = con.prepareStatement("SELECT * FROM bid INNER JOIN bid_auction ba on bid.bidID = ba.bidID AND ba.auctionID = ?");
            ips.setInt(1,rs.getInt("auctionID"));
            irs = ips.executeQuery();
            while(irs.next()) {
                jps = con.prepareStatement("SELECT userID")
                bids.add(new Bid(dIDbi, amount, bidUserID, bidDT))
            }
        }
        return list;
    }

    public void createItem(String name, String desc, Item.ItemDamage dmg, int userID) throws SQLException {//TODO Fix Race Condition
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        con = dataSource.getConnection();
        ps = con.prepareStatement("INSERT INTO item(itemName, itemDesc, itemCond) VALUES(?,?,?)");
        ps.setString(1, name);
        ps.setString(2, desc);
        ps.setString(3, dmg.toString());
        ps.executeUpdate();
        CloseQueryItems(ps);
        ps = null;
        ps = con.prepareStatement("SELECT LAST_INSERT_ID()");
        rs = ps.executeQuery();
        int itemID;
        if (rs.next()) {
            itemID = rs.getInt(1);
        } else {
            throw new SQLException("Cannot get inserted itemID");
        }
        CloseQueryItems(ps);
        ps = null;
        ps = con.prepareStatement("INSERT INTO user_item(userID, itemID) VALUES (?,?)");
        ps.setInt(1,userID);
        ps.setInt(2,itemID);
        ps.executeUpdate();
        CloseQueryItems(con, ps);
    }

    public void createAuction(int userID, double startPrice, double reservePrice, int length, int itemID) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        con = dataSource.getConnection();
        ps = con.prepareStatement("INSERT INTO auction(userID, startPrice, reservePrice, closeDate, status)" +
                " VALUES(?,?,?,DATE_ADD(NOW(), INTERVAL " + length +" DAY), ?)");
        ps.setInt(1, userID);
        ps.setDouble(2, startPrice);
        ps.setDouble(3, reservePrice);
        ps.setString(4, "ACTIVE");
        ps.executeUpdate();
        CloseQueryItems(ps);
        ps = null;
        ps = con.prepareStatement("SELECT LAST_INSERT_ID()");
        int lastID = 0;
        rs = ps.executeQuery();
        if (rs.next()) {
            lastID = rs.getInt(1);
        }
        CloseQueryItems(ps);
        ps = null;
        ps = con.prepareStatement("INSERT INTO auction_item(auctionID, itemID) VALUES (?,?)");
        ps.setInt(1, lastID);
        ps.setInt(2, itemID);
        ps.executeUpdate();
        CloseQueryItems(con,ps);
    }
}