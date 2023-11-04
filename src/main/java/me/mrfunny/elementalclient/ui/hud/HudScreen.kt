package me.mrfunny.elementalclient.ui.hud

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.Window
import gg.essential.elementa.components.inspector.Inspector
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ScaleConstraint
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
import java.util.function.Consumer
import kotlin.math.round

class HudScreen {
    val window = Window(ElementaVersion.V2)
    val matrix = UMatrixStack()
    var initialised = false

    fun init() {
        window.clearChildren()
        for (module in ModuleManager.modules) {
            if(module !is HudModule) continue
            module.root = module.buildComponent()
            for (child in module.root!!.children) {
                if(child is UIText) continue
                child.constrain {
                    width = ScaleConstraint(width, module.scale)
                    height = ScaleConstraint(width, module.scale)
                }
            }
            assignModuleConstraints(module)
            if(module.state) {

                module.root!! childOf window
            }
        }
        initialised = true
    }

    private fun assignModuleConstraints(module: HudModule) {
        module.root?.constrain {
            x = module.xPos.pixels
            y = module.yPos.pixels
            width = ScaleConstraint(width, module.scale)
            height = ScaleConstraint(width, module.scale)
        }
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
        matrix.push()
        window.draw(matrix)
        matrix.pop()
    }
}