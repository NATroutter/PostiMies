package net.natroutter.postimies.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.natroutter.postimies.Utilities.CmdError;
import net.natroutter.postimies.Utilities.Utils;
import net.natroutter.postimies.handlers.commands.PrivateCommand;

public class Help implements PrivateCommand {

    @Override
    public void onCommand(User sender, PrivateChannel channel, String command, String[] args) {

        if (command.equalsIgnoreCase("help")) {
            EmbedBuilder embed = Utils.EmbedBase();

            if (Utils.isWhitelisted(sender)) {
                if (args.length == 0) {

                    embed.setTitle("\uD83D\uDCDA Helpping hand!");
                    embed.setDescription("_Here is a list of commands that you can use\nSome commands has 1 second cooldown because the api is rate limited!_");
                    embed.addField(".add <tracking number> <name>", "_Add new package to tracing_", false);
                    embed.addField(".list", "_Shows list of packages in tracking_", false);
                    embed.addField(".remove <id>", "_remove  package from tracking_", false);
                    embed.addField(".check <id|all|update>", "_Check package current status_", false);
                    embed.addField(".rename <id> <new name>", "_rename package_", false);
                    embed.addField(".clear <amount>", "_Remove chat messageas_", false);
                    embed.addField(".quit", "_Disconnect and quit bot_", false);

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




















