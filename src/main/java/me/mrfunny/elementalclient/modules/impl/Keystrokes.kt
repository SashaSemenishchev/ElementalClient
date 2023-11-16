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
import gg.essential.elementa.state.BasicState
import gg.essential.universal.UKeyboard
import me.mrfunny.elementalclient.ElementalClient
import me.mrfunny.elementalclient.event.EventLink
import me.mrfunny.elementalclient.event.KeyEvent
import me.mrfunny.elementalclient.event.KeyStateChangeEvent
import me.mrfunny.elementalclient.modules.*
import me.mrfunny.elementalclient.services.CpsService
import me.mrfunny.elementalclient.ui.Components.toAlphaConstraint
import me.mrfunny.elementalclient.ui.hud.HudComponent
import me.mrfunny.elementalclient.ui.misc.ScaledPixelConstraint
import net.minecraft.client.settings.KeyBinding
import java.awt.Color
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

class Keystrokes : HudModule("Keystrokes", "Displays your keys") {
    val textColor by ColorValue("Text color", Color.WHITE)
    val textColorWhenPressed by ColorValue("Text color when pressed", Color.BLACK)
    val bgColor by ColorValue("Background color", Color.WHITE)
    val bgColorWhenPressed by ColorValue("Background color when pressed", Color.WHITE)
    val showButtons by BoolValue("Show Mouse buttons", false)
    val showCps by BoolValue("Show CPS", false, isSupported = {showButtons})
    val showSpacebar by BoolValue("Show Spacebar", true)

    override fun getMaxScale(): Float = 2f
    override fun getMinScale(): Float = 0.7f

    private val keybindMap = hashMapOf<KeyBinding, TextBlock>()

    init {
        ElementalClient.executor.scheduleAtFixedRate({
            updateCpsStates()
        }, 0, 1, TimeUnit.SECONDS)
    }
    override fun buildComponent(): HudComponent {
        val result = HudComponent(this)
        val scaleState = lookupValue(this::scale)?.state ?: return result

        // W
        makeBlock(mc.gameSettings.keyBindForward).also {
            it.constrain {
                x = ScaledPixelConstraint(BasicState(18f), scaleState)
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
                x = ScaledPixelConstraint(BasicState(18f), scaleState)
                y = ScaledPixelConstraint(BasicState(18f), scaleState)
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
                x = ScaledPixelConstraint(BasicState(36f), scaleState)
                y = ScaledPixelConstraint(BasicState(18f), scaleState)
                width = 17.pixels
                height = 17.pixels
            }
            it.bg.constrain {
                width = it.getWidth().pixels
                height = it.getHeight().pixels
            }
        } childOf result

        // A
        val lastBottom = makeBlock(mc.gameSettings.keyBindLeft).also {
            it.constrain {
                x = 0.pixels
                y = ScaledPixelConstraint(BasicState(18f), scaleState)
                width = 17.pixels
                height = 17.pixels
            }
            it.bg.constrain {
                width = it.getWidth().pixels
                height = it.getHeight().pixels
            }
        } childOf result
        val space = mc.gameSettings.keyBindJump
        val mouseBound = if(showSpacebar) {
            val bg = UIBlock(bgColor.toAlphaConstraint()).constrain {
                width = 53.pixels
                height = 10.pixels
            }
            val content = UIBlock(textColor.toAlphaConstraint()).constrain {
                y = CenterConstraint() boundTo bg
                x = CenterConstraint() boundTo bg
                width = 20.pixels
                height = 1.pixels
            }
            val spaceBlock = TextBlock(content, bg).constrain {
                y = SiblingConstraint() + ScaledPixelConstraint(BasicState(1f), scaleState)
            }.also { it childOf result }
            keybindMap[space] = spaceBlock
            spaceBlock
        } else {
            keybindMap.remove(space)
            lastBottom
        }

        if(showButtons) {

            makeBlock(mc.gameSettings.keyBindAttack).also {
                it.constrain {
                    y = SiblingConstraint(1f * scale) boundTo mouseBound
                    height = 17.pixels
                    width = 26.pixels
                }
                if(showCps) {
                    makeCpsBlock(it, lmbCps)
                }
                it.bg.constrain {
                    width = it.getWidth().pixels
                    height = it.getHeight().pixels
                }
            } childOf result
            makeBlock(mc.gameSettings.keyBindUseItem).also {
                it.constrain {
                    y = SiblingConstraint(1f * scale) boundTo mouseBound
                    x = ScaledPixelConstraint(BasicState(27f), scaleState)
                    height = 17.pixels
                    width = 26.pixels
                }
                if(showCps) {
                    makeCpsBlock(it, rmbCps)
                }
                it.bg.constrain {
                    width = it.getWidth().pixels
                    height = it.getHeight().pixels
                }
            } childOf result
        }
        return result
    }

    private fun makeCpsBlock(it: TextBlock, state: BasicState<String>) {
        val container = UIContainer().constrain {
            width = it.getWidth().pixels
            height = it.getHeight().pixels
        }
        it.content childOf container
        UIText(state).also { text ->
            val old = it.content.constraints
            text.constrain {
                y = 0.pixels(true) boundTo container
                x = CenterConstraint()
                width = ScaleConstraint(width, 0.46f)
                height = ScaleConstraint(height, 0.46f)
                color = old.color
            }
        } childOf container
        it.content = container
        it.onUpdate = { newState ->
            updateCpsStates()
            for (child in it.content.children) {
                child.animate {
                    this.setColorAnimation(Animations.IN_OUT_SIN, .11f, (if(newState) textColorWhenPressed else textColor).toAlphaConstraint())
                }
            }
        }
        it.rebind()
    }

    private fun makeBlock(keyBinding: KeyBinding): TextBlock {
        val result = TextBlock(
            UIText(getBindName(keyBinding)).constrain {
                x = CenterConstraint()
                y = CenterConstraint()
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

        val update = textBlock.onUpdate
        if(update == null) {
            textBlock.content.animate {
                this.setColorAnimation(strategy, .11f, text)
            }
        } else {
            update(it.newState)
        }
    }

    fun getBindName(keyBinding: KeyBinding): String {
        return when(keyBinding) {
            mc.gameSettings.keyBindAttack -> "LBM"
            mc.gameSettings.keyBindUseItem -> "RBM"
            else -> UKeyboard.getKeyName(keyBinding) ?: "Unknown"
        }
    }
    class TextBlock(var content: UIComponent, val bg: UIBlock, var onUpdate: ((state: Boolean) -> Unit)?=null): UIContainer() {
        init {
            bg childOf this
            content childOf this
            this.constrain {
                width = ChildBasedRangeConstraint()
                height = ChildBasedRangeConstraint()
            }
        }

        fun rebind() {
            clearChildren()
            bg childOf this
            content childOf this
        }
    }

    companion object {
        fun updateCpsStates() {
            lmbCps.set(CpsService.lmb.toString() + " CPS")
            rmbCps.set(CpsService.rmb.toString() + " CPS")
            CpsService.checkClicks()
        }

        @JvmField var lmbCps = BasicState("0 CPS")
        @JvmField var rmbCps = BasicState("0 CPS")
    }
}