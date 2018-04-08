package jasonftw.CustomDifficulty.hooks;

import java.lang.reflect.Method;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import jasonftw.CustomDifficulty.CustomDifficulty;

public abstract class Permissions {
    private static Method has = null;
    public static String noPermission = (Object)ChatColor.RED + "You don't have permissions to use this command!";
    private static Object permissionsHandler = null;

    public static boolean has(CommandSender commandSender, String permission) {
        return Permissions.has(commandSender, permission, false);
    }

    public static boolean has(CommandSender commandSender, String permission, boolean ignoreIsOp) {
        if (commandSender.isOp() && !ignoreIsOp) {
            return true;
        }
        if (commandSender instanceof Player && has != null && permissionsHandler != null) {
            try {
                return (Boolean)has.invoke(permissionsHandler, new Object[]{(Player)commandSender, permission});
            }
            catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    private static boolean hook(JavaPlugin plugin) {
        Method m;
        Plugin permissions = plugin.getServer().getPluginManager().getPlugin("Permissions");
        if (permissions == null) {
            return false;
        }
        Method getHandler = null;
        Method[] arrmethod = permissions.getClass().getMethods();
        int n = arrmethod.length;
        int n2 = 0;
        while (n2 < n) {
            m = arrmethod[n2];
            if (m.getName().equals("getHandler") && m.getParameterTypes().length == 0) {
                getHandler = m;
                break;
            }
            ++n2;
        }
        if (getHandler == null) {
            return false;
        }
        try {
            permissionsHandler = getHandler.invoke((Object)permissions, new Object[0]);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        arrmethod = permissionsHandler.getClass().getMethods();
        n = arrmethod.length;
        n2 = 0;
        while (n2 < n) {
            m = arrmethod[n2];
            if (m.getName().equals("has") && m.getParameterTypes().length == 2 && m.getParameterTypes()[0] == Player.class && m.getParameterTypes()[1] == String.class && m.getReturnType() == Boolean.TYPE) {
                has = m;
                break;
            }
            ++n2;
        }
        if (has == null) {
            return false;
        }
        return true;
    }

    public static void initialize(JavaPlugin plugin) {
        if (Permissions.hook(plugin)) {
            CustomDifficulty.initializeMessage(Level.INFO, "Hooked into " + plugin.getServer().getPluginManager().getPlugin("Permissions").getDescription().getFullName() + ".");
        } else {
            CustomDifficulty.initializeMessage(Level.WARNING, "Couldn't hook into Permissions!");
        }
    }
}
