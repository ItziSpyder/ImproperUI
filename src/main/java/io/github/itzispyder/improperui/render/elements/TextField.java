package io.github.itzispyder.improperui.render.elements;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.itzispyder.improperui.render.Element;
import io.github.itzispyder.improperui.render.constants.BackgroundClip;
import io.github.itzispyder.improperui.render.constants.Visibility;
import io.github.itzispyder.improperui.render.math.Dimensions;
import io.github.itzispyder.improperui.util.MathUtils;
import io.github.itzispyder.improperui.util.RenderUtils;
import io.github.itzispyder.improperui.util.StringUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.RotationAxis;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.Function;

import static io.github.itzispyder.improperui.util.RenderUtils.*;

public class TextField extends Element {

    public static final int CHAR_LEN = 6;
    private int limitW, limitH;
    private HistoryQueue editHistory;
    private CharacterElement[][] chars;
    private Point selStart, selEnd;
    private int cursor, selectionBlink;
    private boolean selectionBlinking, mouseDown;

    public TextField(String innerText, int x, int y, int w, int h) {
        super(x, y, Math.max(w, 50), Math.max(h, 18));
        int addW = w % CHAR_LEN == 0 ? 0 : 1;
        int addH = h % CHAR_LEN == 0 ? 0 : 1;
        this.limitW = (int)(Math.floor(w / (double)CHAR_LEN) + addW) - 2;
        this.limitH = (int)(Math.floor(h / (double)CHAR_LEN) + addH) - 2;
        this.width = (limitW + 2) * CHAR_LEN;
        this.height = (limitH + 2) * CHAR_LEN;
        this.innerText = innerText;
        this.chars = new CharacterElement[limitW][limitH];
        this.updateInnerText();
        this.selStart = new Point();
        this.selEnd = new Point();
        this.cursor = -1;
        this.mouseDown = false;
        this.editHistory = new HistoryQueue(100);

        queueProperty("inner-text: %s".formatted(innerText));
        queueProperty("size: %s %s".formatted(this.width, this.height));
        queueProperty("border: 1 0 white");
        queueProperty("background-color: dark_gray");
    }

    public TextField() {
        this("", 0, 0, 100, 18);
    }

    @Override
    public void style() {
        super.style();
        int addW = width % CHAR_LEN == 0 ? 0 : 1;
        int addH = height % CHAR_LEN == 0 ? 0 : 1;
        this.limitW = (int)(Math.floor(width / (double)CHAR_LEN) + addW) - 2;
        this.limitH = (int)(Math.floor(height / (double)CHAR_LEN) + addH) - 2;
        this.width = (limitW + 2) * CHAR_LEN;
        this.height = (limitH + 2) * CHAR_LEN;
        this.chars = new CharacterElement[limitW][limitH];
        this.updateInnerText();
        this.selStart = new Point();
        this.selEnd = new Point();
        this.cursor = -1;
        this.mouseDown = false;
        this.editHistory = new HistoryQueue(100);
    }

    @Override
    public void onRender(DrawContext context, int mx, int my, float delta) {
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
            boolean focused = parentPanel != null && parentPanel.focused == this;
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
                    focused ? borderColor.getHex() : borderColor.darker().getHex(),
                    focused ? borderColor.getHex() : borderColor.darker().getHex()
            );
            RenderUtils.fillRoundRect(context,
                    x + marginLeft - paddingLeft,
                    y + marginTop - paddingTop,
                    width + paddingLeft + paddingRight,
                    height + paddingTop + paddingBottom,
                    borderRadius,
                    focused ? fillColor.getHex() : fillColor.darker().getHex()
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

        if (mouseDown)
            pollMouseSelection(mx, my);
    }

