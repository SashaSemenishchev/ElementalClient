package me.mrfunny.elementalclient.ui.moduleoverview

import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.*
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.toConstraint
import gg.essential.universal.USound
import gg.essential.vigilance.gui.VigilancePalette
import gg.essential.vigilance.gui.elementa.GuiScaleOffsetConstraint
import gg.essential.vigilance.utils.onLeftClick
import me.mrfunny.elementalclient.modules.BooleanDefinedState
import me.mrfunny.elementalclient.profiles.ProfileManager
import java.awt.Color

class ProfileLabel(val profile: String, var isSelected: Boolean) : UIContainer() {
    val text by UIText(profile, true).constrain {
        y = CenterConstraint()
        textScale = GuiScaleOffsetConstraint(1f)
        color = VigilancePalette.getText().toConstraint()
    } childOf this
    val selectedState = BooleanDefinedState(this::isSelected, VigilancePalette.getTextActive(), VigilancePalette.getText())
    init {
        setColor(selectedState.toConstraint())
        constrain {
            y = SiblingConstraint()
            width = ChildBasedMaxSizeConstraint()
            height = ChildBasedSizeConstraint() + 8.pixels
        }

        onMouseEnter {
            if (!isSelected) {
                text.animate {
                    setColorAnimation(Animations.OUT_EXP, 0.5f, VigilancePalette.getTextHighlight().toConstraint())
                }.setShadowColor(VigilancePalette.getTextShadowMid())
            }
        }

        onMouseLeave {
            if (!isSelected) {
                text.animate {
                    setColorAnimation(Animations.OUT_EXP, 0.5f, VigilancePalette.getText().toConstraint())
                }.setShadowColor(VigilancePalette.getTextShadowMid())
            }
        }
        if(isSelected) {
            setColor()
        }
    }

    fun select(instant: Boolean=false) {
        if(isSelected) return
        ProfileManager.selectProfile(profile)
        isSelected = true
        if(instant) {
            setColor()
            return
        }
        text.animate {
            setColorAnimation(Animations.OUT_EXP, 0.5f, selectedState.toConstraint())
        }.setShadowColor(VigilancePalette.getTextActiveShadow())
    }

    fun setColor() {
        text.setColor(selectedState.toConstraint())
        text.setShadowColor(VigilancePalette.getTextActiveShadow())
    }

    fun deselect() {
        isSelected = false
        text.animate {
            setColorAnimation(Animations.OUT_EXP, 0.5f, selectedState.toConstraint())
        }.setShadowColor(VigilancePalette.getTextShadowMid())
    }
}