/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Server
 *  org.bukkit.World
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 */
package JasonFTW.CustomDifficulty.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import JasonFTW.CustomDifficulty.hooks.Permissions;
import JasonFTW.CustomDifficulty.hooks.WorldGuard;
import JasonFTW.CustomDifficulty.util.Difficulty;
import JasonFTW.CustomDifficulty.util.Manager;

public abstract class RegionSet {
    public static void onCommand(CommandSender commandSender, String[] args) {
        World world;
        String inputDifficulty;
        String name;
        if (!Permissions.has(commandSender, "CustomDifficulty.region.set")) {
            commandSender.sendMessage(Permissions.noPermission);
            return;
        }
        if (!Manager.useRegions()) {
            commandSender.sendMessage((Object)ChatColor.RED + "Using regions is disabled, you can't use this command!");
            return;
        }
        if (commandSender instanceof Player) {
            if (args.length < 2) {
                commandSender.sendMessage((Object)ChatColor.RED + "You have to specify the region name and difficulty at least!");
                return;
            }
            if (args.length == 2) {
                world = ((Player)commandSender).getWorld();
                name = args[0];
                inputDifficulty = args[1];
            } else {
                world = Bukkit.getServer().getPluginManager().getPlugin("CustomDifficulty").getServer().getWorld(args[0]);
                if (world == null) {
                    commandSender.sendMessage((Object)ChatColor.RED + "World " + args[0] + " doesnt exist!");
                    return;
                }
                name = args[1];
                inputDifficulty = args[2];
            }
        } else {
            if (args.length < 3) {
                commandSender.sendMessage((Object)ChatColor.RED + "You have to specify the world, region name and difficulty!");
                return;
            }
            world = Bukkit.getServer().getPluginManager().getPlugin("CustomDifficulty").getServer().getWorld(args[0]);
            if (world == null) {
                commandSender.sendMessage((Object)ChatColor.RED + "World " + args[0] + " doesnt exist!");
                return;
            }
            name = args[1];
            inputDifficulty = args[2];
        }
        Difficulty difficulty = Manager.getDifficulty(inputDifficulty);
        int result = WorldGuard.setDifficulty(world, name, difficulty);
        String msg = null;
        switch (result) {
            case 0: {
                msg = (Object)ChatColor.GREEN + "Difficulty for WorldGuard-Region " + (Object)ChatColor.AQUA + name + (Object)ChatColor.GREEN + " on world " + (Object)ChatColor.AQUA + world.getName() + (Object)ChatColor.GREEN + " has been set to: " + (Object)ChatColor.AQUA + difficulty.getName();
                break;
            }
            case 1: {
                msg = (Object)ChatColor.RED + "Invalid difficulty: " + inputDifficulty;
                break;
            }
            case 2: {
                msg = (Object)ChatColor.RED + "No matching WorldGuard-Region found: " + name;
                break;
            }
            case 3: {
                msg = (Object)ChatColor.RED + "Couldn't save region to file. Changes will be lost after server restart/reload";
            }
        }
        commandSender.sendMessage(msg);
    }
}

