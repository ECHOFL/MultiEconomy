package me.fliqq.multieconomy.manager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import me.fliqq.multieconomy.MultiEconomy;
import me.fliqq.multieconomy.object.Currency;
import net.md_5.bungee.api.ChatColor;

public class CurrencyNoteManager {
    private final CurrencyManager currencyManager = CurrencyManager.getInstance();
    private final NamespacedKey amountKey;
    private final NamespacedKey currencyKey;
    private final NamespacedKey uniqueIdKey;

    public CurrencyNoteManager(MultiEconomy plugin){
        this.amountKey=new NamespacedKey(plugin, "currency_note_amount");
        this.currencyKey=new NamespacedKey(plugin, "currency_note_currency");
        this.uniqueIdKey=new NamespacedKey(plugin, "currency_note_uuid");
    }
    
    @SuppressWarnings("deprecation")
    public ItemStack createCurrencyNote(BigDecimal amount, String currencyId){
        Currency currency = currencyManager.getCurrency(currencyId);
        if(currency == null || !currency.isWithdrawable()){
            throw new IllegalArgumentException("Invalid or non-withdrawable currency: "+ currencyId);
        } 
        Material material = Material.valueOf(currency.getWithdrawItem());
        ItemStack note = new ItemStack(material);
        ItemMeta meta = note.getItemMeta();

        if(meta != null){
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', currency.getWithdrawName()));

            List<String> lore = new ArrayList<>();
            for(String loreLine : currency.getWithdrawLore()){
                loreLine = loreLine.replace("{amount}", amount.toString()).replace("{currency}", currency.getName());
                lore.add(ChatColor.translateAlternateColorCodes('&', loreLine));
            }
            meta.setLore(lore);
            meta.getPersistentDataContainer().set(amountKey, PersistentDataType.STRING, amount.toString());
            meta.getPersistentDataContainer().set(currencyKey, PersistentDataType.STRING, currencyId);
            meta.getPersistentDataContainer().set(uniqueIdKey, PersistentDataType.STRING, UUID.randomUUID().toString());

            note.setItemMeta(meta);
        }
        return note;
    }

    public boolean isCurrencyNote(ItemStack item){
        if(item==null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(amountKey, PersistentDataType.STRING);
    }
    public BigDecimal getNoteAmount(ItemStack note){
        if(!isCurrencyNote(note)) return BigDecimal.ZERO;
        String amountStr = note.getItemMeta().getPersistentDataContainer().get(amountKey, PersistentDataType.STRING);
        return new BigDecimal(amountStr);
    }
    public String getNoteCurrency(ItemStack note){
        if(!isCurrencyNote(note)) return null;
        return note.getItemMeta().getPersistentDataContainer().get(currencyKey, PersistentDataType.STRING);
    }
}
