package io.github.itzispyder.improperui.mixin;

import io.github.itzispyder.improperui.client.ImproperUIClient;
import io.github.itzispyder.improperui.interfaces.AccessorFontManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraftClient {

    @Shadow @Final private FontManager fontManager;
    @Shadow @Final public static Identifier UNIFORM_FONT;

    @Inject(method = "updateFontOptions", at = @At("TAIL"))
    public void initFont(CallbackInfo ci) {
        ImproperUIClient improper = ImproperUIClient.getInstance();
        AccessorFontManager fonts = (AccessorFontManager) this.fontManager;
        improper.codeRenderer = fonts.improperUI$createRenderer(UNIFORM_FONT);
    }
}
