package com.mtier.plugin.api;

import com.mtier.plugin.MTierPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class MenuManager implements Listener {

    public record GamemodeEntry(String displayName, Material icon, Consumer<Player> action) {}

    private final Map<String, GamemodeEntry> registeredModes = new ConcurrentHashMap<>();
    private final String menuTitle = "§6§lMTier §8» §7Select Mode";

    public void registerMode(String id, String displayName, Material icon, Consumer<Player> action) {
        registeredModes.put(id, new GamemodeEntry(displayName, icon, action));
    }

    public void openMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, menuTitle);

        // Fill background with glass
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        if (fillerMeta != null) {
            fillerMeta.setDisplayName(" ");
            filler.setItemMeta(fillerMeta);
        }
        for (int i = 0; i < 27; i++) inv.setItem(i, filler);

        // Add registered modes
        int slot = 10; // Start in middle row
        for (GamemodeEntry entry : registeredModes.values()) {
            if (slot > 16) break; // Simple grid limit for now

            ItemStack item = new ItemStack(entry.icon());
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§e§l" + entry.displayName());
                List<String> lore = new ArrayList<>();
                lore.add("§7Click to enter this sector.");
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            inv.setItem(slot++, item);
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(menuTitle)) return;
        event.setCancelled(true);

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;
        
        Player player = (Player) event.getWhoClicked();
        String clickedName = event.getCurrentItem().getItemMeta().getDisplayName();

        for (GamemodeEntry entry : registeredModes.values()) {
            if (clickedName.contains(entry.displayName())) {
                player.closeInventory();
                entry.action().accept(player);
                return;
            }
        }
    }
}
