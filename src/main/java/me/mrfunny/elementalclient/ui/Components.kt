package me.mrfunny.elementalclient.ui

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.*
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.events.UIClickEvent
import java.awt.Color
import java.util.function.Consumer

object Components {
//    fun UIComponent.button(content: String, x: XConstraint, y: YConstraint, width: WidthConstraint, height: HeightConstraint, click: Consumer<UIClickEvent>, background: Color=Color.BLACK): UIComponent {
//        val text = UIText(content).constrain {
//            this.x = CenterConstraint()
//            this.y = CenterConstraint()
//        }
//
//        return button(text, x, y, width, height, click, background)
//    }
    fun button(content: UIComponent, x: XConstraint, y: YConstraint, width: WidthConstraint, height: HeightConstraint, background: ColorConstraint): UIComponent {
        return UIBlock()
            .setColor(background)
            .setX(x)
            .setY(y)
            .setWidth(width)
            .setHeight(height)
            .addChild(content)
    }

    private fun darkenWhenMouseEnter(background: Color): UIComponent.() -> Unit = {
        val hsv = Color.RGBtoHSB(background.red, background.green, background.blue, null)
        hsv[2] -= 0.1f
        setColor(Color(Color.HSBtoRGB(hsv[0], hsv[1], hsv[2])))
    }

    fun Color.toAlphaConstraint() = AlphaAspectColorConstraint(this, this.alpha / 255f)
}