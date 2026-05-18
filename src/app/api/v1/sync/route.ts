import { NextResponse } from "next/server";
import prisma from "@/lib/prisma";

export async function POST(request: Request) {
  try {
    const authHeader = request.headers.get("authorization");
    const secret = process.env.MINECRAFT_PLUGIN_SECRET;

    // 1. Verify Secret Key
    if (!secret || authHeader !== `Bearer ${secret}`) {
      return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
    }

    const body = await request.json();
    const { uuid, username, event, metadata, timestamp } = body;

    console.log(`[Sync] Received ${event} for ${username} (${uuid})`);

    // 2. Handle Events
    switch (event) {
      case "PLAYER_JOIN":
        // A. Smart Upsert: Find by UUID first, then by Username
        let player = await prisma.player.findUnique({ where: { uuid: uuid } });

        if (!player) {
          // If UUID not found, check if username exists (maybe from seed data)
          const existingByUsername = await prisma.player.findUnique({ where: { username: username } });
          
          if (existingByUsername) {
            // Link existing record with the new UUID
            player = await prisma.player.update({
              where: { id: existingByUsername.id },
              data: { uuid: uuid, updatedAt: new Date(timestamp) }
            });
          } else {
            // Create brand new player
            player = await prisma.player.create({
              data: {
                uuid: uuid,
                username: username,
                createdAt: new Date(timestamp),
                updatedAt: new Date(timestamp)
              }
            });
          }
        } else {
          // Player exists with UUID, just update username/timestamp
          player = await prisma.player.update({
            where: { id: player.id },
            data: { username: username, updatedAt: new Date(timestamp) }
          });
        }

        // B. Initialize Default Tiers (Wood Rank) for all modes if they don't exist
        const activeModes = ["PvP", "Bridge"];
        for (const mode of activeModes) {
          await prisma.tier.upsert({
            where: { playerId_gamemode: { playerId: player.id, gamemode: mode } },
            update: {}, // Don't change if exists
            create: {
              playerId: player.id,
              gamemode: mode,
              rank: "Wood",
              mmr: 0
            }
          });
        }
        break;

      case "MATCH_END":
        // Future: Handle match results and MMR updates
        // Example: await prisma.match.create({ data: { ... } })
        break;

      default:
        return NextResponse.json({ error: "Unknown event type" }, { status: 400 });
    }

    return NextResponse.json({ success: true, message: `Event ${event} processed` });
  } catch (error) {
    console.error("[Sync Error]", error);
    return NextResponse.json({ error: "Internal Server Error" }, { status: 500 });
  }
}
