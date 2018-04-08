package jasonftw.CustomDifficulty.util;

import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;

public enum CdCreatureType {
	CAVESPIDER,
	CHICKEN,
	COW,
	CREEPER_POWERED,
	CREEPER_UNPOWERED,
	ENDERMAN,
	GHAST,
	GIANT,
	MONSTER,
	PIG,
	PIGZOMBIE,
	SHEEP,
	SILVERFISH,
	SKELETON,
	SLIME_AVERAGE,
	SLIME_BIG,
	SLIME_HUGE,
	SLIME_SMALL,
	SPIDER,
	SQUID,
	WOLF_TAMED,
	WOLF_WILD,
	ZOMBIE;

	private static /* synthetic */ int[] $SWITCH_TABLE$org$bukkit$entity$EntityType;
	private static /* synthetic */ int[] $SWITCH_TABLE$Pasukaru$CustomDifficulty$util$CdCreatureType;

	private CdCreatureType() {}

	public static CdCreatureType[] getAllTypes() {
		CdCreatureType[] types = new CdCreatureType[]{CAVESPIDER, CHICKEN, COW, CREEPER_UNPOWERED, CREEPER_POWERED, ENDERMAN, GHAST, GIANT, MONSTER, PIG, PIGZOMBIE, SHEEP, SILVERFISH, SKELETON, SLIME_SMALL, SLIME_AVERAGE, SLIME_BIG, SLIME_HUGE, SPIDER, SQUID, WOLF_TAMED, WOLF_WILD, ZOMBIE};
		return types;
	}

	public static CdCreatureType valueOf(Entity e) {
		if (e instanceof Chicken) {
			return CHICKEN;
		}
		if (e instanceof Cow) {
			return COW;
		}
		if (e instanceof Creeper) {
			if (((Creeper)e).isPowered()) {
				return CREEPER_POWERED;
			}
			return CREEPER_UNPOWERED;
		}
		if (e instanceof Enderman) {
			return ENDERMAN;
		}
		if (e instanceof Ghast) {
			return GHAST;
		}
		if (e instanceof Giant) {
			return GIANT;
		}
		if (e instanceof Pig) {
			return PIG;
		}
		if (e instanceof PigZombie) {
			return PIGZOMBIE;
		}
		if (e instanceof Sheep) {
			return SHEEP;
		}
		if (e instanceof Silverfish) {
			return SILVERFISH;
		}
		if (e instanceof Skeleton) {
			return SKELETON;
		}
		if (e instanceof Slime) {
			switch (((Slime)e).getSize()) {
			case 1: {
				return SLIME_SMALL;
			}
			case 2: {
				return SLIME_AVERAGE;
			}
			case 4: {
				return SLIME_BIG;
			}
			}
			return SLIME_HUGE;
		}
		if (e instanceof Spider) {
			if (e instanceof CaveSpider) {
				return CAVESPIDER;
			}
			return SPIDER;
		}
		if (e instanceof Squid) {
			return SQUID;
		}
		if (e instanceof Wolf) {
			if (((Wolf)e).isTamed()) {
				return WOLF_TAMED;
			}
			return WOLF_WILD;
		}
		if (e instanceof Zombie) {
			return ZOMBIE;
		}
		if (e instanceof Monster) {
			return MONSTER;
		}
		return null;
	}

	public EntityType toCreatureType() {
		switch (CdCreatureType.$SWITCH_TABLE$Pasukaru$CustomDifficulty$util$CdCreatureType()[this.ordinal()]) {
		case 1: {
			return EntityType.CAVE_SPIDER;
		}
		case 2: {
			return EntityType.CHICKEN;
		}
		case 3: {
			return EntityType.COW;
		}
		case 4: 
		case 5: {
			return EntityType.CREEPER;
		}
		case 6: {
			return EntityType.ENDERMAN;
		}
		case 7: {
			return EntityType.GHAST;
		}
		case 8: {
			return EntityType.GIANT;
		}
		case 9: {
			return EntityType.MONSTER;
		}
		case 10: {
			return EntityType.PIG;
		}
		case 11: {
			return EntityType.PIG_ZOMBIE;
		}
		case 12: {
			return EntityType.SHEEP;
		}
		case 13: {
			return EntityType.SILVERFISH;
		}
		case 14: {
			return EntityType.SKELETON;
		}
		case 15: 
		case 16: 
		case 17: 
		case 18: {
			return EntityType.SLIME;
		}
		case 19: {
			return EntityType.SPIDER;
		}
		case 20: {
			return EntityType.SQUID;
		}
		case 21: 
		case 22: {
			return EntityType.WOLF;
		}
		case 23: {
			return EntityType.ZOMBIE;
		}
		}
		return null;
	}

