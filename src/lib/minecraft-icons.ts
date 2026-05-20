/**
 * ULTRA-RELIABLE Minecraft Icon Fetcher
 * Optimized for High-Quality 3D Block Renders and Sharp 2D Items.
 */
export const getMcIcon = (type: string) => {
  if (!type || type.toLowerCase() === 'air') return `https://raw.githubusercontent.com/PrismarineJS/minecraft-assets/master/data/1.21.1/items/barrier.png`;

  const id = type.toLowerCase();
  
  // DIRECT VERIFIED MAPPING (Misode's high-reliability vanilla mirror)
  const STRICT_MAPPING: Record<string, string> = {
    // Ranks
    'wood': 'https://raw.githubusercontent.com/misode/mcmeta/1.21.1-assets/assets/minecraft/textures/block/oak_log.png',
    'stone': 'https://raw.githubusercontent.com/misode/mcmeta/1.21.1-assets/assets/minecraft/textures/block/stone.png',
    'copper': 'https://raw.githubusercontent.com/misode/mcmeta/1.21.1-assets/assets/minecraft/textures/block/copper_block.png',
    'iron': 'https://raw.githubusercontent.com/misode/mcmeta/1.21.1-assets/assets/minecraft/textures/block/iron_block.png',
    'gold': 'https://raw.githubusercontent.com/misode/mcmeta/1.21.1-assets/assets/minecraft/textures/block/gold_block.png',
    'emerald': 'https://raw.githubusercontent.com/misode/mcmeta/1.21.1-assets/assets/minecraft/textures/block/emerald_block.png',
    'amethyst': 'https://raw.githubusercontent.com/misode/mcmeta/1.21.1-assets/assets/minecraft/textures/block/amethyst_block.png',
    'diamond': 'https://raw.githubusercontent.com/misode/mcmeta/1.21.1-assets/assets/minecraft/textures/block/diamond_block.png',
    'netherite': 'https://raw.githubusercontent.com/misode/mcmeta/1.21.1-assets/assets/minecraft/textures/block/netherite_block.png',
    'netherstar': 'https://raw.githubusercontent.com/misode/mcmeta/1.21.1-assets/assets/minecraft/textures/item/nether_star.png',

    // Game Modes
    'bridge': 'https://raw.githubusercontent.com/misode/mcmeta/1.21.1-assets/assets/minecraft/textures/block/oak_planks.png',
    'pvp': 'https://raw.githubusercontent.com/misode/mcmeta/1.21.1-assets/assets/minecraft/textures/item/diamond_sword.png',
    'survival': 'https://raw.githubusercontent.com/misode/mcmeta/1.21.1-assets/assets/minecraft/textures/block/grass_block_top.png',
    'global': 'https://raw.githubusercontent.com/misode/mcmeta/1.21.1-assets/assets/minecraft/textures/block/grass_block_top.png',

    // Dashboard UI
    'authorization': 'https://raw.githubusercontent.com/misode/mcmeta/1.21.1-assets/assets/minecraft/textures/item/experience_bottle.png',
    'assigned sector': 'https://raw.githubusercontent.com/misode/mcmeta/1.21.1-assets/assets/minecraft/textures/item/map.png',
    'clock': 'https://raw.githubusercontent.com/misode/mcmeta/1.21.1-assets/assets/minecraft/textures/item/clock_00.png',
    'recovery_compass': 'https://raw.githubusercontent.com/misode/mcmeta/1.21.1-assets/assets/minecraft/textures/item/recovery_compass_00.png',
    'book_and_quill': 'https://raw.githubusercontent.com/misode/mcmeta/1.21.1-assets/assets/minecraft/textures/item/writable_book.png',
    'experience_bottle': 'https://raw.githubusercontent.com/misode/mcmeta/1.21.1-assets/assets/minecraft/textures/item/experience_bottle.png',
    'empty_map': 'https://raw.githubusercontent.com/misode/mcmeta/1.21.1-assets/assets/minecraft/textures/item/map.png',
  };

  if (STRICT_MAPPING[id]) {
    return STRICT_MAPPING[id];
  }

  // Fallback for match results (Dynamic construction)
  const base = `https://raw.githubusercontent.com/PrismarineJS/minecraft-assets/master/data/1.21.1`;
  const isBlock = id.includes('block') || id.includes('log') || id.includes('planks') || id === 'stone';
  const folder = isBlock ? 'blocks' : 'items';
  
  return `${base}/${folder}/${id}.png`;
};
