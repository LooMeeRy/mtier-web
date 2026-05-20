/**
 * ULTRA-RELIABLE Minecraft Icon Fetcher
 * Optimized for High-Quality 3D Block Renders and Sharp 2D Items.
 */
export const getMcIcon = (type: string) => {
  if (!type || type.toLowerCase() === 'air') return `https://raw.githubusercontent.com/PrismarineJS/minecraft-assets/master/data/1.21.1/items/barrier.png`;

  let id = type.toLowerCase();
  
  // Base for individual high-quality textures from 1.21.1
  const officialBase = `https://raw.githubusercontent.com/misode/mcmeta/1.21.1-assets/assets/minecraft/textures`;
  
  // Mapping for 3D Blocks (Using representative top/side textures)
  const blocks: Record<string, string> = {
    'survival': 'grass_block_top',
    'bridge': 'oak_planks',
    'stone': 'stone',
    'wood': 'oak_log',
    'gold_block': 'gold_block',
    'netherite_block': 'netherite_block',
    'diamond_block': 'diamond_block',
    'emerald_block': 'emerald_block',
    'global': 'grass_block_top', 
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
    'enchanted_golden_apple': 'enchanted_golden_apple',
    'golden_apple': 'golden_apple',
    'golden_sword': 'golden_sword',
    'golden_axe': 'golden_axe',
    'golden_pickaxe': 'golden_pickaxe',
    'golden_shovel': 'golden_shovel',
    'golden_hoe': 'golden_hoe',
    'golden_helmet': 'golden_helmet',
    'golden_chestplate': 'golden_chestplate',
    'golden_leggings': 'golden_leggings',
    'golden_boots': 'golden_boots',
  };

  if (blocks[id]) {
    return `${officialBase}/block/${blocks[id]}.png`;
  }

  const filename = itemOverrides[id] || id;
  return `${officialBase}/item/${filename}.png`;
};
