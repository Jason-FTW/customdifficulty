package jasonftw.CustomDifficulty.SchedulerTasks;

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
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.plugin.Plugin;

import jasonftw.CustomDifficulty.CustomDifficulty;
import jasonftw.CustomDifficulty.commands.Performance;
import jasonftw.CustomDifficulty.util.Aggressiveness;
import jasonftw.CustomDifficulty.util.CdCreatureType;
import jasonftw.CustomDifficulty.util.Config;
import jasonftw.CustomDifficulty.util.CreatureInfo;
import jasonftw.CustomDifficulty.util.Difficulty;
import jasonftw.CustomDifficulty.util.DifficultyDefaults;
import jasonftw.CustomDifficulty.util.Manager;

public class SpawnControl implements Runnable {
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
	private static /* synthetic */ int[] $SWITCH_TABLE$org$bukkit$entity$EntityType;

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
			new jasonftw.CustomDifficulty.SchedulerTasks.SpawnControl(plugin, world);
		} else if (task.id == -1) {
			tasks.remove((Object)world);
			new jasonftw.CustomDifficulty.SchedulerTasks.SpawnControl(plugin, world);
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
		long time = System.nanoTime();
		Difficulty worldDifficulty = Manager.getDifficulty(this.world);
		if (worldDifficulty == null) {
			if (Performance.isRunning() == false) return;
			Performance.spawnAlgorithm += System.nanoTime() - time;
			return;
		}
		int mobsOnServer = MobCounter.getMobsOnServer();
		int mobsA = MobCounter.getAggressive(this.world);
		int mobsF = MobCounter.getFriendly(this.world);
		int mobsP = MobCounter.getPassive(this.world);
		if (mobsOnServer >= this.serverMobLimit) {
			if (Performance.isRunning() == false) return;
			Performance.spawnAlgorithm += System.nanoTime() - time;
			return;
		}
		List<Chunk> possibleChunks = new ArrayList<Chunk>();
		List<Player> players = this.world.getPlayers();
		if (players.size() < 1) {
			if (Performance.isRunning() == false) return;
			Performance.spawnAlgorithm += System.nanoTime() - time;
			return;
		}
		Collections.shuffle(players);
		Player randomPlayer = (Player)players.get(0);
		int centerX = this.world.getChunkAt(randomPlayer.getLocation()).getX();
		int centerZ = this.world.getChunkAt(randomPlayer.getLocation()).getZ();
		int x = centerX - 8;
		while (x <= centerX + 8) {
			int z = centerZ - 8;
			while (z <= centerZ + 8) {
				Chunk c = this.world.getChunkAt(x, z);
				if (Manager.countMobs(c) < this.chunkMobLimit) {
					possibleChunks.add(c);
				}
				++z;
			}
			++x;
		}
		Collections.shuffle(possibleChunks);
		double minDistance = Config.config.getDouble("global.minSpawnDistance", 24.0);
		double maxDistance = Config.config.getDouble("global.maxSpawnDistance", 222.0);
		if (minDistance < 0.0) {
			minDistance = 0.0;
		} else if (minDistance > 128.0) {
			minDistance = 128.0;
		}
		if (maxDistance < 0.0) {
			maxDistance = 0.0;
		}
		boolean useRegions = Manager.useRegion(this.world, "spawnAlgorithm");
		block21 : for (Chunk currentChunk : possibleChunks) {
			if (mobsOnServer >= this.serverMobLimit && Performance.isRunning()) {
				Performance.spawnAlgorithm += System.nanoTime() - time;
				return;
			}
			int randomX = currentChunk.getX() * 16 + SpawnControl.rand.nextInt(16);
			int randomY = SpawnControl.rand.nextInt(128);
			int randomZ = currentChunk.getZ() * 16 + SpawnControl.rand.nextInt(16);
			Location l = new Location(this.world, (double)randomX, (double)randomY, (double)randomZ);
			Difficulty difficulty;
			if (useRegions) {
				difficulty = Manager.getDifficulty(l);
				if (difficulty == null) {
					difficulty = worldDifficulty;
				}
			} else {
				difficulty = worldDifficulty;
			}
			if (difficulty == null) continue;
			List<EntityType> types = difficulty.getCreatureTypes();
			if (types.size() < 1) {
				if (Performance.isRunning() == false) return;
				Performance.spawnAlgorithm += System.nanoTime() - time;
				return;
			}
			Collections.shuffle(types);
			EntityType EntityType = types.get(0);
			if (EntityType == null) {
				if (Performance.isRunning() == false) return;
				Performance.spawnAlgorithm += System.nanoTime() - time;
				return;
			}
			EntityType customCreatureType = CdCreatureType.valueOf(EntityType);
			if (difficulty.getSpawnChance(customCreatureType) <= SpawnControl.rand.nextDouble() * 100.0) continue;
			if (Manager.useRegion(this.world, "aggressiveness")) {
				Difficulty d = Manager.getDifficulty(l);
				if (d == null) {
					d = difficulty;
				}
				Aggressiveness aggressiveness = Manager.isDay(this.world) ? d.getAggressivenessDay(customCreatureType) : d.getAggressivenessNight(customCreatureType);
			} else {
				Aggressiveness aggressiveness = Manager.isDay(this.world) != false ? difficulty.getAggressivenessDay(customCreatureType) : difficulty.getAggressivenessNight(customCreatureType);
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
			Block spawn = this.world.getBlockAt(randomX, randomY, randomZ);
			Block below = this.world.getBlockAt(randomX, randomY - 1, randomZ);
			Block above = this.world.getBlockAt(randomX, randomY + 1, randomZ);
			int height = spawn.getY();
			int minHeight = difficulty.getMinSpawnHeight(customCreatureType);
			int maxHeight = difficulty.getMaxSpawnHeight(customCreatureType);
			if (minHeight == -1) {
				minHeight = 0;
			}
			if (maxHeight == -1) {
				maxHeight = DifficultyDefaults.getMaxSpawnHeight(EntityType);
			}
			if (height > maxHeight || height < minHeight) continue;
			int lightLevel = spawn.getLightLevel();
			int minLightLevel = difficulty.getMinSpawnLightLevel(customCreatureType);
			int maxLightLevel = difficulty.getMaxSpawnLightLevel(customCreatureType);
			if (minLightLevel == -1) {
				minLightLevel = DifficultyDefaults.getMinSpawnLightLevel(EntityType);
			}
			if (maxLightLevel == -1) {
				maxLightLevel = DifficultyDefaults.getMaxSpawnLightLevel(EntityType);
			}
			if (lightLevel > maxLightLevel || lightLevel < minLightLevel) continue;
			Material spawnMaterial = spawn.getType();
			Material belowMaterial = below.getType();
			Material aboveMaterial = above.getType();
			if (EntityType == EntityType.SQUID ? spawnMaterial != Material.WATER && spawnMaterial != Material.STATIONARY_WATER || belowMaterial != Material.WATER && belowMaterial != Material.STATIONARY_WATER : spawnMaterial != Material.AIR && spawnMaterial != Material.SNOW || belowMaterial == Material.AIR || belowMaterial == Material.WATER || belowMaterial == Material.STATIONARY_WATER || belowMaterial == Material.LAVA || belowMaterial == Material.STATIONARY_LAVA || spawnMaterial == Material.WATER || spawnMaterial == Material.STATIONARY_WATER || spawnMaterial == Material.LAVA || spawnMaterial == Material.STATIONARY_LAVA || aboveMaterial != Material.AIR) continue;
			switch (SpawnControl.$SWITCH_TABLE$org$bukkit$entity$EntityType()[EntityType.ordinal()]) {
			case 17: {
				if (this.world.getBlockAt(randomX, randomY + 2, randomZ).getType() == Material.AIR) break;
				** break;
			}
			case 5: {
				int y = 0;
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
			boolean spawnOnlyOn = difficulty.getSpawnOnlyOn(customCreatureType);
			spawnNotOn = difficulty.getSpawnNotOn(customCreatureType);
			if (spawnOnlyOn.size() > 0 && !spawnOnlyOn.contains((Object)belowMaterial) || spawnNotOn.contains((Object)belowMaterial)) continue;
			boolean c0ntinue = false;		// lmao - not my code
			for (Player p : players) {
				pl = p.getLocation();
				dx = pl.getX() - (double)randomX;
				distance = Math.sqrt(dx * dx + (dy = pl.getY() - (double)randomY) * dy + (dz = pl.getZ() - (double)randomZ) * dz);
				if (distance >= minDistance && distance <= maxDistance) continue;
				c0ntinue = true;
				break;
			}
			if (c0ntinue) continue;
			if (EntityType != EntityType.SPIDER) {
				l.setX(l.getX() + 0.5);
				l.setZ(l.getZ() + 0.5);
			}
			if ((creature = this.world.spawnCreature(l, EntityType)) == null) continue;
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
			if (EntityType == EntityType.CREEPER && difficulty.getElectrifiedCreeperChance() > SpawnControl.rand.nextDouble() * 100.0) {
				((Creeper)creature).setPowered(true);
				** break;
			}
			if (EntityType == EntityType.SPIDER && difficulty.getSpiderJockeyChance() > SpawnControl.rand.nextDouble() * 100.0) {
				skeleton = this.world.spawnCreature(l, EntityType.SKELETON);
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
			if (EntityType != EntityType.SHEEP) continue;
			colors = difficulty.getSheepColors();
			r = SpawnControl.rand.nextInt(colors[16]) + 1;
			color = DyeColor.WHITE;
			int i = 0;
			int t = 0;
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

