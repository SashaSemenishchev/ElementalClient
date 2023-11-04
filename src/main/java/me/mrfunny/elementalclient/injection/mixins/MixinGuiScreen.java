package me.mrfunny.elementalclient.injection.mixins;

import me.mrfunny.elementalclient.modules.InProgress;
import me.mrfunny.elementalclient.ui.NoBackground;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiScreen.class)
public class MixinGuiScreen {
    @Inject(method = "drawWorldBackground", at = @At("HEAD"), cancellable = true)
    public void drawWorldBackground(int tint, CallbackInfo ci) {
        if(this instanceof NoBackground) {
            ci.cancel();
        }
    }
}
