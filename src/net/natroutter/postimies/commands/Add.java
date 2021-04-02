package net.natroutter.postimies.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.natroutter.postimies.Postimies;
import net.natroutter.postimies.Utilities.CmdError;
import net.natroutter.postimies.Utilities.Database;
import net.natroutter.postimies.Utilities.Logger;
import net.natroutter.postimies.Utilities.Utils;
import net.natroutter.postimies.Utilities.objects.CourierData;
import net.natroutter.postimies.Utilities.objects.Package;
import net.natroutter.postimies.Utilities.objects.StringArg;
import net.natroutter.postimies.Utilities.objects.TrackingData;
import net.natroutter.postimies.handlers.Tracking;
import net.natroutter.postimies.handlers.commands.PrivateCommand;

import java.util.Arrays;
import java.util.Collections;

public class Add implements PrivateCommand {

    Tracking tracking = Postimies.getTracking();
    Database database = Postimies.getDatabase();

    @Override
    public void onCommand(User sender, PrivateChannel channel, String command, String[] args) {

        if (command.equalsIgnoreCase("add")) {
            EmbedBuilder embed = Utils.EmbedBase();

            if (Utils.isWhitelisted(sender)) {
                if (args.length == 0) {
                    embed = CmdError.ArgNotProvided.get(new StringArg("{arg}", "TrackingCode"));

                } else if (args.length == 1) {
                    embed = CmdError.ArgNotProvided.get(new StringArg("{arg}", "Name"));

                } else if (args.length >= 2) {
                    String[] nameParts = Arrays.copyOfRange(args, 1, args.length);
                    String name = String.join(" ", nameParts);

                    Package checkPack = database.get(args[0]);
                    if (checkPack == null) { //check if trackingnumber is already in database!

                        CourierData.courier courier = tracking.detectCourier(args[0]);
                        if (courier != null) {

                            if (tracking.create(args[0], courier.courier_code)) {

                                TrackingData track = tracking.getResult(Collections.singletonList(args[0]));
                                String lastUpdate = track.data.get(0).lastest_checkpoint_time;
                                if (lastUpdate == null || lastUpdate.equalsIgnoreCase("null")) {
                                    lastUpdate = "Unknown";
                                }

                               // System.out.println("[DEBUG] add: " + lastUpdate);

                                if (database.insert(new Package(0, sender.getId(), sender.getName(), name, args[0], courier.courier_name, courier.courier_code, lastUpdate))) {

                                    EmbedBuilder successEmbed = Utils.EmbedBase();
                                    successEmbed.setTitle("✅ Success!");
                                    successEmbed.setDescription("Package added to Tracking!");
                                    channel.sendMessage(successEmbed.build()).queue();

                                    EmbedBuilder PackageInfo = Utils.Packageinfo(track.data.get(0));
                                    channel.sendMessage(PackageInfo.build()).queue();

                                    Logger.Info("Added new package to tracking!");

                                } else {
                                    Logger.Error("Failed to add package to database!");
                                }

                                return;

                            } else {
                                embed.setTitle("❌ Tracing Error!");
                                embed.setDescription("Failed to create new tracking!");
                            }
                        } else {
                            embed.setTitle("❌ Carrier Detecting Error!");
                            embed.setDescription("Failed while trying to detect carrier!");
                        }
                    } else {
                        embed.setTitle("❌ Package already exits!");
                        embed.setDescription("That package is already in tracking with ID: ``"+checkPack.getId()+"``!");
                    }


                }
            } else {
                CmdError.NoPerm.get();
            }

            channel.sendMessage(embed.build()).queue();

        }

    }

}
