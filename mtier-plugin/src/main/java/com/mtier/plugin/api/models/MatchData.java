package com.mtier.plugin.api.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a match result to be synced with the web.
 * Supports Solo, Teams, and FFA (Multi-player) modes.
 */
public class MatchData {
    private final String gamemode;
    private final String matchType;
    private final long startTime;
    private final long endTime;
    private final List<Participant> participants;
    private final Map<String, Object> globalMetadata;

    private MatchData(Builder builder) {
        this.gamemode = builder.gamemode;
        this.matchType = builder.matchType;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.participants = builder.participants;
        this.globalMetadata = builder.globalMetadata;
    }

    public String getGamemode() { return gamemode; }
    public String getMatchType() { return matchType; }
    public long getDuration() { return (endTime - startTime) / 1000; }
    public List<Participant> getParticipants() { return participants; }
    public Map<String, Object> getGlobalMetadata() { return globalMetadata; }

    public static class Builder {
        private final String gamemode;
        private String matchType = "SOLO";
        private long startTime = System.currentTimeMillis();
        private long endTime = System.currentTimeMillis();
        private final List<Participant> participants = new ArrayList<>();
        private final Map<String, Object> globalMetadata = new HashMap<>();

        public Builder(String gamemode) {
            this.gamemode = gamemode;
        }

        public Builder setMatchType(String type) {
            this.matchType = type;
            return this;
        }

        public Builder setDuration(long seconds) {
            this.startTime = System.currentTimeMillis() - (seconds * 1000);
            this.endTime = System.currentTimeMillis();
            return this;
        }

        public Builder addParticipant(UUID uuid, String name, String teamId, int placement, boolean winner) {
            this.participants.add(new Participant(uuid, name, teamId, placement, winner));
            return this;
        }

        public Builder addParticipant(Participant p) {
            this.participants.add(p);
            return this;
        }

        public Builder addGlobalMetadata(String key, Object value) {
            this.globalMetadata.put(key, value);
            return this;
        }

        public MatchData build() {
            return new MatchData(this);
        }
    }

    public static class Participant {
        private final UUID uuid;
        private final String name;
        private final String teamId;
        private final int placement;
        private final boolean winner;
        private final Map<String, Object> stats = new HashMap<>();

        public Participant(UUID uuid, String name, String teamId, int placement, boolean winner) {
            this.uuid = uuid;
            this.name = name;
            this.teamId = teamId;
            this.placement = placement;
            this.winner = winner;
        }

        public Participant addStat(String key, Object value) {
            this.stats.put(key, value);
            return this;
        }

        public UUID getUuid() { return uuid; }
        public String getName() { return name; }
        public String getTeamId() { return teamId; }
        public int getPlacement() { return placement; }
        public boolean isWinner() { return winner; }
        public Map<String, Object> getStats() { return stats; }
    }
}
