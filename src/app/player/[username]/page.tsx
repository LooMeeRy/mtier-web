import Link from "next/link";
import { getPlayerWithTiers } from "@/app/actions/player";
import { formatRank } from "@/app/leaderboard/page";
import SearchBar from "@/app/components/SearchBar";
import SkinViewer from "@/app/components/SkinViewer";
import PlayerAnalytics from "@/app/components/PlayerAnalytics";
import { getMcIcon } from "@/lib/minecraft-icons";

export const dynamic = 'force-dynamic';

export default async function PlayerProfile({ 
  params,
  searchParams 
}: { 
  params: Promise<{ username: string }>,
  searchParams: Promise<{ fromMode?: string }>
}) {
  const { username } = await params;
  const { fromMode } = await searchParams;
  const player = await getPlayerWithTiers(username);

  const backLink = fromMode ? `/leaderboard?mode=${fromMode}` : "/";
  const backLabel = fromMode ? `BACK TO ${fromMode.toUpperCase()}` : "RETURN TO HUB";

  return (
    <div className="min-h-screen p-4 md:p-12 bg-[#09090b] relative overflow-hidden text-zinc-100">
      <div className="absolute top-0 right-0 w-[500px] h-[500px] bg-orange-500/5 blur-[150px] rounded-full"></div>
      
      <div className="max-w-6xl mx-auto space-y-8 relative z-10">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
            <Link href={backLink} className="group inline-flex items-center gap-2 px-3 py-1.5 rounded-lg bg-zinc-900/50 border border-zinc-800 text-zinc-500 hover:text-white hover:border-zinc-700 transition-all font-bold text-[10px] tracking-widest uppercase">
                <svg xmlns="http://www.w3.org/2000/svg" className="w-3 h-3 transition-transform group-hover:-translate-x-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={3} d="M10 19l-7-7m0 0l7-7m-7 7h18" />
                </svg>
                {backLabel}
            </Link>
            <SearchBar />
        </div>

        {!player ? (
          <div className="bento-card p-20 text-center space-y-6">
            <h2 className="text-3xl font-black text-white italic uppercase tracking-tighter">Identity Not Found</h2>
            <Link href="/" className="inline-block bg-white text-black px-8 py-3 rounded-xl font-black hover:bg-zinc-200 transition-colors text-xs uppercase tracking-widest">Retry Search</Link>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-12 gap-6">
            <div className="md:col-span-4 space-y-6">
              <div className="bento-card p-8 text-center space-y-8 relative overflow-hidden">
                <div className="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-transparent via-orange-500/50 to-transparent"></div>
                <div className="relative inline-block group">
                  <div className="absolute -inset-10 bg-orange-500/10 rounded-full blur-[80px] opacity-0 group-hover:opacity-100 transition-opacity duration-1000"></div>
                  <div className="relative w-56 h-80 bg-[#16161a]/40 rounded-2xl overflow-hidden border border-zinc-800/50 shadow-2xl mx-auto transition-all duration-500 group-hover:border-orange-500/30 backdrop-blur-xl group-hover:shadow-orange-900/10">
                    <SkinViewer username={username} uuid={player.uuid} />
                  </div>
                </div>
                <div className="space-y-1">
                    <h1 className="text-4xl font-black tracking-tighter text-white break-words italic font-display">{player.username}</h1>
                    <div className="flex items-center justify-center gap-2">
                        <span className="w-1.5 h-1.5 bg-orange-500 rounded-full animate-pulse"></span>
                        <span className="text-[9px] font-black text-zinc-600 uppercase tracking-[0.2em]">Verified Registry</span>
                    </div>
                </div>
                <div className="pt-6 border-t border-zinc-800/50 grid grid-cols-2 gap-4 text-center">
                    <div className="flex flex-col items-center gap-2">
                        <img src={getMcIcon('Experience_Bottle')} className="w-6 h-6 object-contain mc-icon opacity-80" alt="" />
                        <div>
                            <p className="text-[8px] font-black text-zinc-700 uppercase tracking-tighter mb-0.5">Authorization</p>
                            <p className="text-xs font-black text-orange-500 italic">Lvl 4</p>
                        </div>
                    </div>
                    <div className="flex flex-col items-center gap-2">
                        <img src={getMcIcon('Empty_Map')} className="w-6 h-6 object-contain mc-icon opacity-80" alt="" />
                        <div>
                            <p className="text-[8px] font-black text-zinc-700 uppercase tracking-tighter mb-0.5">Assigned Sector</p>
                            <p className="text-xs font-black text-zinc-300 italic">Global</p>
                        </div>
                    </div>
                </div>
              </div>
              <div className="bento-card p-6 space-y-4">
                <div className="flex items-center justify-between">
                    <h3 className="text-[10px] font-black text-zinc-700 uppercase tracking-[0.3em]">Log History</h3>
                    <img src={getMcIcon('Book_and_Quill')} className="w-5 h-5 object-contain mc-icon opacity-40" alt="" />
                </div>
                <div className="space-y-4">
                    <div className="flex items-center gap-4">
                        <div className="w-10 h-10 rounded-xl bg-zinc-900 border border-zinc-800 flex items-center justify-center shadow-inner">
                            <img src={getMcIcon('Clock')} className="w-5 h-5 object-contain mc-icon" alt="" />
                        </div>
                        <div>
                            <p className="text-[10px] font-black text-zinc-500 uppercase tracking-widest">Entry Date</p>
                            <p className="text-[11px] font-bold text-zinc-400">{new Date(player.createdAt).toLocaleDateString()}</p>
                        </div>
                    </div>
                    <div className="flex items-center gap-4">
                        <div className="w-10 h-10 rounded-xl bg-zinc-900 border border-zinc-800 flex items-center justify-center shadow-inner">
                            <img src={getMcIcon('Recovery_Compass')} className="w-5 h-5 object-contain mc-icon" alt="" />
                        </div>
                        <div>
                            <p className="text-[10px] font-black text-zinc-500 uppercase tracking-widest">Last Activity</p>
                            <p className="text-[11px] font-bold text-zinc-400 uppercase">{new Date(player.updatedAt).toLocaleTimeString()}</p>
                        </div>
                    </div>
                </div>
              </div>
            </div>
            <div className="md:col-span-8 space-y-6">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                {player.tiers.map((tier: any) => {
                  const rankStyle = formatRank(tier.rank, tier.winsAfterNS);
                  return (
                    <div key={tier.id} className="bento-card p-6 flex flex-col justify-between group relative overflow-hidden transition-all hover:bg-[#16161a]">
                      <div className="absolute -right-6 -top-6 w-32 h-32 opacity-[0.03] group-hover:opacity-[0.12] transition-all duration-700 transform group-hover:rotate-12 group-hover:scale-125">
                         <img src={getMcIcon(tier.gamemode)} className="w-full h-full object-contain mc-icon" alt="" />
                      </div>
                      <div>
                        <div className="flex items-center gap-2 mb-6">
                            <img src={getMcIcon(tier.gamemode)} className="w-4 h-4 object-contain mc-icon drop-shadow-md" alt="" />
                            <h3 className="text-[9px] text-zinc-700 uppercase font-black tracking-[0.4em]">{tier.gamemode} Sector</h3>
                        </div>
                        <div className="flex items-center gap-4">
                            <img 
                              src={getMcIcon(tier.rank)} 
                              className="w-12 h-12 object-contain mc-icon animate-float animate-pulse-glow rank-icon-hover drop-shadow-[0_0_10px_rgba(0,0,0,0.8)]" 
                              alt="" 
                            />
                            <p className={`text-4xl font-black italic tracking-tighter uppercase font-display ${rankStyle.color} drop-shadow-sm`}>{rankStyle.label || tier.rank}</p>
                        </div>
                      </div>
                      <div className="flex items-end justify-between mt-10">
                        <div className="space-y-1">
                            <p className="text-[9px] font-black text-zinc-700 uppercase tracking-widest">Combat Rating</p>
                            <p className="text-2xl font-mono font-black text-zinc-200 tabular-nums italic">{tier.mmr.toLocaleString()}</p>
                        </div>
                        <div className="text-right">
                            <span className="text-[9px] font-black text-zinc-800 uppercase tracking-widest">Performance</span>
                            <p className="text-sm font-black text-zinc-600 italic">TOP 2.5%</p>
                        </div>
                      </div>
                    </div>
                  );
                })}
              </div>
              <PlayerAnalytics player={player as any} />
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
