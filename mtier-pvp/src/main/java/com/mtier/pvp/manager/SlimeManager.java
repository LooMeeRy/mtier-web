package com.mtier.pvp.manager;

import com.infernalsuite.aswm.api.AdvancedSlimePaperAPI;
import com.infernalsuite.aswm.api.world.SlimeWorld;
import com.infernalsuite.aswm.api.world.properties.SlimePropertyMap;
import com.mtier.pvp.MTierPvP;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SlimeManager {

    private final AdvancedSlimePaperAPI slimeAPI;
    private final String templateWorld = "pvp_temp";

    public SlimeManager() {
        this.slimeAPI = AdvancedSlimePaperAPI.instance();
    }

    public CompletableFuture<World> createMatchWorld(UUID roomId) {
        String worldName = "match_" + roomId.toString().substring(0, 8);
        CompletableFuture<World> future = new CompletableFuture<>();

        MTierPvP.getInstance().getLogger().info("§e[SlimeManager] Attempting to create match world: " + worldName);

        Bukkit.getScheduler().runTask(MTierPvP.getInstance(), () -> {
            try {
                // Find a valid loader
                com.infernalsuite.aswm.api.loaders.SlimeLoader loader = null;

                // Priority 1: User specified path
                java.io.File customDir = new java.io.File("/run/media/loomeery/ruby/M-Server/MTier/slime_worlds");
                java.io.File slimeFile = new java.io.File(customDir, templateWorld + ".slime");
                
                if (slimeFile.exists()) {
                    long size = slimeFile.length();
                    MTierPvP.getInstance().getLogger().info("§a[SlimeManager] FOUND SLIME FILE: " + slimeFile.getAbsolutePath() + " (" + size + " bytes)");
                    if (size < 500) {
                        MTierPvP.getInstance().getLogger().warning("§c[SlimeManager] WARNING: File size is extremely small (" + size + " bytes). This world is likely EMPTY!");
                    }
                    if (!slimeFile.canRead()) {
                        MTierPvP.getInstance().getLogger().severe("§c[SlimeManager] CANNOT READ SLIME FILE! Permission issue?");
                    }
                } else {
                    MTierPvP.getInstance().getLogger().severe("§c[SlimeManager] SLIME FILE NOT FOUND AT: " + slimeFile.getAbsolutePath());
                }

                if (customDir.exists() && customDir.isDirectory()) {
                    String[] files = customDir.list();
                    if (files != null) {
                        MTierPvP.getInstance().getLogger().info("§d[SlimeManager] Files in slime_worlds: " + String.join(", ", files));
                    }
                    try {
                        Class<?> fileLoaderClass = Class.forName("com.infernalsuite.aswm.loaders.file.FileLoader");
                        loader = (com.infernalsuite.aswm.api.loaders.SlimeLoader) fileLoaderClass.getConstructor(java.io.File.class).newInstance(customDir);
                        MTierPvP.getInstance().getLogger().info("§a[SlimeManager] FileLoader initialized for custom path.");
                    } catch (Exception e) {
                        MTierPvP.getInstance().getLogger().warning("§c[SlimeManager] Failed to init FileLoader via reflection: " + e.getMessage());
                    }
                }

                // Priority 2: Try to find already loaded template world
                if (loader == null) {
                    for (SlimeWorld sw : slimeAPI.getLoadedWorlds()) {
                        if (sw.getName().equals(templateWorld)) {
                            loader = sw.getLoader();
                            MTierPvP.getInstance().getLogger().info("§a[SlimeManager] Found '" + templateWorld + "' already loaded in server. Using its loader.");
                            break;
                        }
                    }
                }

                if (loader == null) {
                    // Priority 2: Try to get it from services manager
                    var registrations = Bukkit.getServicesManager().getRegistrations(com.infernalsuite.aswm.api.loaders.SlimeLoader.class);
                    for (var reg : registrations) {
                        com.infernalsuite.aswm.api.loaders.SlimeLoader potentialLoader = reg.getProvider();
                        try {
                            if (potentialLoader.listWorlds().contains(templateWorld)) {
                                loader = potentialLoader;
                                break;
                            }
                        } catch (Exception ignored) {}
                    }
                }

                if (loader == null && !slimeAPI.getLoadedWorlds().isEmpty()) {
                    loader = slimeAPI.getLoadedWorlds().iterator().next().getLoader();
                }

                if (loader == null) {
                    MTierPvP.getInstance().getLogger().severe("§c[SlimeManager] FATAL: Could not find any SlimeLoader! Map loading will fail.");
                    future.complete(null);
                    return;
                }

                MTierPvP.getInstance().getLogger().info("§e[SlimeManager] Selected Loader: " + loader.getClass().getName());
                
                try {
                    java.util.List<String> worlds = loader.listWorlds();
                    MTierPvP.getInstance().getLogger().info("§e[SlimeManager] Worlds found in loader: " + String.join(", ", worlds));
                    if (!worlds.contains(templateWorld)) {
                         MTierPvP.getInstance().getLogger().severe("§c[SlimeManager] ERROR: '" + templateWorld + "' NOT FOUND in this loader!");
                    }
                } catch (Exception e) {
                    MTierPvP.getInstance().getLogger().warning("§c[SlimeManager] Failed to list worlds: " + e.getMessage());
                }

                MTierPvP.getInstance().getLogger().info("§e[SlimeManager] Reading template: " + templateWorld);
                SlimeWorld world = slimeAPI.readWorld(loader, templateWorld, false, new SlimePropertyMap());
                
                if (world == null) {
                    MTierPvP.getInstance().getLogger().severe("§c[SlimeManager] readWorld returned NULL for '" + templateWorld + "'");
                    future.complete(null);
                    return;
                }

                MTierPvP.getInstance().getLogger().info("§e[SlimeManager] Template loaded. Cloning to: " + worldName);
                SlimeWorld clonedWorld = world.clone(worldName);
                
                MTierPvP.getInstance().getLogger().info("§e[SlimeManager] Loading cloned world (No Store)...");
                slimeAPI.loadWorld(clonedWorld, false);
                
                // Wait 1 tick for Bukkit to register it properly
                Bukkit.getScheduler().runTask(MTierPvP.getInstance(), () -> {
                    World bukkitWorld = Bukkit.getWorld(worldName);
                    if (bukkitWorld != null) {
                        MTierPvP.getInstance().getLogger().info("§a[SlimeManager] World " + worldName + " is now in Bukkit.");
                        
                        // Set difficulty to Normal
                        bukkitWorld.setDifficulty(org.bukkit.Difficulty.NORMAL);
                        
                        // Disable natural mob spawning for this world
                        bukkitWorld.setGameRule(org.bukkit.GameRule.DO_MOB_SPAWNING, false);
                        MTierPvP.getInstance().getLogger().info("§e[SlimeManager] Difficulty set to NORMAL and mob spawning disabled.");

                        // Clear any existing entities (mobs) in the world
                        for (org.bukkit.entity.Entity entity : bukkitWorld.getEntities()) {
                            if (entity instanceof org.bukkit.entity.Mob) {
                                entity.remove();
                            }
                        }
                        
                        // Force load chunks at spawn points
                        bukkitWorld.getChunkAt(-26 >> 4, -25 >> 4).load();
                        bukkitWorld.getChunkAt(41 >> 4, 46 >> 4).load();
                        
                        // Diagnostic check: Scan Y axis for blocks (Wide Area)
                        MTierPvP.getInstance().getLogger().info("§d[SlimeManager] Starting 10x10 Area Scan around spawn points...");
                        boolean foundAny = false;
                        for (int x = -31; x <= -21; x++) {
                            for (int z = -30; z <= -20; z++) {
                                for (int y = 0; y < 256; y++) {
                                    org.bukkit.Material mat = bukkitWorld.getBlockAt(x, y, z).getType();
                                    if (mat != org.bukkit.Material.AIR && mat != org.bukkit.Material.VOID_AIR && mat != org.bukkit.Material.CAVE_AIR) {
                                        MTierPvP.getInstance().getLogger().info("§d[SlimeManager] FOUND BLOCK at " + x + ", " + y + ", " + z + ": " + mat);
                                        foundAny = true;
                                        // Stop after finding first 5 blocks to avoid log spam
                                        if (y > 200) break; 
                                    }
                                }
                            }
                        }
                        if (!foundAny) {
                            MTierPvP.getInstance().getLogger().warning("§c[SlimeManager] NO BLOCKS FOUND! Generating emergency floor to prevent void fall...");
                            // Generate 3x3 emergency glass floor at both spawn points
                            int[][] spawns = {{-26, -25}, {41, 46}};
                            for (int[] spawn : spawns) {
                                for (int ex = spawn[0] - 1; ex <= spawn[0] + 1; ex++) {
                                    for (int ez = spawn[1] - 1; ez <= spawn[1] + 1; ez++) {
                                        bukkitWorld.getBlockAt(ex, 62, ez).setType(org.bukkit.Material.GLASS);
                                    }
                                }
                            }
                        }
                        
                        future.complete(bukkitWorld);
                    } else {
                        MTierPvP.getInstance().getLogger().severe("§c[SlimeManager] Bukkit.getWorld(\"" + worldName + "\") returned NULL!");
                        future.complete(null);
                    }
                });

            } catch (Exception e) {
                MTierPvP.getInstance().getLogger().severe("§c[SlimeManager] CRITICAL FAILURE: " + e.getMessage());
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    public void unloadMatchWorld(String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            Bukkit.unloadWorld(world, false);
            MTierPvP.getInstance().getLogger().info("§7[SlimeManager] World " + worldName + " unloaded.");
        }
    }
}
