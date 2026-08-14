package io.github.itzispyder.improperui.render.elements;

import io.github.itzispyder.improperui.config.ConfigKey;
import io.github.itzispyder.improperui.config.PropertyCache;
import io.github.itzispyder.improperui.render.ImproperUIPanel;
import io.github.itzispyder.improperui.render.KeyHolderElement;
import io.github.itzispyder.improperui.render.constants.Visibility;
import io.github.itzispyder.improperui.util.MathUtils;
import io.github.itzispyder.improperui.util.StringUtils;
import io.github.itzispyder.improperui.util.misc.Pair;
import io.github.itzispyder.improperui.util.render.RenderUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static io.github.itzispyder.improperui.util.render.RenderUtils.*;

public class TextField extends KeyHolderElement {

    private static final int UNDO_LIMIT = 100;
    private static final int SEL_COLOR = 0xFF3B5EEB;
    private static final int CURSOR_COLOR = 0xE0FFFFFF;

    private int selectionStart, selectionEnd;
    private final Point selectedStartPoint;
    private int textY = 5, textHeight;
    private boolean selectionBlinking, selectedAll;
    private int selectionBlink;
    private final ArrayDeque<Pair<String, Integer>> undoStack = new ArrayDeque<>();
    private final ArrayDeque<Pair<String, Integer>> redoStack = new ArrayDeque<>();
    private int preferredCol = -1;
    private int dragAnchor = -1;
    private Function<Integer, Boolean> keyInterceptor;
    private Consumer<Boolean> onStateChanged;

    private List<FormattedCharSequence> cachedRows = List.of();

    public TextField(String preText, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.selectedStartPoint = new Point();
        this.innerText = preText;
        this.resetSelection();

        queueProperty("inner-text: %s".formatted(innerText));
        queueProperty("size: %s %s".formatted(this.width, this.height));
        queueProperty("border: 1 0 white");
        queueProperty("background-color: dark_gray");
        queueProperty("scrollable: true");
    }

    public TextField(int x, int y, int width, int height) {
        this("", x, y, width, height);
    }

    public TextField() {
        this("", 0, 0, 100, 15);
    }

    @Override
    public void onLoadKey(PropertyCache cache, ConfigKey key) {
        var property = cache.getProperty(key);
        if (property != null)
            innerText = property.getQuote().replace("<newline>", "\n");
    }

    @Override
    public void onSaveKey(PropertyCache cache, ConfigKey key) {
        cache.setProperty(key, "\"%s\"".formatted(innerText.replace("\n", "<newline>")), true);
    }

    private void updateTextDimensions() {
        cachedRows = mc.font.split(FormattedText.of(innerText), width - 10);
        textHeight = cachedRows.size() * 9;
    }

    private void clampScroll() {
        int minScroll = Math.min(5, height - textHeight - 5);
        int maxScroll = 5;
        textY = MathUtils.clamp(textY, minScroll, maxScroll);
    }

    public void ensureCursorVisible() {
        updateTextDimensions();
        int cursorY = selectedStartPoint.y;
        int visibleTop = -textY + 5;
        int visibleBottom = -textY + height - 14;

        if (cursorY < visibleTop) {
            textY = 5 - cursorY;
        } else if (cursorY > visibleBottom) {
            textY = height - 14 - cursorY;
        }
        clampScroll();
    }

    private int lineStart(int pos) {
        int i = Math.min(pos, innerText.length());
        while (i > 0 && innerText.charAt(i - 1) != '\n') i--;
        return i;
    }

    private int lineEnd(int pos) {
        int i = Math.min(pos, innerText.length());
        while (i < innerText.length() && innerText.charAt(i) != '\n') i++;
        return i;
    }

    private int prevWordBoundary(int pos) {
        int i = pos;
        while (i > 0 && Character.isWhitespace(innerText.charAt(i - 1))) i--;
        while (i > 0 && !Character.isWhitespace(innerText.charAt(i - 1))) i--;
        return i;
    }

