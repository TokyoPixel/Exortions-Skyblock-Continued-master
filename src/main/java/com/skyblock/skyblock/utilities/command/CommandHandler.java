package com.skyblock.skyblock.utilities.command;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.ranks.GodspunkyPlayer;
import lombok.Getter;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.skyblock.skyblock.features.ranks.PlayerRank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandHandler implements CommandExecutor {

    private final Skyblock plugin;

    private final HashMap<String, Runnable> commandMessages;

    @Getter
    private final List<Command> commands;

    private CommandSender sender = null;
    private String[] args = null;

    public CommandHandler(Skyblock plugin, Command... commands) {
        this.plugin = plugin;

        this.commandMessages = new HashMap<>();
        this.commands = new ArrayList<>();

        for (Command command : commands) {
            if (command instanceof TrueAlias<?>) ((TrueAlias<?>) command).register();
            this.commands.add(command);
        }

        this.commandMessages.put("only-players", () -> this.sender.sendMessage(this.plugin.getPrefix() + "Only players can execute this command."));
        this.commandMessages.put("base-command", () -> {
            boolean hasPermission = this.commands.stream().anyMatch(command -> command.permission().isAboveOrEqual(getPlayerRank(sender)));

            String message = this.plugin.getPrefix() + "Running " + "Skyblock v" + this.plugin.getVersion() + ".\n"
                    + " > "
                    + (
                    hasPermission ?
                            "Type /skyblock help for more information." :
                            "You do not have permission to use any commands."
            );

            this.sender.sendMessage(message);
        });
        this.commandMessages.put("no-permission", () -> this.sender.sendMessage(this.plugin.getPrefix() + "You do not have permission to execute this command."));
        this.commandMessages.put("not-found", () -> this.sender.sendMessage(this.plugin.getPrefix() + "Command not found."));
    }

    private boolean executeCommandMessage(String message) {
        this.commandMessages.get(message).run();

        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        this.sender = sender;
        this.args = args;

        if (args.length == 0) return executeCommandMessage("base-command");

        for (Command command : this.commands) {
            boolean requiresPlayer = command.requiresPlayer();
            PlayerRank requiredRank = command.permission();

            boolean isAlias = false;

            for (String alias : command.aliases()) {
                if (alias.equalsIgnoreCase(args[0])) {
                    isAlias = true;
                    break;
                }
            }

            if (!this.args[0].equalsIgnoreCase(command.name()) && !isAlias) continue;

            PlayerRank playerRank = getPlayerRank(sender);

            if (!playerRank.isAboveOrEqual(requiredRank)) {
                return executeCommandMessage("no-permission");
            }

            String[] arguments = new String[args.length - 1];
            System.arraycopy(args, 1, arguments, 0, arguments.length);

            if (requiresPlayer) {
                if (!(sender instanceof Player)) return this.executeCommandMessage("only-players");

                command.execute((Player) sender, arguments, this.plugin);
            } else {
                command.execute(sender, arguments, this.plugin);
            }

            return true;
        }

        return this.executeCommandMessage("not-found");
    }

    private PlayerRank getPlayerRank(CommandSender sender) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            GodspunkyPlayer godspunkyPlayer = GodspunkyPlayer.getUser(player);
            if (godspunkyPlayer != null) {
                return godspunkyPlayer.rank;
            }
        }
        return PlayerRank.DEFAULT;
    }
}
