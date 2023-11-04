package me.mrfunny.elementalclient.util
import gg.essential.vigilance.Vigilant
import gg.essential.universal.UChat
import gg.essential.elementa.components.plot.Bounds
import me.mrfunny.elementalclient.api.MultiplayerManager
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation

object RenderUtil : MinecraftInstance() {
    private val badge = ResourceLocation("elementalclient/logo_transparent.png")
    fun renderBadge(entity: EntityPlayer) {
        if(!MultiplayerManager.isPlayingWithSameClient(entity)) return
        GlStateManager.alphaFunc(516, 0.1f)
        GlStateManager.enableDepth()
        GlStateManager.enableAlpha()
        mc.textureManager.bindTexture(badge)
        val badgeX: Int = -(mc.fontRendererObj.getStringWidth(entity.name) / 2) - 10
        Bounds
        GlStateManager.color(1f, 1f, 1f, 1f)
        val size = 8f
        Gui.drawModalRectWithCustomSizedTexture(badgeX - 1, 0, 0f, 0f, size.toInt(), size.toInt(), size, size)
    }
}