    private int nextWordBoundary(int pos) {
        int i = pos;
        while (i < innerText.length() && !Character.isWhitespace(innerText.charAt(i))) i++;
        while (i < innerText.length() && Character.isWhitespace(innerText.charAt(i))) i++;
        return i;
    }

    private boolean hasRange() {
        return !selectedAll && selectionStart != selectionEnd;
    }

    private int selMin() {
        return Math.min(selectionStart, selectionEnd);
    }

    private int selMax() {
        return Math.max(selectionStart, selectionEnd);
    }

    private boolean toggleComment() {
        int from = selectedAll ? 0 : selMin();
        int to = selectedAll ? innerText.length() : selMax();
        int blockStart = lineStart(from);
        int blockEnd = lineEnd(to);
        boolean collapsed = !selectedAll && !hasRange();

        String block = innerText.substring(blockStart, blockEnd);
        String[] lines = block.split("\n", -1);

        boolean allCommented = true;
        for (String line : lines)
            if (!line.isBlank() && !line.startsWith("//")) {
                allCommented = false;
                break;
            }

        StringBuilder rebuilt = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (!line.isBlank())
                line = allCommented ? line.substring(line.startsWith("// ") ? 3 : 2) : "// " + line;
            rebuilt.append(line);
            if (i < lines.length - 1) rebuilt.append("\n");
        }

        pushUndo();
        int savedStart = selectionStart;
        innerText = innerText.substring(0, blockStart) + rebuilt + innerText.substring(blockEnd);

