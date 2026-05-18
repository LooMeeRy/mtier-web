import { NextResponse } from "next/server";
import prisma from "@/lib/prisma";

function getRankFromMMR(mmr: number): string {
    if (mmr >= 4000) return "NetherStar";
    if (mmr >= 3500) return "Amethyst";
    if (mmr >= 3000) return "Diamond";
    if (mmr >= 2500) return "Emerald";
    if (mmr >= 2000) return "Gold";
    if (mmr >= 1500) return "Iron";
    if (mmr >= 1000) return "Copper";
    if (mmr >= 500) return "Stone";
    return "Wood";
}

// Simple Elo calculation helper
function calculateMmrChange(winnerMmr: number, loserMmr: number): number {
    const kFactor = 32;
    const expectedScore = 1 / (1 + Math.pow(10, (loserMmr - winnerMmr) / 400));
    return Math.round(kFactor * (1 - expectedScore));
}

export async function POST(request: Request) {
  try {
    const authHeader = request.headers.get("authorization");
    const secret = process.env.MINECRAFT_PLUGIN_SECRET;

    if (!secret || authHeader !== `Bearer ${secret}`) {
      return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
    }

    const body = await request.json();
    let { event, gamemode, matchType, duration, participants, metadata, timestamp, uuid, username, mode, mmr } = body;

    // Normalize gamemode
    if (gamemode) {
        gamemode = gamemode.toLowerCase() === 'pvp' ? 'PvP' : gamemode.charAt(0).toUpperCase() + gamemode.slice(1).toLowerCase();
    }

    switch (event) {
      case "PLAYER_JOIN":
        let player = await prisma.player.findUnique({ where: { uuid: uuid } });
        if (!player) {
          const existingByUsername = await prisma.player.findUnique({ where: { username: username } });
          if (existingByUsername) {
            player = await prisma.player.update({
              where: { id: existingByUsername.id },
              data: { uuid: uuid, updatedAt: new Date(timestamp) }
            });
          } else {
            player = await prisma.player.create({
              data: { uuid: uuid, username: username, createdAt: new Date(timestamp), updatedAt: new Date(timestamp) }
            });
          }
        }
        return NextResponse.json({ success: true });

      case "GET_STATS":
        const pStats = await prisma.player.findUnique({
          where: { username: username },
          include: { tiers: true }
        });
        if (!pStats) return NextResponse.json({ error: "Not found" }, { status: 404 });
        
        const statsMap: Record<string, any> = {};
        pStats.tiers.forEach(t => {
            statsMap[t.gamemode] = { mmr: t.mmr, rank: t.rank };
        });
        return NextResponse.json({ stats: statsMap });

      case "SUBMIT_MATCH":
        // 1. Process each participant
        for (const p of participants) {
            const dbPlayer = await prisma.player.findUnique({ where: { uuid: p.uuid }, include: { tiers: true } });
            if (!dbPlayer) continue;

            let currentTier = dbPlayer.tiers.find(t => t.gamemode === gamemode);
            if (!currentTier) {
                currentTier = await prisma.tier.create({
                    data: { playerId: dbPlayer.id, gamemode: gamemode, mmr: 1000, rank: "Iron" }
                });
            }

            // Simple win/loss MMR change for now
            // Future: Implement complex Multi-player Elo here based on 'p.placement'
            const change = p.winner ? 25 : -15;
            const newMmr = Math.max(0, currentTier.mmr + change);

            await prisma.tier.update({
                where: { id: currentTier.id },
                data: { mmr: newMmr, rank: getRankFromMMR(newMmr) }
            });

            // 2. Log Match for this player
            await prisma.match.create({
                data: {
                    playerId: dbPlayer.id,
                    gamemode: gamemode,
                    matchType: matchType,
                    result: p.winner ? "WIN" : "LOSS",
                    mmrChange: change,
                    opponent: "Multiplayer", // Legacy field
                    duration: duration,
                    detailsJson: JSON.stringify({
                        placement: p.placement,
                        personalStats: p.stats,
                        allParticipants: participants.map((all: any) => ({ name: all.name, winner: all.winner })),
                        metadata: metadata
                    })
                }
            });
        }
        return NextResponse.json({ success: true });

      default:
        return NextResponse.json({ error: "Unknown event" }, { status: 400 });
    }
  } catch (error) {
    console.error("[Sync Error]", error);
    return NextResponse.json({ error: "Internal Error" }, { status: 500 });
  }
}
