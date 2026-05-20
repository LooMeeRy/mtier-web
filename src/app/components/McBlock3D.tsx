"use client";

import { getMcIcon } from "@/lib/minecraft-icons";

interface McBlock3DProps {
  name: string;
  size?: number;
  className?: string;
}

/**
 * Minecraft 3D Block Renderer (Pure CSS)
 * Renders a realistic isometric block using local textures.
 */
export default function McBlock3D({ name, size = 64, className = "" }: McBlock3DProps) {
  // Logic to determine top vs side textures
  let baseName = name.toLowerCase().replace(/\s+/g, '_');
  
  // Mapping logic for blocks that have distinct top/side textures
  const getTextures = (n: string) => {
    if (n.includes('oak_log') || n === 'wood') return { 
        top: '/mc-assets/block/oak_log_top.png', 
        side: '/mc-assets/block/oak_log.png' 
    };
    if (n.includes('grass')) return { 
        top: '/mc-assets/block/grass_block_top.png', 
        side: '/mc-assets/block/grass_block_side.png',
        bottom: '/mc-assets/block/dirt.png'
    };
    // Default: use the same texture for all sides
    const path = getMcIcon(n);
    return { top: path, side: path };
  };

  const textures = getTextures(baseName);

  return (
    <div 
      className={`relative perspective-1000 ${className}`}
      style={{ 
        width: size, 
        height: size,
        perspective: '1000px'
      }}
    >
      <div 
        className="relative w-full h-full preserve-3d transition-transform duration-700 group-hover:rotate-y-12"
        style={{
            transform: 'rotateX(-25deg) rotateY(45deg)',
            transformStyle: 'preserve-3d'
        }}
      >
        {/* Top Face */}
        <div 
          className="absolute inset-0 mc-icon"
          style={{
            backgroundImage: `url(${textures.top})`,
            backgroundSize: 'cover',
            transform: `translateZ(${size/2}px) translateY(-${size/2}px) rotateX(90deg)`,
            width: size,
            height: size,
            boxShadow: 'inset 0 0 20px rgba(255,255,255,0.1)'
          }}
        />
        
        {/* Front Face */}
        <div 
          className="absolute inset-0 mc-icon"
          style={{
            backgroundImage: `url(${textures.side})`,
            backgroundSize: 'cover',
            transform: `translateZ(${size/2}px)`,
            width: size,
            height: size,
            filter: 'brightness(0.9)',
            boxShadow: 'inset 0 0 20px rgba(0,0,0,0.2)'
          }}
        />

        {/* Right Face */}
        <div 
          className="absolute inset-0 mc-icon"
          style={{
            backgroundImage: `url(${textures.side})`,
            backgroundSize: 'cover',
            transform: `rotateY(90deg) translateZ(${size/2}px)`,
            width: size,
            height: size,
            filter: 'brightness(0.7)',
            boxShadow: 'inset 0 0 20px rgba(0,0,0,0.4)'
          }}
        />
      </div>
    </div>
  );
}
