"use client";

import { useState } from "react";
import MmrChart from "./MmrChart";
import MatchHistory from "./MatchHistory";
import AchievementModal from "./AchievementModal";
import MatchModal from "./MatchModal";
import { getMcIcon } from "@/lib/minecraft-icons";

interface Match {
  id: string;
  gamemode: string;
  matchType: string;
  result: string;
  mmrChange: number;
  opponent: string;
  map: string | null;
  duration: number;
  detailsJson: string;
  createdAt: Date;
}

interface PlayerAnalyticsProps {
  player: {
    tiers: {
      gamemode: string;
      mmr: number;
    }[];
    matches: Match[];
    achievements: {
      id: string;
      name: string;
      description: string;
      icon: string;
    }[];
  };
}

export default function PlayerAnalytics({ player }: PlayerAnalyticsProps) {
  const initialMode = player.tiers.length > 0 ? player.tiers[0].gamemode : "";
  const [selectedMode, setSelectedMode] = useState(initialMode);
  const [isAchModalOpen, setIsAchModalOpen] = useState(false);
  const [selectedMatch, setSelectedMatch] = useState<Match | null>(null);

  const filteredMatches = player.matches.filter(m => m.gamemode === selectedMode);
  const currentTier = player.tiers.find(t => t.gamemode === selectedMode);
  const currentMmr = currentTier ? currentTier.mmr : 1000;
  const gamemodes = player.tiers.map(t => t.gamemode);

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div className="flex items-center gap-2 p-1.5 bg-zinc-900/50 border border-zinc-800 w-fit rounded-xl backdrop-blur-md">
            {gamemodes.map((gm) => (
              <button
                key={gm}
                onClick={() => setSelectedMode(gm)}
                className={`px-4 py-2 rounded-lg text-[10px] font-black transition-all uppercase tracking-widest ${
                  selectedMode === gm 
                    ? 'bg-orange-600 text-white shadow-lg shadow-orange-900/20' 
                    : 'text-zinc-500 hover:text-zinc-300'
                }`}
              >
                {gm} Sector
              </button>
            ))}
          </div>

          <button 
            onClick={() => setIsAchModalOpen(true)}
            className="flex items-center gap-3 px-4 py-2 rounded-xl bg-orange-500/5 border border-orange-500/10 hover:border-orange-500/30 transition-all group"
          >
            <div className="flex -space-x-2">
                {player.achievements.slice(0, 3).map((ach) => (
                    <div key={ach.id} className="w-6 h-6 rounded-full bg-zinc-900 border border-zinc-800 flex items-center justify-center p-1 group-hover:border-orange-500/50 transition-colors">
                        <img src={getMcIcon(ach.icon)} className="w-full h-full object-contain mc-icon" alt="" />
                    </div>
                ))}
            </div>
            <span className="text-[10px] font-black text-orange-500 uppercase tracking-widest">
                View {player.achievements.length} Badges
            </span>
          </button>
      </div>

      <div className="bento-card p-8 h-64 relative group overflow-hidden">
        <div className="absolute top-4 left-8 z-20">
          <h3 className="text-[10px] font-black text-zinc-600 uppercase tracking-[0.4em] mb-1">
            {selectedMode} MMR Projection
          </h3>
          <p className="text-xs font-black text-zinc-400 uppercase italic tracking-widest">
            Combat Effectiveness over time
          </p>
        </div>
        
        <div className="h-full pt-8 relative z-10">
          <MmrChart matches={filteredMatches} initialMmr={currentMmr} />
        </div>

        <div className="absolute top-4 right-8 flex items-center gap-2">
          <span className="text-[8px] font-black text-zinc-800 uppercase tracking-[0.5em]">Real-time Pulse</span>
          <div className="w-2 h-2 bg-red-500 rounded-full animate-pulse shadow-[0_0_8px_rgba(239,68,68,0.6)]"></div>
        </div>
      </div>

      <MatchHistory 
        matches={filteredMatches} 
        mode={selectedMode} 
        onMatchClick={(m) => setSelectedMatch(m)}
      />

      <AchievementModal 
        isOpen={isAchModalOpen} 
        onClose={() => setIsAchModalOpen(false)} 
        achievements={player.achievements}
        getIcon={getMcIcon}
      />

      <MatchModal
        isOpen={!!selectedMatch}
        onClose={() => setSelectedMatch(null)}
        match={selectedMatch}
      />
    </div>
  );
}
