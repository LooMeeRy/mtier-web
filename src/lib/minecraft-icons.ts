/**
 * ULTRA-RELIABLE Minecraft Icon Fetcher
 * Optimized for High-Quality 3D Block Renders and Sharp 2D Items.
 */
export const getMcIcon = (type: string) => {
  const repoBase = `https://raw.githubusercontent.com/anish-shanbhag/minecraft-api/master/public/images`;
  const officialBase = `https://raw.githubusercontent.com/PrismarineJS/minecraft-assets/master/data/1.21.1`;
  
  // Mapping for 3D Blocks
  const blocks: Record<string, string> = {
    'Survival': 'grass_block',
    'Bridge': 'oak_planks',
    'Stone': 'stone',
    'Wood': 'oak_log',
    'Gold_Block': 'gold_block',
  };

  // Mapping for Items
  const items: Record<string, { base: string, path: string }> = {
    'PvP': { base: repoBase, path: 'items/diamond_sword.png' },
    'Manhunt': { base: officialBase, path: 'items/compass_00.png' },
    'NetherStar': { base: officialBase, path: 'items/nether_star.png' },
    'Diamond': { base: officialBase, path: 'items/diamond.png' },
    'Gold': { base: officialBase, path: 'items/gold_ingot.png' },
    'Iron': { base: officialBase, path: 'items/iron_ingot.png' },
    'Amethyst': { base: officialBase, path: 'items/amethyst_shard.png' },
    'Emerald': { base: officialBase, path: 'items/emerald.png' },
    'Copper': { base: officialBase, path: 'items/raw_copper.png' },
    'Experience_Bottle': { base: officialBase, path: 'items/experience_bottle.png' },
    'Empty_Map': { base: officialBase, path: 'items/map.png' },
    'Book_and_Quill': { base: officialBase, path: 'items/writable_book.png' },
    'Recovery_Compass': { base: officialBase, path: 'items/recovery_compass_00.png' },
    'Redstone_Dust': { base: officialBase, path: 'items/redstone.png' },
    'Clock': { base: officialBase, path: 'items/clock_00.png' },
    'Enchanted_Golden_Apple': { base: repoBase, path: 'items/golden_apple.png' }, // Fallback to golden_apple for now
    'Netherite_Sword': { base: repoBase, path: 'items/netherite_sword.png' },
    'Diamond_Pickaxe': { base: repoBase, path: 'items/diamond_pickaxe.png' },
    'Diamond_Sword': { base: repoBase, path: 'items/diamond_sword.png' },
    'Compass': { base: officialBase, path: 'items/compass_00.png' },
  };

  if (blocks[type]) {
    return `${repoBase}/blocks/${blocks[type]}.png`;
  }

  const item = items[type];
  if (item) {
    return `${item.base}/${item.path}`;
  }
  
  return `${officialBase}/items/barrier.png`;
};
