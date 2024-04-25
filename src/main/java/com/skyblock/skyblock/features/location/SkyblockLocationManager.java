package com.skyblock.skyblock.features.location;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkyblockLocationManager {

    private static final String LOCATION_FILE_NAME = "locations.yml";
    private final Skyblock skyblock;
    private final FileConfiguration config;
    private final Map<String, SkyblockLocation> locationsCache;

    public SkyblockLocationManager(Skyblock skyblock) {
        this.skyblock = skyblock;
        this.config = YamlConfiguration.loadConfiguration(getFile());
        this.locationsCache = new HashMap<>();
        init();
    }

    public SkyblockLocation getLocation(Location location) {
        SkyblockLocation temp = null;
        List<SkyblockLocation> found = new ArrayList<>();

        for (SkyblockLocation skyblockLocation : locationsCache.values()) {
            if (Util.inCuboid(location, skyblockLocation.getPosition1(), skyblockLocation.getPosition2())
                    && location.getWorld().equals(skyblockLocation.getPosition1().getWorld())) {
                found.add(skyblockLocation);
            }
        }

        if (found.isEmpty()) {
            return null;
        }

        for (SkyblockLocation loc : found) {
            if (temp == null || loc.getWeight() > temp.getWeight()) {
                temp = loc;
            }
        }

        if (temp != null && temp.getName().equals("The Park")) {
            Biome biome = location.getBlock().getBiome();
            if (biome.equals(Biome.BIRCH_FOREST)) {
                temp = new SkyblockLocation(temp.getPosition1(), temp.getPosition2(), ChatColor.GREEN, "Birch Park", temp.getWeight() + 1);
            } else if (biome.equals(Biome.ICE_PLAINS) || biome.equals(Biome.SWAMPLAND) || biome.equals(Biome.TAIGA)) {
                temp = new SkyblockLocation(temp.getPosition1(), temp.getPosition2(), ChatColor.GREEN, "Spruce Woods", temp.getWeight() + 1);
            } else if (biome.equals(Biome.ROOFED_FOREST)) {
                temp = new SkyblockLocation(temp.getPosition1(), temp.getPosition2(), ChatColor.GREEN, "Dark Thicket", temp.getWeight() + 1);
            } else if (biome.equals(Biome.SAVANNA)) {
                temp = new SkyblockLocation(temp.getPosition1(), temp.getPosition2(), ChatColor.GREEN, "Savanna Woodland", temp.getWeight() + 1);
            } else if (biome.equals(Biome.JUNGLE)) {
                temp = new SkyblockLocation(temp.getPosition1(), temp.getPosition2(), ChatColor.GREEN, "Jungle Island", temp.getWeight() + 1);
            }
        }

        return temp;
    }

    public void init() {
        if (!getFile().exists()) {
            try {
                if (!getFile().createNewFile()) {
                    throw new IOException("Could not create " + getFile().getAbsolutePath() + ", File#createNewFile returns false");
                }
                config.save(getFile());
            } catch (IOException ex) {
                this.skyblock.sendMessage("&cFailed to initialize &8locations.yml&c: " + ex.getMessage());
            }
        }
        loadLocationsCache();
    }

    private void loadLocationsCache() {
        locationsCache.clear();
        for (String name : getLocations()) {
            SkyblockLocation location = getLocation(name);
            if (location != null) {
                locationsCache.put(name, location);
            }
        }
    }

    public SkyblockLocation getLocation(String name) {
        Location pos1 = (Location) getField(name, "pos1");
        Location pos2 = (Location) getField(name, "pos2");

        if (pos1 != null && pos2 != null) {
            ChatColor color = ChatColor.valueOf(((String)getField(name, "color")).toUpperCase());
            int weight = (int) getField(name, "weight");

            return new SkyblockLocation(pos1, pos2, color, name, weight);
        }

        return null;
    }

    public List<String> getLocations() {
        return new ArrayList<>(config.getKeys(false));
    }

    public void createLocation(Location position1, Location position2, String name, ChatColor color, int weight) {
        config.set(name + ".pos1", position1);
        config.set(name + ".pos2", position2);
        config.set(name + ".name", name);
        config.set(name + ".color", color.name());
        config.set(name + ".weight", weight);

        try {
            config.save(getFile());
        } catch (IOException ex) {
            this.skyblock.sendMessage("&cFailed to save &7locations.yml&c: " + ex.getMessage());
        }

        locationsCache.put(name, new SkyblockLocation(position1, position2, color, name, weight));
    }

    private Object getField(String name, String field) {
        return config.get(name + "." + field);
    }

    private File getFile() {
        return new File(skyblock.getDataFolder() + File.separator + LOCATION_FILE_NAME);
    }
}
