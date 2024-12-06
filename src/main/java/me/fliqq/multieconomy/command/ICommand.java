package me.fliqq.multieconomy.command;

import org.bukkit.command.CommandSender;

public interface ICommand {
    boolean execute(CommandSender sender, String[] args);
    String getPermission();
    String getUsage();
    String getDescription();
}
