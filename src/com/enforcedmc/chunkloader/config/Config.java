package com.enforcedmc.chunkloader.config;

import com.enforcedmc.chunkloader.Main;

public class Config
{
    public boolean usingVault() {
        return Main.getInstance().getConfig().getInt("price") != -1.0;
    }
    
    public double getPrice() {
        return Main.getInstance().getConfig().getDouble("price");
    }
    
    public boolean usingChunkLoader() {
        return Main.getInstance().getConfig().getBoolean("chunkloaderblock");
    }
    
    public int getMaximalChunks() {
        return Main.getInstance().getConfig().getInt("maximalchunks");
    }
    
    public int getLoadedTimeCommand() {
        return Main.getInstance().getConfig().getInt("timecommand");
    }
    
    public int getLoadedTimeChunkLoader() {
        return Main.getInstance().getConfig().getInt("timechunkloader");
    }
    
    public boolean dropOnBreak() {
        return Main.getInstance().getConfig().getBoolean("droponbreak");
    }
    
    public boolean showParticles() {
        return Main.getInstance().getConfig().getBoolean("showParticles");
    }
}
