package me.fliqq.multieconomy.hook;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.fliqq.multieconomy.MultiEconomy;
import me.fliqq.multieconomy.manager.CurrencyManager;
import me.fliqq.multieconomy.manager.PlayerDataManager;
import me.fliqq.multieconomy.object.Currency;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class VaultHook implements Economy{
    private final MultiEconomy plugin = MultiEconomy.getInstance();
    private final CurrencyManager currencyManager = CurrencyManager.getInstance();
    private final PlayerDataManager playerDataManager;

    public VaultHook(PlayerDataManager playerDataManager){
        this.playerDataManager=playerDataManager;
    }

    public EconomyResponse withdrawPlayerCurrency(OfflinePlayer player, String currencyId, double amount) {
        UUID playerUUID = player.getUniqueId();
        Currency defaultCurrency = currencyManager.getDefaultCurrency();
        
        if (currencyId.equals(defaultCurrency.getId())) {
            return withdrawPlayer(player, amount);
        } else {
            BigDecimal currentBalance = playerDataManager.getBalance(playerUUID, currencyId);
            
            if (currentBalance.compareTo(BigDecimal.valueOf(amount)) < 0) {
                return new EconomyResponse(0, currentBalance.doubleValue(), EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
            }
            
            BigDecimal newBalance = currentBalance.subtract(BigDecimal.valueOf(amount));
            playerDataManager.setBalance(playerUUID, currencyId, newBalance);
            
            return new EconomyResponse(amount, newBalance.doubleValue(), EconomyResponse.ResponseType.SUCCESS, null);
        }
    }
    
    public EconomyResponse depositPlayerCurrency(OfflinePlayer player, String currencyId, double amount) {
        UUID playerUUID = player.getUniqueId();
        Currency defaultCurrency = currencyManager.getDefaultCurrency();
        
        if (currencyId.equals(defaultCurrency.getId())) {
            return depositPlayer(player, amount);
        } else {
            BigDecimal currentBalance = playerDataManager.getBalance(playerUUID, currencyId);
            BigDecimal newBalance = currentBalance.add(BigDecimal.valueOf(amount));
            playerDataManager.setBalance(playerUUID, currencyId, newBalance);
            
            return new EconomyResponse(amount, newBalance.doubleValue(), EconomyResponse.ResponseType.SUCCESS, null);
        }
    }

    public EconomyResponse setBalance(OfflinePlayer player, String currencyId, double amount) {
        double currentBalance = playerDataManager.getBalance(player.getUniqueId(), currencyId).doubleValue();
        double difference = amount - currentBalance;
        
        if (difference > 0) {
            return depositPlayerCurrency(player, currencyId, difference);
        } else if (difference < 0) {
            return withdrawPlayerCurrency(player, currencyId, -difference);
        } else {
            return new EconomyResponse(0, currentBalance, EconomyResponse.ResponseType.SUCCESS, null);
        }
    }
    public double getBalance(Player player, String currencyId) {
        return playerDataManager.getBalance(player.getUniqueId(), currencyManager.getCurrency(currencyId).getId()).doubleValue();
    }

    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        UUID playerUUID = player.getUniqueId();
        BigDecimal currentBalance = playerDataManager.getBalance(playerUUID, currencyManager.getDefaultCurrency().getId());
        BigDecimal newBalance = currentBalance.add(BigDecimal.valueOf(amount));
        playerDataManager.setBalance(playerUUID, currencyManager.getDefaultCurrency().getId(), newBalance);
        
        return new EconomyResponse(amount, newBalance.doubleValue(), EconomyResponse.ResponseType.SUCCESS, null);
    }


    @Override
    public boolean isEnabled(){return plugin.isEnabled();}

    @Override
    public String getName() {return plugin.getName();}
    
    @Override
    public boolean hasBankSupport(){ return false;}

    @Override
    public int fractionalDigits(){ return 2;}

    @Override
    public String format(double amount){
        Currency defaultCurrency = currencyManager.getDefaultCurrency();
        return defaultCurrency != null ? defaultCurrency.getSymbol() + String.format("%.2f", amount) : String.valueOf(amount);
    }

    @Override
    public String currencyNamePlural(){
        Currency defaultCurrency = currencyManager.getDefaultCurrency();
        return defaultCurrency != null ? defaultCurrency.getName() + "s" : "Coins";//DEFAULT PLURAL
    }
    //ADD THE FUNCTIONALITY
    @Override
    public String currencyNameSingular() {
        Currency defaultCurrency = currencyManager.getDefaultCurrency();
        return defaultCurrency != null ? defaultCurrency.getName() : "Coin";//DEFAULT SINGULAR
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        return depositPlayer(Bukkit.getOfflinePlayer(playerName), amount);
    }

    @Override
    public EconomyResponse depositPlayer(String arg0, String arg1, double arg2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'depositPlayer'");
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer arg0, String arg1, double arg2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'depositPlayer'");
    }

    @Override
    public double getBalance(String playerName) {
        return getBalance(Bukkit.getOfflinePlayer(playerName));
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return playerDataManager.getBalance(player.getUniqueId(), currencyManager.getDefaultCurrency().getId()).doubleValue();
    }


    @Override
    public List<String> getBanks() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBanks'");
    }

    @Override
    public boolean has(String playerName, double amount) {
        return getBalance(playerName) >= amount;
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return has(player.getName(), amount);
    }

    @Override
    public boolean has(String arg0, String arg1, double arg2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'has'");
    }

    @Override
    public boolean has(OfflinePlayer arg0, String arg1, double arg2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'has'");
    }

    @Override
    public boolean hasAccount(String playerName) {
        return playerDataManager.hasPlayerData(Bukkit.getPlayerUniqueId(playerName));
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return playerDataManager.hasPlayerData(player.getUniqueId());
    }
    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        return withdrawPlayer(Bukkit.getOfflinePlayer(playerName), amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        UUID playerUUID = player.getUniqueId();
        BigDecimal currentBalance = playerDataManager.getBalance(playerUUID, currencyManager.getDefaultCurrency().getId());
        
        if (currentBalance.compareTo(BigDecimal.valueOf(amount)) < 0) {
            return new EconomyResponse(0, currentBalance.doubleValue(), EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
        }
        
        BigDecimal newBalance = currentBalance.subtract(BigDecimal.valueOf(amount));
        playerDataManager.setBalance(playerUUID, currencyManager.getDefaultCurrency().getId(), newBalance);
        
        return new EconomyResponse(amount, newBalance.doubleValue(), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse isBankMember(String arg0, String arg1) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isBankMember'");
    }

    @Override
    public EconomyResponse isBankMember(String arg0, OfflinePlayer arg1) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isBankMember'");
    }

    @Override
    public EconomyResponse isBankOwner(String arg0, String arg1) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isBankOwner'");
    }

    @Override
    public EconomyResponse isBankOwner(String arg0, OfflinePlayer arg1) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isBankOwner'");
    }

    @Override
    public EconomyResponse withdrawPlayer(String arg0, String arg1, double arg2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'withdrawPlayer'");
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer arg0, String arg1, double arg2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'withdrawPlayer'");
    }

    @Override
    public boolean hasAccount(String arg0, String arg1) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'hasAccount'");
    }

    @Override
    public boolean hasAccount(OfflinePlayer arg0, String arg1) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'hasAccount'");
    }
    @Override
    public EconomyResponse bankBalance(String arg0) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'bankBalance'");
    }

    @Override
    public EconomyResponse bankDeposit(String arg0, double arg1) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'bankDeposit'");
    }

    @Override
    public EconomyResponse bankHas(String arg0, double arg1) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'bankHas'");
    }

    @Override
    public EconomyResponse bankWithdraw(String arg0, double arg1) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'bankWithdraw'");
    }

    @Override
    public EconomyResponse createBank(String arg0, String arg1) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createBank'");
    }

    @Override
    public EconomyResponse createBank(String arg0, OfflinePlayer arg1) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createBank'");
    }

    @Override
    public boolean createPlayerAccount(String arg0) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createPlayerAccount'");
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer arg0) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createPlayerAccount'");
    }

    @Override
    public boolean createPlayerAccount(String arg0, String arg1) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createPlayerAccount'");
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer arg0, String arg1) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createPlayerAccount'");
    }

    @Override
    public EconomyResponse deleteBank(String arg0) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteBank'");
    }
    @Override
    public double getBalance(String arg0, String arg1) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBalance'");
    }


    @Override
    public double getBalance(OfflinePlayer arg0, String arg1) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBalance'");
    }

    
}