    @Override
    public void onKey(int key, int scan, boolean release) {
        if (parentPanel != null && !release) {
            String typed = GLFW.glfwGetKeyName(key, scan);

            if (key == GLFW.GLFW_KEY_ESCAPE) {
                if (isSelecting())
                    updateCursor();
                else
                    parentPanel.focused = null;
            }
            else if (key == GLFW.GLFW_KEY_A && parentPanel.ctrlKeyPressed) {
                cursor = -1;
                selStart.setLocation(0, 0);
                selEnd.setLocation(limitW, limitH);
            }
            else if (key == GLFW.GLFW_KEY_BACKSPACE) {
                editHistory.push();
                if (isSelecting()) {
                    deleteSelectedText();
                    setCursor(cursor - 1);
                    return;
                }
                onInput(input -> StringUtils.insertString(innerText, cursor + 1, null), false);
            }
            else if (key == GLFW.GLFW_KEY_DELETE) {
                editHistory.push();
                if (isSelecting()) {
                    deleteSelectedText();
                    setCursor(cursor - 1);
                    return;
                }
                onInput(input -> StringUtils.insertString(innerText, cursor + 2, null), false);
                setCursor(cursor + 1);
            }
            else if (key == GLFW.GLFW_KEY_SPACE) {
                editHistory.push();
                onInput(input -> StringUtils.insertString(innerText, cursor + 1, " "), true);
            }
            else if (key == GLFW.GLFW_KEY_V && parentPanel.ctrlKeyPressed) {
                editHistory.push();
                String s = mc.keyboard.getClipboard().replace('\n', ' ');
                boolean sel = isSelecting();
                int len = s.length();

                if (sel) {
                    for (int i = len - 1; i >= 0; i--) {
                        char c = s.charAt(i);
                        onInput(input -> {
                            setCursor(cursor - 1);
                            return StringUtils.insertString(innerText, cursor + 1, String.valueOf(c));
                        }, true);
                    }
                }
                else {
                    for (char c : s.toCharArray())
                        onInput(input -> StringUtils.insertString(innerText, cursor + 1, String.valueOf(c)), true);
                }
            }
            else if (key == GLFW.GLFW_KEY_C && parentPanel.ctrlKeyPressed) {
                mc.keyboard.setClipboard(getSelectedText());
            }
            else if (key == GLFW.GLFW_KEY_Z && parentPanel.ctrlKeyPressed) {
                editHistory.revertLastEdit();
            }
            else if (key == GLFW.GLFW_KEY_LEFT) {
                setCursor(cursor - 1);
                if (parentPanel.shiftKeyPressed)
                    selEnd.setLocation(getCursor());
                else
                    updateCursor();
            }
            else if (key == GLFW.GLFW_KEY_RIGHT) {
                setCursor(cursor + 1);
                if (parentPanel.shiftKeyPressed)
                    selEnd.setLocation(getCursor());
                else
                    updateCursor();
            }
            else if (key == GLFW.GLFW_KEY_UP) {
                var c = getCursor();
                setCursor(c.x - 1, c.y - 1);
                if (parentPanel.shiftKeyPressed)
                    selEnd.setLocation(getCursor());
                else
                    updateCursor();
            }
            else if (key == GLFW.GLFW_KEY_DOWN) {
                var c = getCursor();
                setCursor(c.x - 1, c.y + 1);
                if (parentPanel.shiftKeyPressed)
                    selEnd.setLocation(getCursor());
                else
                    updateCursor();
            }
            else if (typed != null) {
                editHistory.push();
                String s = parentPanel.shiftKeyPressed ? StringUtils.keyPressWithShift(typed) : typed;
                boolean sel = isSelecting();

                onInput(input -> {
                    if (sel)
                        setCursor(cursor - 1);
                    return StringUtils.insertString(innerText, cursor + 1, s);
                }, true);
            }
        }
    }

    public void onInput(Function<String, String> factory, boolean append) {
        if (cursor + 1 >= limitW * limitH && append)
            return;

        deleteSelectedText();
        String typed = factory.apply(innerText);
        if (typed.length() > limitW * limitH && append)
            return;

        innerText = typed;
        updateInnerText();
        cursor += append ? 1 : -1;
        cursor = MathUtils.clamp(cursor, -1, limitW * limitH - 1);
        updateCursor();
    }


    @Override
    public void onLeftClick(int mx, int my, boolean release) {
        super.onLeftClick(mx, my, release);
        this.mouseDown = !release;
    }

    private void pollMouseSelection(int mx, int my) {
        CharacterElement ch = null;
        for (int x = 0; x < limitW; x++) {
            for (int y = 0; y < limitH; y++) {
                var c = chars[x][y];
                if (c != null && c.getHitboxDimensions().contains(mx, my)) {
                    ch = c;
                    break;
                }
            }
        }
        if (ch != null) {
            selEnd.setLocation(ch.idx + (selEnd.x >= selStart.x ? 1 : 0), ch.idy);
            setCursor(selEnd.x - 1, selEnd.y);
        }
    }

    public String getInnerText() {
        return innerText;
    }

    public void setInnerText(String innerText) {
        this.innerText = innerText;
        updateInnerText();
    }

    private void updateInnerText() {
        this.clearChars();

        int len = innerText.length();
        int cx = 0;
        int cy = 0;

        for (int i = 0; i < len; i++) {
            char c = innerText.charAt(i);

            if (cx < limitW) {
                var che = new CharacterElement(i, cx, cy, String.valueOf(c), cx * CHAR_LEN + CHAR_LEN, cy * CHAR_LEN + CHAR_LEN);
                chars[cx][cy] = che;
                this.addChild(che);
                cx++;

                if (cx >= limitW) {
                    cx = 0;
                    cy++;
                }
            }
        }
    }

    public Point getCursor() {
        int cx = 0;
        int cy = 0;

        for (int i = 0; i < (cursor + 1); i++) {
            if (cx < limitW) {
                cx++;

                if (cx >= limitW) {
                    cx = 0;
                    cy++;
                }
            }
        }
        return new Point(cx, cy);
    }

    public String getSelectedText() {
        StringBuilder b = new StringBuilder();
        for (int y = 0; y < limitH; y++) {
            for (int x = 0; x < limitW; x++) {
                var c = chars[x][y];
                if (c != null && c.isSelected())
                    b.append(c.ch);
            }
        }
        return b.toString();
    }

