package io.github.itzispyder.improperui.render;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.itzispyder.improperui.render.constants.*;
import io.github.itzispyder.improperui.render.math.Color;
import io.github.itzispyder.improperui.render.math.Dimensions;
import io.github.itzispyder.improperui.script.CallbackHandler;
import io.github.itzispyder.improperui.script.ScriptArgs;
import io.github.itzispyder.improperui.util.MathUtils;
import io.github.itzispyder.improperui.util.RenderUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.Consumer;

public class Element {

    private static int sequence = 0;
    public final int order = sequence++;
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
    public ChildrenAlignment childrenAlignment;
    public int gridColumns;
    public Visibility visibility;
    public BackgroundClip backgroundClip;
    public Identifier backgroundImage;
    public float opacity;

    private Element parent;
    private final List<Element> children;
    private final Map<String, Consumer<ScriptArgs>> properties;

    public Element() {
        this(0, 0, 0, 0);
    }

    public Element(int x, int y, int width, int height) {
        children = new ArrayList<>();
        properties = new HashMap<>();
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

        childrenAlignment = ChildrenAlignment.GRID;
        gridColumns = 1;
        visibility = Visibility.VISIBLE;
        backgroundClip = BackgroundClip.NONE;
        opacity = 1.0F;

        this.init();
    }

    public void init() {
        registerProperty("position", args -> position = args.get(0).toEnum(Position.class));

        registerProperty("x", args -> x = args.get(0).toInt());
        registerProperty("y", args -> y = args.get(0).toInt());
        registerProperty("width", args -> width = args.get(0).toInt());
        registerProperty("height", args -> height = args.get(0).toInt());
        registerProperty("pos", args -> position(args.get(0).toInt(), args.get(1).toInt()));
        registerProperty("size", args -> size(args.get(0).toInt(), args.get(1).toInt()));

        registerProperty("padding-left", args -> paddingLeft = args.get(0).toInt());
        registerProperty("padding-right", args -> paddingRight = args.get(0).toInt());
        registerProperty("padding-top", args -> paddingTop = args.get(0).toInt());
        registerProperty("padding-bottom", args -> paddingBottom = args.get(0).toInt());
        registerProperty("padding", args -> padding(args.get(0).toInt()));

        registerProperty("margin-left", args -> marginLeft = args.get(0).toInt());
        registerProperty("margin-right", args -> marginRight = args.get(0).toInt());
        registerProperty("margin-top", args -> marginTop = args.get(0).toInt());
        registerProperty("margin-bottom", args -> marginBottom = args.get(0).toInt());
        registerProperty("margin", args -> margin(args.get(0).toInt()));

        registerProperty("border-thickness", args -> borderThickness = args.get(0).toInt());
        registerProperty("border-radius", args -> borderRadius = args.get(0).toInt());
        registerProperty("border-color", args -> borderColor = Color.parse(args.get(0).toString()));
        registerProperty("border", args -> border(args.get(0).toInt(), args.get(1).toInt(), Color.parse(args.get(0).toString())));

        registerProperty("fill-color", args -> fillColor = Color.parse(args.get(0).toString()));
        registerProperty("shadow-color", args -> shadowColor = Color.parse(args.get(0).toString()));
        registerProperty("shadow-distance", args -> shadowDistance = args.get(0).toInt());
        registerProperty("shadow", args -> shadow(args.get(0).toInt(), Color.parse(args.get(0).toString())));

        registerProperty("right-click-action", args -> rightClickAction = args.get(0).toString());
        registerProperty("left-click-action", args -> leftClickAction = args.get(0).toString());
        registerProperty("middle-click-action", args -> middleClickAction = args.get(0).toString());
        registerProperty("scroll-action", args -> scrollAction = args.get(0).toString());
        registerProperty("click-action", args -> {
            String method = args.get(0).toString();
            rightClickAction = method;
            leftClickAction = method;
            middleClickAction = method;
        });
        registerProperty("on-right-click", args -> rightClickAction = args.get(0).toString());
        registerProperty("on-left-click", args -> leftClickAction = args.get(0).toString());
        registerProperty("on-middle-click", args -> middleClickAction = args.get(0).toString());
        registerProperty("on-scroll", args -> scrollAction = args.get(0).toString());
        registerProperty("on-click", args -> {
            String method = args.get(0).toString();
            rightClickAction = method;
            leftClickAction = method;
            middleClickAction = method;
        });

        registerProperty("inner-text", args -> innerText = args.getQuoteAndRemove());
        registerProperty("text-scale", args -> textScale = args.get(0).toFloat());
        registerProperty("text-shadow", args -> textShadow = args.get(0).toBool());
        registerProperty("text-align", args -> textAlignment = args.get(0).toEnum(Alignment.class));

        registerProperty("children-align", args -> childrenAlignment = args.get(0).toEnum(ChildrenAlignment.class));
        registerProperty("grid-columns", args -> gridColumns = args.get(0).toInt());
        registerProperty("visibility", args -> visibility = args.get(0).toEnum(Visibility.class));
        registerProperty("background-clip", args -> backgroundClip = args.get(0).toEnum(BackgroundClip.class));
        registerProperty("background-color", args -> fillColor = Color.parse(args.get(0).toString()));
        registerProperty("background-image", args -> backgroundImage = new Identifier(args.get(0).toString()));
        registerProperty("opacity", args -> opacity = args.get(0).toFloat());
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
        return new Dimensions(getPosX(), getPosY(), width, height);
    }

