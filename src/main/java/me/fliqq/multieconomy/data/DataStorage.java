package me.fliqq.multieconomy.data;

import java.util.UUID;

public interface DataStorage {
    void savePlayerData(UUID playerUUID, PlayerData data);
    PlayerData loadPlayerData(UUID playerUUID);
    boolean hasPlayerData(UUID playerUUID);
}

