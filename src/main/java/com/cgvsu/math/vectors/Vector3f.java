package com.cgvsu.math.vectors;

// Это заготовка для собственной библиотеки для работы с линейной алгеброй
public class Vector3f {
    private float x;
    private float y;
    private float z;

    // ---------------------- КОНСТРУКТОРЫ ----------------------------
    public Vector3f() {
        this(0, 0, 0);
    }
    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // ----------------------- ГЕТЕРЫ ---------------------------------
    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    public float getZ() {
        return z;
    }

    // ----------------------- СЕТТЕРЫ -----------------------------------
    public void setX(float x) {
        this.x = x;
    }
    public void setY(float y) {
        this.y = y;
    }
    public void setZ(float z) {
        this.z = z;
    }

    // ----------------- ОПЕРАЦИИ НАД ВЕКТОРАМИ ----------------------
    // Сложение
    public Vector3f addition(Vector3f other) {
        return new Vector3f(this.x + other.x, this.y + other.y, this.z + other.z);
    }
    // Вычитание
    public Vector3f subtraction(Vector3f other) {
        return new Vector3f(this.x - other.x, this.y - other.y, this.z - other.z);
    }
    // Умножение
    public Vector3f multiplication(float scalar) {
        return new Vector3f(this.x * scalar, this.y * scalar, this.z * scalar);
    }
    // Деление
    public Vector3f division(float scalar) {
        checkDivisionByZero(scalar);
        return new Vector3f(this.x / scalar, this.y / scalar, this.z / scalar);
    }

    // -------------------------- ДРУГИЕ МЕТОДЫ --------------------
    // Длина вектора
    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }
    // Нормализация вектора
    public Vector3f normalize() {
        float len = length();
        return this.division(len);
    }
    // Скалярное произведение
    public float scalarProduct(Vector3f other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }
    // Векторное произведение
    public Vector3f vectorProduct(Vector3f other) {
        return new Vector3f(this.y * other.z - this.z * other.y, this.z * other.x - this.x * other.z, this.x * other.y - this.y * other.x);
    }

    // -------------------------- ВЫВОД ВЕКТОРА --------------------------
    public String toString() {
        return "(" + x + "; " + y + "; " + x + ")";
    }

    // -------------------------- ОШИБКИ ----------------------------------
    private static void checkDivisionByZero(float scalar) {
        if (Math.abs(scalar) < 1e-7f) {
            throw new ArithmeticException("Деление на ноль не допускается! Получено: " + scalar);
        }
    }
}
