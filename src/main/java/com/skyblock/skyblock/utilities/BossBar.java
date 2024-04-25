package com.skyblock.skyblock.utilities;

import com.skyblock.skyblock.Skyblock;
import lombok.Data;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

@Data
public class BossBar {

    private final Player player;
    private String message;
    private EntityWither wither;
    private BukkitRunnable updateTask;

    public BossBar(Player player) {
        this.player = player;
        this.message = "";
        this.updateTask = null;
    }

    public void update() {
        if (updateTask != null) {
            return;
        }

        updateTask = new BukkitRunnable() {
            @Override
            public void run() {
                Vector dir = player.getLocation().getDirection();
                Location loc = player.getLocation().add(dir.multiply(40));
                reset();

                WorldServer world = ((CraftWorld) loc.getWorld()).getHandle();
                wither = new EntityWither(world);
                wither.setSize(1.0f, 1.0f);
                wither.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getPitch(), loc.getYaw());
                wither.setCustomName(message);
                wither.setInvisible(true);

                PacketPlayOutSpawnEntityLiving spawnPacket = new PacketPlayOutSpawnEntityLiving(wither);
                PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(wither.getId(), wither.getDataWatcher(), true);

                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(spawnPacket);
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(metadataPacket);

                updateTask = null;
            }
        };


        updateTask.runTaskAsynchronously(Skyblock.getPlugin());
    }

    public void reset() {
        if (wither == null) {
            return;
        }

        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(wither.getId());
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        wither = null;
    }
}