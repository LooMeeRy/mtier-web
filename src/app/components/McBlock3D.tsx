"use client";

import { getMcIcon } from "@/lib/minecraft-icons";

interface McBlock3DProps {
  name: string;
  size?: number;
  className?: string;
}

/**
 * Adaptive Minecraft Renderer (V3.25)
 * Smartly switches between 3D Cube (for blocks) and 2D Billboard (for items).
 */
export default function McBlock3D({ name, size = 64, className = "" }: McBlock3DProps) {
  const baseName = name.toLowerCase().replace(/\s+/g, '_');
  
  // Detect if it should be a block or an item
  const isBlock = baseName.includes('block') || 
                  baseName.includes('log') || 
                  baseName.includes('planks') || 
                  baseName.includes('wood') ||
                  baseName.includes('stone') ||
                  baseName.includes('dirt') ||
                  baseName === 'bridge';

  const getTexturePaths = (n: string) => {
    if (n.includes('oak_log') || n === 'wood') return { 
        top: '/mc-assets/block/oak_log_top.png', 
        side: '/mc-assets/block/oak_log.png',
        bottom: '/mc-assets/block/oak_log_top.png',
        isBlock: true
    };
    if (n.includes('grass')) return { 
        top: '/mc-assets/block/grass_block_top.png', 
        side: '/mc-assets/block/grass_block_side.png',
        bottom: '/mc-assets/block/dirt.png',
        isBlock: true
    };
    if (n.includes('oak_planks') || n === 'bridge') return {
        top: '/mc-assets/block/oak_planks.png',
        side: '/mc-assets/block/oak_planks.png',
        bottom: '/mc-assets/block/oak_planks.png',
        isBlock: true
    };
    
    // Default to icon lookup
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
      {/* Ambient Glow */}
      <div className="absolute inset-0 bg-orange-500/10 blur-3xl rounded-full scale-150 opacity-40"></div>

      <div 
        className="relative"
        style={{
            width: faceSize,
            height: faceSize,
            transformStyle: 'preserve-3d',
            transform: 'rotateX(-15deg) rotateY(0deg)',
            animation: 'slow-spin 10s linear infinite'
        }}
      >
        {textures.isBlock ? (
          <>
            {/* 3D CUBE FOR BLOCKS */}
            <div className="absolute inset-0 mc-icon" style={{ backgroundImage: `url(${textures.side})`, backgroundSize: '100% 100%', transform: `translateZ(${offset}px)`, filter: 'brightness(1)' }} />
            <div className="absolute inset-0 mc-icon" style={{ backgroundImage: `url(${textures.side})`, backgroundSize: '100% 100%', transform: `rotateY(180deg) translateZ(${offset}px)`, filter: 'brightness(0.6)' }} />
            <div className="absolute inset-0 mc-icon" style={{ backgroundImage: `url(${textures.side})`, backgroundSize: '100% 100%', transform: `rotateY(90deg) translateZ(${offset}px)`, filter: 'brightness(0.8)' }} />
            <div className="absolute inset-0 mc-icon" style={{ backgroundImage: `url(${textures.side})`, backgroundSize: '100% 100%', transform: `rotateY(-90deg) translateZ(${offset}px)`, filter: 'brightness(0.9)' }} />
            <div className="absolute inset-0 mc-icon" style={{ backgroundImage: `url(${textures.top})`, backgroundSize: '100% 100%', transform: `rotateX(90deg) translateZ(${offset}px)`, filter: 'brightness(1.1)' }} />
            <div className="absolute inset-0 mc-icon" style={{ backgroundImage: `url(${textures.bottom})`, backgroundSize: '100% 100%', transform: `rotateX(-90deg) translateZ(${offset}px)`, filter: 'brightness(0.4)' }} />
          </>
        ) : (
          /* 2D FLOATING BILLBOARD FOR ITEMS (Like Nether Star) */
          <div 
            className="absolute inset-0 mc-icon"
            style={{
              backgroundImage: `url(${textures.top})`,
              backgroundSize: 'contain',
              backgroundPosition: 'center',
              backgroundRepeat: 'no-repeat',
              transform: 'rotateY(0deg)',
              filter: 'drop-shadow(0 0 10px rgba(255,255,255,0.3))'
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
