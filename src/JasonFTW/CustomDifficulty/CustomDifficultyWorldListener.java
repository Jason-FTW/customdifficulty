package jasonftw.CustomDifficulty;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import jasonftw.CustomDifficulty.SchedulerTasks.AggressivenessControl;
import jasonftw.CustomDifficulty.SchedulerTasks.BurnInSunlightControl;
import jasonftw.CustomDifficulty.SchedulerTasks.SpawnControl;

public class CustomDifficultyWorldListener implements Listener {
	public void onChunkUnload(ChunkUnloadEvent event) {
		Entity[] entities = event.getChunk().getEntities();
		int i = 0;
		while (i < entities.length) {
			Entity e = entities[i];
			if (e instanceof LivingEntity && !(e instanceof Player)) {
				e.remove();
			}
			++i;
		}
	}

	@EventHandler
	public void onWorldUnload(WorldUnloadEvent event) {
		World world = event.getWorld();
		SpawnControl.deactivate(world);
		AggressivenessControl.deactivate(world);
		BurnInSunlightControl.deactivate(world);
	}
}

