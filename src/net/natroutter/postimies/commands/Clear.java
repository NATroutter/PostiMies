package net.natroutter.postimies.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.natroutter.postimies.Postimies;
import net.natroutter.postimies.Utilities.CmdError;
import net.natroutter.postimies.Utilities.Logger;
import net.natroutter.postimies.Utilities.Utils;
import net.natroutter.postimies.handlers.Bot;
import net.natroutter.postimies.handlers.commands.PrivateCommand;

import java.util.List;

public class Clear implements PrivateCommand {

    private static final Bot bot = Postimies.getBot();

    @Override
    public void onCommand(User sender, PrivateChannel channel, String command, String[] args) {

        if (command.equalsIgnoreCase("clear")) {
            EmbedBuilder embed = Utils.EmbedBase();

            if (Utils.isWhitelisted(sender)) {
                if (args.length == 0) {

                    Logger.Warn("Clearing messages!");
                    MessageHistory history = channel.getHistory();
                    List<Message> msgs = history.retrievePast(50).complete();
                    msgs.forEach(m -> {
                        if (m.getAuthor().isBot()) {
                            m.delete().queue();
                        }
                    });
                    Logger.Info("Messages cleared!");
                    return;

                } else if (args.length == 1) {
                    Integer amount = null;
                    try {
                        amount = Integer.parseInt(args[0]);
                    } catch (Exception ignored) {}

                    if (amount != null) {

                        Logger.Warn("Clearing " + amount + " messages!");
                        MessageHistory history = channel.getHistory();
                        List<Message> msgs = history.retrievePast(amount).complete();
                        msgs.forEach(m -> {
                            if (m.getAuthor().isBot()) {
                                m.delete().queue();
                            }
                        });
                        Logger.Info("Messages cleared!");
                        return;

                    } else {
                        embed = CmdError.InvalidArgs.get();
                    }
                } else {
                    embed = CmdError.TooManyArgs.get();
                }
            } else {
                embed = CmdError.NoPerm.get();
            }

            channel.sendMessage(embed.build()).queue();

        }

    }


}
