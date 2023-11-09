package me.mrfunny.elementalclient.ui.hud

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.Window
import gg.essential.elementa.components.inspector.Inspector
import gg.essential.elementa.constraints.*
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.animate
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.pixels
import gg.essential.universal.UMatrixStack
import me.mrfunny.elementalclient.ElementalClient
import me.mrfunny.elementalclient.event.EventLink
import me.mrfunny.elementalclient.event.ModuleStateChangeEvent
import me.mrfunny.elementalclient.event.Render2DEvent
import me.mrfunny.elementalclient.event.WorldBeginLoadEvent
import me.mrfunny.elementalclient.modules.HudModule
import me.mrfunny.elementalclient.modules.ModuleManager
import me.mrfunny.elementalclient.ui.misc.FinalScaleConstraint
import me.mrfunny.elementalclient.util.MinecraftInstance
import java.util.function.Consumer
import kotlin.math.round

class HudScreen {
    val window = Window(ElementaVersion.V2)
    val matrix = UMatrixStack()
    var initialised = false
    companion object {
        fun assignHudComponents(window: Window) {
            window.clearChildren()
            for (module in ModuleManager.modules) {
                if(module !is HudModule) continue
                module.root = module.buildComponent()
                assignModuleConstraints(module)
                if(module.state) {
                    module.root!! childOf window
                }
            }
        }

        fun getModuleComponents(): List<HudComponent> {
            val list = mutableListOf<HudComponent>()
            for (module in ModuleManager.modules) {
                if(module !is HudModule) continue
                val component = module.buildComponent()
                module.root = component
                assignModuleConstraints(module)
                if(module.state) {
                    list.add(component)
                }
            }

            return list;
        }
        fun assignModuleConstraints(module: HudModule) {
            val root = module.root ?: return
            root.constrain {
                x = module.xPos.pixels
                y = module.yPos.pixels
            }
            assignChildrenScale(module.scale, root)
        }

        private fun assignChildrenScale(scale: Float, component: UIComponent) {
            val height = component.constraints.height
            val width = component.constraints.width
            if(height is MasterConstraint) {
                if(height !is FinalScaleConstraint) {
                    component.constraints.height = FinalScaleConstraint(height, scale)
                } else {
                    height.constraint.recalculate = true
                    height.value = scale
                }
            }

            if(width is MasterConstraint) {
                if(width !is FinalScaleConstraint) {
                    component.constraints.width = FinalScaleConstraint(width, scale)
                } else {
                    width.constraint.recalculate = true
                    width.value = scale
                }
            }
            if(component.children.size == 0) return
            for (child in component.children) {
                assignChildrenScale(scale, child)
            }
        }
    }
    fun init() {
        assignHudComponents(window)
        initialised = true
    }
    private var isPaused = false
    fun pause() {
        isPaused = true
    }

    fun unpause() {
        init()
        isPaused = false
    }



    @EventLink
    val onRender = Consumer<Render2DEvent> {
        render()
    }

    @EventLink
    val onModuleChange = Consumer<ModuleStateChangeEvent> {
        if(!initialised) return@Consumer
        val module = it.module
        if(module !is HudModule) return@Consumer
        val root = module.root ?: return@Consumer
        if(it.profileChanged) {
            window.removeChild(root)
            module.root = module.buildComponent()
            assignModuleConstraints(module)
        }

        if(it.newState) {
            root childOf window
        } else {
            window.removeChild(root)
        }
    }
//    fun rebuildModules() {
//        window.clearChildren()
//        for (module in ModuleManager.modules) {
//            if(!module.state) continue
//            ElementalClient.eventBus.callEvent(ModuleStateChangeEvent(module, module.state, true))
//        }
//    }
//    @EventLink
//    val onWorldLoad = Consumer<WorldBeginLoadEvent> {
//        rebuildModules()
//    }

    private fun render() {
        if(isPaused) {
            if(MinecraftInstance.mc.currentScreen == null) {
                unpause()
            }
            return
        }
        matrix.push()
        window.draw(matrix)
        matrix.pop()
    }
}