package io.github.itzispyder.improperui.render.math;

public class Dimensions {

    public int x, y, width, height;

    public Dimensions(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
