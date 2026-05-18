package com.mtier.pvp.manager;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RoomManager {

    public static class DuelRoom {
        private final UUID id;
        private final Player owner;
        private Player challenger;
        private boolean readyOwner = false;
        private boolean readyChallenger = false;
        private boolean starting = false;

        public DuelRoom(Player owner) {
            this.id = UUID.randomUUID();
            this.owner = owner;
        }

        public UUID getId() { return id; }
        public Player getOwner() { return owner; }
        public Player getChallenger() { return challenger; }
        public void setChallenger(Player challenger) { this.challenger = challenger; }
        public boolean isReadyOwner() { return readyOwner; }
        public void setReadyOwner(boolean readyOwner) { this.readyOwner = readyOwner; }
        public boolean isReadyChallenger() { return readyChallenger; }
        public void setReadyChallenger(boolean readyChallenger) { this.readyChallenger = readyChallenger; }
        public boolean isStarting() { return starting; }
        public void setStarting(boolean starting) { this.starting = starting; }
        
        public boolean isFull() { return challenger != null; }
        public boolean bothReady() { return readyOwner && readyChallenger; }
    }

    private final Map<UUID, DuelRoom> activeRooms = new ConcurrentHashMap<>();
    private final Map<UUID, UUID> playerToRoom = new ConcurrentHashMap<>();

    public DuelRoom createRoom(Player owner) {
        DuelRoom room = new DuelRoom(owner);
        activeRooms.put(room.getId(), room);
        playerToRoom.put(owner.getUniqueId(), room.getId());
        return room;
    }

    public void removeRoom(UUID roomId) {
        DuelRoom room = activeRooms.remove(roomId);
        if (room != null) {
            playerToRoom.remove(room.getOwner().getUniqueId());
            if (room.getChallenger() != null) {
                playerToRoom.remove(room.getChallenger().getUniqueId());
            }
        }
    }

    public DuelRoom getRoom(UUID roomId) {
        return activeRooms.get(roomId);
    }

    public DuelRoom getPlayerRoom(Player player) {
        UUID roomId = playerToRoom.get(player.getUniqueId());
        return roomId != null ? activeRooms.get(roomId) : null;
    }

    public List<DuelRoom> getAvailableRooms() {
        return new ArrayList<>(activeRooms.values());
    }
    
    public void joinRoom(Player player, DuelRoom room) {
        if (room.isFull()) return;
        room.setChallenger(player);
        playerToRoom.put(player.getUniqueId(), room.getId());
    }

    public void leaveRoom(Player player) {
        DuelRoom room = getPlayerRoom(player);
        if (room == null) return;

        if (room.getOwner().getUniqueId().equals(player.getUniqueId())) {
            // Owner leaves -> Dissolve room
            removeRoom(room.getId());
        } else {
            // Challenger leaves
            room.setChallenger(null);
            room.setReadyChallenger(false);
            room.setReadyOwner(false);
            playerToRoom.remove(player.getUniqueId());
        }
    }
}
