/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.world.ChunkUnloadEvent
 *  org.bukkit.event.world.WorldListener
 *  org.bukkit.event.world.WorldUnloadEvent
 */
package JasonFTW.CustomDifficulty;

import JasonFTW.CustomDifficulty.SchedulerTasks.AggressivenessControl;
import JasonFTW.CustomDifficulty.SchedulerTasks.BurnInSunlightControl;
import JasonFTW.CustomDifficulty.SchedulerTasks.SpawnControl;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldUnloadEvent;

public class CustomDifficultyWorldListener
extends WorldListener {
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

    public void onWorldUnload(WorldUnloadEvent event) {
        World world = event.getWorld();
        SpawnControl.deactivate(world);
        AggressivenessControl.deactivate(world);
        BurnInSunlightControl.deactivate(world);
    }
}

