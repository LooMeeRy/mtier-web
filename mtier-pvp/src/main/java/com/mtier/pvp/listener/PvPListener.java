package com.mtier.pvp.listener;

import com.mtier.pvp.MTierPvP;
import com.mtier.pvp.gui.*;
import com.mtier.pvp.manager.RoomManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class PvPListener implements Listener {

    private final PvPMainMenu mainMenu = new PvPMainMenu();
    private final QueueMenu queueMenu = new QueueMenu();
    private final WaitingRoomMenu waitingRoomMenu = new WaitingRoomMenu();
    private final RoomBrowserMenu browserMenu = new RoomBrowserMenu();
    private final BanCategoryMenu banCategoryMenu = new BanCategoryMenu();
    private final BanItemMenu banItemMenu = new BanItemMenu();
    private final BanWaitMenu banWaitMenu = new BanWaitMenu();
    private final SelectionMenu selectionMenu = new SelectionMenu();

    // Prevent recursive exit when refreshing UI
    private final java.util.Set<java.util.UUID> refreshing = new java.util.HashSet<>();
    private final java.util.Map<java.util.UUID, String> searchPending = new java.util.HashMap<>();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (!title.contains("MTier PvP") && !title.contains("PvP Duel") && !title.contains("Ban") && !title.contains("Select")) return;

        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        
        // Handle Item Selection Phase
        if (title.contains("Select »")) {
            RoomManager.DuelRoom room = MTierPvP.getInstance().getRoomManager().getPlayerRoom(player);
            if (room != null && room.getPhase() == RoomManager.Phase.SELECTION_PHASE) {
                boolean isReady = room.getOwner().equals(player) ? room.isReadyOwnerSelection() : room.isReadyChallengerSelection();
                
                // If clicking the top inventory (Selection GUI)
                if (event.getRawSlot() < 54) {
                    event.setCancelled(true);
                    
                    if (item == null || item.getType() == Material.AIR || item.getType() == Material.GRAY_STAINED_GLASS_PANE) return;
                    
                    player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 0.8f, 1.2f);
                    String cat = title.replace(selectionMenu.getTitlePrefix(), "").split(" \\(")[0];

                    // READY BUTTON - Always allowed
                    if (item.getType() == Material.LIME_WOOL || item.getType() == Material.RED_WOOL) {
                        toggleSelectionReady(player, room);
                        refreshing.add(player.getUniqueId());
                        String query = title.contains("(") ? title.split("\\(")[1].replace(")", "") : null;
                        selectionMenu.open(player, cat, 0, query);
                        refreshing.remove(player.getUniqueId());
                        return;
                    }
                    
                    // FORBIDDEN IF READY
                    if (isReady) {
                        player.sendMessage("§6§lMTier §8» §cYou are already ready! Unready to change items.");
                        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                        return;
                    }

                    if (item.getType() == Material.DARK_OAK_DOOR) {
                        refreshing.add(player.getUniqueId());
                        banCategoryMenu.open(player);
                        refreshing.remove(player.getUniqueId());
                    } else if (item.getType() == Material.ARROW) {
                        int targetPage = 0;
                        if (item.getItemMeta().hasLore()) {
                            for (String line : item.getItemMeta().getLore()) {
                                if (line.contains("page")) {
                                    try {
                                        String stripped = org.bukkit.ChatColor.stripColor(line);
                                        targetPage = Integer.parseInt(stripped.replaceAll("[^0-9]", "")) - 1;
                                        break;
                                    } catch (Exception ignored) {}
                                }
                            }
                        }
                        String query = title.contains("(") ? title.split("\\(")[1].replace(")", "") : null;
                        refreshing.add(player.getUniqueId());
                        selectionMenu.open(player, cat, targetPage, query);
                        refreshing.remove(player.getUniqueId());
                    } else if (item.getType() == Material.COMPASS) {
                        searchPending.put(player.getUniqueId(), "SELECT:" + cat);
                        player.closeInventory();
                        player.sendMessage("§6§lMTier §8» §ePlease type your search query in chat!");
                    } else if (item.getItemMeta() != null && item.getItemMeta().getDisplayName().contains("BANNED")) {
                        player.sendMessage("§cThis item is banned!");
                    } else {
                        // GIVE ITEM
                        ItemStack toGive = new ItemStack(item.getType());
                        if (event.isLeftClick()) toGive.setAmount(item.getType().getMaxStackSize());
                        else toGive.setAmount(1);
                        player.getInventory().addItem(toGive);
                        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_ITEM_PICKUP, 0.5f, 1.5f);
                    }
                    return;
                } else {
                    // Clicking personal inventory
                    if (isReady) {
                        event.setCancelled(true);
                        player.sendMessage("§6§lMTier §8» §cYou are already ready! Unready to change items.");
                        return;
                    }
                    if (event.isRightClick()) {
                        event.setCancelled(true);
                        event.setCurrentItem(null);
                        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_ITEM_BREAK, 0.5f, 1.5f);
                    }
                    return;
                }
            }
        }

        event.setCancelled(true);
        if (item == null || item.getType() == Material.AIR) return;
        player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 0.8f, 1.2f);

        if (title.equals(mainMenu.getTitle())) {
            if (item.getType() == Material.ENDER_EYE) {
                queueMenu.open(player, MTierPvP.getInstance().getQueueManager().isSearching(player));
            } else if (item.getType() == Material.OAK_SIGN) {
                browserMenu.open(player, MTierPvP.getInstance().getRoomManager().getAvailableRooms(), 0);
            }
        } else if (title.equals(queueMenu.getTitle())) {
            if (item.getType() == Material.RED_WOOL) {
                MTierPvP.getInstance().getQueueManager().startSearching(player);
                refreshing.add(player.getUniqueId());
                queueMenu.open(player, true);
                refreshing.remove(player.getUniqueId());
            } else if (item.getType() == Material.YELLOW_WOOL) {
                MTierPvP.getInstance().getQueueManager().stopSearching(player);
                refreshing.add(player.getUniqueId());
                queueMenu.open(player, false);
                refreshing.remove(player.getUniqueId());
            } else if (item.getType() == Material.ARROW) {
                mainMenu.open(player);
            }
        } else if (title.contains(browserMenu.getTitle())) {
            if (item.getType() == Material.NETHER_STAR) {
                RoomManager.DuelRoom room = MTierPvP.getInstance().getRoomManager().createRoom(player);
                refreshWaitingRoom(room);
            } else if (item.getType() == Material.BARRIER) {
                mainMenu.open(player);
            } else if (item.getType() == Material.PLAYER_HEAD) {
                String ownerName = item.getItemMeta().getDisplayName().replace("'s Room", "").substring(4);
                for (RoomManager.DuelRoom r : MTierPvP.getInstance().getRoomManager().getAvailableRooms()) {
                    if (r.getOwner().getName().equals(ownerName)) {
                        MTierPvP.getInstance().getRoomManager().joinRoom(player, r);
                        refreshWaitingRoom(r);
                        return;
                    }
                }
            }
        } else if (title.equals(waitingRoomMenu.getTitle())) {
            RoomManager.DuelRoom room = MTierPvP.getInstance().getRoomManager().getPlayerRoom(player);
            if (room == null) return;
            if (item.getType() == Material.GREEN_WOOL) {
                if (room.getOwner().equals(player)) room.setReadyOwner(true);
                else room.setReadyChallenger(true);
                refreshWaitingRoom(room);
            } else if (item.getType() == Material.YELLOW_WOOL) {
                if (room.getOwner().equals(player)) room.setReadyOwner(false);
                else room.setReadyChallenger(false);
                refreshWaitingRoom(room);
            } else if (item.getType() == Material.RED_WOOL) {
                MTierPvP.getInstance().getRoomManager().leaveRoom(player);
                mainMenu.open(player);
                refreshWaitingRoom(room);
            }
        } else if (title.equals(banCategoryMenu.getTitle()) || title.equals(banCategoryMenu.getTitleSelect())) {
            RoomManager.DuelRoom room = MTierPvP.getInstance().getRoomManager().getPlayerRoom(player);
            if (room == null) return;
            
            if (room.getPhase() == RoomManager.Phase.SELECTION_PHASE) {
                if (item.getType() == Material.LIME_WOOL || item.getType() == Material.RED_WOOL) {
                    toggleSelectionReady(player, room);
                    refreshing.add(player.getUniqueId());
                    banCategoryMenu.open(player);
                    refreshing.remove(player.getUniqueId());
                } else if (item.getItemMeta() != null && item.getItemMeta().getDisplayName().startsWith("§")) {
                    // Check if ready before allowing browsing
                    boolean isReady = room.getOwner().equals(player) ? room.isReadyOwnerSelection() : room.isReadyChallengerSelection();
                    if (isReady) {
                        player.sendMessage("§6§lMTier §8» §cYou are already ready! Unready to change items.");
                        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                        return;
                    }
                    String cat = item.getItemMeta().getDisplayName().substring(4);
                    refreshing.add(player.getUniqueId());
                    selectionMenu.open(player, cat, 0, null);
                    refreshing.remove(player.getUniqueId());
                }
                return;
            }

            if (!room.getTurnPlayer().equals(player.getUniqueId())) return;
            if (item.getType() == Material.BARRIER) {
                room.getBanHistory().add(new RoomManager.BanEntry(player.getUniqueId(), null));
                MTierPvP.getInstance().getBanManager().nextTurn(room);
                refreshing.add(player.getUniqueId());
                banWaitMenu.open(player, room);
                refreshing.remove(player.getUniqueId());
            } else if (item.getItemMeta() != null && item.getItemMeta().getDisplayName().startsWith("§")) {
                String cat = item.getItemMeta().getDisplayName().substring(4);
                refreshing.add(player.getUniqueId());
                banItemMenu.open(player, cat, 0, null);
                refreshing.remove(player.getUniqueId());
            }
        } else if (title.startsWith(banItemMenu.getTitlePrefix())) {
            RoomManager.DuelRoom room = MTierPvP.getInstance().getRoomManager().getPlayerRoom(player);
            if (room == null || !room.getTurnPlayer().equals(player.getUniqueId())) return;
            String cat = title.replace(banItemMenu.getTitlePrefix(), "").split(" \\(")[0];
            if (item.getType() == Material.DARK_OAK_DOOR) {
                refreshing.add(player.getUniqueId());
                banCategoryMenu.open(player);
                refreshing.remove(player.getUniqueId());
            } else if (item.getType() == Material.BARRIER) {
                room.getBanHistory().add(new RoomManager.BanEntry(player.getUniqueId(), null));
                MTierPvP.getInstance().getBanManager().nextTurn(room);
                refreshing.add(player.getUniqueId());
                banWaitMenu.open(player, room);
                refreshing.remove(player.getUniqueId());
            } else if (item.getType() == Material.ARROW) {
                String query = title.contains("(") ? title.split("\\(")[1].replace(")", "") : null;
                int targetPage = 0;
                if (item.getItemMeta().hasLore()) {
                    for (String line : item.getItemMeta().getLore()) {
                        if (line.contains("page")) {
                            try {
                                String stripped = org.bukkit.ChatColor.stripColor(line);
                                targetPage = Integer.parseInt(stripped.replaceAll("[^0-9]", "")) - 1;
                                break;
                            } catch (Exception ignored) {}
                        }
                    }
                }
                refreshing.add(player.getUniqueId());
                banItemMenu.open(player, cat, targetPage, query);
                refreshing.remove(player.getUniqueId());
            } else if (item.getType() == Material.COMPASS) {
                searchPending.put(player.getUniqueId(), cat);
                player.closeInventory();
                player.sendMessage("§6§lMTier §8» §ePlease type your search query in chat!");
            } else if (item.getType() != Material.GRAY_STAINED_GLASS_PANE) {
                if (item.getItemMeta() != null && item.getItemMeta().getDisplayName().startsWith("§c§lBANNED")) return;
                room.getBanHistory().add(new RoomManager.BanEntry(player.getUniqueId(), item.getType()));
                MTierPvP.getInstance().getBanManager().nextTurn(room);
                refreshing.add(player.getUniqueId());
                banWaitMenu.open(player, room);
                refreshing.remove(player.getUniqueId());
            }
        }
    }

    private void toggleSelectionReady(Player player, RoomManager.DuelRoom room) {
        if (room.getOwner().equals(player)) {
            room.setReadyOwnerSelection(!room.isReadyOwnerSelection());
        } else {
            room.setReadyChallengerSelection(!room.isReadyChallengerSelection());
        }
        player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
        
        Player other = room.getOwner().equals(player) ? room.getChallenger() : room.getOwner();
        if (other != null) {
            other.sendMessage("§6§lMTier §8» §f" + player.getName() + " §e" + (room.getOwner().equals(player) ? (room.isReadyOwnerSelection() ? "is §aREADY" : "is §cNOT READY") : (room.isReadyChallengerSelection() ? "is §aREADY" : "is §cNOT READY")));
        }
    }

    @EventHandler
    public void onChat(org.bukkit.event.player.AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (searchPending.containsKey(player.getUniqueId())) {
            event.setCancelled(true);
            String rawCat = searchPending.remove(player.getUniqueId());
            String query = event.getMessage();
            Bukkit.getScheduler().runTask(MTierPvP.getInstance(), () -> {
                refreshing.add(player.getUniqueId());
                if (rawCat.startsWith("SELECT:")) selectionMenu.open(player, rawCat.replace("SELECT:", ""), 0, query);
                else banItemMenu.open(player, rawCat, 0, query);
                refreshing.remove(player.getUniqueId());
            });
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (refreshing.contains(player.getUniqueId())) return; 
        String title = event.getView().getTitle();
        if (title.contains("Select »") || title.contains("Select Categories")) {
            RoomManager.DuelRoom room = MTierPvP.getInstance().getRoomManager().getPlayerRoom(player);
            if (room != null && room.getPhase() == RoomManager.Phase.SELECTION_PHASE) {
                net.kyori.adventure.text.Component clickMe = net.kyori.adventure.text.Component.text("§6§lMTier §8» §a§l[CLICK HERE TO REOPEN SELECTION MENU]")
                    .clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand("/banmenu"));
                player.sendMessage("");
                player.sendMessage("§eYou closed the selection menu.");
                player.sendMessage(clickMe);
                player.sendMessage("");
            }
        }
    }

    public void refreshWaitingRoom(RoomManager.DuelRoom room) {
        if (room == null) return;
        Player owner = room.getOwner();
        Player challenger = room.getChallenger();
        if (room.isDeleted()) {
            if (owner != null && owner.isOnline()) mainMenu.open(owner);
            if (challenger != null && challenger.isOnline()) mainMenu.open(challenger);
            return;
        }
        if (owner != null && owner.isOnline()) {
            refreshing.add(owner.getUniqueId());
            waitingRoomMenu.open(owner, owner, challenger, room.isReadyOwner(), room.isReadyChallenger());
            refreshing.remove(owner.getUniqueId());
        }
        if (challenger != null && challenger.isOnline()) {
            refreshing.add(challenger.getUniqueId());
            waitingRoomMenu.open(challenger, owner, challenger, room.isReadyOwner(), room.isReadyChallenger());
            refreshing.remove(challenger.getUniqueId());
        }
        if (room.bothReady() && !room.isStarting()) MTierPvP.getInstance().getRoomManager().startMatch(room);
    }

    public BanCategoryMenu getBanCategoryMenu() { return banCategoryMenu; }
    public BanItemMenu getBanItemMenu() { return banItemMenu; }
    public BanWaitMenu getBanWaitMenu() { return banWaitMenu; }
}
