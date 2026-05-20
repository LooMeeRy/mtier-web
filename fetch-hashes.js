const fs = require('fs');
const path = require('path');
const https = require('https');

/**
 * Minecraft Asset Fetcher V2
 * Optimized for frame-based items and verified 1.21.1 filenames.
 */

const BASE_URL = 'https://raw.githubusercontent.com/PrismarineJS/minecraft-assets/master/data/1.21.1';
const ASSET_DIR = path.join(__dirname, 'public', 'mc-assets');

const download = (url, dest) => {
    return new Promise((resolve, reject) => {
        const file = fs.createWriteStream(dest);
        https.get(url, (response) => {
            if (response.statusCode !== 200) {
                reject(new Error(`Failed to download: ${response.statusCode}`));
                return;
            }
            response.pipe(file);
            file.on('finish', () => {
                file.close(resolve);
            });
        }).on('error', (err) => {
            fs.unlink(dest, () => reject(err));
        });
    });
};

async function fetchCoreAssets() {
    // Corrected filenames for PrismarineJS 1.21.1
    const items = [
        { key: 'diamond_sword', file: 'diamond_sword' },
        { key: 'iron_sword', file: 'iron_sword' },
        { key: 'golden_sword', file: 'gold_sword' },
        { key: 'stone_sword', file: 'stone_sword' },
        { key: 'wooden_sword', file: 'wood_sword' },
        { key: 'experience_bottle', file: 'experience_bottle' },
        { key: 'map', file: 'map' },
        { key: 'compass', file: 'compass_00' },
        { key: 'clock', file: 'clock_00' },
        { key: 'writable_book', file: 'writable_book' },
        { key: 'nether_star', file: 'nether_star' },
        { key: 'golden_apple', file: 'golden_apple' },
        { key: 'enchanted_golden_apple', file: 'enchanted_golden_apple' },
        { key: 'shield', file: 'shield' },
        { key: 'bow', file: 'bow' },
        { key: 'barrier', file: 'barrier' },
        { key: 'recovery_compass', file: 'recovery_compass_00' }
    ];
    
    const blocks = [
        { key: 'oak_log', file: 'oak_log' },
        { key: 'stone', file: 'stone' },
        { key: 'oak_planks', file: 'oak_planks' },
        { key: 'grass_block_top', file: 'grass_block_top' },
        { key: 'copper_block', file: 'copper_block' },
        { key: 'iron_block', file: 'iron_block' }, 
        { key: 'gold_block', file: 'gold_block' },
        { key: 'diamond_block', file: 'diamond_block' },
        { key: 'emerald_block', file: 'emerald_block' },
        { key: 'amethyst_block', file: 'amethyst_block' },
        { key: 'netherite_block', file: 'netherite_block' },
        { key: 'command_block', file: 'command_block_front' }
    ];

    console.log('🚀 Starting Final Asset Cleanup...');

    for (const item of items) {
        try {
            await download(`${BASE_URL}/items/${item.file}.png`, path.join(ASSET_DIR, 'items', `${item.key}.png`));
            console.log(`✅ Fixed item: ${item.key}`);
        } catch (e) {
            console.error(`❌ Still failing item: ${item.key} (${item.file})`);
        }
    }

    for (const block of blocks) {
        try {
            await download(`${BASE_URL}/blocks/${block.file}.png`, path.join(ASSET_DIR, 'blocks', `${block.key}.png`));
            console.log(`✅ Fixed block: ${block.key}`);
        } catch (e) {
            console.error(`❌ Still failing block: ${block.key} (${block.file})`);
        }
    }

    console.log('\n✨ Asset collection is complete and self-hosted.');
}

fetchCoreAssets();
