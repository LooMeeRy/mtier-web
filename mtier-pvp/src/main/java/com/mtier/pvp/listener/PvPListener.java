package com.mtier.pvp.listener;

import com.mtier.pvp.MTierPvP;
import com.mtier.pvp.gui.*;
import com.mtier.pvp.manager.RoomManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class PvPListener implements Listener {

    private final PvPMainMenu mainMenu = new PvPMainMenu();
    private final QueueMenu queueMenu = new QueueMenu();
    private final WaitingRoomMenu waitingRoomMenu = new WaitingRoomMenu();
    private final RoomBrowserMenu browserMenu = new RoomBrowserMenu();

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
                queueMenu.open(player, true);
            } else if (item.getType() == Material.YELLOW_WOOL) {
                MTierPvP.getInstance().getQueueManager().stopSearching(player);
                queueMenu.open(player, false);
            } else if (item.getType() == Material.ARROW) {
                mainMenu.open(player);
            }
        }

        // 3. Handle Browser Menu
        else if (title.contains(browserMenu.getTitle())) {
            if (item.getType() == Material.NETHER_STAR) {
                RoomManager.DuelRoom room = MTierPvP.getInstance().getRoomManager().createRoom(player);
                waitingRoomMenu.open(player, player, null, false, false);
            } else if (item.getType() == Material.BARRIER) {
                mainMenu.open(player);
            } else if (item.getType() == Material.PLAYER_HEAD) {
                // Join Room Logic
                String ownerName = item.getItemMeta().getDisplayName().replace("'s Room", "").substring(4);
                // Simple lookup for demo
                for (RoomManager.DuelRoom r : MTierPvP.getInstance().getRoomManager().getAvailableRooms()) {
                    if (r.getOwner().getName().equals(ownerName) && !r.isFull()) {
                        MTierPvP.getInstance().getRoomManager().joinRoom(player, r);
                        refreshWaitingRoom(r);
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
                if (room.getOwner().equals(player)) room.setReadyOwner(true);
                else room.setReadyChallenger(true);
                refreshWaitingRoom(room);
            } else if (item.getType() == Material.YELLOW_WOOL) {
                // Unready
                if (room.getOwner().equals(player)) room.setReadyOwner(false);
                else room.setReadyChallenger(false);
                refreshWaitingRoom(room);
            } else if (item.getType() == Material.RED_WOOL) {
                // Leave
                MTierPvP.getInstance().getRoomManager().leaveRoom(player);
                mainMenu.open(player);
                if (room.getOwner().equals(player)) {
                    if (room.getChallenger() != null) mainMenu.open(room.getChallenger());
                } else {
                    refreshWaitingRoom(room);
                }
            }
        }
    }

    private void refreshWaitingRoom(RoomManager.DuelRoom room) {
        if (room.getOwner() != null) {
            waitingRoomMenu.open(room.getOwner(), room.getOwner(), room.getChallenger(), room.isReadyOwner(), room.isReadyChallenger());
        }
        if (room.getChallenger() != null) {
            waitingRoomMenu.open(room.getChallenger(), room.getOwner(), room.getChallenger(), room.isReadyOwner(), room.isReadyChallenger());
        }
        
        if (room.bothReady() && !room.isStarting()) {
            room.setStarting(true);
            // TODO: Start Countdown Task
            room.getOwner().sendMessage("§aMatch starts in 5 seconds...");
            if (room.getChallenger() != null) room.getChallenger().sendMessage("§aMatch starts in 5 seconds...");
        }
    }
}
