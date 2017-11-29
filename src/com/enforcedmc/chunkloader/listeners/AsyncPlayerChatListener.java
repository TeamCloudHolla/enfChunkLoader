package com.enforcedmc.chunkloader.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.enforcedmc.chunkloader.ChunkLoader;
import com.enforcedmc.chunkloader.Main;
import com.enforcedmc.chunkloader.Manager;

public class AsyncPlayerChatListener implements Listener
{
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(final AsyncPlayerChatEvent e) {
        if (!Main.getInstance().getRenaming().containsKey(e.getPlayer())) {
            return;
        }
        e.setCancelled(true);
        final Player p = e.getPlayer();
        final ChunkLoader cl = Main.getInstance().getRenaming().get(e.getPlayer());
        final String newName = e.getMessage();
        final Manager m = Manager.getManager();
        if (m.exists(p.getUniqueId(), newName)) {
            p.sendMessage("§cYou already own a chunk/ChunkLoader with that name.");
            p.sendMessage("§2Please choose another name.");
            return;
        }
        m.changeNameOfChunkLoader(cl, newName);
        e.getPlayer().sendMessage("§2Your ChunkLoader has been renamed to §f" + newName + "§2.");
        Main.getInstance().getRenaming().remove(e.getPlayer());
    }
}
