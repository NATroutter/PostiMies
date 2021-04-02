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
import net.natroutter.postimies.Utilities.objects.StringArg;
import net.natroutter.postimies.handlers.Tracking;
import net.natroutter.postimies.handlers.commands.PrivateCommand;

import java.util.Arrays;

public class Rename implements PrivateCommand {


    private static final Database database = Postimies.getDatabase();
    private static final Tracking tracking = Postimies.getTracking();

    @Override
    public void onCommand(User sender, PrivateChannel channel, String command, String[] args) {

        if (command.equalsIgnoreCase("rename")) {
            EmbedBuilder embed = Utils.EmbedBase();

            if (Utils.isWhitelisted(sender)) {
                if (args.length == 0) {
                    embed = CmdError.ArgNotProvided.get(new StringArg("{arg}", "id"));

                } else if (args.length == 1) {
                    embed = CmdError.ArgNotProvided.get(new StringArg("{arg}", "name"));

                } else if (args.length >= 2) {
                    String[] nameParts = Arrays.copyOfRange(args, 1, args.length);
                    String name = String.join(" ", nameParts);

                    Integer id = null;
                    try {
                        id = Integer.parseInt(args[0]);
                    } catch (Exception ignored) {}

                    if (id != null) {

                        Package pack = database.get(id);
                        if (pack != null) {
                            if (pack.getUserID().equals(sender.getId())) {

                                if (database.rename(id, name)) {
                                    Logger.Info("Renamed package " + pack.getId() + " from: " + pack.getPackageName() + " to: " + name);
                                    embed.setTitle("✅ Success!");
                                    embed.setDescription("Package ``"+pack.getId()+"`` has been renamed!\nFrom: ``"+pack.getPackageName()+"`` to ``"+name+"``");
                                } else {
                                    embed.setTitle("❌ Failed!");
                                    embed.setDescription("Package could not be renamed!");
                                    Logger.Error("Failed to rename package ID: " + id);
                                }
                            } else {
                                embed.setTitle("⚠ No permissions!");
                                embed.setDescription("You do not have permissions to check this package!");
                                Logger.Warn(sender.getName() + " Tried to rename " + pack.getUserName() + " package without permissions");
                            }
                        } else {
                            embed.setTitle("⚠ Invalid Arguments!");
                            embed.setDescription("That id does not exist!");
                        }
                    } else {
                        embed = CmdError.InvalidArgs.get();
                    }
                }
            } else {
                embed = CmdError.NoPerm.get();
            }
            channel.sendMessage(embed.build()).queue();
        }
    }

}
