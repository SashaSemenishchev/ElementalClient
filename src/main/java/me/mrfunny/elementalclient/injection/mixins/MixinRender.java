package me.mrfunny.elementalclient.injection.mixins;

import gg.essential.vigilance.gui.VigilancePalette;
import me.mrfunny.elementalclient.util.RenderUtil;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Render.class)
public class MixinRender {

    @Inject(method = "renderLivingLabel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I"))
    public <T extends Entity> void renderName(T entity, String str, double x, double y, double z, int maxDistance, CallbackInfo ci) {
        if(entity instanceof EntityPlayer) {
            RenderUtil.INSTANCE.renderBadge((EntityPlayer) entity);
//            VigilancePalette.INSTANCE.getARROW_DOWN_7X4$Vigilance().create()
        }
    }
}
