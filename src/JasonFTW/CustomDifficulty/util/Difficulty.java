/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.CreatureType
 *  org.bukkit.util.config.Configuration
 */
package JasonFTW.CustomDifficulty.util;

import JasonFTW.CustomDifficulty.util.Aggressiveness;
import JasonFTW.CustomDifficulty.util.CdCreatureType;
import JasonFTW.CustomDifficulty.util.Config;
import JasonFTW.CustomDifficulty.util.CreatureInfo;
import JasonFTW.CustomDifficulty.util.DropItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.CreatureType;
import org.bukkit.util.config.Configuration;

public class Difficulty {
    private final String name;
    private final Configuration config;
    private final HashMap<CdCreatureType, Aggressiveness> aggressivenessDay = new HashMap();
    private final HashMap<CdCreatureType, Aggressiveness> aggressivenessNight = new HashMap();
    private final HashMap<CdCreatureType, Byte> burnsInSunlight = new HashMap();
    private final HashMap<CdCreatureType, Double> damage = new HashMap();
    private final HashMap<CdCreatureType, ArrayList<DropItem>> drops = new HashMap();
    private final HashMap<CdCreatureType, Short> health = new HashMap();
    private final HashMap<CdCreatureType, Byte> heightMax = new HashMap();
    private final HashMap<CdCreatureType, Byte> heightMin = new HashMap();
    private final HashMap<CdCreatureType, Byte> lightLevelMax = new HashMap();
    private final HashMap<CdCreatureType, Byte> lightLevelMin = new HashMap();
    private final HashMap<CdCreatureType, Double> lootMultiplier = new HashMap();
    private final HashMap<CdCreatureType, Double> reward = new HashMap();
    private final HashMap<CdCreatureType, Double> spawnChance = new HashMap();
    private final HashMap<CdCreatureType, ArrayList<Material>> spawnNotOn = new HashMap();
    private final HashMap<CdCreatureType, ArrayList<Material>> spawnOnlyOn = new HashMap();
    private final ArrayList<CreatureType> spawningCreatureTypes;
    private final double electrifiedCreeperChance;
    private final double spiderJockeyChance;
    private final int[] sheepColors;
    private final double rewardDepreciation;
    private final boolean dropOnlyWhenKilledByPlayer;
    private final short vsMonsterDamageAdd;
    private final double vsMonsterDamageMultiply;
    private final short vsPlayerDamageAdd;
    private final double vsPlayerDamageMultiply;

