/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.bukkit.Server
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.PluginCommand
 *  org.bukkit.event.Event
 *  org.bukkit.event.Event$Priority
 *  org.bukkit.event.Event$Type
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginDescriptionFile
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.util.config.Configuration
 */

package JasonFTW.CustomDifficulty;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import JasonFTW.CustomDifficulty.SchedulerTasks.AggressivenessControl;
import JasonFTW.CustomDifficulty.SchedulerTasks.BurnInSunlightControl;
import JasonFTW.CustomDifficulty.SchedulerTasks.MobCounter;
import JasonFTW.CustomDifficulty.SchedulerTasks.MobListCleanup;
import JasonFTW.CustomDifficulty.SchedulerTasks.SpawnControl;
import JasonFTW.CustomDifficulty.commands.Check;
import JasonFTW.CustomDifficulty.commands.DifficultyCommands;
import JasonFTW.CustomDifficulty.commands.Help;
import JasonFTW.CustomDifficulty.hooks.Permissions;
import JasonFTW.CustomDifficulty.hooks.Register;
import JasonFTW.CustomDifficulty.hooks.WorldGuard;
import JasonFTW.CustomDifficulty.util.Manager;


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
        pm.registerEvent(new entityListener, this);
        pm.registerEvent(Event.Type.CREATURE_SPAWN, (Listener)this.entityListener, Event.Priority.High, (Plugin)this);
        pm.registerEvent(Event.Type.ENTITY_COMBUST, (Listener)this.entityListener, Event.Priority.Lowest, (Plugin)this);
        pm.registerEvent(Event.Type.CREEPER_POWER, (Listener)this.entityListener, Event.Priority.Lowest, (Plugin)this);
        pm.registerEvent(Event.Type.ENTITY_DAMAGE, (Listener)this.entityListener, Event.Priority.Lowest, (Plugin)this);
        pm.registerEvent(Event.Type.ENTITY_DEATH, (Listener)this.entityListener, Event.Priority.Lowest, (Plugin)this);
        pm.registerEvent(Event.Type.ENTITY_TAME, (Listener)this.entityListener, Event.Priority.Lowest, (Plugin)this);
        pm.registerEvent(Event.Type.ENTITY_TARGET, (Listener)this.entityListener, Event.Priority.Lowest, (Plugin)this);
        pm.registerEvent(Event.Type.PLAYER_JOIN, (Listener)this.playerListener, Event.Priority.Monitor, (Plugin)this);
        pm.registerEvent(Event.Type.PLAYER_INTERACT_ENTITY, (Listener)this.playerListener, Event.Priority.Lowest, (Plugin)this);
        pm.registerEvent(Event.Type.WORLD_UNLOAD, (Listener)this.worldListener, Event.Priority.Monitor, (Plugin)this);
    }
}

