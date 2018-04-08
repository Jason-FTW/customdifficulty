package jasonftw.CustomDifficulty.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import jasonftw.CustomDifficulty.util.Manager;

public class DifficultyCommands
implements CommandExecutor {
    private static String[] getArgs(String[] args) {
        if (args.length <= 1) {
            return new String[0];
        }
        String[] tempArgs = new String[args.length - 1];
        int i = 0;
        while (i < tempArgs.length) {
            tempArgs[i] = args[i + 1];
            ++i;
        }
        return tempArgs;
    }

    public boolean onCommand(CommandSender commandSender, Command pCommand, String pString, String[] args) {
        if (args.length == 0) {
            Check.onCommand(commandSender, args);
        } else {
            String arg0 = args[0];
            args = DifficultyCommands.getArgs(args);
            if (arg0.equalsIgnoreCase("help")) {
                Help.onCommand(commandSender, args);
            } else if (arg0.equalsIgnoreCase("check")) {
                Check.onCommand(commandSender, args);
            } else if (arg0.equalsIgnoreCase("reload")) {
                Reload.onCommand(commandSender, args);
            } else if (arg0.equalsIgnoreCase("set")) {
                Set.onCommand(commandSender, args);
            } else if (arg0.equalsIgnoreCase("change")) {
                Set.onCommand(commandSender, args);
            } else if (arg0.equalsIgnoreCase("performance")) {
                Performance.onCommand(commandSender, args);
            } else if (arg0.equalsIgnoreCase("region")) {
                if (!Manager.useRegions()) {
                    commandSender.sendMessage((Object)ChatColor.RED + "You need to enable WorldGuard to be able to use region commands!");
                } else if (args.length == 0) {
                    RegionHelp.onCommand(commandSender, args);
                } else {
                    arg0 = args[0];
                    args = DifficultyCommands.getArgs(args);
                    if (arg0.equalsIgnoreCase("info")) {
                        RegionInfo.onCommand(commandSender, args);
                    } else if (arg0.equalsIgnoreCase("set")) {
                        RegionSet.onCommand(commandSender, args);
                    } else if (arg0.equalsIgnoreCase("change")) {
                        RegionSet.onCommand(commandSender, args);
                    } else if (arg0.equalsIgnoreCase("help")) {
                        RegionHelp.onCommand(commandSender, args);
                    } else {
                        commandSender.sendMessage((Object)ChatColor.RED + "Unknown region command: '" + (Object)ChatColor.AQUA + arg0 + (Object)ChatColor.RED + "'.");
                    }
                }
            } else {
                commandSender.sendMessage((Object)ChatColor.RED + "Unknown difficulty command: '" + (Object)ChatColor.AQUA + arg0 + (Object)ChatColor.RED + "'.");
            }
        }
        return true;
    }
}

