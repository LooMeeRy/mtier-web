package com.mtier.plugin.commands;

import com.mtier.plugin.MTierPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandCompleter implements TabCompleter {

    private final List<String> SUB_COMMANDS = Arrays.asList("reload", "test", "setmmr", "play");

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> sub = new ArrayList<>(SUB_COMMANDS);
            sub.addAll(MTierPlugin.getInstance().getSubCommands().keySet());
            List<String> completions = new ArrayList<>();
            StringUtil.copyPartialMatches(args[0], sub, completions);
            return completions;
        }

        // Delegate to subcommands
        MTierPlugin.SubCommand sub = MTierPlugin.getInstance().getSubCommands().get(args[0].toLowerCase());
        if (sub != null && sub.completer() != null) {
            return sub.completer().onTabComplete(sender, command, alias, args);
        }

        List<String> completions = new ArrayList<>();
        List<String> modes = MTierPlugin.getInstance().getMenuManager().getRegisteredIds();
        
        // Add defaults if none registered yet for easier testing
        if (modes.isEmpty()) {
            modes = Arrays.asList("PvP", "Bridge");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("setmmr")) {
            return null; 
        } else if (args.length == 3 && args[0].equalsIgnoreCase("setmmr")) {
            StringUtil.copyPartialMatches(args[2], modes, completions);
        }

        return completions;
    }
}
