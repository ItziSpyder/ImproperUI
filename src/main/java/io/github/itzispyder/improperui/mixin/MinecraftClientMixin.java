package io.github.itzispyder.improperui.mixin;

import io.github.itzispyder.improperui.script.ScriptParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow @Nullable public HitResult crosshairTarget;

    @Inject(method = "doAttack", at = @At("HEAD"))
    public void setScreen(CallbackInfoReturnable<Boolean> cir) {
        if (crosshairTarget instanceof BlockHitResult hit && hit.getType() != HitResult.Type.MISS) {
            ScriptParser.run(new File("screen.ui"));
        }
    }
}
