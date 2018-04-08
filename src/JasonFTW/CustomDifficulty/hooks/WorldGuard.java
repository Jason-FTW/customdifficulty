package jasonftw.CustomDifficulty.hooks;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.Direction.Flag;

import jasonftw.CustomDifficulty.CustomDifficulty;
import jasonftw.CustomDifficulty.util.Difficulty;
import jasonftw.CustomDifficulty.util.Manager;

public abstract class WorldGuard {
	private static Object difficultyFlag = null;
	private static Plugin worldGuard = null;
	private static HashMap<String, Configuration> worldGuardRegionConfigs = null;

	public static Difficulty getDifficulty(Location l) {
		RegionManager rm = ((WorldGuardPlugin)worldGuard).getRegionManager(l.getWorld());
		if (rm == null) {
			return null;
		}
		ApplicableRegionSet ars = rm.getApplicableRegions(new Vector(l.getX(), l.getY(), l.getZ()));
		return Manager.getDifficulty((String)ars.getFlag((Flag)((StringFlag)difficultyFlag)));
	}

	public static void initialize(JavaPlugin plugin) {
		Plugin p = plugin.getServer().getPluginManager().getPlugin("WorldGuard");
		if (p == null) {
			CustomDifficulty.log("WorldGuard not found!");
			return;
		}
		try {
			worldGuard = (WorldGuardPlugin)p;
			if (!worldGuard.isEnabled()) {
				Bukkit.getServer().getPluginManager().enablePlugin(worldGuard);
			}
			worldGuardRegionConfigs = new HashMap();
			WorldGuard.initializeFlags();
			CustomDifficulty.initializeMessage(Level.INFO, "Hooked into " + plugin.getServer().getPluginManager().getPlugin("WorldGuard").getDescription().getFullName() + ".");
		}
		catch (Exception e) {
			e.printStackTrace();
			CustomDifficulty.initializeMessage(Level.WARNING, "Invalid or no WorldGuard found!");
		}
	}

	private static void initializeFlags() {
		if (worldGuard == null) {
			return;
		}
		if (difficultyFlag == null) {
			difficultyFlag = new StringFlag("difficulty");
		}
		int w = 0;
		while (w < Bukkit.getServer().getWorlds().size()) {
			World world = (World)Bukkit.getServer().getWorlds().get(w);
			RegionManager rm = ((WorldGuardPlugin)worldGuard).getRegionManager(world);
			if (rm == null) {
				CustomDifficulty.log(Level.WARNING, "WorldGuard's region manager wasn't found for world " + world.getName() + "!");
			} else {
				Map regions = rm.getRegions();
				Set regionsKeySet = regions.keySet();
				Iterator regionsIterator = regionsKeySet.iterator();
				while (regionsIterator.hasNext()) {
					ProtectedRegion region = (ProtectedRegion)regions.get(regionsIterator.next());
					String difficulty = WorldGuard.readDifficultyFlag(world, region);
					if (difficulty == null || difficulty.length() <= 0) continue;
					region.setFlag((Flag)((StringFlag)difficultyFlag), (Object)difficulty);
				}
			}
			++w;
		}
	}

	public static void printWorldGuardRegionInfo(CommandSender commandSender, World world, String region) {
		ProtectedRegion protectedRegion = ((WorldGuardPlugin)worldGuard).getRegionManager(world).getRegion(region);
		if (protectedRegion == null) {
			commandSender.sendMessage((Object)ChatColor.RED + "No matching WorldGuard-Region found for world " + world.getName() + " found: " + region);
			return;
		}
		String difficulty = (String)protectedRegion.getFlag((Flag)((StringFlag)difficultyFlag));
		if (difficulty == null) {
			difficulty = "none set.";
		} else {
			Difficulty d = Manager.getDifficulty(difficulty);
			if (d == null) {
				difficulty = String.valueOf(difficulty) + " - " + (Object)ChatColor.RED + "won't be used because it wasn't found!";
			}
		}
		commandSender.sendMessage((Object)ChatColor.AQUA + "WorldGuard-Region info: ");
		commandSender.sendMessage((Object)ChatColor.GREEN + "Name: " + (Object)ChatColor.YELLOW + protectedRegion.getId());
		commandSender.sendMessage((Object)ChatColor.GREEN + "World: " + (Object)ChatColor.YELLOW + world.getName());
		commandSender.sendMessage((Object)ChatColor.GREEN + "Difficulty: " + (Object)ChatColor.YELLOW + difficulty);
		commandSender.sendMessage((Object)ChatColor.GREEN + "LocationMinimum: " + (Object)ChatColor.YELLOW + "(" + protectedRegion.getMinimumPoint().getX() + " / " + protectedRegion.getMinimumPoint().getY() + " / " + protectedRegion.getMinimumPoint().getZ() + ")");
		commandSender.sendMessage((Object)ChatColor.GREEN + "LocationMaximum: " + (Object)ChatColor.YELLOW + "(" + protectedRegion.getMaximumPoint().getX() + " / " + protectedRegion.getMaximumPoint().getY() + " / " + protectedRegion.getMaximumPoint().getZ() + ")");
	}

	private static String readDifficultyFlag(World world, ProtectedRegion region) {
		String path = worldGuard.getDataFolder() + File.separator + "worlds" + File.separator + world.getName() + File.separator + "regions.yml";
		Configuration config = null;
		if (!worldGuardRegionConfigs.containsKey(path)) {
			config = new Configuration(new File(path));
			try {
				config.load();
				worldGuardRegionConfigs.put(path, config);
			}
			catch (IOException e) {
				e.printStackTrace();
				CustomDifficulty.log(Level.WARNING, "Failed to load worldGuard config: " + world.getName() + File.separator + "regions.yml");
				return null;
			}
		} else {
			config = worldGuardRegionConfigs.get(path);
		}
		String flags = config.getString("regions." + region.getId() + ".flags");
		if (flags == null || !flags.contains("difficulty=")) {
			return null;
		}
		flags = flags.replace("{", "").replace("}", "");
		if ((flags = flags.substring(flags.indexOf("difficulty=") + 11)).contains(",")) {
			flags = flags.substring(0, flags.indexOf(",")).trim();
		}
		if (flags.length() < 1) {
			return null;
		}
		return flags;
	}

	public static int setDifficulty(World world, String region, Difficulty difficulty) {
		if (difficulty == null) {
			return 1;
		}
		RegionManager rm = ((WorldGuardPlugin)worldGuard).getRegionManager(world);
		if (rm == null) {
			return 2;
		}
		Region pr = rm.getRegion(region);
		if (pr == null) {
			return 2;
		}
		pr.setFlag((Flag)((StringFlag)difficultyFlag), (Object)difficulty.getName());
		try {
			rm.save();
		}
		catch (IOException e) {
			e.printStackTrace();
			return 3;
		}
		return 0;
	}
}

