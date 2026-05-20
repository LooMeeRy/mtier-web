package com.mtier.pvp.gui;

import com.mtier.pvp.MTierPvP;
import com.mtier.pvp.manager.RoomManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BanCategoryMenu {

    private final String titleBan = "§8MTier PvP » §6Ban Categories";
    private final String titleSelect = "§8MTier PvP » §aSelect Categories";

    public void open(Player player) {
        RoomManager.DuelRoom room = MTierPvP.getInstance().getRoomManager().getPlayerRoom(player);
        String title = (room != null && room.getPhase() == RoomManager.Phase.SELECTION_PHASE) ? titleSelect : titleBan;
        Inventory inv = Bukkit.createInventory(null, 27, title);

        // Decoration
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);
        for (int i = 0; i < 27; i++) inv.setItem(i, glass);

        // Categories
        boolean isSelection = room != null && room.getPhase() == RoomManager.Phase.SELECTION_PHASE;
        String actionLore = isSelection ? "§7Click to browse items" : "§7Click to view weapon bans";

        inv.setItem(10, createItem(Material.DIAMOND_SWORD, "§6§lWEAPONS", actionLore));
        inv.setItem(11, createItem(Material.NETHERITE_PICKAXE, "§b§lTOOLS", isSelection ? "§7Click to browse items" : "§7Click to view tool bans"));
        inv.setItem(12, createItem(Material.DIAMOND_CHESTPLATE, "§9§lARMOR", isSelection ? "§7Click to browse items" : "§7Click to view armor bans"));
        inv.setItem(13, createItem(Material.GRASS_BLOCK, "§a§lBLOCKS", isSelection ? "§7Click to browse items" : "§7Click to view block bans"));
        inv.setItem(14, createItem(Material.REDSTONE, "§c§lREDSTONE", isSelection ? "§7Click to browse items" : "§7Click to view redstone bans"));
        inv.setItem(15, createItem(Material.POTION, "§d§lOTHERS", isSelection ? "§7Click to browse items" : "§7Click to view other bans"));

        // Skip / Ready Button
        if (!isSelection) {
            inv.setItem(22, createItem(Material.BARRIER, "§c§lSKIP TURN", "§7Click to skip your current ban turn"));
        } else {
            boolean isReady = room != null && (room.getOwner().getUniqueId().equals(player.getUniqueId()) ? room.isReadyOwnerSelection() : room.isReadyChallengerSelection());
            inv.setItem(22, createItem(isReady ? Material.LIME_WOOL : Material.RED_WOOL, 
                isReady ? "§a§lREADY! §7(Clicked)" : "§c§lNOT READY §7(Click to confirm)", 
                "§7Both players must be ready", "§7to start the match immediately."));
        }

        player.openInventory(inv);
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

    public String getTitle() { return titleBan; }
    public String getTitleSelect() { return titleSelect; }
}
