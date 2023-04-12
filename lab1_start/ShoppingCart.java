import backEnd.*;
import java.util.Scanner;

public class ShoppingCart {
    private static void print(Wallet wallet, Pocket pocket) throws Exception {
        System.out.println("Your current balance is: " + wallet.getBalance() + " credits.");
        System.out.println(Store.asString());
        System.out.println("Your current pocket is:\n" + pocket.getPocket());
    }

    private static String scan(Scanner scanner) throws Exception {
        System.out.print("What do you want to buy? (type quit to stop) ");
        return scanner.nextLine();
    }

    public static void main(String[] args) throws Exception {

        Boolean exploitRaceConditionBoolean = true;
        if (exploitRaceConditionBoolean)
            exploitRaceCondition();
        else {
            runNormal();
        }

    }

    public static void runNormal() throws Exception {
        Wallet wallet = new Wallet();
        Pocket pocket = new Pocket();
        Scanner scanner = new Scanner(System.in);

        print(wallet, pocket);
        String product = scan(scanner);

        while (!product.equals("quit")) {

            if (canBuyProduct(wallet, product)) {
                int newBalance = wallet.getBalance() - Store.getProductPrice(product);
                wallet.setBalance(newBalance);
                pocket.addProduct(product);
            } else {
                System.out.println("You don't have enough credits to buy this product.");
                break;
            }

            // Just to print everything again...
            print(wallet, pocket);
            product = scan(scanner);

        }

        wallet.close();
    }

    /**
     * Checks if the wallet has enough credits to buy a product.
     * 
     * @param wallet
     * @param product
     * @return true if the wallet has enough credits to buy the product, false
     *         otherwise.
     * @throws Exception
     */
    public static boolean canBuyProduct(Wallet wallet, String product) throws Exception {
        return wallet.getBalance() >= Store.getProductPrice(product);
    }

    /**
     * Exploits a raceCondition so that the user can buy 2 cars even if he doesn't
     * have enough credits.
     * 
     * @throws Exception
     */
    public static void exploitRaceCondition() throws Exception {
        Wallet wallet = new Wallet();
        Pocket pocket = new Pocket();

        // we need to be able to purch one car atleast for this exploit to work
        wallet.setBalance(Store.getProductPrice("car"));

        print(wallet, pocket);

        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                try {
                    if (canBuyProduct(wallet, "car")) {
                        Thread.sleep(1000);
                        int newBalance = wallet.getBalance() - Store.getProductPrice("car");
                        wallet.setBalance(newBalance);
                        pocket.addProduct("car");
                    }
                } catch (Exception e) {
                }
            });

            threads[i].start();
        }

        // Wait for all threads to finish
        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }

        print(wallet, pocket);

        wallet.close();

    }

    /**
     * Exploits a raceCondition so that the user can buy 2 cars even if he doesn't
     * have enough credits.
     * 
     * @throws Exception
     */
    public static void exploitRaceConditionWithFix() throws Exception {
        Wallet wallet = new Wallet();
        Pocket pocket = new Pocket();

        // we need to be able to purch one car atleast for this exploit to work
        wallet.setBalance(Store.getProductPrice("car"));

        print(wallet, pocket);

        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                try {
                    if (wallet.safeWithdarw(Store.getProductPrice("car"))) {
                        Thread.sleep(1000);

                        pocket.addProduct("car");
                    }
                } catch (Exception e) {
                }
            });

            threads[i].start();
        }

        // Wait for all threads to finish
        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }

        print(wallet, pocket);

        wallet.close();

    }
}
