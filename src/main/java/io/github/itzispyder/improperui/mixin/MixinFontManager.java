package io.github.itzispyder.improperui.mixin;

import io.github.itzispyder.improperui.interfaces.AccessorFontManager;
import io.github.itzispyder.improperui.util.render.CustomFont;
import net.minecraft.client.font.FontManager;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(FontManager.class)
public abstract class MixinFontManager implements AccessorFontManager {

    @Shadow @Final private Map<Identifier, FontStorage> fontStorages;
    @Shadow @Final FontStorage missingStorage;

    @Override
    public TextRenderer improperUI$createRenderer(Identifier fontId) {
        return new TextRenderer(new CustomFont(fontId, false));
    }

    @Override
    public FontStorage improperUI$getInternalStorage(Identifier fontId) {
        return fontStorages.getOrDefault(fontId, this.missingStorage);
    }
}
