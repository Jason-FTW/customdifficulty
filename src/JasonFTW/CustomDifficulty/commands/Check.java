/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.Server
 *  org.bukkit.World
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package jasonftw.CustomDifficulty.commands;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import jasonftw.CustomDifficulty.CustomDifficulty;
import jasonftw.CustomDifficulty.hooks.Permissions;
import jasonftw.CustomDifficulty.util.Difficulty;
import jasonftw.CustomDifficulty.util.Manager;

public class Check {
    private static CustomDifficulty plugin;

    private static void displayDifficulty(CommandSender commandSender, World world) {
        Difficulty difficulty = Manager.getDifficulty(world);
        String d = "none.";
        if (difficulty != null) {
            d = difficulty.getName();
        }
        commandSender.sendMessage((Object)ChatColor.GREEN + "Difficulty on world " + (Object)ChatColor.AQUA + world.getName() + (Object)ChatColor.GREEN + ": " + (Object)ChatColor.AQUA + d);
        if (commandSender instanceof Player && world == ((Player)commandSender).getWorld()) {
            difficulty = Manager.getDifficulty(((Player)commandSender).getLocation());
            if (difficulty != null) {
                commandSender.sendMessage((Object)ChatColor.GREEN + "Region-difficulty at your current location: " + difficulty.getName());
            } else {
                commandSender.sendMessage((Object)ChatColor.GREEN + "No region-difficulty at your current location.");
            }
        }
    }

    public static void initialize(CustomDifficulty plugin) {
        Check.plugin = plugin;
    }

    public static void onCommand(CommandSender commandSender, String[] args) {
        if (!Permissions.has(commandSender, "CustomDifficulty.check")) {
            commandSender.sendMessage(Permissions.noPermission);
            return;
        }
        World world = null;
        if (args.length == 0) {
            if (commandSender instanceof Player) {
                world = ((Player)commandSender).getWorld();
            } else {
                for (World w : plugin.getServer().getWorlds()) {
                    Check.displayDifficulty(commandSender, w);
                }
            }
        } else if (args.length >= 1) {
            if (args[0].compareToIgnoreCase("all") == 0) {
                for (World w : plugin.getServer().getWorlds()) {
                    Check.displayDifficulty(commandSender, w);
                }
            } else {
                world = plugin.getServer().getWorld(args[0]);
            }
        }
        if (world != null) {
            Check.displayDifficulty(commandSender, world);
        }
    }
}

