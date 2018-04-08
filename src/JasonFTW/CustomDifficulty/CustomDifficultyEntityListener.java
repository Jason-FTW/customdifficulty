package jasonftw.CustomDifficulty;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreeperPowerEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import jasonftw.CustomDifficulty.SchedulerTasks.MobCounter;
import jasonftw.CustomDifficulty.commands.Performance;
import jasonftw.CustomDifficulty.hooks.Register;
import jasonftw.CustomDifficulty.util.Aggressiveness;
import jasonftw.CustomDifficulty.util.CdCreatureType;
import jasonftw.CustomDifficulty.util.Config;
import jasonftw.CustomDifficulty.util.CreatureInfo;
import jasonftw.CustomDifficulty.util.Difficulty;
import jasonftw.CustomDifficulty.util.DifficultyDefaults;
import jasonftw.CustomDifficulty.util.DropItem;
import jasonftw.CustomDifficulty.util.Manager;
import jasonftw.CustomDifficulty.util.PlayerInfo;

class CustomDifficultyEntityListener implements Listener {
	private static /* synthetic */ int[] $SWITCH_TABLE$Pasukaru$CustomDifficulty$util$Aggressiveness;
	private static /* synthetic */ int[] $SWITCH_TABLE$org$bukkit$event$entity$EntityTargetEvent$TargetReason;

	CustomDifficultyEntityListener() {}