	public CdCreatureType toDefault() {
		switch (CdCreatureType.$SWITCH_TABLE$Pasukaru$CustomDifficulty$util$CdCreatureType()[this.ordinal()]) {
		case 1: 
		case 2: 
		case 3: 
		case 6: 
		case 7: 
		case 8: 
		case 9: 
		case 10: 
		case 11: 
		case 12: 
		case 13: 
		case 14: 
		case 19: 
		case 20: 
		case 23: {
			return this;
		}
		case 4: 
		case 5: {
			return CREEPER_UNPOWERED;
		}
		case 15: 
		case 16: 
		case 17: 
		case 18: {
			return SLIME_AVERAGE;
		}
		case 21: 
		case 22: {
			return WOLF_WILD;
		}
		}
		return null;
	}

	public String toDifficultyNode(boolean useSmallNodes) {
		if (useSmallNodes) {
			return this.toString().split("_")[0].toLowerCase();
		}
		return this.toString().replace("_", ".").toLowerCase();
	}
	
	@Override
	public static CdCreatureType valueOf(String string) {
		return (CdCreatureType)((Object)Enum.valueOf(CdCreatureType.class, string));
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

	static /* synthetic */ int[] $SWITCH_TABLE$Pasukaru$CustomDifficulty$util$CdCreatureType() {
		int[] arrn;
		int[] arrn2 = $SWITCH_TABLE$Pasukaru$CustomDifficulty$util$CdCreatureType;
		if (arrn2 != null) {
			return arrn2;
		}
		arrn = new int[CdCreatureType.values().length];
		try {
			arrn[CdCreatureType.CAVESPIDER.ordinal()] = 1;
		}
		catch (NoSuchFieldError v1) {}
		try {
			arrn[CdCreatureType.CHICKEN.ordinal()] = 2;
		}
		catch (NoSuchFieldError v2) {}
		try {
			arrn[CdCreatureType.COW.ordinal()] = 3;
		}
		catch (NoSuchFieldError v3) {}
		try {
			arrn[CdCreatureType.CREEPER_POWERED.ordinal()] = 4;
		}
		catch (NoSuchFieldError v4) {}
		try {
			arrn[CdCreatureType.CREEPER_UNPOWERED.ordinal()] = 5;
		}
		catch (NoSuchFieldError v5) {}
		try {
			arrn[CdCreatureType.ENDERMAN.ordinal()] = 6;
		}
		catch (NoSuchFieldError v6) {}
		try {
			arrn[CdCreatureType.GHAST.ordinal()] = 7;
		}
		catch (NoSuchFieldError v7) {}
		try {
			arrn[CdCreatureType.GIANT.ordinal()] = 8;
		}
		catch (NoSuchFieldError v8) {}
		try {
			arrn[CdCreatureType.MONSTER.ordinal()] = 9;
		}
		catch (NoSuchFieldError v9) {}
		try {
			arrn[CdCreatureType.PIG.ordinal()] = 10;
		}
		catch (NoSuchFieldError v10) {}
		try {
			arrn[CdCreatureType.PIGZOMBIE.ordinal()] = 11;
		}
		catch (NoSuchFieldError v11) {}
		try {
			arrn[CdCreatureType.SHEEP.ordinal()] = 12;
		}
		catch (NoSuchFieldError v12) {}
		try {
			arrn[CdCreatureType.SILVERFISH.ordinal()] = 13;
		}
		catch (NoSuchFieldError v13) {}
		try {
			arrn[CdCreatureType.SKELETON.ordinal()] = 14;
		}
		catch (NoSuchFieldError v14) {}
		try {
			arrn[CdCreatureType.SLIME_AVERAGE.ordinal()] = 15;
		}
		catch (NoSuchFieldError v15) {}
		try {
			arrn[CdCreatureType.SLIME_BIG.ordinal()] = 16;
		}
		catch (NoSuchFieldError v16) {}
		try {
			arrn[CdCreatureType.SLIME_HUGE.ordinal()] = 17;
		}
		catch (NoSuchFieldError v17) {}
		try {
			arrn[CdCreatureType.SLIME_SMALL.ordinal()] = 18;
		}
		catch (NoSuchFieldError v18) {}
		try {
			arrn[CdCreatureType.SPIDER.ordinal()] = 19;
		}
		catch (NoSuchFieldError v19) {}
		try {
			arrn[CdCreatureType.SQUID.ordinal()] = 20;
		}
		catch (NoSuchFieldError v20) {}
		try {
			arrn[CdCreatureType.WOLF_TAMED.ordinal()] = 21;
		}
		catch (NoSuchFieldError v21) {}
		try {
			arrn[CdCreatureType.WOLF_WILD.ordinal()] = 22;
		}
		catch (NoSuchFieldError v22) {}
		try {
			arrn[CdCreatureType.ZOMBIE.ordinal()] = 23;
		}
		catch (NoSuchFieldError v23) {}
		$SWITCH_TABLE$Pasukaru$CustomDifficulty$util$CdCreatureType = arrn;
		return $SWITCH_TABLE$Pasukaru$CustomDifficulty$util$CdCreatureType;
	}
}

