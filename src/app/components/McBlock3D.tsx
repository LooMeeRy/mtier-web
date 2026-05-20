"use client";

import { getMcIcon } from "@/lib/minecraft-icons";

interface McBlock3DProps {
  name: string;
  size?: number;
  className?: string;
}

/**
 * Minecraft Perfect 3D Cube (V3.22)
 * Mathematically aligned isometric projection using pure CSS.
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
  
  // Base unit for construction
  const s = size * 0.5; 

  return (
    <div 
      className={`relative flex items-center justify-center ${className}`}
      style={{ width: size, height: size }}
    >
      <span className="absolute -top-3 left-0 text-[6px] text-emerald-500/40 font-black uppercase tracking-widest">v3.22-final</span>

      <div className="relative" style={{ width: s, height: s, marginTop: s * 0.2 }}>
        {/* LEFT FACE */}
        <div 
          className="absolute mc-icon"
          style={{
            width: s,
            height: s,
            backgroundImage: `url(${textures.side})`,
            backgroundSize: '100% 100%',
            transform: 'skewY(30deg)',
            transformOrigin: 'top right',
            right: '50%',
            top: '0',
            filter: 'brightness(0.8)',
            zIndex: 2,
            borderRight: '1px solid rgba(0,0,0,0.1)'
          }}
        />

        {/* RIGHT FACE */}
        <div 
          className="absolute mc-icon"
          style={{
            width: s,
            height: s,
            backgroundImage: `url(${textures.side})`,
            backgroundSize: '100% 100%',
            transform: 'skewY(-30deg)',
            transformOrigin: 'top left',
            left: '50%',
            top: '0',
            filter: 'brightness(0.6)',
            zIndex: 1
          }}
        />

        {/* TOP FACE: THE CAPSTONE */}
        <div 
          className="absolute mc-icon"
          style={{
            width: s,
            height: s,
            backgroundImage: `url(${textures.top})`,
            backgroundSize: '100% 100%',
            // Precise isometric top projection
            transform: 'scaleY(0.86) rotate(45deg)',
            transformOrigin: 'center',
            left: '0',
            top: `-${s * 0.43}px`,
            zIndex: 3,
            boxShadow: 'inset 0 0 10px rgba(255,255,255,0.05)'
          }}
        />
      </div>
    </div>
  );
}