	private static boolean performInSunlightCheck(LivingEntity entity) {
		World world = entity.getWorld();
		Difficulty difficulty = Manager.useRegion(world, "burnsInSunlight") ? Manager.getDifficulty(entity) : Manager.getDifficulty(world);
		if (difficulty == null) {
			return DifficultyDefaults.burnsInSunlight((Entity)entity);
		}
		if (difficulty.burnsInSunlight(CdCreatureType.valueOf((Entity)entity)) == 0 && CreatureInfo.isInSunlight((Entity)entity)) {
			entity.setFireTicks(0);
			return true;
		}
		return false;
	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		long time = System.nanoTime();
		Entity e = event.getEntity();
		World world = e.getWorld();
		Difficulty worldDifficulty = Manager.getDifficulty(world);
		if (event.isCancelled() || worldDifficulty == null) {
			if (Performance.isRunning()) {
				Performance.onCreatureSpawn += System.nanoTime() - time;
			}
			return;
		}
		if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
			event.setCancelled(true);
		} else if (MobCounter.getMobsOnServer() >= Manager.getMobLimit()) {
			event.setCancelled(true);
		} else if (e instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity)event.getEntity();
			Difficulty difficulty = Manager.getDifficulty(e.getLocation());
			if (difficulty != null) {
				Manager.addMob(livingEntity, difficulty, "CreatureSpawnEvent");
			} else {
				difficulty = Manager.getDifficulty(world);
			}
			int health = CreatureInfo.getHealth(livingEntity, difficulty);
			if (health > 0) {
				livingEntity.setHealth(health);
			} else if (health == 0) {
				event.setCancelled(true);
			}
		}
		if (Performance.isRunning()) {
			Performance.onCreatureSpawn += System.nanoTime() - time;
		}
	}

	@EventHandler
	public void onCreeperPower(CreeperPowerEvent event) {
		long time = System.nanoTime();
		Entity e = event.getEntity();
		if (!event.isCancelled() && e instanceof LivingEntity) {
			int health;
			LivingEntity entity = (LivingEntity)e;
			Difficulty difficulty = Manager.getDifficulty(entity);
			if (difficulty == null) {
				difficulty = Manager.getDifficulty(e.getWorld());
			}
			if ((health = CreatureInfo.getHealth(entity, difficulty)) == 0) {
				e.remove();
			} else if (health > 0) {
				((LivingEntity)e).setHealth(health);
			}
		}
		if (Performance.isRunning()) {
			Performance.onCreeperPower += System.nanoTime() - time;
		}
	}

	@EventHandler
	public void onEntityCombust(EntityCombustEvent event) {
		long time = System.nanoTime();
		Entity e = event.getEntity();
		if (e instanceof LivingEntity) {
			LivingEntity entity = (LivingEntity)e;
			if (event.isCancelled() || Manager.getDifficulty(e.getWorld()) == null) {
				return;
			}
			if (CustomDifficultyEntityListener.performInSunlightCheck(entity)) {
				event.setCancelled(true);
			}
		}
		if (Performance.isRunning()) {
			Performance.onEntityCombust += System.nanoTime() - time;
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		long time = System.nanoTime();
		Entity damagee = event.getEntity();
		if (event.isCancelled() || Manager.getDifficulty(damagee.getWorld()) == null) {
			if (Performance.isRunning()) {
				Performance.onEntityDamage += System.nanoTime() - time;
			}
			return;
		}
		if (event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION && (damagee instanceof Giant || damagee instanceof Ghast)) {
			damagee.remove();
			event.setCancelled(true);
		} else if (event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK && damagee instanceof LivingEntity && CustomDifficultyEntityListener.performInSunlightCheck((LivingEntity)damagee)) {
			event.setCancelled(true);
		} else if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK && event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent edbeEvent = (EntityDamageByEntityEvent)event;
			Entity damager = edbeEvent.getDamager();
			if (damagee instanceof Creature) {
				Creature c;
				Aggressiveness a;
				boolean skip = false;
				if (damagee instanceof Wolf && ((Wolf)damagee).getOwner() == damager) {
					skip = true;
				}
				if (!skip && (a = CreatureInfo.getAggressiveness((LivingEntity)(c = (Creature)damagee))) != null && damager instanceof LivingEntity) {
					LivingEntity leDamager = (LivingEntity)damager;
					switch (CustomDifficultyEntityListener.$SWITCH_TABLE$Pasukaru$CustomDifficulty$util$Aggressiveness()[a.ordinal()]) {
					case 1: {
						c.setTarget(leDamager);
						break;
					}
					case 2: {
						c.setTarget(null);
						break;
					}
					case 4: {
						c.setTarget(leDamager);
					}
					}
				}
			}
			if (!(damager instanceof LivingEntity) || !(damagee instanceof LivingEntity)) {
				if (Performance.isRunning()) {
					Performance.onEntityDamage += System.nanoTime() - time;
				}
				return;
			}
			int damage = -1;
			damage = damager instanceof Player ? PlayerInfo.getDamage((Player)damager, (LivingEntity)damagee, event.getDamage()) : CreatureInfo.getDamage(edbeEvent);
			if (damage == 0) {
				event.setCancelled(true);
			}
			if (damage >= 1) {
				event.setDamage(damage);
			}
		}
		if (Performance.isRunning()) {
			Performance.onEntityDamage += System.nanoTime() - time;
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		AnimalTamer owner;
		CdCreatureType cd;
		EntityDamageByEntityEvent edbee;
		CdCreatureType[] lastKills;
		long time = System.nanoTime();
		LivingEntity e = (LivingEntity)event.getEntity();
		World world = e.getWorld();
		if (Manager.getDifficulty(world) == null) {
			if (Performance.isRunning()) {
				Performance.onEntityDeath += System.nanoTime() - time;
			}
			return;
		}
		double maxMobSpawnerRadius = Config.config.getDouble("global.mobSpawnerRadius", 5.0);
		if (Register.isHooked() && e instanceof LivingEntity && !(e instanceof Player)) {
			this.entityDeath_HandleReward(event);
		}
		Difficulty difficulty = Manager.useRegion(world, "drops") ? Manager.getDifficulty(e) : Manager.getDifficulty(world);
		boolean skip = false;
		if (difficulty.getDropOnlyWhenKilledByPlayer()) {
			skip = this.entityDeath_DropOnlyWhenKilledByPlayerCheck(event, difficulty);
		}
		if (!(skip || difficulty == null || e instanceof Player || CreatureInfo.isMobSpawnerClose(e.getLocation(), maxMobSpawnerRadius) || (cd = CdCreatureType.valueOf((Entity)e)) == null)) {
			ArrayList<DropItem> drops = difficulty.getDrops(cd);
			if (drops.size() > 0) {
				event.getDrops().clear();
			}
			int i = 0;
			while (i < drops.size()) {
				ItemStack item = drops.get(i).getDrop();
				if (item != null) {
					event.getDrops().add(item);
				}
				++i;
			}
		}
		difficulty = Manager.useRegion(world, "lootMultiplier") ? Manager.getDifficulty(e) : Manager.getDifficulty(world);
		if (!skip && difficulty != null && !(e instanceof Player) && event.getDrops().size() > 0) {
			cd = CdCreatureType.valueOf((Entity)e);
			double lootMultiplier = 1.0;
			if (cd != null) {
				lootMultiplier = difficulty.getLootMultiplier(cd);
			}
			if (lootMultiplier == 0.0 || lootMultiplier > 1.0 && !CreatureInfo.isMobSpawnerClose(e.getLocation(), maxMobSpawnerRadius)) {
				List<ItemStack> drops = event.getDrops();
				if (lootMultiplier == 0.0) {
					drops.clear();
				} else if (lootMultiplier > 1.0) {
					ArrayList<ItemStack> multiples = new ArrayList<ItemStack>();
					for (ItemStack item : drops) {
						int leftover;
						int max = item.getType().getMaxStackSize();
						if (max < 1) {
							max = 1;
						}
						if ((leftover = (int)Math.round((double)item.getAmount() * lootMultiplier)) <= 0) continue;
						int amount = max - item.getAmount();
						if (amount > leftover) {
							amount = leftover;
						}
						if (amount < 1) continue;
						item.setAmount(amount);
						while ((leftover -= amount) > 0) {
							ItemStack add = item.clone();
							if (leftover < max) {
								add.setAmount(leftover);
								leftover = 0;
							} else {
								add.setAmount(max);
								leftover -= max;
							}
							multiples.add(add);
						}
					}
					drops.addAll(multiples);
				}
			}
		}
		if (event.getEntity() instanceof Wolf && ((Wolf)event.getEntity()).isTamed() && (owner = ((Wolf)event.getEntity()).getOwner()) instanceof Player) {
			((Player)owner).sendMessage((Object)ChatColor.RED + "Your wolf has been killed. :(");
		}
		if (!(e instanceof Player) && e.getLastDamageCause() instanceof EntityDamageByEntityEvent && (edbee = (EntityDamageByEntityEvent)e.getLastDamageCause()).getDamager() instanceof Player && (lastKills = Manager.getLastKills((Player)edbee.getDamager())).length > 0) {
			int i = lastKills.length - 1;
			while (i > 0) {
				lastKills[i] = lastKills[i - 1];
				--i;
			}
			lastKills[0] = CdCreatureType.valueOf((Entity)e);
		}
		if (!(e instanceof Player)) {
			Manager.removeMob(e, "EntityDeathEvent");
		}
		if (Performance.isRunning()) {
			Performance.onEntityDeath += System.nanoTime() - time;
		}
	}

	@EventHandler
	public void onEntityTame(EntityTameEvent event) {
		long time = System.nanoTime();
		Entity e = event.getEntity();
		if (event.isCancelled() || Manager.getDifficulty(e.getWorld()) == null) {
			if (Performance.isRunning()) {
				Performance.onEntityTame += System.nanoTime() - time;
			}
			return;
		}
		if (e instanceof Tameable && e instanceof LivingEntity) {
			Tameable t = (Tameable)e;
			LivingEntity le = (LivingEntity)e;
			t.setTamed(true);
			t.setOwner(event.getOwner());
			int health = CreatureInfo.getHealth(le, null);
			if (health > 0) {
				le.setHealth(health);
			}
			if (t instanceof Creature && t.getOwner() == ((Creature)t).getTarget()) {
				((Creature)t).setTarget(null);
			}
			for (LivingEntity livE : event.getEntity().getWorld().getLivingEntities()) {
				Wolf w;
				if (!(livE instanceof Wolf) || (w = (Wolf)livE).getOwner() != event.getOwner() || w.getTarget() != e) continue;
				w.setTarget(null);
			}
			if (event.getOwner() instanceof CommandSender) {
				((CommandSender)event.getOwner()).sendMessage((Object)ChatColor.GREEN + "Taming successful!");
			}
			event.setCancelled(true);
		}
		if (Performance.isRunning()) {
			Performance.onEntityTame += System.nanoTime() - time;
		}
	}

	/*
	 * Unable to fully structure code
	 * Enabled aggressive block sorting
	 * Lifted jumps to return sites
	 */
	@EventHandler
	public void onEntityTarget(EntityTargetEvent event) {
		long time = System.nanoTime();
		Entity e = event.getEntity();
		World world = e.getWorld();
		if (event.isCancelled() || Manager.getDifficulty(world) == null) {
			if (Performance.isRunning() == false) return;
			Performance.onEntityTarget += System.nanoTime() - time;
			return;
		}
		if (e instanceof Wolf && ((Wolf)e).isTamed()) {
			Wolf w = (Wolf)e;
			if (event.getTarget() == w.getOwner()) {
				event.setCancelled(true);
			}
		} else if (e instanceof Creature) {
			Creature c = (Creature)e;
			Aggressiveness a = CreatureInfo.getAggressiveness((LivingEntity)c);
			block0 : switch (CustomDifficultyEntityListener.$SWITCH_TABLE$org$bukkit$event$entity$EntityTargetEvent$TargetReason()[event.getReason().ordinal()]) {
			case 2: {
				switch (CustomDifficultyEntityListener.$SWITCH_TABLE$Pasukaru$CustomDifficulty$util$Aggressiveness()[a.ordinal()]) {
				case 1: {
					break;
				}
				case 2: {
					c.setTarget(null);
					event.setCancelled(true);
					break;
				}
				case 4: {
					if (c instanceof Enderman) break block0;
					event.setCancelled(true);
				}
				}
				break;
			}
			case 3: 
			case 4: {
				switch (CustomDifficultyEntityListener.$SWITCH_TABLE$Pasukaru$CustomDifficulty$util$Aggressiveness()[a.ordinal()]) {
				case 1: {
					break;
				}
				case 2: {
					c.setTarget(null);
					event.setCancelled(true);
					break;
				}
				}
			}
			default: {
				break;
			}
			}
		} else if (e instanceof Ghast) {
			Ghast g = (Ghast)e;
			Aggressiveness a = CreatureInfo.getAggressiveness((LivingEntity)g);
			switch (CustomDifficultyEntityListener.$SWITCH_TABLE$org$bukkit$event$entity$EntityTargetEvent$TargetReason()[event.getReason().ordinal()]) {
			case 2: {
				if (a == Aggressiveness.FRIENDLY || a == Aggressiveness.PASSIVE) {
					event.setCancelled(true);
					break;
				}
				break;
			}
			case 3: {
				System.out.println((Object)a);
				if (a == Aggressiveness.FRIENDLY) {
					event.setCancelled(true);
					break;
				}
				if (a != Aggressiveness.PASSIVE) break;
			}
			}
		}
		if (Performance.isRunning() == false) return;
		Performance.onEntityTarget += System.nanoTime() - time;
	}

	private void entityDeath_HandleReward(EntityDeathEvent event) {
		Entity e = event.getEntity();
		LivingEntity damagee = (LivingEntity)e;
		EntityDamageEvent lastDamage = e.getLastDamageCause();
		if (lastDamage instanceof EntityDamageByEntityEvent) {
			double reward;
			ProjectileSource shooter;
			Entity damager = ((EntityDamageByEntityEvent)lastDamage).getDamager();
			Player player = null;
			if (damager instanceof Wolf) {
				Wolf wolf = (Wolf)damager;
				if (wolf.isTamed() && wolf.getOwner() instanceof Player) {
					player = (Player)wolf.getOwner();
				}
			} else if (damager instanceof Player) {
				player = (Player)damager;
			} else if (damager instanceof Projectile && (shooter = ((Projectile)damager).getShooter()) instanceof Player) {
				player = (Player)shooter;
			}
			if (player != null && (reward = PlayerInfo.getReward(player, damagee)) != 0.0 && !CreatureInfo.isMobSpawnerClose(e.getLocation(), Config.config.getDouble("global.mobSpawnerRadius", 5.0)) && Register.addMoney(player, reward)) {
				if (reward > 0.0) {
					player.sendMessage((Object)ChatColor.GREEN + "You received " + (Object)ChatColor.YELLOW + Register.format(reward) + (Object)ChatColor.GREEN + " for killing a " + (Object)ChatColor.AQUA + e.getClass().getSimpleName().replace("Craft", "") + (Object)ChatColor.GREEN + "!");
				} else {
					player.sendMessage((Object)ChatColor.RED + "You lost " + (Object)ChatColor.YELLOW + Register.format(reward) + (Object)ChatColor.RED + " because you killed a " + (Object)ChatColor.AQUA + e.getClass().getSimpleName().replace("Craft", "") + (Object)ChatColor.RED + "!");
				}
			}
		}
	}

	private boolean entityDeath_DropOnlyWhenKilledByPlayerCheck(EntityDeathEvent event, Difficulty difficulty) {
		Entity e = event.getEntity();
		if (!(e instanceof Player)) {
			EntityDamageEvent lastDamageEvent = e.getLastDamageCause();
			if (lastDamageEvent != null && lastDamageEvent instanceof EntityDamageByEntityEvent) {
				ProjectileSource shooter;
				EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent)lastDamageEvent;
				Entity damager = edbee.getDamager();
				if (!(damager instanceof Player) && !(damager instanceof Projectile)) {
					event.getDrops().clear();
					return true;
				}
				if (damager instanceof Projectile && !((shooter = ((Projectile)damager).getShooter()) instanceof Player)) {
					event.getDrops().clear();
					return true;
				}
			} else {
				event.getDrops().clear();
				return true;
			}
		}
		return false;
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

	static /* synthetic */ int[] $SWITCH_TABLE$org$bukkit$event$entity$EntityTargetEvent$TargetReason() {
		int[] arrn;
		int[] arrn2 = $SWITCH_TABLE$org$bukkit$event$entity$EntityTargetEvent$TargetReason;
		if (arrn2 != null) {
			return arrn2;
		}
		arrn = new int[EntityTargetEvent.TargetReason.values().length];
		try {
			arrn[EntityTargetEvent.TargetReason.CLOSEST_PLAYER.ordinal()] = 2;
		}
		catch (NoSuchFieldError v1) {}
		try {
			arrn[EntityTargetEvent.TargetReason.CUSTOM.ordinal()] = 9;
		}
		catch (NoSuchFieldError v2) {}
		try {
			arrn[EntityTargetEvent.TargetReason.FORGOT_TARGET.ordinal()] = 5;
		}
		catch (NoSuchFieldError v3) {}
		try {
			arrn[EntityTargetEvent.TargetReason.OWNER_ATTACKED_TARGET.ordinal()] = 7;
		}
		catch (NoSuchFieldError v4) {}
		try {
			arrn[EntityTargetEvent.TargetReason.PIG_ZOMBIE_TARGET.ordinal()] = 4;
		}
		catch (NoSuchFieldError v5) {}
		try {
			arrn[EntityTargetEvent.TargetReason.RANDOM_TARGET.ordinal()] = 8;
		}
		catch (NoSuchFieldError v6) {}
		try {
			arrn[EntityTargetEvent.TargetReason.TARGET_ATTACKED_ENTITY.ordinal()] = 3;
		}
		catch (NoSuchFieldError v7) {}
		try {
			arrn[EntityTargetEvent.TargetReason.TARGET_ATTACKED_OWNER.ordinal()] = 6;
		}
		catch (NoSuchFieldError v8) {}
		try {
			arrn[EntityTargetEvent.TargetReason.TARGET_DIED.ordinal()] = 1;
		}
		catch (NoSuchFieldError v9) {}
		$SWITCH_TABLE$org$bukkit$event$entity$EntityTargetEvent$TargetReason = arrn;
		return $SWITCH_TABLE$org$bukkit$event$entity$EntityTargetEvent$TargetReason;
	}
}

