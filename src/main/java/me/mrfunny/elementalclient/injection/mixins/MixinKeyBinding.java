package me.mrfunny.elementalclient.injection.mixins;

import me.mrfunny.elementalclient.ElementalClient;
import me.mrfunny.elementalclient.event.KeyStateChangeEvent;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.IntHashMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBinding.class)
public class MixinKeyBinding {

    @Shadow @Final private static IntHashMap<KeyBinding> hash;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static void setKeyBindState(int keyCode, boolean pressed) {
        if(keyCode == 0) return;
        KeyBinding keybinding = hash.lookup(keyCode);
        if(keybinding == null) return;
        keybinding.pressed = pressed;
        ElementalClient.eventBus.callEvent(new KeyStateChangeEvent(keyCode, keybinding, pressed));
    }
}
