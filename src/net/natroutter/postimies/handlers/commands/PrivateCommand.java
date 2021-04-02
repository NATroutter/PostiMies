package net.natroutter.postimies.handlers.commands;

import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;


public interface PrivateCommand {

    public void onCommand(User sender, PrivateChannel channel, String command, String[] args);

}
