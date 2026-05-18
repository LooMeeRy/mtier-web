package com.mtier.pvp.gui;

import com.mtier.pvp.MTierPvP;
import com.mtier.pvp.manager.QueueManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class QueueMenu {

    private final String title = "§6§lMTier PvP §8» §7Matchmaking";

    public void open(Player player, boolean isSearching) {
        Inventory inv = Bukkit.createInventory(null, 27, title);

        // Filler
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        if (fillerMeta != null) {
            fillerMeta.setDisplayName(" ");
            filler.setItemMeta(fillerMeta);
        }
        for (int i = 0; i < 27; i++) inv.setItem(i, filler);

        // 1. Player Head
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        if (headMeta != null) {
            headMeta.setOwningPlayer(player);
            headMeta.setDisplayName("§e§l" + player.getName());
            head.setItemMeta(headMeta);
        }
        inv.setItem(4, head);

        // 2. Search Status (Wool)
        ItemStack wool = new ItemStack(isSearching ? Material.YELLOW_WOOL : Material.RED_WOOL);
        ItemMeta woolMeta = wool.getItemMeta();
        if (woolMeta != null) {
            woolMeta.setDisplayName(isSearching ? "§e§lSEARCHING..." : "§c§lNOT SEARCHING");
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(isSearching ? "§7Click to §cCANCEL §7matchmaking." : "§7Click to §aSTART §7matchmaking.");
            woolMeta.setLore(lore);
            wool.setItemMeta(woolMeta);
        }
        inv.setItem(13, wool);

        // 3. Back Button
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName("§7« Back to Menu");
            back.setItemMeta(backMeta);
        }
        inv.setItem(18, back);

        player.openInventory(inv);
    }

    public String getTitle() { return title; }
}
