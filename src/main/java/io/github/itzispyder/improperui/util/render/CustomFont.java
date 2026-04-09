package io.github.itzispyder.improperui.util.render;

import io.github.itzispyder.improperui.interfaces.AccessorFontManager;
import io.github.itzispyder.improperui.mixin.AccessorMinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GlyphSource;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.glyphs.EffectGlyph;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class CustomFont implements Font.Provider, AutoCloseable {

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

    private GlyphSource getGlyphsImpl(FontDescription source) {
        AccessorMinecraftClient mc = (AccessorMinecraftClient) Minecraft.getInstance();
        AccessorFontManager fontManager = (AccessorFontManager) mc.accessFontManager();
        FontSet storage = fontManager.improperUI$getInternalStorage(fontId);
        return storage.source(advanceValidating);
    }

    @Override
    public GlyphSource glyphs(FontDescription source) {
        if (cached != null && source.equals(cached.source)) {
            return cached.glyphs;
        }
        else {
            GlyphSource glyphProvider = this.getGlyphsImpl(source);
            this.cached = new Cached(source, glyphProvider);
            return glyphProvider;
        }
    }

    @Override
    public EffectGlyph effect() {
        EffectGlyph effectGlyph = this.rectangle;

        if (effectGlyph == null) {
            AccessorMinecraftClient mc = (AccessorMinecraftClient) Minecraft.getInstance();
            AccessorFontManager fontManager = (AccessorFontManager) mc.accessFontManager();
            FontSet storage = fontManager.improperUI$getInternalStorage(FontDescription.DEFAULT.id());

            effectGlyph = storage.whiteGlyph();
            this.rectangle = effectGlyph;
        }

        return effectGlyph;
    }

    @Environment(EnvType.CLIENT)
    record Cached(FontDescription source, GlyphSource glyphs) {}
}
