package net.natroutter.postimies.Utilities;

import net.dv8tion.jda.api.EmbedBuilder;
import net.natroutter.postimies.Utilities.objects.StringArg;

import java.util.regex.Pattern;

public enum CmdError {
    NoPerm("⚠ No permissions!", "You do not have permissions to execute this command!"),
    TooManyArgs("⚠ Too many Arguments!", "You have entered too many arguments in command!"),
    TooFast("⚠ Too Fast!", "You are sending commands to fast wait ``{time}`` seconds"),
    InvalidArgs("⚠ Invalid Arguments!", "Arguments that you entered was invalid!"),
    ArgNotProvided("⚠ Invalid Arguments!", "You have not provided argument ``{arg}``");


    private String title;
    private String description;
    private EmbedBuilder base = Utils.EmbedBase();

    CmdError(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public EmbedBuilder get() {
        base.setTitle(this.title);
        base.setDescription(this.description);
        return base;
    }

    public EmbedBuilder get(StringArg arg) {
        base.setTitle(this.title);
        base.setDescription(this.description.replaceAll(Pattern.quote(arg.getPlaceholder()), arg.getValue()));
        return base;
    }
}