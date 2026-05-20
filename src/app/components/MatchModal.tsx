"use client";

import { getMcIcon } from "@/lib/minecraft-icons";

interface BridgeDetail {
  name: string;
  kills: number;
  deaths: number;
  goals: number;
}

interface PvpStats {
  accuracy: string;
  totalHits: number;
  damageDealt: string;
  avgCps: number;
  healthRemaining: string;
}

interface Match {
  id: string;
  gamemode: string;
  matchType: string;
  result: string;
  mmrChange: number;
  duration: number;
  detailsJson: string;
  createdAt: Date;
}

interface MatchModalProps {
  isOpen: boolean;
  onClose: () => void;
  match: Match | null;
}

export default function MatchModal({ isOpen, onClose, match }: MatchModalProps) {
  if (!isOpen || !match) return null;

  let details: any = {};
  try {
    details = JSON.parse(match.detailsJson);
  } catch (e) {
    console.error("Parse Error");
  }

  const formatDuration = (s: number) => `${Math.floor(s / 60)}m ${s % 60}s`;

  return (
    <div className="fixed inset-0 z-[100] flex items-center justify-center p-4">
      <div className="absolute inset-0 bg-black/90 backdrop-blur-xl transition-opacity animate-in fade-in duration-300" onClick={onClose} />
      
      <div className="relative w-full max-w-4xl bg-[#0d0d0f] border border-zinc-800 rounded-3xl shadow-2xl overflow-hidden animate-in zoom-in-95 duration-300 flex flex-col max-h-[90vh]">
        {/* Header */}
        <div className="px-8 py-8 border-b border-zinc-800/50 bg-gradient-to-b from-zinc-900/50 to-transparent">
          <div className="flex justify-between items-start mb-6">
            <div className="space-y-1">
                <div className="flex items-center gap-2">
                    <img src={getMcIcon(match.gamemode)} className="w-4 h-4 object-contain mc-icon" alt="" />
                    <span className="text-[10px] font-black text-zinc-600 uppercase tracking-[0.4em]">{match.gamemode} OPERATION</span>
                </div>
                <h2 className="text-4xl font-black italic tracking-tighter text-white uppercase font-display">
                    {match.result === 'WIN' ? <span className="text-emerald-500">VICTORY</span> : <span className="text-red-500">DEFEAT</span>}
                </h2>
            </div>
            <button onClick={onClose} className="p-2 rounded-full hover:bg-zinc-800 transition-colors text-zinc-500">
                <svg xmlns="http://www.w3.org/2000/svg" className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                </svg>
            </button>
          </div>
          
          <div className="grid grid-cols-2 md:grid-cols-4 gap-8">
            <div>
                <p className="text-[9px] font-black text-zinc-700 uppercase tracking-widest mb-1">Status</p>
                <p className="text-2xl font-black text-zinc-100 italic tracking-tighter uppercase">{match.matchType}</p>
            </div>
            <div>
                <p className="text-[9px] font-black text-zinc-700 uppercase tracking-widest mb-1">Time Elapsed</p>
                <p className="text-2xl font-black text-zinc-100 italic tracking-tighter">{formatDuration(match.duration)}</p>
            </div>
            <div>
                <p className="text-[9px] font-black text-zinc-700 uppercase tracking-widest mb-1">Impact</p>
                <p className={`text-2xl font-black italic tracking-tighter ${match.mmrChange >= 0 ? 'text-emerald-500' : 'text-red-500'}`}>
                    {match.mmrChange >= 0 ? `+${match.mmrChange}` : match.mmrChange} <span className="text-xs opacity-50">MMR</span>
                </p>
            </div>
            <div>
                <p className="text-[9px] font-black text-zinc-700 uppercase tracking-widest mb-1">Sector</p>
                <p className="text-2xl font-black text-orange-500 italic tracking-tighter uppercase">{match.gamemode}</p>
            </div>
          </div>
        </div>

        {/* Content - Switch based on mode */}
        <div className="flex-1 overflow-y-auto p-8">
            {match.gamemode === 'Bridge' ? (
                <BridgeDetails details={details} />
            ) : (
                <PvpDetails details={details} />
            )}
        </div>

        <div className="p-8 border-t border-zinc-800/50 flex justify-between items-center bg-zinc-900/10">
            <div className="flex flex-col gap-1">
                <span className="text-[9px] font-black text-zinc-700 uppercase tracking-[0.5em]">Session Hash: {match.id.substring(0, 8)}</span>
                <span className="text-[7px] font-black text-zinc-800 uppercase tracking-widest opacity-30">Deployment: V3.1.STABLE_SYNC</span>
            </div>
            <span className="text-[9px] font-black text-zinc-700 uppercase tracking-widest">{new Date(match.createdAt).toLocaleString()}</span>
        </div>
      </div>
    </div>
  );
}

