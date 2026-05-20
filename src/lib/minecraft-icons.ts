/**
 * ULTRA-RELIABLE Minecraft Icon Fetcher
 * Optimized for High-Quality 3D Block Renders and Sharp 2D Items.
 */
export const getMcIcon = (type: string) => {
  if (!type || type.toLowerCase() === 'air') return `https://raw.githubusercontent.com/PrismarineJS/minecraft-assets/master/data/1.21.1/items/barrier.png`;

  let id = type.toLowerCase();
  
  // Industry Standard: PrismarineJS assets are 100% hotlink-friendly and stable
  const base = `https://raw.githubusercontent.com/PrismarineJS/minecraft-assets/master/data/1.21.1`;
  
  // Mapping for Dashboard & Ranks (Using blocks for a solid look)
  const blockMapping: Record<string, string> = {
    'wood': 'oak_log',
    'stone': 'stone',
    'copper': 'copper_block',
    'iron': 'iron_block',
    'gold': 'gold_block',
    'emerald': 'emerald_block',
    'amethyst': 'amethyst_block',
    'diamond': 'diamond_block',
    'netherite': 'netherite_block',
    'netherstar': 'nether_star', // Item
    'bridge': 'oak_planks',
    'pvp': 'diamond_sword', // Item
    'survival': 'grass_block_top',
    'global': 'grass_block_top',
    'authorization': 'command_block',
    'assigned sector': 'compass',
  };

  // Explicit Item Mapping for Bukkit -> Asset Filename
  const itemOverrides: Record<string, string> = {
    'manhunt': 'compass',
    'empty_map': 'map',
    'book_and_quill': 'writable_book',
    'experience_bottle': 'experience_bottle',
    'enchanted_golden_apple': 'enchanted_golden_apple',
    'golden_apple': 'golden_apple',
  };

  const target = blockMapping[id] || itemOverrides[id] || id;

  // Determine if it's a block or item based on our mapping or name
  const isBlock = [
    'wood', 'stone', 'copper', 'iron', 'gold', 'emerald', 'amethyst', 'diamond', 'netherite',
    'bridge', 'survival', 'global', 'authorization'
  ].includes(id) || target.includes('_block') || target.includes('_log') || target.includes('_planks') || target === 'stone';

  const folder = isBlock ? 'blocks' : 'items';
  return `${base}/${folder}/${target}.png`;
};
