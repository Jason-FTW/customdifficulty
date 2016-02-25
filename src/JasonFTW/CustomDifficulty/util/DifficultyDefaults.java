/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 *  org.bukkit.entity.Chicken
 *  org.bukkit.entity.Cow
 *  org.bukkit.entity.CreatureType
 *  org.bukkit.entity.Creeper
 *  org.bukkit.entity.Enderman
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Ghast
 *  org.bukkit.entity.Giant
 *  org.bukkit.entity.Pig
 *  org.bukkit.entity.PigZombie
 *  org.bukkit.entity.Sheep
 *  org.bukkit.entity.Skeleton
 *  org.bukkit.entity.Slime
 *  org.bukkit.entity.Spider
 *  org.bukkit.entity.Squid
 *  org.bukkit.entity.Wolf
 *  org.bukkit.entity.Zombie
 */
package JasonFTW.CustomDifficulty.util;

import JasonFTW.CustomDifficulty.util.Aggressiveness;
import JasonFTW.CustomDifficulty.util.Manager;
import org.bukkit.World;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
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
    private static /* synthetic */ int[] $SWITCH_TABLE$org$bukkit$entity$CreatureType;

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

    public static int getMaxSpawnHeight(CreatureType ct) {
        if (ct == CreatureType.SLIME) {
            return 15;
        }
        return 127;
    }

    public static byte getMaxSpawnLightLevel(CreatureType ct) {
        if (ct == null) {
            return 15;
        }
        switch (DifficultyDefaults.$SWITCH_TABLE$org$bukkit$entity$CreatureType()[ct.ordinal()]) {
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

    public static byte getMinSpawnLightLevel(CreatureType ct) {
        if (ct == null) {
            return 0;
        }
        switch (DifficultyDefaults.$SWITCH_TABLE$org$bukkit$entity$CreatureType()[ct.ordinal()]) {
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

    static /* synthetic */ int[] $SWITCH_TABLE$org$bukkit$entity$CreatureType() {
        int[] arrn;
        int[] arrn2 = $SWITCH_TABLE$org$bukkit$entity$CreatureType;
        if (arrn2 != null) {
            return arrn2;
        }
        arrn = new int[CreatureType.values().length];
        try {
            arrn[CreatureType.CAVE_SPIDER.ordinal()] = 16;
        }
        catch (NoSuchFieldError v1) {}
        try {
            arrn[CreatureType.CHICKEN.ordinal()] = 1;
        }
        catch (NoSuchFieldError v2) {}
        try {
            arrn[CreatureType.COW.ordinal()] = 2;
        }
        catch (NoSuchFieldError v3) {}
        try {
            arrn[CreatureType.CREEPER.ordinal()] = 3;
        }
        catch (NoSuchFieldError v4) {}
        try {
            arrn[CreatureType.ENDERMAN.ordinal()] = 17;
        }
        catch (NoSuchFieldError v5) {}
        try {
            arrn[CreatureType.GHAST.ordinal()] = 4;
        }
        catch (NoSuchFieldError v6) {}
        try {
            arrn[CreatureType.GIANT.ordinal()] = 5;
        }
        catch (NoSuchFieldError v7) {}
        try {
            arrn[CreatureType.MONSTER.ordinal()] = 6;
        }
        catch (NoSuchFieldError v8) {}
        try {
            arrn[CreatureType.PIG.ordinal()] = 7;
        }
        catch (NoSuchFieldError v9) {}
        try {
            arrn[CreatureType.PIG_ZOMBIE.ordinal()] = 8;
        }
        catch (NoSuchFieldError v10) {}
        try {
            arrn[CreatureType.SHEEP.ordinal()] = 9;
        }
        catch (NoSuchFieldError v11) {}
        try {
            arrn[CreatureType.SILVERFISH.ordinal()] = 18;
        }
        catch (NoSuchFieldError v12) {}
        try {
            arrn[CreatureType.SKELETON.ordinal()] = 10;
        }
        catch (NoSuchFieldError v13) {}
        try {
            arrn[CreatureType.SLIME.ordinal()] = 11;
        }
        catch (NoSuchFieldError v14) {}
        try {
            arrn[CreatureType.SPIDER.ordinal()] = 12;
        }
        catch (NoSuchFieldError v15) {}
        try {
            arrn[CreatureType.SQUID.ordinal()] = 13;
        }
        catch (NoSuchFieldError v16) {}
        try {
            arrn[CreatureType.WOLF.ordinal()] = 15;
        }
        catch (NoSuchFieldError v17) {}
        try {
            arrn[CreatureType.ZOMBIE.ordinal()] = 14;
        }
        catch (NoSuchFieldError v18) {}
        $SWITCH_TABLE$org$bukkit$entity$CreatureType = arrn;
        return $SWITCH_TABLE$org$bukkit$entity$CreatureType;
    }
}