    public Dimensions getPaddedDimensions() {
        Dimensions dim = getDimensions();
        dim.x -= paddingLeft;
        dim.y -= paddingTop;
        dim.width += paddingLeft + paddingRight;
        dim.height += paddingTop + paddingBottom;
        return dim;
    }

    public Dimensions getBorderedDimensions() {
        Dimensions dim = getPaddedDimensions();
        dim.x -= borderThickness;
        dim.y -= borderThickness;
        dim.width += borderThickness * 2;
        dim.height += borderThickness * 2;
        return dim;
    }

    public Dimensions getMarginalDimensions() {
        Dimensions dim = getBorderedDimensions();
        dim.x -= marginLeft;
        dim.y -= marginTop;
        dim.width += marginLeft + marginRight;
        dim.height += marginTop + marginBottom;
        return dim;
    }

    public void move(int deltaX, int deltaY) {
        this.x += deltaX;
        this.y += deltaY;

        this.children.forEach(child -> {
            child.move(deltaX, deltaY);
        });
    }

    public void moveTo(int x, int y) {
        int delX = x - this.x;
        int delY = y - this.y;

        this.x = x;
        this.y = y;

        this.children.forEach(child -> {
            child.move(delX, delY);
        });
    }

    public void centerIn(int frameWidth, int frameHeight) {
        moveTo(frameWidth / 2 - width / 2, frameHeight / 2 - height / 2);
    }

    public void boundIn(int frameWidth, int frameHeight) {
        int x = MathUtils.clamp(this.x, 0, frameWidth - width);
        int y = MathUtils.clamp(this.y, 0, frameHeight - height);
        moveTo(x, y);
    }

    public void addChild(Element child) {
        if (child == null || child == this || child.parent != null || children.contains(child))
            return;
        children.add(child);
        child.parent = this;

        int column = 0;
        int rowY = getPosY();
        int columnX = getPosX();

        if (childrenAlignment == ChildrenAlignment.GRID) {
            for (Element e : children) {
                e.position = Position.INHERIT;
                e.moveTo(columnX, rowY);

                Dimensions dim = getMarginalDimensions();
                columnX += dim.width;

                if (column++ >= gridColumns - 1) {
                    column = 0;
                    rowY += dim.height;
                }
            }
        }
    }

    public void removeChild(Element child) {
        if (child == null)
            return;
        child.parent = null;
        children.remove(child);
    }

    public List<Element> getChildren() {
        return new ArrayList<>(children);
    }

    public List<Element> getChildrenOrdered() {
        return new ArrayList<>(getChildren().stream()
                .sorted(Comparator.comparing(e -> ((Element)e).order).reversed())
                .toList());
    }

    public void clearChildren() {
        var children = getChildren();
        children.forEach(this::removeChild);
    }

    public void registerProperty(String key, Consumer<ScriptArgs> callback) {
        if (key != null && !key.isEmpty() && callback != null)
            properties.put(key.toLowerCase(), callback);
    }

    /**
     * This is formatted key:args
     * @param entry property entry
     */
    public void callProperty(String entry) {
        entry = entry.trim();
        String[] split = entry.trim().split("\\s*[:=]\\s*");
        if (split.length < 2 || !properties.containsKey(split[0]))
            return;

        String value = entry.substring(split[0].length()).replaceFirst("\\s*[:=]\\s*", "");
        properties.get(split[0]).accept(new ScriptArgs(value.split("\\s+")));
    }

