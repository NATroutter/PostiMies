package net.natroutter.postimies.handlers;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.managers.AccountManager;
import net.natroutter.postimies.Utilities.Config;
import net.natroutter.postimies.Utilities.Logger;
import net.natroutter.postimies.Utilities.Utils;

import javax.security.auth.login.LoginException;
import java.net.URI;
import java.net.URL;

public class Bot {
    private Config cfg;
    private JDA jda;
    private JDABuilder builder;
    private boolean Connected = false;

    public void setConnected(boolean b) {this.Connected = b;}
    public boolean isConnected(){ return Connected; }
    public JDA getApi(){ return jda; }

    public Bot(Config cfg) {
        this.cfg = cfg;
        builder = JDABuilder.createDefault(cfg.getDiscordApiKey());
        builder.setBulkDeleteSplittingEnabled(false);
        builder.setActivity(Activity.watching("your packages"));
        builder.setStatus(OnlineStatus.ONLINE);

    }

    public Bot registerListeners(Class<?>... clazz) {
        try {
            for (Class<?> c : clazz) {
                builder.addEventListeners(c.getDeclaredConstructor().newInstance());
            }
        } catch (Exception ignored) {}
        return this;
    }

    public void connect() {
        try {
            long startTime = System.currentTimeMillis();

            Logger.Warn("Connecting...");
            jda = builder.build();

            jda.awaitReady();


            AccountManager acc = jda.getSelfUser().getManager().setName("Postimies");
            try {
                jda.getSelfUser().getManager().setAvatar(Icon.from(
                        new URL("https://cdn.nat.gs/img/Postimies_logo.png").openStream()
                )).queue();
            } catch (Exception ignored) {
                Logger.Error("Failed to flech profile picture from server!");
            }

            Connected = true;

            long timeTook = System.currentTimeMillis() - startTime;
            Logger.Warn("Connected (" + timeTook + "ms)!");
            Utils.sendStatus(Utils.Status.Online);

        } catch (LoginException | InterruptedException e) {
            Logger.Error(e.getMessage());
        }
    }

    public void disconnect() {
        Logger.Warn("Disconnecting...");
        Utils.sendStatus(Utils.Status.ShuttingDown); //sends message and shutsdown bot
    }


}
