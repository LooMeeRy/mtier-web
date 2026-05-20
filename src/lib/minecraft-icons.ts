/**
 * ULTRA-RELIABLE Minecraft Icon Fetcher
 * Optimized for High-Quality 3D Block Renders and Sharp 2D Items.
 */
/**
 * SELF-HOSTED Minecraft Icon System
 * Serves assets directly from /public/mc-assets/ for maximum speed and reliability.
 */
export const getMcIcon = (type: string) => {
  if (!type || type.toLowerCase() === 'air') return `/mc-assets/items/barrier.png`;

  let id = type.toLowerCase()
    .replace('golden_', 'gold_')
    .replace('wooden_', 'wood_');
  
  // Local path instead of external URL
  const base = `/mc-assets`;

  // 1. Explicit UI/Rank Overrides
  const UI_MAPPING: Record<string, { folder: string, file: string }> = {
    'wood': { folder: 'blocks', file: 'oak_log' },
    'stone': { folder: 'blocks', file: 'stone' },
    'bridge': { folder: 'blocks', file: 'oak_planks' },
    'pvp': { folder: 'items', file: 'diamond_sword' },
    'survival': { folder: 'blocks', file: 'grass_block_top' },
    'global': { folder: 'blocks', file: 'grass_block_top' },
    'authorization': { folder: 'items', file: 'experience_bottle' },
    'assigned sector': { folder: 'items', file: 'map' }
  };

  if (UI_MAPPING[id]) {
    return `${base}/${UI_MAPPING[id].folder}/${UI_MAPPING[id].file}.png`;
  }

  // 2. Intelligent Folder Switching
  const isBlock = id.includes('_block') || 
                  id.includes('_log') || 
                  id.includes('_planks') || 
                  id.includes('_sapling') ||
                  id.includes('_ore') ||
                  id.includes('_wool') ||
                  id.includes('_terracotta') ||
                  id.includes('_concrete') ||
                  id.includes('_glass') ||
                  ['stone', 'dirt', 'grass_block', 'sand', 'gravel', 'bedrock', 'obsidian'].includes(id);

  const folder = isBlock ? 'blocks' : 'items';
  
  // Return the local project path
  return `${base}/${folder}/${id}.png`;
};
