package net.natroutter.postimies.Utilities;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.natroutter.postimies.Postimies;
import net.natroutter.postimies.Utilities.objects.Package;
import net.natroutter.postimies.Utilities.objects.TrackingData;
import net.natroutter.postimies.handlers.Bot;
import net.natroutter.postimies.handlers.Tracking;
import net.natroutter.postimies.handlers.Tracking.DeliveryStatysType;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("deprecation")
public class Utils {

    private static final Bot bot = Postimies.getBot();
    private static final Config cfg = Postimies.getConfig();
    private static final Database database = Postimies.getDatabase();
    private static final Tracking Tracking = Postimies.getTracking();


    public enum Status {
        Online, ShuttingDown
    }

    public static boolean isWhitelisted(User user) {
        for (Config.Whitelisted wUser : cfg.getWhitelisted()) {
            if (wUser.UserID == null) {
                Logger.Error("Invalid user: (" + wUser.UserName + ":" + wUser.UserID + ")");
            }
            if (user.getIdLong() == wUser.UserID) {
                return true;
            }
        }
        return false;
    }

    public static String getCourierImage(String courierCode) {
        switch (courierCode) {
            case "finland-posti":
                return "https://i.imgur.com/9z71adp.png";
            case "dhl":
            case "dhlglobalmail":
            case "dhl-benelux":
            case "dhl-germany":
            case "dhlparcel-nl":
            case "dhl-poland":
            case "dhl-es":
            case "dhl-parcel-nl":
            case "dhlecommerce-asia":
            case "dhl-active":
            case "dhl-hong-kong":
            case "dhl-global-logistics":
            case "dhl-uk":
                return "https://i.imgur.com/xAt0Zx3.png";
            case "fedex":
            case "opek":
            case "fedex-uk":
                return "https://i.imgur.com/JiwtR9f.png";
            case "ups":
            case "ups-mi":
                return "https://i.imgur.com/gvqoztI.png";
        }
        return null;
    }

    public static EmbedBuilder EmbedBase() {
        Date date = new Date();
        SimpleDateFormat Formater = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(new Color(235, 52, 177));
        builder.setFooter("TimeStamp | " + Formater.format(date));
        return builder;
    }

    public static EmbedBuilder Packageinfo(TrackingData.Package data) {
        EmbedBuilder builder = EmbedBase();
        Package pack = database.get(data.tracking_number);

        String description = "";

        String status = null;
        String subStatus = null;
        String details = null;
        String createDate;
        String updateDate;
        String weight;
        if (data.delivery_status != null) {
            status = Tracking.translateStatus(DeliveryStatysType.normal, data.delivery_status);
            status = "\n``Status:`` _"+status+"_";
        }
        if (data.substatus != null) {
            subStatus = Tracking.translateStatus(DeliveryStatysType.subStatus, data.substatus);
            subStatus = "\n``Substatus:`` _"+subStatus.toLowerCase()+"_";
        }
        if (data.created_at != null) {
            createDate = "\n``Creation Date:`` _"+FormatDate2(data.created_at)+"_";
            description = description + createDate;
        }
        if (data.lasest_event != null) {
            String[] sp = data.lasest_event.split(",");
            details = "\n``Details:`` _" + sp[0].toLowerCase() + "_";
            updateDate = "\n``Last Update:`` _" + FormatDate(sp[sp.length - 1]) + "_";
            description = description + updateDate;
        }
        if (data.weight != null) {
            weight = "\n``Weight:`` _"+data.weight+"_";
            description = description + weight;
        }

        if (status != null || subStatus != null || details != null) {
            description = description + "\n\n**─────── Current Status ───────**";

            if (status != null) {
                description = description + status;
            }
            if (subStatus != null) {
                description = description + subStatus;
            }
            if (details != null) {
                description = description + details;
            }
        }


        if (data.origin_info.trackinfo.size() > 0) {
            description = description + "\n\n**─────── Tracking Information ───────**";
        }

        builder.setTitle("\uD83D\uDCE6 Package information (" + pack.getPackageName() + ")");
        builder.setDescription("\n``Id:`` _"+pack.getId()+"_\n``Tracking Number:`` _"+data.tracking_number+"_\n``Courier:`` _"+pack.getCourierName() +"_" + description);

        String img = getCourierImage(data.courier_code);
        if (img != null) {
            builder.setThumbnail(img);
        }

        for (TrackingData.Package.originInfo.trackInfo track : data.origin_info.trackinfo) {
            String descInfo = "";
            String status1;
            String subStatus2;
            String details2;
            if (track.checkpoint_delivery_status != null) {
                status1 = Tracking.translateStatus(DeliveryStatysType.normal, track.checkpoint_delivery_status);
                status1 = "``Status:`` _"+status1+"_\n";
                descInfo = descInfo + status1;
            }
            if (track.checkpoint_delivery_substatus != null) {
                subStatus2 = Tracking.translateStatus(DeliveryStatysType.subStatus, track.checkpoint_delivery_substatus);
                subStatus2 = "``Substatus:`` _"+subStatus2.toLowerCase()+"_\n";
                descInfo = descInfo + subStatus2;
            }
            if (track.tracking_detail != null) {
                details2 = "``Details:`` _"+track.tracking_detail.toLowerCase()+"_\n";
                descInfo = descInfo + details2;
            }
            builder.addField(FormatDate(track.checkpoint_date), descInfo, false);
        }

        return builder;
    }

