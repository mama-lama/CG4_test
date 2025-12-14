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

    // ----------------- ОПЕРАЦИИ НАД ВЕКТОРАМИ ----------------------
    // Сложение
    public Vector4f addition(Vector4f other) {
        return new Vector4f(this.x + other.x, this.y + other.y, this.z + other.z, this.w + other.w);
    }
    // Вычитание
    public Vector4f subtraction(Vector4f other) {
        return new Vector4f(this.x - other.x, this.y - other.y, this.z - other.z, this.w - other.w);
    }
    // Умножение
    public Vector4f multiplication(float scalar) {
        return new Vector4f(this.x * scalar, this.y * scalar, this.z * scalar, this.w * scalar);
    }
    // Деление
    public Vector4f division(float scalar) {
        checkDivisionByZero(scalar);
        return new Vector4f(this.x / scalar, this.y / scalar, this.z / scalar, this.w / scalar);
    }

    // ------------------------- ДРУГИЕ МЕТОДЫ ----------------------------
    // Длина вектора
    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z + w * w);
    }
    // Нормализация вектора
    public Vector4f normalize() {
        float len = length();
        return this.division(len);
    }
    // Скалярное произведение
    public float scalarProduct(Vector4f other) {
        return this.x * other.x + this.y * other.y + this.z * other.z + this.w * other.w;
    }

    // ------------------------- ВЫВОД -------------------------------------
    public String toString() {
        return "(" + x + "; " + y + "; " + z + "; " + w + ")";
    }

    // ----------------------- ОШИБКИ --------------------------------------
    private static void checkDivisionByZero(float scalar) {
        if (Math.abs(scalar) < 0.0000001) {
            throw new ArithmeticException("Деление на ноль не допускается! Получено: " + scalar);
        }
    }
}
