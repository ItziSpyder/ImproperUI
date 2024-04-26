package io.github.itzispyder.improperui.render;

import io.github.itzispyder.improperui.render.constants.Alignment;
import io.github.itzispyder.improperui.render.math.Color;
import io.github.itzispyder.improperui.render.math.Dimensions;
import io.github.itzispyder.improperui.util.RenderUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class Element {

    public int marginLeft, marginRight, marginTop, marginBottom;
    public int paddingLeft, paddingRight, paddingTop, paddingBottom;
    public int x, y, width, height;
    public Color borderColor, fillColor, shadowColor;
    public int borderThickness, borderRadius, shadowDistance;
    public String clickAction, startHoverAction, stopHoverAction;
    public Alignment textAlignment;
    public String innerText;
    public float textScale;
    public boolean textShadow;

    public Element(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        marginLeft = marginRight = marginTop = marginBottom = 0;
        paddingLeft = paddingRight = paddingTop = paddingBottom = 0;
        shadowColor = new Color(0x80000000);
        fillColor = new Color(0xFFFFFFFF);
        borderColor = new Color(0xFF202020);
        borderRadius = borderThickness = shadowDistance = 0;

        textScale = 1.0F;
        textAlignment = Alignment.LEFT;
        textShadow = false;
    }

    public Element margin(int margin) {
        marginLeft = marginRight = marginTop = marginBottom = margin;
        return this;
    }

    public Element padding(int padding) {
        paddingLeft = paddingRight = paddingTop = paddingBottom = padding;
        return this;
    }

    public Element border(int borderThickness, int borderRadius, Color borderColor) {
        this.borderThickness = borderThickness;
        this.borderRadius = borderRadius;
        this.borderColor = borderColor;
        return this;
    }

    public Element shadow(int shadowDistance, Color shadowColor) {
        this.shadowDistance = shadowDistance;
        this.shadowColor = shadowColor;
        return this;
    }

    public Element size(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public Element position(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Element clickAction(String clickAction) {
        this.clickAction = clickAction;
        return this;
    }

    public Dimensions getRawDimensions() {
        return new Dimensions(x, y, width, height);
    }

    public Dimensions getDimensions() {
        int x = this.x - paddingLeft - marginLeft;
        int y = this.y - paddingTop - marginTop;
        int width = this.width + marginLeft + paddingLeft + paddingRight + marginRight;
        int height = this.height + marginTop + paddingTop + paddingBottom + marginBottom;
        return new Dimensions(x, y, width, height);
    }

    // built-in

    public void onRender(DrawContext context, float delta) {
        RenderUtils.fillRoundShadow(context,
                x - paddingLeft,
                y - paddingTop,
                width + paddingLeft + paddingRight,
                height + paddingTop + paddingBottom,
                borderRadius,
                borderThickness,
                shadowColor.getHex(),
                shadowColor.getHexCustomAlpha(0)
        );
        RenderUtils.fillRoundRect(context,
                x - paddingLeft,
                y - paddingTop,
                width + paddingLeft + paddingRight,
                height + paddingTop + paddingBottom,
                borderRadius,
                shadowColor.getHex()
        );

        if (innerText != null) {
            Text text = Text.of(innerText);
            switch (textAlignment) {
                case LEFT -> RenderUtils.drawDefaultScaledText(context, text, x, y + height / 3, textScale, textShadow);
                case CENTER -> RenderUtils.drawDefaultCenteredScaledText(context, text, x + width / 2, y + height / 3, textScale, textShadow);
                case RIGHT -> RenderUtils.drawDefaultRightScaledText(context, text, x + width, y + height / 3, textScale, textShadow);
            }
        }
    }

    public void onClick() {
        tryInvoke(clickAction);
    }

    public void onStartHover() {
        tryInvoke(startHoverAction);
    }

    public void onStopHover() {
        tryInvoke(stopHoverAction);
    }

    private void tryInvoke(String methodName) throws IllegalArgumentException {
        if (methodName == null)
            return;

        var methods = this.getClass().getDeclaredMethods();
        for (var method : methods) {
            if (methodName.equals(method.getName())) {
                try {
                    if (method.getParameterCount() == 0 || method.getParameters()[0].getType() != Element.class)
                        error("specified callback method must have one parameter: io.github.itzispyder.improperui.render.Element");
                    method.setAccessible(true);
                    method.invoke(null, this);
                }
                catch (Exception ex) {
                    error("encountered error invoking method: %s", ex.getMessage());
                }
                return;
            }
        }
        error("method \"%s.%s\" not found!", this.getClass().getSimpleName(), clickAction);
    }

    public void error(String message, Object... args) {
        throw new IllegalArgumentException(message.formatted(args));
    }
}
