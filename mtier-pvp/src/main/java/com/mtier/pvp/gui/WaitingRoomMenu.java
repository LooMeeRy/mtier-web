package com.mtier.pvp.gui;

import com.mtier.plugin.MTierPlugin;
import com.mtier.plugin.api.WebSyncManager;
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

public class WaitingRoomMenu {

    private final String title = "§6§lPvP Duel §8» §7Waiting Room";
    
    // Frame slots around slot 10 and 16
    private final int[] frameA = {0, 1, 2, 9, 11, 18, 19, 20};
    private final int[] frameB = {6, 7, 8, 15, 17, 24, 25, 26};

    public void open(Player player, Player playerA, Player playerB, boolean readyA, boolean readyB) {
        Inventory inv = Bukkit.createInventory(null, 45, title);

        // Background filler
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        for (int i = 0; i < 45; i++) inv.setItem(i, filler);

        // Player A Side
        setupSide(inv, playerA, readyA, 10, frameA);
        
        // Player B Side
        setupSide(inv, playerB, readyB, 16, frameB);

        // Buttons
        boolean isA = player.getUniqueId().equals(playerA.getUniqueId());
        boolean selfReady = isA ? readyA : readyB;

        // Ready/Cancel Ready Button
        ItemStack readyBtn = new ItemStack(selfReady ? Material.YELLOW_WOOL : Material.GREEN_WOOL);
        ItemMeta readyMeta = readyBtn.getItemMeta();
        if (readyMeta != null) {
            readyMeta.setDisplayName(selfReady ? "§e§lUNREADY" : "§a§lREADY TO FIGHT");
            readyBtn.setItemMeta(readyMeta);
        }
        inv.setItem(30, readyBtn);

        // Cancel Match Button
        ItemStack cancelBtn = new ItemStack(Material.RED_WOOL);
        ItemMeta cancelMeta = cancelBtn.getItemMeta();
        if (cancelMeta != null) {
            cancelMeta.setDisplayName("§c§lLEAVE MATCH");
            cancelBtn.setItemMeta(cancelMeta);
        }
        inv.setItem(32, cancelBtn);

        player.openInventory(inv);
    }

    private void setupSide(Inventory inv, Player p, boolean ready, int headSlot, int[] frame) {
        if (p == null) {
            // Placeholder when no player is in the slot
            ItemStack waiting = new ItemStack(Material.BARRIER);
            ItemMeta meta = waiting.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§8Waiting for opponent...");
                waiting.setItemMeta(meta);
            }
            inv.setItem(headSlot, waiting);
            
            // Red frame for empty side
            ItemStack frameItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta frameMeta = frameItem.getItemMeta();
            if (frameMeta != null) {
                frameMeta.setDisplayName(" ");
                frameItem.setItemMeta(frameMeta);
            }
            for (int slot : frame) inv.setItem(slot, frameItem);
            return;
        }

        // Head
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(p);
            meta.setDisplayName("§e§l" + p.getName());
            
            // Get MMR/Rank from Core Cache
            WebSyncManager.PlayerData data = MTierPlugin.getAPI().getCachedPlayerData(p.getUniqueId());
            List<String> lore = new ArrayList<>();
            if (data != null && data.stats().containsKey("PvP")) {
                lore.add("§7MMR: §f" + data.stats().get("PvP").mmr());
                lore.add("§7Rank: §6" + data.stats().get("PvP").rank());
            } else {
                lore.add("§7MMR: §f1000");
                lore.add("§7Rank: §fIron");
            }
            meta.setLore(lore);
            head.setItemMeta(meta);
        }
        inv.setItem(headSlot, head);

        // Frame
        ItemStack wool = new ItemStack(ready ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);
        ItemMeta woolMeta = wool.getItemMeta();
        if (woolMeta != null) {
            woolMeta.setDisplayName(" ");
            wool.setItemMeta(woolMeta);
        }
        for (int slot : frame) inv.setItem(slot, wool);
    }

    public String getTitle() { return title; }
}
