package net.natroutter.postimies;

import net.natroutter.postimies.Utilities.Config;
import net.natroutter.postimies.Utilities.Database;
import net.natroutter.postimies.commands.*;
import net.natroutter.postimies.handlers.AutoChecker;
import net.natroutter.postimies.handlers.Bot;
import net.natroutter.postimies.handlers.Tracking;
import net.natroutter.postimies.handlers.commands.CommandHandler;

import java.util.Scanner;

public class Postimies {

    private static Scanner scan = new Scanner(System.in);

    private static Config cfg;
    private static Bot bot;
    private static Database database;
    private static Tracking tracking;
    private static AutoChecker autoChecker;

    public static Config getConfig() {return cfg;}
    public static Bot getBot() {return bot;}
    public static Database getDatabase() {return database;}
    public static Tracking getTracking() {return tracking;}
    public static AutoChecker getAutoChecker() {return autoChecker;}

    public static void main(String[] args) {

        cfg = new Config("config.json");

        database = new Database();
        tracking = new Tracking();

        autoChecker = new AutoChecker(60 * 15);

        //Register commands!
        CommandHandler.registerCommands(
                Help.class, Add.class, List.class,
                Remove.class, Check.class, Clear.class,
                Quit.class, Rename.class
        );

        //create bot
        bot = new Bot(cfg);

        //register bot eventlistenrs and connect bot!
        bot.registerListeners(CommandHandler.class);
        bot.connect();

        //start console!
        Console(true);
    }

    private static void Console(boolean first) {Console(first, false);}
    private static void Console(boolean first, boolean err) {
        if (!err) {
            System.out.print("PostiMies: ");
        }
        if (scan.hasNextLine() && !err) {
            String input = scan.nextLine();
            CommandHandling(input);
            Console(false, false);
        } else {
            try {Thread.sleep(100L);} catch (Exception ignored) {}
            Console(false, true);
        }
    }





    private static boolean CommandHandling(String cmd) {
        if (cmd.equalsIgnoreCase("help")) {
            System.out.println("---------[ PostiMies Help ]---------");
            System.out.println("Commands:");
            System.out.println("  - connect     :: Connect bot to servers");
            System.out.println("  - disconnect  :: Disconnect bot from servers");
            System.out.println("  - quit        :: Stop the program");
            System.out.println("---------[ PostiMies Help ]---------");
            return true;

        } else if (cmd.equalsIgnoreCase("disconnect")) {
            if (bot.isConnected()) {
                bot.disconnect();
            } else {
                System.out.println("Bot is not connected!");
            }
            return true;

        } else if (cmd.equalsIgnoreCase("connect")) {
            if (!bot.isConnected()) {
                bot.connect();
            } else {
                System.out.println("bot is already connected!");
            }
            return true;

        } else if (cmd.equalsIgnoreCase("quit")) {
            bot.disconnect();
            System.exit(0);
            return true;
        }
        return false;
    }



}
