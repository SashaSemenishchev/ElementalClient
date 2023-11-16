package me.mrfunny.elementalclient.ui.moduleoverview

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.*
import gg.essential.elementa.constraints.*
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
        this effect ScissorEffect()
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
            width = 90.percent
            height = 100.percent
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
            y = CenterConstraint()
            x = 0.pixels(true)
            width = 10.pixels
            height = ChildBasedSizeConstraint()
        }.onMouseEnter {
            for (child in children) {
                child.children[0].animate {
                    setColorAnimation(Animations.OUT_EXP, .25f, dotsColor.map { it.darker() }.toConstraint())
                }
            }
        }.onMouseLeave {
            for (child in children) {
                child.children[0].animate {
                    setColorAnimation(Animations.OUT_EXP, .25f, dotsColor.toConstraint())
                }
            }
        }.onLeftClick {
            val screen = ModuleManager.gui()
            if (screen == null) {
                UChat.chat("ModuleManager breakey breakey")
                return@onLeftClick
            }
            USound.playButtonPress()
            UScreen.displayScreen(screen)
            screen.guiAtModule(module)
        }
        morePropertiesDot(dots, dotsColor)
        morePropertiesDot(dots, dotsColor)
        morePropertiesDot(dots, dotsColor)
        if(module.values.isNotEmpty()) {
            dots childOf statusBlock
        }
    }

    fun update() {
        enabledText.bindText(text)
        statusBlock.setColor(colorState.get())
    }
    private fun makeCircle(radius: Float): UIComponent {
        val circle = UICircle(radius).constrain {
            x = CenterConstraint()
            y = CenterConstraint()
        }
        return UIContainer().constrain {
            width = circle.getWidth().pixels
            height = circle.getWidth().pixels
        }.also { circle childOf it }
    }
    private fun morePropertiesDot(parent: UIComponent, state: BasicState<Color>) = makeCircle(2f).constrain {
//        if(padding) {
//            UIContainer().constrain {
//                height = 0.2.pixels
//                y = SiblingConstraint()
//            } childOf parent
//        }
        x = CenterConstraint()
        y = SiblingConstraint()
        color = ConstantColorConstraint(state)
    } childOf parent
}