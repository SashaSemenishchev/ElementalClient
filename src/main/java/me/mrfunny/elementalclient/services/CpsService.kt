package me.mrfunny.elementalclient.services

import me.mrfunny.elementalclient.ElementalClient
import me.mrfunny.elementalclient.event.EventLink
import me.mrfunny.elementalclient.event.KeyStateChangeEvent
import me.mrfunny.elementalclient.modules.ModuleManager
import me.mrfunny.elementalclient.modules.impl.Keystrokes
import me.mrfunny.elementalclient.util.MinecraftInstance
import net.minecraft.client.Minecraft
import org.lwjgl.Sys
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.function.Consumer
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KMutableProperty0

class CpsService {
    companion object {
        var lmb = 0
        var rmb = 0

        private var lmbClicksCount = 0
        private var lmbLastClicked = 0L
        private var rmbClicksCount = 0
        private var rmbLastClicked = 0L
        @JvmField val executor = Executors.newSingleThreadScheduledExecutor()
    }

    val mc = MinecraftInstance.mc

    @EventLink
    val onStateUpdate = Consumer<KeyStateChangeEvent> {
        if(!it.newState) return@Consumer
        if(it.keybind == mc.gameSettings.keyBindAttack) {
            update(Companion::lmb, Companion::lmbLastClicked, Companion::lmbClicksCount)
        } else if(it.keybind == mc.gameSettings.keyBindUseItem) {
            update(Companion::rmb, Companion::rmbLastClicked, Companion::rmbClicksCount)
        }
    }

    fun update(cpsField: KMutableProperty0<Int>, lastClicked: KMutableProperty0<Long>, clicksCount: KMutableProperty0<Int>) {
        val now = System.currentTimeMillis()
        val interval = now - lastClicked.get()
//        println(interval)
        lastClicked.set(now)
        if(interval >= 5000) {
            cpsField.set(0)
            return
        }
        val clicksCountVal = clicksCount.get() + 1
        clicksCount.set(clicksCountVal)
        cpsField.set(clicksCountVal)
        Keystrokes.updateCpsStates()
        executor.schedule({
            val actualVal = clicksCount.get()
            if(actualVal >= 0) {
                cpsField.set(actualVal)
                if(actualVal > 0) {
                    clicksCount.set(actualVal - 1)
                }

            } else {
                cpsField.set(max(0, cpsField.get() - 1))
            }
            Keystrokes.updateCpsStates()
        }, 1, TimeUnit.SECONDS)
    }
}