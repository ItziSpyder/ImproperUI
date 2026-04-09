package io.github.itzispyder.improperui.mixin;

import io.github.itzispyder.improperui.interfaces.AccessorFontManager;
import io.github.itzispyder.improperui.util.render.CustomFont;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(FontManager.class)
public abstract class MixinFontManager implements AccessorFontManager {

    @Shadow @Final private Map<Identifier, FontSet> fontSets;
    @Shadow @Final FontSet missingFontSet;

    @Override
    public Font improperUI$createRenderer(Identifier fontId) {
        return new Font(new CustomFont(fontId, false));
    }

    @Override
    public FontSet improperUI$getInternalStorage(Identifier fontId) {
        return fontSets.getOrDefault(fontId, this.missingFontSet);
    }
}
