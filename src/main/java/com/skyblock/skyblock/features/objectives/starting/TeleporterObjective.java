package com.skyblock.skyblock.features.objectives.starting;

import com.skyblock.skyblock.events.SkyblockPlayerMoveEvent;
import com.skyblock.skyblock.features.objectives.Objective;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class TeleporterObjective extends Objective {
    public TeleporterObjective() {
        super("teleporter", "Use the teleporter");
    }

    @EventHandler
    public void onMove(SkyblockPlayerMoveEvent e) {
        Location from = e.getFrom();
        Location to = e.getTo();
        if (to == null || from.getBlockX() != to.getBlockX()
                || from.getBlockY() != to.getBlockY()
                || from.getBlockZ() != to.getBlockZ()) {
            if (!isThisObjective(e.getPlayer())) return;

            if (e.getTo().getBlock().getType().equals(Material.PORTAL)) complete(e.getPlayer());
        }
    }
}
