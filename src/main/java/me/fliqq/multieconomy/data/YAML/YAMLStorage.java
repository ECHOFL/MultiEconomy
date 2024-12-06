package me.fliqq.multieconomy.data.YAML;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.fliqq.multieconomy.MultiEconomy;
import me.fliqq.multieconomy.data.DataStorage;
import me.fliqq.multieconomy.data.PlayerData;

public class YAMLStorage implements DataStorage {
    private final File file;
    private final FileConfiguration config;

    public YAMLStorage(MultiEconomy plugin){
        //Create playersdata.yml if do not exist.
        file = new File(plugin.getDataFolder(), "players.yml");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create players.yml");
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public void savePlayerData(UUID playerUUID, PlayerData data) {
        String path = "players." + playerUUID.toString();
        Bukkit.getLogger().info("Saving data for player: " + playerUUID.toString());
    
        // Save balances
        ConfigurationSection balanceSection = config.createSection(path + ".balances");
        for(Map.Entry<String, BigDecimal> entry : data.getBalances().entrySet()){
            balanceSection.set(entry.getKey(), entry.getValue().doubleValue());
            Bukkit.getLogger().info("Saving balance: " + entry.getKey() + " -> " + entry.getValue());
        }
        try {
            config.save(file);
            Bukkit.getLogger().info("Saved successfully.");
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to save player data.");
            e.printStackTrace();
        }
    }
    

    @Override
    public PlayerData loadPlayerData(UUID playerUUID) {
        String path = "players." + playerUUID.toString();
        if(!config.contains(path)){ return null;} //no data exists Create PlayerData ????
        PlayerData data = new PlayerData(playerUUID);
        //load balances
        ConfigurationSection balanceSection = config.getConfigurationSection(path + ".balances");
        if(balanceSection != null){
            for(String currencyId : balanceSection.getKeys(false)){
                double balance = balanceSection.getDouble(currencyId, 0.0);
                data.setBalance(currencyId, BigDecimal.valueOf(balance));
            }
        }
        return data;
    }

    @Override
    public boolean hasPlayerData(UUID playerUUID) {
        return config.contains("players." + playerUUID.toString());
    }

    
    
}
