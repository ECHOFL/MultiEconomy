package me.fliqq.multieconomy.listener;

import java.math.BigDecimal;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.fliqq.multieconomy.hook.VaultHook;
import me.fliqq.multieconomy.manager.CurrencyNoteManager;
import net.milkbowl.vault.economy.EconomyResponse;

public class CurrencyNoteListener implements Listener {
    private CurrencyNoteManager currencyNoteManager;
    private VaultHook vaultHook;


    public CurrencyNoteListener(CurrencyNoteManager currencyNoteManager, VaultHook vaultHook) {
        this.currencyNoteManager = currencyNoteManager;
        this.vaultHook=vaultHook;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = event.getItem();
        
        if (item == null || !currencyNoteManager.isCurrencyNote(item)) return;

        event.setCancelled(true);

        BigDecimal amount = currencyNoteManager.getNoteAmount(item);
        String currencyId = currencyNoteManager.getNoteCurrency(item);
        
        // Update balance
        EconomyResponse response = vaultHook.depositPlayerCurrency(event.getPlayer(), currencyId, amount.doubleValue());
        
        if (response.type == EconomyResponse.ResponseType.FAILURE) {
            event.getPlayer().sendMessage(response.errorMessage);
        } else {     
             // Remove one item from stack
            item.setAmount(item.getAmount() - 1);
    
            event.getPlayer().sendMessage("You have redeemed " + amount + " " + currencyId + ".");}
    }
}
