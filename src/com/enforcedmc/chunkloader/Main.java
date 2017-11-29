package com.enforcedmc.chunkloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.enforcedmc.chunkloader.command.ChunkLoaderCommand;
import com.enforcedmc.chunkloader.config.Config;
import com.enforcedmc.chunkloader.listeners.AsyncPlayerChatListener;
import com.enforcedmc.chunkloader.listeners.BlockBreakListener;
import com.enforcedmc.chunkloader.listeners.BlockPlaceListener;
import com.enforcedmc.chunkloader.listeners.ChunkUnloadListener;
import com.enforcedmc.chunkloader.listeners.ExplosionListener;
import com.enforcedmc.chunkloader.listeners.PlayerInteractListener;

public class Main extends JavaPlugin
{
    private static Main instance;
    private File chunkFile;
    private YamlConfiguration chunkConfig;
    private File chunkLoaderFile;
    private YamlConfiguration chunkLoaderConfig;
    private boolean usingVault;
    private double price;
    private boolean usingChunkLoader;
    private int maximalChunks;
    private int loadedTimeCommand;
    private int loadedTimeChunkLoader;
    private boolean dropOnBreak;
    private boolean showParticles;
    private List<Chunk> waitingChunks;
    private Map<Player, ChunkLoader> renaming;
    private Economy economy;
    
    public void onEnable() {
        (Main.instance = this).copyConfig();
        this.initAttributes();
        this.setupEconomy();
        this.initFiles();
        this.registerListeners();
        this.getCommand("chunkloader").setExecutor((CommandExecutor)new ChunkLoaderCommand());
    }
    
    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents((Listener)new ChunkUnloadListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new BlockPlaceListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new BlockBreakListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new ExplosionListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new PlayerInteractListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new AsyncPlayerChatListener(), (Plugin)this);
    }
    
    private void initAttributes() {
        final Config cfg = new Config();
        this.usingVault = cfg.usingVault();
        this.price = cfg.getPrice();
        this.usingChunkLoader = cfg.usingChunkLoader();
        this.maximalChunks = cfg.getMaximalChunks();
        this.loadedTimeCommand = cfg.getLoadedTimeCommand();
        this.loadedTimeChunkLoader = cfg.getLoadedTimeChunkLoader();
        this.dropOnBreak = cfg.dropOnBreak();
        this.showParticles = cfg.showParticles();
        this.waitingChunks = new ArrayList<Chunk>();
        this.renaming = new HashMap<Player, ChunkLoader>();
    }
    
    private void initFiles() {
        this.chunkFile = new File("plugins/ChunkLoaderX/chunks.yml");
        if (!this.chunkFile.exists()) {
            try {
                this.chunkFile.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        (this.chunkConfig = YamlConfiguration.loadConfiguration(this.chunkFile)).addDefault("chunks", (Object)new ArrayList());
        this.chunkConfig.options().copyDefaults(true);
        this.saveChunkConfig();
        this.chunkLoaderFile = new File("plugins/ChunkLoaderX/chunkloader.yml");
        if (!this.chunkLoaderFile.exists()) {
            try {
                this.chunkLoaderFile.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        (this.chunkLoaderConfig = YamlConfiguration.loadConfiguration(this.chunkLoaderFile)).addDefault("chunkloaders", (Object)new ArrayList());
        this.chunkLoaderConfig.options().copyDefaults(true);
        this.saveChunkLoaderConfig();
        this.getLogger().log(Level.INFO, "Using economy: " + this.usingVault());
    }
    
    public List<Chunk> getWaitingChunks() {
        return this.waitingChunks;
    }
    
    public static Main getInstance() {
        return Main.instance;
    }
    
    public YamlConfiguration getChunkConfig() {
        return this.chunkConfig;
    }
    
    public void saveChunkConfig() {
        try {
            this.chunkConfig.save(this.chunkFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public YamlConfiguration getChunkLoaderConfig() {
        return this.chunkLoaderConfig;
    }
    
    public void saveChunkLoaderConfig() {
        try {
            this.chunkLoaderConfig.save(this.chunkLoaderFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public Map<Player, ChunkLoader> getRenaming() {
        return this.renaming;
    }
    
    private void setupEconomy() {
        try {
            final RegisteredServiceProvider<Economy> economyProvider = (RegisteredServiceProvider<Economy>)this.getServer().getServicesManager().getRegistration((Class)Economy.class);
            if (economyProvider != null) {
                this.economy = (Economy)economyProvider.getProvider();
                this.usingVault = true;
            }
            else {
                this.getLogger().warning("There is no Vault-supported economy-plugin installed, you won't be able to use the economy feature!");
                this.usingVault = false;
            }
        }
        catch (NoClassDefFoundError e) {
            this.getLogger().warning("There is no Vault-supported economy-plugin installed, you won't be able to use the economy feature!");
            this.usingVault = false;
        }
    }
    
    public boolean usingVault() {
        return this.usingVault;
    }
    
    public double getPrice() {
        return this.price;
    }
    
    public boolean usingChunkLoader() {
        return this.usingChunkLoader;
    }
    
    public int getMaximalChunksPerUser() {
        return this.maximalChunks;
    }
    
    public boolean infiniteLoadedTimeCommand() {
        return this.loadedTimeCommand == -1;
    }
    
    public int getLoadedTimeCommand() {
        return this.loadedTimeCommand;
    }
    
    public boolean infiniteLoadedTimeChunkLoader() {
        return this.loadedTimeChunkLoader == -1;
    }
    
    public int getLoadedTimeChunkLoader() {
        return this.loadedTimeChunkLoader;
    }
    
    public boolean dropOnBreak() {
        return this.dropOnBreak;
    }
    
    public boolean showParticles() {
        return this.showParticles;
    }
    
    public Economy getEconomy() {
        return this.economy;
    }
    
    private void copyConfig() {
        final File dir = new File("plugins/ChunkLoaderX");
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }
        if (new File("plugins/ChunkLoaderX/config.yml").exists()) {
            return;
        }
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = this.getResource("config.yml");
            outputStream = new FileOutputStream(new File("plugins/ChunkLoaderX/config.yml"));
            int read = 0;
            final byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                }
                catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
            return;
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                }
                catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
        if (inputStream != null) {
            try {
                inputStream.close();
            }
            catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        if (outputStream != null) {
            try {
                outputStream.flush();
                outputStream.close();
            }
            catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }
}
