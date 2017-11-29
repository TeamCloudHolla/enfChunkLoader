package com.enforcedmc.chunkloader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

public class Manager
{
    private static Manager m;
    
    static {
        Manager.m = new Manager();
    }
    
    public static Manager getManager() {
        return Manager.m;
    }
    
    public List<MCChunk> getBoughtChunks() {
        final List<String> list = (List<String>)Main.getInstance().getChunkConfig().getStringList("chunks");
        final List<MCChunk> chunks = new ArrayList<MCChunk>();
        for (final String s : list) {
            final String[] split = s.split(";");
            try {
                chunks.add(new MCChunk(split[4], UUID.fromString(split[0]), new Location(Bukkit.getWorld(split[1]), (double)Integer.parseInt(split[2]), 128.0, (double)Integer.parseInt(split[3])).getChunk()));
            }
            catch (NumberFormatException e) {
                Main.getInstance().getLogger().severe("Error in chunks.yml [ERROR #101]");
                Main.getInstance().getLogger().severe("Disabling plugin.");
                Bukkit.getPluginManager().disablePlugin((Plugin)Main.getInstance());
                return null;
            }
        }
        return chunks;
    }
    
    public List<ChunkLoader> getChunkLoaders() {
        final List<ChunkLoader> list = new ArrayList<ChunkLoader>();
        for (final String s : Main.getInstance().getChunkLoaderConfig().getStringList("chunkloaders")) {
            final String[] split = s.split(";");
            list.add(new ChunkLoader(split[5], UUID.fromString(split[0]), new Location(Bukkit.getWorld(split[1]), (double)Integer.parseInt(split[2]), (double)Integer.parseInt(split[3]), (double)Integer.parseInt(split[4]))));
        }
        return list;
    }
    
    public List<MCChunk> getBoughtChunks(final UUID uuid) {
        final List<MCChunk> chunks = new ArrayList<MCChunk>();
        for (final MCChunk c : this.getBoughtChunks()) {
            if (c.getOwner().equals(uuid)) {
                chunks.add(c);
            }
        }
        return chunks;
    }
    
    public List<ChunkLoader> getChunkLoaders(final UUID uuid) {
        final List<ChunkLoader> list = new ArrayList<ChunkLoader>();
        for (final ChunkLoader c : this.getChunkLoaders()) {
            if (c.getOwner().equals(uuid)) {
                list.add(c);
            }
        }
        return list;
    }
    
    public MCChunk getChunk(final Chunk c) {
        for (final MCChunk chunk : this.getBoughtChunks()) {
            if (chunk.getChunk().equals(c)) {
                return chunk;
            }
        }
        return null;
    }
    
    public MCChunk getChunk(final UUID uuid, final String name) {
        for (final MCChunk chunk : this.getBoughtChunks(uuid)) {
            if (chunk.getName().equalsIgnoreCase(name)) {
                return chunk;
            }
        }
        return null;
    }
    
    public ChunkLoader getChunkLoader(final Location loc) {
        for (final ChunkLoader cl : this.getChunkLoaders()) {
            if (cl.getLocation().equals((Object)loc)) {
                return cl;
            }
        }
        return null;
    }
    
    public ChunkLoader getChunkLoader(final Chunk chunk) {
        for (final ChunkLoader cl : this.getChunkLoaders()) {
            if (cl.getLocation().getChunk().equals(chunk)) {
                return cl;
            }
        }
        return null;
    }
    
    public ChunkLoader getChunkLoader(final UUID uuid, final String name) {
        if (!this.existsChunkLoader(uuid, name)) {
            return null;
        }
        for (final ChunkLoader cl : this.getChunkLoaders(uuid)) {
            if (cl.getName().equalsIgnoreCase(name)) {
                return cl;
            }
        }
        return null;
    }
    
    private List<Chunk> getAllLoadedChunks() {
        final List<Chunk> chunks = new ArrayList<Chunk>();
        for (final MCChunk c : this.getBoughtChunks()) {
            chunks.add(c.getChunk());
        }
        for (final ChunkLoader c2 : this.getChunkLoaders()) {
            chunks.add(c2.getLocation().getChunk());
        }
        return chunks;
    }
    
    public List<Chunk> getAllLoadedChunks(final UUID uuid) {
        final List<Chunk> chunks = new ArrayList<Chunk>();
        for (final MCChunk c : this.getBoughtChunks()) {
            if (c.getOwner().equals(uuid)) {
                chunks.add(c.getChunk());
            }
        }
        for (final ChunkLoader c2 : this.getChunkLoaders()) {
            if (c2.getOwner().equals(uuid)) {
                chunks.add(c2.getLocation().getChunk());
            }
        }
        return chunks;
    }
    
