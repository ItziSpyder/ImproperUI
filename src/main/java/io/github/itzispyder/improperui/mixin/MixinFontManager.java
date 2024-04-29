package io.github.itzispyder.improperui.mixin;

import io.github.itzispyder.improperui.interfaces.FontManagerAccessor;
import net.minecraft.client.font.FontManager;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(FontManager.class)
public abstract class MixinFontManager implements FontManagerAccessor {

    @Shadow @Final private Map<Identifier, FontStorage> fontStorages;
    @Shadow @Final private FontStorage missingStorage;

    @Override
    public TextRenderer createRenderer(Identifier fontId) {
        return new TextRenderer(id -> this.fontStorages.getOrDefault(fontId, this.missingStorage), false);
    }
}
