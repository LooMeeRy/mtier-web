const { PrismaClient } = require('@prisma/client')
const prisma = new PrismaClient()

async function main() {
  await prisma.match.deleteMany({})
  await prisma.tier.deleteMany({})
  await prisma.achievement.deleteMany({})
  await prisma.player.deleteMany({})

  const achievements = await Promise.all([
    prisma.achievement.create({ data: { name: 'Top 1', description: 'Be the number one in any gamemode', icon: 'Gold_Block' } }),
    prisma.achievement.create({ data: { name: 'Hard Grinder', description: 'Play more than 50 matches in a week', icon: 'Diamond_Pickaxe' } }),
    prisma.achievement.create({ data: { name: 'Unstoppable', description: 'Win 10 matches in a row', icon: 'Enchanted_Golden_Apple' } }),
    prisma.achievement.create({ data: { name: 'Verified Pro', description: 'Participate in an official tournament', icon: 'Netherite_Sword' } }),
  ])

  const players = [
    { 
      username: 'Loomeery', 
      tiers: { 
        create: [
          { gamemode: 'PvP', rank: 'NetherStar', mmr: 5000, winsAfterNS: 12 }, 
          { gamemode: 'Bridge', rank: 'Emerald', mmr: 2500 }
        ] 
      },
      achievements: { connect: [{ id: achievements[1].id }, { id: achievements[3].id }] }
    },
    { 
      username: 'Dream', 
      tiers: { 
        create: [
          { gamemode: 'PvP', rank: 'NetherStar', mmr: 10000, winsAfterNS: 150 }, 
          { gamemode: 'Bridge', rank: 'Diamond', mmr: 8000 }
        ] 
      },
      achievements: { connect: [{ id: achievements[0].id }, { id: achievements[2].id }] }
    },
  ]

  for (const p of players) {
    const player = await prisma.player.create({ data: p })

    // BRIDGE MATCHES (Team-based)
    for (let i = 0; i < 5; i++) {
        const isWin = Math.random() > 0.4
        const allies = [{ name: player.username, kills: 12, deaths: 3, goals: 3 }, { name: 'Ally_One', kills: 8, deaths: 5, goals: 2 }]
        const enemies = [{ name: 'Hostile_1', kills: 10, deaths: 10, goals: 4 }, { name: 'Hostile_2', kills: 5, deaths: 12, goals: 1 }]
        await prisma.match.create({
          data: {
            playerId: player.id,
            gamemode: 'Bridge',
            matchType: 'DOUBLE',
            result: isWin ? 'WIN' : 'LOSS',
            mmrChange: isWin ? 25 : -15,
            opponent: 'Hostile_Squad',
            map: 'Bridge Core',
            duration: 420,
            detailsJson: JSON.stringify({ allies, enemies, score: isWin ? '5 - 3' : '2 - 5' })
          }
        })
    }
    
    // PVP MATCHES (1v1 focused)
    for (let i = 0; i < 5; i++) {
        const isWin = Math.random() > 0.5
        const pvpDetails = {
            stats: {
                accuracy: isWin ? "42%" : "28%",
                totalHits: isWin ? 142 : 89,
                damageDealt: isWin ? "254.5" : "112.0",
                avgCps: isWin ? 14.5 : 9.2,
                healthRemaining: isWin ? "4.5 ❤" : "0.0"
            },
            opponentInfo: {
                name: 'Elite_PvPer',
                rank: 'Diamond',
                ping: '24ms'
            }
        }
        await prisma.match.create({
            data: {
              playerId: player.id,
              gamemode: 'PvP',
              matchType: 'SOLO',
              result: isWin ? 'WIN' : 'LOSS',
              mmrChange: isWin ? 18 : -12,
              opponent: 'Elite_PvPer',
              duration: 125,
              detailsJson: JSON.stringify(pvpDetails)
            }
        })
    }
  }

  console.log('Bridge vs PvP contextual seed data created!')
}

main().catch(e => { console.error(e); process.exit(1) }).finally(async () => { await prisma.$disconnect() })
