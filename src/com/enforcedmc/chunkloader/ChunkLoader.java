package com.enforcedmc.chunkloader;

import java.util.UUID;

import org.bukkit.Location;

public class ChunkLoader
{
    private String name;
    private UUID owner;
    private Location loc;
    
    public ChunkLoader(final String name, final UUID owner, final Location loc) {
        this.name = name;
        this.owner = owner;
        this.loc = loc;
    }
    
    public String getName() {
        return this.name;
    }
    
    public UUID getOwner() {
        return this.owner;
    }
    
    public Location getLocation() {
        return this.loc;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
}
