/**
 * ULTRA-RELIABLE Minecraft Icon Fetcher
 * Optimized for High-Quality 3D Block Renders and Sharp 2D Items.
 */
export const getMcIcon = (type: string) => {
  if (!type || type.toLowerCase() === 'air') return `https://mc-heads.net/item/barrier`;

  let id = type.toLowerCase();
  
  // mc-heads.net is extremely stable and handles both items/blocks in one API
  const base = `https://mc-heads.net/item`;
  
  // Mapping for Dashboard & Ranks
  const mapping: Record<string, string> = {
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
    'survival': 'grass_block',
    'global': 'grass_block',
    'authorization': 'command_block',
    'assigned sector': 'compass',
    'manhunt': 'compass',
    'empty_map': 'map',
    'book_and_quill': 'writable_book',
    'experience_bottle': 'experience_bottle',
    'enchanted_golden_apple': 'enchanted_golden_apple',
    'golden_apple': 'golden_apple',
    'clock': 'clock',
    'recovery_compass': 'recovery_compass',
  };

  const target = mapping[id] || id;

  // mc-heads.net expects the material name without .png
  return `${base}/${target}`;
};
