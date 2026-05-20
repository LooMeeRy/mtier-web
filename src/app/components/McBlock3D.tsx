"use client";

import { getMcIcon } from "@/lib/minecraft-icons";

interface McBlock3DProps {
  name: string;
  size?: number;
  className?: string;
}

/**
 * Minecraft 3D Block Renderer (Final Geometry)
 * Uses a robust wrapper system to ensure faces are perfectly aligned.
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
  const halfSize = size / 2;

  return (
    <div 
      className={`relative flex items-center justify-center ${className}`}
      style={{ 
        width: size, 
        height: size,
        perspective: '1000px',
      }}
    >
      {/* Glow Effect to confirm update */}
      <div className="absolute inset-0 bg-orange-500/10 blur-2xl rounded-full opacity-50"></div>

      <div 
        className="relative"
        style={{
            width: size,
            height: size,
            transformStyle: 'preserve-3d',
            transform: 'rotateX(-30deg) rotateY(45deg)',
            transition: 'transform 0.5s ease-out'
        }}
      >
        {/* TOP FACE */}
        <div 
          className="absolute mc-icon"
          style={{
            width: size,
            height: size,
            backgroundImage: `url(${textures.top})`,
            backgroundSize: '100% 100%',
            transform: `rotateX(90deg) translateZ(${halfSize}px)`,
            boxShadow: 'inset 0 0 10px rgba(255,255,255,0.2)',
          }}
        />
        
        {/* FRONT FACE (LEFT-ish in Isometric) */}
        <div 
          className="absolute mc-icon"
          style={{
            width: size,
            height: size,
            backgroundImage: `url(${textures.side})`,
            backgroundSize: '100% 100%',
            transform: `rotateY(0deg) translateZ(${halfSize}px)`,
            filter: 'brightness(0.9)',
            boxShadow: 'inset 0 0 15px rgba(0,0,0,0.3)',
          }}
        />

        {/* RIGHT FACE (RIGHT-ish in Isometric) */}
        <div 
          className="absolute mc-icon"
          style={{
            width: size,
            height: size,
            backgroundImage: `url(${textures.side})`,
            backgroundSize: '100% 100%',
            transform: `rotateY(90deg) translateZ(${halfSize}px)`,
            filter: 'brightness(0.7)',
            boxShadow: 'inset 0 0 20px rgba(0,0,0,0.5)',
          }}
        />
      </div>
    </div>
  );
}
