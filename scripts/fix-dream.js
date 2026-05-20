const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();

function getRankFromMMR(mmr) {
    if (mmr >= 4000) return "NetherStar";
    if (mmr >= 3500) return "Netherite";
    if (mmr >= 3000) return "Amethyst";
    if (mmr >= 2500) return "Diamond";
    if (mmr >= 2000) return "Emerald";
    if (mmr >= 1500) return "Gold";
    if (mmr >= 1000) return "Iron";
    if (mmr >= 500) return "Copper";
    if (mmr >= 250) return "Stone";
    return "Wood";
}

async function main() {
  const username = 'Dream';
  const targetMmr = 2000;
  const correctRank = getRankFromMMR(targetMmr);

  console.log(`Searching for player: ${username}`);
  
  let player = await prisma.player.findUnique({
    where: { username },
    include: { tiers: true }
  });

  if (player) {
    console.log(`Fixing MMR/Rank for ${username} to ${targetMmr} (${correctRank})...`);
    for (const tier of player.tiers) {
      await prisma.tier.update({
        where: { id: tier.id },
        data: { mmr: targetMmr, rank: correctRank }
      });
    }
    console.log(`Successfully fixed Dream's stats.`);
  } else {
    console.log(`Player ${username} not found. Skipping fix.`);
  }
}

main()
  .catch(e => {
    console.error(e);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
