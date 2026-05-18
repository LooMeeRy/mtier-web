import Link from "next/link";
import prisma from "@/lib/prisma";
import SearchBar from "@/app/components/SearchBar";
import { getMcIcon } from "@/lib/minecraft-icons";

export const dynamic = 'force-dynamic';

// Helper to format rank with stars and colors
export function formatRank(rank: string, wins: number) {
  if (rank === 'NetherStar') {
    return {
      label: `NetherStar ${wins} Stars`,
      color: 'text-indigo-400',
      bg: 'bg-indigo-500/10',
      border: 'border-indigo-500/20'
    };
  }
  
  const ranks: Record<string, any> = {
    'Wood': { color: 'text-orange-900', bg: 'bg-orange-900/10', border: 'border-orange-900/20' },
    'Stone': { color: 'text-gray-500', bg: 'bg-gray-500/10', border: 'border-gray-500/20' },
    'Copper': { color: 'text-orange-500', bg: 'bg-orange-500/10', border: 'border-orange-500/20' },
    'Iron': { color: 'text-zinc-300', bg: 'bg-zinc-300/10', border: 'border-zinc-300/20' },
    'Gold': { color: 'text-yellow-500', bg: 'bg-yellow-500/10', border: 'border-yellow-500/20' },
    'Emerald': { color: 'text-emerald-500', bg: 'bg-emerald-500/10', border: 'border-emerald-500/20' },
    'Diamond': { color: 'text-blue-400', bg: 'bg-blue-400/10', border: 'border-blue-400/20' },
    'Amethyst': { color: 'text-purple-400', bg: 'bg-purple-400/10', border: 'border-purple-400/20' },
  };

  return ranks[rank] || { label: rank, color: 'text-zinc-400', bg: 'bg-zinc-400/10', border: 'border-zinc-400/20' };
}

