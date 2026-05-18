package com.mtier.pvp.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PvPMainMenu {

    private final String title = "§6§lMTier PvP §8» §7Main Menu";

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, title);

        // Filler
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        if (fillerMeta != null) {
            fillerMeta.setDisplayName(" ");
            filler.setItemMeta(fillerMeta);
        }
        for (int i = 0; i < 27; i++) inv.setItem(i, filler);

        // 1. Random Matchmaking Button
        ItemStack queue = new ItemStack(Material.ENDER_EYE);
        ItemMeta qMeta = queue.getItemMeta();
        if (qMeta != null) {
            qMeta.setDisplayName("§e§lRandom Matchmaking");
            List<String> lore = new ArrayList<>();
            lore.add("§7Find an opponent with similar MMR.");
            lore.add("");
            lore.add("§6» Click to enter queue");
            qMeta.setLore(lore);
            queue.setItemMeta(qMeta);
        }
        inv.setItem(11, queue);

        // 2. Room Browser Button
        ItemStack browser = new ItemStack(Material.OAK_SIGN);
        ItemMeta bMeta = browser.getItemMeta();
        if (bMeta != null) {
            bMeta.setDisplayName("§b§lRoom Browser");
            List<String> lore = new ArrayList<>();
            lore.add("§7Browse or create custom 1v1 rooms.");
            lore.add("");
            lore.add("§6» Click to view rooms");
            bMeta.setLore(lore);
            browser.setItemMeta(bMeta);
        }
        inv.setItem(15, browser);

        player.openInventory(inv);
    }

    public String getTitle() { return title; }
}
