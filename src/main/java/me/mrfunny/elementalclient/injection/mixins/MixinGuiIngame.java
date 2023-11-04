package me.mrfunny.elementalclient.injection.mixins;

import me.mrfunny.elementalclient.ElementalClient;
import me.mrfunny.elementalclient.event.Render2DEvent;
import me.mrfunny.elementalclient.ui.hud.HudScreen;
import me.mrfunny.elementalclient.util.ClassUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public class MixinGuiIngame {

    @Inject(method = "renderTooltip", at = @At("RETURN"))
    private void renderTooltipPost(ScaledResolution sr, float partialTicks, CallbackInfo callbackInfo) {
        if (!ClassUtils.hasClass("net.labymod.api.LabyModAPI")) {
            ElementalClient.eventBus.callEvent(new Render2DEvent(partialTicks));
        }
    }
}
