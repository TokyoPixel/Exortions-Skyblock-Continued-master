package com.skyblock.skyblock.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Getter
@AllArgsConstructor
public class SkyblockPlayerMoveEvent extends SkyblockEvent {
    private final Player player;
    private final Location from;
    private final Location to;

}
