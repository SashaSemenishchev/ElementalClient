package me.mrfunny.elementalclient.injection.mixins;

import gg.essential.vigilance.gui.SettingsGui;
import me.mrfunny.elementalclient.ui.NoBackground;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SettingsGui.class)
public class MixinSettingsGui implements NoBackground {
}
