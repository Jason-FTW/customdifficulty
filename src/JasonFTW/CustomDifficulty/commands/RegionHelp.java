/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginDescriptionFile
 *  org.bukkit.plugin.PluginManager
 */
package JasonFTW.CustomDifficulty.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import JasonFTW.CustomDifficulty.hooks.Permissions;
import JasonFTW.CustomDifficulty.util.Manager;

public abstract class RegionHelp {
    public static void onCommand(CommandSender commandSender, String[] args) {
        if (!Permissions.has(commandSender, "CustomDifficulty.region.help")) {
            commandSender.sendMessage((Object)ChatColor.RED + "You don't have permission to use this command!");
            return;
        }
        String red = ChatColor.RED.toString();
        String aqua = ChatColor.AQUA.toString();
        String yellow = ChatColor.YELLOW.toString();
        String dash = (Object)ChatColor.GREEN + " - " + yellow;
        commandSender.sendMessage("");
        commandSender.sendMessage(String.valueOf(red) + "         ~~~ " + aqua + " CustomDifficulty " + Bukkit.getServer().getPluginManager().getPlugin("CustomDifficulty").getDescription().getVersion() + " Regions Help " + red + " ~~~");
        if (Manager.useRegions()) {
            commandSender.sendMessage("           " + aqua + " CustomDifficulty" + (Object)ChatColor.YELLOW + " is using " + aqua + "WorldGuard" + (Object)ChatColor.YELLOW + "-Regions! ");
        } else {
            commandSender.sendMessage(String.valueOf(red) + "                      Using regions is disabled! ");
        }
        commandSender.sendMessage("");
        commandSender.sendMessage(String.valueOf(aqua) + "info" + red + " (" + aqua + "WorldGuard-Region" + red + ")");
        commandSender.sendMessage(String.valueOf(dash) + "Displays information about a region.");
        commandSender.sendMessage(String.valueOf(red) + "[" + aqua + "change" + red + " or " + aqua + "set" + red + "] (" + aqua + "world" + red + ") [" + aqua + "name" + red + "]");
        commandSender.sendMessage(String.valueOf(dash) + "Changes difficulty of a region. " + aqua + "World" + yellow + " argument is optional.");
    }
}

