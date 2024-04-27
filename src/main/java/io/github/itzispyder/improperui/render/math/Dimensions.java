package io.github.itzispyder.improperui.render.math;

public class Dimensions {

    public final int x, y, width, height, widthX, heightY;

    public Dimensions(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.widthX = x + width;
        this.heightY = y + height;
    }

    public boolean isOverlapping(Dimensions dim) {
        int tx = this.getX();
        int ty = this.getY();
        int tw = this.getWidth();
        int th = this.getHeight();
        int txTw = tx + tw;
        int tyTh = ty + th;

        int ox = dim.getX();
        int oy = dim.getY();
        int ow = dim.getWidth();
        int oh = dim.getHeight();
        int oxOw = ox + ow;
        int oyOh = oy + oh;

        var topLeft = (txTw >= ox && txTw <= oxOw) && (tyTh >= oy && tyTh <= oyOh);
        var topRight = (tx >= ox && tx <= oxOw) && (tyTh >= oy && tyTh <= oyOh);
        var bottomRight = (tx >= ox && tx <= oxOw) && (ty >= oy && ty <= oyOh);
        var bottomLeft = (txTw >= ox && txTw <= oxOw) && (ty >= oy && ty <= oyOh);

        return topLeft || topRight || bottomLeft || bottomRight;
    }

    public boolean contains(int x, int y) {
        return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.height;
    }

    public boolean contains(double x, double y) {
        return contains((int)x, (int)y);
    }

    public int getX() {
        return x;
    }

    public Dimensions withX(int x) {
        return new Dimensions(x, y, width, height);
    }

    public int getY() {
        return y;
    }

    public Dimensions withY(int y) {
        return new Dimensions(x, y, width, height);
    }

    public int getWidth() {
        return width;
    }

    public Dimensions withWidth(int width) {
        return new Dimensions(x, y, width, height);
    }

    public int getHeight() {
        return height;
    }

    public Dimensions withHeight(int height) {
        return new Dimensions(x, y, width, height);
    }
}
