package com.enforcedmc.chunkloader;

import java.util.UUID;

import org.bukkit.Chunk;

public class MCChunk
{
    private String name;
    private UUID owner;
    private Chunk chunk;
    
    public MCChunk(final String name, final UUID owner, final Chunk chunk) {
        this.name = name;
        this.owner = owner;
        this.chunk = chunk;
    }
    
    public String getName() {
        return this.name;
    }
    
    public UUID getOwner() {
        return this.owner;
    }
    
    public Chunk getChunk() {
        return this.chunk;
    }
    
    public void setName(final String s) {
        this.name = s;
    }
}
