package me.fliqq.multieconomy.manager;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.fliqq.multieconomy.MultiEconomy;
import me.fliqq.multieconomy.object.Currency;

public class CurrencyManager {
    //DEFAULTS VALUES
    private static final String DEFAULT_SYMBOL = "$";
    private static final BigDecimal DEFAULT_STARTING_BALANCE = BigDecimal.ZERO;
    private static final boolean DEFAULT_IS_DEFAULT = false;
    private static final boolean DEFAULT_TRANSFERABLE = true;
    private static final boolean DEFAULT_WITHDRAWABLE = true;
    private static final String DEFAULT_WITHDRAW_ITEM = "PAPER";
    private static final String DEFAULT_WITHDRAW_NAME = "&6{currency} Note";
    private static final String[] DEFAULT_WITHDRAW_LORE = {"&7Amount: &e{amount}", "&7Currency: &e{currency}"};

    private static CurrencyManager INSTANCE;
    private final Map<String, Currency> currencies;
    private final MultiEconomy plugin;
    private final File currencyFile;
    private FileConfiguration currencyConfig;

    private CurrencyManager(MultiEconomy plugin){
        this.plugin=plugin;
        this.currencies= new HashMap<>();
        this.currencyFile=new File(plugin.getDataFolder(), "currencies.yml");
        loadCurrencyConfig();
        loadCurrencies();
    }
    public static void initialize(MultiEconomy plugin){
        if(INSTANCE==null){
            INSTANCE = new CurrencyManager(plugin);
        }
    }

    public static CurrencyManager getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("CurrencyManager has not been initialized");
        }
        return INSTANCE;
    }
    private void loadCurrencyConfig(){
        if(!currencyFile.exists()){
            plugin.saveResource("currencies.yml",false);
        }
        currencyConfig = YamlConfiguration.loadConfiguration(currencyFile);
    }

    public void loadCurrencies() {
        currencies.clear();
        ConfigurationSection currencySection = currencyConfig.getConfigurationSection("currencies");
        if (currencySection == null) {
            throw new IllegalStateException("No currencies defined in currencies.yml");
        }
        
        for (String currencyId : currencySection.getKeys(false)) {
            ConfigurationSection currencyConfig = currencySection.getConfigurationSection(currencyId);
            if (currencyConfig == null) continue;

            String name = currencyConfig.getString("name", currencyId);
            String symbol = currencyConfig.getString("symbol", DEFAULT_SYMBOL);
            BigDecimal startingBalance = BigDecimal.valueOf(currencyConfig.getDouble("starting-balance", DEFAULT_STARTING_BALANCE.doubleValue()));
            boolean isDefault = currencyConfig.getBoolean("is-default", DEFAULT_IS_DEFAULT);
            boolean transferable = currencyConfig.getBoolean("transferable", DEFAULT_TRANSFERABLE);
            boolean withdrawable = currencyConfig.getBoolean("withdrawable", DEFAULT_WITHDRAWABLE);
            String withdrawItem = currencyConfig.getString("withdraw.item", DEFAULT_WITHDRAW_ITEM);
            String withdrawName = currencyConfig.getString("withdraw.name", DEFAULT_WITHDRAW_NAME);
            String[] withdrawLore = currencyConfig.getStringList("withdraw.lore").toArray(new String[0]);
            if (withdrawLore.length == 0) {
                withdrawLore = DEFAULT_WITHDRAW_LORE;
            }

            Currency currency = new Currency(currencyId, name, symbol, startingBalance, isDefault, transferable, withdrawable, withdrawItem, withdrawName, withdrawLore);
            currencies.put(currencyId, currency);
        }
        if (currencies.isEmpty()) throw new IllegalStateException("No valid currencies found in currencies.yml");
    }
    


    public Currency getCurrency(String id){
        return currencies.get(id);
    }
    public Currency getDefaultCurrency() {
        for (Currency currency : currencies.values()) {
            if (currency.isDefault()) {
                return currency;
            }
        }
        return currencies.isEmpty() ? null : currencies.values().iterator().next();
    }

    public Map<String, Currency> getAllCurrencies(){
        return new HashMap<>(currencies);
    }
    public boolean currencyExists(String currencyId) {
        return currencies.containsKey(currencyId);
    }

    public void saveCurrencies() {
        for (Currency currency : currencies.values()) {
            String path = "currencies." + currency.getId() + ".";
            currencyConfig.set(path + "name", currency.getName());
            currencyConfig.set(path + "symbol", currency.getSymbol());
            currencyConfig.set(path + "starting-balance", currency.getStartingBalance().doubleValue());
            currencyConfig.set(path + "is-default", currency.isDefault());
            currencyConfig.set(path + "transferable", currency.isTransferable());
            currencyConfig.set(path + "withdrawable", currency.isWithdrawable());
            currencyConfig.set(path + "withdraw.item", currency.getWithdrawItem());
            currencyConfig.set(path + "withdraw.name", currency.getWithdrawName());
            currencyConfig.set(path + "withdraw.lore", currency.getWithdrawLore());
        }
        try {
            currencyConfig.save(currencyFile);
        } catch (Exception e) {
            plugin.getLogger().severe("Could not save currencies.yml");
            e.printStackTrace();
        }
    }
}
