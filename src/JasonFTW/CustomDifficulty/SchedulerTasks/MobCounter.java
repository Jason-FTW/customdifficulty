/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.World
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitScheduler
 */
package JasonFTW.CustomDifficulty.SchedulerTasks;

import JasonFTW.CustomDifficulty.CustomDifficulty;
import JasonFTW.CustomDifficulty.util.Aggressiveness;
import JasonFTW.CustomDifficulty.util.CreatureInfo;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public class MobCounter
implements Runnable {
    private static int taskID = -1;
    private static HashMap<World, int[]> counts = new HashMap<World, int[]>();
    private static int[] total = new int[4];
    private static /* synthetic */ int[] $SWITCH_TABLE$Pasukaru$CustomDifficulty$util$Aggressiveness;

    public static void start(CustomDifficulty plugin) {
        if (taskID == -1) {
            MobCounter mc = new MobCounter();
            taskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)plugin, (Runnable)mc, 1, 1);
            CustomDifficulty.initializeMessage(Level.INFO, "Mob counter started.");
        }
    }

    public static void stop() {
        if (taskID != -1) {
            Bukkit.getServer().getScheduler().cancelTask(taskID);
            taskID = -1;
            CustomDifficulty.initializeMessage(Level.INFO, "Mob counter stopped.");
        }
    }

    @Override
    public void run() {
        total = new int[4];
        for (World world : Bukkit.getServer().getWorlds()) {
            int[] count = new int[4];
            for (LivingEntity entity : world.getLivingEntities()) {
                if (entity instanceof Player) continue;
                switch (MobCounter.$SWITCH_TABLE$Pasukaru$CustomDifficulty$util$Aggressiveness()[CreatureInfo.getAggressiveness(entity).ordinal()]) {
                    case 1: {
                        int[] arrn = count;
                        arrn[0] = arrn[0] + 1;
                        break;
                    }
                    case 2: {
                        int[] arrn = count;
                        arrn[1] = arrn[1] + 1;
                        break;
                    }
                    case 4: {
                        int[] arrn = count;
                        arrn[2] = arrn[2] + 1;
                        break;
                    }
                    default: {
                        int[] arrn = count;
                        arrn[3] = arrn[3] + 1;
                    }
                }
            }
            int i = 0;
            while (i < 4) {
                int[] arrn = total;
                int n = i;
                arrn[n] = arrn[n] + count[i];
                ++i;
            }
            counts.put(world, count);
        }
    }

    public static int getMobsOnServer() {
        int c = 0;
        for (World world : Bukkit.getServer().getWorlds()) {
            c += MobCounter.getMobsOnWorld(world);
        }
        return c;
    }

    public static int getMobsOnWorld(World world) {
        int c = 0;
        int i = 0;
        while (i < 4) {
            c += counts.get((Object)world)[i];
            ++i;
        }
        return c;
    }

    public static int getAggressive(World world) {
        return counts.get((Object)world)[0];
    }

    public static int getFriendly(World world) {
        return counts.get((Object)world)[1];
    }

    public static int getPassive(World world) {
        return counts.get((Object)world)[2];
    }

    public static int getUndef(World world) {
        return counts.get((Object)world)[3];
    }

    public static int getAggressive() {
        int count = 0;
        for (World world : Bukkit.getServer().getWorlds()) {
            count += counts.get((Object)world)[0];
        }
        return count;
    }

    public static int getFriendly() {
        int count = 0;
        for (World world : Bukkit.getServer().getWorlds()) {
            count += counts.get((Object)world)[1];
        }
        return count;
    }

    public static int getPassive() {
        int count = 0;
        for (World world : Bukkit.getServer().getWorlds()) {
            count += counts.get((Object)world)[2];
        }
        return count;
    }

    public static int getUndef() {
        int count = 0;
        for (World world : Bukkit.getServer().getWorlds()) {
            count += counts.get((Object)world)[3];
        }
        return count;
    }

    static /* synthetic */ int[] $SWITCH_TABLE$Pasukaru$CustomDifficulty$util$Aggressiveness() {
        int[] arrn;
        int[] arrn2 = $SWITCH_TABLE$Pasukaru$CustomDifficulty$util$Aggressiveness;
        if (arrn2 != null) {
            return arrn2;
        }
        arrn = new int[Aggressiveness.values().length];
        try {
            arrn[Aggressiveness.AGGRESSIVE.ordinal()] = 1;
        }
        catch (NoSuchFieldError v1) {}
        try {
            arrn[Aggressiveness.FRIENDLY.ordinal()] = 2;
        }
        catch (NoSuchFieldError v2) {}
        try {
            arrn[Aggressiveness.NULL.ordinal()] = 3;
        }
        catch (NoSuchFieldError v3) {}
        try {
            arrn[Aggressiveness.PASSIVE.ordinal()] = 4;
        }
        catch (NoSuchFieldError v4) {}
        $SWITCH_TABLE$Pasukaru$CustomDifficulty$util$Aggressiveness = arrn;
        return $SWITCH_TABLE$Pasukaru$CustomDifficulty$util$Aggressiveness;
    }
}

