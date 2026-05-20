package com.mtier.pvp.manager;

import com.mtier.plugin.MTierPlugin;
import com.mtier.plugin.api.WebSyncManager;
import com.mtier.plugin.api.models.MatchData;
import com.mtier.pvp.MTierPvP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BanManager {

    private final Map<UUID, BossBar> roomBars = new ConcurrentHashMap<>();
    private final Map<UUID, BukkitRunnable> roomTasks = new ConcurrentHashMap<>();

    public void initBossBar(RoomManager.DuelRoom room) {
        if (roomBars.containsKey(room.getId())) return;
        
        BossBar bar = Bukkit.createBossBar("§6§lBan Phase §8» §fInitializing...", org.bukkit.boss.BarColor.YELLOW, org.bukkit.boss.BarStyle.SOLID);
        bar.setVisible(true);
        bar.setProgress(1.0);
        
        Player p1 = Bukkit.getPlayer(room.getOwner().getUniqueId());
        Player p2 = room.getChallenger() != null ? Bukkit.getPlayer(room.getChallenger().getUniqueId()) : null;
        
        if (p1 != null) bar.addPlayer(p1);
        if (p2 != null) bar.addPlayer(p2);
        
        roomBars.put(room.getId(), bar);
    }

    public void startBanPhase(RoomManager.DuelRoom room) {
        UUID ownerId = room.getOwner().getUniqueId();
        UUID challengerId = room.getChallenger() != null ? room.getChallenger().getUniqueId() : null;

        room.setPhase(RoomManager.Phase.BAN_PHASE);
        room.setTurnRemaining(120);
        room.setTurnCount(0);
        room.setTurnPlayer(ownerId);

        initBossBar(room);
        BossBar bar = roomBars.get(room.getId());

        Bukkit.getScheduler().runTask(MTierPvP.getInstance(), () -> {
            Player op1 = Bukkit.getPlayer(ownerId);
            Player op2 = challengerId != null ? Bukkit.getPlayer(challengerId) : null;

            Component clickMe = Component.text("§6§lMTier §8» §a§l[CLICK HERE TO OPEN BAN MENU]")
                .clickEvent(ClickEvent.runCommand("/banmenu"))
                .hoverEvent(HoverEvent.showText(Component.text("§7Click to open the item ban selector")));

            if (op1 != null) {
                op1.sendMessage("");
                op1.sendMessage("§6§lMTier §8» §eThe Ban Phase has officially started!");
                op1.sendMessage(clickMe);
                op1.sendMessage("");
                op1.playSound(op1.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                MTierPvP.getInstance().getPvPListener().getBanCategoryMenu().open(op1);
            }
            
            if (op2 != null) {
                op2.sendMessage("");
                op2.sendMessage("§6§lMTier §8» §eThe Ban Phase has officially started!");
                op2.sendMessage(clickMe);
                op2.sendMessage("");
                op2.playSound(op2.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                MTierPvP.getInstance().getPvPListener().getBanWaitMenu().open(op2, room);
            }
        });

        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (room.isDeleted()) {
                    cleanup(room.getId());
                    cancel();
                    return;
                }
                
                if (room.getPhase() != RoomManager.Phase.BAN_PHASE) {
                    cancel();
                    return;
                }

                int remaining = room.getTurnRemaining();
                if (remaining <= 0) {
                    nextTurn(room);
                    return;
                }

                room.setTurnRemaining(remaining - 1);
                
                Player turnP = Bukkit.getPlayer(room.getTurnPlayer());
                String pName = turnP != null ? turnP.getName() : "Unknown";
                bar.setTitle("§6§lBan Phase §8» §e" + pName + " §fis banning... §7(" + formatTime(room.getTurnRemaining()) + ")");
                bar.setProgress(Math.max(0.0, Math.min(1.0, room.getTurnRemaining() / 120.0)));
                
                if (remaining % 30 == 0 || (remaining <= 10 && remaining > 0)) {
                    if (turnP != null) turnP.playSound(turnP.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_HAT, 0.5f, 1.0f);
                }
            }
        };
        task.runTaskTimer(MTierPvP.getInstance(), 20L, 20L);
        roomTasks.put(room.getId(), task);
    }

    public void nextTurn(RoomManager.DuelRoom room) {
        room.setTurnCount(room.getTurnCount() + 1);
        
        if (room.getTurnCount() >= 6) {
            startSelectionPhase(room);
            return;
        }

        if (room.getTurnPlayer().equals(room.getOwner().getUniqueId())) {
            room.setTurnPlayer(room.getChallenger().getUniqueId());
        } else {
            room.setTurnPlayer(room.getOwner().getUniqueId());
        }

        room.setTurnRemaining(120);
        
        Player turnP = Bukkit.getPlayer(room.getTurnPlayer());
        Player waitingP = room.getTurnPlayer().equals(room.getOwner().getUniqueId()) ? 
                         room.getChallenger() : room.getOwner();

        Bukkit.getScheduler().runTask(MTierPvP.getInstance(), () -> {
            if (turnP != null) MTierPvP.getInstance().getPvPListener().getBanCategoryMenu().open(turnP);
            if (waitingP != null) MTierPvP.getInstance().getPvPListener().getBanWaitMenu().open(waitingP, room);
        });
        
        if (turnP != null) turnP.playSound(turnP.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
    }

    public void startSelectionPhase(RoomManager.DuelRoom room) {
        room.setPhase(RoomManager.Phase.SELECTION_PHASE);
        room.setTurnRemaining(300); // 5 minutes

        Player p1 = Bukkit.getPlayer(room.getOwner().getUniqueId());
        Player p2 = room.getChallenger() != null ? Bukkit.getPlayer(room.getChallenger().getUniqueId()) : null;

        initBossBar(room);
        BossBar bar = roomBars.get(room.getId());
        if (bar != null) {
            bar.setColor(BarColor.GREEN);
            bar.setTitle("§a§lSelection Phase §8» §fPick your items! §7(" + formatTime(room.getTurnRemaining()) + ")");
        }

        Bukkit.getScheduler().runTask(MTierPvP.getInstance(), () -> {
            if (p1 != null) {
                p1.closeInventory();
                MTierPvP.getInstance().getPvPListener().getBanCategoryMenu().open(p1);
            }
            if (p2 != null) {
                p2.closeInventory();
                MTierPvP.getInstance().getPvPListener().getBanCategoryMenu().open(p2);
            }
        });

        BukkitRunnable selectionTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (room.isDeleted()) {
                    cleanup(room.getId());
                    this.cancel();
                    return;
                }

                if (room.getPhase() != RoomManager.Phase.SELECTION_PHASE) {
                    this.cancel();
                    return;
                }

                // CHECK BOTH READY
                if (room.bothReadySelection()) {
                    startPvPPhase(room);
                    this.cancel();
                    return;
                }

                int remaining = room.getTurnRemaining();
                if (remaining <= 0) {
                    startPvPPhase(room);
                    this.cancel();
                    return;
                }

                room.setTurnRemaining(remaining - 1);
                if (bar != null) {
                    bar.setTitle("§a§lSelection Phase §8» §fPick your items! §7(" + formatTime(room.getTurnRemaining()) + ")");
                    bar.setProgress(Math.max(0.0, Math.min(1.0, room.getTurnRemaining() / 300.0)));
                }
            }
        };
        selectionTask.runTaskTimer(MTierPvP.getInstance(), 20L, 20L);
        roomTasks.put(room.getId(), selectionTask);
    }

    public void startPvPPhase(RoomManager.DuelRoom room) {
        room.setPhase(RoomManager.Phase.PVP_PHASE);
        room.setTurnRemaining(900); // 15 minutes

        Player p1 = Bukkit.getPlayer(room.getOwner().getUniqueId());
        Player p2 = room.getChallenger() != null ? Bukkit.getPlayer(room.getChallenger().getUniqueId()) : null;

        initBossBar(room);
        BossBar bar = roomBars.get(room.getId());
        if (bar != null) {
            bar.setColor(BarColor.RED);
            bar.setTitle("§c§lPvP Phase §8» §fGood Luck! §7(" + formatTime(room.getTurnRemaining()) + ")");
            bar.setProgress(1.0);
        }

        Bukkit.getScheduler().runTask(MTierPvP.getInstance(), () -> {
            if (p1 != null) {
                p1.closeInventory();
                
                // Snapshot Loadout (Main + Armor + Offhand)
                room.getOwnerLoadout().clear();
                for (org.bukkit.inventory.ItemStack item : p1.getInventory().getContents()) {
                    if (item != null && item.getType() != org.bukkit.Material.AIR) {
                        room.getOwnerLoadout().add(item.getType());
                    }
                }
                for (org.bukkit.inventory.ItemStack item : p1.getInventory().getArmorContents()) {
                    if (item != null && item.getType() != org.bukkit.Material.AIR) {
                        room.getOwnerLoadout().add(item.getType());
                    }
                }
                if (p1.getInventory().getItemInOffHand() != null && p1.getInventory().getItemInOffHand().getType() != org.bukkit.Material.AIR) {
                    room.getOwnerLoadout().add(p1.getInventory().getItemInOffHand().getType());
                }

                p1.setGameMode(GameMode.SURVIVAL);
                p1.setFlying(false);
                p1.setAllowFlight(false);
                p1.sendTitle("§c§lFIGHT!", "§eMatch Duration: 15m", 10, 40, 10);
                p1.playSound(p1.getLocation(), org.bukkit.Sound.ENTITY_WITHER_SPAWN, 1.0f, 1.0f);
            }
            if (p2 != null) {
                p2.closeInventory();
                
                // Snapshot Loadout (Main + Armor + Offhand)
                room.getChallengerLoadout().clear();
                for (org.bukkit.inventory.ItemStack item : p2.getInventory().getContents()) {
                    if (item != null && item.getType() != org.bukkit.Material.AIR) {
                        room.getChallengerLoadout().add(item.getType());
                    }
                }
                for (org.bukkit.inventory.ItemStack item : p2.getInventory().getArmorContents()) {
                    if (item != null && item.getType() != org.bukkit.Material.AIR) {
                        room.getChallengerLoadout().add(item.getType());
                    }
                }
                if (p2.getInventory().getItemInOffHand() != null && p2.getInventory().getItemInOffHand().getType() != org.bukkit.Material.AIR) {
                    room.getChallengerLoadout().add(p2.getInventory().getItemInOffHand().getType());
                }

                p2.setGameMode(GameMode.SURVIVAL);
                p2.setFlying(false);
                p2.setAllowFlight(false);
                p2.sendTitle("§c§lFIGHT!", "§eMatch Duration: 15m", 10, 40, 10);
                p2.playSound(p2.getLocation(), org.bukkit.Sound.ENTITY_WITHER_SPAWN, 1.0f, 1.0f);
            }
        });

        BukkitRunnable pvpTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (room.isDeleted()) {
                    cleanup(room.getId());
                    this.cancel();
                    return;
                }

                if (room.getPhase() != RoomManager.Phase.PVP_PHASE) {
                    this.cancel();
                    return;
                }

                int remaining = room.getTurnRemaining();
                if (remaining <= 0) {
                    endMatch(room, null); // DRAW
                    this.cancel();
                    return;
                }

                room.setTurnRemaining(remaining - 1);
                if (bar != null) {
                    bar.setTitle("§c§lPvP Phase §8» §fFight! §7(" + formatTime(room.getTurnRemaining()) + ")");
                    bar.setProgress(Math.max(0.0, Math.min(1.0, room.getTurnRemaining() / 900.0)));
                }
            }
        };
        pvpTask.runTaskTimer(MTierPvP.getInstance(), 20L, 20L);
        roomTasks.put(room.getId(), pvpTask);
    }

    public void endMatch(RoomManager.DuelRoom room, Player winner) {
        room.setPhase(RoomManager.Phase.POST_MATCH);
        BossBar bar = roomBars.get(room.getId());
        if (bar != null) {
            bar.setColor(BarColor.WHITE);
            bar.setTitle(winner == null ? "§7§lMATCH DRAW" : "§6§lWINNER: §f" + winner.getName());
            bar.setProgress(1.0);
        }

        Player p1 = Bukkit.getPlayer(room.getOwner().getUniqueId());
        Player p2 = room.getChallenger() != null ? Bukkit.getPlayer(room.getChallenger().getUniqueId()) : null;

        String resultMsg = winner == null ? "§7The match ended in a §lDRAW§7!" : "§6§l" + winner.getName() + " §ehas won the duel!";
        if (p1 != null) {
            p1.sendMessage("");
            p1.sendMessage("§6§lMTier §8» " + resultMsg);
            p1.sendTitle(winner == null ? "§7§lDRAW" : (winner.equals(p1) ? "§a§lVICTORY" : "§c§lDEFEAT"), "§eReturning in 10s...", 10, 60, 10);
        }
        if (p2 != null) {
            p2.sendMessage("");
            p2.sendMessage("§6§lMTier §8» " + resultMsg);
            p2.sendTitle(winner == null ? "§7§lDRAW" : (winner.equals(p2) ? "§a§lVICTORY" : "§c§lDEFEAT"), "§eReturning in 10s...", 10, 60, 10);
        }

        if (winner != null) {
            spawnWinEffect(winner);
        }

        submitMatchResults(room, winner);

        new BukkitRunnable() {
            int count = 10;
            @Override
            public void run() {
                if (count <= 0) {
                    cleanupMatch(room);
                    this.cancel();
                    return;
                }
                count--;
            }
        }.runTaskTimer(MTierPvP.getInstance(), 20L, 20L);
    }

    private void cleanupMatch(RoomManager.DuelRoom room) {
        Player p1 = Bukkit.getPlayer(room.getOwner().getUniqueId());
        Player p2 = room.getChallenger() != null ? Bukkit.getPlayer(room.getChallenger().getUniqueId()) : null;

        org.bukkit.World mainWorld = Bukkit.getWorlds().get(0);
        GameMode defaultMode = Bukkit.getDefaultGameMode();

        if (p1 != null) {
            p1.getInventory().clear();
            p1.setGameMode(defaultMode);
            p1.teleport(mainWorld.getSpawnLocation());
        }
        if (p2 != null) {
            p2.getInventory().clear();
            p2.setGameMode(defaultMode);
            p2.teleport(mainWorld.getSpawnLocation());
        }

        // Handle Spectators
        for (UUID specId : room.getSpectators()) {
            Player spec = Bukkit.getPlayer(specId);
            if (spec != null && spec.isOnline()) {
                spec.setGameMode(defaultMode);
                spec.teleport(mainWorld.getSpawnLocation());
                spec.sendMessage("§6§lMTier §8» §eThe match has ended. Returning to main world.");
            }
        }
        room.getSpectators().clear();

        String worldName = "match_" + room.getId().toString().substring(0, 8);
        MTierPvP.getInstance().getSlimeManager().unloadMatchWorld(worldName);
        MTierPvP.getInstance().getRoomManager().removeRoom(room.getId());
        cleanup(room.getId());
    }

    public void cleanup(UUID roomId) {
        BossBar bar = roomBars.remove(roomId);
        if (bar != null) bar.removeAll();
        BukkitRunnable task = roomTasks.remove(roomId);
        if (task != null) task.cancel();
    }

    private void spawnWinEffect(Player winner) {
        winner.playSound(winner.getLocation(), org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.5f, 1.0f);
        
        new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if (count >= 5 || !winner.isOnline()) {
                    this.cancel();
                    return;
                }
                
                org.bukkit.entity.Firework fw = winner.getWorld().spawn(winner.getLocation().add(0, 1, 0), org.bukkit.entity.Firework.class);
                org.bukkit.inventory.meta.FireworkMeta fwm = fw.getFireworkMeta();
                
                fwm.addEffect(org.bukkit.FireworkEffect.builder()
                    .withColor(org.bukkit.Color.ORANGE, org.bukkit.Color.YELLOW)
                    .withFade(org.bukkit.Color.WHITE)
                    .with(org.bukkit.FireworkEffect.Type.BALL_LARGE)
                    .trail(true)
                    .flicker(true)
                    .build());
                fwm.setPower(1);
                fw.setFireworkMeta(fwm);
                
                count++;
            }
        }.runTaskTimer(MTierPvP.getInstance(), 0L, 10L);
    }

    private void submitMatchResults(RoomManager.DuelRoom room, Player winner) {
        Player p1 = room.getOwner();
        Player p2 = room.getChallenger();
        if (p1 == null || p2 == null) return;

        // Get MMRs
        int mmr1 = 1000;
        int mmr2 = 1000;
        
        WebSyncManager.PlayerData data1 = MTierPlugin.getAPI().getCachedPlayerData(p1.getUniqueId());
        WebSyncManager.PlayerData data2 = MTierPlugin.getAPI().getCachedPlayerData(p2.getUniqueId());
        
        if (data1 != null && data1.stats().containsKey("PvP")) mmr1 = data1.stats().get("PvP").mmr();
        if (data2 != null && data2.stats().containsKey("PvP")) mmr2 = data2.stats().get("PvP").mmr();

        // Calculate ELO
        double expected1 = 1.0 / (1.0 + Math.pow(10, (mmr2 - mmr1) / 400.0));
        double expected2 = 1.0 / (1.0 + Math.pow(10, (mmr1 - mmr2) / 400.0));

        double score1 = (winner == null) ? 0.5 : (winner.equals(p1) ? 1.0 : 0.0);
        double score2 = (winner == null) ? 0.5 : (winner.equals(p2) ? 1.0 : 0.0);

        int change1 = (int) Math.round(32 * (score1 - expected1));
        int change2 = (int) Math.round(32 * (score2 - expected2));

        // Build MatchData
        MatchData.Builder builder = new MatchData.Builder("PvP")
            .setMatchType("SOLO")
            .setDuration(900 - room.getTurnRemaining());

        // Banned Items
        java.util.List<String> bannedStrings = room.getBannedItems().stream().map(Enum::name).collect(java.util.stream.Collectors.toList());
        builder.addGlobalMetadata("banned_items", bannedStrings);

        // Winner/Loser Loadouts
        java.util.List<String> p1Loadout = room.getOwnerLoadout().stream().map(Enum::name).collect(java.util.stream.Collectors.toList());
        java.util.List<String> p2Loadout = room.getChallengerLoadout().stream().map(Enum::name).collect(java.util.stream.Collectors.toList());

        builder.addParticipant(new MatchData.Participant(p1.getUniqueId(), p1.getName(), "TEAM1", (winner == null ? 1 : (winner.equals(p1) ? 1 : 2)), winner != null && winner.equals(p1))
            .addStat("mmr_change", change1)
            .addStat("loadout", p1Loadout));

        builder.addParticipant(new MatchData.Participant(p2.getUniqueId(), p2.getName(), "TEAM2", (winner == null ? 1 : (winner.equals(p2) ? 1 : 2)), winner != null && winner.equals(p2))
            .addStat("mmr_change", change2)
            .addStat("loadout", p2Loadout));

        // Submit
        MTierPlugin.getAPI().submitMatch(builder.build()).thenAccept(success -> {
            if (success) {
                p1.sendMessage("§6§lMTier §8» §7MMR Update: §f" + (change1 >= 0 ? "§a+" : "§c") + change1);
                p2.sendMessage("§6§lMTier §8» §7MMR Update: §f" + (change2 >= 0 ? "§a+" : "§c") + change2);
            }
        });
    }

    private String formatTime(int seconds) {
        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }
}
