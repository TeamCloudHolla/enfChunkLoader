package com.enforcedmc.chunkloader.listeners;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import com.enforcedmc.chunkloader.Main;
import com.enforcedmc.chunkloader.Manager;
import com.enforcedmc.chunkloader.item.ChunkLoaderItem;

public class ExplosionListener implements Listener
{
    private Manager m;
    
    public ExplosionListener() {
        this.m = Manager.getManager();
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityExplode(final EntityExplodeEvent e) {
        for (final Block b : e.blockList()) {
            if (this.m.existsChunkLoader(b.getLocation())) {
                this.m.removeChunkLoader(this.m.getChunkLoader(b.getLocation()));
                if (!Main.getInstance().dropOnBreak()) {
                    continue;
                }
                b.getWorld().dropItemNaturally(b.getLocation(), ChunkLoaderItem.getItem());
            }
        }
    }
}
