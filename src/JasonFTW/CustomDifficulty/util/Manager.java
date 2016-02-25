/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.Location
 *  org.bukkit.Server
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginDescriptionFile
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.util.config.Configuration
 */
package JasonFTW.CustomDifficulty.util;

import JasonFTW.CustomDifficulty.CustomDifficulty;
import JasonFTW.CustomDifficulty.SchedulerTasks.AggressivenessControl;
import JasonFTW.CustomDifficulty.SchedulerTasks.BurnInSunlightControl;
import JasonFTW.CustomDifficulty.SchedulerTasks.MobCounter;
import JasonFTW.CustomDifficulty.SchedulerTasks.MobListCleanup;
import JasonFTW.CustomDifficulty.SchedulerTasks.SpawnControl;
import JasonFTW.CustomDifficulty.hooks.WorldGuard;
import JasonFTW.CustomDifficulty.util.CdCreatureType;
import JasonFTW.CustomDifficulty.util.Config;
import JasonFTW.CustomDifficulty.util.CreatureInfo;
import JasonFTW.CustomDifficulty.util.Difficulty;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.config.Configuration;

public class Manager {
    private static CustomDifficulty plugin = null;
    private static Server server = null;
    private static final HashMap<World, HashMap<LivingEntity, Difficulty>> mobDifficulties = new HashMap();
    private static final HashMap<World, Difficulty> worldDifficulties = new HashMap();
    private static final ArrayList<Difficulty> difficulties = new ArrayList();
    private static final HashMap<Player, CdCreatureType[]> lastKills = new HashMap();
    private static boolean useOldGetDifficulty;

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static boolean checkForUpdates() {
        try {
            URI baseURI = new URI("http://forums.bukkit.org/threads/20135/");
            HttpURLConnection con = (HttpURLConnection)baseURI.toURL().openConnection();
            con.setConnectTimeout(5000);
            con.setInstanceFollowRedirects(false);
            String header = con.getHeaderField("Location");
            if (header == null) {
                return false;
            }
            String url = new URI(con.getHeaderField("Location")).toString();
            Pattern regex = Pattern.compile("v([0-9]+-)*[0-9]+");
            Matcher matcher = regex.matcher(url);
            if (!matcher.find()) {
                return false;
            }
            String[] forumVersion = matcher.group().substring(1).split("-");
            String[] thisVersion = plugin.getDescription().getVersion().split("\\.");
            thisVersion[2] = thisVersion[2].substring(0, 1);
            int i = 0;
            do {
                if (i >= Math.min(forumVersion.length, thisVersion.length)) {
                    return false;
                }
                if (Integer.parseInt(forumVersion[i]) > Integer.parseInt(thisVersion[i])) {
                    return true;
                }
                ++i;
            } while (true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void initialize(CustomDifficulty plugin) {
        Manager.plugin = plugin;
        server = plugin.getServer();
        useOldGetDifficulty = Config.config.getBoolean("global.useOldGetDifficulty", false);
        Manager.createDefaultDifficulties();
        Manager.reloadDifficulties();
        Manager.reloadWorldDifficulties();
        Manager.reloadEntityDifficulties();
    }

    private static void reloadDifficulties() {
        difficulties.clear();
        File difficultiesFolder = new File(plugin.getDataFolder() + File.separator + "Difficulties");
        File[] arrfile = difficultiesFolder.listFiles();
        int n = arrfile.length;
        int n2 = 0;
        while (n2 < n) {
            File f = arrfile[n2];
            if (f.isFile() && f.getName().endsWith(".yml")) {
                String name = f.getName().substring(0, f.getName().length() - 4);
                Configuration config = new Configuration(f);
                if (config != null && name != null && name.length() > 0) {
                    difficulties.add(new Difficulty(name, config));
                }
            }
            ++n2;
        }
        if (difficulties.size() > 0) {
            String list = "";
            Iterator<Difficulty> i = difficulties.iterator();
            while (i.hasNext()) {
                Difficulty difficulty = i.next();
                list = String.valueOf(list) + difficulty.getName();
                list = i.hasNext() ? String.valueOf(list) + ", " : String.valueOf(list) + ".";
            }
            CustomDifficulty.log("Found the following " + difficulties.size() + " difficulties:\n" + list);
        }
    }

    private static void reloadWorldDifficulties() {
        for (World world : server.getWorlds()) {
            Difficulty difficulty;
            String name = world.getName().replace(" ", "");
            String dName = Config.config.getString("worlds." + name + ".difficulty");
            if (dName == null || (difficulty = Manager.getDifficulty(dName)) == null) continue;
            worldDifficulties.put(world, difficulty);
        }
    }

    private static void reloadEntityDifficulties() {
        for (World world : server.getWorlds()) {
            HashMap<LivingEntity, Difficulty> current = new HashMap<LivingEntity, Difficulty>();
            mobDifficulties.put(world, current);
            for (LivingEntity entity : world.getLivingEntities()) {
                if (entity instanceof Player) continue;
                Difficulty difficulty = Manager.getDifficulty(entity.getLocation());
                current.put(entity, difficulty);
            }
        }
    }

    public static Difficulty getDifficulty(String name) {
        for (Difficulty difficulty : difficulties) {
            if (!difficulty.getName().equalsIgnoreCase(name)) continue;
            return difficulty;
        }
        return null;
    }

    public static Difficulty getDifficulty(LivingEntity entity) {
        HashMap<LivingEntity, Difficulty> mobDifficulties;
        Difficulty difficulty = useOldGetDifficulty ? Manager.getDifficulty(entity.getLocation()) : ((mobDifficulties = Manager.mobDifficulties.get((Object)entity.getWorld())) != null ? mobDifficulties.get((Object)entity) : null);
        if (difficulty == null) {
            difficulty = Manager.getDifficulty(entity.getWorld());
        }
        return difficulty;
    }

    public static Difficulty getDifficulty(Location location) {
        if (Manager.useRegions()) {
            return WorldGuard.getDifficulty(location);
        }
        return null;
    }

    public static Difficulty getDifficulty(World world) {
        return worldDifficulties.get((Object)world);
    }

    public static int countMobs(Chunk chunk) {
        int count = 0;
        Entity[] arrentity = chunk.getEntities();
        int n = arrentity.length;
        int n2 = 0;
        while (n2 < n) {
            Entity entity = arrentity[n2];
            if (entity instanceof LivingEntity && !(entity instanceof Player)) {
                ++count;
            }
            ++n2;
        }
        return count;
    }

    public static int getMobLimit() {
        return Config.config.getInt("global.mobLimit", 500);
    }

    public static int getMobChunkLimit(World world) {
        String name = world.getName().replace(" ", "");
        return Config.config.getInt("worlds." + name + ".mobChunkLimit", 1);
    }

    public static int getMobAggressiveLimit(World world) {
        String name = world.getName().replace(" ", "");
        return Config.config.getInt("worlds." + name + ".mobAggressiveLimit", 75);
    }

    public static int getMobFriendlyLimit(World world) {
        String name = world.getName().replace(" ", "");
        return Config.config.getInt("worlds." + name + ".mobFriendlyLimit", 75);
    }

    public static int getMobPassiveLimit(World world) {
        String name = world.getName().replace(" ", "");
        return Config.config.getInt("worlds." + name + ".mobPassiveLimit", 75);
    }

    public static CdCreatureType[] getLastKills(Player player) {
        CdCreatureType[] lastKills = Manager.lastKills.get((Object)player);
        if (lastKills == null) {
            lastKills = new CdCreatureType[Config.config.getInt("global.savePlayerKills", 3)];
            Manager.lastKills.put(player, lastKills);
        }
        return lastKills;
    }

    public static boolean isDay(World world) {
        long time = world.getTime();
        if (time > 0 && time < 13000) {
            return true;
        }
        return false;
    }

    public static boolean isNight(World world) {
        long time = world.getTime();
        if (time >= 0 && time <= 13000) {
            return false;
        }
        return true;
    }

    public static void addMob(LivingEntity entity, Difficulty difficulty, String reason) {
        HashMap<LivingEntity, Difficulty> mobDifficulties = Manager.mobDifficulties.get((Object)entity.getWorld());
        mobDifficulties.put(entity, difficulty);
    }

    public static void removeMob(LivingEntity entity, String reason) {
        HashMap<LivingEntity, Difficulty> mobDifficulties = Manager.mobDifficulties.get((Object)entity.getWorld());
        mobDifficulties.remove((Object)entity);
    }

    public static int cleanMobs(World world) {
        HashMap mobDifficulties = Manager.mobDifficulties.get((Object)world);
        if (mobDifficulties == null) {
            mobDifficulties = new HashMap();
            Manager.mobDifficulties.put(world, mobDifficulties);
        }
        Set<LivingEntity> keySet = mobDifficulties.keySet();
        Iterator<LivingEntity> iterator = keySet.iterator();
        ArrayList<LivingEntity> remove = new ArrayList<LivingEntity>();
        while (iterator.hasNext()) {
            LivingEntity entity = iterator.next();
            if (!entity.isDead()) continue;
            remove.add(entity);
        }
        int count = 0;
        int i = 0;
        while (i < remove.size()) {
            Manager.removeMob((LivingEntity)remove.get(i), "cleaning");
            ++count;
            ++i;
        }
        return count;
    }

    public static boolean useRegion(World world, String regionNode) {
        String root = "worlds." + world.getName().replace(" ", "") + ".useRegions." + regionNode;
        return Config.config.getBoolean(root, false);
    }

    public static boolean useRegions() {
        if (!Config.config.getBoolean("global.useWorldGuardRegions", true) || plugin.getServer().getPluginManager().getPlugin("WorldGuard") == null) {
            return false;
        }
        return true;
    }

    public static void setDifficulty(World world, Difficulty difficulty) {
        String sDifficulty;
        String sWorld = world.getName().replace(" ", "");
        if (difficulty != null) {
            worldDifficulties.put(world, difficulty);
            sDifficulty = difficulty.getName();
        } else {
            worldDifficulties.remove((Object)world);
            sDifficulty = "none";
        }
        String root = "worlds." + sWorld + ".difficulty";
        Config.config.setProperty(root, (Object)sDifficulty);
    }

    public static void reload() {
        long time = System.nanoTime();
        CustomDifficulty.log("Reloading...");
        for (World world2 : server.getWorlds()) {
            AggressivenessControl.deactivate(world2);
            SpawnControl.deactivate(world2);
            BurnInSunlightControl.deactivate(world2);
        }
        MobCounter.stop();
        MobListCleanup.deactivate();
        Config.config.load();
        useOldGetDifficulty = Config.config.getBoolean("global.useOldGetDifficulty", false);
        Manager.reloadDifficulties();
        Manager.reloadWorldDifficulties();
        Manager.reloadEntityDifficulties();
        for (World world2 : server.getWorlds()) {
            for (LivingEntity entity : world2.getLivingEntities()) {
                Difficulty difficulty;
                int health = CreatureInfo.getHealth(entity, difficulty = Manager.getDifficulty(entity));
                if (health > 0) {
                    entity.setHealth(health);
                    continue;
                }
                if (health != 0) continue;
                entity.remove();
            }
            SpawnControl.activate(plugin, world2);
            AggressivenessControl.activate(plugin, world2);
            BurnInSunlightControl.activate(plugin, world2);
        }
        MobCounter.start(plugin);
        MobListCleanup.activate(plugin);
        CustomDifficulty.log("Reloading done. (" + (double)Math.round((double)(System.nanoTime() - time) / 10000.0) / 100.0 + "ms)");
    }

    public static boolean getUseOldDGetDifficuly() {
        return useOldGetDifficulty;
    }

    private static void writeFile(File f) {
        InputStream in = null;
        FileOutputStream fos = null;
        try {
            in = Manager.class.getResourceAsStream("/" + f.getName());
            if (in == null) {
                CustomDifficulty.log(Level.WARNING, "Couldn't find default file: " + f.getName() + " inside .jar!");
                return;
            }
            try {
                File output = new File(plugin.getDataFolder() + File.separator + "Difficulties" + File.separator + f.getName());
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

    private static void createDefaultDifficulties() {
        File conf;
        if (!new File(plugin.getDataFolder() + File.separator + "Difficulties" + File.separator).exists()) {
            new File(plugin.getDataFolder() + File.separator + "Difficulties" + File.separator).mkdirs();
        }
        if (!(conf = new File(plugin.getDataFolder() + File.separator + "Difficulties", "default.yml")).exists()) {
            Manager.writeFile(conf);
        }
    }
}

