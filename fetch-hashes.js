const items = [
  { id: 'minecraft:diamond_sword', name: 'Diamond Sword' },
  { id: 'minecraft:grass_block', name: 'Grass Block' },
  { id: 'minecraft:oak_planks', name: 'Oak Planks' },
  { id: 'minecraft:compass', name: 'Compass' },
  { id: 'minecraft:nether_star', name: 'Nether Star' },
  { id: 'minecraft:diamond', name: 'Diamond' },
  { id: 'minecraft:gold_ingot', name: 'Gold Ingot' },
  { id: 'minecraft:iron_ingot', name: 'Iron Ingot' },
  { id: 'minecraft:stone', name: 'Stone' },
  { id: 'minecraft:oak_log', name: 'Oak Log' },
  { id: 'minecraft:amethyst_shard', name: 'Amethyst Shard' },
  { id: 'minecraft:emerald', name: 'Emerald' },
  { id: 'minecraft:raw_copper', name: 'Raw Copper' },
  { id: 'minecraft:experience_bottle', name: 'Experience Bottle' },
  { id: 'minecraft:map', name: 'Map' },
  { id: 'minecraft:writable_book', name: 'Book and Quill' },
  { id: 'minecraft:recovery_compass', name: 'Recovery Compass' },
  { id: 'minecraft:redstone', name: 'Redstone' },
];

async function getHashes() {
  const mapping = {};
  for (const item of items) {
    try {
      console.log(`Fetching ${item.id}...`);
      let res = await fetch(`https://blocksitems.com/items/${item.id}`);
      if (res.status === 404) {
        res = await fetch(`https://blocksitems.com/blocks/${item.id}`);
      }
      const html = await res.text();
      // Look for the image with alt matching the display name
      const regex = new RegExp(`<img src="\/api\/v1\/images\/([a-f0-9]{64})\\?w=128&h=128" alt="${item.name}"`);
      let match = html.match(regex);
      
      if (!match) {
          // Try alternative alt
          const regex2 = new RegExp(`<img src="\/api\/v1\/images\/([a-f0-9]{64})\\?w=128&h=128" alt=""`);
          match = html.match(regex2);
      }

      if (match) {
        mapping[item.id.split(':')[1]] = match[1];
      } else {
        console.warn(`No hash found for ${item.id}`);
      }
    } catch (e) {
      console.error(`Failed to fetch ${item.id}: ${e.message}`);
    }
  }
  console.log(JSON.stringify(mapping, null, 2));
}

getHashes();
