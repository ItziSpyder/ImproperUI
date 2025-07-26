package io.github.itzispyder.improperui.interfaces;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.Identifier;

public interface FontManagerAccessor {

    TextRenderer improperUI$createRenderer(Identifier fontId);
}
