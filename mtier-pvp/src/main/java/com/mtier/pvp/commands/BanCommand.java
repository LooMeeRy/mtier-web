package com.mtier.pvp.commands;

import com.mtier.pvp.MTierPvP;
import com.mtier.pvp.manager.RoomManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BanCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;

        RoomManager.DuelRoom room = MTierPvP.getInstance().getRoomManager().getPlayerRoom(player);
        if (room == null || (room.getPhase() != RoomManager.Phase.BAN_PHASE && room.getPhase() != RoomManager.Phase.SELECTION_PHASE)) {
            player.sendMessage("§6§lMTier §8» §cYou are not in a valid phase!");
            return true;
        }

        if (room.getPhase() == RoomManager.Phase.SELECTION_PHASE) {
            MTierPvP.getInstance().getPvPListener().getBanCategoryMenu().open(player);
            return true;
        }

        if (room.getTurnPlayer().equals(player.getUniqueId())) {
            MTierPvP.getInstance().getPvPListener().getBanCategoryMenu().open(player);
        } else {
            MTierPvP.getInstance().getPvPListener().getBanWaitMenu().open(player, room);
        }

        return true;
    }
}
