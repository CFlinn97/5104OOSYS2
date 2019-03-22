package PLATFORM;

import CORE.Auction;
import CORE.User;

import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class AuctionSystem {
    private Integer userID = null;
    private final Scanner scanner = new Scanner(System.in);
    private final DatabaseAccess databaseAccess;

    List<Auction> auctionList = null; //Used to store last updated auction information

    public AuctionSystem() throws SQLException {
        databaseAccess = new DatabaseAccess();
    }

    public void begin() throws SQLException {
        Integer input = null;
        int[] counts = databaseAccess.GetSystemStats();
        System.out.printf("Welcome to the Auction System. \n " +
                        "Stats: Users %d, Total Auctions %d, Active Auctions %d, Bids %d",
                counts[0], counts[1], counts[2], counts[3]);

        if (userID != null) {
            do {
                System.out.println("Please select an option:");
                System.out.println("[1] Create Account");
                System.out.println("[2] Login");
                System.out.println("[3] Browse Auctions");
                System.out.println("[0] Exit");
                try {
                    input = scanner.nextInt();
                    switch(input){
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
                    scanner.next();
                    System.out.println("That is not a valid input");
                }

            }while(input != 0);
        } else {
            do {
                System.out.println("Logged in as %s. You have %d Auctions active and %d Bids open");
                System.out.println("Please select an option:");
                System.out.println("[1] Create Auction");
                System.out.println("[2] Browse Auctions");
                System.out.println("[0] Exit");
                try {
                    input = scanner.nextInt();
                    switch(input){
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
                    scanner.next();
                    System.out.println("That is not a valid input");
                }

            }while(input != 0);
        }



    }

    public void CreateAccount() {
        int complete = 0;
        do {
            System.out.println("Please enter your chosen username");
            String username = scanner.nextLine();
            if (!databaseAccess.CheckAvailability(username)) {
                System.out.println("That username is already taken, please try another");
                continue;
            }
            System.out.println("Please enter your chosen password");
            String password = scanner.nextLine();
            if (databaseAccess.CreateUser(username, password)) {
                System.out.println("Account successfully created! Please login.");
                complete = 1;
            } else {
                System.out.println("Could not create user, please try again");
            }
        }while(complete !=1);
    }

    public void Login() {

    }

    public void CreateAuction() {

    }

    public void BrowseAuction() {

    }

}
