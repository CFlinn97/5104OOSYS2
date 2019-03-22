package PLATFORM;

import CORE.Bid;
import CORE.Auction;
import CORE.Item;
import CORE.User;


import com.mysql.cj.jdbc.MysqlDataSource;

import java.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;


public final class DatabaseAccess {
    private Connection connection;
    private MysqlDataSource dataSource = new MysqlDataSource();



    public DatabaseAccess() {
        dataSource.setUser("5104COMP");
        dataSource.setPassword("HQr$32spd");//Having this in code completely defeats the point of it being a reasonably strong password
        dataSource.setServerName("flinn.dev");
        dataSource.setDatabaseName("testDB");

    }

    public boolean CheckAvailability(String username) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            ps = con.prepareStatement("SELECT ? FROM user");
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (rs.next()) {
                return false;
            } else {
                return true;
            }
        } catch (SQLException ex) {
            System.out.println("Error checking name availability: " + ex.toString());
        } finally {
            CloseQueryItems(con, ps, rs);
        }
        return false;
    }

    public int[] GetSystemStats() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
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
            return counts;
        } catch (SQLException ex) {
            System.out.println("Error getting stats: " + ex.toString());
            return null;
        } finally {
            CloseQueryItems(con, ps, rs);
        }
    }

    private static void CloseQueryItems(Connection con, PreparedStatement ps, ResultSet rs) {
        try { rs.close(); } catch (Exception e) {}
        try { ps.close(); } catch (Exception e) {}
        try { con.close(); } catch (Exception e) {}
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

    public boolean CreateUser(String username, String password){
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            ps = con.prepareStatement("INSERT INTO user(userName, password) VALUES (?,?)");
            ps.setString(1,username);
            ps.setString(2,HashPassword(password));
            rs = ps.executeQuery();
            return true;
        } catch (NoSuchAlgorithmException | SQLException ex) {
            System.out.println("Error creating user: " + ex.toString());
            return false;
        } finally {
            CloseQueryItems(con, ps, rs);
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