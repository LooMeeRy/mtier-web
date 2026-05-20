/**
 * ULTRA-RELIABLE Minecraft Icon Fetcher
 * Optimized for High-Quality 3D Block Renders and Sharp 2D Items.
 */
export const getMcIcon = (type: string) => {
  if (!type || type.toLowerCase() === 'air') return `https://raw.githubusercontent.com/PrismarineJS/minecraft-assets/master/data/1.21.1/items/barrier.png`;

  let id = type.toLowerCase();
  const mcAssetBase = `https://mcasset.cloud/1.21.1/assets/minecraft/textures`;
  
  // Mapping for Category Blocks (Top view for 2D feel)
  const categoryBlocks: Record<string, string> = {
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

  // Explicit Item Mapping for Bukkit -> Minecraft Asset Name
  const itemOverrides: Record<string, string> = {
    'pvp': 'diamond_sword',
    'manhunt': 'compass',
    'netherstar': 'nether_star',
    'empty_map': 'map',
    'book_and_quill': 'writable_book',
    'clock': 'clock',
    'recovery_compass': 'recovery_compass',
    'experience_bottle': 'experience_bottle',
    'enchanted_golden_apple': 'golden_apple',
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
    'wooden_sword': 'wooden_sword',
    'wooden_axe': 'wooden_axe',
    'wooden_pickaxe': 'wooden_pickaxe',
    'wooden_shovel': 'wooden_shovel',
    'wooden_hoe': 'wooden_hoe',
  };

  if (categoryBlocks[id]) {
    return `${mcAssetBase}/block/${categoryBlocks[id]}.png`;
  }

  const filename = itemOverrides[id] || id;
  
  // Dynamic switch between item and block folders
  // Most rank/item strings are items. If it ends with _block, it's a block.
  const folder = id.endsWith('_block') ? 'block' : 'item';
  return `${mcAssetBase}/${folder}/${filename}.png`;
};
