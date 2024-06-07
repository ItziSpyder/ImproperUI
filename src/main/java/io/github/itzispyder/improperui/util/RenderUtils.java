package io.github.itzispyder.improperui.util;

import io.github.itzispyder.improperui.client.ImproperUIClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

import static com.mojang.blaze3d.systems.RenderSystem.*;

public final class RenderUtils {

    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final ImproperUIClient system = ImproperUIClient.getInstance();

    // fill

    public static void fillRect(DrawContext context, int x, int y, int w, int h, int color) {
        BufferBuilder buf = getTessellator().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        Matrix4f mat = context.getMatrices().peek().getPositionMatrix();

        buf.vertex(mat, (float)x, (float)y, 0).color(color);
        buf.vertex(mat, (float)(x + w), (float)y, 0).color(color);
        buf.vertex(mat, (float)(x + w), (float)(y + h), 0).color(color);
        buf.vertex(mat, (float)x, (float)(y + h), 0).color(color);

        beginRendering();
        BufferRenderer.drawWithGlobalProgram(buf.end());
        finishRendering();
    }

    public static void fillArc(DrawContext context, int cX, int cY, int radius, int start, int end, int color) {
        BufferBuilder buf = getTessellator().begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        Matrix4f mat = context.getMatrices().peek().getPositionMatrix();

        buf.vertex(mat, (float)cX, (float)cY, 0).color(color);

        for (int i = start - 90; i <= end - 90; i ++) {
            double angle = Math.toRadians(i);
            float x = (float)(Math.cos(angle) * radius) + cX;
            float y = (float)(Math.sin(angle) * radius) + cY;
            buf.vertex(mat, x, y, 0).color(color);
        }

        beginRendering();
        drawBuffer(buf);
        finishRendering();
    }

    public static void fillCircle(DrawContext context, int cX, int cY, int radius, int color) {
        fillArc(context, cX, cY, radius, 0, 360, color);
    }

    public static void fillAnnulusArc(DrawContext context, int cx, int cy, int radius, int start, int end, int thickness, int color) {
        BufferBuilder buf = getTessellator().begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        Matrix4f mat = context.getMatrices().peek().getPositionMatrix();

        for (int i = start - 90; i <= end - 90; i ++) {
            float angle = (float)Math.toRadians(i);
            float cos = (float)Math.cos(angle);
            float sin = (float)Math.sin(angle);
            float x1 = cx + cos * radius;
            float y1 = cy + sin * radius;
            float x2 = cx + cos * (radius + thickness);
            float y2 = cy + sin * (radius + thickness);
            buf.vertex(mat, x1, y1, 0).color(color);
            buf.vertex(mat, x2, y2, 0).color(color);
        }

        beginRendering();
        drawBuffer(buf);
        finishRendering();
    }

    public static void fillAnnulus(DrawContext context, int cx, int cy, int radius, int thickness, int color) {
        fillAnnulusArc(context, cx, cy, radius, 0, 360, thickness, color);
    }

    public static void fillRoundRect(DrawContext context, int x, int y, int w, int h, int r, int color) {
        r = MathUtils.clamp(r, 0, Math.min(w, h) / 2);

        BufferBuilder buf = getTessellator().begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);;
        Matrix4f mat = context.getMatrices().peek().getPositionMatrix();

        buf.vertex(mat, x + w / 2F, y + h / 2F, 0).color(color);

        int[][] corners = {
                { x + w - r, y + r },
                { x + w - r, y + h - r},
                { x + r, y + h - r },
                { x + r, y + r }
        };

        for (int corner = 0; corner < 4; corner++) {
            int cornerStart = (corner - 1) * 90;
            int cornerEnd = cornerStart + 90;
            for (int i = cornerStart; i <= cornerEnd; i += 10) {
                float angle = (float)Math.toRadians(i);
                float rx = corners[corner][0] + (float)(Math.cos(angle) * r);
                float ry = corners[corner][1] + (float)(Math.sin(angle) * r);
                buf.vertex(mat, rx, ry, 0).color(color);
            }
        }

        buf.vertex(mat, corners[0][0], y, 0).color(color); // connect last to first vertex

