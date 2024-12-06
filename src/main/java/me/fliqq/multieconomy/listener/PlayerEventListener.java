package me.fliqq.multieconomy.listener;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.fliqq.multieconomy.manager.PlayerDataManager;

public class PlayerEventListener implements Listener{
    
    private final PlayerDataManager playerDataManager;
    public PlayerEventListener(PlayerDataManager playerDataManager){
        this.playerDataManager=playerDataManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        UUID playerUUID = event.getPlayer().getUniqueId();
        playerDataManager.loadPlayerData(playerUUID);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        UUID playerUUID = event.getPlayer().getUniqueId();
        playerDataManager.unloadPlayerData(playerUUID);
    }
}
