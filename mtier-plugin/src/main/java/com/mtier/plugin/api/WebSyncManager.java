package com.mtier.plugin.api;

import com.google.gson.Gson;
import com.mtier.plugin.MTierPlugin;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class WebSyncManager {

    public record PlayerData(Map<String, GamemodeStats> stats) {}
    public record GamemodeStats(int mmr, String rank) {}

    private final HttpClient httpClient;
    private final Gson gson;
    private final String apiUrl;
    private final String apiKey;

    public WebSyncManager() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
        this.apiUrl = MTierPlugin.getInstance().getConfig().getString("api.url");
        this.apiKey = MTierPlugin.getInstance().getConfig().getString("api.key");
    }

    public CompletableFuture<Void> syncPlayerData(String uuid, String username, String eventType, Map<String, Object> metadata) {
        MTierPlugin.getInstance().getLogger().info("Attempting to sync " + eventType + " for " + username + " to " + apiUrl);
        
        Map<String, Object> payload = Map.of(
            "uuid", uuid,
            "username", username,
            "event", eventType,
            "metadata", metadata,
            "timestamp", System.currentTimeMillis()
        );

        String json = gson.toJson(payload);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        MTierPlugin.getInstance().getLogger().info("Successfully synced " + username + " (Status: " + response.statusCode() + ")");
                    } else {
                        MTierPlugin.getInstance().getLogger().warning("Failed to sync " + username + ". Server returned Status: " + response.statusCode() + " - Body: " + response.body());
                    }
                })
                .exceptionally(ex -> {
                    MTierPlugin.getInstance().getLogger().severe("Network Error while syncing " + username + ": " + ex.getMessage());
                    if (ex.getCause() != null) {
                        MTierPlugin.getInstance().getLogger().severe("Cause: " + ex.getCause().getMessage());
                    }
                    return null;
                });
        } catch (Exception e) {
            MTierPlugin.getInstance().getLogger().severe("Configuration Error: API URL might be invalid: " + apiUrl);
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<PlayerData> fetchPlayerData(String uuid, String username) {
        Map<String, Object> payload = Map.of(
            "uuid", uuid,
            "username", username,
            "event", "GET_STATS",
            "timestamp", System.currentTimeMillis()
        );

        String json = gson.toJson(payload);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        return gson.fromJson(response.body(), PlayerData.class);
                    }
                    return null;
                });
        } catch (Exception e) {
            return CompletableFuture.completedFuture(null);
        }
    }

    public CompletableFuture<Boolean> updatePlayerMMR(String targetName, String mode, int mmr) {
        Map<String, Object> payload = Map.of(
            "username", targetName,
            "mode", mode,
            "mmr", mmr,
            "event", "UPDATE_MMR",
            "timestamp", System.currentTimeMillis()
        );

        String json = gson.toJson(payload);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> response.statusCode() == 200);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(false);
        }
    }
}
