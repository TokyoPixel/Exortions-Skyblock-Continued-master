package com.skyblock.skyblock.listeners;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.island.SkyblockIsland;
import com.skyblock.skyblock.features.minions.MinionBase;
import com.skyblock.skyblock.utilities.Util;
import net.citizensnpcs.npc.ai.speech.Chat;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;

public class BlockListener implements Listener {

    @EventHandler(priority=EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        System.out.println(Skyblock.getPlugin().getItemHandler().getPlaceableItems().size());
        Material previous = event.getBlock().getType();

        event.setCancelled(SkyblockPlayer.getPlayer(event.getPlayer()).isNotOnPrivateIsland());

        if (!SkyblockPlayer.getPlayer(event.getPlayer()).isNotOnPrivateIsland()) {
            ItemStack item = event.getItemInHand();

            if (item.getItemMeta().hasDisplayName()) {
                String display = event.getItemInHand().getItemMeta().getDisplayName();

                if (item.getItemMeta().getDisplayName().contains("Minion")) {
                    SkyblockPlayer player = SkyblockPlayer.getPlayer(event.getPlayer());
                    SkyblockIsland island = SkyblockIsland.getIsland(event.getPlayer().getUniqueId());

                    int minionsPlaced = ((List<Object>) island.getValue("island.minions")).size();
                    int minionSlots = (int) island.getValue("island.minion.slots");

                    if (minionsPlaced + 1 > minionSlots) {
                        event.setCancelled(true);
                        event.getBlock().setType(previous);
                        event.getPlayer().sendMessage(ChatColor.RED + "You have reached the maximum amount of minions you can place! (" + minionSlots + ")");
                        return;
                    }

                    Location spawnAt = event.getBlock().getLocation().add(0.5, 0, 0.5);

                    spawnAt.setPitch(event.getPlayer().getLocation().getPitch() * -1);

                    Reflections reflections = new Reflections("com.skyblock.skyblock.features.minions.types");
                    Set<Class<? extends MinionBase>> minions = reflections.getSubTypesOf(MinionBase.class);

                    MinionBase minion = null;

                    String minionToPlace = ChatColor.stripColor(WordUtils.capitalize(display.split(" Minion")[0]).replace(" ", ""));

                    for (Class<? extends MinionBase> minionClass : minions) {
                        if (minionClass.getSimpleName().equals(minionToPlace + "Minion")) {
                            try {
                                minion = minionClass.getDeclaredConstructor().newInstance();
                            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                                     InvocationTargetException ex) {
                                System.out.println("[DEBUG] unable to create instance of minion");
                                player.getBukkitPlayer().sendMessage(ChatColor.RED + "Could not place minion: " + ex.getMessage());
                                event.setCancelled(true);
                                return;
                            }
                        }
                    }

                    if (minion == null) {
                        System.out.println("minions size : " + minions.size());
                        System.out.println("[DEBUG] Minion is null");
                        player.getBukkitPlayer().sendMessage(ChatColor.RED + "Could not place minion: Minion not found!");
                        event.setCancelled(true);
                        return;
                    }

                    minion.spawn(player, spawnAt, Util.fromRoman(display.split(" ")[display.split(" ").length - 1]));

                    event.getPlayer().sendMessage(ChatColor.AQUA + String.format("You placed a minion! (%s/%s)", minionsPlaced + 1, minionSlots));
                    event.getPlayer().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);

                    event.getPlayer().setItemInHand(null);
                } else{
                    event.setCancelled(!Skyblock.getPlugin().getItemHandler().getPlaceableItems().contains(ChatColor.stripColor(event.getItemInHand().getItemMeta().getDisplayName())));
                }
            }
        }
    }

}
