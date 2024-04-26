package io.github.itzispyder.improperui.render;

import io.github.itzispyder.improperui.render.constants.Alignment;
import io.github.itzispyder.improperui.render.constants.Position;
import io.github.itzispyder.improperui.render.math.Color;
import io.github.itzispyder.improperui.render.math.Dimensions;
import io.github.itzispyder.improperui.util.RenderUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class Element {

    public Position position;
    public int marginLeft, marginRight, marginTop, marginBottom;
    public int paddingLeft, paddingRight, paddingTop, paddingBottom;
    public int x, y, width, height;
    public Color borderColor, fillColor, shadowColor;
    public int borderThickness, borderRadius, shadowDistance;
    public String rightClickAction, leftClickAction, middleClickAction, scrollAction, startHoverAction, stopHoverAction;
    public Alignment textAlignment;
    public String innerText;
    public float textScale;
    public boolean textShadow;
    private Element parent;
    private final List<Element> children;

    public Element(int x, int y, int width, int height) {
        children = new ArrayList<>();
        position = Position.INHERIT;

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

    public int getPosX() {
        return (position == Position.INHERIT && parent != null) ? (parent.x + x) : x;
    }

    public int getPosY() {
        return (position == Position.INHERIT && parent != null) ? (parent.y + y) : y;
    }

    public Dimensions getRawDimensions() {
        return new Dimensions(x, y, width, height);
    }

    public Dimensions getDimensions() {
        int x = getPosX() - paddingLeft - marginLeft;
        int y = getPosY() - paddingTop - marginTop;
        int width = this.width + marginLeft + paddingLeft + paddingRight + marginRight;
        int height = this.height + marginTop + paddingTop + paddingBottom + marginBottom;
        return new Dimensions(x, y, width, height);
    }

    public void addChild(Element child) {
        if (child == null || child == this || child.parent != null || children.contains(child))
            return;
        children.add(child);
        child.parent = this;
    }

    public void removeChild(Element child) {
        if (child == null)
            return;
        child.parent = null;
        children.remove(child);
    }

    // built-in

    public void onRender(DrawContext context, float delta) {
        int x = getPosX();
        int y = getPosY();

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

    public void onRightClick() {
        tryInvoke(rightClickAction);
    }

    public void onLeftClick() {
        tryInvoke(leftClickAction);
    }

    public void onMiddleClick() {
        tryInvoke(middleClickAction);
    }

    public void onScroll() {
        tryInvoke(scrollAction);
    }

    public void onStartHover() {
        tryInvoke(startHoverAction);
    }

    public void onStopHover() {
        tryInvoke(stopHoverAction);
    }

    private void tryInvoke(String methodName) throws IllegalArgumentException {
        if (methodName == null || methodName.trim().isEmpty())
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
        error("method \"%s.%s\" not found!", this.getClass().getSimpleName(), methodName);
    }

    public void error(String message, Object... args) {
        throw new IllegalArgumentException(message.formatted(args));
    }

    @Override
    public String toString() {
        return "Element:{dimensions:[%s,%s,%s,%s],margin:[%s,%s,%s,%s],padding:[%s,%s,%s,%s],border:[%s,%s,%s],fill:%s,shadow:[%s,%s],mouse:['%s','%s','%s','%s'],hoverAction:['%s','%s'],text:[%s,%s,'%s',%s]}".formatted(
                x, y, width, height,
                marginLeft, marginRight, marginTop, marginBottom,
                paddingLeft, paddingRight, paddingTop, paddingBottom,
                borderThickness, borderRadius, borderColor,
                fillColor,
                shadowDistance, shadowColor,
                rightClickAction, leftClickAction, middleClickAction, scrollAction,
                startHoverAction, stopHoverAction,
                textScale, textAlignment, innerText, textShadow
        );
    }
}
