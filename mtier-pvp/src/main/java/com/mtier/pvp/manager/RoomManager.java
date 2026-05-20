package com.mtier.pvp.manager;

import com.mtier.pvp.MTierPvP;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RoomManager {

    public enum Phase {
        WAITING, BAN_PHASE, SELECTION_PHASE, PVP_PHASE, POST_MATCH
    }

    public static class DuelRoom {
        private final UUID id;
        private final Player owner;
        private Player challenger;
        private boolean readyOwner = false;
        private boolean readyChallenger = false;
        private boolean readyOwnerSelection = false;
        private boolean readyChallengerSelection = false;
        private boolean starting = false;
        private boolean deleted = false;
        
        // Spectators
        private final List<UUID> spectators = new ArrayList<>();

        // Loadouts
        private final List<Material> ownerLoadout = new ArrayList<>();
        private final List<Material> challengerLoadout = new ArrayList<>();
        
        // Ban Phase Fields
        private Phase phase = Phase.WAITING;
        private final List<BanEntry> banHistory = new ArrayList<>();
        private UUID turnPlayer; // Current player who is banning
        private int turnCount = 0; // Up to 6
        private int turnRemaining = 120; // 2 minutes in seconds

        public DuelRoom(Player owner) {
            this.id = UUID.randomUUID();
            this.owner = owner;
            this.turnPlayer = owner.getUniqueId();
        }

        public UUID getId() { return id; }
        public Player getOwner() { return owner; }
        public Player getChallenger() { return challenger; }
        public void setChallenger(Player challenger) { this.challenger = challenger; }
        public boolean isReadyOwner() { return readyOwner; }
        public void setReadyOwner(boolean readyOwner) { this.readyOwner = readyOwner; }
        public boolean isReadyChallenger() { return readyChallenger; }
        public void setReadyChallenger(boolean readyChallenger) { this.readyChallenger = readyChallenger; }
        
        public boolean isReadyOwnerSelection() { return readyOwnerSelection; }
        public void setReadyOwnerSelection(boolean readyOwnerSelection) { this.readyOwnerSelection = readyOwnerSelection; }
        public boolean isReadyChallengerSelection() { return readyChallengerSelection; }
        public void setReadyChallengerSelection(boolean readyChallengerSelection) { this.readyChallengerSelection = readyChallengerSelection; }
        
        public boolean isStarting() { return starting; }
        public void setStarting(boolean starting) { this.starting = starting; }
        public boolean isDeleted() { return deleted; }
        public void setDeleted(boolean deleted) { this.deleted = deleted; }
        
        public List<UUID> getSpectators() { return spectators; }
        public List<Material> getOwnerLoadout() { return ownerLoadout; }
        public List<Material> getChallengerLoadout() { return challengerLoadout; }
        
        public boolean isFull() { return challenger != null; }
        public boolean bothReady() { return readyOwner && readyChallenger; }
        public boolean bothReadySelection() { return readyOwnerSelection && readyChallengerSelection; }

        public Phase getPhase() { return phase; }
        public void setPhase(Phase phase) { this.phase = phase; }
        
        public List<BanEntry> getBanHistory() { return banHistory; }
        
        public java.util.Set<Material> getBannedItems() {
            java.util.Set<Material> banned = new java.util.HashSet<>();
            for (BanEntry entry : banHistory) {
                if (entry.material() != null) banned.add(entry.material());
            }
            return banned;
        }

        public UUID getTurnPlayer() { return turnPlayer; }
        public void setTurnPlayer(UUID turnPlayer) { this.turnPlayer = turnPlayer; }
        public int getTurnCount() { return turnCount; }
        public void setTurnCount(int turnCount) { this.turnCount = turnCount; }
        public int getTurnRemaining() { return turnRemaining; }
        public void setTurnRemaining(int turnRemaining) { this.turnRemaining = turnRemaining; }
    }

    public record BanEntry(UUID player, Material material) {}

    private final Map<UUID, DuelRoom> activeRooms = new ConcurrentHashMap<>();
    private final Map<UUID, UUID> playerToRoom = new ConcurrentHashMap<>();

    public DuelRoom createRoom(Player owner) {
        DuelRoom room = new DuelRoom(owner);
        activeRooms.put(room.getId(), room);
        playerToRoom.put(owner.getUniqueId(), room.getId());
        return room;
    }

    public void createMatchmakingRoom(Player a, Player b) {
        DuelRoom room = createRoom(a);
        room.setChallenger(b);
        playerToRoom.put(b.getUniqueId(), room.getId());
        
        a.sendMessage("§6§lMTier §8» §aMatch Found! §7vs §e" + b.getName());
        b.sendMessage("§6§lMTier §8» §aMatch Found! §7vs §e" + a.getName());
        
        a.playSound(a.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        b.playSound(b.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        
        // Use Scheduler to ensure we are on main thread for UI
        org.bukkit.Bukkit.getScheduler().runTask(MTierPvP.getInstance(), () -> {
            MTierPvP.getInstance().getPvPListener().refreshWaitingRoom(room);
        });
    }

    public void removeRoom(UUID roomId) {
        DuelRoom room = activeRooms.remove(roomId);
        if (room != null) {
            room.setDeleted(true);
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
            removeRoom(room.getId());
        } else {
            room.setChallenger(null);
            room.setReadyChallenger(false);
            room.setReadyOwner(false);
            playerToRoom.remove(player.getUniqueId());
        }
    }

    public void startMatch(DuelRoom room) {
        if (room == null || room.isStarting()) return;
        room.setStarting(true);

        Player p1 = room.getOwner();
        Player p2 = room.getChallenger();

        if (p1 == null || p2 == null) return;

        // 1. Start ASWM World Creation
        UUID p1Id = p1.getUniqueId();
        UUID p2Id = p2.getUniqueId();

        MTierPvP.getInstance().getSlimeManager().createMatchWorld(room.getId()).thenAccept(world -> {
            if (world == null) {
                Player op1 = Bukkit.getPlayer(p1Id);
                Player op2 = Bukkit.getPlayer(p2Id);
                if (op1 != null) op1.sendMessage("§cFailed to create match world. Match cancelled.");
                if (op2 != null) op2.sendMessage("§cFailed to create match world. Match cancelled.");
                room.setStarting(false);
                return;
            }

            // 2. Teleport to World First
            Bukkit.getScheduler().runTask(MTierPvP.getInstance(), () -> {
                Player op1 = Bukkit.getPlayer(p1Id);
                Player op2 = Bukkit.getPlayer(p2Id);
                if (op1 == null || op2 == null) {
                    MTierPvP.getInstance().getLogger().warning("One or more players logged out during world creation.");
                    return;
                }

                // Specific spawn locations
                org.bukkit.Location loc1 = new org.bukkit.Location(world, -26.5, 63, -25.5);
                org.bukkit.Location loc2 = new org.bukkit.Location(world, 41.5, 63, 46.5);

                MTierPvP.getInstance().getLogger().info("Teleporting " + op1.getName() + " to " + loc1.getX() + ", " + loc1.getY() + ", " + loc1.getZ());
                MTierPvP.getInstance().getLogger().info("Teleporting " + op2.getName() + " to " + loc2.getX() + ", " + loc2.getY() + ", " + loc2.getZ());
                
                op1.setAllowFlight(true);
                op1.setFlying(true);
                op2.setAllowFlight(true);
                op2.setFlying(true);
                
                op1.teleport(loc1);
                op2.teleport(loc2);
                
                // 3. Start 5-second Countdown before Ban Phase
                new BukkitRunnable() {
                    int seconds = 5;
                    @Override
                    public void run() {
                        Player p1 = Bukkit.getPlayer(p1Id);
                        Player p2 = Bukkit.getPlayer(p2Id);
                        
                        if (p1 == null || p2 == null || !p1.isOnline() || !p2.isOnline()) {
                            this.cancel();
                            return;
                        }

                        if (seconds <= 0) {
                            MTierPvP.getInstance().getLogger().info("§a[RoomManager] COUNTDOWN FINISHED! Starting Ban Phase...");
                            // Force start on next tick to be safe
                            Bukkit.getScheduler().runTask(MTierPvP.getInstance(), () -> {
                                MTierPvP.getInstance().getBanManager().startBanPhase(room);
                            });
                            this.cancel();
                            return;
                        }

                        String countdownMsg = "§6§lMTier §8» §eStarting Ban Phase in §f" + seconds + "§es...";
                        MTierPvP.getInstance().getLogger().info("§7[RoomManager] Countdown: " + seconds);
                        p1.sendMessage(countdownMsg);
                        p2.sendMessage(countdownMsg);
                        p1.sendTitle("§6§l" + seconds, "§ePreparing Ban Phase...", 5, 10, 5);
                        p2.sendTitle("§6§l" + seconds, "§ePreparing Ban Phase...", 5, 10, 5);
                        p1.playSound(p1.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                        p2.playSound(p2.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);

                        seconds--;
                    }
                }.runTaskTimer(MTierPvP.getInstance(), 20L, 20L);
            });
            
        }).exceptionally(ex -> {
            p1.sendMessage("§cError: " + ex.getMessage());
            p2.sendMessage("§cError: " + ex.getMessage());
            room.setStarting(false);
            return null;
        });
    }
}
