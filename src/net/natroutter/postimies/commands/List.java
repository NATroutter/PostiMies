package net.natroutter.postimies.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.natroutter.postimies.Postimies;
import net.natroutter.postimies.Utilities.CmdError;
import net.natroutter.postimies.Utilities.Database;
import net.natroutter.postimies.Utilities.Logger;
import net.natroutter.postimies.Utilities.Utils;
import net.natroutter.postimies.Utilities.objects.Package;
import net.natroutter.postimies.handlers.commands.PrivateCommand;

import java.util.ArrayList;

public class List implements PrivateCommand {

    private static final Database database = Postimies.getDatabase();

    @Override
    public void onCommand(User sender, PrivateChannel channel, String command, String[] args) {

        if (command.equalsIgnoreCase("list")) {
            EmbedBuilder embed = Utils.EmbedBase();

            if (Utils.isWhitelisted(sender)) {
                if (args.length == 0) {

                    Logger.Info("Displaying users packages!");
                    embed.setTitle("\uD83D\uDCCB Your Packages!");
                    embed.setDescription("_List of your currently active package trackings_");

                    ArrayList<Package> packages = database.getAll();
                    int userPacks = 0;

                    if (packages.size() > 0) {
                        embed.setDescription("_List of your currently active package trackings_");
                        for (Package pack : packages) {
                            if (pack.getUserID().equals(sender.getId())) {
                                embed.addField(pack.getPackageName() + " (ID:" +pack.getId()+ ")", "``TrackingNumber:`` _"+pack.getTrackingCode()+"_\n``Last Update:`` _"+Utils.FormatDate2(pack.getLastUpdate())+"_\n``Carrier:`` _"+pack.getCourierName()+"_", false);
                                userPacks++;
                            }
                        }
                        if (userPacks == 0) {
                            embed.setDescription("_You dont have any packages currently in tracking!_");
                        }
                    } else {
                        embed.setDescription("_You dont have any packages currently in tracking!_");
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
