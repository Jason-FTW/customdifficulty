/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Chunk
 *  org.bukkit.DyeColor
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.entity.CreatureType
 *  org.bukkit.entity.Creeper
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Sheep
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitScheduler
 *  org.bukkit.util.config.Configuration
 */
package JasonFTW.CustomDifficulty.SchedulerTasks;

import JasonFTW.CustomDifficulty.CustomDifficulty;
import JasonFTW.CustomDifficulty.SchedulerTasks.MobCounter;
import JasonFTW.CustomDifficulty.commands.Performance;
import JasonFTW.CustomDifficulty.util.Aggressiveness;
import JasonFTW.CustomDifficulty.util.CdCreatureType;
import JasonFTW.CustomDifficulty.util.Config;
import JasonFTW.CustomDifficulty.util.CreatureInfo;
import JasonFTW.CustomDifficulty.util.Difficulty;
import JasonFTW.CustomDifficulty.util.DifficultyDefaults;
import JasonFTW.CustomDifficulty.util.Manager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.config.Configuration;
import org.bukkit.configuration.*;

public class SpawnControl
implements Runnable {
    private static final HashMap<World, SpawnControl> tasks = new HashMap();
    private static Random rand = new Random();
    private int id;
    private final World world;
    private int worldAggressiveLimit;
    private int worldFriendlyLimit;
    private int worldPassiveLimit;
    private int serverMobLimit;
    private int chunkMobLimit;
    private static /* synthetic */ int[] $SWITCH_TABLE$Pasukaru$CustomDifficulty$util$Aggressiveness;
    private static /* synthetic */ int[] $SWITCH_TABLE$org$bukkit$entity$CreatureType;

    private SpawnControl(CustomDifficulty plugin, World world) {
        if (!tasks.containsKey((Object)world) && world != null) {
            this.world = world;
            this.serverMobLimit = Manager.getMobLimit();
            this.chunkMobLimit = Manager.getMobChunkLimit(world);
            this.worldAggressiveLimit = Manager.getMobAggressiveLimit(world);
            this.worldFriendlyLimit = Manager.getMobFriendlyLimit(world);
            this.worldPassiveLimit = Manager.getMobPassiveLimit(world);
            int interval = SpawnControl.getInterval(world);
            this.id = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)plugin, (Runnable)this, 5, (long)interval);
            tasks.put(world, this);
            CustomDifficulty.initializeMessage(Level.INFO, "Spawning activated for world " + world.getName() + ". | Interval: " + interval);
        } else {
            this.world = null;
            this.id = -1;
        }
    }

    public static void activate(CustomDifficulty plugin, World world) {
        Difficulty worldDifficulty = Manager.getDifficulty(world);
        if (worldDifficulty == null) {
            CustomDifficulty.initializeMessage(Level.WARNING, "Spawning will not be activated for world " + world.getName() + ". World's difficulty is invalid and ignoring is disabled.");
            SpawnControl.deactivate(world);
            return;
        }
        SpawnControl task = tasks.get((Object)world);
        if (task == null) {
            new JasonFTW.CustomDifficulty.SchedulerTasks.SpawnControl(plugin, world);
        } else if (task.id == -1) {
            tasks.remove((Object)world);
            new JasonFTW.CustomDifficulty.SchedulerTasks.SpawnControl(plugin, world);
        }
    }

    public static void activateAllWorlds(CustomDifficulty plugin) {
        for (World w : Bukkit.getServer().getWorlds()) {
            SpawnControl.activate(plugin, w);
        }
    }

    public static void deactivate(World world) {
        SpawnControl task = tasks.get((Object)world);
        if (task != null && task.id != -1) {
            Bukkit.getServer().getScheduler().cancelTask(task.id);
            task.id = -1;
            tasks.remove((Object)world);
            CustomDifficulty.initializeMessage(Level.INFO, "Spawning deactivated for world " + world.getName());
        }
    }

    public static void deactivateAllWorlds() {
        for (World w : Bukkit.getServer().getWorlds()) {
            SpawnControl.deactivate(w);
        }
    }

    public static int getInterval(World world) {
        int interval = Config.config.getInt("worlds." + world.getName().replace(" ", "") + ".spawnInterval", 5);
        if (interval < 1) {
            interval = 1;
        }
        return interval;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    @Override
    public void run() {
        time = System.nanoTime();
        worldDifficulty = Manager.getDifficulty(this.world);
        if (worldDifficulty == null) {
            if (Performance.isRunning() == false) return;
            Performance.spawnAlgorithm += System.nanoTime() - time;
            return;
        }
        mobsOnServer = MobCounter.getMobsOnServer();
        mobsA = MobCounter.getAggressive(this.world);
        mobsF = MobCounter.getFriendly(this.world);
        mobsP = MobCounter.getPassive(this.world);
        if (mobsOnServer >= this.serverMobLimit) {
            if (Performance.isRunning() == false) return;
            Performance.spawnAlgorithm += System.nanoTime() - time;
            return;
        }
        possibleChunks = new ArrayList<Chunk>();
        players = this.world.getPlayers();
        if (players.size() < 1) {
            if (Performance.isRunning() == false) return;
            Performance.spawnAlgorithm += System.nanoTime() - time;
            return;
        }
        Collections.shuffle(players);
        randomPlayer = (Player)players.get(0);
        centerX = this.world.getChunkAt(randomPlayer.getLocation()).getX();
        centerZ = this.world.getChunkAt(randomPlayer.getLocation()).getZ();
        x = centerX - 8;
        while (x <= centerX + 8) {
            z = centerZ - 8;
            while (z <= centerZ + 8) {
                c = this.world.getChunkAt(x, z);
                if (Manager.countMobs(c) < this.chunkMobLimit) {
                    possibleChunks.add(c);
                }
                ++z;
            }
            ++x;
        }
        Collections.shuffle(possibleChunks);
        minDistance = Config.config.getDouble("global.minSpawnDistance", 24.0);
        maxDistance = Config.config.getDouble("global.maxSpawnDistance", 222.0);
        if (minDistance < 0.0) {
            minDistance = 0.0;
        } else if (minDistance > 128.0) {
            minDistance = 128.0;
        }
        if (maxDistance < 0.0) {
            maxDistance = 0.0;
        }
        useRegions = Manager.useRegion(this.world, "spawnAlgorithm");
        block21 : for (Chunk currentChunk : possibleChunks) {
            if (mobsOnServer >= this.serverMobLimit && Performance.isRunning()) {
                Performance.spawnAlgorithm += System.nanoTime() - time;
                return;
            }
            randomX = currentChunk.getX() * 16 + SpawnControl.rand.nextInt(16);
            randomY = SpawnControl.rand.nextInt(128);
            randomZ = currentChunk.getZ() * 16 + SpawnControl.rand.nextInt(16);
            l = new Location(this.world, (double)randomX, (double)randomY, (double)randomZ);
            if (useRegions) {
                difficulty = Manager.getDifficulty(l);
                if (difficulty == null) {
                    difficulty = worldDifficulty;
                }
            } else {
                difficulty = worldDifficulty;
            }
            if (difficulty == null) continue;
            types = difficulty.getCreatureTypes();
            if (types.size() < 1) {
                if (Performance.isRunning() == false) return;
                Performance.spawnAlgorithm += System.nanoTime() - time;
                return;
            }
            Collections.shuffle(types);
            creatureType = types.get(0);
            if (creatureType == null) {
                if (Performance.isRunning() == false) return;
                Performance.spawnAlgorithm += System.nanoTime() - time;
                return;
            }
            customCreatureType = CdCreatureType.valueOf(creatureType);
            if (difficulty.getSpawnChance(customCreatureType) <= SpawnControl.rand.nextDouble() * 100.0) continue;
            if (Manager.useRegion(this.world, "aggressiveness")) {
                d = Manager.getDifficulty(l);
                if (d == null) {
                    d = difficulty;
                }
                aggressiveness = Manager.isDay(this.world) ? d.getAggressivenessDay(customCreatureType) : d.getAggressivenessNight(customCreatureType);
            } else {
                aggressiveness = Manager.isDay(this.world) != false ? difficulty.getAggressivenessDay(customCreatureType) : difficulty.getAggressivenessNight(customCreatureType);
            }
            switch (SpawnControl.$SWITCH_TABLE$Pasukaru$CustomDifficulty$util$Aggressiveness()[aggressiveness.ordinal()]) {
                case 1: {
                    if (mobsA < this.worldAggressiveLimit) break;
                    ** break;
                }
                case 2: {
                    if (mobsF < this.worldFriendlyLimit) break;
                    ** break;
                }
                case 4: {
                    if (mobsP >= this.worldPassiveLimit) continue block21;
                }
            }
            spawn = this.world.getBlockAt(randomX, randomY, randomZ);
            below = this.world.getBlockAt(randomX, randomY - 1, randomZ);
            above = this.world.getBlockAt(randomX, randomY + 1, randomZ);
            height = spawn.getY();
            minHeight = difficulty.getMinSpawnHeight(customCreatureType);
            maxHeight = difficulty.getMaxSpawnHeight(customCreatureType);
            if (minHeight == -1) {
                minHeight = 0;
            }
            if (maxHeight == -1) {
                maxHeight = DifficultyDefaults.getMaxSpawnHeight(creatureType);
            }
            if (height > maxHeight || height < minHeight) continue;
            lightLevel = spawn.getLightLevel();
            minLightLevel = difficulty.getMinSpawnLightLevel(customCreatureType);
            maxLightLevel = difficulty.getMaxSpawnLightLevel(customCreatureType);
            if (minLightLevel == -1) {
                minLightLevel = DifficultyDefaults.getMinSpawnLightLevel(creatureType);
            }
            if (maxLightLevel == -1) {
                maxLightLevel = DifficultyDefaults.getMaxSpawnLightLevel(creatureType);
            }
            if (lightLevel > maxLightLevel || lightLevel < minLightLevel) continue;
            spawnMaterial = spawn.getType();
            belowMaterial = below.getType();
            aboveMaterial = above.getType();
            if (creatureType == CreatureType.SQUID ? spawnMaterial != Material.WATER && spawnMaterial != Material.STATIONARY_WATER || belowMaterial != Material.WATER && belowMaterial != Material.STATIONARY_WATER : spawnMaterial != Material.AIR && spawnMaterial != Material.SNOW || belowMaterial == Material.AIR || belowMaterial == Material.WATER || belowMaterial == Material.STATIONARY_WATER || belowMaterial == Material.LAVA || belowMaterial == Material.STATIONARY_LAVA || spawnMaterial == Material.WATER || spawnMaterial == Material.STATIONARY_WATER || spawnMaterial == Material.LAVA || spawnMaterial == Material.STATIONARY_LAVA || aboveMaterial != Material.AIR) continue;
            switch (SpawnControl.$SWITCH_TABLE$org$bukkit$entity$CreatureType()[creatureType.ordinal()]) {
                case 17: {
                    if (this.world.getBlockAt(randomX, randomY + 2, randomZ).getType() == Material.AIR) break;
                    ** break;
                }
                case 5: {
                    y = 0;
                    while (y < 15) {
                        x = -1;
                        while (x < 2) {
                            z = -1;
                            while (z < 2) {
                                if (this.world.getBlockAt(randomX + x, randomY + y, randomZ + z).getType() != Material.AIR) {
                                    // empty if block
                                }
                                ++z;
                            }
                            ++x;
                        }
                        ++y;
                    }
                    break block5;
                }
            }
            spawnOnlyOn = difficulty.getSpawnOnlyOn(customCreatureType);
            spawnNotOn = difficulty.getSpawnNotOn(customCreatureType);
            if (spawnOnlyOn.size() > 0 && !spawnOnlyOn.contains((Object)belowMaterial) || spawnNotOn.contains((Object)belowMaterial)) continue;
            c0ntinue = false;
            for (Player p : players) {
                pl = p.getLocation();
                dx = pl.getX() - (double)randomX;
                distance = Math.sqrt(dx * dx + (dy = pl.getY() - (double)randomY) * dy + (dz = pl.getZ() - (double)randomZ) * dz);
                if (distance >= minDistance && distance <= maxDistance) continue;
                c0ntinue = true;
                break;
            }
            if (c0ntinue) continue;
            if (creatureType != CreatureType.SPIDER) {
                l.setX(l.getX() + 0.5);
                l.setZ(l.getZ() + 0.5);
            }
            if ((creature = this.world.spawnCreature(l, creatureType)) == null) continue;
            ++mobsOnServer;
            switch (SpawnControl.$SWITCH_TABLE$Pasukaru$CustomDifficulty$util$Aggressiveness()[CreatureInfo.getAggressiveness(creature).ordinal()]) {
                case 1: {
                    ++mobsA;
                    break;
                }
                case 2: {
                    ++mobsF;
                    break;
                }
                case 4: {
                    ++mobsP;
                }
            }
            if (creatureType == CreatureType.CREEPER && difficulty.getElectrifiedCreeperChance() > SpawnControl.rand.nextDouble() * 100.0) {
                ((Creeper)creature).setPowered(true);
                ** break;
            }
            if (creatureType == CreatureType.SPIDER && difficulty.getSpiderJockeyChance() > SpawnControl.rand.nextDouble() * 100.0) {
                skeleton = this.world.spawnCreature(l, CreatureType.SKELETON);
                creature.setPassenger((Entity)skeleton);
                ++mobsOnServer;
                switch (SpawnControl.$SWITCH_TABLE$Pasukaru$CustomDifficulty$util$Aggressiveness()[CreatureInfo.getAggressiveness(creature).ordinal()]) {
                    case 1: {
                        ++mobsA;
                        ** break;
                    }
                    case 2: {
                        ++mobsF;
                        ** break;
                    }
                    case 4: {
                        ++mobsP;
                    }
                }
                ** break;
            }
            if (creatureType != CreatureType.SHEEP) continue;
            colors = difficulty.getSheepColors();
            r = SpawnControl.rand.nextInt(colors[16]) + 1;
            color = DyeColor.WHITE;
            i = 0;
            t = 0;
            while (i < 16) {
                if (r <= (t = (int)((byte)(t + colors[i])))) {
                    color = DyeColor.getByData((byte)i);
                    break;
                }
                i = (byte)(i + 1);
            }
            ((Sheep)creature).setColor(color);
lbl189: // 8 sources:
        }
        if (Performance.isRunning() == false) return;
        Performance.spawnAlgorithm += System.nanoTime() - time;
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

