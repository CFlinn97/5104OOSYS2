package platform;

import core.Auction;
import core.Item;

import java.math.RoundingMode;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;



public class AuctionSystem {
    private final Scanner scanner = new Scanner(System.in);
    private final DatabaseAccess databaseAccess;
    private Integer userID = null;
    Thread t;
    boolean run = true;
    public AuctionSystem() {
        databaseAccess = new DatabaseAccess();
        Runnable updater = new Runnable() {
            @Override
            public void run() {
                while (run) {
                    try {
                        databaseAccess.updateDatabaseInfo();
                        System.out.println("DB UPDATE RUN");
                        Thread.sleep(60000);
                    } catch (SQLException | InterruptedException e) {
                        System.out.println(e.toString());
                        break;

                    }
                }
            }
        };
        t = new Thread(updater);
        t.start();
    }

    public void begin() {
        Integer input = null;
        try {
            int[] counts = databaseAccess.GetSystemStats();
            System.out.printf("Welcome to the Auction System. \n" +
                            "Stats: Users %d, Total Auctions %d, Active Auctions %d, Bids %d\n",
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
                        case 0:
                            run = false;
                            t.interrupt();
                            return;
                        default:
                            System.out.println("Shouldn't see this");
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
                    WinNotification(databaseAccess.checkWonAuctions(userID));

                } catch (SQLException e) {
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
        } while (input != 0);
    }
    //TODO Show logged in users all own auctions
    //TODO Show logged in users all auctions they have bid on
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
        Login:
        do {
            System.out.println("Please enter username");
            String username = scanner.nextLine();
            System.out.println("Please enter your password");
            String password = scanner.nextLine();
            try {
                userID = databaseAccess.LoginUser(username, password);
            } catch (NoSuchAlgorithmException | SQLException e) {
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
        } catch (SQLException e) {
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
                for (Item i : items) {
                    count++;
                    System.out.printf("[%d] Name: %s, Description: %.20s, Condition %s\n", count, i.getName(), i.getDescription(), i.getItemDamage().toString());
                }
                int choice = scanner.nextInt();
                scanner.nextLine();
                if (choice > count || choice < 0) {
                    System.out.println("That is not a valid input");
                } else {
                    selectedItem = items.get(count - 1);
                    selectComplete = 1;
                }
            } while (selectComplete != 1);
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
            } while (timeComp != 1);
            System.out.println("\nIs this information correct?[Y/N]");
            System.out.printf("Name: %s, Description: %.20s, Condition %s\n", selectedItem.getName(),
                    selectedItem.getDescription(), selectedItem.getItemDamage().toString());
            System.out.printf("Start price: %.2f, Reserve price: %.2f, Length: %d day(s)\n", startPrice, reservePrice, length);
            char input = scanner.nextLine().toLowerCase().charAt(0);
            if (input == 'y') {
                allComp = 1;
                try {
                    databaseAccess.createAuction(userID, startPrice, reservePrice, length, selectedItem.getItemID());
                    System.out.println("Auction created!");
                } catch (SQLException e) {
                    System.out.println("Error: " + e.toString());
                    return;
                }

            }

        } while (allComp != 1);


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
            } while (enumComp != 1);
            System.out.println("Is this information correct? [Y/N]");
            System.out.printf("Name: %s\nDescription: %s\nQuality: %s\n", name, desc, dmg.toString());
            char input = scanner.nextLine().toLowerCase().charAt(0);
            if (input == 'y') {
                complete = 1;
                try {
                    databaseAccess.createItem(name, desc, dmg, userID);
                } catch (SQLException e) {
                    System.out.println("Error: " + e.toString());
                    return;
                }

            }
        } while (complete != 1);
    }

    public void BrowseAuction() {
        List<Auction> auctions;
        try {
            auctions = databaseAccess.getActiveAuctions();
        } catch (SQLException e) {
            System.out.println(e.toString());
            return;
        }
        boolean picked = false;
        do {
            System.out.println("Please select an auction to view, or enter 0 to go back.");
            int count = 0;
            for (Auction a : auctions) {
                count++;
                System.out.printf("[%d] Item: %s, Price: %.2f, Description: %20s\n", count, a.getAuctionItem().getName(),
                        a.getHighestBid() == null ? a.getStartPrice() : a.getHighestBid().getAmount(), a.getAuctionItem().getDescription());
            }
            int input = scanner.nextInt();
            scanner.nextLine();
            if (input == 0) {
                return;
            }
            if (input <= count && input > 0) {
                ViewAuction(auctions.get(input - 1));
                picked = true;
            } else {
                System.out.println("That is not a valid input");
            }
        } while (!picked);

    }

    public void ViewAuction(Auction auctionIN) {
        Auction auction;
        do {
            try {
                auction = databaseAccess.getAuction(auctionIN.getAuctionID());
            } catch (SQLException e) {
                System.out.println(e.toString());
                return;
            }
            System.out.println("========== Auction Details ==========");
            System.out.printf("Name: %s", auction.getAuctionItem().getName());
            System.out.printf("\nDescription: %s", auction.getAuctionItem().getDescription());
            System.out.printf("\nCondition: %s", auction.getAuctionItem().getItemDamage().toString());
            System.out.printf("\nHighest Bid: %.2f", auction.getHighestBid() == null ? 0.00 : auction.getHighestBid().getAmount());
            System.out.printf("%s %tA %<td %<tB %<tT", "\nClose Date:", auction.getCloseDate());
            System.out.println("\nPlease make a selection: ");
            System.out.println("[1] Place Bid");
            System.out.println("[2] Refresh Info");
            System.out.println("[0] Back");
            int input = scanner.nextInt();
            scanner.nextLine();
            switch (input) {
                case 0:
                    return;
                case 1:
                    PlaceBid(auction);
                case 2:
                    break;
                default:
                    System.out.println("That is not a valid input");
            }
        } while (true);


    }

    public void PlaceBid(Auction auctionIN) {
        if(userID == null) {
            System.out.println("You must be logged in to place a bid");
            return;
        }
        boolean placed = false;
        do {
            Auction auction;
            try {
                auction = databaseAccess.getAuction(auctionIN.getAuctionID());
            } catch (SQLException e) {
                System.out.println(e.toString());
                return;
            }

            System.out.printf("The minimum bid you can place is: %.2f\n", auction.getLowestPossibleBid());
            System.out.println("Please input the amount you would like to bid");
            double input;
            try {
                input = Double.parseDouble(scanner.nextLine());
                DecimalFormat df = new DecimalFormat("#.##");
                df.setRoundingMode(RoundingMode.CEILING);
                input = Double.parseDouble(df.format(input));
            } catch (NumberFormatException e) {
                System.out.println("That is not a valid input, format must be '0.00'");
                continue;
            }
            try {
                databaseAccess.placeBid(auction.getAuctionID(), userID, input);
            } catch (SQLException e) {
                System.out.println("Bid could not be placed, please try again later");
                System.out.println(e.toString());
                break;
            }
            System.out.println("Bid successfully placed");
            placed = true;
        }while(!placed);
    }

    public void WinNotification(List<Auction> wins) {
        if (wins.size() > 0) {
            System.out.println("\n~~~ You have won! ~~~");
            for (Auction a : wins) {
                System.out.printf("Item: %s, Price: %.2f", a.getAuctionItem().getName(), a.getHighestBid().getAmount());
                try {
                    databaseAccess.markWinnerNotified(a);
                } catch (SQLException e) {
                    System.out.println(e.toString());
                }

            }
        }
    }


}