    public boolean isLoaded(final Chunk chunk) {
        for (final Chunk c : this.getAllLoadedChunks()) {
            if (c.equals(chunk)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean exists(final UUID uuid, final String name) {
        for (final ChunkLoader cl : this.getChunkLoaders(uuid)) {
            if (cl.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        for (final MCChunk c : this.getBoughtChunks(uuid)) {
            if (c.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean existsChunkLoader(final ChunkLoader cl) {
        for (final ChunkLoader chunkLoader : this.getChunkLoaders()) {
            if (chunkLoader.getName().equalsIgnoreCase(cl.getName()) && chunkLoader.getOwner().equals(cl.getOwner()) && chunkLoader.getLocation().equals((Object)cl.getLocation())) {
                return true;
            }
        }
        return false;
    }
    
    public boolean existsChunkLoader(final Location loc) {
        for (final ChunkLoader cl : this.getChunkLoaders()) {
            if (cl.getLocation().equals((Object)loc)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean existsChunkLoader(final Chunk c) {
        for (final ChunkLoader cl : this.getChunkLoaders()) {
            if (cl.getLocation().getChunk().equals(c)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean existsChunkLoader(final UUID uuid, final String name) {
        for (final ChunkLoader cl : this.getChunkLoaders(uuid)) {
            if (cl.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean ownsChunk(final UUID uuid, final Chunk chunk) {
        if (!this.isLoaded(chunk)) {
            return false;
        }
        for (final MCChunk c : this.getBoughtChunks(uuid)) {
            if (c.getChunk().equals(chunk)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean ownsChunk(final String name, final UUID uuid) {
        for (final MCChunk c : this.getBoughtChunks(uuid)) {
            if (c.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
    
    public void addChunk(final MCChunk chunk) {
        if (this.isLoaded(chunk.getChunk())) {
            return;
        }
        if (this.exists(chunk.getOwner(), chunk.getName())) {
            return;
        }
        final List<String> list = (List<String>)Main.getInstance().getChunkConfig().getStringList("chunks");
        final String world = chunk.getChunk().getWorld().getName();
        final int x = chunk.getChunk().getX() * 16;
        final int z = chunk.getChunk().getZ() * 16;
        list.add(String.valueOf(chunk.getOwner().toString()) + ";" + world + ";" + x + ";" + z + ";" + chunk.getName());
        Main.getInstance().getChunkConfig().set("chunks", (Object)list);
        Main.getInstance().saveChunkConfig();
    }
    
    public void addChunkLoader(final ChunkLoader cl) {
        if (this.existsChunkLoader(cl)) {
            return;
        }
        if (this.exists(cl.getOwner(), cl.getName())) {
            return;
        }
        final List<String> list = (List<String>)Main.getInstance().getChunkLoaderConfig().getStringList("chunkloaders");
        list.add(String.valueOf(cl.getOwner().toString()) + ";" + cl.getLocation().getWorld().getName() + ";" + cl.getLocation().getBlockX() + ";" + cl.getLocation().getBlockY() + ";" + cl.getLocation().getBlockZ() + ";" + cl.getName());
        Main.getInstance().getChunkLoaderConfig().set("chunkloaders", (Object)list);
        Main.getInstance().saveChunkLoaderConfig();
    }
    
    public void removeChunk(final MCChunk chunk) {
        final List<String> list = (List<String>)Main.getInstance().getChunkConfig().getStringList("chunks");
        final String world = chunk.getChunk().getWorld().getName();
        final int x = chunk.getChunk().getX() * 16;
        final int z = chunk.getChunk().getZ() * 16;
        list.remove(String.valueOf(chunk.getOwner().toString()) + ";" + world + ";" + x + ";" + z + ";" + chunk.getName());
        Main.getInstance().getChunkConfig().set("chunks", (Object)list);
        Main.getInstance().saveChunkConfig();
    }
    
    public void removeChunkLoader(final ChunkLoader cl) {
        if (!this.existsChunkLoader(cl)) {
            return;
        }
        final List<String> list = (List<String>)Main.getInstance().getChunkLoaderConfig().getStringList("chunkloaders");
        list.remove(String.valueOf(cl.getOwner().toString()) + ";" + cl.getLocation().getWorld().getName() + ";" + cl.getLocation().getBlockX() + ";" + cl.getLocation().getBlockY() + ";" + cl.getLocation().getBlockZ() + ";" + cl.getName());
        Main.getInstance().getChunkLoaderConfig().set("chunkloaders", (Object)list);
        Main.getInstance().saveChunkLoaderConfig();
    }
    
    public void changeNameOfChunk(final MCChunk c, final String newName) {
        this.removeChunk(c);
        c.setName(newName);
        this.addChunk(c);
    }
    
    public void changeNameOfChunkLoader(final ChunkLoader cl, final String newName) {
        this.removeChunkLoader(cl);
        cl.setName(newName);
        this.addChunkLoader(cl);
    }
    
    public String getRandomName(final UUID uuid) {
        final List<String> names = new ArrayList<String>();
        for (final ChunkLoader cl : this.getChunkLoaders(uuid)) {
            names.add(cl.getName());
        }
        for (final MCChunk c : this.getBoughtChunks(uuid)) {
            names.add(c.getName());
        }
        for (int i = 0; i < 5000; ++i) {
            if (!names.contains("chunk-" + i)) {
                return "chunk-" + i;
            }
        }
        return "chunk-" + new Random().nextInt(500000);
    }
}
