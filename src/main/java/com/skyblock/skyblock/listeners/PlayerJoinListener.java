package com.skyblock.skyblock.listeners;

import com.skyblock.skyblock.utilities.Util;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


public class PlayerJoinListener implements Listener {

    @EventHandler
    public void giveSkyblockMenu(PlayerJoinEvent event) {
        event.getPlayer().getInventory().setItem(8, Util.createSkyblockMenu());
        event.setJoinMessage(null);
    }



}
