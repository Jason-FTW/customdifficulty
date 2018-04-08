package jasonftw.CustomDifficulty.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import jasonftw.CustomDifficulty.hooks.Permissions;
import jasonftw.CustomDifficulty.hooks.WorldGuard;
import jasonftw.CustomDifficulty.util.Manager;

public abstract class RegionInfo {
    public static void onCommand(CommandSender commandSender, String[] args) {
        World world;
        String name;
        if (!Permissions.has(commandSender, "CustomDifficulty.region.info")) {
            commandSender.sendMessage(Permissions.noPermission);
            return;
        }
        if (!Manager.useRegions()) {
            commandSender.sendMessage((Object)ChatColor.RED + "Using regions is disabled, you can't use this command!");
            return;
        }
        if (commandSender instanceof Player) {
            if (args.length <= 0) {
                commandSender.sendMessage((Object)ChatColor.RED + "You have to specify the region name at least!");
                return;
            }
            if (args.length == 1) {
                world = ((Player)commandSender).getWorld();
                if (world == null) {
                    commandSender.sendMessage((Object)ChatColor.RED + "World " + args[0] + " doesnt exist!");
                    return;
                }
                name = args[0];
            } else {
                world = Bukkit.getServer().getPluginManager().getPlugin("CustomDifficulty").getServer().getWorld(args[0]);
                if (world == null) {
                    commandSender.sendMessage((Object)ChatColor.RED + "World " + args[0] + " doesnt exist!");
                    return;
                }
                name = args[1];
            }
        } else {
            if (args.length < 2) {
                commandSender.sendMessage((Object)ChatColor.RED + "You have to specify the world and region name!");
                return;
            }
            world = Bukkit.getServer().getPluginManager().getPlugin("CustomDifficulty").getServer().getWorld(args[0]);
            if (world == null) {
                commandSender.sendMessage((Object)ChatColor.RED + "World " + args[0] + "doesnt exist!");
                return;
            }
            name = args[1];
        }
        WorldGuard.printWorldGuardRegionInfo(commandSender, world, name);
    }
}

