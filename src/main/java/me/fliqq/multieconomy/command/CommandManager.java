package me.fliqq.multieconomy.command;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandManager implements CommandExecutor {
    private final Map<String, ICommand> commands = new HashMap<>();

    public void registerCommand(String name, ICommand command) {
        commands.put(name.toLowerCase(), command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ICommand cmd = commands.get(command.getName().toLowerCase());
        if (cmd == null) {
            return false;
        }
        if (cmd.getPermission() != null && !sender.hasPermission(cmd.getPermission())) {
            sender.sendMessage("You don't have permission to use this command.");
            return true;
        }
        return cmd.execute(sender, args);
    }
}

