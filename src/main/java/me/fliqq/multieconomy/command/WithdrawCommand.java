package me.fliqq.multieconomy.command;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.fliqq.multieconomy.manager.CurrencyManager;
import me.fliqq.multieconomy.manager.CurrencyNoteManager;
import me.fliqq.multieconomy.object.Currency;
import me.fliqq.multieconomy.hook.VaultHook;
import net.milkbowl.vault.economy.EconomyResponse;

public class WithdrawCommand implements ICommand {
    private final CurrencyNoteManager currencyNoteManager;
    private final VaultHook vaultHook;

    public WithdrawCommand(CurrencyNoteManager currencyNoteManager, VaultHook vaultHook) {
        this.currencyNoteManager = currencyNoteManager;
        this.vaultHook = vaultHook;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1 || args.length > 2) {
            player.sendMessage("Usage: /withdraw <amount> [<currency>]");
            return true;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(args[0]);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                player.sendMessage("Amount must be greater than zero.");
                return true;
            }
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid amount specified.");
            return true;
        }

        String currencyId;
        if (args.length == 2) {
            currencyId = args[1];
            if (!CurrencyManager.getInstance().currencyExists(currencyId)) {
                player.sendMessage("Invalid currency specified.");
                return true;
            }
        } else {
            Currency defaultCurrency = CurrencyManager.getInstance().getDefaultCurrency();
            if (defaultCurrency == null) {
                player.sendMessage("No default currency set. Please specify a currency.");
                return true;
            }
            currencyId = defaultCurrency.getId();
        }

        try {
            ItemStack currencyNote = currencyNoteManager.createCurrencyNote(amount, currencyId);

            if (player.getInventory().firstEmpty() == -1) {
                player.sendMessage("Your inventory is full.");
                return true;
            }

            // Update balance
            EconomyResponse response = vaultHook.withdrawPlayerCurrency(player, CurrencyManager.getInstance().getCurrency(currencyId).getId(), amount.doubleValue());
            if (response.type == EconomyResponse.ResponseType.FAILURE) {
                player.sendMessage(response.errorMessage);
                return true;
            }
            /*if (currencyId.equals(CurrencyManager.getInstance().getDefaultCurrency().getId())) {
                // Use VaultHook for default currency
                EconomyResponse response = vaultHook.withdrawPlayer(player, amount.doubleValue());
                if (response.type == EconomyResponse.ResponseType.FAILURE) {
                    player.sendMessage(response.errorMessage);
                    return true;
                }
            } else {
                // Use playerDataManager for other currencies
                BigDecimal balance = playerDataManager.getBalance(player.getUniqueId(), currencyId);
                if (balance.compareTo(amount) < 0) {
                    player.sendMessage("Insufficient funds.");
                    return true;
                }
                playerDataManager.setBalance(player.getUniqueId(), currencyId, balance.subtract(amount));
            }*/

            // Add custom lore with player name and date
            ItemMeta meta = currencyNote.getItemMeta();
            if (meta != null) {
                List<String> lore = meta.getLore();
                if (lore != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dateIssued = sdf.format(new Date());
                    lore.add(ChatColor.GRAY + "Issued by: " + player.getName());
                    lore.add(ChatColor.GRAY + "Date: " + dateIssued);
                    meta.setLore(lore);
                    currencyNote.setItemMeta(meta);
                }
            }

            player.getInventory().addItem(currencyNote);
            player.sendMessage("Successfully withdrawn " + amount + " " + currencyId + ".");
        } catch (IllegalArgumentException e) {
            player.sendMessage(e.getMessage());
        }

        return true;
    }

    @Override
    public String getPermission() {
        return "multieconomy.withdraw";
    }

    @Override
    public String getUsage() {
        return "/withdraw <amount> [<currency>]";
    }

    @Override
    public String getDescription() {
        return "Withdraw a specified amount of the default or a specific currency.";
    }
}
