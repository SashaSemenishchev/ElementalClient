package me.mrfunny.elementalclient.ui.misc

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.RainbowColorConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.universal.UMatrixStack
import gg.essential.universal.UScreen
import gg.essential.vigilance.utils.onLeftClick
import me.mrfunny.elementalclient.ui.hud.HudEditGui
import me.mrfunny.elementalclient.ui.moduleoverview.ModuleOverviewGui
import java.awt.Color

// todo: make an actual GUI out of it
class ElementalMainMenu : WindowScreen(ElementaVersion.V2) {
    init {
        UIBlock(Color.RED).constrain {
            height = 50.pixels
            width = 50.pixels
            x = CenterConstraint()
            y = SiblingConstraint()
        }.onLeftClick {
            displayScreen(HudEditGui())
        }.addChild(UIText("edit HUD layout").constrain { x = CenterConstraint(); y = CenterConstraint() }) childOf window

        UIBlock(Color.BLUE).constrain {
            height = 50.pixels
            width = 50.pixels
            x = CenterConstraint()
            y = SiblingConstraint(5f)
        }.onLeftClick {
            displayScreen(ModuleOverviewGui())
        }.addChild(UIText("open settings").constrain { x = CenterConstraint(); y = CenterConstraint()}) childOf window
    }
}