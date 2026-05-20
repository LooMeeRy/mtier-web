/**
 * ULTRA-RELIABLE Minecraft Icon Fetcher
 * Optimized for High-Quality 3D Block Renders and Sharp 2D Items.
 */
export const getMcIcon = (type: string) => {
  if (!type || type.toLowerCase() === 'air') return `https://raw.githubusercontent.com/PrismarineJS/minecraft-assets/master/data/1.21.1/items/barrier.png`;

  let id = type.toLowerCase();
  
  // Faithful-Pack is a reliable 1:1 mirror of vanilla assets
  const officialBase = `https://raw.githubusercontent.com/Faithful-Pack/Default-Java/1.21.1/assets/minecraft/textures`;
  
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
    'global': 'grass_block', 
  };

  // Explicit Item Mapping for Bukkit -> Asset Filename
  // Minecraft 1.21.1 uses 'golden_sword', 'apple', etc.
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
  };

  if (blocks[id]) {
    return `${officialBase}/block/${blocks[id]}.png`;
  }

  const filename = itemOverrides[id] || id;
  return `${officialBase}/item/${filename}.png`;
};
