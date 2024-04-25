package com.skyblock.skyblock.features.island;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.minions.MinionHandler;
import com.skyblock.skyblock.features.npc.NPC;
import com.skyblock.skyblock.features.npc.NPCHandler;
import com.skyblock.skyblock.utilities.Util;
import lombok.Data;
import net.swofty.swm.api.SlimePlugin;
import net.swofty.swm.api.exceptions.*;
import net.swofty.swm.api.loaders.SlimeLoader;
import net.swofty.swm.api.world.SlimeWorld;
import net.swofty.swm.api.world.properties.SlimeProperties;
import net.swofty.swm.api.world.properties.SlimePropertyMap;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldUnloadEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Data
public class SkyblockIsland {


    private SkyblockPlayer owner;
    private UUID uuid;
    private SlimePlugin slimePlugin;

    private HashMap<String, Object> dataCache;
    private List<String> cacheChanged;
    private FileConfiguration config;

    private List<MinionHandler.MinionSerializable> minions;
    private File configFile;
    private World bukkitWorld;
    private SlimeWorld slimeWorld;
    private Player bukkitPlayer;
    public static final String ISLAND_PREFIX = "island-";


    public static HashMap<UUID, SkyblockIsland> islandRegistry = new HashMap<>();

    public static SkyblockIsland getIsland(UUID uuid) {
        return islandRegistry.computeIfAbsent(uuid, SkyblockIsland::new);
    }
    public static SkyblockIsland getIsland(SkyblockPlayer player) {
        return islandRegistry.computeIfAbsent(player.getBukkitPlayer().getUniqueId(), SkyblockIsland::new);
    }



    private SkyblockIsland(UUID uuid){
        this.dataCache = new HashMap<>();
        this.cacheChanged = new ArrayList<>();
        this.owner = SkyblockPlayer.getPlayer(uuid);
        this.minions = new ArrayList<>();
        this.uuid = uuid;
        this.bukkitPlayer = Bukkit.getPlayer(uuid);
        this.slimePlugin = Skyblock.getPlugin().getSlimePlugin();
        this.slimeWorld = null;
        this.bukkitWorld = null;

    }

    public static World getWorld(Player player) {
        return Bukkit.getWorld(ISLAND_PREFIX + player.getUniqueId());
    }
    public static World getWorld(UUID uuid){
        return Bukkit.getWorld(ISLAND_PREFIX + uuid);
    }


    public void create(){
        SlimePropertyMap properties = new SlimePropertyMap();
        properties.setValue(SlimeProperties.DIFFICULTY, "normal");
        properties.setValue(SlimeProperties.ALLOW_MONSTERS, false);
        SlimeLoader loader = slimePlugin.getLoader("mongodb");
        try {
            if (!loader.worldExists(ISLAND_PREFIX + uuid.toString())) {
                this.slimeWorld = slimePlugin.createEmptyWorld(
                        loader,
                        ISLAND_PREFIX + uuid.toString(),
                        false,
                        properties
                );
                slimePlugin.generateWorld(slimeWorld).thenRun(()->{
                    this.bukkitWorld = Bukkit.getWorld(ISLAND_PREFIX + uuid);
                    Util.generate(new Location(Bukkit.getWorld(ISLAND_PREFIX + uuid.toString()), 0, 100, 0), "private_island.schematic");
                    Location loc1 = new Location(bukkitWorld, 0, 100 + 4.0, + 35.0);
                    Location loc2 = new Location(bukkitWorld, -2, 100,  35.0);
                    Util.setBlocks(loc1, loc2, Material.PORTAL, false);
                    bukkitPlayer.teleport(new Location(Bukkit.getWorld(ISLAND_PREFIX + uuid.toString()), 0, 100, 0));
                    addJerry();
                    initConfig();
                    Skyblock.getPlugin().getMinionHandler().reloadPlayer(owner , false);
                    System.out.println("Save method is calling minion reload method");
                });

            } else if (loader.worldExists(ISLAND_PREFIX + uuid.toString()) && Bukkit.getWorld(ISLAND_PREFIX+uuid) == null) {
                this.slimeWorld = slimePlugin.loadWorld(
                        loader,
                        ISLAND_PREFIX + uuid.toString(),
                        false,
                        properties
                );
                slimePlugin.generateWorld(slimeWorld).thenRun(() ->{
                    bukkitPlayer.teleport(new Location(Bukkit.getWorld(ISLAND_PREFIX + uuid.toString()), 0, 100, 0));
                    addJerry();
                    initConfig();
                    Skyblock.getPlugin().getMinionHandler().reloadPlayer(owner , false);
                    System.out.println("Load method is calling minion reload method");

                });


            }else if (Bukkit.getWorld(ISLAND_PREFIX + uuid) != null){
                bukkitPlayer.teleport(new Location(Bukkit.getWorld(ISLAND_PREFIX + uuid.toString()), 0, 100, 0));
            }
        }catch (WorldAlreadyExistsException | IOException | CorruptedWorldException | NewerFormatException |
                WorldInUseException | UnknownWorldException ex){
            ex.printStackTrace();
        }
    }
    public void setMinions(List<MinionHandler.MinionSerializable> minions){
        this.minions = minions;
        saveMinions();
    }

