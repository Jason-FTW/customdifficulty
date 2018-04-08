package jasonftw.CustomDifficulty.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public abstract class CreatureInfo {
    public static Aggressiveness getAggressiveness(LivingEntity e) {
        World world = e.getWorld();
        Difficulty difficulty = Manager.useRegion(world, "aggressiveness") ? Manager.getDifficulty(e) : Manager.getDifficulty(world);
        if (difficulty == null) {
            return Aggressiveness.NULL;
        }
        if (Manager.isDay(world)) {
            return difficulty.getAggressivenessDay(CdCreatureType.valueOf((Entity)e));
        }
        return difficulty.getAggressivenessNight(CdCreatureType.valueOf((Entity)e));
    }

	public static EntityType getCreatureTypeFromName(String name) {
        name = name.replace(" ", "");
        if ((name = name.replace("_", "")).equalsIgnoreCase("cavespider")) {
            return EntityType.CAVE_SPIDER;
        }
        if (name.equalsIgnoreCase("chicken")) {
            return EntityType.CHICKEN;
        }
        if (name.equalsIgnoreCase("cow")) {
            return EntityType.COW;
        }
        if (name.equalsIgnoreCase("creeper")) {
            return EntityType.CREEPER;
        }
        if (name.equalsIgnoreCase("enderman")) {
            return EntityType.ENDERMAN;
        }
        if (name.equalsIgnoreCase("ghast")) {
            return EntityType.GHAST;
        }
        if (name.equalsIgnoreCase("giant")) {
            return EntityType.GIANT;
        }
        if (name.equalsIgnoreCase("pig")) {
            return EntityType.PIG;
        }
        if (name.equalsIgnoreCase("pigzombie")) {
            return EntityType.PIG_ZOMBIE;
        }
        if (name.equalsIgnoreCase("sheep")) {
            return EntityType.SHEEP;
        }
        if (name.equalsIgnoreCase("silverfish")) {
            return EntityType.SILVERFISH;
        }
        if (name.equalsIgnoreCase("skeleton")) {
            return EntityType.SKELETON;
        }
        if (name.equalsIgnoreCase("slime")) {
            return EntityType.SLIME;
        }
        if (name.equalsIgnoreCase("squid")) {
            return EntityType.SQUID;
        }
        if (name.equalsIgnoreCase("spider")) {
            return EntityType.SPIDER;
        }
        if (name.equalsIgnoreCase("wolf")) {
            return EntityType.WOLF;
        }
        if (name.equalsIgnoreCase("zombie")) {
            return EntityType.ZOMBIE;
        }
        if (name.equalsIgnoreCase("monster")) {
            return EntityType.MONSTER;
        }
        return null;
    }

    public static int getDamage(EntityDamageByEntityEvent event) {
        Entity e = event.getDamager();
        Difficulty difficulty = null;
        if (e instanceof Wolf && ((Wolf)e).isTamed()) {
            difficulty = Manager.getDifficulty(e.getWorld());
        } else if (e instanceof LivingEntity) {
            difficulty = Manager.getDifficulty((LivingEntity)e);
        }
        if (difficulty == null) {
            return -1;
        }
        CdCreatureType cd = CdCreatureType.valueOf(e);
        if (cd == null) {
            return -1;
        }
        double damage = difficulty.getDamage(cd);
        if (damage == -1.0) {
            return (int) event.getDamage();
        }
        if (e instanceof Creeper) {
            return (int)Math.round((double)event.getDamage() * damage);
        }
        return (int)damage;
    }

    public static int getHealth(LivingEntity entity, Difficulty difficulty) {
        World world = entity.getWorld();
        if (entity instanceof Wolf && ((Wolf)entity).isTamed() || !Manager.useRegion(world, "mobHP")) {
            difficulty = Manager.getDifficulty(world);
        }
        if (difficulty == null) {
            return -1;
        }
        CdCreatureType cd = CdCreatureType.valueOf((Entity)entity);
        if (cd == null) {
            return -1;
        }
        int hp = difficulty.getHealth(cd);
        return hp;
    }

    public static boolean isInSunlight(Entity e) {
        World world = e.getWorld();
        if (world.getEnvironment() != World.Environment.NETHER && Manager.isDay(world)) {
            int skyLimit = 127;
            int x = (int)e.getLocation().getX();
            int y = (int)e.getLocation().getY() + 1;
            int z = (int)e.getLocation().getZ();
            int i = y;
            while (i <= 127) {
                Material material = world.getBlockAt(x, i, z).getType();
                if (material != Material.AIR && material != Material.GLASS && material != Material.LEAVES) {
                    return false;
                }
                ++i;
            }
            return true;
        }
        return false;
    }

    public static boolean isMobSpawnerClose(Location l, double radius) {
        if (radius <= 0.0) {
            return false;
        }
        World w = l.getWorld();
        int minX = (int)Math.round((double)l.getBlockX() - radius);
        int minY = (int)Math.round((double)l.getBlockY() - radius);
        int minZ = (int)Math.round((double)l.getBlockZ() - radius);
        int maxX = (int)Math.round((double)l.getBlockX() + radius);
        int maxY = (int)Math.round((double)l.getBlockY() + radius);
        int maxZ = (int)Math.round((double)l.getBlockZ() + radius);
        int x = minX;
        while (x <= maxX) {
            int y = minY;
            while (y <= maxY) {
                int z = minZ;
                while (z <= maxZ) {
                    if (w.getBlockAt(x, y, z).getType() == Material.MOB_SPAWNER) {
                        return true;
                    }
                    ++z;
                }
                ++y;
            }
            ++x;
        }
        return false;
    }
}

