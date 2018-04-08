package jasonftw.CustomDifficulty.util;

import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;

public abstract class DifficultyDefaults {
    private static /* synthetic */ int[] $SWITCH_TABLE$org$bukkit$entity$EntityType;

    public static boolean burnsInSunlight(Entity e) {
        if (e instanceof PigZombie) {
            return false;
        }
        if (e instanceof Skeleton || e instanceof Enderman || e instanceof Zombie && !(e instanceof PigZombie)) {
            return true;
        }
        return false;
    }

    public static Aggressiveness getAggressivenessDay(Entity e) {
        if (e instanceof Chicken) {
            return Aggressiveness.FRIENDLY;
        }
        if (e instanceof Cow) {
            return Aggressiveness.FRIENDLY;
        }
        if (e instanceof Creeper) {
            return Aggressiveness.AGGRESSIVE;
        }
        if (e instanceof Enderman) {
            return Aggressiveness.PASSIVE;
        }
        if (e instanceof Ghast) {
            return Aggressiveness.AGGRESSIVE;
        }
        if (e instanceof Giant) {
            return Aggressiveness.AGGRESSIVE;
        }
        if (e instanceof Pig) {
            return Aggressiveness.FRIENDLY;
        }
        if (e instanceof PigZombie) {
            return Aggressiveness.PASSIVE;
        }
        if (e instanceof Sheep) {
            return Aggressiveness.FRIENDLY;
        }
        if (e instanceof Skeleton) {
            return Aggressiveness.AGGRESSIVE;
        }
        if (e instanceof Slime) {
            return Aggressiveness.AGGRESSIVE;
        }
        if (e instanceof Spider) {
            return Manager.isDay(e.getWorld()) ? Aggressiveness.PASSIVE : Aggressiveness.AGGRESSIVE;
        }
        if (e instanceof Squid) {
            return Aggressiveness.FRIENDLY;
        }
        if (e instanceof Wolf) {
            return Aggressiveness.PASSIVE;
        }
        if (e instanceof Zombie) {
            return Aggressiveness.AGGRESSIVE;
        }
        if (e.getClass().getSimpleName().equals("CraftMonster")) {
            return Aggressiveness.AGGRESSIVE;
        }
        return null;
    }

    public static int getHealth(Entity e) {
        if (e == null) {
            return 20;
        }
        if (e instanceof Chicken) {
            return 4;
        }
        if (e instanceof Cow) {
            return 10;
        }
        if (e instanceof Creeper) {
            return 20;
        }
        if (e instanceof Enderman) {
            return 20;
        }
        if (e instanceof Ghast) {
            return 10;
        }
        if (e instanceof Giant) {
            return 50;
        }
        if (e instanceof Pig) {
            return 10;
        }
        if (e instanceof PigZombie) {
            return 20;
        }
        if (e instanceof Sheep) {
            return 10;
        }
        if (e instanceof Skeleton) {
            return 20;
        }
        if (e instanceof Slime) {
            switch (((Slime)e).getSize()) {
                case 1: {
                    return 1;
                }
                case 2: {
                    return 4;
                }
                case 4: {
                    return 16;
                }
            }
            return 32;
        }
        if (e instanceof Spider) {
            return 20;
        }
        if (e instanceof Squid) {
            return 10;
        }
        if (e instanceof Wolf) {
            return ((Wolf)e).isTamed() ? 20 : 8;
        }
        if (e instanceof Zombie) {
            return 20;
        }
        if (e.getClass().getSimpleName().equals("CraftMonster")) {
            return 20;
        }
        return 20;
    }

    public static int getMaxSpawnHeight(EntityType et) {
        if (et == EntityType.SLIME) {
            return 15;
        }
        return 127;
    }

    public static byte getMaxSpawnLightLevel(EntityType et) {
        if (et == null) {
            return 15;
        }
        switch (DifficultyDefaults.$SWITCH_TABLE$org$bukkit$entity$EntityType()[et.ordinal()]) {
            case 3: 
            case 10: 
            case 12: 
            case 14: 
            case 17: {
                return 7;
            }
            case 1: 
            case 2: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 9: 
            case 11: 
            case 13: 
            case 15: {
                return 15;
            }
        }
        return 15;
    }

    public static byte getMinSpawnLightLevel(EntityType et) {
        if (et == null) {
            return 0;
        }
        switch (DifficultyDefaults.$SWITCH_TABLE$org$bukkit$entity$EntityType()[et.ordinal()]) {
            case 15: {
                return 7;
            }
            case 1: 
            case 2: 
            case 7: 
            case 9: {
                return 9;
            }
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 8: 
            case 10: 
            case 11: 
            case 12: 
            case 13: 
            case 14: {
                return 0;
            }
        }
        return 0;
    }

    static /* synthetic */ int[] $SWITCH_TABLE$org$bukkit$entity$EntityType() {
        int[] arrn;
        int[] arrn2 = $SWITCH_TABLE$org$bukkit$entity$EntityType;
        if (arrn2 != null) {
            return arrn2;
        }
        arrn = new int[EntityType.values().length];
        try {
            arrn[EntityType.CAVE_SPIDER.ordinal()] = 16;
        }
        catch (NoSuchFieldError v1) {}
        try {
            arrn[EntityType.CHICKEN.ordinal()] = 1;
        }
        catch (NoSuchFieldError v2) {}
        try {
            arrn[EntityType.COW.ordinal()] = 2;
        }
        catch (NoSuchFieldError v3) {}
        try {
            arrn[EntityType.CREEPER.ordinal()] = 3;
        }
        catch (NoSuchFieldError v4) {}
        try {
            arrn[EntityType.ENDERMAN.ordinal()] = 17;
        }
        catch (NoSuchFieldError v5) {}
        try {
            arrn[EntityType.GHAST.ordinal()] = 4;
        }
        catch (NoSuchFieldError v6) {}
        try {
            arrn[EntityType.GIANT.ordinal()] = 5;
        }
        catch (NoSuchFieldError v7) {}
        try {
            arrn[EntityType.MONSTER.ordinal()] = 6;
        }
        catch (NoSuchFieldError v8) {}
        try {
            arrn[EntityType.PIG.ordinal()] = 7;
        }
        catch (NoSuchFieldError v9) {}
        try {
            arrn[EntityType.PIG_ZOMBIE.ordinal()] = 8;
        }
        catch (NoSuchFieldError v10) {}
        try {
            arrn[EntityType.SHEEP.ordinal()] = 9;
        }
        catch (NoSuchFieldError v11) {}
        try {
            arrn[EntityType.SILVERFISH.ordinal()] = 18;
        }
        catch (NoSuchFieldError v12) {}
        try {
            arrn[EntityType.SKELETON.ordinal()] = 10;
        }
        catch (NoSuchFieldError v13) {}
        try {
            arrn[EntityType.SLIME.ordinal()] = 11;
        }
        catch (NoSuchFieldError v14) {}
        try {
            arrn[EntityType.SPIDER.ordinal()] = 12;
        }
        catch (NoSuchFieldError v15) {}
        try {
            arrn[EntityType.SQUID.ordinal()] = 13;
        }
        catch (NoSuchFieldError v16) {}
        try {
            arrn[EntityType.WOLF.ordinal()] = 15;
        }
        catch (NoSuchFieldError v17) {}
        try {
            arrn[EntityType.ZOMBIE.ordinal()] = 14;
        }
        catch (NoSuchFieldError v18) {}
        $SWITCH_TABLE$org$bukkit$entity$EntityType = arrn;
        return $SWITCH_TABLE$org$bukkit$entity$EntityType;
    }
}

