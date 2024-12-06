package me.fliqq.multieconomy.manager;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


import me.fliqq.multieconomy.data.DataStorage;
import me.fliqq.multieconomy.data.PlayerData;

public class PlayerDataManager {
    private final Map<UUID, PlayerData> playerDataMap = new HashMap<>();
    private final DataStorage storage;

    public PlayerDataManager(DataStorage storage){
        this.storage=storage;
    }

    public void loadPlayerData(UUID playerUuid){
        PlayerData data = storage.loadPlayerData(playerUuid);
        if(data==null){
            data=new PlayerData(playerUuid);
        }
        playerDataMap.put(playerUuid, data);
    }
    public void savePlayerData(UUID playerUuid){
        PlayerData data = playerDataMap.get(playerUuid);
        if(data != null){
            storage.savePlayerData(playerUuid, data);
        }
    }

    public void saveAllData(){
        for(Map.Entry<UUID, PlayerData> entry : playerDataMap.entrySet()){
            storage.savePlayerData(entry.getKey(), entry.getValue());
        }
    }
    public void unloadPlayerData(UUID playerUuid){
        savePlayerData(playerUuid);
        playerDataMap.remove(playerUuid);
    }

    public BigDecimal getBalance(UUID playerUuid, String currencyId) {
        PlayerData data = playerDataMap.get(playerUuid);
        if(data!=null){
            return data.getBalance(currencyId);
        }
        return BigDecimal.ZERO;
    }

    
    public void setBalance(UUID playerUuid, String currencyId, BigDecimal newAmount) {
        PlayerData data = playerDataMap.get(playerUuid);
        if (data != null) {
            data.setBalance(currencyId, newAmount);
        }
    }

    public boolean hasPlayerData(UUID playerUuid){
        return playerDataMap.containsKey(playerUuid);
    }
}