    public Difficulty(String name, Configuration config) {
        this.name = name;
        this.config = config;
        this.config.load();
        this.spawningCreatureTypes = this.createCreatureTypeArray();
        this.electrifiedCreeperChance = config.getDouble("creeper.electrifiedChance", 2.0);
        this.spiderJockeyChance = config.getDouble("spider.spiderJockeyChance", 1.0);
        this.sheepColors = this.createSheepColorArray();
        this.rewardDepreciation = config.getDouble("global.rewardDepreciation", 0.0);
        this.vsPlayerDamageAdd = (short)config.getInt("player.vsPlayerDamageAdd", 0);
        this.vsPlayerDamageMultiply = config.getDouble("player.vsPlayerDamageMultiply", 1.0);
        this.vsMonsterDamageAdd = (short)config.getInt("player.vsMonsterDamageAdd", 0);
        this.vsMonsterDamageMultiply = config.getDouble("player.vsMonsterDamageMultiply", 1.0);
        this.dropOnlyWhenKilledByPlayer = config.getBoolean("global.dropOnlyWhenKilledByPlayer", false);
        CdCreatureType[] arrcdCreatureType = CdCreatureType.getAllTypes();
        int n = arrcdCreatureType.length;
        int n2 = 0;
        while (n2 < n) {
            CdCreatureType type = arrcdCreatureType[n2];
            String nodeLong = type.toDifficultyNode(false);
            String nodeSmall = type.toDifficultyNode(true);
            int hp = config.getInt(String.valueOf(nodeLong) + ".hp", -1);
            if (hp < 0 || hp > 200) {
                hp = -1;
            }
            this.health.put(type, (short)hp);
            double damage = config.getDouble(String.valueOf(nodeLong) + ".damage", -1.0);
            if (damage > 200.0 || damage < 0.0) {
                damage = 0.0;
            }
            this.damage.put(type, damage);
            this.aggressivenessDay.put(type, Aggressiveness.valueOfString(config.getString(String.valueOf(nodeLong) + ".aggressivenessDay")));
            this.aggressivenessNight.put(type, Aggressiveness.valueOfString(config.getString(String.valueOf(nodeLong) + ".aggressivenessNight")));
            int lightMin = config.getInt(String.valueOf(nodeSmall) + ".lightLevelMin", -1);
            if (lightMin < 0 || lightMin > 15) {
                lightMin = -1;
            }
            this.lightLevelMin.put(type, Byte.valueOf((byte)lightMin));
            int lightMax = config.getInt(String.valueOf(nodeSmall) + ".lightLevelMax", -1);
            if (lightMax < 0 || lightMax > 15) {
                lightMax = -1;
            }
            this.lightLevelMax.put(type, Byte.valueOf((byte)lightMax));
            int heightMin = config.getInt(String.valueOf(nodeSmall) + ".heightMin", -1);
            if (heightMin < 0 || heightMin > 15) {
                heightMin = -1;
            }
            this.heightMin.put(type, Byte.valueOf((byte)heightMin));
            int heightMax = config.getInt(String.valueOf(nodeSmall) + ".heightMax", -1);
            if (heightMax < 0 || lightMax > 15) {
                heightMax = -1;
            }
            this.heightMax.put(type, Byte.valueOf((byte)heightMax));
            if (Config.nodeIsBoolean(config, String.valueOf(nodeLong) + ".burnsInSunlight")) {
                this.burnsInSunlight.put(type, Byte.valueOf(config.getBoolean(String.valueOf(nodeLong) + ".burnsInSunlight", true) ? 1 : 0));
            } else {
                this.burnsInSunlight.put(type, Byte.valueOf(-1));
            }
            double spawnChance = config.getDouble(String.valueOf(nodeSmall) + ".spawnChance", -1.0);
            if (spawnChance < 0.0 || spawnChance > 100.0) {
                spawnChance = -1.0;
            }
            this.spawnChance.put(type, spawnChance);
            this.spawnOnlyOn.put(type, this.readMaterialList(String.valueOf(nodeSmall) + ".spawnOnlyOn"));
            this.spawnNotOn.put(type, this.readMaterialList(String.valueOf(nodeSmall) + ".spawnNotOn"));
            this.reward.put(type, config.getDouble(String.valueOf(nodeLong) + ".reward", 0.0));
            double lootMultiplier = config.getDouble(String.valueOf(nodeLong) + ".lootMultiplier", 1.0);
            if (lootMultiplier < 0.0) {
                lootMultiplier = 1.0;
            }
            this.lootMultiplier.put(type, lootMultiplier);
            this.drops.put(type, this.readDropList(String.valueOf(nodeLong) + ".drops"));
            ++n2;
        }
    }

    public byte burnsInSunlight(CdCreatureType creatureType) {
        if (creatureType == null || !this.burnsInSunlight.containsKey((Object)creatureType)) {
            return -1;
        }
        return this.burnsInSunlight.get((Object)creatureType).byteValue();
    }

    private int[] createSheepColorArray() {
        int[] colors = new int[17];
        int temp = this.config.getInt("sheep.color0", 85);
        int total = 0;
        colors[0] = temp;
        total += temp;
        int i = 1;
        while (i < 16) {
            colors[i] = temp = this.config.getInt("sheep.color" + i, 0);
            total += temp;
            ++i;
        }
        colors[16] = total;
        return colors;
    }

    private ArrayList<CreatureType> createCreatureTypeArray() {
        ArrayList<CreatureType> temp = new ArrayList<CreatureType>();
        List input = this.config.getList("global.possibleSpawnCreatures");
        if (input == null) {
            return temp;
        }
        for (Object o : input) {
            CreatureType ct = CreatureInfo.getCreatureTypeFromName((String)o);
            if (ct == null || temp.contains((Object)ct)) continue;
            temp.add(ct);
        }
        return temp;
    }

    public int[] getSheepColors() {
        return this.sheepColors;
    }

    public double getElectrifiedCreeperChance() {
        return this.electrifiedCreeperChance;
    }

    public double getSpiderJockeyChance() {
        return this.spiderJockeyChance;
    }

    public boolean getDropOnlyWhenKilledByPlayer() {
        return this.dropOnlyWhenKilledByPlayer;
    }

    public Aggressiveness getAggressivenessDay(CdCreatureType creatureType) {
        return this.aggressivenessDay.get((Object)creatureType);
    }

    public Aggressiveness getAggressivenessNight(CdCreatureType creatureType) {
        return this.aggressivenessNight.get((Object)creatureType);
    }

    public ArrayList<CreatureType> getCreatureTypes() {
        return this.spawningCreatureTypes;
    }

    public double getDamage(CdCreatureType creatureType) {
        return this.damage.get((Object)creatureType);
    }

    public ArrayList<DropItem> getDrops(CdCreatureType creatureType) {
        return this.drops.get((Object)creatureType);
    }

    public int getHealth(CdCreatureType creatureType) {
        return this.health.get((Object)creatureType).shortValue();
    }

