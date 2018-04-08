package jasonftw.CustomDifficulty.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import jasonftw.CustomDifficulty.hooks.Permissions;
import jasonftw.CustomDifficulty.util.Manager;

public abstract class Reload {
    public static void onCommand(CommandSender commandSender, String[] args) {
        if (!Permissions.has(commandSender, "CustomDifficulty.reload")) {
            commandSender.sendMessage(Permissions.noPermission);
            return;
        }
        Manager.reload();
        if (commandSender instanceof Player) {
            commandSender.sendMessage((Object)ChatColor.GREEN + "Reloading done.");
        }
    }
}

