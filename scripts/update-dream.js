const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();

async function main() {
  const username = 'Dream';
  const newMmr = 2000;

  console.log(`Searching for player: ${username}`);
  
  let player = await prisma.player.findUnique({
    where: { username },
    include: { tiers: true }
  });

  if (!player) {
    console.log(`Player ${username} not found. Creating...`);
    player = await prisma.player.create({
      data: {
        username,
        tiers: {
          create: [
            { gamemode: 'PVP', rank: 'NetherStar', mmr: newMmr },
            { gamemode: 'BRIDGE', rank: 'NetherStar', mmr: newMmr }
          ]
        }
      }
    });
    console.log(`Created player ${username} with MMR ${newMmr}`);
  } else {
    console.log(`Updating MMR for ${username}...`);
    for (const tier of player.tiers) {
      await prisma.tier.update({
        where: { id: tier.id },
        data: { mmr: newMmr, rank: 'NetherStar' } // Setting to NetherStar because 2000 is high
      });
    }
    console.log(`Updated all tiers for ${username} to MMR ${newMmr}`);
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
