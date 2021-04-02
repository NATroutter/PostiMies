package net.natroutter.postimies.Utilities;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;

public class Config {

    private Gson gson = new Gson();
    private Cfg cfg;
    private FileManager fm;

    public Config(String fileName) {
        this.fm = new FileManager("", fileName);
        cfg = gson.fromJson(fm.getFile(), Cfg.class);
    }

    public String getDiscordApiKey() {
        return cfg.DiscordApiKey;
    }
    public String getTrackingMoreApiKey() {
        return cfg.TrackingMoreApiKey;
    }
    public String getCommandPrefix() {
        return cfg.CommandPrefix;
    }
    public boolean getRemoveWhenAraived() { return cfg.RemoveWhenAraived; }

    public List<Whitelisted> getWhitelisted() {
        return cfg.Whitelist;
    }


    public static class Cfg {
        private String DiscordApiKey = "";
        private String TrackingMoreApiKey = "";
        private String CommandPrefix = "";
        private boolean RemoveWhenAraived = true;
        private List<Whitelisted> Whitelist = new ArrayList<>();
    }

    public static class Whitelisted {
        public String UserName = "";
        public Long UserID = 0L;
    }

}