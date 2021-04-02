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

public class Remove implements PrivateCommand {

    private static final Database database = Postimies.getDatabase();
    private static final Tracking tracking = Postimies.getTracking();

    @Override
    public void onCommand(User sender, PrivateChannel channel, String command, String[] args) {

        if (command.equalsIgnoreCase("remove")) {
            EmbedBuilder embed = Utils.EmbedBase();

            if (Utils.isWhitelisted(sender)) {
                if (args.length == 0) {
                    embed = CmdError.ArgNotProvided.get(new StringArg("{arg}", "id"));

                } else if (args.length == 1) {

                    Integer id = null;
                    try {
                        id = Integer.parseInt(args[0]);
                    } catch (Exception ignored) {}

                    if (id != null) {

                        Package pack = database.get(id);
                        if (pack != null) {

                            if (pack.getUserID().equals(sender.getId())) {
                                if (tracking.delete(pack.getTrackingCode(), pack.getCourierCode())) {
                                    if (database.delete(id)) {

                                        embed.setTitle("✅ Success!");
                                        embed.setDescription("Package deleted from Tracking!\n``Id:`` _" + pack.getId() + "_\n``Name:``_" + pack.getPackageName() + "_");

                                        Logger.Info("Removed package from tracking! (ID: "+pack.getId()+" | Name: "+pack.getPackageName()+")");

                                    } else {
                                        embed.setTitle("❌ Failed!");
                                        embed.setDescription("Package could not be deleted from database");
                                        Logger.Warn("Database replied false when tried to delete package ID: " + id);

                                    }
                                } else {
                                    embed.setTitle("❌ Failed!");
                                    embed.setDescription("Package could not be deleted from tracking!");
                                    Logger.Warn("Tracking api responded false when tried to remove tracking");
                                }
                            } else {
                                embed.setTitle("⚠ No permissions!");
                                embed.setDescription("You do not have permissions to remove this package!");
                                Logger.Warn(sender.getName() + " Tried to delete " + pack.getUserName() + " package without permissions");
                            }

                        } else {
                            embed.setTitle("⚠ Invalid Arguments!");
                            embed.setDescription("That id does not exist!");
                        }
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
