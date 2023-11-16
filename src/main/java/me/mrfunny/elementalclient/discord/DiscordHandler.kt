package me.mrfunny.elementalclient.discord

import dev.cbyrne.kdiscordipc.KDiscordIPC
import dev.cbyrne.kdiscordipc.core.event.impl.ReadyEvent
import dev.cbyrne.kdiscordipc.data.activity.Activity
import kotlinx.coroutines.*
import me.mrfunny.elementalclient.ElementalClient
import me.mrfunny.elementalclient.util.MinecraftInstance
import net.minecraft.client.gui.GuiMultiplayer
import net.minecraft.client.multiplayer.ServerData
import net.minecraft.util.EnumChatFormatting
import java.net.UnknownHostException

class DiscordHandler {
    lateinit var discord: KDiscordIPC
    private var currentActivity: String = "In the Main Menu"

    private fun makeSocket() = KDiscordIPC("1172162054585593986").apply {
        runBlocking {
            on<ReadyEvent> {
                resendActivity()
            }
        }
    }

    fun start() = GlobalScope.launch {
        makeSocket().apply {
            this@DiscordHandler.discord = this
            this.connect()
        }
    }

    fun mainMenu() {
        updateActivity("In the Main Menu").start()
    }
    fun updateActivity(text: String) = GlobalScope.launch {
        this@DiscordHandler.currentActivity = text
        if(!isConnected()) return@launch
        val mc = MinecraftInstance.mc
        discord.activityManager.setActivity(
            Activity(
                text,
                "elementalclient.net",
                Activity.Timestamps(ElementalClient.startupTime, null),
                Activity.Assets("https://cdn.discordapp.com/app-assets/1172162054585593986/1173620094425903114.png", "Get Elemental Client at https://elementalclient.net", "https://crafatar.com/avatars/${mc.session.playerID}", mc.session.username)
            )
        )
    }

    private fun isConnected() = this::discord.isInitialized && discord.connected

    fun shutdown() {
        if(!isConnected()) return
        discord.disconnect()
    }

    fun resendActivity() {
        updateActivity(currentActivity)
    }

    fun updateServer(
        server: ServerData
    ) {
        updateActivity(
            "Playing on ${server.serverIP}"
        ).start()
    }
}