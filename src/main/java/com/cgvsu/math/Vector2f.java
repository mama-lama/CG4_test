package com.cgvsu.math;

public class Vector2f {
    private float x;
    private float y;

    // --------------- КОНСТРУКТОРЫ --------------------
    public Vector2f() {
        this(0,0);
    }
    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    // ---------------- ГЕТЕРЫ ---------------------------
    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }

    // ----------------- СЕТТЕРЫ --------------------------
    public void setX(float newX) {
        this.x = newX;
    }
    public void setY(float newY) {
        this.y = newY;
    }
    // ----------------- ОПЕРАЦИИ НАД ВЕКТОРАМИ ---------------
    // Сложение
    public Vector2f addition(Vector2f other) {
        return new Vector2f(this.x + other.x, this.y + other.y);
    }
    // Вычитание
    public Vector2f subtraction(Vector2f other) {
        return new Vector2f(this.x - other.x, this.y - other.y);
    }
    // Умножение на скаляр
    public Vector2f multiplication(float scalar) {
        return new Vector2f(this.x * scalar, this.y * scalar);
    }
    // Деление на скаляр
    public Vector2f division(float scalar) {
        checkDivisionByZero(scalar);
        return new Vector2f(this.x / scalar, this.y / scalar);
    }

    // ---------------------- ДРУГИЕ МЕТОДЫ -------------------
    // Длина вектора
    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }
    // Нормализация вектора
    public Vector2f normalize() {
        float len = length();
        return this.division(len);
    }
    // Скалярное произведение
    public float scalarProduct(Vector2f other) {
        return (float) Math.sqrt(this.x * other.x + other.y * this.y);
    }

    // ---------------------- ВЫВОД ВЕКТОРА -------------------
    public String toString() {
        return "(" + x + "; " + y + ")";
    }

    // ---------------------- ОШИБКИ --------------------------
    private static void checkDivisionByZero(float scalar) {
        if (Math.abs(scalar) < 0.0000001) {
            throw new ArithmeticException("Деление на ноль не допускается! Получено: " + scalar);
        }
    }
}
