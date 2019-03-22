package PLATFORM;

import CORE.Auction;
import CORE.Bid;
import CORE.Item;
import CORE.User;
import com.mysql.cj.jdbc.MysqlDataSource;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;


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

    public boolean CheckAvailability(String username) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        con = dataSource.getConnection();
        ps = con.prepareStatement("SELECT ? FROM user");
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
        return  new UserStat(rs.getString("c1"), rs.getInt("c2"), rs.getInt("c3"));
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

    public ResultSet InsertRecord(Bid bid) {
        return null;
    }

    public ResultSet InsertRecord(Item item) {
        return null;
    }

    public ResultSet InsertRecord(Auction auction) {
        return null;
    }

    private String HashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        byte[] hash = digest.digest(password.getBytes());
        String encoded = Base64.getEncoder().encodeToString(hash);
        return encoded;
    }

    public boolean CreateUser(String username, String password) throws SQLException, NoSuchAlgorithmException {
        Connection con = null;
        PreparedStatement ps = null;
        Integer executeReturn = null;

        con = dataSource.getConnection();
        ps = con.prepareStatement("INSERT INTO user(userName, password) VALUES (?,?)");
        ps.setString(1, username);
        ps.setString(2, HashPassword(password));
        executeReturn = ps.executeUpdate();
        CloseQueryItems(con, ps);
        if (executeReturn == 1) {
            return true;
        } else {
            return false;
        }

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


    public ResultSet UpdateRecord(Bid bid) {
        return null;
    }

    public ResultSet UpdateRecord(Item item) {
        return null;
    }

    public ResultSet UpdateRecord(Auction auction) {
        return null;
    }

    public ResultSet UpdateRecord(User user) {
        return null;
    }


}