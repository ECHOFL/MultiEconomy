package me.fliqq.multieconomy.command;

import java.math.BigDecimal;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.fliqq.multieconomy.hook.VaultHook;
import me.fliqq.multieconomy.manager.CurrencyManager;
import me.fliqq.multieconomy.object.Currency;
import net.milkbowl.vault.economy.EconomyResponse;

public class AddBalanceCommand implements ICommand {
    private final CurrencyManager currencyManager = CurrencyManager.getInstance();
    private final VaultHook vaultHook;

    public AddBalanceCommand(VaultHook vaultHook) {
        this.vaultHook = vaultHook;
    }
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
    
        // Check for correct number of arguments
        if (args.length < 3) { // Ensure there are at least three arguments
            sender.sendMessage("Usage: /addbalance <player> <currency> <amount>");
            return true;
        }
    
        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            sender.sendMessage("Player " + args[0] + " is not online.");
            return true;
        }
    
        String currencyId = args[1];
        BigDecimal amount;
    
        try {
            amount = new BigDecimal(args[2]); // Access args[2] only when args.length >= 3
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                sender.sendMessage("Amount must be greater than zero.");
                return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid amount specified. Please enter a valid number.");
            return true;
        }
    
        // Check if currency exists in CurrencyManager
        Currency currency = currencyManager.getCurrency(currencyId);
        if (currency == null) {
            sender.sendMessage("Currency " + currencyId + " does not exist.");
            return true;
        }
    
        // Update Vault balance
        EconomyResponse response = vaultHook.depositPlayerCurrency(targetPlayer, currency.getId(), amount.doubleValue());
        
        if (response.type == EconomyResponse.ResponseType.FAILURE) {
            sender.sendMessage(response.errorMessage);
            return true;
        }
    
        // Provide feedback
        String symbol = currency.getSymbol();
        targetPlayer.sendMessage("Your balance of " + currencyId + " has been increased by " + amount + symbol + ".");
        sender.sendMessage("Successfully added " + amount + " " + currencyId + " to " + targetPlayer.getName() + "'s balance.");
        
        return true;
    }
    
    

    @Override
    public String getPermission() {
        return "multieconomy.addbalance"; // Define permission for this command
    }

    @Override
    public String getUsage() {
        return "/addbalance <player> <currency> <amount>";
    }

    @Override
    public String getDescription() {
        return "Add a specified amount to a player's balance for a specific currency.";
    }
}
