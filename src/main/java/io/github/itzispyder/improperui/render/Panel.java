package io.github.itzispyder.improperui.render;

import io.github.itzispyder.improperui.util.RenderUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Panel extends Screen {

    public Element selected, focused, hovered;
    public boolean shiftKeyPressed, altKeyPressed, ctrlKeyPressed;
    private final List<Element> children;
    private final int[] cursor;

    public Panel() {
        super(Text.of("Custom Scripted Panel Screen"));
        children = new ArrayList<>();
        cursor = new int[2];
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
            selected.boundIn(RenderUtils.width(), RenderUtils.height());
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
                    case 0 -> child.onLeftClick();
                    case 1 -> child.onRightClick();
                    case 2 -> child.onMiddleClick();
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
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);

        for (Element child : getChildrenOrdered()) {
            if (child.getPaddedDimensions().contains(mouseX, mouseY)) {
                child.onScroll();
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
        return true;
    }

    public void addChild(Element child) {
        if (child == null || child.parentPanel != null || child.parent != null || children.contains(child))
            return;
        children.add(child);
        child.parentPanel = this;
    }

    public void removeChild(Element child) {
        if (child == null)
            return;
        child.parentPanel = null;
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
}
