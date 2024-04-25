package com.skyblock.skyblock.commands.player;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.ranks.PlayerRank;
import com.skyblock.skyblock.listeners.SkyblockMenuListener;
import com.skyblock.skyblock.utilities.FruitProfileGenerator;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RankPermission;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RequiresPlayer
@RankPermission(permission = PlayerRank.DEFAULT)
@Usage(usage = "/sb profile create/switch/list")
@Description(description = "create/switch profiles")
public class ProfileCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
//        SkyblockPlayer p = SkyblockPlayer.getPlayer(player);
//        File activeProfileFile = new File(SkyblockPlayer.ACTIVE_PROFILES_FOLDER, player.getUniqueId() + ".yml");
//        FileConfiguration activeProfileConfig = YamlConfiguration.loadConfiguration(activeProfileFile);
//        List<String> profiles = activeProfileConfig.getStringList("profiles");
//        String profile = FruitProfileGenerator.generateRandomFruitName();
//        switch (args[0]) {
//            case "create":
//                try {
//                    p.createNewProfile(profile);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//                break;
//            case "list":
//                player.sendMessage(profiles.toString());
//                break;
//            case "switch":
//                try {
//                    p.loadProfile(args[1]);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//                break;
//        }
//    }


    }
}
