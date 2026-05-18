"use server";

import prisma from "@/lib/prisma";

export async function getPlayerWithTiers(username: string) {
  try {
    const player = await prisma.player.findUnique({
      where: { username },
      include: {
        tiers: {
          orderBy: {
            gamemode: 'asc',
          },
        },
        matches: {
          orderBy: {
            createdAt: 'desc',
          },
          take: 20,
        },
        achievements: {
          orderBy: {
            createdAt: 'asc',
          },
        },
      },
    });
    return player;
  } catch (error) {
    console.error("Error fetching player:", error);
    return null;
  }
}
