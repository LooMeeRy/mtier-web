/**
 * ULTRA-RELIABLE Minecraft Icon Fetcher
 * Optimized for High-Quality 3D Block Renders and Sharp 2D Items.
 */
export const getMcIcon = (type: string) => {
  if (!type || type.toLowerCase() === 'air') return `https://raw.githubusercontent.com/PrismarineJS/minecraft-assets/master/data/1.21.1/items/barrier.png`;

  let id = type.toLowerCase();
  
  // Mapping for 3D Blocks (Manual Overrides for categories)
  const blocks: Record<string, string> = {
    'survival': 'grass_block',
    'bridge': 'oak_planks',
    'stone': 'stone',
    'wood': 'oak_log',
    'gold_block': 'gold_block',
    'netherite_block': 'netherite_block',
    'diamond_block': 'diamond_block',
    'emerald_block': 'emerald_block',
    'global': 'oak_log', // Fallback for Assigned Sector
  };

  // Explicit Item Mapping for Bukkit -> Asset Filename
  const itemOverrides: Record<string, string> = {
    'pvp': 'diamond_sword',
    'manhunt': 'compass',
    'netherstar': 'nether_star',
    'empty_map': 'map',
    'book_and_quill': 'writable_book',
    'clock': 'clock_00',
    'recovery_compass': 'recovery_compass_00',
    'experience_bottle': 'experience_bottle',
    'enchanted_golden_apple': 'golden_apple',
    'golden_apple': 'golden_apple',
    'golden_sword': 'gold_sword',
    'golden_axe': 'gold_axe',
    'golden_pickaxe': 'gold_pickaxe',
    'golden_shovel': 'gold_shovel',
    'golden_hoe': 'gold_hoe',
    'golden_helmet': 'gold_helmet',
    'golden_chestplate': 'gold_chestplate',
    'golden_leggings': 'gold_leggings',
    'golden_boots': 'gold_boots',
    'wooden_sword': 'wood_sword',
    'wooden_axe': 'wood_axe',
    'wooden_pickaxe': 'wood_pickaxe',
    'wooden_shovel': 'wood_shovel',
    'wooden_hoe': 'wood_hoe',
  };

  const officialBase = `https://raw.githubusercontent.com/InventivetalentDev/minecraft-assets/1.21.1/assets/minecraft/textures`;
  
  if (blocks[id]) {
    return `${officialBase}/block/${blocks[id]}.png`;
  }

  if (itemOverrides[id]) {
    return `${officialBase}/item/${itemOverrides[id]}.png`;
  }

  // Standard item path fallback
  return `${officialBase}/item/${id}.png`;
};
