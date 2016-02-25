/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package JasonFTW.CustomDifficulty.commands;

import JasonFTW.CustomDifficulty.hooks.Permissions;
import JasonFTW.CustomDifficulty.util.Manager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