    public boolean exist() {
        SlimeLoader loader = slimePlugin.getLoader("mongodb");
        if (loader == null) return false;
        try{
        if (loader.worldExists(ISLAND_PREFIX + uuid.toString())) return true;
     }catch (IOException ex){
            ex.printStackTrace();
        }
        return false;
    }




    public void delete(){
        if (slimeWorld == null) return;
        SlimeLoader loader = slimePlugin.getLoader("mongodb");
        try {
            loader.deleteWorld(ISLAND_PREFIX + uuid.toString());
        }catch (IOException | UnknownWorldException ex){
            ex.printStackTrace();
        }
    }

    private void addJerry() {
        NPCHandler npcs = Skyblock.getPlugin().getNpcHandler();
        if (!npcs.getNPCs().containsKey("jerry_" + uuid) && getWorld(uuid) != null) {
            NPC jerry = new NPC("Jerry", true, false, true, Villager.Profession.FARMER,
                    new Location(Bukkit.getWorld(ISLAND_PREFIX + bukkitPlayer.getUniqueId()), 2.5, 100, 26.5),
                    (p) -> {
                        if (p.getUniqueId().equals(bukkitPlayer.getUniqueId())) {
                            if (((List<String>) owner.getValue("quests.completedObjectives")).contains("jerry")) {
                            } else {
                                NPC.sendMessages(p, "Jerry",
                                        "Your Skyblock island is part of a much larger universe",
                                        "The Skyblock universe is full of islands to explore and resources to discover!",
                                        "Use the Portal to warp to the first of those islands - The Skyblock Hub!");
                            }
                        } else {
                            NPC.sendMessage(p, "Jerry", "Jerry doesn't speak to strangers!", false);
                            p.playSound(p.getLocation(), Sound.VILLAGER_NO, 10, 1);
                        }
                    }, "", "");

            npcs.registerNPC("jerry_" + owner.getBukkitPlayer().getUniqueId().toString(), jerry);
            jerry.spawn();
        }
    }

    private void initConfig(){
        File folder = new File(Skyblock.getPlugin(Skyblock.class).getDataFolder() + File.separator + "islands");
        if (!folder.exists())  folder.mkdirs();
        configFile = new File(Skyblock.getPlugin(Skyblock.class).getDataFolder() + File.separator + "islands" + File.separator + uuid + ".yml");
        this.config = YamlConfiguration.loadConfiguration(configFile);
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                config.set("island.last_login", System.currentTimeMillis());
                config.set("island.minion.slots", 6);
                config.set("island.minions", new ArrayList<>());
                config.save(configFile);
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }
    }

    public void setValue(String path, Object item) {
        dataCache.put(path, item);
        if (!cacheChanged.contains(path)) cacheChanged.add(path);

    }
    public Object getValue(String path) {
        if (config == null) return null;
        if (!dataCache.containsKey(path)) dataCache.put(path, config.get(path));
//        if (path.equalsIgnoreCase("island.minions")) saveMinions();

        return dataCache.get(path);
    }


    public int getIntValue(String path) {
        return (int) getValue(path);
    }

    public String getStringValue(String path) {
        return (String) getValue(path);
    }

    public boolean getBoolValue(String path) {
        return (boolean) getValue(path);
    }

    public double getDouble(String path) {
        return Double.parseDouble(getValue(path).toString());
    }

    public void onQuit(){
        setValue("island.minions", minions);
        setValue("island.last_login", System.currentTimeMillis());
        saveToDisk();

    }

    public void saveMinions(){
        setValue("island.minions", minions);
    }

    public void saveToDisk() {
        if (config == null) return;
        System.out.println("minions size : " + minions.size());
        for (String path : cacheChanged) {
            config.set(path, dataCache.get(path));
            try {
                config.save(configFile);
            }catch (IOException exception){
                exception.printStackTrace();
            }
        }
    }
}



