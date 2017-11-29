package com.enforcedmc.chunkloader.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.enforcedmc.chunkloader.ChunkLoader;
import com.enforcedmc.chunkloader.Main;
import com.enforcedmc.chunkloader.Manager;
import com.enforcedmc.chunkloader.item.ChunkLoaderItem;

public class BlockBreakListener implements Listener
{
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(final BlockBreakEvent e) {
        if (e.getBlock().getType() != Material.GOLD_BLOCK) {
            return;
        }
        final Player p = e.getPlayer();
        final Location loc = e.getBlock().getLocation();
        final Manager m = Manager.getManager();
        if (!m.existsChunkLoader(loc)) {
            return;
        }
        final ChunkLoader cl = m.getChunkLoader(loc);
        if (!cl.getOwner().equals(p.getUniqueId())) {
            p.sendMessage("§cYou don't own this ChunkLoader.");
            e.setCancelled(true);
            return;
        }
        e.getBlock().getDrops().clear();
        if (Main.getInstance().dropOnBreak()) {
            loc.getWorld().dropItemNaturally(loc, ChunkLoaderItem.getItem());
        }
        m.removeChunkLoader(cl);
        p.sendMessage("§2ChunkLoader has been removed.");
    }
}
