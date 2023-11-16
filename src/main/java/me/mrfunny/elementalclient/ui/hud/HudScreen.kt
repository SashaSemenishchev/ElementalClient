package me.mrfunny.elementalclient.ui.hud

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.Window
import gg.essential.elementa.constraints.*
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.pixels
import gg.essential.universal.UMatrixStack
import me.mrfunny.elementalclient.event.EventLink
import me.mrfunny.elementalclient.event.ModuleStateChangeEvent
import me.mrfunny.elementalclient.event.Render2DEvent
import me.mrfunny.elementalclient.modules.HudModule
import me.mrfunny.elementalclient.modules.ModuleManager
import me.mrfunny.elementalclient.ui.misc.FinalScaleConstraint
import me.mrfunny.elementalclient.ui.misc.Unscalable
import me.mrfunny.elementalclient.util.MinecraftInstance
import java.util.function.Consumer

class HudScreen {
    private val window = Window(ElementaVersion.V2)
    private val matrix = UMatrixStack()
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
            val constraints = component.constraints
            constraints.height = assignSizeScale(constraints.height, scale)
            constraints.width = assignSizeScale(constraints.width, scale)
            constraints.x = assignPositionScale(constraints.x, scale)
            constraints.y = assignPositionScale(constraints.y, scale)
            if(component.children.size == 0) return
            for (child in component.children) {
                assignChildrenScale(scale, child)
            }
        }

        private fun <T: SuperConstraint<Float>> assignPositionScale(constraint: T, scale: Float=1.0f): T {
            return if(constraint !is Unscalable) {
                constraint
            } else {
                constraint.recalculate = true
                constraint.value = scale
                constraint
            }
        }
        private fun <T: SuperConstraint<Float>> assignSizeScale(constraint: T, scale: Float=1.0f): T {
            if(constraint is ChildBasedRangeConstraint) return constraint
            return if(constraint !is Unscalable) {
                FinalScaleConstraint(constraint, scale) as T
            } else {
                constraint.recalculate = true
                constraint.value = scale
                constraint
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