package com.mtier.plugin.commands;

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

    private final List<String> SUB_COMMANDS = Arrays.asList("reload", "test", "setmmr");
    private final List<String> MODES = Arrays.asList("PvP", "Bridge");

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], SUB_COMMANDS, completions);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("setmmr")) {
            // Player names are handled by Bukkit default if we return null, 
            // but we can provide empty list to wait for input or let Bukkit handle it.
            return null; 
        } else if (args.length == 3 && args[0].equalsIgnoreCase("setmmr")) {
            StringUtil.copyPartialMatches(args[2], MODES, completions);
        }

        return completions;
    }
}
