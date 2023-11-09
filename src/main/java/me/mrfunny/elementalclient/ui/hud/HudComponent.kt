package me.mrfunny.elementalclient.ui.hud

import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.constraints.ChildBasedRangeConstraint
import gg.essential.elementa.dsl.constrain
import me.mrfunny.elementalclient.modules.HudModule

class HudComponent(val module: HudModule) : UIContainer() {
    init {
        constrain {
            width = ChildBasedRangeConstraint()
            height = ChildBasedRangeConstraint()
        }
    }
}