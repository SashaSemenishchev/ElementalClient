package me.mrfunny.elementalclient.modules

import gg.essential.elementa.dsl.effect
import gg.essential.elementa.effects.ScissorEffect
import me.mrfunny.elementalclient.ui.hud.HudComponent
import me.mrfunny.elementalclient.ui.hud.HudScreen
import org.lwjgl.opengl.Display
abstract class HudModule(name: String, description: String): Module(name, description) {
    @delegate:InternalField
    var xPos by FloatValue("xPos", Display.getWidth() / 2f, 0f..Short.MAX_VALUE.toFloat())
    @delegate:InternalField
    var yPos by FloatValue("yPos", Display.getHeight() / 2f, 0f..Short.MAX_VALUE.toFloat())
    @delegate:InternalField
    var scale by FloatValue("scale", 1f, 0.1f..10f)

    abstract fun buildComponent(): HudComponent

    var root: HudComponent? = null
        set(value) {
            field = value?.effect(ScissorEffect())
        }
    override fun postInit() {
        internalValues.addAll(this.getInternalValues(HudModule::class.java))
    }

    fun setPosition(mouseX: Float, mouseY: Float) {
        xPos = mouseX
        yPos = mouseY
        update()
    }

    fun update() {
        HudScreen.assignModuleConstraints(this)
    }
}