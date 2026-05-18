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

export async function POST(request: Request) {
  try {
    const authHeader = request.headers.get("authorization");
    const secret = process.env.MINECRAFT_PLUGIN_SECRET;

    // 1. Verify Secret Key
    if (!secret || authHeader !== `Bearer ${secret}`) {
      return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
    }

    const body = await request.json();
    const { uuid, username, event, metadata, timestamp, mode, mmr } = body;

    // 2. Handle Events
    switch (event) {
      case "PLAYER_JOIN":
        // Smart Upsert logic
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
        } else {
          player = await prisma.player.update({
            where: { id: player.id },
            data: { username: username, updatedAt: new Date(timestamp) }
          });
        }

        // Initialize Default Tiers
        const activeModes = ["PvP", "Bridge"];
        for (const m of activeModes) {
          await prisma.tier.upsert({
            where: { playerId_gamemode: { playerId: player.id, gamemode: m } },
            update: {},
            create: { playerId: player.id, gamemode: m, rank: "Wood", mmr: 0 }
          });
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

      case "UPDATE_MMR":
        const targetPlayer = await prisma.player.findUnique({ where: { username: username } });
        if (!targetPlayer) return NextResponse.json({ error: "Player not found" }, { status: 404 });

        const newRank = getRankFromMMR(mmr);
        await prisma.tier.upsert({
            where: { playerId_gamemode: { playerId: targetPlayer.id, gamemode: mode } },
            update: { mmr: mmr, rank: newRank },
            create: { playerId: targetPlayer.id, gamemode: mode, mmr: mmr, rank: newRank }
        });
        return NextResponse.json({ success: true });

      default:
        return NextResponse.json({ error: "Unknown event type" }, { status: 400 });
    }
  } catch (error) {
    console.error("[Sync Error]", error);
    return NextResponse.json({ error: "Internal Server Error" }, { status: 500 });
  }
}
