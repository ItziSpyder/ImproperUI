package io.github.itzispyder.improperui.mixin;

import io.github.itzispyder.improperui.client.ImproperUIClient;
import io.github.itzispyder.improperui.config.keybinds.Keybind;
import io.github.itzispyder.improperui.render.constants.InputType;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public abstract class MixinKeyboard {

    @Unique
    private static final ImproperUIClient system = ImproperUIClient.getInstance();

    @Inject(method = "onKey", at = @At("HEAD"))
    public void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        var a = InputType.of(action);
        if (!(a.isRelease() || a.isClick()))
            return;

        for (Keybind bind : system.getBindsOf(key)) {
            if (bind.canPress(key)) {
                if (a.isClick())
                    bind.onPress();
                else if (a.isRelease())
                    bind.onRelease();
            }
        }
    }
}
