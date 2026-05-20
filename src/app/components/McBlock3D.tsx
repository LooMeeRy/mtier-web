"use client";

import { getMcIcon } from "@/lib/minecraft-icons";

interface McBlock3DProps {
  name: string;
  size?: number;
  className?: string;
}

/**
 * Minecraft Ultimate 3D Block Engine (V3.24)
 * Fully solid 6-face cube with enhanced size and smooth rotation.
 */
export default function McBlock3D({ name, size = 64, className = "" }: McBlock3DProps) {
  const baseName = name.toLowerCase().replace(/\s+/g, '_');
  
  const getTextures = (n: string) => {
    if (n.includes('oak_log') || n === 'wood') return { 
        top: '/mc-assets/block/oak_log_top.png', 
        side: '/mc-assets/block/oak_log.png',
        bottom: '/mc-assets/block/oak_log_top.png'
    };
    if (n.includes('grass')) return { 
        top: '/mc-assets/block/grass_block_top.png', 
        side: '/mc-assets/block/grass_block_side.png',
        bottom: '/mc-assets/block/dirt.png'
    };
    if (n.includes('oak_planks') || n === 'bridge') return {
        top: '/mc-assets/block/oak_planks.png',
        side: '/mc-assets/block/oak_planks.png',
        bottom: '/mc-assets/block/oak_planks.png'
    };
    const path = getMcIcon(n);
    return { top: path, side: path, bottom: path };
  };

  const textures = getTextures(baseName);
  
  // Increased face size for a "bigger" look
  const faceSize = size * 0.7; 
  const offset = faceSize / 2;

  return (
    <div 
      className={`relative flex items-center justify-center ${className}`}
      style={{ width: size, height: size, perspective: '1200px' }}
    >
      {/* Ambient Glow - Stronger for larger block */}
      <div className="absolute inset-0 bg-orange-500/10 blur-3xl rounded-full scale-150 opacity-40"></div>

      <div 
        className="relative"
        style={{
            width: faceSize,
            height: faceSize,
            transformStyle: 'preserve-3d',
            transform: 'rotateX(-25deg) rotateY(-45deg)',
            animation: 'slow-spin 15s linear infinite'
        }}
      >
        {/* FRONT */}
        <div 
          className="absolute inset-0 mc-icon"
          style={{
            backgroundImage: `url(${textures.side})`,
            backgroundSize: '100% 100%',
            transform: `translateZ(${offset}px)`,
            filter: 'brightness(1)',
            boxShadow: 'inset 0 0 10px rgba(0,0,0,0.1)'
          }}
        />

        {/* BACK */}
        <div 
          className="absolute inset-0 mc-icon"
          style={{
            backgroundImage: `url(${textures.side})`,
            backgroundSize: '100% 100%',
            transform: `rotateY(180deg) translateZ(${offset}px)`,
            filter: 'brightness(0.6)',
          }}
        />

        {/* RIGHT */}
        <div 
          className="absolute inset-0 mc-icon"
          style={{
            backgroundImage: `url(${textures.side})`,
            backgroundSize: '100% 100%',
            transform: `rotateY(90deg) translateZ(${offset}px)`,
            filter: 'brightness(0.8)',
          }}
        />

        {/* LEFT */}
        <div 
          className="absolute inset-0 mc-icon"
          style={{
            backgroundImage: `url(${textures.side})`,
            backgroundSize: '100% 100%',
            transform: `rotateY(-90deg) translateZ(${offset}px)`,
            filter: 'brightness(0.9)',
          }}
        />

        {/* TOP */}
        <div 
          className="absolute inset-0 mc-icon"
          style={{
            backgroundImage: `url(${textures.top})`,
            backgroundSize: '100% 100%',
            transform: `rotateX(90deg) translateZ(${offset}px)`,
            filter: 'brightness(1.1)',
            boxShadow: 'inset 0 0 10px rgba(255,255,255,0.1)'
          }}
        />

        {/* BOTTOM */}
        <div 
          className="absolute inset-0 mc-icon"
          style={{
            backgroundImage: `url(${textures.bottom})`,
            backgroundSize: '100% 100%',
            transform: `rotateX(-90deg) translateZ(${offset}px)`,
            filter: 'brightness(0.4)',
          }}
        />
      </div>

      <style jsx>{`
        @keyframes slow-spin {
          from { transform: rotateX(-25deg) rotateY(0deg); }
          to { transform: rotateX(-25deg) rotateY(360deg); }
        }
      `}</style>
    </div>
  );
}
