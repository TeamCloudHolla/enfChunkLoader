package com.enforcedmc.chunkloader.item;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ChunkLoaderItem
{
    public static ItemStack getItem() {
        final ItemStack item = new ItemStack(Material.GOLD_BLOCK);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6ChunkLoader");
        meta.setLore((List)Arrays.asList("§2This block keeps the chunk it's in loaded."));
        item.setItemMeta(meta);
        return item;
    }
}
