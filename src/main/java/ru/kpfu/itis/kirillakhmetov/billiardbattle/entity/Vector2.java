package ru.kpfu.itis.kirillakhmetov.billiardbattle.entity;

import lombok.ToString;

@ToString
public class Vector2 {
    private double x;
    private double y;

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Vector2 add(Vector2 v) {
        return new Vector2(x + v.x, y + v.y);
    }

    public Vector2 sub(Vector2 v) {
        return new Vector2(x - v.x, y - v.y);
    }

    public void multiply(double f) {
        x *= f;
        y *= f;
    }

    public double getSize() {
        return Math.sqrt(x * x + y * y);
    }

    public void normalize() {
        double intensity = getSize();
        x /= intensity;
        y /= intensity;
    }

    public double dot(Vector2 v) {
        return x * v.x + y * v.y;
    }

    public boolean isNull() {
        return ((x == 0) && (y == 0));
    }
}