        if (collapsed) {
            int delta = rebuilt.length() - block.length();
            selectionStart = selectionEnd = MathUtils.clamp(savedStart + delta, blockStart, blockStart + rebuilt.length());
        } else {
            selectionStart = blockStart;
            selectionEnd = blockStart + rebuilt.length();
        }
        updateSelection();
        preferredCol = -1;
        return true;
    }

    private void deleteRange() {
        pushUndo();
        int lo = selectedAll ? 0 : selMin();
        int hi = selectedAll ? innerText.length() : selMax();
        innerText = innerText.substring(0, lo) + innerText.substring(hi);
        selectionStart = selectionEnd = lo;
        selectedAll = false;
        updateSelection();
    }

    private void pushUndo() {
        if (!undoStack.isEmpty() && undoStack.peek().left.equals(innerText)) return;
        undoStack.push(Pair.of(innerText, selectionStart));
        redoStack.clear();
        while (undoStack.size() > UNDO_LIMIT) undoStack.removeLast();
    }

    @Override
    public void onChar(char chr) {
        if (parentPanel == null || parentPanel.focused != this) return;
        if (Character.isISOControl(chr)) return;

        char closing = matchingClose(chr);

        if (closing != 0 && (hasRange() || selectedAll)) {
            wrapSelection(chr, closing);
            preferredCol = -1;
            fireChanged(true);
            return;
        }

        if (hasRange() || selectedAll) deleteRange();

        if (isClosingBracket(chr) && selectionStart < innerText.length() && innerText.charAt(selectionStart) == chr) {
            shiftRight();
            preferredCol = -1;
            fireChanged(true);
            return;
        }

        if (closing != 0) {
            final char c = closing;
            onInput(input -> insertInput(chr + String.valueOf(c)));
            shiftRight();
            preferredCol = -1;
            fireChanged(true);
            return;
        }

        onInput(input -> insertInput(String.valueOf(chr)));
        shiftRight();
        preferredCol = -1;
        fireChanged(true);
    }

    private static char matchingClose(char open) {
        return switch (open) {
            case '(' -> ')';
            case '[' -> ']';
            case '{' -> '}';
            default -> 0;
        };
    }

    private static boolean isClosingBracket(char c) {
        return c == ')' || c == ']' || c == '}';
    }

    private void wrapSelection(char open, char close) {
        int lo = selectedAll ? 0 : selMin();
        int hi = selectedAll ? innerText.length() : selMax();
        pushUndo();
        innerText = innerText.substring(0, lo) + open + innerText.substring(lo, hi) + close + innerText.substring(hi);
        selectionStart = lo + 1;
        selectionEnd = hi + 1;
        selectedAll = false;
        updateSelection();
    }

    @Override
    public void onKey(int key, int scan, boolean release) {
        super.onKey(key, scan, release);

        if (parentPanel == null || parentPanel.focused != this || release)
            return;
        if (keyInterceptor != null && keyInterceptor.apply(key))
            return;

        if (handleKeyInner(key))
            fireChanged(key == GLFW.GLFW_KEY_BACKSPACE || key == GLFW.GLFW_KEY_DELETE);
    }

    private boolean handleKeyInner(int key) {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            if (hasRange() || selectedAll) {
                selectedAll = false;
                selectionEnd = selectionStart;
                return true;
            }
            parentPanel.focused = null;
            return true;
        }

        if (parentPanel.ctrlKeyPressed) {
            switch (key) {
                case GLFW.GLFW_KEY_A -> {
                    selectedAll = true;
                    preferredCol = -1;
                    return true;
                }
                case GLFW.GLFW_KEY_C -> {
                    if (selectedAll) mc.keyboardHandler.setClipboard(innerText);
                    else if (hasRange()) mc.keyboardHandler.setClipboard(innerText.substring(selMin(), selMax()));
                    return true;
                }
                case GLFW.GLFW_KEY_X -> {
                    if (selectedAll) {
                        mc.keyboardHandler.setClipboard(innerText);
                        clear();
                    } else if (hasRange()) {
                        mc.keyboardHandler.setClipboard(innerText.substring(selMin(), selMax()));
                        deleteRange();
                    }
                    preferredCol = -1;
                    return true;
                }
                case GLFW.GLFW_KEY_V -> {
                    if (hasRange() || selectedAll) deleteRange();
                    onInput(input -> insertInput(mc.keyboardHandler.getClipboard()));
                    shiftRight();
                    preferredCol = -1;
                    return true;
                }
                case GLFW.GLFW_KEY_Z -> {
                    if (!undoStack.isEmpty()) {
                        redoStack.push(Pair.of(innerText, selectionStart));
                        Pair<String, Integer> state = undoStack.pop();
                        innerText = state.left;
                        selectionStart = selectionEnd = MathUtils.clamp(state.right, 0, innerText.length());
                        updateSelection();
                    }
                    preferredCol = -1;
                    return true;
                }
                case GLFW.GLFW_KEY_Y -> {
                    if (!redoStack.isEmpty()) {
                        undoStack.push(Pair.of(innerText, selectionStart));
                        Pair<String, Integer> state = redoStack.pop();
                        innerText = state.left;
                        selectionStart = selectionEnd = MathUtils.clamp(state.right, 0, innerText.length());
                        updateSelection();
                    }
                    preferredCol = -1;
                    return true;
                }
                case GLFW.GLFW_KEY_D -> {
                    int ls = lineStart(selectionStart);
                    int le = lineEnd(selectionStart);
                    String line = innerText.substring(ls, le);
                    pushUndo();
                    String ins = "\n" + line;
                    innerText = innerText.substring(0, le) + ins + innerText.substring(le);
                    selectionStart = selectionEnd = le + ins.length();
                    updateSelection();
                    preferredCol = -1;
                    return true;
                }
                case GLFW.GLFW_KEY_SLASH -> {
                    return toggleComment();
                }
                case GLFW.GLFW_KEY_LEFT -> {
                    selectionStart = selectionEnd = prevWordBoundary(selectionStart);
                    selectedAll = false;
                    updateSelection();
                    preferredCol = -1;
                    return true;
                }
                case GLFW.GLFW_KEY_RIGHT -> {
                    selectionStart = selectionEnd = nextWordBoundary(selectionStart);
                    selectedAll = false;
                    updateSelection();
                    preferredCol = -1;
                    return true;
                }
                case GLFW.GLFW_KEY_BACKSPACE -> {
                    if (hasRange() || selectedAll) {
                        deleteRange();
                        preferredCol = -1;
                        return true;
                    }
                    int wordStart = prevWordBoundary(selectionStart);
                    if (wordStart < selectionStart) {
                        pushUndo();
                        innerText = innerText.substring(0, wordStart) + innerText.substring(selectionStart);
                        selectionStart = selectionEnd = wordStart;
                        updateSelection();
                    }
                    preferredCol = -1;
                    return true;
                }
            }
        }

        switch (key) {
            case GLFW.GLFW_KEY_BACKSPACE -> {
                if (hasRange() || selectedAll) {
                    deleteRange();
                    preferredCol = -1;
                    return true;
                }
                if (selectionStart > 0 && selectionStart < innerText.length()) {
                    char expectedClose = matchingClose(innerText.charAt(selectionStart - 1));
                    if (expectedClose != 0 && innerText.charAt(selectionStart) == expectedClose) {
                        pushUndo();
                        innerText = innerText.substring(0, selectionStart - 1) + innerText.substring(selectionStart + 1);
                        selectionStart = selectionEnd = selectionStart - 1;
                        updateSelection();
                        preferredCol = -1;
                        return true;
                    }
                }
                onInput(cur -> selectionStart > 0 && !cur.isEmpty()
                        ? cur.substring(0, selectionStart - 1) + cur.substring(selectionStart)
                        : cur);
                shiftLeft();
                preferredCol = -1;
                return true;
            }
            case GLFW.GLFW_KEY_DELETE -> {
                if (hasRange() || selectedAll) {
                    deleteRange();
                    preferredCol = -1;
                    return true;
                }
                onInput(input -> StringUtils.insertString(innerText, selectionStart + 1, null));
                preferredCol = -1;
                return true;
            }
            case GLFW.GLFW_KEY_ENTER -> {
                if (hasRange() || selectedAll) deleteRange();
                int ls = lineStart(selectionStart);
                String before = innerText.substring(ls, selectionStart);
                int spaces = 0;
                while (spaces < before.length() && before.charAt(spaces) == ' ') spaces++;

                boolean betweenBraces = selectionStart > 0
                        && selectionStart < innerText.length()
                        && innerText.charAt(selectionStart - 1) == '{'
                        && innerText.charAt(selectionStart) == '}';

                pushUndo();
                if (betweenBraces) {
                    String inner = "\n" + " ".repeat(spaces + 4);
                    String outer = "\n" + " ".repeat(spaces);
                    innerText = innerText.substring(0, selectionStart) + inner + outer + innerText.substring(selectionStart);
                    selectionStart = selectionEnd = selectionStart + inner.length();
                } else {
                    String indent = "\n" + " ".repeat(spaces);
                    innerText = innerText.substring(0, selectionStart) + indent + innerText.substring(selectionStart);
                    selectionStart = selectionEnd = selectionStart + indent.length();
                }
                updateSelection();
                preferredCol = -1;
                return true;
            }
            case GLFW.GLFW_KEY_TAB -> {
                if (hasRange() || selectedAll) deleteRange();
                onInput(input -> insertInput("    "));
                for (int i = 0; i < 4; i++) shiftRight();
                preferredCol = -1;
                return true;
            }
            case GLFW.GLFW_KEY_LEFT -> {
                if (hasRange() || selectedAll) {
                    selectionStart = selectionEnd = selMin();
                    selectedAll = false;
                    updateSelection();
                } else shiftLeft();
                preferredCol = -1;
                return true;
            }
            case GLFW.GLFW_KEY_RIGHT -> {
                if (hasRange() || selectedAll) {
                    selectionStart = selectionEnd = selMax();
                    selectedAll = false;
                    updateSelection();
                } else shiftRight();
                preferredCol = -1;
                return true;
            }
            case GLFW.GLFW_KEY_HOME -> {
                selectionStart = selectionEnd = lineStart(selectionStart);
                selectedAll = false;
                updateSelection();
                preferredCol = -1;
                return true;
            }
            case GLFW.GLFW_KEY_END -> {
                selectionStart = selectionEnd = lineEnd(selectionStart);
                selectedAll = false;
                updateSelection();
                preferredCol = -1;
                return true;
            }
            case GLFW.GLFW_KEY_UP -> {
                navigateVertical(-1);
                return true;
            }
            case GLFW.GLFW_KEY_DOWN -> {
                navigateVertical(1);
                return true;
            }
        }
        return false;
    }

    private void navigateVertical(int dir) {
        selectedAll = false;
        int col = preferredCol >= 0 ? preferredCol : selectionStart - lineStart(selectionStart);
        preferredCol = col;

        String[] lines = innerText.split("\n", -1);
        int charPos = 0, curLine = 0;
        for (int i = 0; i < lines.length; i++) {
            if (charPos + lines[i].length() >= selectionStart) {
                curLine = i;
                break;
            }
            charPos += lines[i].length() + 1;
        }

        int targetLine = MathUtils.clamp(curLine + dir, 0, lines.length - 1);
        int targetPos = 0;
        for (int i = 0; i < targetLine; i++) targetPos += lines[i].length() + 1;
        targetPos += Math.min(col, lines[targetLine].length());
        selectionStart = selectionEnd = targetPos;
        updateSelection();
    }

    @Override
    public void onLeftClick(int mx, int my, boolean release) {
        super.onLeftClick(mx, my, release);
        if (release)
            mouseReleased(mx, my, 0);
        else
            mouseClicked(mx, my, 0);
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        selectedAll = false;

        if (button == 0 && getHitboxDimensions().contains(mouseX, mouseY)) {
            int pos = pixelToContentPos(mouseX, mouseY);
            selectionStart = selectionEnd = pos;
            dragAnchor = pos;
            preferredCol = -1;
            updateSelection();
            if (parentPanel != null) {
                parentPanel.focused = this;
                parentPanel.selected = this;
            }
            fireChanged(false);
        }
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) dragAnchor = -1;
    }

    @Override
    public void onDrag(double mouseX, double mouseY) {
        if (dragAnchor < 0) return;
        int pos = pixelToContentPos(mouseX, mouseY);
        selectionStart = dragAnchor;
        selectionEnd = pos;
        selectedAll = false;
        updateSelection();
    }

    private int pixelToContentPos(double mouseX, double mouseY) {
        List<FormattedCharSequence> rows = cachedRows.isEmpty()
                ? mc.font.split(FormattedText.of(innerText), width - 10)
                : cachedRows;
        int targetRow = MathUtils.clamp((int) ((mouseY - (getPosY() + textY)) / 9), 0, Math.max(0, rows.size() - 1));

        int pos = 0, row = 0, rowStart = 0;
        while (pos <= innerText.length() && row < targetRow) {
            int next = pos + 1;
            List<FormattedCharSequence> r = mc.font.split(FormattedText.of(innerText.substring(0, next)), width - 10);
            if (r.size() > row + 1) {
                row++;
                rowStart = next;
            }
            pos = next;
        }

        int best = rowStart;
        double bestDist = Double.MAX_VALUE;
        double targetX = mouseX - (getPosX() + 5);

        for (int p = rowStart; p <= innerText.length(); p++) {
            List<FormattedCharSequence> r = mc.font.split(FormattedText.of(innerText.substring(0, p)), width - 10);
            if (r.size() > targetRow + 1) break;
            double cx = r.isEmpty() ? 0 : mc.font.width(r.get(Math.min(targetRow, r.size() - 1)));
            double dist = Math.abs(cx - targetX);
            if (dist <= bestDist) {
                bestDist = dist;
                best = p;
            }
        }

        return MathUtils.clamp(best, 0, innerText.length());
    }

    @Override
    public void onRender(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        int x = getPosX();
        int y = getPosY();

        if (visibility == Visibility.INVISIBLE)
            return;

        context.pose().pushMatrix();
        int cx = x + width / 2;
        int cy = y + height / 2;
        context.pose().rotateAbout((float)Math.toRadians(rotateZ), cx, cy);

        if (visibility != Visibility.ONLY_CHILDREN) {
            boolean focused = parentPanel != null && parentPanel.focused == this;

            fillRoundShadow(context,
                    x + marginLeft - paddingLeft - borderThickness,
                    y + marginTop - paddingTop - borderThickness,
                    width + paddingLeft + paddingRight + borderThickness * 2,
                    height + paddingTop + paddingBottom + borderThickness * 2,
                    borderRadius,
                    shadowDistance,
                    shadowColor.getHexCustomOpacity(opacity),
                    shadowColor.getHexCustomAlpha(0)
            );
            fillRoundShadow(context,
                    x + marginLeft - paddingLeft,
                    y + marginTop - paddingTop,
                    width + paddingLeft + paddingRight,
                    height + paddingTop + paddingBottom,
                    borderRadius,
                    borderThickness,
                    focused ? borderColor.getHexCustomOpacity(opacity) : borderColor.darker().getHexCustomOpacity(opacity),
                    focused ? borderColor.getHexCustomOpacity(opacity) : borderColor.darker().getHexCustomOpacity(opacity)
            );
            fillRoundRect(context,
                    x + marginLeft - paddingLeft,
                    y + marginTop - paddingTop,
                    width + paddingLeft + paddingRight,
                    height + paddingTop + paddingBottom,
                    borderRadius,
                    focused ? fillColor.getHexCustomOpacity(opacity) : fillColor.darker().getHexCustomOpacity(opacity)
            );
            if (backgroundImage != null) {
                drawRoundTexture(context,
                        backgroundImage,
                        x + marginLeft - paddingLeft,
                        y + marginTop - paddingTop,
                        width + paddingLeft + paddingRight,
                        height + paddingTop + paddingBottom,
                        borderRadius
                );
            }
        }

        context.enableScissor(x, y, x + width, y + height);

        if (visibility != Visibility.ONLY_SELF) {
            onRenderChildren(context, mouseX, mouseY, delta);
        }

        updateTextDimensions();

        int caret = y + textY;

        // Render selection highlight BEHIND the text lines
        if (hasRange()) {
            renderRangeHighlight(context, cachedRows, x, y);
        }

        if (selectedAll) {
            for (FormattedCharSequence line : cachedRows) {
                RenderUtils.fillRect(context, x + 5, caret - 1, mc.font.width(line), 9, SEL_COLOR);
                caret += 9;
            }
            caret = y + textY;
        }

        // Render text on top
        for (FormattedCharSequence line : cachedRows) {
            context.text(mc.font, line, x + 5, caret, textColor.getHex(), false);
            caret += 9;
        }

        if (selectionBlinking && parentPanel != null && parentPanel.focused == this) {
            RenderUtils.drawVerLine(context, x + 5 + selectedStartPoint.x, y - 1 + textY + selectedStartPoint.y, 9, CURSOR_COLOR);
        }

        context.disableScissor();
        context.pose().popMatrix();
    }

    private void renderRangeHighlight(GuiGraphicsExtractor context, List<FormattedCharSequence> rows, int x, int y) {
        Point startPt = computePointFor(selMin());
        Point endPt = computePointFor(selMax());
        int startRow = startPt.y / 9;
        int endRow = endPt.y / 9;

        for (int row = startRow; row <= endRow; row++) {
            int rx0 = (row == startRow) ? startPt.x : 0;
            int rx1 = (row == endRow) ? endPt.x : (row < rows.size() ? mc.font.width(rows.get(row)) : 0);
            int ry = y + textY + (row * 9);
            RenderUtils.fillRect(context, x + 5 + rx0, ry - 1, Math.max(1, rx1 - rx0), 9, SEL_COLOR);
        }
    }

    private Point computePointFor(int pos) {
        String sub = innerText.substring(0, MathUtils.clamp(pos, 0, innerText.length()));
        List<FormattedCharSequence> lines = mc.font.split(FormattedText.of(sub), width - 10);
        if (lines == null || lines.isEmpty()) return new Point(0, 0);
        return new Point(mc.font.width(lines.get(lines.size() - 1)), (lines.size() - 1) * 9);
    }

    @Override
    public void onTick() {
        super.onTick();
        if (parentPanel != null) {
            if (parentPanel.focused != this) {
                selectionBlinking = false;
                return;
            }
            if (selectionBlink++ >= 20) selectionBlink = 0;
            if (selectionBlink % 10 == 0 && selectionBlink > 0) selectionBlinking = !selectionBlinking;
        }
    }

    public void onInput(Function<String, String> factory) {
        if (selectedAll) {
            innerText = "";
            selectedAll = false;
            resetSelection();
        }
        pushUndo();
        innerText = factory.apply(innerText);
        updateSelection();
    }

    public void shiftRight() {
        selectionStart = MathUtils.clamp(selectionStart + 1, 0, innerText.length());
        selectionEnd = selectionStart;
        updateSelection();
    }

    public void shiftLeft() {
        selectionStart = MathUtils.clamp(selectionStart - 1, 0, innerText.length());
        selectionEnd = selectionStart;
        updateSelection();
    }

    public String insertInput(String input) {
        return StringUtils.insertString(innerText, selectionStart, input);
    }

    @Override
    public void onScroll(int mx, int my, boolean up) {
        super.onScroll(mx, my, up);
        int amount = up ? 1 : -1;
        for (int i = 0; i < ImproperUIPanel.SCROLL_MULTIPLIER; i++) {
            textY += amount;
        }
        clampScroll();
    }

    public void resetSelection() {
        selectionStart = selectionEnd = 0;
        updateSelection();
    }

    public void updateSelection() {
        updateTextDimensions();
        String str = innerText.substring(0, MathUtils.clamp(selectionStart, 0, innerText.length()));
        List<FormattedCharSequence> lines = mc.font.split(FormattedText.of(str), width - 10);
        if (lines == null || lines.isEmpty()) {
            selectedStartPoint.setLocation(0, 0);
        } else {
            selectedStartPoint.x = mc.font.width(lines.get(lines.size() - 1));
            selectedStartPoint.y = (lines.size() - 1) * 9;
        }
        ensureCursorVisible();
    }

    public String getWordBeforeCursor() {
        int start = selectionStart;
        while (start > 0 && innerText.charAt(start - 1) != '\n' && !Character.isWhitespace(innerText.charAt(start - 1)))
            start--;
        return innerText.substring(start, selectionStart);
    }

    public String getCurrentLine() {
        return innerText.substring(lineStart(selectionStart), lineEnd(selectionStart));
    }

    public int getCursorColInLine() {
        return selectionStart - lineStart(selectionStart);
    }

    public int getCursorPixelX() {
        return getPosX() + 5 + selectedStartPoint.x;
    }

    public int getCursorPixelY() {
        return getPosY() + textY + selectedStartPoint.y;
    }

    public void insertCompletion(String completion) {
        int wordStart = selectionStart;
        while (wordStart > 0 && innerText.charAt(wordStart - 1) != '\n' && !Character.isWhitespace(innerText.charAt(wordStart - 1)))
            wordStart--;
        pushUndo();
        innerText = innerText.substring(0, wordStart) + completion + innerText.substring(selectionStart);
        selectionStart = selectionEnd = wordStart + completion.length();
        updateSelection();
    }

    public String[] getLines() {
        return innerText.lines().toArray(String[]::new);
    }

    public String getContent() {
        return innerText;
    }

    public void setKeyInterceptor(Function<Integer, Boolean> interceptor) {
        this.keyInterceptor = interceptor;
    }

    public void setOnContentChanged(Consumer<Boolean> callback) {
        this.onStateChanged = callback;
    }

    private void fireChanged(boolean typed) {
        if (onStateChanged != null) onStateChanged.accept(typed);
        ensureCursorVisible();
    }

    public void clear() {
        undoStack.clear();
        redoStack.clear();
        dragAnchor = -1;
        preferredCol = -1;
        cachedRows = List.of();
        resetSelection();
    }

    public void clearUndoHistory() {
        undoStack.clear();
        redoStack.clear();
    }
}