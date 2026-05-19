package com.mtier.pvp.gui;

import com.mtier.plugin.MTierPlugin;
import com.mtier.plugin.api.WebSyncManager;
import com.mtier.pvp.manager.RoomManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class RoomBrowserMenu {

    private final String title = "§6§lMTier PvP §8» §7Room Browser";

    public void open(Player player, List<RoomManager.DuelRoom> rooms, int page) {
        Inventory inv = Bukkit.createInventory(null, 54, title + " (Page " + (page + 1) + ")");

        // Filler
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        for (int i = 0; i < 54; i++) inv.setItem(i, filler);

        // Rooms (45 slots for rooms, last row for buttons)
        int startIdx = page * 45;
        for (int i = 0; i < 45 && (startIdx + i) < rooms.size(); i++) {
            RoomManager.DuelRoom room = rooms.get(startIdx + i);
            Player owner = room.getOwner();

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            if (meta != null) {
                meta.setOwningPlayer(owner);
                meta.setDisplayName("§e§l" + owner.getName() + "'s Room");
                
                WebSyncManager.PlayerData data = MTierPlugin.getAPI().getCachedPlayerData(owner.getUniqueId());
                List<String> lore = new ArrayList<>();
                if (data != null && data.stats().containsKey("PvP")) {
                    lore.add("§7MMR: §f" + data.stats().get("PvP").mmr());
                    lore.add("§7Rank: §6" + data.stats().get("PvP").rank());
                } else {
                    lore.add("§7MMR: §f0");
                    lore.add("§7Rank: §8Wood");
                }
                lore.add("");
                lore.add(room.isFull() ? "§c§lFULL" : "§a§lCLICK TO JOIN");
                meta.setLore(lore);
                head.setItemMeta(meta);
            }
            inv.setItem(i, head);
        }

        // Navigation & Create & Refresh
        if (page > 0) inv.setItem(45, createItem(Material.ARROW, "§7« Previous Page"));
        if ((page + 1) * 45 < rooms.size()) inv.setItem(53, createItem(Material.ARROW, "§7Next Page »"));

        inv.setItem(49, createItem(Material.NETHER_STAR, "§a§lCREATE ROOM"));
        inv.setItem(50, createItem(Material.SUNFLOWER, "§e§lREFRESH LIST"));
        inv.setItem(48, createItem(Material.BARRIER, "§c« Back to Menu"));

        player.openInventory(inv);
    }

    private ItemStack createItem(Material m, String name) {
        ItemStack item = new ItemStack(m);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    public String getTitle() { return title; }
}
