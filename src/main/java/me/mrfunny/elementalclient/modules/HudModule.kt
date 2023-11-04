package me.mrfunny.elementalclient.modules

import gg.essential.elementa.UIComponent
import org.lwjgl.opengl.Display

abstract class HudModule(name: String, description: String): Module(name, description) {
    @delegate:InternalField
    var xPos by FloatValue("xPos", Display.getWidth() / 2f, 0f..Short.MAX_VALUE.toFloat())
    @delegate:InternalField
    var yPos by FloatValue("yPos", Display.getHeight() / 2f, 0f..Short.MAX_VALUE.toFloat())
    @delegate:InternalField
    var scale by FloatValue("yPos", 1f, 0.1f..10f)

    abstract fun buildComponent(): UIComponent

    var root: UIComponent? = null
    override fun postInit() {
        internalValues.addAll(this.getInternalValues(HudModule::class.java))
    }
}