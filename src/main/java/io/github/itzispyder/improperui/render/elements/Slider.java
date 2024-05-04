package io.github.itzispyder.improperui.render.elements;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.itzispyder.improperui.config.ConfigKey;
import io.github.itzispyder.improperui.config.ConfigKeyHolder;
import io.github.itzispyder.improperui.render.Element;
import io.github.itzispyder.improperui.script.ScriptParser;
import io.github.itzispyder.improperui.util.MathUtils;
import io.github.itzispyder.improperui.util.RenderUtils;
import net.minecraft.client.gui.DrawContext;

import static io.github.itzispyder.improperui.util.RenderUtils.drawText;
import static io.github.itzispyder.improperui.util.RenderUtils.fillRect;

public class Slider extends Element implements ConfigKeyHolder {

    public double min, max, val;
    public int decimalPlaces;

    private int fillEnd;

    public Slider() {
        super();
        queueProperty("size: 100 16");
        queueProperty("range: 0 10");
        queueProperty("value: 10");
        queueProperty("decimal-places: 1");
        queueProperty("border-radius: 360");
        queueProperty("background-color: green");
        queueProperty("inner-text: \"Slider\"");
        this.fillEnd = x + width;
    }

    @Override
    public void init() {
        super.init();
        registerProperty("min", args -> min = args.get(0).toDouble());
        registerProperty("minimum", args -> min = args.get(0).toDouble());
        registerProperty("max", args -> max = args.get(0).toDouble());
        registerProperty("maximum", args -> max = args.get(0).toDouble());
        registerProperty("val", args -> val = args.get(0).toDouble());
        registerProperty("value", args -> val = args.get(0).toDouble());
        registerProperty("decimal-places", args -> decimalPlaces = args.get(0).toInt());
        registerProperty("range", args -> {
            min = args.get(0).toDouble();
            max = args.get(1).toDouble();
        });
    }

    @Override
    public void onRender(DrawContext context, int mx, int my, float delta) {
        int x = getPosX();
        int y = getPosY();

        boolean notOpaque = opacity < 1.0F;
        if (notOpaque)
            RenderSystem.setShaderColor(1, 1, 1, opacity);

        if (parentPanel != null && parentPanel.selected == this) {
            this.fillEnd = MathUtils.clamp(mx, x, x + width);
            double range = max - min;
            double ratio = (double)(fillEnd - x) / (double)width;
            double value = range * ratio;
            val = MathUtils.round(value + min, decimalPlaces);
        }

        double range = max - min;
        double value = val - min;
        double ratio = value / range;
        int len = (int)(width * ratio);
        this.fillEnd = x + len;

        val = MathUtils.round(range * ratio + min, decimalPlaces);

        String text = "§o" + getText() + ": §r(" + val + ")";
        drawText(context, text, x, y + 10 / 3, 0.9F, false);
        fillRect(context, x, y + 10 + 10 / 3, width, 2, borderColor.getHex());
        fillRect(context, x, y + 10 + 10 / 3, len, 2, fillColor.getHex());

        int pad = paddingTop + marginTop + borderThickness + MathUtils.clamp(borderRadius, 0, height / 4);
        x = fillEnd - pad;
        y = y + 10 + 10 / 3 + 1 - pad;

        RenderUtils.fillRoundShadow(context,
                x + marginLeft - paddingLeft - borderThickness,
                y + marginTop - paddingTop - borderThickness,
                height / 2 + paddingLeft + paddingRight + borderThickness * 2,
                height / 2 + paddingTop + paddingBottom + borderThickness * 2,
                borderRadius,
                shadowDistance,
                shadowColor.getHex(),
                shadowColor.getHexCustomAlpha(0)
        );
        RenderUtils.fillRoundShadow(context,
                x + marginLeft - paddingLeft,
                y + marginTop - paddingTop,
                height / 2 + paddingLeft + paddingRight,
                height / 2 + paddingTop + paddingBottom,
                borderRadius,
                borderThickness,
                borderColor.getHex(),
                borderColor.getHex()
        );
        RenderUtils.fillRoundRect(context,
                x + marginLeft - paddingLeft,
                y + marginTop - paddingTop,
                height / 2 + paddingLeft + paddingRight,
                height / 2 + paddingTop + paddingBottom,
                borderRadius,
                fillColor.getHex()
        );
        if (backgroundImage != null) {
            RenderUtils.drawRoundTexture(context,
                    backgroundImage,
                    x + marginLeft - paddingLeft,
                    y + marginTop - paddingTop,
                    height / 2 + paddingLeft + paddingRight,
                    height / 2 + paddingTop + paddingBottom,
                    borderRadius
            );
        }

        if (notOpaque)
            RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    @Override
    public void style() {
        super.style();

        var property = ScriptParser.CACHE.getProperty(getConfigKey());
        if (property != null)
            val = property.get(0).toDouble();
    }

    @Override
    public void onLeftClick(int mx, int my, boolean release) {
        super.onLeftClick(mx, my, release);

        var key = getConfigKey();
        if (release && key != null)
            ScriptParser.CACHE.setProperty(key, val, true);
    }

    @Override
    public ConfigKey getConfigKey() {
        return ConfigKeyHolder.ELEMENT_KEY_HOLDER.apply(this);
    }
}