    // built-in

    public void onRender(DrawContext context, float delta) {
        int x = getPosX();
        int y = getPosY();

        if (visibility == Visibility.INVISIBLE)
            return;

        boolean notOpaque = opacity < 1.0F;
        if (notOpaque)
            RenderSystem.setShaderColor(1, 1, 1, opacity);

        if (visibility != Visibility.ONLY_CHILDREN) {
            RenderUtils.fillRoundShadow(context,
                    x - paddingLeft - borderThickness,
                    y - paddingTop - borderThickness,
                    width + paddingLeft + paddingRight + borderThickness * 2,
                    height + paddingTop + paddingBottom + borderThickness * 2,
                    borderRadius,
                    shadowDistance,
                    shadowColor.getHex(),
                    shadowColor.getHexCustomAlpha(0)
            );
            RenderUtils.fillRoundShadow(context,
                    x - paddingLeft,
                    y - paddingTop,
                    width + paddingLeft + paddingRight,
                    height + paddingTop + paddingBottom,
                    borderRadius,
                    borderThickness,
                    borderColor.getHex(),
                    borderColor.getHex()
            );
            RenderUtils.fillRoundRect(context,
                    x - paddingLeft,
                    y - paddingTop,
                    width + paddingLeft + paddingRight,
                    height + paddingTop + paddingBottom,
                    borderRadius,
                    shadowColor.getHex()
            );
            if (backgroundImage != null) {
                RenderUtils.drawRoundTexture(context,
                        backgroundImage,
                        x - paddingLeft,
                        y - paddingTop,
                        width + paddingLeft + paddingRight,
                        height + paddingTop + paddingBottom,
                        borderRadius
                );
            }

            if (innerText != null) {
                Text text = Text.of(innerText);
                switch (textAlignment) {
                    case LEFT -> RenderUtils.drawDefaultScaledText(context, text, x, y + height / 3, textScale, textShadow);
                    case CENTER -> RenderUtils.drawDefaultCenteredScaledText(context, text, x + width / 2, y + height / 3, textScale, textShadow);
                    case RIGHT -> RenderUtils.drawDefaultRightScaledText(context, text, x + width, y + height / 3, textScale, textShadow);
                }
            }
        }

        if (visibility != Visibility.ONLY_SELF) {
            boolean shouldClip = backgroundClip != BackgroundClip.NONE;
            if (shouldClip) {
                Dimensions shape;
                switch (backgroundClip) {
                    case PADDING -> shape = getPaddedDimensions();
                    case BORDER -> shape = getBorderedDimensions();
                    case MARGIN -> shape = getMarginalDimensions();
                    default -> shape = getDimensions();
                }
                RenderSystem.enableScissor(shape.x, shape.y, shape.width, shape.height);
            }
            onRenderChildren(context, delta);
            if (shouldClip) {
                RenderSystem.disableScissor();
            }
        }

        if (notOpaque)
            RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    public void onRenderChildren(DrawContext context, float delta) {
        getChildren().forEach(child -> child.onRender(context, delta));
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
                    if (method.getAnnotation(CallbackHandler.class) == null)
                        error("specified callback method must have annotation: @io.github.itzispyder.improperui.script.CallbackHandler");
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
        return "Element:{children-count:%s,position:%s,dimensions:[%s,%s,%s,%s],margin:[%s,%s,%s,%s],padding:[%s,%s,%s,%s],border:[%s,%s,%s],fill:%s,shadow:[%s,%s],mouseActions:['%s','%s','%s','%s'],hoverActions:['%s','%s'],text:[%s,%s,'%s',%s],childrenAlignment:[%s,%s],backgroundImage:'%s',backgroundClip:%s,opacity:%s},".formatted(
                children.size(),
                position,
                x, y, width, height,
                marginLeft, marginRight, marginTop, marginBottom,
                paddingLeft, paddingRight, paddingTop, paddingBottom,
                borderThickness, borderRadius, borderColor,
                fillColor,
                shadowDistance, shadowColor,
                rightClickAction, leftClickAction, middleClickAction, scrollAction,
                startHoverAction, stopHoverAction,
                textScale, textAlignment, innerText, textShadow,
                childrenAlignment, gridColumns,
                backgroundImage,
                backgroundClip,
                opacity
        );
    }
}
