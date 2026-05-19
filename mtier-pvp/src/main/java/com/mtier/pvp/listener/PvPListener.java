package com.mtier.pvp.listener;

import com.mtier.pvp.MTierPvP;
import com.mtier.pvp.gui.*;
import com.mtier.pvp.manager.RoomManager;
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

    // Prevent recursive exit when refreshing UI
    private final java.util.Set<java.util.UUID> refreshing = new java.util.HashSet<>();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (!title.contains("MTier PvP") && !title.contains("PvP Duel")) return;
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;

        // 1. Handle Main Menu
        if (title.equals(mainMenu.getTitle())) {
            if (item.getType() == Material.ENDER_EYE) {
                queueMenu.open(player, MTierPvP.getInstance().getQueueManager().isSearching(player));
            } else if (item.getType() == Material.OAK_SIGN) {
                browserMenu.open(player, MTierPvP.getInstance().getRoomManager().getAvailableRooms(), 0);
            }
        }

        // 2. Handle Queue Menu
        else if (title.equals(queueMenu.getTitle())) {
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
        }

        // 3. Handle Browser Menu
        else if (title.contains(browserMenu.getTitle())) {
            if (item.getType() == Material.NETHER_STAR) {
                RoomManager.DuelRoom room = MTierPvP.getInstance().getRoomManager().createRoom(player);
                refreshWaitingRoom(room); // Open for owner
            } else if (item.getType() == Material.SUNFLOWER) {
                browserMenu.open(player, MTierPvP.getInstance().getRoomManager().getAvailableRooms(), 0);
            } else if (item.getType() == Material.BARRIER) {
                mainMenu.open(player);
            } else if (item.getType() == Material.PLAYER_HEAD) {
                String ownerName = item.getItemMeta().getDisplayName().replace("'s Room", "").substring(4);
                for (RoomManager.DuelRoom r : MTierPvP.getInstance().getRoomManager().getAvailableRooms()) {
                    if (r.getOwner().getName().equals(ownerName)) {
                        if (r.isFull()) {
                            player.sendMessage("§6§lMTier §8» §cThis room is already full!");
                            return;
                        }
                        MTierPvP.getInstance().getRoomManager().joinRoom(player, r);
                        refreshWaitingRoom(r); // Open for both
                        return;
                    }
                }
            }
        }

        // 4. Handle Waiting Room
        else if (title.equals(waitingRoomMenu.getTitle())) {
            RoomManager.DuelRoom room = MTierPvP.getInstance().getRoomManager().getPlayerRoom(player);
            if (room == null) return;

            if (item.getType() == Material.GREEN_WOOL) {
                // Set Ready
                if (room.getOwner().getUniqueId().equals(player.getUniqueId())) {
                    room.setReadyOwner(true);
                } else if (room.getChallenger() != null && room.getChallenger().getUniqueId().equals(player.getUniqueId())) {
                    room.setReadyChallenger(true);
                }
                refreshWaitingRoom(room);
            } else if (item.getType() == Material.YELLOW_WOOL) {
                // Unready
                if (room.getOwner().getUniqueId().equals(player.getUniqueId())) {
                    room.setReadyOwner(false);
                } else if (room.getChallenger() != null && room.getChallenger().getUniqueId().equals(player.getUniqueId())) {
                    room.setReadyChallenger(false);
                }
                refreshWaitingRoom(room);
            } else if (item.getType() == Material.RED_WOOL) {
                // Leave
                MTierPvP.getInstance().getRoomManager().leaveRoom(player);
                mainMenu.open(player);
                refreshWaitingRoom(room);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (refreshing.contains(player.getUniqueId())) return; 

        String title = event.getView().getTitle();
        if (title.equals(queueMenu.getTitle())) {
            MTierPvP.getInstance().getQueueManager().stopSearching(player);
        } else if (title.equals(waitingRoomMenu.getTitle())) {
            RoomManager.DuelRoom room = MTierPvP.getInstance().getRoomManager().getPlayerRoom(player);
            if (room != null && !room.isStarting()) {
                MTierPvP.getInstance().getRoomManager().leaveRoom(player);
                refreshWaitingRoom(room);
            }
        }
    }

    public void refreshWaitingRoom(RoomManager.DuelRoom room) {
        if (room == null) return;
        
        Player owner = room.getOwner();
        Player challenger = room.getChallenger();

        // If room is deleted, send online participants to main menu
        if (room.isDeleted()) {
            if (owner != null && owner.isOnline() && owner.getOpenInventory().getTitle().equals(waitingRoomMenu.getTitle())) {
                mainMenu.open(owner);
                owner.sendMessage("§6§lMTier §8» §cThe room has been dissolved.");
            }
            if (challenger != null && challenger.isOnline() && challenger.getOpenInventory().getTitle().equals(waitingRoomMenu.getTitle())) {
                mainMenu.open(challenger);
                challenger.sendMessage("§6§lMTier §8» §cThe room has been dissolved.");
            }
            return;
        }

        // Safe Refresh for Owner
        if (owner != null && owner.isOnline()) {
            refreshing.add(owner.getUniqueId());
            waitingRoomMenu.open(owner, owner, challenger, room.isReadyOwner(), room.isReadyChallenger());
            refreshing.remove(owner.getUniqueId());
        }

        // Safe Refresh for Challenger
        if (challenger != null && challenger.isOnline()) {
            refreshing.add(challenger.getUniqueId());
            waitingRoomMenu.open(challenger, owner, challenger, room.isReadyOwner(), room.isReadyChallenger());
            refreshing.remove(challenger.getUniqueId());
        }
        
        if (room.bothReady() && !room.isStarting()) {
            room.setStarting(true);
            String msg = "§6§lMTier §8» §aAll players ready! Match starts in 5 seconds...";
            if (owner != null) owner.sendMessage(msg);
            if (challenger != null) challenger.sendMessage(msg);
        }
    }
}
