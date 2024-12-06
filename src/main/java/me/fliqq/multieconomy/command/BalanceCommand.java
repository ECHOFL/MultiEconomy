package me.fliqq.multieconomy.command;

import java.math.BigDecimal;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.fliqq.multieconomy.hook.VaultHook;
import me.fliqq.multieconomy.manager.CurrencyManager;
import me.fliqq.multieconomy.object.Currency;

public class BalanceCommand implements ICommand {
    private final CurrencyManager currencyManager = CurrencyManager.getInstance();
    private final VaultHook vaultHook;

    public BalanceCommand(VaultHook vaultHook) {
        this.vaultHook = vaultHook;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        Currency defaultCurrency = currencyManager.getDefaultCurrency();

        if (defaultCurrency == null) {
            sender.sendMessage("No currencies are defined in the system.");
            return true;
        }

        String currencyId = args.length > 0 ? args[0] : defaultCurrency.getId();

        // Check if the specified currency exists
        Currency currency = currencyManager.getCurrency(currencyId);
        if (currency == null) {
            player.sendMessage("Invalid currency specified: " + currencyId);
            return true;
        }

        // Get internal balance
        BigDecimal balance = BigDecimal.valueOf(vaultHook.getBalance(player, currencyId));

        // Display internal balance
        player.sendMessage("Your balance of " + currencyId + " is: " + balance);

        return true;
    }

    @Override
    public String getPermission() {
        return null; 
    }

    @Override
    public String getUsage() {
        return "/balance [currency]";
    }

    @Override
    public String getDescription() {
        return "Check your balance for a specific currency";
    }
}
