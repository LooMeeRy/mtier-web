/**
 * ULTRA-RELIABLE Minecraft Icon Fetcher
 * Optimized for High-Quality 3D Block Renders and Sharp 2D Items.
 */
export const getMcIcon = (type: string) => {
  if (!type || type.toLowerCase() === 'air') return `https://cdn.jsdelivr.net/gh/PrismarineJS/minecraft-assets@master/data/1.21.1/items/barrier.png`;

  let id = type.toLowerCase();
  
  // Use JsDelivr CDN - Best for speed, CORS, and high-availability
  const base = `https://cdn.jsdelivr.net/gh/PrismarineJS/minecraft-assets@master/data/1.21.1`;
  
  // Mapping for Dashboard & Ranks (Using 1:1 vanilla names)
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
    'netherstar': 'nether_star',
    'bridge': 'oak_planks',
    'pvp': 'diamond_sword',
    'survival': 'grass_block_top',
    'global': 'grass_block_top',
    'authorization': 'command_block',
    'assigned sector': 'compass',
  };

  const itemOverrides: Record<string, string> = {
    'manhunt': 'compass',
    'empty_map': 'map',
    'book_and_quill': 'writable_book',
    'experience_bottle': 'experience_bottle',
    'enchanted_golden_apple': 'enchanted_golden_apple',
    'golden_apple': 'golden_apple',
    'clock': 'clock_00',
    'recovery_compass': 'recovery_compass_00',
  };

  const target = blockMapping[id] || itemOverrides[id] || id;

  // Decision logic for folder
  const isBlock = [
    'wood', 'stone', 'copper', 'iron', 'gold', 'emerald', 'amethyst', 'diamond', 'netherite',
    'bridge', 'survival', 'global', 'authorization', 'grass_block', 'oak_log', 'stone_block'
  ].includes(id) || target.includes('_block') || target.includes('_log') || target.includes('_planks') || target === 'stone';

  const folder = isBlock ? 'blocks' : 'items';
  return `${base}/${folder}/${target}.png`;
};
