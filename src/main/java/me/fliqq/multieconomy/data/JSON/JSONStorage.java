package me.fliqq.multieconomy.data.JSON;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import me.fliqq.multieconomy.MultiEconomy;
import me.fliqq.multieconomy.data.DataStorage;
import me.fliqq.multieconomy.data.PlayerData;

public class JSONStorage implements DataStorage {
    private final File file;
    private final Gson gson;
    private Map<UUID, PlayerData> data;
    
    public JSONStorage(MultiEconomy plugin){
        this.file = new File(plugin.getDataFolder(), "players.json");
        this.gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .setPrettyPrinting()
                    .create();
        this.data = new HashMap<>();
        loadFromFile();
    }

    private void loadFromFile(){
        if(!file.exists()){
            try {
                file.createNewFile();
                saveToFile(); //save empty structure file initialy
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<UUID, PlayerData>>(){}.getType();
            Map<UUID, PlayerData> loadedData = gson.fromJson(reader, type);
            if(loadedData != null){
                data = loadedData;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void saveToFile(){
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void savePlayerData(UUID playerUUID, PlayerData data) {
        this.data.put(playerUUID, data);
        saveToFile();
    }

    @Override
    public PlayerData loadPlayerData(UUID playerUUID) {
        return data.get(playerUUID);
    }

    @Override
    public boolean hasPlayerData(UUID playerUUID) {
        return data.containsKey(playerUUID);
    }
}
