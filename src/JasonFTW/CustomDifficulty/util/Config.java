package jasonftw.CustomDifficulty.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import jasonftw.CustomDifficulty.CustomDifficulty;

public class Config {
	public static FileConfiguration config;

	private static void checkDefaultNode(String root, Object val) {
		String check = config.getString(root);
		if (check == null || check.equals("") || check.equals("null")) {
			Config.write(config, root, val);
		}
	}

	public static void initialize(CustomDifficulty plugin) {
		File configFile = new File(plugin.getDataFolder() + File.separator + "config.yml");
		if (!configFile.exists()) {
			CustomDifficulty.log("config.yml does not exist - creating it...");
			configFile.getParentFile().mkdirs();
			Config.writeFile(plugin, configFile);
		}
		try {
			config.load(configFile);
		} catch (IOException | InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String nodePrefix = "global.";
		Config.checkDefaultNode(String.valueOf(nodePrefix) + "checkForUpdates", true);
		Config.checkDefaultNode(String.valueOf(nodePrefix) + "savePlayerKills", 3);
		Config.checkDefaultNode(String.valueOf(nodePrefix) + "useOldGetDifficulty", false);
		Config.checkDefaultNode(String.valueOf(nodePrefix) + "initMessages", true);
		Config.checkDefaultNode(String.valueOf(nodePrefix) + "useWorldGuardRegions", true);
		Config.checkDefaultNode(String.valueOf(nodePrefix) + "minSpawnDistance", 24.0);
		Config.checkDefaultNode(String.valueOf(nodePrefix) + "maxSpawnDistance", 222.0);
		Config.checkDefaultNode(String.valueOf(nodePrefix) + "mobSpawnerRadius", 5.0);
		Config.checkDefaultNode(String.valueOf(nodePrefix) + "mobLimit", 500);
		List<World> worlds = plugin.getServer().getWorlds();
		for (World world : worlds) {
			Config.writeDefaultNodes(world);
		}
	}

	public static boolean nodeExists(Configuration config, String root) {
		String node = config.getString(root);
		if (node != null && node.compareToIgnoreCase("null") != 0) {
			return true;
		}
		return false;
	}

	public static boolean nodeIsBoolean(Configuration config, String root) {
		String node = config.getString(root);
		if (node != null && (node.equals("true") || node.equals("false"))) {
			return true;
		}
		return false;
	}

	public static void write(FileConfiguration config, String root, Object x) {
		config.load();
		config.setProperty(root, x);
		config.save();
	}

	public static void writeDefaultNodes(World world) {
		String nodePrefix = "worlds." + world.getName().replace(" ", "") + ".";
		Config.checkDefaultNode(String.valueOf(nodePrefix) + "mobAggressiveLimit", 75);
		Config.checkDefaultNode(String.valueOf(nodePrefix) + "mobPassiveLimit", 75);
		Config.checkDefaultNode(String.valueOf(nodePrefix) + "mobFriendlyLimit", 75);
		Config.checkDefaultNode(String.valueOf(nodePrefix) + "mobChunkLimit", 1);
		Config.checkDefaultNode(String.valueOf(nodePrefix) + "difficulty", "none");
		Config.checkDefaultNode(String.valueOf(nodePrefix) + "burnsInSunlightInterval", 20);
		Config.checkDefaultNode(String.valueOf(nodePrefix) + "spawnInterval", 5);
		Config.checkDefaultNode(String.valueOf(nodePrefix) + "aggressivenessInterval", 10);
		nodePrefix = String.valueOf(nodePrefix) + "useRegions.";
		Config.checkDefaultNode(String.valueOf(nodePrefix) + "aggressiveness", false);
		Config.checkDefaultNode(String.valueOf(nodePrefix) + "burnsInSunlight", false);
		Config.checkDefaultNode(String.valueOf(nodePrefix) + "mobHP", false);
		Config.checkDefaultNode(String.valueOf(nodePrefix) + "mobDamage", false);
		Config.checkDefaultNode(String.valueOf(nodePrefix) + "playerVsPlayer", false);
		Config.checkDefaultNode(String.valueOf(nodePrefix) + "playerVsMonster", false);
		Config.checkDefaultNode(String.valueOf(nodePrefix) + "reward", false);
		Config.checkDefaultNode(String.valueOf(nodePrefix) + "lootMultiplier", false);
		Config.checkDefaultNode(String.valueOf(nodePrefix) + "spawnAlgorithm", false);
	}

	private static void writeFile(JavaPlugin plugin, File f) {
		InputStream in = null;
		FileOutputStream fos = null;
		try {
			in = Manager.class.getResourceAsStream("/" + f.getName());
			if (in == null) {
				CustomDifficulty.log(Level.WARNING, "Couldn't find default file: " + f.getName() + " inside .jar!");
				return;
			}
			try {
				File output = new File(plugin.getDataFolder(), f.getName());
				fos = new FileOutputStream(output.getAbsoluteFile());
				int len = 0;
				byte[] buffer = new byte[1024];
				while ((len = in.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.flush();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		finally {
			if (fos != null) {
				try {
					fos.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (in != null) {
				try {
					in.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

