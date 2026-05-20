"use client";

import { getMcIcon } from "@/lib/minecraft-icons";

interface McBlock3DProps {
  name: string;
  size?: number;
  className?: string;
}

/**
 * Minecraft Future-Proof Renderer (V3.27)
 * Comprehensive mapping for all rank types (Blocks vs Items).
 */
export default function McBlock3D({ name, size = 64, className = "" }: McBlock3DProps) {
  const baseName = name.toLowerCase().replace(/\s+/g, '_');
  
  // Comprehensive Rank Mapping
  const RANK_MAP: Record<string, { top: string; side: string; bottom: string; isBlock: boolean }> = {
    'wood': { top: 'oak_log_top', side: 'oak_log', bottom: 'oak_log_top', isBlock: true },
    'stone': { top: 'stone', side: 'stone', bottom: 'stone', isBlock: true },
    'copper': { top: 'copper_block', side: 'copper_block', bottom: 'copper_block', isBlock: true },
    'iron': { top: 'iron_block', side: 'iron_block', bottom: 'iron_block', isBlock: true },
    'gold': { top: 'gold_block', side: 'gold_block', bottom: 'gold_block', isBlock: true },
    'emerald': { top: 'emerald_block', side: 'emerald_block', bottom: 'emerald_block', isBlock: true },
    'diamond': { top: 'diamond_block', side: 'diamond_block', bottom: 'diamond_block', isBlock: true },
    'amethyst': { top: 'amethyst_block', side: 'amethyst_block', bottom: 'amethyst_block', isBlock: true },
    'netherite': { top: 'netherite_block', side: 'netherite_block', bottom: 'netherite_block', isBlock: true },
    'bridge': { top: 'oak_planks', side: 'oak_planks', bottom: 'oak_planks', isBlock: true },
    'netherstar': { top: 'nether_star', side: 'nether_star', bottom: 'nether_star', isBlock: false }
  };

  const getTexturePaths = (n: string) => {
    // 1. Check explicit mapping
    for (const [key, config] of Object.entries(RANK_MAP)) {
        if (n.includes(key)) {
            return {
                top: `/mc-assets/block/${config.top}.png`,
                side: `/mc-assets/block/${config.side}.png`,
                bottom: `/mc-assets/block/${config.bottom}.png`,
                isBlock: config.isBlock
            };
        }
    }

    // 2. Generic fallback logic
    const isBlock = n.includes('block') || n.includes('log') || n.includes('planks');
    const path = getMcIcon(n);
    return { top: path, side: path, bottom: path, isBlock: isBlock };
  };

  const textures = getTexturePaths(baseName);
  const faceSize = size * 0.7; 
  const offset = faceSize / 2;

  return (
    <div 
      className={`relative flex items-center justify-center ${className}`}
      style={{ width: size, height: size, perspective: '1200px' }}
    >
      <div className="absolute inset-0 bg-orange-500/5 blur-3xl rounded-full scale-150 opacity-30"></div>

      <div 
        className="relative"
        style={{
            width: faceSize,
            height: faceSize,
            transformStyle: 'preserve-3d',
            transform: 'rotateX(-15deg) rotateY(0deg)',
            animation: 'slow-spin 12s linear infinite'
        }}
      >
        {textures.isBlock ? (
          <>
            <div className="absolute inset-0 mc-icon" style={{ backgroundImage: `url(${textures.side})`, backgroundSize: '100% 100%', transform: `translateZ(${offset}px)`, filter: 'brightness(1)', border: '0.5px solid rgba(0,0,0,0.1)' }} />
            <div className="absolute inset-0 mc-icon" style={{ backgroundImage: `url(${textures.side})`, backgroundSize: '100% 100%', transform: `rotateY(180deg) translateZ(${offset}px)`, filter: 'brightness(0.6)' }} />
            <div className="absolute inset-0 mc-icon" style={{ backgroundImage: `url(${textures.side})`, backgroundSize: '100% 100+%', transform: `rotateY(90deg) translateZ(${offset}px)`, filter: 'brightness(0.8)' }} />
            <div className="absolute inset-0 mc-icon" style={{ backgroundImage: `url(${textures.side})`, backgroundSize: '100% 100%', transform: `rotateY(-90deg) translateZ(${offset}px)`, filter: 'brightness(0.9)' }} />
            <div className="absolute inset-0 mc-icon" style={{ backgroundImage: `url(${textures.top})`, backgroundSize: '100% 100%', transform: `rotateX(90deg) translateZ(${offset}px)`, filter: 'brightness(1.1)' }} />
            <div className="absolute inset-0 mc-icon" style={{ backgroundImage: `url(${textures.bottom})`, backgroundSize: '100% 100%', transform: `rotateX(-90deg) translateZ(${offset}px)`, filter: 'brightness(0.4)' }} />
          </>
        ) : (
          <div 
            className="absolute inset-0 mc-icon"
            style={{
              backgroundImage: `url(${textures.top})`,
              backgroundSize: 'contain',
              backgroundPosition: 'center',
              backgroundRepeat: 'no-repeat',
              transform: 'rotateY(0deg)',
              filter: 'drop-shadow(0 0 15px rgba(255,255,255,0.2))'
            }}
          />
        )}
      </div>

      <style jsx>{`
        @keyframes slow-spin {
          from { transform: rotateX(-15deg) rotateY(0deg); }
          to { transform: rotateX(-15deg) rotateY(360deg); }
        }
      `}</style>
    </div>
  );
}
