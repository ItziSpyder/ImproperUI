package io.github.itzispyder.improperui.interfaces;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.resources.Identifier;

public interface AccessorFontManager {

    Font improperUI$createRenderer(Identifier fontId);

    FontSet improperUI$getInternalStorage(Identifier fontId);
}
