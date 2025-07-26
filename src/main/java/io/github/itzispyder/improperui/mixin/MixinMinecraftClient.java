package io.github.itzispyder.improperui.mixin;

import io.github.itzispyder.improperui.client.ImproperUIClient;
import io.github.itzispyder.improperui.interfaces.FontManagerAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Shadow @Final private FontManager fontManager;
    @Shadow @Final public static Identifier UNICODE_FONT_ID;

    @Inject(method = "onFontOptionsChanged", at = @At("TAIL"))
    public void initFont(CallbackInfo ci) {
        var fonts = ((FontManagerAccessor)this.fontManager);
        ImproperUIClient.getInstance().codeRenderer = fonts.improperUI$createRenderer(UNICODE_FONT_ID);
    }
}
