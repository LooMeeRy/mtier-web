const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();

async function main() {
  console.log("Fixing Match Opponent Data...");

  const players = await prisma.player.findMany({
    include: { matches: true }
  });

  for (const player of players) {
    for (const match of player.matches) {
      if (match.opponent === player.username) {
        console.log(`Found faulty match for ${player.username} (ID: ${match.id})`);
        
        let details = {};
        try {
          details = JSON.parse(match.detailsJson);
        } catch (e) {}

        const participants = details.allParticipants || [];
        const realOpponent = participants.find(p => p.name !== player.username)?.name;

        if (realOpponent) {
          console.log(`Updating opponent to: ${realOpponent}`);
          await prisma.match.update({
            where: { id: match.id },
            data: { opponent: realOpponent }
          });
        } else {
          console.log(`No clear opponent found in details. Defaulting to 'Unknown'`);
          await prisma.match.update({
            where: { id: match.id },
            data: { opponent: 'Unknown' }
          });
        }
      }
    }
  }

  console.log("Data Fix Complete.");
}

main()
  .catch(e => {
    console.error(e);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
