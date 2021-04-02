package net.natroutter.postimies.handlers;

import net.dv8tion.jda.api.EmbedBuilder;
import net.natroutter.postimies.Postimies;
import net.natroutter.postimies.Utilities.Config;
import net.natroutter.postimies.Utilities.Database;
import net.natroutter.postimies.Utilities.Logger;
import net.natroutter.postimies.Utilities.Utils;
import net.natroutter.postimies.Utilities.objects.Package;
import net.natroutter.postimies.Utilities.objects.TrackingData;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class AutoChecker {

    private final Tracking tracking = Postimies.getTracking();
    private final Database database = Postimies.getDatabase();
    private final Config config = Postimies.getConfig();

    private Integer Interval;
    private Timer timer;

    public AutoChecker(Integer IntervalSeconds) {
        this.Interval = IntervalSeconds * 1000;
        this.timer = new Timer();
        this.timer.scheduleAtFixedRate(checkTask, (30 * 1000), Interval);
    }

    public Integer getInterval() {
        return Interval;
    }

    public boolean run() {
        return process();
    }

    private TimerTask checkTask = new TimerTask() {
        @Override
        public void run() {
            process();
        }
    };

    private boolean process() {
        long startTime = System.currentTimeMillis();
        int packageUpdates = 0;

        Logger.Info("[AutoChecker] Checking package status!");

        EmbedBuilder embed = Utils.EmbedBase();
        ArrayList<String> codes = new ArrayList<>();
        ArrayList<Package> packs = database.getAll();

        if (packs.size() > 0) {

            packs.forEach(p -> {
                codes.add(p.getTrackingCode());
            });

            TrackingData track = tracking.getResult(codes);
            if (track != null) {

                for (Package pack : packs) {
                    if (track.data == null) {
                        return false;
                    }
                    for (int i = 0; i < track.data.size(); i++) {
                        TrackingData.Package pack2 = track.data.get(i);
                        if (pack.getTrackingCode().equals(pack2.tracking_number)) {

                            String lastUpdate = track.data.get(i).lastest_checkpoint_time;
                            if (lastUpdate == null || lastUpdate.equalsIgnoreCase("null")) {
                                lastUpdate = "Unknown";
                            }

                           // System.out.println("[DEBUG] " + pack.getLastUpdate() + " - " + lastUpdate);

                            if (!pack.getLastUpdate().equals(lastUpdate)) {
                                String araived = pack2.origin_info.arrived_destination_date;
                                if (araived != null && !araived.equalsIgnoreCase("null")) {

                                    EmbedBuilder builder = Utils.Packageinfo(pack2);
                                    builder.setTitle("\uD83C\uDF89 Your package as araived (" + pack.getPackageName() + ") \uD83C\uDF89");
                                    Utils.SendEmbed(pack.getUserID(), builder.build());

                                    Logger.Info("[AutoChecker] Package as araived "+pack.getTrackingCode()+" (" + pack.getPackageName() + ")");

                                    if (config.getRemoveWhenAraived()) {
                                        Logger.Warn("[AutoChecker] Removing araived package from tracking! ID: " + pack.getId() + " | Name: " + pack.getPackageName());
                                        database.delete(pack.getId());
                                        tracking.delete(pack.getTrackingCode(), pack.getCourierCode());
                                        try {Thread.sleep(1100L);} catch (Exception ignored) {}
                                    } else {
                                        Logger.Warn("[AutoChecker] Updating araived package! ID: " + pack.getId() + " | Name: " + pack.getPackageName());
                                        database.updateDate(pack.getId(), lastUpdate);
                                    }

                                } else {

                                    database.updateDate(pack.getId(), lastUpdate);

                                    EmbedBuilder builder = Utils.Packageinfo(pack2);
                                    builder.setTitle("\uD83D\uDCE6 Updated on package (" + pack.getPackageName() + ")");
                                    Utils.SendEmbed(pack.getUserID(), builder.build());

                                    Logger.Info("[AutoChecker] New update on package "+pack.getTrackingCode()+" (" + pack.getPackageName() + ")");

                                }
                                packageUpdates++;
                            } else {
                                Logger.Info("[AutoChecker] No update on package "+pack.getTrackingCode()+" (" + pack.getPackageName() + ")");
                            }
                        }
                    }
                }
                Logger.Info("[AutoChecker] Package checking finished in (" + (System.currentTimeMillis() - startTime) + "ms) (UpdatedPackages: "+packageUpdates+"/"+track.data.size()+")");
                return packageUpdates > 0;
            } else {
                Logger.Error("Api failed to send data!");
            }
        }
        return false;
    }

























}
