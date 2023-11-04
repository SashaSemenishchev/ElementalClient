package me.mrfunny.elementalclient.ui.moduleoverview

import gg.essential.elementa.components.*
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ConstantColorConstraint
import gg.essential.elementa.constraints.FillConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.ScissorEffect
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.toConstraint
import gg.essential.universal.UChat
import gg.essential.universal.UScreen
import gg.essential.universal.USound
import gg.essential.vigilance.gui.VigilancePalette
import gg.essential.vigilance.utils.onLeftClick
import me.mrfunny.elementalclient.modules.BooleanDefinedState
import me.mrfunny.elementalclient.modules.Module
import me.mrfunny.elementalclient.modules.ModuleManager
import me.mrfunny.elementalclient.modules.ModuleManager.guiAtModule
import java.awt.Color

class ModuleMiniature(val module: Module) : UIBlock(VigilancePalette.getDividerDark()) {
    val colorState = BooleanDefinedState(module::state, VigilancePalette.getSuccess(), VigilancePalette.getMidGray())
    val text = BooleanDefinedState(module::state, "Enabled", "Disabled")
    val enabledText by UIText().bindText(text).constrain {
        x = CenterConstraint()
        y = CenterConstraint()
    }
    val statusBlock by UIBlock(ConstantColorConstraint(colorState)).constrain {
        y = SiblingConstraint()
        x = CenterConstraint()
        width = 100.percent
        height = FillConstraint()
    }
    init {
        constrain {
            width = 100.pixels
            height = 110.pixels
        } effect ScissorEffect()
        UIContainer().constrain {
            y = SiblingConstraint()
            height = 7.pixels
            width = 100.percent
        } childOf this
        UIImage.ofResource("/assets/minecraft/elementalclient/icons/modules/${module.name.lowercase()}.png").constrain {
            x = CenterConstraint()
            y = SiblingConstraint()
            height = 64.pixels
            width = 75.pixels
        } childOf this
        UIContainer().constrain {
            y = SiblingConstraint()
            height = 7.pixels
            width = 100.percent
        } childOf this
        UIText(module.spacedName).constrain {
            x = CenterConstraint()
            y = SiblingConstraint()
        } childOf this
        UIContainer().constrain {
            y = SiblingConstraint()
            height = 4.pixels
            width = 100.percent
        } childOf this


        statusBlock childOf this
        enabledText childOf statusBlock
        UIContainer().constrain {
            width = (statusBlock.getWidth() - 10).pixels
            height = statusBlock.getHeight().pixels
        }.childOf(statusBlock).onMouseEnter {
            parent.animate {
                setColorAnimation(Animations.OUT_EXP, .25f, colorState.map { it.brighter() }.toConstraint())
            }
        }.onMouseLeave {
            parent.animate {
                setColorAnimation(Animations.OUT_EXP, .25f, colorState.toConstraint())
            }
        }.onLeftClick {
            USound.playButtonPress()
            module.state = !module.state
            parent.setColor(colorState.map {it.brighter()}.toConstraint())
            enabledText.setText(text.get())
        }
        val dotsColor = BasicState(Color.WHITE)
        val dots = UIContainer().constrain {
            y = 6f.pixels
            x = (statusBlock.getWidth() - 10).pixels
            width = 10.pixels
            height = statusBlock.getHeight().pixels
        }.onMouseEnter {
            for (child in children) {
                child.animate {
                    setColorAnimation(Animations.OUT_EXP, .25f, dotsColor.map { it.darker() }.toConstraint())
                }
            }
        }.onMouseLeave {
            for (child in children) {
                child.animate {
                    setColorAnimation(Animations.OUT_EXP, .25f, dotsColor.toConstraint())
                }
            }
        }.onLeftClick {
            val screen = ModuleManager.gui()
            if(screen == null) {
                UChat.chat("ModuleManager breakey breakey")
                return@onLeftClick
            }
            USound.playButtonPress()
            UScreen.displayScreen(screen)
            screen.guiAtModule(module)
        } childOf statusBlock
        morePropertiesDot(dotsColor) childOf dots
        morePropertiesDot(dotsColor) childOf dots
        morePropertiesDot(dotsColor) childOf dots
    }

    fun update() {
        enabledText.bindText(text)
        statusBlock.setColor(colorState.get())
    }

    private fun morePropertiesDot(state: BasicState<Color>) = UICircle(2f).constrain {
        x = CenterConstraint()
        y = SiblingConstraint(2f)
        color = ConstantColorConstraint(state)
    }
}