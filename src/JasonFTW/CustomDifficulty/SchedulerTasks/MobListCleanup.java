/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.World
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitScheduler
 */
package JasonFTW.CustomDifficulty.SchedulerTasks;

import JasonFTW.CustomDifficulty.CustomDifficulty;
import JasonFTW.CustomDifficulty.commands.Performance;
import JasonFTW.CustomDifficulty.util.Manager;
import java.io.PrintStream;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public class MobListCleanup
implements Runnable {
    private int id = -1;
    private static MobListCleanup instance = null;

    private MobListCleanup(CustomDifficulty plugin) {
        this.id = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)plugin, (Runnable)this, 5, 20);
        instance = this;
    }

    public static void activate(CustomDifficulty plugin) {
        if (instance == null || !Bukkit.getServer().getScheduler().isCurrentlyRunning(MobListCleanup.instance.id)) {
            instance = new MobListCleanup(plugin);
            CustomDifficulty.log("MobListCleanup activated.");
        }
    }

    public static void deactivate() {
        if (instance != null) {
            if (MobListCleanup.instance.id != -1) {
                Bukkit.getServer().getScheduler().cancelTask(MobListCleanup.instance.id);
            }
            instance = null;
            System.out.println("MobListCleanup deactivated.");
        }
    }

    @Override
    public void run() {
        long time = System.nanoTime();
        for (World world : Bukkit.getServer().getWorlds()) {
            Manager.cleanMobs(world);
        }
        if (Performance.isRunning()) {
            Performance.mobCleanup += System.nanoTime() - time;
        }
    }
}

