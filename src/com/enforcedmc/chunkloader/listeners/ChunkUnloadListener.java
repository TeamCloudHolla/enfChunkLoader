package com.enforcedmc.chunkloader.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.enforcedmc.chunkloader.ChunkLoader;
import com.enforcedmc.chunkloader.MCChunk;
import com.enforcedmc.chunkloader.Main;
import com.enforcedmc.chunkloader.Manager;

public class ChunkUnloadListener implements Listener
{
    private List<Chunk> chunks;
    
    public ChunkUnloadListener() {
        this.chunks = new ArrayList<Chunk>();
    }
    
    @EventHandler
    public void onUnload(final ChunkUnloadEvent e) {
        final Manager m = Manager.getManager();
        for (final MCChunk c : m.getBoughtChunks()) {
            if (c.getChunk().equals(e.getChunk())) {
                if (Main.getInstance().infiniteLoadedTimeCommand()) {
                    e.setCancelled(true);
                    return;
                }
                if (!this.chunks.contains(e.getChunk())) {
                    this.chunks.add(e.getChunk());
                    new BukkitRunnable() {
                        public void run() {
                            ChunkUnloadListener.this.chunks.remove(e.getChunk());
                        }
                    }.runTaskLaterAsynchronously((Plugin)Main.getInstance(), Main.getInstance().getLoadedTimeCommand() * 20L);
                    e.setCancelled(true);
                    return;
                }
                e.setCancelled(false);
            }
        }
        for (final ChunkLoader cl : m.getChunkLoaders()) {
            if (cl.getLocation().getChunk().equals(e.getChunk())) {
                if (Main.getInstance().infiniteLoadedTimeChunkLoader()) {
                    e.setCancelled(true);
                    return;
                }
                if (!this.chunks.contains(e.getChunk())) {
                    this.chunks.add(e.getChunk());
                    new BukkitRunnable() {
                        public void run() {
                            ChunkUnloadListener.this.chunks.remove(e.getChunk());
                        }
                    }.runTaskLaterAsynchronously((Plugin)Main.getInstance(), Main.getInstance().getLoadedTimeChunkLoader() * 20L);
                    e.setCancelled(true);
                    return;
                }
                e.setCancelled(false);
            }
        }
    }
}
