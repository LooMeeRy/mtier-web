"use client";

import { getMcIcon } from "@/lib/minecraft-icons";

interface McBlock3DProps {
  name: string;
  size?: number;
  className?: string;
}

/**
 * Minecraft 3D Block Renderer (Isometric Cube)
 * High-fidelity 3D reconstruction using pure CSS.
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

  return (
    <div 
      className={`relative ${className}`}
      style={{ 
        width: size, 
        height: size,
        perspective: '1000px',
        display: 'inline-block'
      }}
    >
      <div 
        className="relative w-full h-full"
        style={{
            transform: 'rotateX(-30deg) rotateY(45deg)',
            transformStyle: 'preserve-3d',
            width: '100%',
            height: '100%'
        }}
      >
        {/* TOP FACE */}
        <div 
          className="absolute mc-icon"
          style={{
            width: size,
            height: size,
            backgroundImage: `url(${textures.top})`,
            backgroundSize: 'cover',
            transform: `rotateX(90deg) translateZ(${size / 2}px)`,
            boxShadow: 'inset 0 0 15px rgba(255,255,255,0.1)'
          }}
        />
        
        {/* FRONT FACE (LEFT-ish) */}
        <div 
          className="absolute mc-icon"
          style={{
            width: size,
            height: size,
            backgroundImage: `url(${textures.side})`,
            backgroundSize: 'cover',
            transform: `rotateY(0deg) translateZ(${size / 2}px)`,
            filter: 'brightness(0.9)',
            boxShadow: 'inset 0 0 15px rgba(0,0,0,0.2)'
          }}
        />

        {/* RIGHT FACE (RIGHT-ish) */}
        <div 
          className="absolute mc-icon"
          style={{
            width: size,
            height: size,
            backgroundImage: `url(${textures.side})`,
            backgroundSize: 'cover',
            transform: `rotateY(90deg) translateZ(${size / 2}px)`,
            filter: 'brightness(0.7)',
            boxShadow: 'inset 0 0 20px rgba(0,0,0,0.4)'
          }}
        />
      </div>
    </div>
  );
}
