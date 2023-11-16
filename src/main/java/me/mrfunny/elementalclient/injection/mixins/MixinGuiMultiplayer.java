package me.mrfunny.elementalclient.injection.mixins;

import me.mrfunny.elementalclient.ElementalClient;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.OldServerPinger;
import net.minecraft.util.EnumChatFormatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.UnknownHostException;

@Mixin(GuiMultiplayer.class)
public class MixinGuiMultiplayer {

    @Shadow @Final private OldServerPinger oldServerPinger;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void onInit(GuiScreen parentScreen, CallbackInfo ci) {
        ElementalClient.INSTANCE.getDiscordHandler().updateActivity("Selecting a server...").start();
    }

    @Inject(method = "connectToServer", at = @At("RETURN"))
    public void onConnect(ServerData server, CallbackInfo ci) {
        ElementalClient.INSTANCE.getDiscordHandler().updateServer(server);

//        thread.start();

//        ElementalClient.discordHandler.updateActivity("Playing on " + ip + " (" + populationInfo + ")").start();
    }
}