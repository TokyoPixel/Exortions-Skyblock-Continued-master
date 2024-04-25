package com.skyblock.skyblock.commands.misc;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.ranks.GodspunkyPlayer;
import com.skyblock.skyblock.features.ranks.PlayerRank;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.List;

@Usage(usage = "/sb help [command]")
@Description(description = "Shows a list of commands or information about a specific command.")
public class HelpCommand implements Command {

    @Override
    public void execute(CommandSender sender, String[] args, Skyblock plugin) {
        List<Command> commands = plugin.getCommandHandler().getCommands();

        Player p = (Player) sender;

        StringBuilder message = new StringBuilder();

        message.append(plugin.getPrefix()).append("Running ").append(ChatColor.DARK_AQUA).append("Skyblock v").append(plugin.getDescription().getVersion()).append(ChatColor.WHITE).append(".\n");

        int allowed = 0;

        if (args.length == 0) {
            for (Command command : commands) {
                PlayerRank permission = command.permission();

                if (GodspunkyPlayer.getUser(p).rank.isAboveOrEqual(PlayerRank.ADMIN)) {
                    message.append(ChatColor.DARK_AQUA).append(" > ").append(ChatColor.WHITE).append(ChatColor.WHITE).append(command.usage()).append("\n");

                    allowed++;
                }
            }

            if (allowed <= 0) {
                message.append(ChatColor.DARK_AQUA).append(" > ").append(ChatColor.WHITE).append("You do not have permission to view any commands.");
            }
        } else {
            for (Command command : commands) {
                if (!args[0].equalsIgnoreCase(command.name())) continue;

                message.append(ChatColor.DARK_AQUA).append(" > ").append(ChatColor.WHITE).append(command.description()).append("\n");
                message.append(ChatColor.DARK_AQUA).append(" > ").append(ChatColor.WHITE).append(command.usage());
            }
        }

        sender.sendMessage(message.toString());
    }
}
