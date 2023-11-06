package me.mrfunny.elementalclient.modules.impl

import me.mrfunny.elementalclient.ElementalClient
import me.mrfunny.elementalclient.event.ChatEvent
import me.mrfunny.elementalclient.event.EventLink
import me.mrfunny.elementalclient.modules.*
import net.minecraft.util.EnumChatFormatting
import java.util.concurrent.TimeUnit
import java.util.function.Consumer
import kotlin.math.round
import kotlin.math.roundToLong

class AutoGG : Module("AutoGG", "Automatically sends 'Good Game' message when game ends") {
    val text1 by TextValue("First Message", "gg", placeholder = "gg")
    val text2 by TextValue("Second Message", "", placeholder = "Sniped by HypixelSS")
    val delay by FloatValue("Delay between messages", 1f, 0f..5f, description = "Delay between 2 messages (in seconds)")
    val initialDelay by FloatValue("Initial delay", 0.5f, 0f..5f, description = "Delay before gg message should be sent (in seconds)")
    val delayBetweenTriggers by IntegerValue("Delay between triggers", 10, 1..60, description = "Minimum delay between 2 triggers (in seconds)")
    private val triggers = arrayOf(
        "1st Killer - ",
        "1st Place - ",
        "Winner: ",
        " - Damage Dealt - ",
        "Winning Team -",
        "1st - ",
        "Winners: ",
        "Winner: ",
        "Winning Team: ",
        " won the game!",
        "Top Seeker: ",
        "1st Place: ",
        "Last team standing!",
        "Winner #1 (",
        "Top Survivors",
        "Winners - ",
        "Reward Summary"
    )
    var lastTriggered: Long = 0
    @EventLink
    val onChat = Consumer<ChatEvent> {
        val text = EnumChatFormatting.getTextWithoutFormattingCodes(it.component.unformattedText)
        var triggered = false
        for (trigger in triggers) {
            if(trigger !in text) continue
            triggered = true
            break
        }

        if(!triggered) return@Consumer
        if(System.currentTimeMillis() - lastTriggered < delayBetweenTriggers * 1000) return@Consumer
        lastTriggered = System.currentTimeMillis()
        ElementalClient.executor.schedule({
            mc.thePlayer.sendChatMessage(text1)
            if(text2.isBlank()) return@schedule
            ElementalClient.executor.schedule({
                mc.thePlayer.sendChatMessage(text2)
            }, (delay * 1000).roundToLong(), TimeUnit.MILLISECONDS)
        }, (initialDelay * 1000).roundToLong(), TimeUnit.MILLISECONDS)
    }

}