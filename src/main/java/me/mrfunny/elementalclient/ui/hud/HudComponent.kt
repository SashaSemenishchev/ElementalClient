package me.mrfunny.elementalclient.ui.hud

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.constraints.ChildBasedRangeConstraint
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.effects.ScissorEffect
import gg.essential.elementa.utils.elementaDebug
import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import me.mrfunny.elementalclient.modules.HudModule
import org.lwjgl.opengl.GL11
import java.awt.Color

class HudComponent(val module: HudModule, var isSelected: Boolean=false) : UIContainer() {
    init {
        constrain {
            width = ChildBasedRangeConstraint()
            height = ChildBasedRangeConstraint()
        }
    }

    override fun draw(matrixStack: UMatrixStack) {
        super.draw(matrixStack)
        if(!isSelected) return
        val scissors = generateSequence(this as UIComponent) { if (it.parent != it) it.parent else null }
            .flatMap { it.effects.filterIsInstance<ScissorEffect>().asReversed() }
            .toList()
            .reversed()

        val x1 = this.getLeft().toDouble()
        val y1 = this.getTop().toDouble()
        val x2 = this.getRight().toDouble()
        val y2 = this.getBottom().toDouble()

        // Clear the depth buffer cause we will be using it to draw our outside-of-scissor-bounds block
        UGraphics.glClear(GL11.GL_DEPTH_BUFFER_BIT)

        // Draw a highlight on the element respecting its scissor effects
        scissors.forEach { it.beforeDraw(matrixStack) }
        UIBlock.drawBlock(matrixStack, Color(100, 100, 100, 100), x1, y1, x2, y2)
        scissors.asReversed().forEach { it.afterDraw(matrixStack) }

        // Then draw another highlight (with depth testing such that we do not overwrite the previous one)
        // which does not respect the scissor effects and thereby indicates where the element is drawn outside of
        // its scissor bounds.
        UGraphics.enableDepth()
        UGraphics.depthFunc(GL11.GL_LESS)
        ElementaVersion.V0.enableFor { // need the custom depth testing
            UIBlock.drawBlock(matrixStack, Color(255, 100, 100, 100), x1, y1, x2, y2)
        }
        UGraphics.depthFunc(GL11.GL_LEQUAL)
        UGraphics.disableDepth()
    }
}