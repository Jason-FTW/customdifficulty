/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Chicken
 *  org.bukkit.entity.Cow
 *  org.bukkit.entity.Creature
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Giant
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Pig
 *  org.bukkit.entity.PigZombie
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Sheep
 *  org.bukkit.entity.Squid
 *  org.bukkit.event.Event
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.entity.EntityDamageEvent$DamageCause
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.scheduler.BukkitScheduler
 *  org.bukkit.util.Vector
 *  org.bukkit.util.config.Configuration
 */
package JasonFTW.CustomDifficulty.SchedulerTasks;

import JasonFTW.CustomDifficulty.CustomDifficulty;
import JasonFTW.CustomDifficulty.commands.Performance;
import JasonFTW.CustomDifficulty.util.Aggressiveness;
import JasonFTW.CustomDifficulty.util.CdCreatureType;
import JasonFTW.CustomDifficulty.util.Config;
import JasonFTW.CustomDifficulty.util.CreatureInfo;
import JasonFTW.CustomDifficulty.util.Difficulty;
import JasonFTW.CustomDifficulty.util.Manager;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Giant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Squid;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;
import org.bukkit.util.config.Configuration;

public class AggressivenessControl
implements Runnable {
    private static final HashMap<World, AggressivenessControl> tasks = new HashMap();
    private final int id;
    private final World world;

    private AggressivenessControl(CustomDifficulty plugin, World world) {
        this.world = world;
        if (!tasks.containsKey((Object)world) && world != null) {
            int interval = AggressivenessControl.getInterval(world);
            this.id = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)plugin, (Runnable)this, 5, (long)interval);
            tasks.put(world, this);
            CustomDifficulty.initializeMessage(Level.INFO, "AggressivenessControl activated for world: " + world.getName() + ". | Interval: " + interval);
        } else {
            this.id = -1;
        }
    }

    public static void activate(CustomDifficulty plugin, World world) {
        Difficulty worldDifficulty = Manager.getDifficulty(world);
        if (worldDifficulty == null) {
            AggressivenessControl.deactivate(world);
            return;
        }
        AggressivenessControl task = tasks.get((Object)world);
        if (task == null || task.id == -1) {
            new JasonFTW.CustomDifficulty.SchedulerTasks.AggressivenessControl(plugin, world);
        }
    }

    public static void activateAllWorlds(CustomDifficulty plugin) {
        for (World w : Bukkit.getServer().getWorlds()) {
            AggressivenessControl.activate(plugin, w);
        }
    }

    public static void deactivate(World world) {
        AggressivenessControl task = tasks.get((Object)world);
        if (task != null && task.id != -1) {
            Bukkit.getServer().getScheduler().cancelTask(task.id);
            tasks.remove((Object)world);
            CustomDifficulty.initializeMessage(Level.INFO, "AggressivenessControl deactivated for world: " + world.getName());
        }
    }

    public static void deactivateAllWorlds() {
        for (World w : Bukkit.getServer().getWorlds()) {
            AggressivenessControl.deactivate(w);
        }
    }

    public static int getInterval(World world) {
        int interval = Config.config.getInt("worlds." + world.getName().replace(" ", "") + ".aggressivenessInterval", 10);
        if (interval < 1) {
            interval = 1;
        }
        return interval;
    }

    @Override
    public void run() {
        long time = System.nanoTime();
        for (Player player : this.world.getPlayers()) {
            List ents = player.getNearbyEntities(17.0, 8.0, 17.0);
            for (Entity e : ents) {
                double dz;
                int damage;
                Difficulty difficulty;
                Location pLoc;
                if (!(e instanceof Creature) || !(e instanceof LivingEntity)) continue;
                if (e instanceof Chicken || e instanceof Pig || e instanceof Cow || e instanceof Sheep || e instanceof PigZombie || e instanceof Squid) {
                    Difficulty difficulty2;
                    Material m;
                    int damage2;
                    Creature creature = (Creature)e;
                    pLoc = player.getLocation();
                    Location cLoc = creature.getLocation();
                    double dx = pLoc.getX() - cLoc.getX();
                    double dy = pLoc.getY() - cLoc.getY();
                    dz = pLoc.getZ() - cLoc.getZ();
                    double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                    if (creature.getTarget() == null && CreatureInfo.getAggressiveness((LivingEntity)creature) == Aggressiveness.AGGRESSIVE && distance < 16.0) {
                        creature.setTarget((LivingEntity)player);
                    }
                    if (creature instanceof Squid && creature.getTarget() == player && ((m = this.world.getBlockAt(cLoc).getType()) == Material.WATER || m == Material.STATIONARY_WATER)) {
                        Material above;
                        double total = Math.abs(dx) + Math.abs(dy) + Math.abs(dz);
                        double speed = 0.15 * (double)AggressivenessControl.getInterval(this.world);
                        double vx = speed * (dx / total);
                        double vz = speed * (dz / total);
                        double vy = speed * (dy / total);
                        if (vy > 0.0 && (above = this.world.getBlockAt((int)cLoc.getX(), (int)cLoc.getY() + 1, (int)cLoc.getZ()).getType()) != Material.WATER && above != Material.STATIONARY_WATER) {
                            vy = 0.0;
                        }
                        creature.setVelocity(new Vector(vx, vy, vz));
                    }
                    if (creature.getTarget() != player || creature.getHealth() <= 0 || player.getNoDamageTicks() != 0 || distance >= 3.0 || (damage2 = (int)(difficulty2 = Manager.useRegion(this.world, "damage") ? Manager.getDifficulty((LivingEntity)e) : Manager.getDifficulty(this.world)).getDamage(CdCreatureType.valueOf((Entity)creature))) <= 0) continue;
                    EntityDamageByEntityEvent edbee = new EntityDamageByEntityEvent(e, (Entity)player, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage2);
                    Bukkit.getServer().getPluginManager().callEvent((Event)edbee);
                    if (edbee.isCancelled() || edbee.getDamage() <= 0) continue;
                    player.damage(edbee.getDamage(), (Entity)creature);
                    continue;
                }
                if (!(e instanceof Giant)) continue;
                Giant giant = (Giant)e;
                pLoc = player.getLocation();
                Location gLoc = giant.getLocation();
                double dy = pLoc.getY() - gLoc.getY();
                double dx = pLoc.getX() - gLoc.getX();
                dz = pLoc.getZ() - gLoc.getZ();
                if (giant.getTarget() == null && CreatureInfo.getAggressiveness((LivingEntity)giant) == Aggressiveness.AGGRESSIVE && Math.sqrt(dx * dx + dy * dy + dz * dz) < 16.0) {
                    giant.setTarget((LivingEntity)player);
                }
                if (giant.getTarget() != player || giant.getHealth() <= 0 || player.getNoDamageTicks() != 0 || (difficulty = Manager.useRegion(this.world, "damage") ? Manager.getDifficulty((LivingEntity)giant) : Manager.getDifficulty(this.world)) == null || dy < -3.0 || dy > 13.0 || Math.sqrt(dx * dx + dz * dz) > 4.5 || (damage = (int)difficulty.getDamage(CdCreatureType.valueOf((Entity)giant))) <= 0) continue;
                EntityDamageByEntityEvent edbee = new EntityDamageByEntityEvent((Entity)giant, (Entity)player, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage);
                Bukkit.getServer().getPluginManager().callEvent((Event)edbee);
                damage = edbee.getDamage();
                if (edbee.isCancelled() || damage <= 0) continue;
                player.damage(damage, (Entity)giant);
            }
        }
        if (Performance.isRunning()) {
            Performance.aggControl += System.nanoTime() - time;
        }
    }
}

