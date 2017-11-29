package com.enforcedmc.chunkloader.command;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.enforcedmc.chunkloader.ChunkLoader;
import com.enforcedmc.chunkloader.Main;
import com.enforcedmc.chunkloader.Manager;
import com.enforcedmc.chunkloader.ParticlePlayer;
import com.enforcedmc.chunkloader.item.ChunkLoaderItem;

public class ChunkLoaderCommand implements CommandExecutor
{
    private Manager m;
    
    public ChunkLoaderCommand() {
        this.m = Manager.getManager();
    }
    
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cYou are not a player!");
            return true;
        }
        final Player p = (Player)sender;
        if (args.length == 2 && args[0].equalsIgnoreCase("buy")) {
            if (!p.hasPermission("cl.buy")) {
                p.sendMessage("§cYou don't have permission.");
                return true;
            }
            final String name = args[1];
            if (Main.getInstance().usingVault()) {
                final double balance = Main.getInstance().getEconomy().getBalance((OfflinePlayer)p);
                if (balance < Main.getInstance().getPrice()) {
                    p.sendMessage("§cYou don't have enough money. (" + (balance - Main.getInstance().getPrice()) + Main.getInstance().getEconomy().currencyNamePlural() + ")");
                    return true;
                }
                if (this.m.isLoaded(p.getLocation().getChunk())) {
                    p.sendMessage("§cThis chunk is already being kept loaded.");
                    return true;
                }
                if (this.m.exists(p.getUniqueId(), name)) {
                    p.sendMessage("§cYou already own a chunk/ChunkLoader with that name.");
                    return true;
                }
                if (this.m.getAllLoadedChunks(p.getUniqueId()).size() >= Main.getInstance().getMaximalChunksPerUser()) {
                    p.sendMessage("§cYou already own too many chunks!");
                    return true;
                }
                final MCChunk chunk = new MCChunk(name, p.getUniqueId(), p.getLocation().getChunk());
                Main.getInstance().getEconomy().withdrawPlayer((OfflinePlayer)p, Main.getInstance().getPrice());
                this.m.addChunk(chunk);
                new ParticlePlayer(3).playParticles(p, chunk.getChunk());
                p.sendMessage("§2You successfully bought this chunk.");
                return true;
            }
            else {
                if (this.m.isLoaded(p.getLocation().getChunk())) {
                    p.sendMessage("§cThis chunk is already being kept loaded.");
                    return true;
                }
                if (this.m.exists(p.getUniqueId(), name)) {
                    p.sendMessage("§cYou already own a chunk/ChunkLoader with that name.");
                    return true;
                }
                if (this.m.getAllLoadedChunks(p.getUniqueId()).size() >= Main.getInstance().getMaximalChunksPerUser()) {
                    p.sendMessage("§cYou already own too many chunks!");
                    return true;
                }
                final MCChunk chunk2 = new MCChunk(name, p.getUniqueId(), p.getLocation().getChunk());
                this.m.addChunk(chunk2);
                p.sendMessage("§2You successfully bought this chunk.");
                return true;
            }
        }
        else {
            if (args.length == 1 && args[0].equalsIgnoreCase("chunkloader")) {
                if (!p.hasPermission("cl.chunkloader")) {
                    p.sendMessage("§cYou don't have permission.");
                    return true;
                }
                if (!Main.getInstance().usingChunkLoader()) {
                    p.sendMessage("§cYour server doesn't support this feature.");
                    return true;
                }
                if (Main.getInstance().usingVault()) {
                    final double balance2 = Main.getInstance().getEconomy().getBalance((OfflinePlayer)p);
                    if (balance2 >= Main.getInstance().getPrice()) {
                        Main.getInstance().getEconomy().withdrawPlayer((OfflinePlayer)p, Main.getInstance().getPrice());
                        p.getWorld().dropItem(p.getLocation(), ChunkLoaderItem.getItem());
                        p.sendMessage("§2You successfully bought a ChunkLoader.");
                        return true;
                    }
                    p.sendMessage("§cYou don't have enough money. (" + (balance2 - Main.getInstance().getPrice()) + Main.getInstance().getEconomy().currencyNamePlural() + ")");
                    return true;
                }
                else {
                    p.getWorld().dropItem(p.getLocation(), ChunkLoaderItem.getItem());
                    p.sendMessage("§2You successfully bought a ChunkLoader.");
                }
            }
            if (args.length == 1 && args[0].equalsIgnoreCase("remove")) {
                if (!p.hasPermission("cl.remove")) {
                    p.sendMessage("§cYou don't have permission.");
                    return true;
                }
                if (!this.m.ownsChunk(p.getUniqueId(), p.getLocation().getChunk())) {
                    p.sendMessage("§cYou don't own this chunk!");
                    return true;
                }
                final MCChunk c = this.m.getChunk(p.getLocation().getChunk());
                this.m.removeChunk(c);
                p.sendMessage("§2The chunk has been removed successfully.");
                return true;
            }
            else if (args.length == 3 && args[0].equalsIgnoreCase("remove")) {
                if (!p.hasPermission("cl.remove.others")) {
                    p.sendMessage("§cYou don't have permission.");
                    return true;
                }
                final String name = args[1];
                final String player = args[2];
                final UUID uuid = Bukkit.getOfflinePlayer(player).getUniqueId();
                if (uuid == null) {
                    p.sendMessage("§cThe player §f" + player + " §chas never played on this server before!");
                    return true;
                }
                if (!this.m.ownsChunk(name, uuid)) {
                    p.sendMessage("§cThe player §f" + player + " §cdoesn't own a chunk with that name.");
                    return true;
                }
                final MCChunk c2 = this.m.getChunk(uuid, name);
                this.m.removeChunk(c2);
                p.sendMessage("§2The chunk has been removed successfully.");
                return true;
            }
            else if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
                if (!p.hasPermission("cl.list")) {
                    p.sendMessage("§cYou don't have permission.");
                    return true;
                }
                if (this.m.getBoughtChunks(p.getUniqueId()).size() == 0) {
                    p.sendMessage("§cThere are no chunks which are being kept loaded for you.");
                }
                else {
                    p.sendMessage("§2These chunks are being kept loaded for you:");
                    for (final MCChunk c : this.m.getBoughtChunks(p.getUniqueId())) {
                        p.sendMessage("  §2- §f[" + c.getName() + ", " + c.getChunk().getWorld().getName() + ", " + c.getChunk().getX() * 16 + ", " + c.getChunk().getZ() * 16 + "]");
                        new ParticlePlayer(10).playParticles(p, c.getChunk());
                    }
                }
                if (this.m.getChunkLoaders(p.getUniqueId()).size() == 0) {
                    p.sendMessage("§cYou don't own any ChunkLoaders.");
                }
                else {
                    p.sendMessage("§2You own these ChunkLoaders:");
                    for (final ChunkLoader cl : this.m.getChunkLoaders(p.getUniqueId())) {
                        final Chunk c3 = cl.getLocation().getChunk();
                        p.sendMessage("  §2- §f[" + cl.getName() + ", " + c3.getWorld().getName() + ", " + c3.getX() * 16 + ", " + c3.getZ() * 16 + "]");
                        new ParticlePlayer(10).playParticles(p, c3);
                    }
                }
                return true;
            }
            else if (args.length == 2 && args[0].equalsIgnoreCase("list")) {
                if (!p.hasPermission("cl.list.others")) {
                    p.sendMessage("§cYou don't have permission.");
                    return true;
                }
                final String player2 = args[1];
                final UUID uuid2 = Bukkit.getOfflinePlayer(player2).getUniqueId();
                if (uuid2 == null) {
                    p.sendMessage("§cThe player §f" + player2 + " §chas never played on this server before!");
                    return true;
                }
                if (this.m.getBoughtChunks(uuid2).size() == 0) {
                    p.sendMessage("§2There are no chunks which are being kept loaded for §f" + player2 + "§2.");
                }
                else {
                    p.sendMessage("§2These chunks are being kept loaded for §f" + player2 + "§2:");
                    for (final MCChunk c4 : this.m.getBoughtChunks(uuid2)) {
                        p.sendMessage("  §2- §f[" + c4.getName() + ", " + c4.getChunk().getWorld().getName() + ", " + c4.getChunk().getX() * 16 + ", " + c4.getChunk().getZ() * 16 + "]");
                        new ParticlePlayer(10).playParticles(p, c4.getChunk());
                    }
                }
                if (this.m.getChunkLoaders(uuid2).size() == 0) {
                    p.sendMessage(String.valueOf(player2) + " §2doesn't own any ChunkLoaders.");
                }
                else {
                    p.sendMessage(String.valueOf(player2) + " §2owns these ChunkLoaders:");
                    for (final ChunkLoader cl2 : this.m.getChunkLoaders(uuid2)) {
                        final Chunk c5 = cl2.getLocation().getChunk();
                        p.sendMessage("  §2- §f[" + cl2.getName() + ", " + c5.getWorld().getName() + ", " + c5.getX() * 16 + ", " + c5.getZ() * 16 + "]");
                        new ParticlePlayer(10).playParticles(p, c5);
                    }
                }
                return true;
            }
            else if (args.length == 2 && args[0].equalsIgnoreCase("rename")) {
                if (!p.hasPermission("cl.rename")) {
                    p.sendMessage("§cYou don't have permission.");
                    return true;
                }
                final String newName = args[1];
                if (!this.m.isLoaded(p.getLocation().getChunk())) {
                    p.sendMessage("§cYou don't own this chunk.");
                    return true;
                }
                final MCChunk chunk2 = this.m.getChunk(p.getLocation().getChunk());
                if (!chunk2.getOwner().equals(p.getUniqueId())) {
                    p.sendMessage("§cYou don't own this chunk.");
                    return true;
                }
                if (chunk2.getName().equals(newName)) {
                    p.sendMessage("§cThat chunk already has that name.");
                    return true;
                }
                this.m.changeNameOfChunk(chunk2, newName);
                p.sendMessage("§2This chunk has been renamed to §f" + newName + "§2.");
                return true;
            }
            else if (args.length == 3 && args[0].equalsIgnoreCase("rename")) {
                if (!p.hasPermission("cl.rename")) {
                    p.sendMessage("§cYou don't have permission.");
                    return true;
                }
                final String oldName = args[1];
                final String newName2 = args[2];
                if (!this.m.ownsChunk(oldName, p.getUniqueId())) {
                    p.sendMessage("§cYou don't own a chunk with that name.");
                    return true;
                }
                if (oldName.equals(newName2)) {
                    p.sendMessage("§cThat chunk already has that name.");
                    return true;
                }
                final MCChunk c4 = this.m.getChunk(p.getUniqueId(), oldName);
                if (!c4.getOwner().equals(p.getUniqueId())) {
                    p.sendMessage("§cYou don't own this chunk.");
                    return true;
                }
                this.m.changeNameOfChunk(c4, newName2);
                p.sendMessage("§2This chunk has been renamed to §f" + newName2 + "§2.");
                return true;
            }
            else if (args.length == 4 && args[0].equalsIgnoreCase("rename")) {
                if (!p.hasPermission("cl.rename.others")) {
                    p.sendMessage("§cYou don't have permission.");
                    return true;
                }
                final String player2 = args[3];
                final UUID uuid2 = Bukkit.getOfflinePlayer(player2).getUniqueId();
                if (uuid2 == null) {
                    p.sendMessage("§cThe player §f" + player2 + " §chas never played on this server before!");
                    return true;
                }
                final String oldName2 = args[1];
                final String newName3 = args[2];
                if (!this.m.ownsChunk(oldName2, uuid2)) {
                    p.sendMessage("§cThat player doesn't own a chunk with that name.");
                    return true;
                }
                if (oldName2.equals(newName3)) {
                    p.sendMessage("§cThat chunk already has that name.");
                    return true;
                }
                final MCChunk c6 = this.m.getChunk(uuid2, oldName2);
                this.m.changeNameOfChunk(c6, newName3);
                p.sendMessage("§2This chunk has been renamed to §f" + newName3 + "§2.");
                return true;
            }
            else {
                if (args.length != 2 || !args[0].equalsIgnoreCase("teleport")) {
                    if (args.length == 1) {
                        if (args[0].equalsIgnoreCase("help")) {
                            if (!p.hasPermission("cl.help")) {
                                p.sendMessage("§cYou don't have permission.");
                                return true;
                            }
                            p.sendMessage("/cl buy <name> §2- Buy the chunk you are currently in.");
                            p.sendMessage("/cl chunkloader §2- Buy a ChunkLoader.");
                            p.sendMessage("/cl remove §2- Remove the chunk you are currently in.");
                            p.sendMessage("/cl remove <name> [player] §2- Remove the chunk with this name.");
                            p.sendMessage("/cl list [player] §2- List the chunks and ChunkLoaders a player owns.");
                            p.sendMessage("/cl rename [oldName] <newName> §2- Rename a chunk.");
                            p.sendMessage("/cl teleport <name> §2- Teleport to this chunk/ChunkLoader.");
                            p.sendMessage("/cl border §2- Show the borders of the chunk you are in.");
                            return true;
                        }
                        else if (args.length == 1 && args[0].equalsIgnoreCase("border")) {
                            if (!p.hasPermission("cl.border")) {
                                p.sendMessage("§cYou don't have permission.");
                                return true;
                            }
                            new ParticlePlayer(5).playParticles(p, p.getLocation().getChunk());
                            return true;
                        }
                    }
                    return true;
                }
                if (!p.hasPermission("cl.teleport")) {
                    p.sendMessage("§cYou don't have permission.");
                    return true;
                }
                final String name = args[1];
                if (!this.m.exists(p.getUniqueId(), name)) {
                    p.sendMessage("§cYou don't own a ChunkLoader/chunk with that name.");
                    return true;
                }
                Location toTeleport;
                if (this.m.existsChunkLoader(p.getUniqueId(), name)) {
                    toTeleport = this.m.getChunkLoader(p.getUniqueId(), name).getLocation();
                    toTeleport = toTeleport.getWorld().getHighestBlockAt(toTeleport).getLocation();
                    toTeleport.setY(toTeleport.getY() + 1.0);
                }
                else {
                    final Chunk c3 = this.m.getChunk(p.getUniqueId(), name).getChunk();
                    final int x = c3.getX() * 16;
                    final int y = 50;
                    final int z = c3.getZ() * 16;
                    final Location loc = new Location(c3.getWorld(), (double)x, (double)y, (double)z);
                    toTeleport = loc.getWorld().getHighestBlockAt(loc).getLocation();
                    toTeleport.setY(toTeleport.getY() + 1.0);
                }
                p.teleport(toTeleport);
                p.sendMessage("§2You have been teleported.");
                return true;
            }
        }
    }
}
