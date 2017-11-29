package com.enforcedmc.chunkloader.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.enforcedmc.chunkloader.ChunkLoader;
import com.enforcedmc.chunkloader.Main;
import com.enforcedmc.chunkloader.Manager;
import com.enforcedmc.chunkloader.ParticlePlayer;

public class PlayerInteractListener implements Listener
{
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(final PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        final Manager m = Manager.getManager();
        final Player p = e.getPlayer();
        if (p.getItemInHand().getType() != Material.NAME_TAG) {
            if (!m.existsChunkLoader(e.getClickedBlock().getLocation())) {
                return;
            }
            new ParticlePlayer(2).playParticles(e.getPlayer(), e.getClickedBlock().getChunk());
        }
        else {
            if (!m.existsChunkLoader(e.getClickedBlock().getLocation())) {
                return;
            }
            final ChunkLoader cl = m.getChunkLoader(e.getClickedBlock().getLocation());
            if (!cl.getOwner().equals(p.getUniqueId())) {
                p.sendMessage("§cYou don't own this ChunkLoader!");
                return;
            }
            p.sendMessage("§2Now type in the new name of your ChunkLoader.");
            Main.getInstance().getRenaming().put(p, cl);
            e.setCancelled(true);
        }
    }
}
