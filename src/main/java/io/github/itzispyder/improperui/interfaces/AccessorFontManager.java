package io.github.itzispyder.improperui.interfaces;

import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.Identifier;

public interface AccessorFontManager {

    TextRenderer improperUI$createRenderer(Identifier fontId);

    FontStorage improperUI$getInternalStorage(Identifier fontId);
}
