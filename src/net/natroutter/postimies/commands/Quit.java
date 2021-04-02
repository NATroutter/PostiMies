package net.natroutter.postimies.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.natroutter.postimies.Postimies;
import net.natroutter.postimies.Utilities.CmdError;
import net.natroutter.postimies.Utilities.Config;
import net.natroutter.postimies.Utilities.Utils;
import net.natroutter.postimies.handlers.commands.PrivateCommand;

public class Quit implements PrivateCommand {

    private Config config = Postimies.getConfig();

    @Override
    public void onCommand(User sender, PrivateChannel channel, String command, String[] args) {

        if (command.equalsIgnoreCase("quit")) {
            EmbedBuilder embed = Utils.EmbedBase();

            if (config.getWhitelisted().get(0) != null) {
                if (config.getWhitelisted().get(0).UserID.equals(sender.getIdLong())) {
                    if (args.length == 0) {

                        Utils.sendStatus(Utils.Status.ShuttingDown, true);
                        return;

                    } else {
                        embed = CmdError.TooManyArgs.get();
                    }
                } else {
                    embed = CmdError.NoPerm.get();
                }
            }

            channel.sendMessage(embed.build()).queue();

        }

    }



}
