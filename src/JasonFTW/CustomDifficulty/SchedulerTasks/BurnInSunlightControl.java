package jasonftw.CustomDifficulty.SchedulerTasks;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.plugin.Plugin;

import jasonftw.CustomDifficulty.CustomDifficulty;
import jasonftw.CustomDifficulty.commands.Performance;
import jasonftw.CustomDifficulty.util.CdCreatureType;
import jasonftw.CustomDifficulty.util.Config;
import jasonftw.CustomDifficulty.util.CreatureInfo;
import jasonftw.CustomDifficulty.util.Difficulty;
import jasonftw.CustomDifficulty.util.Manager;

public class BurnInSunlightControl
implements Runnable {
    private int id;
    private World world;
    private static final HashMap<World, BurnInSunlightControl> tasks = new HashMap<World, BurnInSunlightControl>();

    public BurnInSunlightControl(CustomDifficulty plugin, World world) {
        if (!tasks.containsKey((Object)world) && world != null) {
            this.world = world;
            int interval = BurnInSunlightControl.getInterval(world);
            this.id = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)plugin, (Runnable)this, 5, (long)interval);
            tasks.put(world, this);
            CustomDifficulty.initializeMessage(Level.INFO, "BurnsInSunlightControl activated for world: " + world.getName() + ". | Interval: " + interval);
        } else {
            this.world = null;
            this.id = -1;
        }
    }

    public static void activate(CustomDifficulty plugin, World world) {
        Difficulty worldDifficulty = Manager.getDifficulty(world);
        if (worldDifficulty == null) {
            BurnInSunlightControl.deactivate(world);
            return;
        }
        BurnInSunlightControl task = tasks.get((Object)world);
        if (task == null || task.id == -1) {
            new jasonftw.CustomDifficulty.SchedulerTasks.BurnInSunlightControl(plugin, world);
        }
    }

    public static void activateAllWorlds(CustomDifficulty plugin) {
        for (World w : Bukkit.getServer().getWorlds()) {
            BurnInSunlightControl.activate(plugin, w);
        }
    }

    public static void deactivate(World world) {
        BurnInSunlightControl task = tasks.get((Object)world);
        if (task != null && task.id != -1) {
            Bukkit.getServer().getScheduler().cancelTask(task.id);
            task.id = -1;
            tasks.remove((Object)world);
            CustomDifficulty.initializeMessage(Level.INFO, "BurnsInSunlightControl deactivated for world: " + world.getName());
        }
    }

    public static void deactivateAllWorlds() {
        for (World w : Bukkit.getServer().getWorlds()) {
            BurnInSunlightControl.deactivate(w);
        }
    }

    public static int getInterval(World world) {
        int interval = Config.config.getInt("worlds." + world.getName().replace(" ", "") + ".burnsInSunlightInterval", 20);
        if (interval < 1) {
            interval = 1;
        }
        return interval;
    }

    @Override
    public void run() {
        long time = System.nanoTime();
        List<LivingEntity> entities = this.world.getLivingEntities();
        Difficulty difficulty = Manager.getDifficulty(this.world);
        if (difficulty == null) {
            if (Performance.isRunning()) {
                Performance.BISControl += System.nanoTime() - time;
            }
            return;
        }
        int i = 0;
        while (i < entities.size()) {
            LivingEntity e = (LivingEntity)entities.get(i);
            if (!(e instanceof Skeleton || e instanceof Zombie && !(e instanceof PigZombie) || e instanceof Player)) {
                Material m;
                if (Manager.useRegion(this.world, "burnsInSunlight")) {
                    difficulty = Manager.getDifficulty(e);
                }
                if ((m = e.getWorld().getBlockAt(e.getLocation()).getType()) != Material.WATER && m != Material.STATIONARY_WATER && e.getFireTicks() <= 20 && (!this.world.hasStorm() || this.world.getBlockAt(e.getLocation()).getBiome() == Biome.DESERT) && difficulty.burnsInSunlight(CdCreatureType.valueOf((Entity)e)) == 1 && CreatureInfo.isInSunlight((Entity)e)) {
                    if ((e instanceof PigZombie || e instanceof Ghast) && e.getNoDamageTicks() == 0) {
                        e.damage(1);
                    }
                    e.setNoDamageTicks(1);
                    e.setFireTicks(300);
                }
            }
            ++i;
        }
        if (Performance.isRunning()) {
            Performance.BISControl += System.nanoTime() - time;
        }
    }
}

