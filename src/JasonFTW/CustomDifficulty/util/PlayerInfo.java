package jasonftw.CustomDifficulty.util;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import jasonftw.CustomDifficulty.hooks.Permissions;

public abstract class PlayerInfo {
	public static int getDamage(Player player, LivingEntity target, double d) {
		double damageMultiply;
		short damageAdd;
		World world = player.getWorld();
		String useRegionNode = target instanceof Player ? "playerVsPlayer" : "playerVsMonster";
		Difficulty difficulty = Manager.useRegion(world, useRegionNode) ? Manager.getDifficulty(player.getLocation()) : Manager.getDifficulty(world);
		if (difficulty == null) {
			return -1;
		}
		if (target instanceof Player) {
			damageAdd = difficulty.getPlayerVsPlayerDamageAdd();
			damageMultiply = difficulty.getPlayerVsPlayerDamageMultiply();
		} else {
			damageAdd = difficulty.getPlayerVsMonsterDamageAdd();
			damageMultiply = difficulty.getPlayerVsMonsterDamageMultiply();
		}
		int damage = (int)Math.round((double)d * damageMultiply + (double)damageAdd);
		if (damage < 1) {
			damage = 1;
		} else if (damage > 100) {
			damage = 100;
		}
		return damage;
	}

	public static double getReward(Player player, LivingEntity e) {
		double reward;
		if (!Permissions.has((CommandSender)player, "CustomDifficulty.reward")) {
			return 0.0;
		}
		World world = e.getWorld();
		Difficulty difficulty = Manager.useRegion(world, "reward") ? Manager.getDifficulty(e) : Manager.getDifficulty(world);
		if (difficulty == null) {
			return 0.0;
		}
		CdCreatureType currentKill = CdCreatureType.valueOf((Entity)e);
		if (currentKill == null) {
			return 0.0;
		}
		reward = difficulty.getReward(currentKill);
		CdCreatureType[] lastKills = Manager.getLastKills(player);
		double factor = 1.0 - difficulty.getRewardDepreciation() / 100.0;
		if (factor > 1.0) {
			factor = 1.0;
		} else if (factor < 0.0) {
			factor = 0.0;
		}
		if (factor != 1.0) {
			int i = 0;
			while (i < lastKills.length) {
				CdCreatureType kill = lastKills[i];
				if (kill != null && kill == currentKill) {
					reward *= factor;
					++i;
					continue;
				}
				break;
			}
		} else if (factor == 0.0) {
			reward = 0.0;
		}
		return reward;
	}
}

