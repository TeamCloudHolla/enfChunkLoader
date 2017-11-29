package com.enforcedmc.chunkloader.listeners;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import com.enforcedmc.chunkloader.ChunkLoader;
import com.enforcedmc.chunkloader.Main;
import com.enforcedmc.chunkloader.Manager;
import com.enforcedmc.chunkloader.ParticlePlayer;
import com.enforcedmc.chunkloader.item.ChunkLoaderItem;

public class BlockPlaceListener implements Listener
{
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlace(final BlockPlaceEvent e) {
        final ItemStack chunkLoader = ChunkLoaderItem.getItem();
        chunkLoader.setAmount(e.getPlayer().getItemInHand().getAmount());
        if (!e.getPlayer().getItemInHand().equals((Object)chunkLoader)) {
            return;
        }
        final Player p = e.getPlayer();
        final Manager m = Manager.getManager();
        final Chunk chunk = e.getBlockPlaced().getChunk();
        if (m.isLoaded(chunk)) {
            p.sendMessage("§cThis chunk is already being kept loaded.");
            e.setCancelled(true);
            return;
        }
        if (m.getAllLoadedChunks(p.getUniqueId()).size() >= Main.getInstance().getMaximalChunksPerUser()) {
            p.sendMessage("§cYou already own too many chunks!");
            e.setCancelled(true);
            return;
        }
        final ChunkLoader cl = new ChunkLoader(m.getRandomName(p.getUniqueId()), p.getUniqueId(), e.getBlockPlaced().getLocation());
        p.sendMessage("§2You successfully placed a ChunkLoader.");
        m.addChunkLoader(cl);
        new ParticlePlayer(2).playParticles(p, chunk);
    }
}
