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

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import JasonFTW.CustomDifficulty.hooks.Permissions;
import JasonFTW.CustomDifficulty.util.Difficulty;
import JasonFTW.CustomDifficulty.util.Manager;

public abstract class Set {
    private static String worldNotFound = (Object)ChatColor.RED + "World " + (Object)ChatColor.AQUA + "%%% " + (Object)ChatColor.RED + "not found!";

    public static void onCommand(CommandSender commandSender, String[] args) {
        World world;
        String inputDifficulty;
        if (!Permissions.has(commandSender, "CustomDifficulty.change")) {
            commandSender.sendMessage(Permissions.noPermission);
            return;
        }
        if (args.length == 0) {
            commandSender.sendMessage((Object)ChatColor.RED + "You have to specify at least the difficulty!");
            return;
        }
        if (args.length == 1) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage((Object)ChatColor.RED + "You have to specify a world if you are no logged-in player!");
                return;
            }
            world = ((Player)commandSender).getWorld();
            inputDifficulty = args[0];
        } else {
            world = Bukkit.getServer().getPluginManager().getPlugin("CustomDifficulty").getServer().getWorld(args[0]);
            inputDifficulty = args[1];
        }
        if (world == null) {
            commandSender.sendMessage(worldNotFound.replace("%%%", args[0]));
            return;
        }
        Difficulty difficulty = Manager.getDifficulty(inputDifficulty);
        if (difficulty == null) {
            commandSender.sendMessage((Object)ChatColor.RED + "Difficulties" + File.separator + (Object)ChatColor.AQUA + inputDifficulty + ".yml" + (Object)ChatColor.RED + " does not exist!");
            return;
        }
        Manager.setDifficulty(world, difficulty);
        commandSender.sendMessage((Object)ChatColor.GREEN + "Difficulty of world " + (Object)ChatColor.AQUA + world.getName() + (Object)ChatColor.GREEN + " has been set to " + (Object)ChatColor.AQUA + difficulty.getName());
    }
}

