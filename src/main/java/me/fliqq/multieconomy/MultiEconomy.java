package me.fliqq.multieconomy;

import org.bukkit.plugin.java.JavaPlugin;

import me.fliqq.multieconomy.command.AddBalanceCommand;
import me.fliqq.multieconomy.command.BalanceCommand;
import me.fliqq.multieconomy.command.CommandManager;
import me.fliqq.multieconomy.command.PayCommand;
import me.fliqq.multieconomy.command.WithdrawCommand;
import me.fliqq.multieconomy.config.ConfigManager;
import me.fliqq.multieconomy.data.DataStorage;
import me.fliqq.multieconomy.data.StorageFactory;
import me.fliqq.multieconomy.hook.VaultHook;
import me.fliqq.multieconomy.listener.CurrencyNoteListener;
import me.fliqq.multieconomy.listener.PlayerEventListener;
import me.fliqq.multieconomy.manager.CurrencyManager;
import me.fliqq.multieconomy.manager.CurrencyNoteManager;
import me.fliqq.multieconomy.manager.PlayerDataManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.ServicePriority;

public class MultiEconomy extends JavaPlugin {

    private ConfigManager configManager;
    private DataStorage storage;
    private PlayerDataManager playerDataManager;
    private CommandManager commandManager;
    private VaultHook vaultHook;
    private CurrencyNoteManager currencyNoteManager;

    @Override
    public void onEnable() {
        configManager = ConfigManager.getInstance();
        configManager.initialize(this);
    
        // Initialize Currency Manager
        CurrencyManager.initialize(this);

        // Load balances Storage based on config db choice
        storage = StorageFactory.createStorage(this);
        // Initialize PlayerDataManager
        playerDataManager = new PlayerDataManager(storage);

        // Check if Vault integration is enabled in config
        if (configManager.getVault()) {
            if (setupEconomy()) {
                getLogger().info("Vault integration successful.");
            } else {
                getLogger().warning("Vault not found! Economy features will be limited. If you do not use it, disable it in the config.");
            }
        } else {
            getLogger().info("Vault integration disabled in config.");
        }
        
        //CurrencyNoteManager
        currencyNoteManager = new CurrencyNoteManager(this);

        // Register events
        getServer().getPluginManager().registerEvents(new PlayerEventListener(playerDataManager), this);
        getServer().getPluginManager().registerEvents(new CurrencyNoteListener(currencyNoteManager, vaultHook), this);


        // Initialize Command Manager
        this.commandManager = new CommandManager();
    
        commandManager.registerCommand("addbalance", new AddBalanceCommand(vaultHook));
        commandManager.registerCommand("balance", new BalanceCommand(vaultHook));
        commandManager.registerCommand("pay", new PayCommand(vaultHook));
        commandManager.registerCommand("withdraw", new WithdrawCommand(currencyNoteManager, vaultHook));
        
        // Set command executors      
        getCommand("balance").setExecutor(commandManager);      
        getCommand("addbalance").setExecutor(commandManager);
        getCommand("pay").setExecutor(commandManager);
        getCommand("withdraw").setExecutor(commandManager);


    
        messages();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        vaultHook = new VaultHook(playerDataManager);
        getServer().getServicesManager().register(Economy.class, vaultHook, this, ServicePriority.Highest);
        return true;
    }
    

    private void messages() {
        getLogger().info("***********");
        getLogger().info("MultiEconomy 1.0 enabled");
        getLogger().info("Plugin by Fliqqq");
        getLogger().info("***********");
    }

    public static MultiEconomy getInstance() {
        return getPlugin(MultiEconomy.class);
    }
}
