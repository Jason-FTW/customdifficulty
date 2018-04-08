package jasonftw.CustomDifficulty.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import jasonftw.CustomDifficulty.CustomDifficulty;
import jasonftw.CustomDifficulty.hooks.Permissions;
import jasonftw.CustomDifficulty.util.Manager;

public abstract class Help {
    private static CustomDifficulty plugin;

    public static void initialize(CustomDifficulty plugin) {
        Help.plugin = plugin;
    }

    public static void onCommand(CommandSender commandSender, String[] args) {
        if (!Permissions.has(commandSender, "CustomDifficulty.help")) {
            commandSender.sendMessage(Permissions.noPermission);
            return;
        }
        String darkred = ChatColor.DARK_RED.toString();
        String green = ChatColor.GREEN.toString();
        String aqua = ChatColor.AQUA.toString();
        String yellow = ChatColor.YELLOW.toString();
        String dash = (Object)ChatColor.RED + " - " + yellow;
        commandSender.sendMessage("");
        commandSender.sendMessage(String.valueOf(green) + "             ~~~ " + aqua + " CustomDifficulty " + plugin.getDescription().getVersion() + " Help " + green + " ~~~");
        commandSender.sendMessage("");
        commandSender.sendMessage(String.valueOf(green) + "/difficulty " + aqua + "check" + green + " [" + aqua + "World" + green + "]" + dash + "Displays [" + aqua + "World" + yellow + "]'s difficulty.");
        commandSender.sendMessage(String.valueOf(green) + "/difficulty " + aqua + "help" + green + dash + "Displays this message. :D");
        commandSender.sendMessage(String.valueOf(green) + "/difficulty [" + aqua + "change" + green + " or " + aqua + "set" + green + " ] [" + aqua + "World" + green + "] [" + aqua + "difficulty" + green + "]");
        commandSender.sendMessage(String.valueOf(dash) + "Changes difficulty and reloads config.");
        commandSender.sendMessage(String.valueOf(green) + "/difficulty " + aqua + "reload" + green + dash + "Reloads config. Monsters will be healed.");
        if (Manager.useRegions()) {
            commandSender.sendMessage(String.valueOf(green) + "/difficulty " + aqua + "region help" + green + dash + "Displays help about region commands.");
        } else {
            commandSender.sendMessage(String.valueOf(green) + "/difficulty " + aqua + "region" + dash + darkred + "not available without WorldGuard!");
        }
    }
}

