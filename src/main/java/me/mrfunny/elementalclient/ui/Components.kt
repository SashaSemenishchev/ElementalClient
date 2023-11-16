package me.mrfunny.elementalclient.ui

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.constraints.*
import gg.essential.elementa.constraints.resolution.ConstraintVisitor
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.MappedState
import gg.essential.elementa.state.State
import java.awt.Color

object Components {
    fun Color.toAlphaConstraint() = AlphaAspectColorConstraint(this, this.alpha / 255f)
}