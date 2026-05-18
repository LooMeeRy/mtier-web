package com.mtier.plugin.api;

import com.google.gson.Gson;
import com.mtier.plugin.MTierPlugin;
import com.mtier.plugin.api.models.MatchData;
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
        Map<String, Object> payload = Map.of(
            "uuid", uuid,
            "username", username,
            "event", eventType,
            "metadata", metadata,
            "timestamp", System.currentTimeMillis()
        );

        return sendAsyncRequest(payload).thenAccept(res -> {});
    }

    public CompletableFuture<PlayerData> fetchPlayerData(String uuid, String username) {
        Map<String, Object> payload = Map.of(
            "uuid", uuid,
            "username", username,
            "event", "GET_STATS",
            "timestamp", System.currentTimeMillis()
        );

        return sendAsyncRequest(payload).thenApply(body -> {
            if (body != null) return gson.fromJson(body, PlayerData.class);
            return null;
        });
    }

    public CompletableFuture<Boolean> updatePlayerMMR(String targetName, String mode, int mmr) {
        Map<String, Object> payload = Map.of(
            "username", targetName,
            "mode", mode,
            "mmr", mmr,
            "event", "UPDATE_MMR",
            "timestamp", System.currentTimeMillis()
        );

        return sendAsyncRequest(payload).thenApply(body -> body != null);
    }

    /**
     * New method for submitting complex match results.
     */
    public CompletableFuture<Boolean> submitMatch(MatchData match) {
        Map<String, Object> payload = Map.of(
            "event", "SUBMIT_MATCH",
            "gamemode", match.getGamemode(),
            "matchType", match.getMatchType(),
            "duration", match.getDuration(),
            "participants", match.getParticipants(),
            "metadata", match.getGlobalMetadata(),
            "timestamp", System.currentTimeMillis()
        );

        return sendAsyncRequest(payload).thenApply(body -> body != null);
    }

    private CompletableFuture<String> sendAsyncRequest(Object payload) {
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
                    if (response.statusCode() == 200) return response.body();
                    MTierPlugin.getInstance().getLogger().warning("API Error (" + response.statusCode() + "): " + response.body());
                    return null;
                })
                .exceptionally(ex -> {
                    MTierPlugin.getInstance().getLogger().severe("Network Error: " + ex.getMessage());
                    return null;
                });
        } catch (Exception e) {
            return CompletableFuture.completedFuture(null);
        }
    }
}
