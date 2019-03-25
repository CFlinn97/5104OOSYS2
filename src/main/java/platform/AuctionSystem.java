package platform;

import core.Auction;
import core.Item;

import javax.swing.plaf.synth.SynthLookAndFeel;
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
                    DatabaseAccess.UserStat userStat = databaseAccess.GetUserStats(userID);
                    System.out.printf("Logged in as %s. You have %d Auctions active and %d Bids open\n", userStat.username,
                            userStat.auctions, userStat.bid);
                }catch (SQLException e) {
                    System.out.println("Error: " + e.toString());
                    return;
                }

                System.out.println("Please select an option:");
                System.out.println("[1] Create Auction");
                System.out.println("[2] Browse Auctions");
                System.out.println("[3] Create Item");
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
                        case 3:
                            CreateItem();
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
                databaseAccess.CreateUser(username, password);
                    System.out.println("Account successfully created! Please login.");
                    complete = 1;
            } catch (NoSuchAlgorithmException | SQLException e) {//TODO Maybe change to catching generic exceptions instead of specific ones, while we done make use of specificity anyway
                System.out.println("Error: " + e.toString());
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
            } else {
                System.out.println("Login success");
            }
        } while (userID == null);

    }

    public void CreateAuction() {
        List<Item> items;
        Item selectedItem = null;
        try {
            items = databaseAccess.getUserItems(userID);
        }catch (SQLException e) {
            System.out.println("Error: " + e.toString());
            return;
        }
        if (items.size() == 0) {
            System.out.println("You have to register an item before you can sell it!\n");
            CreateItem();
        }
        int allComp = 0;
        do {
            int selectComplete = 0;
            do {
                System.out.println("Please select an item to sell");
                int count = 0;
                for(Item i : items) {
                    count++;
                    System.out.printf("[%d] Name: %s, Description: %.20s, Condition %s\n", count, i.getName(), i.getDescription(), i.getItemDamage().toString());
                }
                int choice = scanner.nextInt();
                scanner.nextLine();
                if (choice >= items.size()) {
                    System.out.println("That is not a valid input");
                } else {
                    selectedItem = items.get(count-1);
                    selectComplete = 1;
                }
            }while (selectComplete != 1);
            System.out.println("Please enter your chosen starting price");
            Double startPrice = Double.parseDouble(scanner.nextLine());
            System.out.println("Please enter your chosen reserve price");
            Double reservePrice = Double.parseDouble(scanner.nextLine());
            int length = 0;
            int timeComp = 0;
            do {
                System.out.println("Please select the length of your auction");
                System.out.println("[1] day");
                System.out.println("[2] days");
                System.out.println("[3] days");
                System.out.println("[4] days");
                System.out.println("[5] days");
                System.out.println("[6] days");
                System.out.println("[7] days");
                int input = scanner.nextInt();
                scanner.nextLine();
                if (input > 7 || (input < 0)) {
                    System.out.println("That is not a valid input");
                } else {
                    timeComp = 1;
                    length = input;
                }
            }while (timeComp != 1);
            System.out.println("\nIs this information correct?[Y/N]");
            System.out.printf("Name: %s, Description: %.20s, Condition %s\n", selectedItem.getName(),
                    selectedItem.getDescription(), selectedItem.getItemDamage().toString());
            System.out.printf("Start price: %f, Reserve price: %f, Length: %d day(s)", startPrice, reservePrice, length);
            char input  = scanner.nextLine().toLowerCase().charAt(0);
            if (input == 'y') {
                allComp = 1;
                try {
                    databaseAccess.createAuction(userID, startPrice, reservePrice, length, selectedItem.getItemID());
                    System.out.println("Auction created!");
                } catch(SQLException e) {
                    System.out.println("Error: " + e.toString());
                    return;
                }

            }

        }while(allComp != 1);



    }

    public void CreateItem() {
        int complete = 0;
        do {
            System.out.println("Please enter item name");
            String name = scanner.nextLine();
            System.out.println("Please enter item description");
            String desc = scanner.nextLine();
            Item.ItemDamage dmg = null;
            int enumComp = 0;
            do {
                System.out.println("Please select item quality");
                System.out.println("[1] NEW");
                System.out.println("[2] GOOD");
                System.out.println("[3] COSMETIC");
                System.out.println("[4] HEAVY");
                int input = scanner.nextInt();
                scanner.nextLine();
                switch (input) {
                    case 1:
                        dmg = Item.ItemDamage.NEW;
                        enumComp = 1;
                        break;
                    case 2:
                        dmg = Item.ItemDamage.GOOD;
                        enumComp = 1;
                        break;
                    case 3:
                        dmg = Item.ItemDamage.COSMETIC;
                        enumComp = 1;
                        break;
                    case 4:
                        dmg = Item.ItemDamage.HEAVY;
                        enumComp = 1;
                        break;
                    default:
                        System.out.println("That is not a valid input");
                }
            }while (enumComp != 1);
            System.out.println("Is this information correct? [Y/N]");
            System.out.printf("Name: %s\nDescription: %s\nQuality: %s\n", name, desc, dmg.toString());
            char input  = scanner.nextLine().toLowerCase().charAt(0);
            if (input == 'y') {
                complete = 1;
                try {
                    databaseAccess.createItem(name, desc, dmg, userID);
                } catch(SQLException e) {
                    System.out.println("Error: " + e.toString());
                    return;
                }

            }
        }while(complete != 1);
    }

    public void BrowseAuction() {

    }

}
