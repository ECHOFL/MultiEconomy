package me.fliqq.multieconomy.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.fliqq.multieconomy.MultiEconomy;

public class ConfigManager {

    private static ConfigManager INSTANCE;
    private MultiEconomy plugin;
    private FileConfiguration config;
    private File configFile;

    private ConfigManager(){
    }
    public static synchronized ConfigManager getInstance(){
        if(INSTANCE == null) INSTANCE = new ConfigManager();
        return INSTANCE;
    }

    public void initialize(MultiEconomy plugin) {
        if (this.plugin == null) {
            this.plugin = plugin;
            loadConfig();
        }
    }

    public void loadConfig(){
        if(configFile == null){
            configFile = new File(plugin.getDataFolder(), "config.yml");
        }
        if(!configFile.exists()){
            plugin.saveResource("config.yml", false);
        }
        config=YamlConfiguration.loadConfiguration(configFile);

        //LOOK FOR DEFAULTS FROM THE JAR
        Reader defConfigStream = new InputStreamReader(plugin.getResource("config.yml"), StandardCharsets.UTF_8);
        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
        config.setDefaults(defConfig);
    }

    public FileConfiguration getConfig(){
        if (config == null){
            loadConfig();
        }
        return config;
    }

    public void saveConfig(){
        if(config == null || configFile == null){
            return;
        }
        try {
            getConfig().save(configFile);
        } catch (IOException ex) {
            plugin.getLogger().severe("Could not save config to " + configFile);
        }
    }

    public void reloadConfig(){
        loadConfig();;
    }


    //METHOD FOR DATAS
    public String getStorageType(){
        return getConfig().getString("storage.type", "YAML");
    }
    public String getLanguage(){
        return getConfig().getString("settings.language", "en");
    }
    public boolean getVault(){
        return getConfig().getBoolean("settings.vault-integration", true);
    }
    
}
