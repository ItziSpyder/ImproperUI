package io.github.itzispyder.improperui.mixin;

import io.github.itzispyder.improperui.client.ImproperUIClient;
import io.github.itzispyder.improperui.interfaces.FontManagerAccessor;
import io.github.itzispyder.improperui.script.ScriptParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Unique private static final ImproperUIClient system = ImproperUIClient.getInstance();
    @Shadow @Nullable public HitResult crosshairTarget;
    @Shadow @Final private FontManager fontManager;

    @Shadow @Final public static Identifier UNICODE_FONT_ID;

    @Inject(method = "doAttack", at = @At("HEAD"))
    public void setScreen(CallbackInfoReturnable<Boolean> cir) {
        if (crosshairTarget instanceof BlockHitResult hit && hit.getType() != HitResult.Type.MISS) {
            ScriptParser.run(new File("screen.ui"));
        }
    }

    @Inject(method = "initFont", at = @At("TAIL"))
    public void initFont(boolean forcesUnicode, CallbackInfo ci) {
        var fonts = ((FontManagerAccessor)this.fontManager);
        system.codeRenderer = fonts.createRenderer(UNICODE_FONT_ID);
    }
}