        beginRendering();
        drawBuffer(buf);
        finishRendering();
    }

    public static void fillRoundShadow(DrawContext context, int x, int y, int w, int h, int r, int thickness, int innerColor, int outerColor) {
        r = MathUtils.clamp(r, 0, Math.min(w, h) / 2);

        BufferBuilder buf = getTessellator().begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);;
        Matrix4f mat = context.getMatrices().peek().getPositionMatrix();

        int[][] corners = {
                { x + w - r, y + r },
                { x + w - r, y + h - r},
                { x + r, y + h - r },
                { x + r, y + r }
        };

        for (int corner = 0; corner < 4; corner++) {
            int cornerStart = (corner - 1) * 90;
            int cornerEnd = cornerStart + 90;
            for (int i = cornerStart; i <= cornerEnd; i += 10) {
                float angle = (float)Math.toRadians(i);
                float rx1 = corners[corner][0] + (float)(Math.cos(angle) * r);
                float ry1 = corners[corner][1] + (float)(Math.sin(angle) * r);
                float rx2 = corners[corner][0] + (float)(Math.cos(angle) * (r + thickness));
                float ry2 = corners[corner][1] + (float)(Math.sin(angle) * (r + thickness));
                buf.vertex(mat, rx1, ry1, 0).color(innerColor);
                buf.vertex(mat, rx2, ry2, 0).color(outerColor);
            }
        }

        buf.vertex(mat, corners[0][0], y, 0).color(innerColor); // connect last to first vertex
        buf.vertex(mat, corners[0][0], y - thickness, 0).color(outerColor); // connect last to first vertex

        beginRendering();
        drawBuffer(buf);
        finishRendering();
    }

    public static void fillRoundTabTop(DrawContext context, int x, int y, int w, int h, int r, int color) {
        r = MathUtils.clamp(r, 0, Math.min(w, h) / 2);

        BufferBuilder buf = getTessellator().begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);;
        Matrix4f mat = context.getMatrices().peek().getPositionMatrix();

        buf.vertex(mat, x + w / 2F, y + h / 2F, 0).color(color);

        int[][] corners = {
                { x + r, y + r },
                { x + w - r, y + r }
        };

        for (int corner = 0; corner < 2; corner++) {
            int cornerStart = (corner - 2) * 90;
            int cornerEnd = cornerStart + 90;
            for (int i = cornerStart; i <= cornerEnd; i += 10) {
                float angle = (float)Math.toRadians(i);
                float rx = corners[corner][0] + (float)(Math.cos(angle) * r);
                float ry = corners[corner][1] + (float)(Math.sin(angle) * r);
                buf.vertex(mat, rx, ry, 0).color(color);
            }
        }

        buf.vertex(mat, x + w, y + h, 0).color(color);
        buf.vertex(mat, x, y + h, 0).color(color);
        buf.vertex(mat, x, corners[0][1], 0).color(color); // connect last to first vertex

        beginRendering();
        drawBuffer(buf);
        finishRendering();
    }

    public static void fillRoundTabBottom(DrawContext context, int x, int y, int w, int h, int r, int color) {
        r = MathUtils.clamp(r, 0, Math.min(w, h) / 2);

        BufferBuilder buf = getTessellator().begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);;
        Matrix4f mat = context.getMatrices().peek().getPositionMatrix();

        buf.vertex(mat, x + w / 2F, y + h / 2F, 0).color(color);

        int[][] corners = {
                { x + w - r, y + h - r},
                { x + r, y + h - r }
        };

        for (int corner = 0; corner < 2; corner++) {
            int cornerStart = corner * 90;
            int cornerEnd = cornerStart + 90;
            for (int i = cornerStart; i <= cornerEnd; i += 10) {
                float angle = (float)Math.toRadians(i);
                float rx = corners[corner][0] + (float)(Math.cos(angle) * r);
                float ry = corners[corner][1] + (float)(Math.sin(angle) * r);
                buf.vertex(mat, rx, ry, 0).color(color);
            }
        }

        buf.vertex(mat, x, y, 0).color(color);
        buf.vertex(mat, x + w, y, 0).color(color);
        buf.vertex(mat, x + w, corners[0][1], 0).color(color); // connect last to first vertex

        beginRendering();
        drawBuffer(buf);
        finishRendering();
    }

    public static void fillRoundHoriLine(DrawContext context, int x, int y, int length, int thickness, int color) {
        fillRoundRect(context, x, y, length, thickness, thickness / 2, color);
    }

    public static void fillRoundVertLine(DrawContext context, int x, int y, int length, int thickness, int color) {
        fillRoundRect(context, x, y, thickness, length, thickness / 2, color);
    }
    
    // draw

    public static void drawRect(DrawContext context, int x, int y, int w, int h, int color) {
        drawHorLine(context, x, y, w, color);
        drawVerLine(context, x, y + 1, h - 2, color);
        drawVerLine(context, x + w - 1, y + 1, h - 2, color);
        drawHorLine(context, x, y + h - 1, w, color);
    }

    public static void drawBox(DrawContext context, int x, int y, int w, int h, int color) {
        drawLine(context, x, y, x + w, y, color);
        drawLine(context, x, y + h, x + w, y + h, color);
        drawLine(context, x, y, x, y + h, color);
        drawLine(context, x + w, y, x + w, y + h, color);
    }

    public static void drawHorLine(DrawContext context, int x, int y, int length, int color) {
        fillRect(context, x, y, length, 1, color);
    }

    public static void drawVerLine(DrawContext context, int x, int y, int length, int color) {
        fillRect(context, x, y, 1, length, color);
    }

    public static void drawLine(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        BufferBuilder buf = getTessellator().begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);;
        Matrix4f mat = context.getMatrices().peek().getPositionMatrix();

        buf.vertex(mat, (float)x1, (float)y1, 0).color(color);
        buf.vertex(mat, (float)x2, (float)y2, 0).color(color);

        beginRendering();
        drawBuffer(buf);
        finishRendering();
    }

    public static void drawArc(DrawContext context, int cX, int cY, int radius, int start, int end, int color) {
        BufferBuilder buf = getTessellator().begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);;
        Matrix4f mat = context.getMatrices().peek().getPositionMatrix();

        for (int i = start - 90; i <= end - 90; i++) {
            double angle = Math.toRadians(i);
            float x = (float)(Math.cos(angle) * radius) + cX;
            float y = (float)(Math.sin(angle) * radius) + cY;
            buf.vertex(mat, x, y, 0).color(color);
        }

        beginRendering();
        drawBuffer(buf);
        finishRendering();
    }

    public static void drawCircle(DrawContext context, int cX, int cY, int radius, int color) {
        drawArc(context, cX, cY, radius, 0, 360, color);
    }

    public static void drawRoundRect(DrawContext context, int x, int y, int w, int h, int r, int color) {
        r = MathUtils.clamp(r, 0, Math.min(w, h) / 2);

        BufferBuilder buf = getTessellator().begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
        Matrix4f mat = context.getMatrices().peek().getPositionMatrix();

        int[][] corners = {
                { x + w - r, y + r },
                { x + w - r, y + h - r},
                { x + r, y + h - r },
                { x + r, y + r }
        };

        for (int corner = 0; corner < 4; corner++) {
            int cornerStart = (corner - 1) * 90;
            int cornerEnd = cornerStart + 90;
            for (int i = cornerStart; i <= cornerEnd; i += 10) {
                float angle = (float)Math.toRadians(i);
                float rx = corners[corner][0] + (float)(Math.cos(angle) * r);
                float ry = corners[corner][1] + (float)(Math.sin(angle) * r);
                buf.vertex(mat, rx, ry, 0).color(color);
            }
        }

        buf.vertex(mat, corners[0][0], y, 0).color(color); // connect last to first vertex

        beginRendering();
        drawBuffer(buf);
        finishRendering();
    }

    public static void drawRoundHoriLine(DrawContext context, int x, int y, int length, int thickness, int color) {
        drawRoundRect(context, x, y, length, thickness, thickness / 2, color);
    }

    public static void drawRoundVertLine(DrawContext context, int x, int y, int length, int thickness, int color) {
        drawRoundRect(context, x, y, thickness, length, thickness / 2, color);
    }

    // default text

    public static void drawDefaultScaledText(DrawContext context, Text text, int x, int y, float scale, boolean shadow, int color) {
        MatrixStack m = context.getMatrices();
        m.scale(scale, scale, scale);

        float rescale = 1 / scale;
        x = (int)(x * rescale);
        y = (int)(y * rescale);

        drawDefaultText(context, text, x, y, shadow, color);
        m.scale(rescale, rescale, rescale);
    }

    public static void drawDefaultCenteredScaledText(DrawContext context, Text text, int centerX, int y, float scale, boolean shadow, int color) {
        MatrixStack m = context.getMatrices();
        m.scale(scale, scale, scale);

        float rescale = 1 / scale;
        centerX = (int)(centerX * rescale);
        centerX = centerX - (mc.textRenderer.getWidth(text) / 2);
        y = (int)(y * rescale);

        drawDefaultText(context, text, centerX, y, shadow, color);
        m.scale(rescale, rescale, rescale);
    }

    public static void drawDefaultRightScaledText(DrawContext context, Text text, int rightX, int y, float scale, boolean shadow, int color) {
        MatrixStack m = context.getMatrices();
        m.scale(scale, scale, scale);

        float rescale = 1 / scale;
        rightX = (int)(rightX * rescale);
        rightX = rightX - mc.textRenderer.getWidth(text);
        y = (int)(y * rescale);

        drawDefaultText(context, text, rightX, y, shadow, color);
        m.scale(rescale, rescale, rescale);
    }

    public static void drawDefaultScaledText(DrawContext context, Text text, int x, int y, float scale, boolean shadow) {
        drawDefaultScaledText(context, text, x, y, scale, shadow, 0xFFFFFFFF);
    }

    public static void drawDefaultCenteredScaledText(DrawContext context, Text text, int centerX, int y, float scale, boolean shadow) {
        drawDefaultCenteredScaledText(context, text, centerX, y, scale, shadow, 0xFFFFFFFF);
    }

    public static void drawDefaultRightScaledText(DrawContext context, Text text, int rightX, int y, float scale, boolean shadow) {
        drawDefaultRightScaledText(context, text, rightX, y, scale, shadow, 0xFFFFFFFF);
    }

    public static void drawDefaultText(DrawContext context, Text text, int x, int y, boolean shadow, int color) {
        context.drawText(mc.textRenderer, text, x, y, color, shadow);
    }

    public static void drawDefaultCode(DrawContext context, String code, int x, int y, boolean shadow, int color) {
        context.drawText(system.codeRenderer, code, x, y, color, shadow);
    }

    // non-default
    // draw normal text

    public static void drawText(DrawContext context, String text, int x, int y, float scale, boolean shadow) {
        drawDefaultScaledText(context, Text.literal(text), x, y, scale, shadow);
    }

    public static void drawText(DrawContext context, String text, int x, int y, boolean shadow) {
        drawDefaultScaledText(context, Text.literal(text), x, y, 1.0F, shadow);
    }

    // draw right-aligned text

    public static void drawRightText(DrawContext context, String text, int leftX, int y, float scale, boolean shadow) {
        drawDefaultRightScaledText(context, Text.literal(text), leftX, y, scale, shadow);
    }

    public static void drawRightText(DrawContext context, String text, int leftX, int y, boolean shadow) {
        drawDefaultRightScaledText(context, Text.literal(text), leftX, y, 1.0F, shadow);
    }

    public static void drawRightText(DrawContext context, Text text, int leftX, int y, float scale, boolean shadow) {
        drawDefaultRightScaledText(context, text, leftX, y, scale, shadow);
    }

    public static void drawRightText(DrawContext context, Text text, int leftX, int y, boolean shadow) {
        drawDefaultRightScaledText(context, text, leftX, y, 1.0F, shadow);
    }

    // draw centered text

    public static void drawCenteredText(DrawContext context, String text, int centerX, int y, float scale, boolean shadow) {
        drawDefaultCenteredScaledText(context, Text.literal(text), centerX, y, scale, shadow);
    }

    public static void drawCenteredText(DrawContext context, String text, int centerX, int y, boolean shadow) {
        drawDefaultCenteredScaledText(context, Text.literal(text), centerX, y, 1.0F, shadow);
    }

    public static void drawCenteredText(DrawContext context, Text text, int centerX, int y, float scale, boolean shadow) {
        drawDefaultCenteredScaledText(context, text, centerX, y, scale, shadow);
    }

    public static void drawCenteredText(DrawContext context, Text text, int centerX, int y, boolean shadow) {
        drawDefaultCenteredScaledText(context, text, centerX, y, 1.0F, shadow);
    }

    // misc

    public static void drawTexture(DrawContext context, Identifier texture, int x, int y, int w, int h) {
        BufferBuilder buf = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        Matrix4f mat = context.getMatrices().peek().getPositionMatrix();

        buf.vertex(mat, x, y, 0).texture(0, 0);
        buf.vertex(mat, x, y + h, 0).texture(0, 1);
        buf.vertex(mat, x + w, y + h, 0).texture(1, 1);
        buf.vertex(mat, x + w, y, 0).texture(1, 0);

        disableCull();
        setShader(GameRenderer::getPositionTexProgram);
        setShaderTexture(0, texture);

        BufferRenderer.drawWithGlobalProgram(buf.end());

        enableCull();
    }

    public static void drawRoundTexture(DrawContext context, Identifier texture, int x, int y, int w, int h, int r) {
        r = MathUtils.clamp(r, 0, Math.min(w, h) / 2);

        BufferBuilder buf = getTessellator().begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_TEXTURE);
        Matrix4f mat = context.getMatrices().peek().getPositionMatrix();

        buf.vertex(mat, x + w / 2F, y + h / 2F, 0).texture(0.5F, 0.5F);

        int[][] corners = {
                { x + w - r, y + r },
                { x + w - r, y + h - r},
                { x + r, y + h - r },
                { x + r, y + r }
        };

        for (int corner = 0; corner < 4; corner++) {
            int cornerStart = (corner - 1) * 90;
            int cornerEnd = cornerStart + 90;
            for (int i = cornerStart; i <= cornerEnd; i += 10) {
                float angle = (float)Math.toRadians(i);
                float rx = corners[corner][0] + (float)(Math.cos(angle) * r);
                float ry = corners[corner][1] + (float)(Math.sin(angle) * r);
                float u = (rx - x) / w;
                float v = (ry - y) / h;
                buf.vertex(mat, rx, ry, 0).texture(u, v);
            }
        }

        buf.vertex(mat, corners[0][0], y, 0).texture(((float)corners[0][0] - x) / w, 0); // connect last to first vertex

        disableCull();
        setShader(GameRenderer::getPositionTexProgram);
        setShaderTexture(0, texture);

        BufferRenderer.drawWithGlobalProgram(buf.end());

        enableCull();
    }

    public static void drawItem(DrawContext context, ItemStack item, int x, int y, float scale) {
        x = (int)(x / scale);
        y = (int)(y / scale);
        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, scale);
        context.drawItem(item, x, y);
        context.drawItemInSlot(mc.textRenderer, item, x, y);
        context.getMatrices().pop();
    }

    public static void drawItem(DrawContext context, ItemStack item, int x, int y, float scale, String text) {
        x = (int)(x / scale);
        y = (int)(y / scale);
        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, scale);
        context.drawItem(item, x, y);
        context.drawItemInSlot(mc.textRenderer, item, x, y, text);
        context.getMatrices().pop();
    }

    public static void drawItem(DrawContext context, ItemStack item, int x, int y, int size) {
        drawItem(context, item, x, y, size / 16.0F);
    }

    public static void drawItem(DrawContext context, ItemStack item, int x, int y) {
        drawItem(context, item, x, y, 1.0F);
    }

    // util

    public static void beginRendering() {
        disableCull();
        enableBlend();
        defaultBlendFunc();
        setShader(GameRenderer::getPositionColorProgram);
    }

    public static void finishRendering() {
        enableCull();
        disableBlend();
        setShader(GameRenderer::getPositionTexProgram);
    }

    public static void check(boolean check, String msg) {
        if (!check) {
            throw new IllegalArgumentException(msg);
        }
    }

    public static void drawBuffer(BufferBuilder buf) {
        BufferRenderer.drawWithGlobalProgram(buf.end());
    }

    public static Tessellator getTessellator() {
        return Tessellator.getInstance();
    }

    public static int width() {
        return mc.getWindow().getScaledWidth();
    }

    public static int height() {
        return mc.getWindow().getScaledHeight();
    }

    public static void setCursor(int x, int y) {
        Window win = mc.getWindow();
        int w1 = win.getWidth();
        int w2 = win.getScaledWidth();
        int h1 = win.getHeight();
        int h2 = win.getScaledHeight();
        double ratW = (double)w2 / (double)w1;
        double ratH = (double)h2 / (double)h1;
        GLFW.glfwSetCursorPos(win.getHandle(), x / ratW, y / ratH);
    }

    public static Point getCursor() {
        Window win = mc.getWindow();
        int w1 = win.getWidth();
        int w2 = win.getScaledWidth();
        int h1 = win.getHeight();
        int h2 = win.getScaledHeight();
        double rW = (double)w2 / (double)w1;
        double rH = (double)h2 / (double)h1;
        return new Point((int)(rW * mc.mouse.getX()), (int)(rH * mc.mouse.getY()));
    }
}
