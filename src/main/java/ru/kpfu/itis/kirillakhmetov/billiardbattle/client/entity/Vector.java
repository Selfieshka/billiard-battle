package ru.kpfu.itis.kirillakhmetov.billiardbattle.client.entity;

import lombok.Data;

@Data
public class Vector {
    private double x;
    private double y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector add(Vector v) {
        return new Vector(x + v.x, y + v.y);
    }

    public Vector sub(Vector v) {
        return new Vector(x - v.x, y - v.y);
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

    public double dot(Vector v) {
        return x * v.x + y * v.y;
    }

    public boolean isNull() {
        return ((x == 0) && (y == 0));
    }
}