export default async function Leaderboard({
  searchParams,
}: {
  searchParams: Promise<{ mode?: string }>;
}) {
  let { mode } = await searchParams;

  const gamemodes = await prisma.tier.findMany({
    distinct: ['gamemode'],
    select: { gamemode: true },
    orderBy: { gamemode: 'asc' }
  });

  // If no mode is selected, default to the first available mode
  if (!mode && gamemodes.length > 0) {
    mode = gamemodes[0].gamemode;
  }

  const topTiers = await prisma.tier.findMany({
    where: mode ? { gamemode: mode } : {},
    take: 50,
    include: { player: true },
    orderBy: { mmr: 'desc' }
  });

  return (
    <div className="min-h-screen p-4 md:p-12 bg-[#09090b] relative overflow-hidden">
      {/* Background patterns */}
      <div className="absolute top-0 left-0 w-full h-full opacity-10 pointer-events-none" style={{ backgroundImage: 'radial-gradient(#27272a 1px, transparent 1px)', backgroundSize: '32px 32px' }}></div>
      
      <div className="max-w-6xl mx-auto space-y-12 relative z-10">
        <div className="flex flex-col md:flex-row md:items-end justify-between gap-8">
            <div className="space-y-4">
                <div className="flex items-center gap-4">
                    <Link href="/" className="group inline-flex items-center gap-2 px-3 py-1.5 rounded-lg bg-zinc-900/50 border border-zinc-800 text-zinc-500 hover:text-white transition-all font-bold text-[10px] tracking-widest uppercase">
                        <svg xmlns="http://www.w3.org/2000/svg" className="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={3} d="M10 19l-7-7m0 0l7-7m-7 7h18" />
                        </svg>
                        HUB
                    </Link>
                    <SearchBar />
                </div>
                <div className="space-y-1">
                    <h1 className="text-5xl md:text-7xl font-black tracking-tighter text-white italic uppercase font-display">HALL OF <span className="text-orange-500">FAME</span></h1>
                    <p className="text-zinc-600 font-bold uppercase tracking-[0.4em] text-[10px]">Global Competitive Rankings • Protocol v1.0</p>
                </div>
            </div>

            <div className="flex flex-wrap gap-1.5 p-1.5 bg-zinc-900/50 border border-zinc-800 rounded-xl">
                {gamemodes.map((gm) => (
                    <Link 
                        key={gm.gamemode}
                        href={`/leaderboard?mode=${gm.gamemode}`}
                        className={`px-5 py-2.5 rounded-lg text-[10px] font-black transition-all uppercase tracking-widest flex items-center gap-2 ${mode === gm.gamemode ? 'bg-orange-600 text-white shadow-lg shadow-orange-900/20' : 'text-zinc-500 hover:text-zinc-300'}`}
                    >
                        <img src={getMcIcon(gm.gamemode)} className="w-4 h-4 object-contain mc-icon shadow-lg" alt="" />
                        {gm.gamemode}
                    </Link>
                ))}
            </div>
        </div>
        
        <div className="bento-card border-zinc-800/50 bg-[#121214]/50 shadow-2xl backdrop-blur-md">
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
                <thead>
                <tr className="border-b border-zinc-800/50 text-zinc-600 text-[10px] uppercase font-black tracking-[0.4em]">
                    <th className="px-8 py-6 w-24 text-center">Rank</th>
                    <th className="px-8 py-6">Operator</th>
                    <th className="px-8 py-6">Rating</th>
                    <th className="px-8 py-6 text-right">League Status</th>
                </tr>
                </thead>
                <tbody className="divide-y divide-zinc-800/30">
                {topTiers.map((item, index) => {
                    const rankStyle = formatRank(item.rank, item.winsAfterNS);
                    return (
                        <tr key={item.id} className="hover:bg-white/[0.01] transition-all group">
                        <td className="px-8 py-6">
                            <div className="flex justify-center">
                                <span className={`
                                    text-2xl font-black italic tabular-nums
                                    ${index === 0 ? 'text-orange-500' : 
                                      index === 1 ? 'text-zinc-400' :
                                      index === 2 ? 'text-orange-900' :
                                      'text-zinc-800'}
                                `}>
                                    {String(index + 1).padStart(2, '0')}
                                </span>
                            </div>
                        </td>
                        <td className="px-8 py-6">
                            <Link href={`/player/${item.player.username}${mode ? `?fromMode=${mode}` : ''}`} className="flex items-center gap-4 group/item">
                                <div className="relative w-10 h-10 bg-zinc-900 rounded-lg overflow-hidden border border-zinc-800 group-hover/item:border-zinc-700 transition-all">
                                    <img src={`https://mc-heads.net/avatar/${item.player.username}/40`} className="object-cover" alt="" />
                                </div>
                                <div>
                                    <span className="font-black text-base block leading-none text-zinc-200 group-hover/item:text-orange-500 transition-colors tracking-tighter italic">{item.player.username}</span>
                                    <span className="text-[9px] text-zinc-700 uppercase font-black tracking-widest mt-1 block">Verified ID</span>
                                </div>
                            </Link>
                        </td>
                        <td className="px-8 py-6">
                            <span className="font-mono text-lg font-black text-zinc-100 tabular-nums italic">{item.mmr.toLocaleString()}</span>
                        </td>
                        <td className="px-8 py-6 text-right">
                            <div className="inline-flex items-center gap-3">
                                <img src={getMcIcon(item.rank)} className="w-5 h-5 object-contain mc-icon shadow-sm animate-pulse-glow rank-icon-hover" alt="" />
                                <span className={`font-black uppercase text-[9px] px-3 py-1.5 rounded-lg border ${rankStyle.bg} ${rankStyle.color} ${rankStyle.border} shadow-sm inline-block min-w-[140px] text-center italic tracking-widest`}>
                                    {rankStyle.label || item.rank}
                                </span>
                            </div>
                        </td>
                        </tr>
                    );
                })}
                {topTiers.length === 0 && (
                    <tr>
                    <td colSpan={4} className="px-8 py-32 text-center">
                        <p className="text-zinc-700 font-black italic uppercase tracking-[0.5em] text-xs">Zero Data Points Found</p>
                    </td>
                    </tr>
                )}
                </tbody>
            </table>
          </div>
        </div>

        <div className="flex justify-center pt-8">
            <p className="text-[9px] font-black text-zinc-800 uppercase tracking-[0.6em]">End of Transmission</p>
        </div>
      </div>
    </div>
  );
}
