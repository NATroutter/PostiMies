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
import net.natroutter.postimies.Utilities.objects.TrackingData;
import net.natroutter.postimies.handlers.AutoChecker;
import net.natroutter.postimies.handlers.Tracking;
import net.natroutter.postimies.handlers.commands.PrivateCommand;

import java.util.ArrayList;
import java.util.Collections;

public class Check implements PrivateCommand {

    private static final Database database = Postimies.getDatabase();
    private static final Tracking tracking = Postimies.getTracking();
    private static final AutoChecker checker = Postimies.getAutoChecker();

    @Override
    public void onCommand(User sender, PrivateChannel channel, String command, String[] args) {

        if (command.equalsIgnoreCase("check")) {
            EmbedBuilder embed = Utils.EmbedBase();

            if (Utils.isWhitelisted(sender)) {
                if (args.length == 0) {
                    embed = CmdError.ArgNotProvided.get(new StringArg("{arg}", "id"));

                } else if (args.length == 1) {

                    if (args[0].equalsIgnoreCase("all")) {

                        ArrayList<Package> packages = database.getAll();
                        if (packages.size() > 0) {

                            Logger.Info("Displaying all users packages!");

                            ArrayList<String> trackingNumbers = new ArrayList<>();
                            for (Package pack : packages) {
                                if (pack.getUserID().equals(sender.getId())) {
                                    trackingNumbers.add(pack.getTrackingCode());
                                }
                            }
                            TrackingData trackingData = tracking.getResult(trackingNumbers);

                            for (TrackingData.Package packData : trackingData.data) {
                                EmbedBuilder emb = Utils.Packageinfo(packData);
                                channel.sendMessage(emb.build()).queue();
                            }

                            return;

                        } else {
                            embed.setTitle("⚠ No packages!");
                            embed.setDescription("You do not have any packages in tracking!");
                            Logger.Info("User did not have any packages in tracking");
                        }

                    } else if (args[0].equalsIgnoreCase("update")) {
                        if (!checker.run()) {
                            embed.setTitle("⚠ No updates!");
                            embed.setDescription("All packages are uptodate!");
                        } else {
                            return;
                        }

                    } else {
                        Integer id = null;
                        try {
                            id = Integer.parseInt(args[0]);
                        } catch (Exception ignored) {}

                        if (id != null) {

                            Package pack = database.get(id);
                            if (pack != null) {
                                if (pack.getUserID().equals(sender.getId())) {

                                    TrackingData packet = tracking.getResult(Collections.singletonList(pack.getTrackingCode()));
                                    if (packet != null) {

                                        Logger.Info("Dispalying package ID: " + id);
                                        embed = Utils.Packageinfo(packet.data.get(0));

                                    } else {
                                        embed.setTitle("❌ Failed!");
                                        embed.setDescription("Can not get data from api!");
                                        Logger.Error("Tracking api failed to send data!");
                                    }

                                } else {
                                    embed.setTitle("⚠ No permissions!");
                                    embed.setDescription("You do not have permissions to check this package!");
                                    Logger.Warn(sender.getName() + " Tried to check " + pack.getUserName() + " package without permissions");
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
                    embed = CmdError.TooManyArgs.get();
                }
            } else {
                embed = CmdError.NoPerm.get();
            }
            channel.sendMessage(embed.build()).queue();
        }
    }

}
