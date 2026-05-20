"use client";

import { getMcIcon } from "@/lib/minecraft-icons";

interface McBlock3DProps {
  name: string;
  size?: number;
  className?: string;
}

/**
 * Minecraft 3D Block Renderer (Pure CSS)
 * Corrected geometry for a solid isometric cube.
 */
export default function McBlock3D({ name, size = 64, className = "" }: McBlock3DProps) {
  let baseName = name.toLowerCase().replace(/\s+/g, '_');
  
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
    
    // Default: use the same texture for all sides
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
        perspective: '1000px'
      }}
    >
      <div 
        className="relative w-full h-full preserve-3d"
        style={{
            transform: 'rotateX(-30deg) rotateY(45deg)',
            transformStyle: 'preserve-3d',
            transition: 'transform 0.5s ease-out'
        }}
      >
        {/* Top Face */}
        <div 
          className="absolute inset-0 mc-icon"
          style={{
            backgroundImage: `url(${textures.top})`,
            backgroundSize: 'cover',
            transform: `rotateX(90deg) translateZ(${size/2}px)`,
            width: size,
            height: size,
            boxShadow: 'inset 0 0 10px rgba(255,255,255,0.2)'
          }}
        />
        
        {/* Front Face (South) */}
        <div 
          className="absolute inset-0 mc-icon"
          style={{
            backgroundImage: `url(${textures.side})`,
            backgroundSize: 'cover',
            transform: `rotateY(0deg) translateZ(${size/2}px)`,
            width: size,
            height: size,
            filter: 'brightness(0.9)',
            boxShadow: 'inset 0 0 10px rgba(0,0,0,0.2)'
          }}
        />

        {/* Right Face (East) */}
        <div 
          className="absolute inset-0 mc-icon"
          style={{
            backgroundImage: `url(${textures.side})`,
            backgroundSize: 'cover',
            transform: `rotateY(90deg) translateZ(${size/2}px)`,
            width: size,
            height: size,
            filter: 'brightness(0.7)',
            boxShadow: 'inset 0 0 10px rgba(0,0,0,0.4)'
          }}
        />

        {/* Left Face (West) - Optional but good for full turns */}
        <div 
          className="absolute inset-0 mc-icon"
          style={{
            backgroundImage: `url(${textures.side})`,
            backgroundSize: 'cover',
            transform: `rotateY(-90deg) translateZ(${size/2}px)`,
            width: size,
            height: size,
            filter: 'brightness(0.8)',
          }}
        />
      </div>
    </div>
  );
}
