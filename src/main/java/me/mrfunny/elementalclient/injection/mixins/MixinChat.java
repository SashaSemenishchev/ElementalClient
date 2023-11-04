package me.mrfunny.elementalclient.injection.mixins;

import me.mrfunny.elementalclient.ElementalClient;
import me.mrfunny.elementalclient.event.ChatEvent;
import me.mrfunny.elementalclient.util.MinecraftInstance;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiNewChat.class)
public class MixinChat {
    @Inject(method = "printChatMessage", at = @At("HEAD"), cancellable = true)
    public void handleChat(IChatComponent chatComponent, CallbackInfo ci) {
        ChatEvent event = new ChatEvent(chatComponent);
        ElementalClient.eventBus.callEvent(event);
        if(event.isCancelled()) {
            ci.cancel();
        }
    }
}
