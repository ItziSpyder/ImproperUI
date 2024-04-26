package io.github.itzispyder.improperui.render.math;

import io.github.itzispyder.improperui.util.MathUtils;

public class Color {

    public static final Color WHITE = new Color(0xFFFFFFFF);
    public static final Color BLACK = new Color(0xFF000000);
    public static final Color BROWN = new Color(0xFF805100);
    public static final Color RED = new Color(0xFFFF0000);
    public static final Color ORANGE = new Color(0xFFFF8000);
    public static final Color YELLOW = new Color(0xFFFFFF00);
    public static final Color GREEN = new Color(0xFF008000);
    public static final Color LIME = new Color(0xFF80FF00);
    public static final Color BLUE = new Color(0xFF0000FF);
    public static final Color AQUA = new Color(0xFF00D0FF);
    public static final Color MAGENTA = new Color(0xFFE000FF);
    public static final Color PURPLE = new Color(0xFF871FFF);

    public static Color parse(String color) {
        color = color.trim().toLowerCase();
        Color result = BLACK;

        if (color.isEmpty())
            return result;

        if (color.startsWith("#")) {
            color = color.substring(1);
            int len = color.length();

            if (len != 6 && len != 8)
                return result;
            int hex = Integer.parseUnsignedInt(color, 16);
            int a = len == 8 ? (hex >> 24 & 0xFF) : 0xFF;
            int r = hex >> 16 & 0xFF;
            int g = hex >> 8 & 0xFF;
            int b = hex & 0xFF;
            return new Color(a, r, g, b);
        }
        else switch (color) {
            case "white" -> result = WHITE;
            case "brown" -> result = BROWN;
            case "red" -> result = RED;
            case "orange" -> result = ORANGE;
            case "yellow" -> result = YELLOW;
            case "green" -> result = GREEN;
            case "lime" -> result = LIME;
            case "blue" -> result = BLUE;
            case "aqua" -> result = AQUA;
            case "magenta" -> result = MAGENTA;
            case "purple" -> result = PURPLE;
        }
        return result;
    }

    private final int r, g, b, a, hex;
    
    private Color(int hex, int a, int r, int g, int b) {
        this.hex = hex;
        this.a = MathUtils.clamp(a, 0, 255);
        this.r = MathUtils.clamp(r, 0, 255);
        this.g = MathUtils.clamp(g, 0, 255);
        this.b = MathUtils.clamp(b, 0, 255);
    }

    public Color(int hex) {
        this(hex, hex >> 24 & 0xFF, hex >> 16 & 0xFF, hex >> 8 & 0xFF, hex & 0xFF);
    }

    public Color() {
        this(0x00000000);
    }

    public Color(int a, int r, int g, int b) {
        this((a << 24 | r << 16 | g << 8 | b), a, r, g, b);
    }

    public Color(float a, float r, float g, float b) {
        this((int)(a * 0xFF), (int)(r * 0xFF), (int)(g * 0xFF), (int)(b * 0xFF));
    }

    public Color(double a, double r, double g, double b) {
        this((float)a, (float)r, (float)g, (float)b);
    }

    public Color(java.awt.Color awtColor) {
        this(awtColor.getAlpha(), awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue());
    }

    public int getHex() {
        return hex;
    }

    public int getHexOpaque() {
        return 0xFF << 24 | r << 16 | g << 8 | b;
    }

    public int getHexCustomAlpha(int alpha) {
        return alpha << 24 | r << 16 | g << 8 | b;
    }

    public int getHexCustomAlpha(float alpha) {
        return getHexCustomAlpha((int)(alpha * 0xFF));
    }

    public int getHexCustomAlpha(double alpha) {
        return getHexCustomAlpha((int)(alpha * 0xFF));
    }

    public Color withAlpha(int a) {
        return new Color(a, r, g, b);
    }

    public void withRed(int r) {
        new Color(a, r, g, b);
    }

    public void withBlue(int g) {
        new Color(a, r, g, b);
    }

    public void withGreen(int b) {
        new Color(a, r, g, b);
    }

    public int getAlpha() {
        return a;
    }

    public int getRed() {
        return r;
    }

    public int getGreen() {
        return g;
    }

    public int getBlue() {
        return b;
    }

    public float getAlphaF() {
        return a / 255.0F;
    }

    public float getRedF() {
        return r / 255.0F;
    }

    public float getGreenF() {
        return g / 255.0F;
    }

    public float getBlueF() {
        return b / 255.0F;
    }

    public Color lerp(Color color, float delta) {
        delta = (float) MathUtils.clamp(delta);
        float iDelta = 1 - delta;
        int a = (int)(this.a * delta + color.a * iDelta);
        int r = (int)(this.r * delta + color.r * iDelta);
        int g = (int)(this.g * delta + color.g * iDelta);
        int b = (int)(this.b * delta + color.b * iDelta);
        return new Color(a, r, g, b);
    }

    public Color brighter() {
        return new Color(a, r + 10, g + 10, b + 10);
    }

    public Color darker() {
        return new Color(a, r - 10, g - 10, b - 10);
    }

    public java.awt.Color toAwtColor() {
        return new java.awt.Color(r, g, b, a);
    }

    @Override
    public String toString() {
        return "#" + Integer.toHexString(hex).toUpperCase();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Color c)) {
            return false;
        }
        return c.a == a && c.r == r && c.g == g && c.b == b && c.hex == hex;
    }
}
