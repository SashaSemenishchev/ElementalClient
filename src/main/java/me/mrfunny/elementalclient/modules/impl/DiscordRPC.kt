package me.mrfunny.elementalclient.modules.impl

import me.mrfunny.elementalclient.ElementalClient
import me.mrfunny.elementalclient.modules.Module

object DiscordRPC : Module("Discord RPC", "Shows information about the client on Discord") {
    override fun onEnable() {
        ElementalClient.discordHandler.apply {
            this.start()
            this.resendActivity()
        }
    }

    override fun onDisable() {
        ElementalClient.discordHandler.shutdown()
    }
}