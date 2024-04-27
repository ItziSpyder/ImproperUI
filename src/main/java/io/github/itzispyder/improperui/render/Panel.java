package io.github.itzispyder.improperui.render;

import io.github.itzispyder.improperui.script.CallbackListener;
import io.github.itzispyder.improperui.script.Event;
import io.github.itzispyder.improperui.script.ScriptParser;
import io.github.itzispyder.improperui.util.ChatUtils;
import io.github.itzispyder.improperui.util.RenderUtils;
import io.github.itzispyder.improperui.util.StringUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Panel extends Screen {

    public Element selected, focused, hovered;
    public boolean shiftKeyPressed, altKeyPressed, ctrlKeyPressed;
    private final List<Element> children;
    private final List<CallbackListener> callbackListeners;
    private final int[] cursor;
    private String scriptPath;

    public Panel() {
        super(Text.of("Custom Scripted Panel Screen"));
        children = new ArrayList<>();
        callbackListeners = new ArrayList<>();
        cursor = new int[2];
    }

    public Panel(String scriptPath) {
        this();
        this.scriptPath = scriptPath;
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (selected != null && selected.draggable) {
            int dx = mouseX - cursor[0];
            int dy = mouseY - cursor[1];
            selected.move(dx, dy);
            cursor[0] = mouseX;
            cursor[1] = mouseY;
        }

        hovered = getHoveredElement(mouseX, mouseY);
        getChildren().forEach(child -> child.onRender(context, delta));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Element child : getChildrenOrdered()) {
            if (child.getPaddedDimensions().contains(mouseX, mouseY)) {
                selected = child;
                focused = child;
                hovered = child;
                cursor[0] = (int)mouseX;
                cursor[1] = (int)mouseY;

                switch (button) {
                    case 0 -> child.onLeftClick(false);
                    case 1 -> child.onRightClick(false);
                    case 2 -> child.onMiddleClick(false);
                }
                break;
            }
        }
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);
        selected = null;

        for (Element child : getChildrenOrdered()) {
            if (child.getPaddedDimensions().contains(mouseX, mouseY)) {
                selected = child;
                focused = child;
                hovered = child;
                cursor[0] = (int)mouseX;
                cursor[1] = (int)mouseY;

                switch (button) {
                    case 0 -> child.onLeftClick(true);
                    case 1 -> child.onRightClick(true);
                    case 2 -> child.onMiddleClick(true);
                }
                break;
            }
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);

        if (verticalAmount == 0)
            return false;

        for (Element child : getChildrenOrdered()) {
            if (child.getPaddedDimensions().contains(mouseX, mouseY)) {
                child.onScroll(verticalAmount > 0);
                break;
            }
        }
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT)
            this.shiftKeyPressed = true;
        else if (keyCode == GLFW.GLFW_KEY_LEFT_ALT || keyCode == GLFW.GLFW_KEY_RIGHT_ALT)
            this.altKeyPressed = true;
        else if (keyCode == GLFW.GLFW_KEY_LEFT_CONTROL || keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL)
            this.ctrlKeyPressed = true;

        super.keyPressed(keyCode, scanCode, modifiers);

        var c = RenderUtils.getCursor();
        int cx = c.x;
        int cy = c.y;
        for (Element child : getChildrenOrdered()) {
            if (child.getPaddedDimensions().contains(cx, cy)) {
                child.onKey(keyCode, scanCode, false);
                break;
            }
        }
        return true;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT)
            this.shiftKeyPressed = false;
        else if (keyCode == GLFW.GLFW_KEY_LEFT_ALT || keyCode == GLFW.GLFW_KEY_RIGHT_ALT)
            this.altKeyPressed = false;
        else if (keyCode == GLFW.GLFW_KEY_LEFT_CONTROL || keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL)
            this.ctrlKeyPressed = false;

        super.keyReleased(keyCode, scanCode, modifiers);

        var c = RenderUtils.getCursor();
        int cx = c.x;
        int cy = c.y;
        for (Element child : getChildrenOrdered()) {
            if (child.getPaddedDimensions().contains(cx, cy)) {
                child.onKey(keyCode, scanCode, true);
                break;
            }
        }
        return true;
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        if (scriptPath != null)
            ScriptParser.run(new File(scriptPath));
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
                .sorted(Comparator.comparing(e -> ((Element)e).order).reversed())
                .toList());
    }

    public void clearChildren() {
        var children = getChildren();
        children.forEach(this::removeChild);
    }

    public Element getHoveredElement(int mx, int my) {
        for (Element child : getChildrenOrdered())
            if (child.getPaddedDimensions().contains(mx, my))
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
}
