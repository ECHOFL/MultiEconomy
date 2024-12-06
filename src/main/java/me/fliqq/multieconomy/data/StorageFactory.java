package me.fliqq.multieconomy.data;

import me.fliqq.multieconomy.MultiEconomy;
import me.fliqq.multieconomy.data.JSON.JSONStorage;
import me.fliqq.multieconomy.data.YAML.YAMLStorage;

public class StorageFactory {
    public static DataStorage createStorage(MultiEconomy plugin){
        String storageType = plugin.getConfig().getString("storage.type", "YAML");
        switch (storageType.toUpperCase()) {
            case "YAML":
                return new YAMLStorage(plugin);
            case "JSON":
                return new JSONStorage(plugin);
            case "MYSQL":
                //TO IMPLEMENT
                break;
            case "SQLITE":
                //TO IMPLEMENT
                break;
            default:
                plugin.getLogger().warning("Invalid storage type. Defaulting to YAML.");
                return new YAMLStorage(plugin);
        }
        return new YAMLStorage(plugin); //default
    }
}