    public static String FormatDate(String raw) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(raw);
            SimpleDateFormat newFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            return newFormat.format(date);
        } catch (Exception e){
            Logger.Error("[FormatDate1] Failed to parse date! (Raw: " + raw + ")");
        }
        return raw;
    }

    public static String FormatDate2(String raw) {
        try {
            // 2021-03-30T17:19:45+00:00
            String[] splited = raw.split("T"); // 2021-03-30
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(splited[0]);

            String[] splitted2 = splited[1].split(":");
            date.setHours(Integer.parseInt(splitted2[0]));
            date.setMinutes(Integer.parseInt(splitted2[1]));


            SimpleDateFormat newFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            return newFormat.format(date);

        } catch (Exception e) {
            Logger.Error("[FormatDate2] Failed to parse date! (Raw: " + raw + ")");
        }
        return raw;
    }

    public static void sendStatus(Status stat) {
        sendStatus(stat, false);
    }
    public static void sendStatus(Status stat, boolean quit) {
        for (Config.Whitelisted wUser : cfg.getWhitelisted()) {
            if (wUser.UserID == null) {
                Logger.Error("Invalid user: (" + wUser.UserName + ":" + wUser.UserID + ")");
            }

            bot.getApi().retrieveUserById(wUser.UserID).queue(user -> user.openPrivateChannel().queue(c -> {

                EmbedBuilder builder = EmbedBase();

                if (stat.equals(Status.Online)) {
                    builder.setTitle("✅ Status: online");
                    builder.setDescription("Hello i'm PostiMies i will help you with your post package tracking!\nIf you want to know more just type ``.help``");
                    c.sendMessage(builder.build()).queue();

                } else if (stat.equals(Status.ShuttingDown)) {
                    builder.setTitle("❌ Status: Shuttingdown");
                    builder.setDescription("Sorry but i have to go now :(");
                    c.sendMessage(builder.build()).queue();

                    bot.getApi().shutdown();
                    bot.setConnected(false);
                    Logger.Warn("Disconnected!");
                    if (quit) {
                        System.exit(0);
                    }
                }
            }));
        }
    }

    public static void SendEmbed(String userId, MessageEmbed emb) {
        bot.getApi().retrieveUserById(userId).queue(user -> user.openPrivateChannel().queue(c -> c.sendMessage(emb).queue()));
    }


}
