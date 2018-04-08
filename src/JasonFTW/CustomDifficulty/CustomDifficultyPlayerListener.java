package jasonftw.CustomDifficulty;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import jasonftw.CustomDifficulty.commands.Performance;
import jasonftw.CustomDifficulty.util.Config;
import jasonftw.CustomDifficulty.util.CreatureInfo;
import jasonftw.CustomDifficulty.util.Manager;

public class CustomDifficultyPlayerListener implements Listener {
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        if (event.getPlayer().isOp() && Config.config.getBoolean("global.checkForUpdates", true)) {
            new Thread(){

                @Override
                public void run() {
                    try {
                        if (Manager.checkForUpdates()) {
                            player.sendMessage((Object)ChatColor.GREEN + "A new version of " + (Object)ChatColor.AQUA + "CustomDifficulty" + (Object)ChatColor.GREEN + " is available!");
                        }
                    }
                    catch (Exception ex) {
                        // empty catch block
                    }
                }
            }.start();
        }
    }

    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Wolf w;
        AnimalTamer o;
        long time = System.nanoTime();
        if (event.isCancelled()) {
            if (Performance.isRunning()) {
                Performance.onPlayerInteract += System.nanoTime() - time;
            }
            return;
        }
        Player p = event.getPlayer();
        if (Manager.getDifficulty(p.getWorld()) == null) {
            if (Performance.isRunning()) {
                Performance.onPlayerInteract += System.nanoTime() - time;
            }
            return;
        }
        Entity e = event.getRightClicked();
        Material m = p.getInventory().getItemInMainHand().getType();
        if (e instanceof Wolf && (m == Material.PORK || m == Material.GRILLED_PORK || m == Material.ROTTEN_FLESH) && (o = (w = (Wolf)e).getOwner()) != null && o instanceof Player && o == p) {
            int maxHP;
            double hp = w.getHealth();
            if (hp >= (maxHP = CreatureInfo.getHealth((LivingEntity)w, null))) {
                p.sendMessage((Object)ChatColor.GREEN + "Your wolf isn't hungry!");
            } else if (maxHP != 0) {
                if ((hp += 3) > maxHP) {
                    hp = maxHP;
                }
                w.setHealth(hp);
                ItemStack item = p.getInventory().getItemInMainHand();
                int amount = item.getAmount();
                if (amount > 1) {
                    item.setAmount(amount - 1);
                } else {
                    p.getInventory().remove(item);
                }
                p.sendMessage((Object)ChatColor.GREEN + "You fed your wolf. (Health: " + hp * 100 / maxHP + "%)");
            }
            event.setCancelled(true);
        }
        if (Performance.isRunning()) {
            Performance.onPlayerInteract += System.nanoTime() - time;
        }
    }

}