    public void deleteSelectedText() {
        if (selStart.equals(selEnd))
            return;

        int x, y, s1, s2;
        x = MathUtils.clamp(selStart.x, 0, limitW);
        y = MathUtils.clamp(selStart.y, 0, limitH);
        s1 = MathUtils.clamp(limitW * y + x, 0, innerText.length());
        x = MathUtils.clamp(selEnd.x, 0, limitW);
        y = MathUtils.clamp(selEnd.y, 0, limitH);
        s2 = MathUtils.clamp(limitW * y + x, 0, innerText.length());

        if (s1 == s2)
            return;

        this.innerText = innerText.substring(0, Math.min(s1, s2)) + innerText.substring(Math.max(s1, s2));
        updateInnerText();
        this.setCursor(Math.min(s1, s2));
        updateCursor();
    }

    public boolean isSelecting() {
        return !selStart.equals(selEnd);
    }

    public void setCursor(int x, int y) {
        x = MathUtils.clamp(x, 0, limitW);
        y = MathUtils.clamp(y, 0, limitH);
        this.setCursor(limitW * y + x);
    }

    public void setCursor(int cursor) {
        this.cursor = MathUtils.clamp(cursor, -1, innerText.length() - 1);
    }

    public CharacterElement getCursorChar() {
        var c = getCursor();
        return chars[c.x][c.y];
    }

    private void updateCursor() {
        this.selStart.setLocation(getCursor());
        this.selEnd.setLocation(selStart);
    }

    private void clearChars() {
        for (int x = 0; x < limitW; x++) {
            for (int y = 0; y < limitH; y++) {
                chars[x][y] = null;
            }
        }
        this.clearChildren();
    }

    @Override
    public void onTick() {
        super.onTick();

        if (parentPanel != null) {
            if (parentPanel.focused != this) {
                selectionBlinking = false;
                return;
            }

            if (selectionBlink++ >= 20) {
                selectionBlink = 0;
            }
            if (selectionBlink % 10 == 0 && selectionBlink > 0) {
                selectionBlinking = !selectionBlinking;
            }
        }
    }

    public class CharacterElement extends Element {
        private final String ch;
        public final int idc, idx, idy;

        public CharacterElement(int idc, int idx, int idy, String ch, int x, int y) {
            super(x, y, CHAR_LEN, CHAR_LEN);
            this.ch = ch;
            this.idc = idc;
            this.idx = idx;
            this.idy = idy;
        }

        @Override
        public void onRender(DrawContext context, int mx, int my, float delta) {
            int x = getPosX();
            int y = getPosY();

            boolean selected = this.isSelected();
            if (selected) {
                fillRect(context, x, y, width, height, 0xD000B7FF);
            }

            if (TextField.this.parentPanel != null) {
                if (TextField.this.parentPanel.altKeyPressed) {
                    int color = 0xFFFFFFFF;
                    if (idx == selStart.x && idy == selStart.y)
                        color = 0xFF00FF00;
                    if (idx == selEnd.x && idy == selEnd.y)
                        color = 0xFFFF0000;
                    drawBox(context, x, y, width, height, color);
                }
                int color = TextField.this.parentPanel.focused == TextField.this ?
                        (selected ? 0xFF000000 : TextField.this.textColor.brighter().brighter().getHex()) :
                        TextField.this.textColor.getHex();
                drawDefaultCode(context, ch, x, y, false, color);
            }

            if (selectionBlinking && cursor == idc) {
                int tx = x + width;
                int ty = y - 2;
                drawVerLine(context, tx, ty, height + 2, 0xFFFFFFFF);
            }
        }

        @Override
        public void onLeftClick(int mx, int my, boolean release) {
            if (release)
                return;
            setCursor(idx, idy);
            updateCursor();
            if (TextField.this.parentPanel != null) {
                TextField.this.parentPanel.focused = TextField.this;
                TextField.this.parentPanel.selected = TextField.this;
            }
        }

        public boolean isSelected() {
            int len = TextField.this.innerText == null ? 0 : TextField.this.innerText.length();
            int s1 = MathUtils.clamp(limitW * selStart.y + selStart.x, 0, len);
            int s2 = MathUtils.clamp(limitW * selEnd.y + selEnd.x, 0, len);
            return idc >= Math.min(s1, s2) && idc < Math.max(s1, s2);
        }
    }

    public class HistoryQueue extends ArrayList<String> {
        private final int limit;

        public HistoryQueue(int limit) {
            this.limit = limit;
        }

        public void pop() {
            if (!this.isEmpty())
                this.remove(this.size() - 1);
        }

        public void push() {
            if (innerText.isEmpty())
                return;
            this.add(innerText);
            if (size() > limit)
                this.remove(0);
        }

        public String peek() {
            if (this.isEmpty())
                return null;
            return this.get(this.size() - 1);
        }

        public void revertLastEdit() {
            String peek = this.peek();
            if (peek == null)
                return;

            innerText = peek;
            updateInnerText();
            setCursor(innerText.length() - 1);
            updateCursor();

            pop();
        }
    }
}
