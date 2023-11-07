package me.mrfunny.elementalclient.ui.hud

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.inspector.Inspector
import gg.essential.elementa.constraints.RainbowColorConstraint
import gg.essential.elementa.constraints.ScaleConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import me.mrfunny.elementalclient.ElementalClient
import me.mrfunny.elementalclient.modules.HudModule
import me.mrfunny.elementalclient.modules.ModuleManager
import me.mrfunny.elementalclient.ui.hud.HudScreen.Companion.assignHudComponents

class HudEditGui : WindowScreen(ElementaVersion.V2) {
    init {
        assignHudComponents(window)
        Inspector(window) childOf window
        ElementalClient.hudScreen.pause()
    }
}