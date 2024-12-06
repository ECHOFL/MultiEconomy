package me.fliqq.multieconomy.command;

import java.math.BigDecimal;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.fliqq.multieconomy.hook.VaultHook;
import me.fliqq.multieconomy.manager.CurrencyManager;
import me.fliqq.multieconomy.object.Currency;
import net.milkbowl.vault.economy.EconomyResponse;

public class PayCommand implements ICommand {
    private final CurrencyManager currencyManager = CurrencyManager.getInstance();
    private final VaultHook vaultHook;

    public PayCommand(VaultHook vaultHook) {
        this.vaultHook = vaultHook;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
    
        Player payer = (Player) sender;
    
        // Check for correct number of arguments
        if (args.length < 2 || args.length > 3) {
            sender.sendMessage("Usage: /pay <player> <amount> OR /pay <player> <currency> <amount>");
            return true;
        }
    
        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            sender.sendMessage("Player " + args[0] + " is not online.");
            return true;
        }
    
        String currencyId;
        BigDecimal amount;
        Currency currency;
    
        try {
            if (args.length == 2) {
                // Default currency
                currency = currencyManager.getDefaultCurrency();
                currencyId = currency.getId();
                amount = new BigDecimal(args[1]);
            } else {
                // Specified currency
                currencyId = args[1];
                currency = currencyManager.getCurrency(currencyId);
                if (currency == null) {
                    sender.sendMessage("Currency " + currencyId + " does not exist.");
                    return true;
                }
                amount = new BigDecimal(args[2]); // Ensure args[2] is accessed only when args.length > 2
            }
            
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                sender.sendMessage("Amount must be greater than zero.");
                return true;
            }
    
            // Check if payer has enough balance
            BigDecimal payerBalance = new BigDecimal(vaultHook.getBalance(payer, currencyId));
            if (payerBalance.compareTo(amount) < 0) {
                sender.sendMessage("You don't have enough " + currencyId + " to make this payment.");
                return true;
            }
    
            EconomyResponse response = vaultHook.withdrawPlayerCurrency(payer, CurrencyManager.getInstance().getCurrency(currencyId).getId(), amount.doubleValue());
            if (response.type == EconomyResponse.ResponseType.FAILURE) {
                payer.sendMessage(response.errorMessage);
                return true;
            }
    
            EconomyResponse response2 = vaultHook.depositPlayerCurrency(targetPlayer, CurrencyManager.getInstance().getCurrency(currencyId).getId(), amount.doubleValue());
            if (response2.type == EconomyResponse.ResponseType.FAILURE) {
                payer.sendMessage(response.errorMessage);
                return true;
            }
    
            // Provide feedback
            String symbol = currency.getSymbol();
            payer.sendMessage("You have sent " + amount + symbol + " " + currencyId + " to " + targetPlayer.getName() + ".");
            targetPlayer.sendMessage("You have received " + amount + symbol + " " + currencyId + " from " + payer.getName() + ".");
    
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid amount specified. Please enter a valid number.");
        } catch (ArrayIndexOutOfBoundsException e) {
            sender.sendMessage("An unexpected error occurred. Please check your command format.");
        }
    
        return true;
    }

    @Override
    public String getPermission() {
        return "multieconomy.pay";
    }

    @Override
    public String getUsage() {
        return "/pay <player> <amount> OR /pay <player> <currency> <amount>";
    }

    @Override
    public String getDescription() {
        return "Pay another player using the default or a specific currency.";
    }
}
