package me.fliqq.multieconomy.data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.gson.annotations.Expose;

import me.fliqq.multieconomy.manager.CurrencyManager;
import me.fliqq.multieconomy.object.Currency;

public class PlayerData {
    private final UUID uuid;

    @Expose
    private final Map<String, BigDecimal> balances = new HashMap<>();

    private final CurrencyManager currencyManager = CurrencyManager.getInstance();

    public PlayerData(UUID uuid){
        this.uuid = uuid;
        initializeBalances(currencyManager);
    }

    private void initializeBalances(CurrencyManager currencyManager) {
        for (Currency currency : currencyManager.getAllCurrencies().values()) {
            balances.put(currency.getId(), currency.getStartingBalance());
        }
    }

    public UUID getUuid(){return uuid;}
    public Player getPlayer(){return Bukkit.getPlayer(uuid);}
    public String getName(){return getPlayer().toString();}

    public BigDecimal getBalance(String currencyId){
        return balances.getOrDefault(currencyId, BigDecimal.ZERO);
    }
    public void setBalance(String currencyId, BigDecimal amount){
        balances.put(currencyId, amount);
    }
    public Map<String, BigDecimal> getBalances(){
        return balances;
    }

    
}
