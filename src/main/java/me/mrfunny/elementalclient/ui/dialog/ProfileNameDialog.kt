package me.mrfunny.elementalclient.ui.dialog

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.input.UITextInput
import gg.essential.elementa.constraints.*
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.universal.UKeyboard
import gg.essential.vigilance.gui.VigilancePalette
import gg.essential.vigilance.utils.onLeftClick

class ProfileNameDialog(onRun: ProfileNameDialog.(String) -> Boolean): UIContainer() {
    val titlebar by UIBlock(VigilancePalette.getMainBackground()) childOf this
    val title by UIText("New Profile").constrain {
        x = 2.pixels
        y = 2.pixels
    } childOf this
    val content by UIBlock(VigilancePalette.getComponentHighlight())
    val input by UITextInput()
    init {
        titlebar.constrain {
            width = 100.pixels
            height = 12.pixels
            y = SiblingConstraint()
        }
        content.constrain {
            width = RelativeConstraint()
            height = ChildBasedSizeConstraint() + 7.pixels
            y = SiblingConstraint()
        } childOf this
        UIText("Name").constrain {
            y = SiblingConstraint() + 4.pixels
            x = CenterConstraint()
        } childOf content

        val inputBlock = UIBlock(VigilancePalette.getMainBackground()).constrain {
            width = 80.percent
            height = 12.pixels
            y = CramSiblingConstraint() + 3.pixels
            x = CenterConstraint()
        }.onLeftClick {
            input.grabWindowFocus()
        } childOf content

        input.constrain {
            x = 2.pixels
            y = 2.pixels

            width = RelativeConstraint(1f) - 6.pixels()
        } childOf inputBlock

//        inputBlock.

        val buttonBlock = UIContainer().constrain {
            y = SiblingConstraint()
            x = CenterConstraint()
            height = ChildBasedSizeConstraint()
            width = ChildBasedSizeConstraint()
        }
        val hideAndProceed = {
            if(!onRun(input.getText())) {
                this@ProfileNameDialog.hide()
            }
        }
        button("Proceed").onLeftClick {
            hideAndProceed()
        } childOf buttonBlock
        button("Cancel").onLeftClick {
            this@ProfileNameDialog.hide()
        } childOf buttonBlock
        buttonBlock childOf content
        input.onKeyType { _, keyCode ->
            when (keyCode) {
                UKeyboard.KEY_ENTER -> {
                    hideAndProceed()
                }
            }
        }
        constrain {
            height = (titlebar.getWidth() + content.getWidth()).pixels
        }
    }

    override fun afterInitialization() {
        super.afterInitialization()
        input.grabWindowFocus()
        titlebar.constrain {
            width = content.getWidth().pixels
        }
    }
    var previousError: UIBlock? = null
    fun error(error: String) {
        previousError?.hide()
        previousError = (UIBlock(VigilancePalette.getWarning())
            .addChild(UIText(error).constrain {
                x = CenterConstraint()
                y = CenterConstraint()
            })
            .constrain {
                x = CenterConstraint()
                y = (SiblingConstraint()) + 4.pixels
                width = ChildBasedSizeConstraint()
                height = ChildBasedSizeConstraint() + 10f.pixels
            } as UIBlock).also {
                it childOf content
        }
    }

    fun refocus() {
        input.grabWindowFocus()
    }

    fun button(text: String): UIComponent {
        return UIBlock(VigilancePalette.getMainBackground()).constrain {
            y = CenterConstraint()
            x = SiblingConstraint(5f)
//            width = 50.pixels
//            height = 10.pixels
            width = ChildBasedSizeConstraint() + 5.pixels
            height = ChildBasedSizeConstraint() + 5.pixels
        }.onMouseEnter {
            animate {
                setColorAnimation(Animations.OUT_EXP, .25f, VigilancePalette.getButton().brighter().toConstraint())
            }
        }.onMouseLeave {
            animate {
                setColorAnimation(Animations.OUT_EXP, .25f, VigilancePalette.getMainBackground().toConstraint())
            }
        }
        .addChild(UIText(text).constrain {
            y = CenterConstraint()
            x = CenterConstraint()
            height = 10.pixels
        })
    }
}