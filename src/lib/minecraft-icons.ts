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
    'wood': { folder: 'block', file: 'oak_log' },
    'stone': { folder: 'block', file: 'stone' },
    'bridge': { folder: 'block', file: 'oak_planks' },
    'pvp': { folder: 'item', file: 'diamond_sword' },
    'survival': { folder: 'block', file: 'grass_block_top' },
    'global': { folder: 'block', file: 'grass_block_top' },
    'authorization': { folder: 'item', file: 'experience_bottle' },
    'assigned sector': { folder: 'item', file: 'map' }
  };

  if (UI_MAPPING[id]) {
    return `${base}/${UI_MAPPING[id].folder}/${UI_MAPPING[id].file}.png`;
  }

  // Decision logic for folder (Singular 'item' and 'block' to match actual files)
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

  const folder = isBlock ? 'block' : 'item';
  
  // Return the local project path (Verified: public/mc-assets/item and public/mc-assets/block)
  return `${base}/${folder}/${id}.png`;
};
