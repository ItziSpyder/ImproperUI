package io.github.itzispyder.improperui.render;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.itzispyder.improperui.render.constants.*;
import io.github.itzispyder.improperui.render.math.Color;
import io.github.itzispyder.improperui.render.math.Dimensions;
import io.github.itzispyder.improperui.script.ScriptArgs;
import io.github.itzispyder.improperui.script.ScriptReader;
import io.github.itzispyder.improperui.script.events.KeyEvent;
import io.github.itzispyder.improperui.script.events.MouseEvent;
import io.github.itzispyder.improperui.util.RenderUtils;
import io.github.itzispyder.improperui.util.StringUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class Element {

    public static final Comparator<Object> ORDER = Comparator.comparing(e -> ((Element)e).order).reversed();
    protected static final MinecraftClient mc = MinecraftClient.getInstance();

    private static int sequence = 0;
    private String id, tag;
    public final Set<String> classList;

    public int order = 0;
    public Position position;
    public int marginLeft, marginRight, marginTop, marginBottom;
    public int paddingLeft, paddingRight, paddingTop, paddingBottom;
    public int x, y, width, height;
    public Color borderColor, fillColor, shadowColor, textColor;
    public int borderThickness, borderRadius, shadowDistance;
    public String clickAction, scrollAction, keyAction;
    public Alignment textAlignment;
    public String innerText, innerTextPrefix, innerTextSuffix;
    public float textScale;
    public boolean textShadow;
    public ChildrenAlignment childrenAlignment;
    public int gridColumns;
    public Visibility visibility;
    public BackgroundClip backgroundClip;
    public Identifier backgroundImage;
    public float opacity;
    public boolean draggable, scrollable, clickThrough;
    public int rotateX, rotateY, rotateZ;
    public Element hoverStyle, focusStyle, selectStyle;


    public Element parent;
    public Panel parentPanel;
    private final List<Element> children;
    private final Map<String, Consumer<ScriptArgs>> properties;
    private final List<String> queuedProperties;

    public Element() {
        this(0, 0, 0, 0);
    }

    public Element(int x, int y, int width, int height) {
        queuedProperties = new ArrayList<>();
        children = new ArrayList<>();
        properties = new HashMap<>();
        classList = new HashSet<>();
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
        textColor = new Color(0xFFD0D0D0);
        borderRadius = borderThickness = shadowDistance = 0;

        textScale = 1.0F;
        textAlignment = Alignment.LEFT;
        textShadow = false;

        childrenAlignment = ChildrenAlignment.NONE;
        gridColumns = 1;
        visibility = Visibility.VISIBLE;
        backgroundClip = BackgroundClip.NONE;
        opacity = 1.0F;
        draggable = scrollable = clickThrough = false;
        rotateX = rotateY = rotateZ = 0;

        this.init();
    }

    public void init() {
        registerProperty("position", args -> position = args.get(0).toEnum(Position.class));

        registerProperty("x", args -> x = parseIntValue(args.get(0), false));
        registerProperty("y", args -> y = parseIntValue(args.get(0), true));
        registerProperty("width", args -> width = parseIntValue(args.get(0), false));
        registerProperty("height", args -> height = parseIntValue(args.get(0), true));
        registerProperty("pos", args -> position(parseIntValue(args.get(0), false), parseIntValue(args.get(1), true)));
        registerProperty("size", args -> size(parseIntValue(args.get(0), false), parseIntValue(args.get(1), true)));
        registerProperty("center", args -> {
            switch (args.get(0).toEnum(Axis.class)) {
                case HORIZONTAL -> {
                    int len = parent == null ? RenderUtils.width() : parent.width;
                    x = (len - this.width) / 2;
                }
                case VERTICAL -> {
                    int len = parent == null ? RenderUtils.height() : parent.height;
                    y = (len - this.height) / 2;
                }
                case BOTH -> {
                    int lenWidth = parent == null ? RenderUtils.width() : parent.width;
                    int lenHeight = parent == null ? RenderUtils.height() : parent.height;
                    x = (lenWidth - this.width) / 2;
                    y = (lenHeight - this.height) / 2;
                }
            }
        });

        registerProperty("padding-left", args -> paddingLeft = parseIntValue(args.get(0), false));
        registerProperty("padding-right", args -> paddingRight = parseIntValue(args.get(0), false));
        registerProperty("padding-top", args -> paddingTop = parseIntValue(args.get(0), true));
        registerProperty("padding-bottom", args -> paddingBottom = parseIntValue(args.get(0), true));
        registerProperty("padding", args -> padding(parseIntValue(args.get(0), false)));

        registerProperty("margin-left", args -> marginLeft = parseIntValue(args.get(0), false));
        registerProperty("margin-right", args -> marginRight = parseIntValue(args.get(0), false));
        registerProperty("margin-top", args -> marginTop = parseIntValue(args.get(0), true));
        registerProperty("margin-bottom", args -> marginBottom = parseIntValue(args.get(0), true));
        registerProperty("margin", args -> margin(parseIntValue(args.get(0), false)));

        registerProperty("border-thickness", args -> borderThickness = parseIntValue(args.get(0), false));
        registerProperty("border-radius", args -> borderRadius = parseIntValue(args.get(0), false));
        registerProperty("border-color", args -> borderColor = Color.parse(args.get(0).toString()));
        registerProperty("border", args -> border(parseIntValue(args.get(0), false), parseIntValue(args.get(1), false), Color.parse(args.get(2).toString())));

        registerProperty("fill-color", args -> fillColor = Color.parse(args.get(0).toString()));
        registerProperty("shadow-color", args -> shadowColor = Color.parse(args.get(0).toString()));
        registerProperty("shadow-distance", args -> shadowDistance = parseIntValue(args.get(0), false));
        registerProperty("shadow", args -> shadow(args.get(0).toInt(), Color.parse(args.get(0).toString())));

        registerProperty("scroll-action", args -> scrollAction = args.get(0).toString());
        registerProperty("click-action", args -> clickAction = args.get(0).toString());
        registerProperty("key-action", args -> keyAction = args.get(0).toString());
        registerProperty("on-scroll", args -> scrollAction = args.get(0).toString());
        registerProperty("on-click", args -> clickAction = args.get(0).toString());
        registerProperty("on-key", args -> keyAction = args.get(0).toString());

        registerProperty("inner-text", args -> innerText = StringUtils.color(args.getQuoteAndRemove()));
        registerProperty("inner-text-prefix", args -> innerTextPrefix = StringUtils.color(args.getQuoteAndRemove()));
        registerProperty("inner-text-suffix", args -> innerTextSuffix = StringUtils.color(args.getQuoteAndRemove()));
        registerProperty("text-scale", args -> textScale = args.get(0).toFloat());
        registerProperty("text-shadow", args -> textShadow = args.get(0).toBool());
        registerProperty("text-align", args -> textAlignment = args.get(0).toEnum(Alignment.class));
        registerProperty("text-color", args -> textColor = Color.parse(args.get(0).toString()));
        registerProperty("color", args -> textColor = Color.parse(args.get(0).toString()));

        registerProperty("children-align", args -> childrenAlignment = args.get(0).toEnum(ChildrenAlignment.class));
        registerProperty("child-align", args -> childrenAlignment = args.get(0).toEnum(ChildrenAlignment.class));
        registerProperty("display", args -> childrenAlignment = args.get(0).toEnum(ChildrenAlignment.class));
        registerProperty("grid-columns", args -> gridColumns = args.get(0).toInt());
        registerProperty("visibility", args -> visibility = args.get(0).toEnum(Visibility.class));
        registerProperty("background-clip", args -> backgroundClip = args.get(0).toEnum(BackgroundClip.class));
        registerProperty("background-color", args -> fillColor = Color.parse(args.get(0).toString()));
        registerProperty("background-image", args -> backgroundImage = new Identifier(args.get(0).toString()));
        registerProperty("opacity", args -> opacity = args.get(0).toFloat());
        registerProperty("draggable", args -> draggable = args.get(0).toBool());
        registerProperty("scrollable", args -> scrollable = args.get(0).toBool());
        registerProperty("click-through", args -> clickThrough = args.get(0).toBool());

        registerProperty("rotate-x", args -> rotateX = args.get(0).toInt());
        registerProperty("rotate-y", args -> rotateY = args.get(0).toInt());
        registerProperty("rotate-z", args -> rotateZ = args.get(0).toInt());
        registerProperty("rotate", args -> rotate(args.get(0).toInt(), args.get(1).toInt(), args.get(2).toInt()));

        registerProperty("hover", args -> hoverStyle = parsePropertiesThenSet(hoverStyle, args.getAll().toString()));
        registerProperty("select", args -> selectStyle = parsePropertiesThenSet(selectStyle, args.getAll().toString()));
        registerProperty("focus", args -> focusStyle = parsePropertiesThenSet(focusStyle, args.getAll().toString()));
        registerProperty("hovered", args -> hoverStyle = parsePropertiesThenSet(hoverStyle, args.getAll().toString()));
        registerProperty("selected", args -> selectStyle = parsePropertiesThenSet(selectStyle, args.getAll().toString()));
        registerProperty("focused", args -> focusStyle = parsePropertiesThenSet(focusStyle, args.getAll().toString()));

        registerProperty("order", args -> order = args.get(0).toInt());
        registerProperty("z-index", args -> order = args.get(0).toInt());
    }

    private int parseIntValue(ScriptArgs.Arg arg, boolean forHeight) {
        String str = arg.toString();
        if (str.matches("\\d+$"))
            return arg.toInt();

        if (str.endsWith("%")) {
            str = str.substring(0, str.length() - 1);
            double percent = Integer.parseInt(str) / 100.0;
            int len;

            if (forHeight)
                len = parent == null ? RenderUtils.height() : parent.height;
            else
                len = parent == null ? RenderUtils.width() : parent.width;
            return (int)(len * percent);
        }

        double percent = Integer.parseInt(str.substring(0, str.length() - 2)) / 100.0;
        if (str.endsWith("vw"))
            return (int)(RenderUtils.width() * percent);
        if (str.endsWith("vh"))
            return (int)(RenderUtils.height() * percent);
        return arg.toInt();
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

    public Element rotate(int x, int y, int z) {
        this.rotateX = x;
        this.rotateY = y;
        this.rotateZ = z;
        return this;
    }

    public int getPosX() {
        return (position == Position.INHERIT && parent != null) ? (parent.getPosX() + parent.marginLeft + x) : x;
    }

    public int getPosY() {
        return (position == Position.INHERIT && parent != null) ? (parent.getPosY() + parent.marginTop + y) : y;
    }

    public Dimensions getRawDimensions() {
        return new Dimensions(x, y, width, height);
    }

    public Dimensions getDimensions() {
        return new Dimensions(getPosX(), getPosY(), width, height);
    }

    public Dimensions getPaddedDimensions() {
        Dimensions dim = getDimensions();
        return dim.withX(dim.x - paddingLeft)
                .withY(dim.y - paddingTop)
                .withWidth(dim.width + paddingLeft + paddingRight)
                .withHeight(dim.height + paddingTop + paddingBottom);
    }

    public Dimensions getBorderedDimensions() {
        Dimensions dim = getPaddedDimensions();
        return dim.withX(dim.x - borderThickness)
                .withY(dim.y - borderThickness)
                .withWidth(dim.width + borderThickness * 2)
                .withHeight(dim.height + borderThickness * 2);
    }

    public Dimensions getMarginalDimensions() {
        Dimensions dim = getBorderedDimensions();
        return dim.withX(dim.x - marginLeft)
                .withY(dim.y - marginTop)
                .withWidth(dim.width + marginLeft + marginRight)
                .withHeight(dim.height + marginTop + marginBottom);
    }
    
    public Dimensions getHitboxDimensions() {
        return new Dimensions(
                getPosX() + marginLeft - paddingLeft,
                getPosY() + marginTop - paddingTop,
                width + paddingLeft + paddingRight,
                height + paddingTop + paddingBottom
        );
    }

    public void move(int deltaX, int deltaY) {
        this.x += deltaX;
        this.y += deltaY;

        this.children.stream().filter(child -> {
            return child.position == Position.ABSOLUTE;
        }).forEach(child -> {
            child.move(deltaX, deltaY);
        });
    }

    public void moveTo(int x, int y) {
        int delX = x - this.x;
        int delY = y - this.y;

        this.x = x;
        this.y = y;

        this.children.stream().filter(child -> {
            return child.position == Position.ABSOLUTE;
        }).forEach(child -> {
            child.move(delX, delY);
        });
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

    public List<Element> getChildren() {
        return new ArrayList<>(children);
    }

    public List<Element> getChildrenOrdered() {
        return new ArrayList<>(getChildren().stream().sorted(ORDER).toList());
    }

    public void clearChildren() {
        var children = getChildren();
        children.forEach(this::removeChild);
    }

    public void registerProperty(String key, Consumer<ScriptArgs> callback) {
        if (key != null && !key.isEmpty() && callback != null)
            properties.put(key.toLowerCase(), callback);
    }

    public String getId() {
        return id;
    }

    public String getTag() {
        return tag;
    }

    /**
     * This is formatted key:args
     * @param entry property entry
     */
    public void callProperty(String entry) {
        String regex = "\\s*((=>)|(->)|:|=)\\s*";
        entry = entry.trim();
        String[] split = entry.trim().split(regex);

        if (split.length < 2 || !properties.containsKey(split[0]))
            return;

        String value = entry.substring(split[0].length()).replaceFirst(regex, "");
        properties.get(split[0]).accept(new ScriptArgs(value.split("\\s+")));
    }

    public void callAttribute(String entry) {
        entry = entry.trim();
        int len = entry.length();

        if (entry.startsWith("#") && len > 1)
            id = entry.substring(1);
        else if (entry.startsWith("-") && len > 1)
            classList.add(entry.substring(1));
        else if (len > 0)
            tag = entry;
    }

    public void printAll() {
        System.out.println(this);
        children.forEach(Element::printAll);
    }

    public void queueProperty(String entry, boolean highPriority) {
        if (highPriority)
            queuedProperties.add(0, entry);
        else
            queuedProperties.add(entry);
    }

    public void queueProperty(String entry) {
        queueProperty(entry, false);
    }

    public void style() {
        order = sequence++;
        queuedProperties.forEach(this::callProperty);
        children.forEach(Element::style);

        int column, rowY, columnX;
        column = rowY = columnX = 0;

        if (childrenAlignment == ChildrenAlignment.GRID) {
            for (Element e : children) {
                e.position = Position.INHERIT;
                e.moveTo(columnX, rowY);

                Dimensions dim = e.getMarginalDimensions();
                columnX += dim.width;

                if (column++ >= gridColumns - 1) {
                    column = 0;
                    columnX = 0;
                    rowY += dim.height;
                }
            }
        }
    }

    // built-in

    public void onRender(DrawContext context, int mx, int my, float delta) {
        if (parentPanel != null) {
            if (parentPanel.selected == this && selectStyle != null) {
                selectStyle.teleport(this);
                selectStyle.onRender(context, mx, my, delta);
                return;
            }
            if (parentPanel.hovered == this && hoverStyle != null) {
                hoverStyle.teleport(this);
                hoverStyle.onRender(context, mx, my, delta);
                return;
            }
            if (parentPanel.focused == this && focusStyle != null) {
                focusStyle.teleport(this);
                focusStyle.onRender(context, mx, my, delta);
                return;
            }
        }

        int x = getPosX();
        int y = getPosY();

        if (visibility == Visibility.INVISIBLE)
            return;

        context.getMatrices().push();
        int cx = x + width / 2;
        int cy = y + height / 2;
        context.getMatrices().multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotateX), cx, cy, 0);
        context.getMatrices().multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotateY), cx, cy, 0);
        context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotateZ), cx, cy, 0);

        if (visibility != Visibility.ONLY_CHILDREN) {
            boolean notOpaque = opacity < 1.0F;
            if (notOpaque)
                RenderSystem.setShaderColor(1, 1, 1, opacity);

            RenderUtils.fillRoundShadow(context,
                    x + marginLeft - paddingLeft - borderThickness,
                    y + marginTop - paddingTop - borderThickness,
                    width + paddingLeft + paddingRight + borderThickness * 2,
                    height + paddingTop + paddingBottom + borderThickness * 2,
                    borderRadius,
                    shadowDistance,
                    shadowColor.getHex(),
                    shadowColor.getHexCustomAlpha(0)
            );
            RenderUtils.fillRoundShadow(context,
                    x + marginLeft - paddingLeft,
                    y + marginTop - paddingTop,
                    width + paddingLeft + paddingRight,
                    height + paddingTop + paddingBottom,
                    borderRadius,
                    borderThickness,
                    borderColor.getHex(),
                    borderColor.getHex()
            );
            RenderUtils.fillRoundRect(context,
                    x + marginLeft - paddingLeft,
                    y + marginTop - paddingTop,
                    width + paddingLeft + paddingRight,
                    height + paddingTop + paddingBottom,
                    borderRadius,
                    fillColor.getHex()
            );
            if (backgroundImage != null) {
                RenderUtils.drawRoundTexture(context,
                        backgroundImage,
                        x + marginLeft - paddingLeft,
                        y + marginTop - paddingTop,
                        width + paddingLeft + paddingRight,
                        height + paddingTop + paddingBottom,
                        borderRadius
                );
            }

            if (innerText != null) {
                String raw = innerText;
                if (innerTextPrefix != null)
                    raw = innerTextPrefix + raw;
                if (innerTextSuffix != null)
                    raw = raw + innerTextSuffix;
                Text text = Text.of(raw);
                x += marginLeft;
                y += marginTop;
                int textY = (int)(y + (height - (textScale * 7)) / 2);
                switch (textAlignment) {
                    case LEFT -> RenderUtils.drawDefaultScaledText(context, text, x, textY, textScale, textShadow, textColor.getHex());
                    case CENTER -> RenderUtils.drawDefaultCenteredScaledText(context, text, x + width / 2, textY, textScale, textShadow, textColor.getHex());
                    case RIGHT -> RenderUtils.drawDefaultRightScaledText(context, text, x + width, textY, textScale, textShadow, textColor.getHex());
                }
            }

            if (notOpaque)
                RenderSystem.setShaderColor(1, 1, 1, 1);
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
                context.enableScissor(shape.x, shape.y, shape.x + shape.width, shape.y + shape.height);
            }

            onRenderChildren(context, mx, my, delta);

            if (shouldClip)
                context.disableScissor();
        }

        context.getMatrices().pop();
    }

    public void onRenderChildren(DrawContext context, int mx, int my, float delta) {
        getChildren().forEach(child -> child.onRender(context, mx, my, delta));
    }

    public void onLeftClick(int mx, int my, boolean release) {

    }

    public void onRightClick(int mx, int my, boolean release) {

    }

    public void onMiddleClick(int mx, int my, boolean release) {

    }

    public void onScroll(int mx, int my, boolean up) {

    }

    public void onKey(int key, int scan, boolean release) {

    }

    public boolean pollClickable(int button, int mx, int my, boolean release) {
        if (clickThrough || parentPanel == null || visibility == Visibility.INVISIBLE)
            return false;
        if (!getHitboxDimensions().contains(mx, my))
            return false;
        if (parent != null) {
            if (parent.visibility == Visibility.ONLY_SELF)
                return false;
            if (parent.backgroundClip != BackgroundClip.NONE && !parent.getHitboxDimensions().contains(mx, my))
                return false;
        }

        if (!release) {
            parentPanel.selected = this;
            parentPanel.focused = this;
            parentPanel.cursor[0] = mx;
            parentPanel.cursor[1] = my;
        }
        if (visibility != Visibility.ONLY_CHILDREN) {
            InputType input = !release ? InputType.CLICK : InputType.RELEASE;
            parentPanel.runCallbacks(clickAction, new MouseEvent(button, 0, input, this));
        }
        return true;
    }

    public boolean pollScrollable(int mx, int my, boolean up) {
        if (clickThrough || parentPanel == null || visibility == Visibility.INVISIBLE)
            return false;
        if (!getHitboxDimensions().contains(mx, my))
            return false;
        if (parent != null) {
            if (parent.visibility == Visibility.ONLY_SELF)
                return false;
            if (parent.backgroundClip != BackgroundClip.NONE && !parent.getHitboxDimensions().contains(mx, my))
                return false;
        }

        if (visibility != Visibility.ONLY_CHILDREN) {
            int delta = 0;
            if (scrollable) {
                for (int i = 0; i < 10; i++) {
                    var dim = getMarginalDimensions();
                    if (up && children.stream().noneMatch(child -> child.getMarginalDimensions().y < dim.y))
                        break;
                    if (!up && children.stream().noneMatch(child -> child.getMarginalDimensions().heightY > dim.heightY))
                        break;
                    int dy = up ? 1 : -1;
                    delta += dy;
                    children.forEach(child -> child.move(0, dy));
                }
            }
            parentPanel.runCallbacks(scrollAction, new MouseEvent(2, delta, InputType.SCROLL, this));
        }
        return true;
    }

    public boolean pollTypeable(int key, int scan, boolean release) {
        if (clickThrough || parentPanel == null || visibility == Visibility.INVISIBLE)
            return false;
        if (parent != null && parent.visibility == Visibility.ONLY_SELF)
            return false;

        if (visibility != Visibility.ONLY_CHILDREN) {
            InputType input = !release ? InputType.CLICK : InputType.RELEASE;
            parentPanel.runCallbacks(keyAction, new KeyEvent(key, scan, input, this));
        }
        return true;
    }

    public void onTick() {
        children.forEach(Element::onTick);
    }

    protected void setParentPanel(Panel parentPanel) {
        this.parentPanel = parentPanel;
        this.children.forEach(child -> child.setParentPanel(parentPanel));
    }

    @Override
    public String toString() {
        JsonObject o = new JsonObject();
        JsonArray arr;

        o.addProperty("children-count", children.size());
        o.addProperty("position", stringify(position));

        arr = new JsonArray();
        arr.add(x);
        arr.add(y);
        arr.add(width);
        arr.add(height);
        o.add("dimensions", arr);

        arr = new JsonArray();
        arr.add(marginLeft);
        arr.add(marginRight);
        arr.add(marginTop);
        arr.add(marginBottom);
        o.add("margin", arr);

        arr = new JsonArray();
        arr.add(paddingLeft);
        arr.add(paddingRight);
        arr.add(paddingTop);
        arr.add(paddingBottom);
        o.add("padding", arr);

        arr = new JsonArray();
        arr.add(borderThickness);
        arr.add(borderRadius);
        arr.add(stringify(borderColor));
        o.add("border", arr);

        arr = new JsonArray();
        arr.add(stringify(fillColor));
        arr.add(stringify(backgroundClip));
        arr.add(stringify(backgroundImage));
        arr.add(opacity);
        o.add("background", arr);

        arr = new JsonArray();
        arr.add(shadowDistance);
        arr.add(stringify(shadowColor));
        o.add("shadow", arr);

        arr = new JsonArray();
        arr.add(clickAction);
        arr.add(scrollAction);
        arr.add(keyAction);
        o.add("callbacks", arr);

        arr = new JsonArray();
        arr.add(textScale);
        arr.add(stringify(textAlignment));
        arr.add(innerText);
        arr.add(innerTextPrefix);
        arr.add(innerTextSuffix);
        arr.add(textShadow);
        arr.add(stringify(textColor));
        o.add("text", arr);

        arr = new JsonArray();
        arr.add(stringify(childrenAlignment));
        arr.add(gridColumns);
        o.add("child-align", arr);

        arr = new JsonArray();
        arr.add(rotateX);
        arr.add(rotateY);
        arr.add(rotateZ);
        o.add("rotation", arr);

        o.addProperty("draggable", draggable);
        o.addProperty("scrollable", scrollable);
        o.addProperty("click-through", clickThrough);

        return "%s#%s[%s]%s%s".formatted(tag, id, order, classList, o.toString());
    }

    private <T> String stringify(T val, Function<T, String> func) {
        return val == null ? "null" : func.apply(val);
    }

    private <T extends Enum<?>> String stringify(T val) {
        return stringify(val, Enum::name);
    }

    private String stringify(Object val) {
        return stringify(val, Object::toString);
    }

    public void teleport(Element element) {
        moveTo(element.x, element.y);
    }

    public List<Element> collect() {
        List<Element> list = new ArrayList<>();
        for (Element child : children) {
            list.add(child);
            list.addAll(child.collect());
        }
        return list;
    }

    public List<Element> collectOrdered() {
        return new ArrayList<>(collect().stream().sorted(ORDER).toList());
    }

    private Element parsePropertiesThenSet(Element target, String excerpt) {
        if (target == this)
            return target;

        target = target != null ? target : new Element();
        target.parent = this.parent;
        target.parentPanel = this.parentPanel;

        this.queuedProperties.stream()
                .filter(s -> !s.matches("^(select|hover|focus).*$"))
                .forEach(target::queueProperty);

        ScriptReader.parse(ScriptReader.firstSection(excerpt, '{', '}')).forEach(target::queueProperty);
        target.style();

        return target;
    }
}
