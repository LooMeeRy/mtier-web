"use client";

import { useEffect } from "react";

interface Achievement {
  id: string;
  name: string;
  description: string;
  icon: string;
}

interface AchievementModalProps {
  isOpen: boolean;
  onClose: () => void;
  achievements: Achievement[];
  getIcon: (type: string) => string;
}

export default function AchievementModal({ isOpen, onClose, achievements, getIcon }: AchievementModalProps) {
  // Prevent scrolling when modal is open
  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = 'unset';
    }
    return () => { document.body.style.overflow = 'unset'; };
  }, [isOpen]);

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-[100] flex items-center justify-center p-4">
      {/* Backdrop */}
      <div 
        className="absolute inset-0 bg-black/80 backdrop-blur-md transition-opacity animate-in fade-in duration-300" 
        onClick={onClose}
      />
      
      {/* Modal Content */}
      <div className="relative w-full max-w-lg bg-[#121214] border border-zinc-800 rounded-3xl shadow-2xl overflow-hidden animate-in zoom-in-95 duration-300">
        {/* Header */}
        <div className="px-8 py-6 border-b border-zinc-800/50 flex items-center justify-between">
            <div className="flex items-center gap-3">
                <div className="p-2 rounded-lg bg-orange-500/10 border border-orange-500/20">
                    <svg xmlns="http://www.w3.org/2000/svg" className="w-5 h-5 text-orange-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M5 3v4M3 5h4M6 17v4m-2-2h4m5-16l2.286 6.857L21 12l-5.714 2.143L13 21l-2.286-6.857L5 12l5.714-2.143L13 3z" />
                    </svg>
                </div>
                <h2 className="text-xl font-black italic tracking-tighter text-white uppercase font-display">Trophy Case</h2>
            </div>
            <button onClick={onClose} className="text-zinc-500 hover:text-white transition-colors">
                <svg xmlns="http://www.w3.org/2000/svg" className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                </svg>
            </button>
        </div>

        {/* Content */}
        <div className="p-8 max-h-[60vh] overflow-y-auto space-y-6 scrollbar-thin scrollbar-thumb-zinc-800">
            {achievements.length > 0 ? (
                achievements.map((ach) => (
                    <div key={ach.id} className="flex gap-6 p-4 rounded-2xl bg-zinc-900/50 border border-zinc-800/50 hover:bg-zinc-900 transition-colors group relative overflow-hidden">
                        <div className="w-16 h-16 shrink-0 rounded-xl bg-black/40 border border-zinc-800 flex items-center justify-center shadow-inner group-hover:border-orange-500/30 transition-colors relative rare-aura-container enchanted-shimmer">
                            <img src={getIcon(ach.icon)} className="w-10 h-10 object-contain mc-icon animate-wobble relative z-10" alt="" />
                            {/* Inner glow for extra "rare" feel */}
                            <div className="absolute inset-0 bg-orange-500/5 opacity-0 group-hover:opacity-100 transition-opacity duration-700 pointer-events-none"></div>
                        </div>
                        <div className="space-y-1 relative z-10">
                            <h3 className="text-sm font-black text-orange-500 uppercase tracking-widest">{ach.name}</h3>
                            <p className="text-xs text-zinc-400 font-medium leading-relaxed">{ach.description}</p>
                            <div className="pt-2 flex items-center gap-1.5">
                                <span className="w-1 h-1 bg-emerald-500 rounded-full"></span>
                                <span className="text-[9px] font-black text-zinc-600 uppercase tracking-widest">Achievement Unlocked</span>
                            </div>
                        </div>
                    </div>
                ))
            ) : (
                <div className="py-12 text-center space-y-4">
                    <p className="text-zinc-700 font-black italic uppercase tracking-[0.4em] text-xs">Zero Achievements Recorded</p>
                </div>
            )}
        </div>

        {/* Footer */}
        <div className="p-6 bg-zinc-900/30 border-t border-zinc-800/50 flex justify-center">
            <p className="text-[9px] font-black text-zinc-700 uppercase tracking-[0.5em]">End of Records</p>
        </div>
      </div>
    </div>
  );
}
