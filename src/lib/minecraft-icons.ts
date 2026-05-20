/**
 * ULTRA-RELIABLE Minecraft Icon Fetcher
 * Optimized for High-Quality 3D Block Renders and Sharp 2D Items.
 */
export const getMcIcon = (type: string) => {
  if (!type || type.toLowerCase() === 'air') return `https://raw.githubusercontent.com/PrismarineJS/minecraft-assets/master/data/1.21.1/items/barrier.png`;

  let id = type.toLowerCase();
  
  // Bukkit Material -> Minecraft Texture Naming Fixes
  id = id.replace('golden_', 'gold_');
  id = id.replace('wooden_', 'wood_');
  
  const officialBase = `https://raw.githubusercontent.com/InventivetalentDev/minecraft-assets/1.21.1/assets/minecraft/textures`;
  
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
  };

  if (blocks[id]) {
    return `${officialBase}/block/${blocks[id]}.png`;
  }

  // Special cases for Ranks/Categories
  if (id === 'pvp') return `${officialBase}/item/diamond_sword.png`;
  if (id === 'manhunt') return `${officialBase}/item/compass.png`;
  if (id === 'netherstar') return `${officialBase}/item/nether_star.png`;

  // Standard item path
  return `${officialBase}/item/${id}.png`;
};
