package me.mrfunny.elementalclient.injection.mixins;

import me.mrfunny.elementalclient.modules.impl.RenderOwnName;
import me.mrfunny.elementalclient.util.MinecraftInstance;
import me.mrfunny.elementalclient.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RendererLivingEntity.class)
public class MixinRenderLivingEntity<T extends EntityLivingBase> {

    @Unique
    private Minecraft mc = Minecraft.getMinecraft();

    @Inject(method = "canRenderName(Lnet/minecraft/entity/EntityLivingBase;)Z", at = @At("HEAD"), cancellable = true)
    public void renderOwnName(T entity, CallbackInfoReturnable<Boolean> cir) {
        if(entity != mc.thePlayer) return;
        if(RenderOwnName.INSTANCE.getState()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "renderName(Lnet/minecraft/entity/EntityLivingBase;DDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I"))
    public void renderIcon(T entity, double x, double y, double z, CallbackInfo ci) {
        if(entity instanceof EntityPlayer) {
            RenderUtil.INSTANCE.renderBadge((EntityPlayer) entity);
        }
    }

}
