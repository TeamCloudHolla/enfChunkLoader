package com.enforcedmc.chunkloader;

import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutWorldParticles;

import org.bukkit.Chunk;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ParticlePlayer
{
    private int duration;
    private int i;
    
    public ParticlePlayer(final int d) {
        this.i = 0;
        this.duration = d;
    }
    
    public void playParticles(final Player p, final Chunk chunk) {
        if (!Main.getInstance().showParticles()) {
            return;
        }
        new BukkitRunnable() {
            public void run() {
                if (ParticlePlayer.this.i >= ParticlePlayer.this.duration * 20) {
                    this.cancel();
                    return;
                }
                final int beginX = chunk.getX() * 16;
                final int beginZ = chunk.getZ() * 16;
                for (int y = p.getLocation().getBlockY(); y < p.getLocation().getBlockY() + 3; ++y) {
                    for (int z = beginZ; z < beginZ + 17; z += 16) {
                        for (int x = beginX; x < beginX + 16; ++x) {
                            final PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.FLAME, true, (float)x, (float)y, (float)z, 0.0f, 0.0f, 0.0f, 0.0f, 1, (int[])null);
                            ((CraftPlayer)p).getHandle().playerConnection.sendPacket((Packet)packet);
                        }
                    }
                    for (int x2 = beginX; x2 < beginX + 17; x2 += 16) {
                        for (int z2 = beginZ; z2 < beginZ + 16; ++z2) {
                            final PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.FLAME, true, (float)x2, (float)y, (float)z2, 0.0f, 0.0f, 0.0f, 0.0f, 1, (int[])null);
                            ((CraftPlayer)p).getHandle().playerConnection.sendPacket((Packet)packet);
                        }
                    }
                }
                final ParticlePlayer this$0 = ParticlePlayer.this;
                ParticlePlayer.access$2(this$0, this$0.i + 10);
            }
        }.runTaskTimerAsynchronously((Plugin)Main.getInstance(), 0L, 10L);
    }
    
    static /* synthetic */ void access$2(final ParticlePlayer particlePlayer, final int i) {
        particlePlayer.i = i;
    }
}
