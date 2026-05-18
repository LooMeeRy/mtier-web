"use client";

import { useEffect, useRef, useState } from "react";
import * as skinview3d from "skinview3d";

interface SkinViewerProps {
  username: string;
  uuid?: string;
}

export default function SkinViewer({ username }: SkinViewerProps) {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const [isInitializing, setIsInitializing] = useState(true);

  useEffect(() => {
    if (!canvasRef.current) return;

    console.log(`[3D Viewer] Initializing for ${username}`);
    
    const viewer = new skinview3d.SkinViewer({
      canvas: canvasRef.current,
      width: 224,
      height: 320,
    });

    viewer.autoRotate = true;
    viewer.autoRotateSpeed = 1.0; 
    
    const idle = new skinview3d.IdleAnimation();
    idle.speed = 0.8;
    viewer.animation = idle;
    viewer.renderer.setClearColor(0x000000, 0); 

    // 1. Close loader immediately when Engine is ready
    setIsInitializing(false);

    // 2. Load skin in background (don't wait for it)
    const skinUrl = `https://minotar.net/skin/${username}`;
    viewer.loadSkin(skinUrl, { model: "auto-detect" }).catch(() => {
        viewer.loadSkin("https://minotar.net/skin/char").catch(() => {});
    });

    // 3. Safety net to ensure spinner is gone
    const timer = setTimeout(() => setIsInitializing(false), 2000);

    viewer.camera.position.set(0, 10, 55);
    viewer.fov = 35;

    return () => {
      clearTimeout(timer);
      viewer.dispose();
    };
  }, [username]);

  return (
    <div className="relative w-full h-full flex items-center justify-center bg-black/40 border border-white/5 rounded-2xl overflow-hidden shadow-inner">
      {/* 3D Canvas - Always rendered, no opacity hiding */}
      <canvas 
        ref={canvasRef} 
        className="w-full h-full object-contain drop-shadow-[0_10px_30px_rgba(0,0,0,0.5)]" 
      />

      {/* Small Loader while booting */}
      {isInitializing && (
        <div className="absolute inset-0 flex items-center justify-center bg-[#121214]">
            <div className="w-6 h-6 border-2 border-orange-500/20 border-t-orange-500 rounded-full animate-spin"></div>
        </div>
      )}
    </div>
  );
}
