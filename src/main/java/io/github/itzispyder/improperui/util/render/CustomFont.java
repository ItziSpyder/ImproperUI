package io.github.itzispyder.improperui.util.render;

import io.github.itzispyder.improperui.interfaces.AccessorFontManager;
import io.github.itzispyder.improperui.mixin.AccessorMinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.EffectGlyph;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.GlyphProvider;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CustomFont implements TextRenderer.GlyphsProvider, AutoCloseable {

    private final boolean advanceValidating;
    private volatile Cached cached;
    private volatile EffectGlyph rectangle;
    private final Identifier fontId;

    public CustomFont(Identifier fontId, boolean advanceValidating) {
        this.fontId = fontId;
        this.advanceValidating = advanceValidating;
    }

    public void clear() {
        this.cached = null;
        this.rectangle = null;
    }

    @Override
    public void close() {
        this.clear();
    }

    private GlyphProvider getGlyphsImpl(StyleSpriteSource source) {
        AccessorMinecraftClient mc = (AccessorMinecraftClient) MinecraftClient.getInstance();
        AccessorFontManager fontManager = (AccessorFontManager) mc.accessFontManager();
        FontStorage storage = fontManager.improperUI$getInternalStorage(fontId);
        return storage.getGlyphs(advanceValidating);
    }

    @Override
    public GlyphProvider getGlyphs(StyleSpriteSource source) {
        if (cached != null && source.equals(cached.source)) {
            return cached.glyphs;
        }
        else {
            GlyphProvider glyphProvider = this.getGlyphsImpl(source);
            this.cached = new Cached(source, glyphProvider);
            return glyphProvider;
        }
    }

    @Override
    public EffectGlyph getRectangleGlyph() {
        EffectGlyph effectGlyph = this.rectangle;

        if (effectGlyph == null) {
            AccessorMinecraftClient mc = (AccessorMinecraftClient) MinecraftClient.getInstance();
            AccessorFontManager fontManager = (AccessorFontManager) mc.accessFontManager();
            FontStorage storage = fontManager.improperUI$getInternalStorage(StyleSpriteSource.DEFAULT.id());

            effectGlyph = storage.getRectangleBakedGlyph();
            this.rectangle = effectGlyph;
        }

        return effectGlyph;
    }

    @Environment(EnvType.CLIENT)
    record Cached(StyleSpriteSource source, GlyphProvider glyphs) {}
}
