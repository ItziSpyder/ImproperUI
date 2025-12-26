package io.github.itzispyder.improperui.render;

import io.github.itzispyder.improperui.render.math.Color;
import io.github.itzispyder.improperui.script.CallbackListener;
import io.github.itzispyder.improperui.script.Event;
import io.github.itzispyder.improperui.script.ScriptParser;
import io.github.itzispyder.improperui.util.ChatUtils;
import io.github.itzispyder.improperui.util.StringUtils;
import io.github.itzispyder.improperui.util.render.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImproperUIPanel extends Screen {

    public Element selected, focused, hovered;
    public boolean shiftKeyPressed, altKeyPressed, ctrlKeyPressed;
    public final int[] cursor;
    private final List<Element> children;
    private final List<CallbackListener> callbackListeners;

    private File uiScriptAsFile;
    private String uiScriptAsString;

    public ImproperUIPanel() {
        super(Text.of("Custom Scripted Panel Screen"));
        children = new ArrayList<>();
        callbackListeners = new ArrayList<>();
        cursor = new int[2];
    }

    public ImproperUIPanel(List<Element> widgets, CallbackListener... callbackListeners) {
        this();
        for (var callback : callbackListeners)
            this.registerCallback(callback);
        for (var child : widgets)
            this.addChild(child);
    }

    public ImproperUIPanel(String uiScript, CallbackListener... callbackListeners) {
        this(ScriptParser.parse(uiScript), callbackListeners);
        uiScriptAsString = uiScript;
    }

    public ImproperUIPanel(File uiScript, CallbackListener... callbackListeners) {
        this(ScriptParser.parseFile(uiScript), callbackListeners);
        uiScriptAsFile = uiScript;
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {

    }

    @Override
    public void render(DrawContext context, int mx, int my, float delta) {
        if (selected != null && selected.draggable) {
            int dx = mx - cursor[0];
            int dy = my - cursor[1];
            selected.move(dx, dy);
            selected.boundInConstraints();
            cursor[0] = mx;
            cursor[1] = my;
        }

        getChildren().forEach(child -> child.onRender(context, mx, my, delta));

        boolean foundTarget = false;
        for (var child : collectOrdered()) {
            if (!foundTarget && child.getHitboxDimensions().contains(mx, my)) {
                hovered = child;
                foundTarget = true;
            }
            if (altKeyPressed) {
                var hit = child.getHitboxDimensions();
                RenderUtils.drawBox(context, hit.x, hit.y, hit.width, hit.height, Color.RED.getHex());

                if (ctrlKeyPressed)
                    RenderUtils.drawLine(context, RenderUtils.width() / 2, RenderUtils.height() / 2, hit.x, hit.y, Color.BLUE.getHex());
                if (selected == child)
                    RenderUtils.drawRect(context, hit.x, hit.y, hit.width, hit.height, Color.RED.getHex());
                else if (focused == child)
                    RenderUtils.drawRect(context, hit.x, hit.y, hit.width, hit.height, Color.BLUE.getHex());
                else if (hovered == child)
                    RenderUtils.drawRect(context, hit.x, hit.y, hit.width, hit.height, Color.ORANGE.getHex());
            }
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        int mx = (int) click.x();
        int my = (int) click.y();
        int button = click.button();

        for (var child : collectOrdered()) {
            if (child.pollClickable(button, mx, my, false)) {
                switch (button) {
                    case 0 -> child.onLeftClick(mx, my, false);
                    case 1 -> child.onRightClick(mx, my, false);
                    case 2 -> child.onMiddleClick(mx, my, false);
                }
                break;
            }
        }
        return true;
    }

    @Override
    public boolean mouseReleased(Click click) {
        int mx = (int) click.x();
        int my = (int) click.y();
        int button = click.button();

        for (var child : collectOrdered()) {
            if (child.pollClickable(button, mx, my, true)) {
                switch (button) {
                    case 0 -> child.onLeftClick(mx, my, true);
                    case 1 -> child.onRightClick(mx, my, true);
                    case 2 -> child.onMiddleClick(mx, my, true);
                }
                break;
            }
        }
        selected = null;
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (verticalAmount == 0)
            return false;

        int mx = (int) mouseX;
        int my = (int) mouseY;

        for (var child : collectOrdered()) {
            if (child.pollScrollable(mx, my, verticalAmount > 0)) {
                child.onScroll(mx, my, true);
                break;
            }
        }
        return true;
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        int keyCode = input.key();
        int scanCode = input.scancode();

        if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT)
            this.shiftKeyPressed = true;
        else if (keyCode == GLFW.GLFW_KEY_LEFT_ALT || keyCode == GLFW.GLFW_KEY_RIGHT_ALT)
            this.altKeyPressed = true;
        else if (keyCode == GLFW.GLFW_KEY_LEFT_CONTROL || keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL)
            this.ctrlKeyPressed = true;

        super.keyPressed(input);

        if (focused != null)
            focused.onKey(keyCode, scanCode, false);
        return true;
    }

    @Override
    public boolean keyReleased(KeyInput input) {
        int keyCode = input.key();
        int scanCode = input.scancode();

        if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT)
            this.shiftKeyPressed = false;
        else if (keyCode == GLFW.GLFW_KEY_LEFT_ALT || keyCode == GLFW.GLFW_KEY_RIGHT_ALT)
            this.altKeyPressed = false;
        else if (keyCode == GLFW.GLFW_KEY_LEFT_CONTROL || keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL)
            this.ctrlKeyPressed = false;

        super.keyReleased(input);

        if (focused != null)
            focused.onKey(keyCode, scanCode, true);
        return true;
    }

    @Override
    public void tick() {
        children.forEach(Element::onTick);
    }

    @Override
    public void resize(int width, int height) {
        if (uiScriptAsFile != null)
            new ImproperUIPanel(uiScriptAsFile, callbackListeners.toArray(CallbackListener[]::new)).open();
        else if (uiScriptAsString != null)
            new ImproperUIPanel(uiScriptAsString, callbackListeners.toArray(CallbackListener[]::new)).open();
    }

    public void addChild(Element child) {
        if (child == null || child.parentPanel != null || child.parent != null || children.contains(child))
            return;
        children.add(child);
        child.setParentPanel(this);
    }

    public void removeChild(Element child) {
        if (child == null)
            return;
        child.setParentPanel(null);
        children.remove(child);
    }

    public List<Element> getChildren() {
        return new ArrayList<>(children);
    }

    public List<Element> getChildrenOrdered() {
        return new ArrayList<>(getChildren().stream()
                .sorted(Element.ORDER)
                .toList());
    }

    public void clearChildren() {
        var children = getChildren();
        children.forEach(this::removeChild);
    }

    public Element getHoveredElement(int mx, int my) {
        for (Element child : collectOrdered())
            if (child.getHitboxDimensions().contains(mx, my))
                return child;
        return null;
    }

    public void registerCallback(CallbackListener listener) {
        if (listener != null)
            callbackListeners.add(listener);
    }

    public void runCallbacks(String methodName, Event event) {
        try {
            var callbacks = new ArrayList<>(callbackListeners);
            callbacks.forEach(callback -> callback.runCallbacks(methodName, event));
        }
        catch (Exception ex) {
            ChatUtils.sendMessage(StringUtils.color("&c" + ex.getMessage()));
        }
    }

    public void printAll() {
        children.forEach(Element::printAll);
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
        return new ArrayList<>(collect().stream().sorted(Element.ORDER).toList());
    }

    public List<Element> collectById(String id) {
        List<Element> list = new ArrayList<>();
        for (Element child : collect())
            if (child.getId().equals(id))
                list.add(child);
        return list;
    }

    public Element collectFirstById(String id) {
        for (Element child : collect())
            if (child.getId().equals(id))
                return child;
        return null;
    }

    public List<Element> collectByClassAttribute(String classAttribute) {
        List<Element> list = new ArrayList<>();
        for (Element child : collect())
            if (child.classList.contains(classAttribute))
                list.add(child);
        return list;
    }

    public Element collectFirstByClassAttribute(String classAttribute) {
        for (Element child : collect())
            if (child.classList.contains(classAttribute))
                return child;
        return null;
    }

    public void open() {
        var mc = MinecraftClient.getInstance();
        if (mc.currentScreen != this)
            mc.execute(() -> mc.setScreen(this));
    }
}
