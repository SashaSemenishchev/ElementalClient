package me.mrfunny.elementalclient.injection.mixins;

import me.mrfunny.elementalclient.ElementalClient;
import me.mrfunny.elementalclient.event.EventBus;
import me.mrfunny.elementalclient.event.KeyEvent;
import me.mrfunny.elementalclient.event.TickEvent;
import me.mrfunny.elementalclient.event.WorldBeginLoadEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
@SideOnly(Side.CLIENT)
public class MixinMinecraft {

    @Shadow @Final private static Logger logger;

    @Shadow public GuiScreen currentScreen;

    @Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;checkGLError(Ljava/lang/String;)V", ordinal = 2, shift = At.Shift.AFTER))
    public void startGame(CallbackInfo ci) {
        ElementalClient.INSTANCE.startClient();
    }

    @ModifyConstant(method = "createDisplay", constant = @Constant(stringValue = "Minecraft 1.8.9"))
    public String changeTitle(String input) {
        return ElementalClient.INSTANCE.getClientTitle();
    }

    @Inject(method = "runTick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;joinPlayerCounter:I", ordinal = 0))
    public void onTick(final CallbackInfo callbackInfo) {
        ElementalClient.eventBus.callEvent(new TickEvent());
    }

    @Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At("HEAD"))
    public void onPreLoad(WorldClient worldClientIn, String loadingMessage, CallbackInfo ci) {
        ElementalClient.eventBus.callEvent(new WorldBeginLoadEvent(worldClientIn));
    }

    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;dispatchKeypresses()V", shift = At.Shift.AFTER))
    private void onKey(CallbackInfo callbackInfo) {

        if (Keyboard.getEventKeyState() && currentScreen == null)
            ElementalClient.eventBus.callEvent(new KeyEvent(Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey()));
    }
}
