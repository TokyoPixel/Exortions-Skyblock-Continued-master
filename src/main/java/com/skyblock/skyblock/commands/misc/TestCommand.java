package com.skyblock.skyblock.commands.misc;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.crafting.SkyblockRecipe;
import com.skyblock.skyblock.features.entities.dragon.DragonSequence;
import com.skyblock.skyblock.features.ranks.PlayerRank;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RankPermission;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftSlime;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@RequiresPlayer
@Usage(usage = "/sb test")
@Description(description = "Command for testing features")
@RankPermission(permission = PlayerRank.ADMIN)
public class TestCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        if (args[0].equals("all")) {
            for (SkyblockRecipe recipe : plugin.getRecipeHandler().getRecipes()) {
                System.out.println(recipe.toShapeless());
            }
        }
        if (args[0].equals("crafting_table")){
            System.out.println(plugin.getRecipeHandler().getRecipe(new ItemStack(Material.WORKBENCH)).toShapeless());
        }
    }
}
