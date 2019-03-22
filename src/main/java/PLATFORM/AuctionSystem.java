package PLATFORM;

import CORE.Auction;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class AuctionSystem {
    private final Scanner scanner = new Scanner(System.in);
    private final DatabaseAccess databaseAccess;
    List<Auction> auctionList = null; //Used to store last updated auction information
    private Integer userID = null;

    public AuctionSystem() {
        databaseAccess = new DatabaseAccess();
    }

    public void begin() {
        Integer input = null;
        try {
            int[] counts = databaseAccess.GetSystemStats();
            System.out.printf("Welcome to the Auction System. \n " +
                            "Stats: Users %d, Total Auctions %d, Active Auctions %d, Bids %d",
                    counts[0], counts[1], counts[2], counts[3]);
        } catch (SQLException e) {
            System.out.println("Error: " + e.toString());
            return;
        }

        do {
            if (userID == null) {
                System.out.println("Please select an option:");
                System.out.println("[1] Create Account");
                System.out.println("[2] Login");
                System.out.println("[3] Browse Auctions");
                System.out.println("[0] Exit");
                try {
                    input = scanner.nextInt();
                    if (scanner.hasNextLine()) {
                        scanner.nextLine();
                    }
                    switch (input) {
                        case 1:
                            CreateAccount();
                            break;
                        case 2:
                            Login();
                            break;
                        case 3:
                            BrowseAuction();
                            break;
                        default:
                            System.out.println("test");
                    }
                } catch (InputMismatchException e) {
                    if (scanner.hasNextLine()) {
                        scanner.nextLine();
                    }
                    System.out.println("That is not a valid input");
                }
            } else {
                try {
                    DatabaseAccess.UserStat userStat = databaseAccess.GetUserStats();
                    System.out.printf("Logged in as %s. You have %d Auctions active and %d Bids open", userStat.username,
                            userStat.auctions, userStat.bid);
                }catch (SQLException e) {
                    System.out.println("Error: " + e.toString());
                    return;
                }

                System.out.println("Please select an option:");
                System.out.println("[1] Create Auction");
                System.out.println("[2] Browse Auctions");
                System.out.println("[0] Exit");
                try {
                    input = scanner.nextInt();
                    if (scanner.hasNextLine()) {
                        scanner.nextLine();
                    }
                    switch (input) {
                        case 1:
                            CreateAuction();
                            break;
                        case 2:
                            BrowseAuction();
                            break;
                        default:
                            System.out.println("test");
                    }
                } catch (InputMismatchException e) {
                    if (scanner.hasNextLine()) {
                        scanner.nextLine();
                    }
                    System.out.println("That is not a valid input");
                }
            }
        }while(input != 0);
    }

    //TODO Allow exit to menu
    public void CreateAccount() {
        int complete = 0;
        do {
            System.out.println("Please enter your chosen username");
            String username = scanner.nextLine();
            System.out.println(username);
            try {
                if (!databaseAccess.CheckAvailability(username)) {
                    System.out.println("That username is already taken, please try another");
                    continue;
                }
            } catch (SQLException e) {
                System.out.println("Error: " + e.toString());
                continue;
            }

            System.out.println("Please enter your chosen password");
            String password = scanner.nextLine();
            try {
                if (databaseAccess.CreateUser(username, password)) {
                    System.out.println("Account successfully created! Please login.");
                    complete = 1;
                } else {
                    System.out.println("Could not create user, please try again");
                }
            } catch (NoSuchAlgorithmException | SQLException e) {//TODO Maybe change to catching generic exceptions instead of specific ones, while we done make use of specificity anyway
                System.out.println("Error: " + e.toString());
                continue;
            }

        } while (complete != 1);
    }

    public void Login() {
       Login: do {
            System.out.println("Please enter username");
            String username = scanner.nextLine();
            System.out.println("Please enter your password");
            String password = scanner.nextLine();
            try {
                userID = databaseAccess.LoginUser(username, password);
            }catch (NoSuchAlgorithmException | SQLException e) {
                System.out.println("Error: " + e.toString());
                continue;
            }
            if (userID == null) {
                System.out.println("Username or password incorrect. Please choose and option:");
                System.out.println("[1] Retry");
                System.out.println("[0] Return");
                Integer input = scanner.nextInt();
                if (scanner.hasNextLine()) {
                    scanner.nextLine();
                }
                switch (input) {
                    case 1:
                        continue;
                    case 0:
                        break Login;
                    default:
                        break Login;
                }
            }
        } while (userID == null);

    }

    public void CreateAuction() {

    }

    public void BrowseAuction() {

    }

}
