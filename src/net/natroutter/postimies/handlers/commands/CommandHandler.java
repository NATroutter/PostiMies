package net.natroutter.postimies.handlers.commands;

import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.natroutter.postimies.Postimies;
import net.natroutter.postimies.Utilities.Config;
import net.natroutter.postimies.Utilities.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandHandler extends ListenerAdapter {

    Config cfg = Postimies.getConfig();

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) { return; }

        String message = e.getMessage().getContentRaw();

        if (message.startsWith(cfg.getCommandPrefix())) {
            String[] args = message.split(" ");
            String command = args[0].substring(1);
            args = Arrays.copyOfRange(args, 1, args.length);

            Logger.Info("[Command] "+e.getAuthor().getName()+" || "+command+" " + String.join(" ", args));

            for (PrivateCommand cmd : commands) {
                cmd.onCommand(e.getAuthor(), e.getChannel(), command, args);

            }
        }
    }

    private static List<PrivateCommand> commands = new ArrayList<>();

    public static void registerCommands(Class<?>... clazz) {
        for (Class<?> c : clazz) {
           try {
               commands.add((PrivateCommand)c.getDeclaredConstructor().newInstance());
           } catch (Exception ignored) {}
        }
    }
}