    public double getLootMultiplier(CdCreatureType creatureType) {
        if (this.lootMultiplier != null && creatureType != null) {
            Double multiplier = this.lootMultiplier.get((Object)creatureType);
            if (multiplier != null) {
                return multiplier;
            }
            return 1.0;
        }
        return 1.0;
    }

    public byte getMaxSpawnHeight(CdCreatureType creatureType) {
        return this.heightMax.get((Object)creatureType).byteValue();
    }

    public byte getMaxSpawnLightLevel(CdCreatureType creatureType) {
        return this.lightLevelMax.get((Object)creatureType).byteValue();
    }

    public byte getMinSpawnHeight(CdCreatureType creatureType) {
        return this.heightMin.get((Object)creatureType).byteValue();
    }

    public byte getMinSpawnLightLevel(CdCreatureType creatureType) {
        return this.lightLevelMin.get((Object)creatureType).byteValue();
    }

    public String getName() {
        return this.name;
    }

    public short getPlayerVsMonsterDamageAdd() {
        return this.vsMonsterDamageAdd;
    }

    public double getPlayerVsMonsterDamageMultiply() {
        return this.vsMonsterDamageMultiply;
    }

    public short getPlayerVsPlayerDamageAdd() {
        return this.vsPlayerDamageAdd;
    }

    public double getPlayerVsPlayerDamageMultiply() {
        return this.vsPlayerDamageMultiply;
    }

    public double getReward(CdCreatureType creatureType) {
        return this.reward.get((Object)creatureType);
    }

    public double getRewardDepreciation() {
        return this.rewardDepreciation;
    }

    public double getSpawnChance(CdCreatureType creatureType) {
        return this.spawnChance.get((Object)creatureType);
    }

    public ArrayList<Material> getSpawnNotOn(CdCreatureType creatureType) {
        return this.spawnNotOn.get((Object)creatureType);
    }

    public ArrayList<Material> getSpawnOnlyOn(CdCreatureType creatureType) {
        return this.spawnOnlyOn.get((Object)creatureType);
    }

    private ArrayList<DropItem> readDropList(String root) {
        List input = this.config.getList(root);
        ArrayList<DropItem> output = new ArrayList<DropItem>();
        if (input == null) {
            return output;
        }
        for (Object o : input) {
            int chance;
            short amountMax;
            short meta;
            short amountMin;
            Material material;
            block22 : {
                String[] split;
                block21 : {
                    block20 : {
                        block19 : {
                            String s = null;
                            try {
                                s = (String)o;
                            }
                            catch (Exception e) {
                                continue;
                            }
                            split = s.replace(" ", "").split(":");
                            if (split.length != 5) continue;
                            material = null;
                            meta = -1;
                            amountMin = 1;
                            amountMax = 1;
                            chance = 100;
                            try {
                                material = Material.getMaterial((int)Integer.parseInt(split[0]));
                                if (material == null || material == Material.AIR) {
                                    continue;
                                }
                            }
                            catch (Exception var13_15) {
                                // empty catch block
                            }
                            if (material == null && ((material = Material.valueOf((String)split[0])) == null || material == Material.AIR)) continue;
                            try {
                                meta = Short.valueOf(split[1]);
                                if (meta < 0) continue;
                                if (meta > 15) {
                                }
                                break block19;
                            }
                            catch (Exception e) {}
                            continue;
                        }
                        try {
                            amountMin = Short.valueOf(split[2]);
                            if (amountMin < 0) continue;
                            if (amountMin > 64) {
                            }
                            break block20;
                        }
                        catch (Exception e) {}
                        continue;
                    }
                    try {
                        amountMax = Short.valueOf(split[3]);
                        if (amountMax < 1) continue;
                        if (amountMax > 64) {
                        }
                        break block21;
                    }
                    catch (Exception e) {}
                    continue;
                }
                try {
                    chance = Integer.valueOf(split[4]);
                    if (chance < 1) continue;
                    if (chance > 100) {
                    }
                    break block22;
                }
                catch (Exception e) {}
                continue;
            }
            output.add(new DropItem(material, meta, amountMin, amountMax, chance));
        }
        return output;
    }

    private ArrayList<Material> readMaterialList(String root) {
        List input = this.config.getList(root);
        ArrayList<Material> output = new ArrayList<Material>();
        if (input == null) {
            return output;
        }
        for (Object o : input) {
            Material m = null;
            try {
                m = Material.getMaterial((int)((Integer)o));
            }
            catch (Exception var7_8) {
                // empty catch block
            }
            if (m == null) {
                try {
                    m = Material.getMaterial((String)((String)o));
                }
                catch (Exception var7_9) {
                    // empty catch block
                }
            }
            if (m == null) continue;
            output.add(m);
        }
        return output;
    }
}

