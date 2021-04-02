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

        System.out.println(" ");
        System.out.println("  _____          _   _           _           ");
        System.out.println(" |  __ \\        | | (_)         (_)          ");
        System.out.println(" | |__) |__  ___| |_ _ _ __ ___  _  ___  ___ ");
        System.out.println(" |  ___/ _ \\/ __| __| | '_ ` _ \\| |/ _ \\/ __|");
        System.out.println(" | |  | (_) \\__ \\ |_| | | | | | | |  __/\\__ \\");
        System.out.println(" |_|   \\___/|___/\\__|_|_| |_| |_|_|\\___||___/");
        System.out.println(" ");

        cfg = new Config("config.json");

        if (cfg.getDiscordApiKey() == null || cfg.getDiscordApiKey().length() < 51 && cfg.getTrackingMoreApiKey() == null || cfg.getTrackingMoreApiKey().length() < 33) {

            System.out.println(" ");
            System.out.println("You need to configure discord api key & trackingmore api key in config.json");
            System.out.println(" ");
            System.exit(0);
            return;
        }

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
        Console();
    }

    private static Integer dummy = 0;
    private static void Console() {Console(false);}
    private static void Console(boolean err) {
        if (!err) { System.out.print("PostiMies: "); }

        if (dummy == -1) { return; } // this is stupid but my intellij throws some stupid error if i dont have this
        dummy++;

        if (scan.hasNextLine() && !err) {
            String input = scan.nextLine();
            CommandHandling(input);
            Console(false);
        } else {
            try {Thread.sleep(100L);} catch (Exception ignored) {}
            Console(true);
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
            if (bot.isConnected()) {
                bot.disconnect();
            }
            System.exit(0);
            return true;
        }
        return false;
    }



}
