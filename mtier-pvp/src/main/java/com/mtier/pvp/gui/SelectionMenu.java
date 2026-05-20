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
import java.util.Set;
import java.util.stream.Collectors;

public class SelectionMenu {

    private final String titlePrefix = "§8Select » §6";

    public void open(Player player, String category, int page, String searchQuery) {
        String title = titlePrefix + category + (searchQuery != null ? " (" + searchQuery + ")" : "");
        Inventory inv = Bukkit.createInventory(null, 54, title);

        List<Material> allItems = getItemsForCategory(category);
        if (searchQuery != null) {
            String finalSearchQuery = searchQuery.toLowerCase();
            allItems = allItems.stream()
                .filter(m -> m.name().toLowerCase().contains(finalSearchQuery))
                .collect(Collectors.toList());
        }

        RoomManager.DuelRoom room = MTierPvP.getInstance().getRoomManager().getPlayerRoom(player);
        Set<Material> banned = room != null ? room.getBannedItems() : java.util.Collections.emptySet();

        int start = page * 45;
        for (int i = 0; i < 45 && (start + i) < allItems.size(); i++) {
            Material mat = allItems.get(start + i);
            if (banned.contains(mat)) {
                inv.setItem(i, createBannedPlaceholder(mat));
            } else {
                inv.setItem(i, createSelectionItem(mat));
            }
        }

        // Navigation & Bottom Row
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);
        for (int i = 45; i < 54; i++) inv.setItem(i, glass);

        if (page > 0) inv.setItem(45, createItem(Material.ARROW, "§e« Previous Page", "§7Go to page " + page));
        if ((page + 1) * 45 < allItems.size()) inv.setItem(53, createItem(Material.ARROW, "§eNext Page »", "§7Go to page " + (page + 2)));

        inv.setItem(48, createItem(Material.DARK_OAK_DOOR, "§cBack to Categories", "§7Return to main selection"));
        inv.setItem(49, createItem(Material.COMPASS, "§bSearch Items", "§7Click then type in chat to search"));

        // Ready Button
        boolean isReady = room != null && (room.getOwner().getUniqueId().equals(player.getUniqueId()) ? room.isReadyOwnerSelection() : room.isReadyChallengerSelection());
        inv.setItem(50, createItem(isReady ? Material.LIME_WOOL : Material.RED_WOOL, 
            isReady ? "§a§lREADY! §7(Clicked)" : "§c§lNOT READY §7(Click to confirm)", 
            "§7Both players must be ready", "§7to start the match immediately."));

        player.openInventory(inv);
    }

    private ItemStack createSelectionItem(Material mat) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§a§lSELECT: §f" + formatName(mat.name()));
        List<String> lore = new ArrayList<>();
        lore.add("§7Click to add this item to your inventory.");
        lore.add("");
        lore.add("§e§lLEFT-CLICK §7for a full stack");
        lore.add("§6§lRIGHT-CLICK §7for 1 item");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createBannedPlaceholder(Material mat) {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§c§lBANNED: §f" + formatName(mat.name()));
        List<String> lore = new ArrayList<>();
        lore.add("§7This item was banned during the ban phase");
        lore.add("§7and cannot be used in this match.");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
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

    private List<Material> getItemsForCategory(String cat) {
        // Reuse logic from BanItemMenu (could be refactored into a helper later)
        List<Material> list = new ArrayList<>();
        String upperCat = cat.toUpperCase();
        for (Material m : Material.values()) {
            if (!m.isItem() || m.isAir() || m.name().startsWith("LEGACY_")) continue;
            String n = m.name();
            if (n.contains("COMMAND_BLOCK") || n.contains("STRUCTURE_BLOCK") || n == "JIGSAW" || n == "DEBUG_STICK") continue;
            if (n.endsWith("_SPAWN_EGG")) continue;

            boolean isWeapon = n.contains("SWORD") || n.contains("AXE") || n.contains("BOW") || n.contains("CROSSBOW") || n.contains("TRIDENT") || n.contains("MACE");
            if (n.contains("WAXED")) {
                if (!n.contains("SWORD") && !n.contains("BOW") && !n.contains("CROSSBOW")) isWeapon = false;
            }
            boolean isTool = n.contains("PICKAXE") || n.contains("SHOVEL") || n.contains("HOE") || n.contains("SHEARS") || m == Material.FISHING_ROD || m == Material.FLINT_AND_STEEL || m == Material.BRUSH;
            boolean isArmor = n.contains("HELMET") || n.contains("CHESTPLATE") || n.contains("LEGGINGS") || n.contains("BOOTS") || n.contains("HORSE_ARMOR") || m == Material.ELYTRA || m == Material.SHIELD;
            boolean isRedstone = n.contains("REDSTONE") || n.contains("PISTON") || m == Material.TNT || m == Material.REPEATER || m == Material.COMPARATOR || m == Material.DISPENSER || m == Material.DROPPER || m == Material.OBSERVER || m == Material.HOPPER || m == Material.FIRE_CHARGE || m == Material.TARGET || m == Material.LECTERN || m == Material.DAYLIGHT_DETECTOR;
            boolean isBlock = m.isBlock();

            switch (upperCat) {
                case "WEAPONS": if (isWeapon && !n.contains("COPPER")) list.add(m); break;
                case "TOOLS": if (isTool) list.add(m); break;
                case "ARMOR": if (isArmor) list.add(m); break;
                case "BLOCKS": if (isBlock && !isWeapon && !isTool && !isArmor && !isRedstone) list.add(m); break;
                case "REDSTONE": if (isRedstone) list.add(m); break;
                case "OTHERS": if (!isWeapon && !isTool && !isArmor && !isRedstone && !isBlock) list.add(m); break;
            }
        }
        list.sort((a, b) -> a.name().compareTo(b.name()));
        return list;
    }

    private String formatName(String name) {
        String[] split = name.toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (String s : split) sb.append(s.substring(0, 1).toUpperCase()).append(s.substring(1)).append(" ");
        return sb.toString().trim();
    }

    public String getTitlePrefix() { return titlePrefix; }
}
