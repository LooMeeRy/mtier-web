/**
 * ULTRA-RELIABLE Minecraft Icon Fetcher
 * Optimized for High-Quality 3D Block Renders and Sharp 2D Items.
 */
export const getMcIcon = (type: string) => {
  if (!type || type.toLowerCase() === 'air') return `https://raw.githubusercontent.com/PrismarineJS/minecraft-assets/master/data/1.21.1/items/barrier.png`;

  let id = type.toLowerCase();
  
  // This repo has pre-rendered 2D sprites for EVERYTHING (Shields, Blocks, etc.)
  const repoBase = `https://raw.githubusercontent.com/anish-shanbhag/minecraft-api/master/public/images`;
  
  // Mapping for 3D Blocks (Overrides for categories)
  const blocks: Record<string, string> = {
    'survival': 'grass_block',
    'bridge': 'oak_planks',
    'stone': 'stone',
    'wood': 'oak_log',
    'gold_block': 'gold_block',
    'netherite_block': 'netherite_block',
    'diamond_block': 'diamond_block',
    'emerald_block': 'emerald_block',
    'global': 'grass_block', 
  };

  // Naming Fixes for this specific repo
  // It uses 'gold_sword' but 'golden_apple'
  if (id.includes('golden_') && !id.includes('apple') && !id.includes('carrot')) {
    id = id.replace('golden_', 'gold_');
  }
  if (id.includes('wooden_')) {
    id = id.replace('wooden_', 'wood_');
  }

  // Explicit mapping for dashboard
  const overrides: Record<string, string> = {
    'pvp': 'items/diamond_sword.png',
    'manhunt': 'items/compass.png',
    'netherstar': 'items/nether_star.png',
    'empty_map': 'items/map.png',
    'book_and_quill': 'items/writable_book.png',
    'clock': 'items/clock_00.png',
    'recovery_compass': 'items/recovery_compass_00.png',
    'experience_bottle': 'items/experience_bottle.png',
  };

  if (blocks[id]) {
    return `${repoBase}/blocks/${blocks[id]}.png`;
  }

  if (overrides[id]) {
    return `${repoBase}/${overrides[id]}`;
  }

  // Standard item path for all other Bukkit materials
  return `${repoBase}/items/${id}.png`;
};