// --- Specialized Sub-Components ---

function BridgeDetails({ details }: { details: any }) {
    const allies = (details.allies || []) as BridgeDetail[];
    const enemies = (details.enemies || []) as BridgeDetail[];
    
    return (
        <div className="space-y-12">
            <div className="text-center bg-zinc-900/20 p-6 rounded-3xl border border-zinc-800/50">
                <p className="text-[9px] font-black text-zinc-700 uppercase tracking-[0.5em] mb-2">Final Scoreline</p>
                <p className="text-5xl font-black italic tracking-tighter text-white font-display">{details.score || '0 - 0'}</p>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-12">
                <div className="space-y-6">
                    <span className="text-[10px] font-black text-emerald-500 uppercase tracking-widest border-l-2 border-emerald-500 pl-4">Team Alpha</span>
                    <div className="space-y-3">
                        {allies.map((p, i) => <BridgePlayerRow key={i} player={p} color="emerald" />)}
                    </div>
                </div>
                <div className="space-y-6">
                    <span className="text-[10px] font-black text-red-500 uppercase tracking-widest border-l-2 border-red-500 pl-4">Hostile Squad</span>
                    <div className="space-y-3">
                        {enemies.map((p, i) => <BridgePlayerRow key={i} player={p} color="red" />)}
                    </div>
                </div>
            </div>
        </div>
    )
}

