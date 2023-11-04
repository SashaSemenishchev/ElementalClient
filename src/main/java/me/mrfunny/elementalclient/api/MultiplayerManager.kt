package me.mrfunny.elementalclient.api

import me.mrfunny.elementalclient.util.MinecraftInstance
import net.minecraft.entity.player.EntityPlayer

object MultiplayerManager : MinecraftInstance() {
    fun isPlayingWithSameClient(player: EntityPlayer): Boolean {
        if(player == mc.thePlayer) return true

        // TODO: Add network of players to actually check if they're playing together
        return false
    }
}