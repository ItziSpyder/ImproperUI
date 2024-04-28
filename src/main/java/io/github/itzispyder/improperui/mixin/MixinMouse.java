package io.github.itzispyder.improperui.mixin;

import io.github.itzispyder.improperui.client.ImproperUIClient;
import io.github.itzispyder.improperui.config.keybinds.Keybind;
import io.github.itzispyder.improperui.render.constants.InputType;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MixinMouse {

    @Unique
    private static final ImproperUIClient system = ImproperUIClient.getInstance();

    @Inject(method = "onMouseButton", at = @At("HEAD"))
    public void onButton(long window, int button, int action, int mods, CallbackInfo ci) {
        var a = InputType.of(action);
        if (!(a.isRelease() || a.isClick()))
            return;

        int code = 69420 + button;
        for (Keybind bind : system.getBindsOf(code)) {
            if (bind.canPress(code)) {
                if (a.isClick())
                    bind.onPress();
                else if (a.isRelease())
                    bind.onRelease();
            }
        }
    }
}
