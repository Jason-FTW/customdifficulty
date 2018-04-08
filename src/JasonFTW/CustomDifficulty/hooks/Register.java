package jasonftw.CustomDifficulty.hooks;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import jasonftw.CustomDifficulty.CustomDifficulty;

public abstract class Register {
    private static Plugin register = null;

    private static boolean hook() {
        Plugin p = Bukkit.getServer().getPluginManager().getPlugin("Register");
        if (p == null) {
            return false;
        }
        register = p;
        return true;
    }

    public static void initialize(JavaPlugin plugin) {
        if (Register.hook()) {
            CustomDifficulty.initializeMessage(Level.INFO, "Hooked into " + plugin.getServer().getPluginManager().getPlugin("Register").getDescription().getFullName() + ".");
        } else {
            CustomDifficulty.initializeMessage(Level.WARNING, "Couldn't hook into Register!");
        }
    }

    public static boolean isHooked() {
        if (register != null && register.isEnabled()) {
            return true;
        }
        return false;
    }

    public static String format(double amount) {
        Method method = Methods.getMethod();
        return method.format(amount);
    }

    public static boolean addMoney(Player player, double amount) {
        Method method = Methods.getMethod();
        Method.MethodAccount account = method.getAccount(player.getName());
        if (account != null) {
            return account.add(amount);
        }
        return false;
    }
}

