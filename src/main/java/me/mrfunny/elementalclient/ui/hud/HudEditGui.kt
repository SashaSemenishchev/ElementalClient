package me.mrfunny.elementalclient.ui.hud

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.Window
import gg.essential.elementa.components.inspector.Inspector
import gg.essential.elementa.constraints.AlphaAspectColorConstraint
import gg.essential.elementa.constraints.PixelConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.pixels
import gg.essential.universal.UKeyboard
import me.mrfunny.elementalclient.ElementalClient
import me.mrfunny.elementalclient.ui.hud.HudScreen.Companion.assignHudComponents
import me.mrfunny.elementalclient.ui.misc.UnscalableConstraint
import org.lwjgl.input.Cursor
import org.lwjgl.input.Mouse
import java.awt.Color
import java.awt.Toolkit
import java.nio.ByteBuffer
import java.nio.IntBuffer
import java.util.concurrent.atomic.AtomicReference

class HudEditGui : WindowScreen(ElementaVersion.V2) {
    val modules: ArrayList<HudComponent>
    init {
        assignHudComponents(window)
        modules = window.children.filterIsInstanceTo(ArrayList(window.children.size))
        assignModuleAdditions()
        Inspector(window) childOf window
        ElementalClient.hudScreen.pause()
    }

    private val currentComponent = AtomicReference<HudComponent>(null)
    private var clickPos: Pair<Float, Float>? = null

    fun assignModuleAdditions() {
        for (module in modules) {
//            val block = UIBlock(AlphaAspectColorConstraint(Color.GRAY, 0.4f))
            module.onMouseEnter {
                module.isSelected = true
            }.onMouseClick {
                currentComponent.set(module)
                clickPos =
                    if (it.relativeX < 0 || it.relativeY < 0 || it.relativeX > getWidth() || it.relativeY > getHeight()) {
                        null
                    } else {
                        it.relativeX to it.relativeY
                    }
            }.onMouseRelease {
                clickPos = null
                if(currentComponent.get() == module) {
                    currentComponent.set(null)
                }
            }.onMouseLeave {
                module.isSelected = false
            }.onMouseDrag { mouseX, mouseY, mouseButton ->
                if(mouseButton != 0) return@onMouseDrag
                val clickPos = this@HudEditGui.clickPos ?: return@onMouseDrag
                val selected = this@HudEditGui.currentComponent.get() ?: return@onMouseDrag
                if(selected !== module) return@onMouseDrag
                selected.module.also {
                    val moduleRoot = it.root ?: return@also
                    it.setPosition(
                        moduleRoot.getLeft() + mouseX - clickPos.first,
                        moduleRoot.getTop() + mouseY - clickPos.second
                    )
//                    block.constrain {
//                        height = module.getHeight().pixels
//                        width = module.getWidth().pixels
//                    }
                }
            }.onMouseScroll {
                if(it.currentTarget !== module) return@onMouseScroll
                module.module.scale += it.delta.toFloat() / 100
                println(module.module.scale)
                module.module.update()
//                block.constrain {
//                    (height as UnscalableConstraint<PixelConstraint>).value = module.getHeight()
//                    (width as UnscalableConstraint<PixelConstraint>).value = module.getWidth()
//                }
            }
        }
    }
}