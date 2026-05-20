/**
 * ULTRA-RELIABLE Minecraft Icon Fetcher
 * Optimized for High-Quality 3D Block Renders and Sharp 2D Items.
 */
/**
 * SELF-HOSTED Minecraft Icon System
 * Serves assets directly from /public/mc-assets/ for maximum speed and reliability.
 */
export const getMcIcon = (type: string) => {
  if (!type || type.toLowerCase() === 'air') return `/mc-assets/item/barrier.png`;

  // Standardize: lowercase, replace spaces with underscores
  let id = type.toLowerCase()
    .trim()
    .replace(/\s+/g, '_')
    .replace('golden_', 'gold_')
    .replace('wooden_', 'wood_');
  
  const base = `/mc-assets`;

  // 1. Explicit UI/Rank Mapping (Must match your actual filenames in /public/mc-assets/block)
  const UI_MAPPING: Record<string, { folder: string, file: string }> = {
    'wood': { folder: 'block', file: 'oak_log' },
    'stone': { folder: 'block', file: 'stone' },
    'copper': { folder: 'block', file: 'copper_block' },
    'iron': { folder: 'block', file: 'iron_block' },
    'gold': { folder: 'block', file: 'gold_block' },
    'emerald': { folder: 'block', file: 'emerald_block' },
    'amethyst': { folder: 'block', file: 'amethyst_block' },
    'diamond': { folder: 'block', file: 'diamond_block' },
    'netherite': { folder: 'block', file: 'netherite_block' },
    'netherstar': { folder: 'item', file: 'nether_star' },
    'bridge': { folder: 'block', file: 'oak_planks' },
    'pvp': { folder: 'item', file: 'diamond_sword' },
    'survival': { folder: 'block', file: 'grass_block_top' },
    'global': { folder: 'block', file: 'grass_block_top' },
    'authorization': { folder: 'item', file: 'experience_bottle' },
    'assigned_sector': { folder: 'item', file: 'map' },
    'assigned sector': { folder: 'item', file: 'map' },
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
                  ['stone', 'dirt', 'grass_block', 'sand', 'gravel', 'bedrock', 'obsidian', 'leaves'].some(k => id.includes(k));

  const folder = isBlock ? 'block' : 'item';
  
  return `${base}/${folder}/${id}.png`;
};
