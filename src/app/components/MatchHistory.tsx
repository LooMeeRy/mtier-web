import { formatDistanceToNow } from "date-fns";

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

interface MatchHistoryProps {
  matches: Match[];
  mode: string;
  onMatchClick: (match: Match) => void;
}

export default function MatchHistory({ matches, mode, onMatchClick }: MatchHistoryProps) {
  const formatDuration = (s: number) => `${Math.floor(s / 60)}m ${s % 60}s`;

  return (
    <div className="bento-card overflow-hidden">
      <div className="px-8 py-6 border-b border-zinc-800/50 flex items-center justify-between">
        <h3 className="text-[10px] font-black text-zinc-600 uppercase tracking-[0.4em]">Combat Logs • {mode} Sector</h3>
        <span className="px-2 py-1 rounded bg-orange-500/10 text-orange-500 text-[8px] font-black uppercase tracking-widest border border-orange-500/20">LIVE DATA</span>
      </div>
      <div className="overflow-x-auto">
        <table className="w-full text-left border-collapse">
          <tbody className="divide-y divide-zinc-800/30">
            {matches.map((match) => (
              <tr 
                key={match.id} 
                onClick={() => onMatchClick(match)}
                className="hover:bg-white/[0.02] transition-all group cursor-pointer active:bg-white/[0.04]"
              >
                <td className="px-8 py-6">
                  <div className="flex items-center gap-4">
                    <div className={`w-1.5 h-10 rounded-full ${match.result === 'WIN' ? 'bg-emerald-500 shadow-[0_0_15px_rgba(16,185,129,0.4)]' : 'bg-red-500 shadow-[0_0_15px_rgba(239,68,68,0.4)]'}`}></div>
                    <div>
                      <p className={`text-base font-black italic tracking-tighter uppercase ${match.result === 'WIN' ? 'text-emerald-400' : 'text-red-400'}`}>
                        {match.result === 'WIN' ? 'Victory' : 'Defeat'}
                      </p>
                      <p className="text-[9px] font-black text-zinc-600 uppercase tracking-widest mt-0.5">
                        {match.map || (mode === 'Survival' ? 'Wilderness' : 'Arena 01')}
                      </p>
                    </div>
                  </div>
                </td>
                
                <td className="px-8 py-6">
                    <p className="text-[9px] font-black text-zinc-700 uppercase tracking-widest mb-1">Combatant</p>
                    <div className="flex items-center gap-2">
                        <img 
                            src={`https://mc-heads.net/avatar/${match.opponent || 'Steve'}/16`} 
                            className="w-4 h-4 rounded-sm bg-zinc-900" 
                            alt="" 
                            onError={(e) => {
                                (e.target as HTMLImageElement).src = 'https://crafatar.com/avatars/Steve?size=16';
                            }}
                        />
                        <p className="text-xs font-black text-zinc-300 italic uppercase tracking-tighter">{match.opponent || 'Unknown'}</p>
                    </div>
                </td>

                <td className="px-8 py-6">
                    <p className="text-[9px] font-black text-zinc-700 uppercase tracking-widest mb-1">Squad Type</p>
                    <p className="text-xs font-black text-orange-500 italic uppercase tracking-tighter">{match.matchType}</p>
                </td>

                <td className="px-8 py-6">
                  <p className="text-[9px] font-black text-zinc-700 uppercase tracking-widest mb-1">Duration</p>
                  <p className="text-xs font-black text-zinc-300 italic uppercase">{formatDuration(match.duration)}</p>
                </td>

                <td className="px-8 py-6">
                   <div className="flex flex-col">
                    <p className="text-[9px] font-black text-zinc-700 uppercase tracking-widest mb-1">Impact</p>
                    <span className={`text-sm font-mono font-black italic ${match.mmrChange >= 0 ? 'text-emerald-500' : 'text-red-500'}`}>
                        {match.mmrChange >= 0 ? `+${match.mmrChange}` : match.mmrChange} <span className="text-[10px] opacity-50 uppercase">mmr</span>
                    </span>
                   </div>
                </td>

                <td className="px-8 py-6 text-right">
                  <p className="text-[10px] font-black text-zinc-700 uppercase tracking-widest mb-1">Date</p>
                  <p className="text-[11px] font-black text-zinc-500 uppercase tracking-tighter italic">
                    {new Date(match.createdAt).toLocaleDateString()}
                  </p>
                </td>
              </tr>
            ))}
            {matches.length === 0 && (
              <tr>
                <td className="px-8 py-20 text-center text-zinc-800 font-black italic uppercase tracking-[0.5em] text-xs">
                  Zero Combat Records Encrypted
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
