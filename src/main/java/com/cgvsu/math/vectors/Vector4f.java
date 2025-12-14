package com.cgvsu.math.vectors;

public class Vector4f {
    private float x;
    private float y;
    private float z;
    private float w;

    // ------------------------------- КОНСТРУКТОРЫ ----------------------------
    public Vector4f() {
        this(0, 0, 0, 0);
    }
    public Vector4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    // ---------------------------- ГЕТТЕРЫ -------------------------------
    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    public float getZ() {
        return z;
    }
    public float getW() {
        return w;
    }

    // ---------------------------- СЕТТЕРЫ -------------------------------
    public void setX(float x) {
        this.x = x;
    }
    public void setY(float y) {
        this.y = y;
    }
    public void setZ(float z) {
        this.z = z;
    }
    public void setW(float w) {
        this.w = w;
    }

    // ------------------------- ВЫВОД -------------------------------------
    public String toString() {
        return "(" + x + "; " + y + "; " + z + "; " + w + ")";
    }
}
