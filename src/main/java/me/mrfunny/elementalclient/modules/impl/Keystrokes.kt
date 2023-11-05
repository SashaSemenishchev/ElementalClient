package me.mrfunny.elementalclient.modules.impl

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.UIWrappedText
import gg.essential.elementa.components.inspector.Inspector
import gg.essential.elementa.constraints.*
import gg.essential.elementa.constraints.animation.AnimationStrategy
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.universal.UKeyboard
import me.mrfunny.elementalclient.event.EventLink
import me.mrfunny.elementalclient.event.KeyEvent
import me.mrfunny.elementalclient.event.KeyStateChangeEvent
import me.mrfunny.elementalclient.modules.BoolValue
import me.mrfunny.elementalclient.modules.ColorValue
import me.mrfunny.elementalclient.modules.HudModule
import me.mrfunny.elementalclient.modules.PercentageValue
import me.mrfunny.elementalclient.ui.Components.toAlphaConstraint
import net.minecraft.client.settings.KeyBinding
import java.awt.Color
import java.util.*
import java.util.function.Consumer

class Keystrokes : HudModule("Keystrokes", "Displays your keys") {
    val textColor by ColorValue("Text color", Color.WHITE)
//    val textOpacity by PercentageValue("Text opacity", .1f)
    val textColorWhenPressed by ColorValue("Text color when pressed", Color.BLACK)
//    val textOpacityWhenPRessed by PercentageValue("Text opacity when pressed", .1f)
    val bgColor by ColorValue("Background color", Color.WHITE)
//    val bgOpacity by PercentageValue("Background opacity", 0f)
    val bgColorWhenPressed by ColorValue("Background color when pressed", Color.WHITE)
//    val bgOpacityWhenPressed by PercentageValue("Background opacity when pressed", .1f)
    val showButtons by BoolValue("Show Mouse buttons", false)
    val showCps by BoolValue("Show CPS", false, isSupported = {showButtons})

    val showSpacebar by BoolValue("Show Spacebar", true)

    val keybindMap = hashMapOf<KeyBinding, TextBlock>()
    override fun buildComponent(): UIComponent {
        val result = UIContainer().constrain {
            width = ChildBasedSizeConstraint()
            height = ChildBasedSizeConstraint()
        }

        // W
        makeBlock(mc.gameSettings.keyBindForward).also {
            it.constrain {
                x = 18.pixels
                y = 0.pixels
                width = 17.pixels
                height = 17.pixels
            }
            it.bg.constrain {
                width = it.getWidth().pixels
                height = it.getHeight().pixels
            }
        } childOf result

        // S
        makeBlock(mc.gameSettings.keyBindBack).also {
            it.constrain {
                x = 18.pixels
                y = 18.pixels
                width = 17.pixels
                height = 17.pixels
            }
            it.bg.constrain {
                width = it.getWidth().pixels
                height = it.getHeight().pixels
            }
        } childOf result

        // D
        makeBlock(mc.gameSettings.keyBindRight).also {
            it.constrain {
                x = 36.pixels
                y = 18.pixels
                width = 17.pixels
                height = 17.pixels
            }
            it.bg.constrain {
                width = it.getWidth().pixels
                height = it.getHeight().pixels
            }
        } childOf result

        // A
        makeBlock(mc.gameSettings.keyBindLeft).also {
            it.constrain {
                x = 0.pixels
                y = 18.pixels
                width = 17.pixels
                height = 17.pixels
            }
            it.bg.constrain {
                width = it.getWidth().pixels
                height = it.getHeight().pixels
            }
        } childOf result
        val space = mc.gameSettings.keyBindJump
        if(showSpacebar) {
            val bg = UIBlock(bgColor.toAlphaConstraint()).constrain {
                width = 54.pixels
                height = 10.pixels
            }
            val content = UIBlock(textColor.toAlphaConstraint()).constrain {
                y = CenterConstraint() boundTo bg
                x = CenterConstraint() boundTo bg
                width = 20.pixels
                height = 1.5.pixels
            }
            keybindMap[space] = TextBlock(content, bg).constrain {
                y = SiblingConstraint(2f)
            }.also { it childOf result }
//            UIBlock(Color.BLACK).constrain {
//                width = FillConstraint()
//                height = 5.pixels
//            } childOf result
        } else {
            keybindMap.remove(space)
        }

        return result
    }

    fun makeBlock(keyBinding: KeyBinding): TextBlock {

        val result = TextBlock(
            UIText(getBindName(keyBinding)).constrain {
                x = CenterConstraint()
                y = CenterConstraint()
//                width = 14.pixels
//                height = 14.pixels
//                width = 100.percent
//                height = 100.percent
                color = textColor.toAlphaConstraint()
            },
            UIBlock(bgColor.toAlphaConstraint()).constrain {
                x = CenterConstraint()
                y = CenterConstraint()
            }
        )

        keybindMap[keyBinding] = result
        return result
    }

    @EventLink
    val onKeyPress = Consumer<KeyStateChangeEvent> {
        val textBlock = keybindMap[it.keybind] ?: return@Consumer
        val bg = if(it.newState) {
            bgColorWhenPressed.toAlphaConstraint()
        } else {
            bgColor.toAlphaConstraint()
        }

        val text = if(it.newState) {
            textColorWhenPressed.toAlphaConstraint()
        } else {
            textColor.toAlphaConstraint()
        }
        val strategy: AnimationStrategy = Animations.IN_OUT_SIN
        textBlock.bg.animate {
            this.setColorAnimation(strategy, .11f, bg)
        }
        textBlock.content.animate {
            this.setColorAnimation(strategy, .11f, text)
        }
    }

    fun getBindName(keyBinding: KeyBinding): String {
        return when(keyBinding) {
            mc.gameSettings.keyBindAttack -> "LBM"
            mc.gameSettings.keyBindUseItem -> "RBM"
            else -> UKeyboard.getKeyName(keyBinding) ?: "Unknown"
        }
    }
    class TextBlock(val content: UIComponent, val bg: UIBlock): UIContainer() {
        init {
            bg childOf this
            content childOf this
            this.constrain {
                width = ChildBasedSizeConstraint()
                height = ChildBasedSizeConstraint()
            }
        }
    }
}