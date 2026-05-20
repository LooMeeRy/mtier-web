"use client";

import { getMcIcon } from "@/lib/minecraft-icons";

interface McBlock3DProps {
  name: string;
  size?: number;
  className?: string;
}

/**
 * Minecraft 2.5D Isometric Block Renderer
 * Uses stable 2D skew transforms for a perfect 3D look without 3D bugs.
 */
export default function McBlock3D({ name, size = 64, className = "" }: McBlock3DProps) {
  const baseName = name.toLowerCase().replace(/\s+/g, '_');
  
  const getTextures = (n: string) => {
    if (n.includes('oak_log') || n === 'wood') return { 
        top: '/mc-assets/block/oak_log_top.png', 
        side: '/mc-assets/block/oak_log.png' 
    };
    if (n.includes('grass')) return { 
        top: '/mc-assets/block/grass_block_top.png', 
        side: '/mc-assets/block/grass_block_side.png'
    };
    if (n.includes('oak_planks') || n === 'bridge') return {
        top: '/mc-assets/block/oak_planks.png',
        side: '/mc-assets/block/oak_planks.png'
    };
    const path = getMcIcon(n);
    return { top: path, side: path };
  };

  const textures = getTextures(baseName);
  
  // Scale factor for isometric projection
  const s = size * 0.6; 

  return (
    <div 
      className={`relative flex items-center justify-center ${className}`}
      style={{ width: size, height: size }}
    >
      {/* Verification Label (Subtle) */}
      <span className="absolute -top-2 left-0 text-[6px] text-orange-500/20 font-black uppercase tracking-widest">v3.21-stable</span>

      <div className="relative" style={{ width: s, height: s }}>
        {/* TOP FACE: Skewed and Rotated */}
        <div 
          className="absolute mc-icon"
          style={{
            width: s,
            height: s,
            backgroundImage: `url(${textures.top})`,
            backgroundSize: 'cover',
            transform: 'rotate(-45deg) skew(15deg, 15deg)',
            top: `-${s * 0.35}px`,
            left: '0px',
            zIndex: 3,
            boxShadow: 'inset 0 0 10px rgba(255,255,255,0.1)'
          }}
        />
        
        {/* LEFT FACE: Skewed */}
        <div 
          className="absolute mc-icon"
          style={{
            width: s,
            height: s,
            backgroundImage: `url(${textures.side})`,
            backgroundSize: 'cover',
            transform: 'skewY(30deg)',
            top: '0px',
            left: `-${s * 0.5}px`,
            filter: 'brightness(0.85)',
            zIndex: 2,
            boxShadow: 'inset 0 0 15px rgba(0,0,0,0.2)'
          }}
        />

        {/* RIGHT FACE: Skewed */}
        <div 
          className="absolute mc-icon"
          style={{
            width: s,
            height: s,
            backgroundImage: `url(${textures.side})`,
            backgroundSize: 'cover',
            transform: 'skewY(-30deg)',
            top: '0px',
            left: `${s * 0.5}px`,
            filter: 'brightness(0.7)',
            zIndex: 1,
            boxShadow: 'inset 0 0 20px rgba(0,0,0,0.4)'
          }}
        />
      </div>
    </div>
  );
}
