"use client";

import { getMcIcon } from "@/lib/minecraft-icons";

interface McBlock3DProps {
  name: string;
  size?: number;
  className?: string;
}

/**
 * Minecraft Ultimate 3D Block Engine (V3.23)
 * Solid CSS 3D Cube with dynamic textures and ambient lighting.
 */
export default function McBlock3D({ name, size = 64, className = "" }: McBlock3DProps) {
  const baseName = name.toLowerCase().replace(/\s+/g, '_');
  
  const getTextures = (n: string) => {
    if (n.includes('oak_log') || n === 'wood') return { 
        top: '/mc-assets/block/oak_log_top.png', 
        front: '/mc-assets/block/oak_log.png',
        side: '/mc-assets/block/oak_log.png' 
    };
    if (n.includes('grass')) return { 
        top: '/mc-assets/block/grass_block_top.png', 
        front: '/mc-assets/block/grass_block_side.png',
        side: '/mc-assets/block/grass_block_side.png'
    };
    if (n.includes('oak_planks') || n === 'bridge') return {
        top: '/mc-assets/block/oak_planks.png',
        front: '/mc-assets/block/oak_planks.png',
        side: '/mc-assets/block/oak_planks.png'
    };
    const path = getMcIcon(n);
    return { top: path, front: path, side: path };
  };

  const textures = getTextures(baseName);
  
  // faceSize calculation to ensure cube fits within 'size' container
  const faceSize = size * 0.55; 
  const offset = faceSize / 2;

  return (
    <div 
      className={`relative flex items-center justify-center ${className}`}
      style={{ width: size, height: size, perspective: '1000px' }}
    >
      {/* Ambient Glow */}
      <div className="absolute inset-0 bg-orange-500/5 blur-xl rounded-full scale-150"></div>

      <div 
        className="relative group-hover:pause-animation"
        style={{
            width: faceSize,
            height: faceSize,
            transformStyle: 'preserve-3d',
            transform: 'rotateX(-30deg) rotateY(-45deg)',
            animation: 'slow-spin 20s linear infinite'
        }}
      >
        {/* FRONT FACE */}
        <div 
          className="absolute inset-0 mc-icon"
          style={{
            backgroundImage: `url(${textures.front})`,
            backgroundSize: 'cover',
            transform: `translateZ(${offset}px)`,
            filter: 'brightness(1)',
            boxShadow: 'inset 0 0 10px rgba(0,0,0,0.2)'
          }}
        />

        {/* RIGHT FACE */}
        <div 
          className="absolute inset-0 mc-icon"
          style={{
            backgroundImage: `url(${textures.side})`,
            backgroundSize: 'cover',
            transform: `rotateY(90deg) translateZ(${offset}px)`,
            filter: 'brightness(0.7)',
            boxShadow: 'inset 0 0 15px rgba(0,0,0,0.4)'
          }}
        />

        {/* TOP FACE */}
        <div 
          className="absolute inset-0 mc-icon"
          style={{
            backgroundImage: `url(${textures.top})`,
            backgroundSize: 'cover',
            transform: `rotateX(90deg) translateZ(${offset}px)`,
            filter: 'brightness(1.1)',
            boxShadow: 'inset 0 0 10px rgba(255,255,255,0.1)'
          }}
        />

        {/* LEFT FACE (Hidden usually, but good for spin) */}
        <div 
          className="absolute inset-0 mc-icon"
          style={{
            backgroundImage: `url(${textures.side})`,
            backgroundSize: 'cover',
            transform: `rotateY(-90deg) translateZ(${offset}px)`,
            filter: 'brightness(0.8)',
          }}
        />
      </div>

      <style jsx>{`
        @keyframes slow-spin {
          from { transform: rotateX(-30deg) rotateY(0deg); }
          to { transform: rotateX(-30deg) rotateY(360deg); }
        }
      `}</style>
    </div>
  );
}
