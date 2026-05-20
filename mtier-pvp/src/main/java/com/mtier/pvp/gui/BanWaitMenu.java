package com.mtier.pvp.gui;

import com.mtier.pvp.MTierPvP;
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
import java.util.UUID;

public class BanWaitMenu {

    private final String title = "§8MTier PvP » §6Combat Bans";

    public void open(Player player, RoomManager.DuelRoom room) {
        Inventory inv = Bukkit.createInventory(null, 54, title);

        // Decoration
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);
        for (int i = 0; i < 54; i++) inv.setItem(i, glass);

        // Header
        inv.setItem(4, createItem(Material.DIAMOND_SWORD, "§6§lCURRENT BANS", "§7Review what items are prohibited."));

        // Split Layout: Left side for Owner, Right side for Challenger
        Player owner = room.getOwner();
        Player challenger = room.getChallenger();

        // Heads at the top of each section
        inv.setItem(11, getPlayerHead(owner));
        if (challenger != null) inv.setItem(15, getPlayerHead(challenger));

        // Sorting bans into left/right side
        List<RoomManager.BanEntry> ownerBans = new ArrayList<>();
        List<RoomManager.BanEntry> challengerBans = new ArrayList<>();

        for (RoomManager.BanEntry entry : room.getBanHistory()) {
            if (entry.player().equals(owner.getUniqueId())) {
                ownerBans.add(entry);
            } else if (challenger != null && entry.player().equals(challenger.getUniqueId())) {
                challengerBans.add(entry);
            }
        }

        // Fill Owner Bans (Slots 19, 20, 21, 28, 29, 30, 37, 38, 39)
        int[] ownerSlots = {19, 20, 21, 28, 29, 30, 37, 38, 39};
        for (int i = 0; i < ownerBans.size() && i < ownerSlots.length; i++) {
            inv.setItem(ownerSlots[i], getBanItem(ownerBans.get(i)));
        }

        // Fill Challenger Bans (Slots 23, 24, 25, 32, 33, 34, 41, 42, 43)
        int[] challengerSlots = {23, 24, 25, 32, 33, 34, 41, 42, 43};
        for (int i = 0; i < challengerBans.size() && i < challengerSlots.length; i++) {
            inv.setItem(challengerSlots[i], getBanItem(challengerBans.get(i)));
        }

        // Center line
        for (int i = 13; i < 54; i += 9) {
            inv.setItem(i, createItem(Material.IRON_BARS, "§8Separator", ""));
        }

        // Back button
        inv.setItem(49, createItem(Material.ARROW, "§eBack to Categories", "§7Click to open selection menu"));

        player.openInventory(inv);
    }

    private ItemStack getPlayerHead(Player p) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(p);
        meta.setDisplayName("§e§l" + p.getName());
        List<String> lore = new ArrayList<>();
        lore.add("§7Reviewing bans for this warrior.");
        meta.setLore(lore);
        head.setItemMeta(meta);
        return head;
    }

    private ItemStack getBanItem(RoomManager.BanEntry entry) {
        if (entry.material() == null) {
            return createItem(Material.BARRIER, "§c§lSKIPPED TURN", "§7This player decided not to", "§7ban anything this turn.");
        }
        ItemStack item = new ItemStack(entry.material());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§c§lBANNED: §f" + formatName(entry.material().name()));
        List<String> lore = new ArrayList<>();
        lore.add("§7This item was removed from the match.");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private String formatName(String name) {
        String[] split = name.toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (String s : split) sb.append(s.substring(0, 1).toUpperCase()).append(s.substring(1)).append(" ");
        return sb.toString().trim();
    }

    private ItemStack createItem(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        List<String> list = new ArrayList<>();
        for (String s : lore) list.add(s);
        meta.setLore(list);
        item.setItemMeta(meta);
        return item;
    }

    public String getTitle() { return title; }
}
