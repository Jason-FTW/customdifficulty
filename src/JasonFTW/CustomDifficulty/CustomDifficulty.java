package jasonftw.CustomDifficulty;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.event.Event;

import jasonftw.CustomDifficulty.SchedulerTasks.AggressivenessControl;
import jasonftw.CustomDifficulty.SchedulerTasks.BurnInSunlightControl;
import jasonftw.CustomDifficulty.SchedulerTasks.MobCounter;
import jasonftw.CustomDifficulty.SchedulerTasks.MobListCleanup;
import jasonftw.CustomDifficulty.SchedulerTasks.SpawnControl;
import jasonftw.CustomDifficulty.commands.Check;
import jasonftw.CustomDifficulty.commands.DifficultyCommands;
import jasonftw.CustomDifficulty.commands.Help;
import jasonftw.CustomDifficulty.hooks.Permissions;
import jasonftw.CustomDifficulty.hooks.Register;
import jasonftw.CustomDifficulty.hooks.WorldGuard;
import jasonftw.CustomDifficulty.util.Manager;

public class CustomDifficulty extends JavaPlugin {
	private final CustomDifficultyEntityListener entityListener = new CustomDifficultyEntityListener();
	private final CustomDifficultyPlayerListener playerListener = new CustomDifficultyPlayerListener();
	private final CustomDifficultyWorldListener worldListener = new CustomDifficultyWorldListener();
	private static Logger log;
	private static String prefix;

	public static FileConfiguration Config = null;

	public static void initializeMessage(Level level, String msg) {
		if (Config.getBoolean("global.initMessages", true)) {
			CustomDifficulty.log(level, msg);
		}
	}

	public static void log(Level level, String msg) {
		log.log(level, String.valueOf(prefix) + msg);
	}

	public static void log(String msg) {
		CustomDifficulty.log(Level.INFO, msg);
	}

	private void initialize() {
		log = this.getServer().getLogger();
		prefix = "[" + this.getDescription().getName() + "] ";
		//Config.initialize(this);
		this.loadPlugins("WorldManager", "Multiverse-Core");
		if (Config.getBoolean("global.useWorldGuardRegions", true) && this.getServer().getPluginManager().getPlugin("WorldGuard") != null) {
			WorldGuard.initialize(this);
		}
		Manager.initialize(this);
		Permissions.initialize(this);
		Register.initialize(this);
	}

	private /* varargs */ void loadPlugins(String ... names) {
		String[] arrstring = names;
		int n = arrstring.length;
		int n2 = 0;
		while (n2 < n) {
			String name = arrstring[n2];
			Plugin plugin = this.getServer().getPluginManager().getPlugin(name);
			if (plugin != null && !plugin.isEnabled()) {
				CustomDifficulty.initializeMessage(Level.INFO, "Loading Plugin: " + name);
				this.getServer().getPluginManager().enablePlugin(plugin);
			}
			++n2;
		}
	}

	public void onDisable() {
		BurnInSunlightControl.deactivateAllWorlds();
		SpawnControl.deactivateAllWorlds();
		MobCounter.stop();
		CustomDifficulty.log("has been disabled.");
	}

	public void onEnable() {
		long time = System.nanoTime();
		this.initialize();
		this.registerEvents();
		this.registerCommands();
		SpawnControl.activateAllWorlds(this);
		AggressivenessControl.activateAllWorlds(this);
		BurnInSunlightControl.activateAllWorlds(this);
		MobListCleanup.activate(this);
		MobCounter.start(this);
		CustomDifficulty.log("version " + this.getDescription().getVersion() + " has been enabled. (" + (double)Math.round((double)(System.nanoTime() - time) / 10000.0) / 100.0 + "ms)");
	}

	private void registerCommands() {
		this.getCommand("difficulty").setExecutor((CommandExecutor)new DifficultyCommands());
		Check.initialize(this);
		Help.initialize(this);
	}

	private void registerEvents() {
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(entityListener, this);
		pm.registerEvents(this.entityListener, this);
		pm.registerEvents(this.playerListener, this);
		pm.registerEvents(this.playerListener, this);
		pm.registerEvents(this.worldListener, this);
	}
}

