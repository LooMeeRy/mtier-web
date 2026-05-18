"use client";

import Link from "next/link";

export default function Home() {
  return (
    <div className="relative flex flex-col items-center justify-center min-h-screen p-6 overflow-hidden bg-[#09090b]">
      {/* Decorative background glow */}
      <div className="absolute top-[-10%] left-[-10%] w-[40%] h-[40%] bg-orange-500/10 blur-[120px] rounded-full pointer-events-none"></div>
      <div className="absolute bottom-[-10%] right-[-10%] w-[40%] h-[40%] bg-orange-600/10 blur-[120px] rounded-full pointer-events-none"></div>

      <div className="relative z-10 w-full max-w-2xl space-y-12 text-center">
        <div className="space-y-4">
          <div className="inline-flex items-center gap-2 px-3 py-1 text-[10px] font-bold tracking-widest uppercase border rounded-lg bg-orange-500/5 text-orange-500 border-orange-500/20">
            <span className="relative flex w-2 h-2">
              <span className="absolute inline-flex w-full h-full bg-orange-400 rounded-full animate-ping opacity-75"></span>
              <span className="relative inline-flex w-2 h-2 bg-orange-500 rounded-full"></span>
            </span>
            NETWORK ONLINE
          </div>
          <h1 className="text-6xl font-black tracking-tighter text-white md:text-8xl italic font-display">
            MTier <span className="text-orange-500">Pro</span>
          </h1>
        </div>

        <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
          <Link href="/leaderboard" className="bento-card p-8 text-left group">
            <div className="p-3 mb-4 transition-colors rounded-lg bg-zinc-900 w-fit group-hover:bg-orange-500/10 group-hover:text-orange-500 border border-zinc-800">
              <svg xmlns="http://www.w3.org/2000/svg" className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6" />
              </svg>
            </div>
            <h3 className="text-[10px] font-black tracking-widest text-zinc-600 uppercase mb-1">Hall of Fame</h3>
            <p className="text-2xl font-black text-zinc-300">Leaderboard</p>
          </Link>

          <Link href="/player/Dream" className="bento-card p-8 text-left group">
            <div className="p-3 mb-4 transition-colors rounded-lg bg-zinc-900 w-fit group-hover:bg-orange-500/10 group-hover:text-orange-500 border border-zinc-800">
              <svg xmlns="http://www.w3.org/2000/svg" className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </div>
            <h3 className="text-[10px] font-black tracking-widest text-zinc-600 uppercase mb-1">Direct Access</h3>
            <p className="text-2xl font-black text-zinc-300">Player Lookup</p>
          </Link>
        </div>

        <div className="bento-card p-6 text-left flex flex-row items-center justify-between">
            <div>
              <h3 className="text-[10px] font-black tracking-widest text-zinc-600 uppercase mb-1">Active Tournament</h3>
              <p className="text-xl font-black text-zinc-300 italic">Season 01: The Awakening</p>
            </div>
            <div className="hidden md:flex flex-col items-end">
              <span className="text-[10px] font-black text-orange-500/50 uppercase tracking-widest">TIME REMAINING</span>
              <span className="text-xl font-black text-zinc-600 font-mono">24D 12H 05M</span>
            </div>
          </div>
      </div>

      <div className="absolute left-4 bottom-4">
        <span className="text-[10px] font-black text-zinc-800 uppercase tracking-[0.5em] select-none">AUTHORIZED ACCESS ONLY</span>
      </div>
    </div>
  );
}
