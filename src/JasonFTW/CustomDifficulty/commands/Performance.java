package jasonftw.CustomDifficulty.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import jasonftw.CustomDifficulty.SchedulerTasks.AggressivenessControl;
import jasonftw.CustomDifficulty.SchedulerTasks.BurnInSunlightControl;
import jasonftw.CustomDifficulty.SchedulerTasks.MobCounter;
import jasonftw.CustomDifficulty.SchedulerTasks.SpawnControl;
import jasonftw.CustomDifficulty.hooks.Permissions;
import jasonftw.CustomDifficulty.util.Manager;

public abstract class Performance {
    private static volatile boolean run = false;
    public static long onCreatureSpawn = 0;
    public static long onCreeperPower = 0;
    public static long onEntityCombust = 0;
    public static long onEntityDamage = 0;
    public static long onEntityDeath = 0;
    public static long onEntityTame = 0;
    public static long onEntityTarget = 0;
    public static long onPlayerInteract = 0;
    public static long spawnAlgorithm = 0;
    public static long mobCleanup = 0;
    public static long aggControl = 0;
    public static long BISControl = 0;

    public static void onCommand(final CommandSender commandSender, String[] args) {
        if (!Permissions.has(commandSender, "performance")) {
            System.out.println(Permissions.noPermission);
            return;
        }
        if (run) {
            commandSender.sendMessage((Object)ChatColor.RED + "Performance check is running, wait until it finishes");
            return;
        }
        commandSender.sendMessage((Object)ChatColor.GOLD + "Started performance check... results will show up in 10 sec.");
        run = true;
        final int players = commandSender.getServer().getOnlinePlayers().size();
        new Thread("CustomDifficulty_Performance"){

            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Performance.access$0(false);
                System.out.println(" ~~~ CustomDifficulty-Performance ~~~");
                System.out.println("     OS: " + System.getProperty("os.name") + "   |   CPUs: " + Runtime.getRuntime().availableProcessors());
                System.out.println("   useOldGetDifficulty : " + Manager.getUseOldDGetDifficuly());
                System.out.println("onCreatureSpawn   :" + Performance.round((double)Performance.onCreatureSpawn / 1000000.0) + "ms");
                System.out.println("onCreeperPower    :" + Performance.round((double)Performance.onCreeperPower / 1000000.0) + "ms");
                System.out.println("onEntityCombust   :" + Performance.round((double)Performance.onEntityCombust / 1000000.0) + "ms");
                System.out.println("onEntityDamage    :" + Performance.round((double)Performance.onEntityDamage / 1000000.0) + "ms");
                System.out.println("onEntityDeath     :" + Performance.round((double)Performance.onEntityDeath / 1000000.0) + "ms");
                System.out.println("onEntityTame      :" + Performance.round((double)Performance.onEntityTame / 1000000.0) + "ms");
                System.out.println("onEntityTarget    :" + Performance.round((double)Performance.onEntityTarget / 1000000.0) + "ms");
                System.out.println("onPlayerInteract  :" + Performance.round((double)Performance.onPlayerInteract / 1000000.0) + "ms");
                String intervals = "";
                for (World world22 : Bukkit.getServer().getWorlds()) {
                    if (Manager.getDifficulty(world22) == null) continue;
                    intervals = String.valueOf(intervals) + AggressivenessControl.getInterval(world22) + ", ";
                }
                System.out.println("aggressivnessCtrl :" + Performance.round((double)Performance.aggControl / 1000000.0) + "ms | Intervals: " + intervals);
                intervals = "";
                for (World world22 : Bukkit.getServer().getWorlds()) {
                    if (Manager.getDifficulty(world22) == null) continue;
                    intervals = String.valueOf(intervals) + SpawnControl.getInterval(world22) + ", ";
                }
                System.out.println("spawnAlgorithm    :" + Performance.round((double)Performance.spawnAlgorithm / 1000000.0) + "ms | Intervals: " + intervals);
                intervals = "";
                for (World world22 : Bukkit.getServer().getWorlds()) {
                    if (Manager.getDifficulty(world22) == null) continue;
                    intervals = String.valueOf(intervals) + BurnInSunlightControl.getInterval(world22) + ", ";
                }
                System.out.println("BISControl        :" + Performance.round((double)Performance.BISControl / 1000000.0) + "ms | Intervals: " + intervals);
                System.out.println("Mob cleanup       :" + Performance.round((double)Performance.mobCleanup / 1000000.0) + "ms | Interval: 20");
                System.out.println("  Total           :" + Performance.round((double)Performance.sum() / 1000000.0) + "ms (" + Performance.round((double)((int)((double)Performance.sum() / 1000000.0)) / 100.0) + "%)");
                System.out.println("  Players online  :" + players + " | Mobs on server: " + MobCounter.getAggressive() + "/" + MobCounter.getFriendly() + "/" + MobCounter.getPassive() + "/" + MobCounter.getUndef() + " | Total:" + MobCounter.getMobsOnServer());
                System.out.println(" ~~~           Finished           ~~~");
                if (commandSender instanceof Player) {
                    commandSender.sendMessage((Object)ChatColor.YELLOW + "Performance check finished. See console for results.");
                }
                Performance.reset();
            }
        }.start();
    }

    private static double round(double d) {
        return (double)Math.round(d * 100.0) / 100.0;
    }

    private static void reset() {
        onCreatureSpawn = 0;
        onCreeperPower = 0;
        onEntityCombust = 0;
        onEntityDamage = 0;
        onEntityDeath = 0;
        onEntityTame = 0;
        onEntityTarget = 0;
        onPlayerInteract = 0;
        aggControl = 0;
        spawnAlgorithm = 0;
        BISControl = 0;
        mobCleanup = 0;
    }

    private static long sum() {
        return onCreatureSpawn + onCreeperPower + onEntityCombust + onEntityDamage + onEntityDeath + onEntityTame + onEntityTarget + onPlayerInteract + aggControl + spawnAlgorithm + BISControl + mobCleanup;
    }

    public static boolean isRunning() {
        return run;
    }

    static /* synthetic */ void access$0(boolean bl) {
        run = bl;
    }

}