function PvpDetails({ details }: { details: any }) {
    const bannedItems = (details.metadata?.banned_items || []) as string[];
    const participants = (details.allParticipants || []) as any[];
    const winner = participants.find(p => p.winner);
    const loser = participants.find(p => !p.winner);

    return (
        <div className="space-y-12">
            {/* Banned Items Row */}
            {bannedItems.length > 0 && (
                <div className="space-y-4">
                    <div className="flex items-center gap-3">
                        <span className="text-[10px] font-black text-red-500 uppercase tracking-widest pl-4 border-l-2 border-red-500">Protocol Restrictions (Banned)</span>
                    </div>
                    <div className="flex flex-wrap gap-2 px-4 py-6 rounded-3xl bg-red-500/[0.02] border border-red-500/10">
                        {bannedItems.map((item, i) => (
                            <div key={i} className="w-10 h-10 flex items-center justify-center bg-zinc-900 border border-zinc-800 rounded-lg group hover:border-red-500/30 transition-all p-2">
                                <img src={getMcIcon(item)} className="w-full h-full object-contain mc-icon opacity-60" title={item} alt="" />
                            </div>
                        ))}
                    </div>
                </div>
            )}

            {/* Loadouts Row */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-12">
                {/* Winner Loadout */}
                <div className="space-y-6">
                    <span className="text-[10px] font-black text-emerald-500 uppercase tracking-widest border-l-2 border-emerald-500 pl-4">Tactical Arsenal » {winner?.name || 'Winner'}</span>
                    <div className="grid grid-cols-6 gap-2 p-6 rounded-3xl bg-emerald-500/[0.02] border border-emerald-500/10 min-h-[160px]">
                        {winner?.loadout?.length > 0 ? winner.loadout.map((item: string, i: number) => (
                            <div key={i} className="aspect-square flex items-center justify-center bg-zinc-900 border border-zinc-800 rounded-xl p-2 group hover:border-emerald-500/20 transition-all shadow-inner">
                                <img src={getMcIcon(item)} className="w-full h-full object-contain mc-icon" title={item} alt="" />
                            </div>
                        )) : (
                            <div className="col-span-6 flex items-center justify-center text-[9px] font-black text-zinc-800 uppercase italic">No gear data encrypted</div>
                        )}
                    </div>
                </div>

                {/* Loser Loadout */}
                <div className="space-y-6">
                    <span className="text-[10px] font-black text-zinc-500 uppercase tracking-widest border-l-2 border-zinc-700 pl-4">Engaged Arsenal » {loser?.name || 'Loser'}</span>
                    <div className="grid grid-cols-6 gap-2 p-6 rounded-3xl bg-zinc-900/30 border border-zinc-800/50 min-h-[160px]">
                        {loser?.loadout?.length > 0 ? loser.loadout.map((item: string, i: number) => (
                            <div key={i} className="aspect-square flex items-center justify-center bg-zinc-900 border border-zinc-800 rounded-xl p-2 group hover:border-zinc-500/20 transition-all opacity-80">
                                <img src={getMcIcon(item)} className="w-full h-full object-contain mc-icon" title={item} alt="" />
                            </div>
                        )) : (
                            <div className="col-span-6 flex items-center justify-center text-[9px] font-black text-zinc-800 uppercase italic">No gear data encrypted</div>
                        )}
                    </div>
                </div>
            </div>

            {/* Engagement Status */}
            <div className="bento-card p-6 border-zinc-800/50 flex items-center justify-between">
                <div className="flex items-center gap-6">
                    <div className="w-16 h-16 rounded-2xl bg-zinc-900 border border-zinc-800 overflow-hidden flex items-center justify-center">
                        <img src={`https://minotar.net/helm/${loser?.name || 'Steve'}/64`} className="object-contain w-[75%] h-[70%]" alt="" />
                    </div>
                    <div>
                        <p className="text-[10px] font-black text-zinc-600 uppercase tracking-widest mb-1">Target Neutralized</p>
                        <p className="text-2xl font-black text-white italic tracking-tighter uppercase">{loser?.name || 'Unknown Entity'}</p>
                        <p className="text-[9px] font-black text-zinc-700 uppercase tracking-widest mt-1 italic">Protocol 1v1 Execution Verified</p>
                    </div>
                </div>
                <img src={getMcIcon('Iron_Sword')} className="w-8 h-8 object-contain mc-icon opacity-20" alt="" />
            </div>
        </div>
    )
}

function BridgePlayerRow({ player, color }: { player: BridgeDetail, color: string }) {
    return (
        <div className="p-4 rounded-2xl bg-zinc-900/30 border border-zinc-800/50 flex items-center justify-between group hover:bg-zinc-900/50 transition-all">
            <div className="flex items-center gap-4">
                <img src={`https://minotar.net/helm/${player.name}/40`} className="w-10 h-10 rounded-lg bg-zinc-800 border border-zinc-700" alt="" />
                <div>
                    <p className="text-sm font-black text-zinc-100 tracking-tighter italic uppercase">{player.name}</p>
                </div>
            </div>
            <div className="flex gap-6 text-right">
                <SmallStat value={player.kills} label="K" />
                <SmallStat value={player.deaths} label="D" />
                <SmallStat value={player.goals} label="G" />
            </div>
        </div>
    )
}

function PvpStatCard({ label, value, icon }: { label: string, value: any, icon: string }) {
    return (
        <div className="bento-card p-6 flex items-center gap-6 group hover:border-orange-500/20 transition-all">
            <div className="w-12 h-12 shrink-0 rounded-xl bg-zinc-900 border border-zinc-800 flex items-center justify-center p-2 shadow-inner group-hover:border-orange-500/10 transition-colors">
                <img src={getMcIcon(icon)} className="w-full h-full object-contain mc-icon opacity-50 group-hover:opacity-100 transition-opacity" alt="" />
            </div>
            <div>
                <p className="text-[9px] font-black text-zinc-700 uppercase tracking-widest mb-0.5">{label}</p>
                <p className="text-xl font-black text-zinc-100 italic tracking-tighter">{value}</p>
            </div>
        </div>
    )
}

function SmallStat({ value, label }: { value: number, label: string }) {
    return (
        <div>
            <p className="text-[8px] font-black text-zinc-700 uppercase tracking-tighter mb-0.5">{label}</p>
            <p className="text-sm font-mono font-black text-zinc-400">{value}</p>
        </div>
    )
}

function Stat({ value, label }: { value: number, label: string }) {
    return (
        <div>
            <p className="text-[8px] font-black text-zinc-700 uppercase tracking-tighter mb-0.5">{label}</p>
            <p className="text-sm font-mono font-black text-zinc-400">{value}</p>
        </div>
    )
}
