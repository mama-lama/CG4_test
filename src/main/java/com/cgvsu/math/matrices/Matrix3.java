package com.cgvsu.math.matrices;

import com.cgvsu.math.MathUtil;
import com.cgvsu.math.vectors.Vector3f;

public class Matrix3 implements Matrix<Matrix3, Vector3f> {
    private float[][] data = new float[3][3];

    // -------------------------- Конструкторы ---------------------------
    // Без параметров (нулевая матрица)
    public Matrix3() {}

    // Передается матрица (Исключительно 3х3 и без значений null!)
    public Matrix3(float[][] data) {
        checkNullMatrix(data);
        checkRowMatrix(data);

        for (int i = 0; i < 3; i++) {
            checkNullRows(data, i);
            checkColumnMatrix(data, i);
            for (int j = 0; j < 3; j++) {
                this.data[i][j] = data[i][j];
            }
        }
    }

    // Передаются числа по очереди
    public Matrix3(float a00, float a01, float a02,
                   float a10, float a11, float a12,
                   float a20, float a21, float a22) {
        data[0][0] = a00; data[0][1] = a01; data[0][2] = a02;
        data[1][0] = a10; data[1][1] = a11; data[1][2] = a12;
        data[2][0] = a20; data[2][1] = a21; data[2][2] = a22;
    }

    // Копирование матрицы
    public Matrix3(Matrix3 other) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.data[i][j] = other.data[i][j];
            }
        }
    }

    // Единичная матрица
    @Override
    public Matrix3 identity() {
        float[][] id = new float[3][3];

        for (int i = 0; i < 3; i++) {
            id[i][i] = 1.0F;
        }

        return new Matrix3(id);
    }

    // Нулевая
    @Override
    public Matrix3 zero() {
        return new Matrix3();
    }

    // ----------------------- ГЕТТЕРЫ И СЕТТЕРЫ ---------------------------
    @Override
    public float get(int row, int col) {
        checkIndices(row, col);
        return data[row][col];
    }

    @Override
    public void set(int row, int col, float value) {
        checkIndices(row, col);
        data[row][col] = value;
    }

    // Установить значения строки
    public void setRow(int row, float a, float b, float c) {
        checkIndices(row, 0);
        data[row][0] = a;
        data[row][1] = b;
        data[row][2] = c;
    }

    // Установить значения столбца
    public void setColumn(int col, float a, float b, float c) {
        checkIndices(0, col);
        data[0][col] = a;
        data[1][col] = b;
        data[2][col] = c;
    }

    // --------------------------- РАБОТА С МАТРИЦАМИ --------------------------
    // Сложение
    @Override
    public Matrix3 add(Matrix3 other) {
        float[][] result = new float[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                result[i][j] = this.data[i][j] + other.data[i][j];
            }
        }

        return new Matrix3(result);
    }

    // Вычитание
    @Override
    public Matrix3 sub(Matrix3 other) {
        float[][] result = new float[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                result[i][j] = this.data[i][j] - other.data[i][j];
            }
        }

        return new Matrix3(result);
    }

    // Умножение на вектор
    @Override
    public Vector3f mult(Vector3f vector) {
        checkNullVector(vector);
        float newX = data[0][0] * vector.getX() + data[0][1] * vector.getY() + data[0][2] * vector.getZ();
        float newY = data[1][0] * vector.getX() + data[1][1] * vector.getY() + data[1][2] * vector.getZ();
        float newZ = data[2][0] * vector.getX() + data[2][1] * vector.getY() + data[2][2] * vector.getZ();

        return new Vector3f(newX, newY, newZ);
    }

    // Умножение на матрицу
    @Override
    public Matrix3 mult(Matrix3 other) {
        float[][] result = new float[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                float sum = 0;
                for (int k = 0; k < 3; k++) {
                    sum += this.data[i][k] * other.data[k][j];
                }
                result[i][j] = sum;
            }
        }

        return new Matrix3(result);
    }

    // Транспонирование
    @Override
    public Matrix3 trans() {
        float[][] result = new float[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                result[j][i] = this.data[i][j];
            }
        }

        return new Matrix3(result);
    }

    // Детерминант матрицы
    public float det() {
        float a = data[0][0], b = data[0][1], c = data[0][2];
        float d = data[1][0], e = data[1][1], f = data[1][2];
        float g = data[2][0], h = data[2][1], i = data[2][2];
        return a * (e * i - h * f) - b * (d * i - f * g) +
                c * (d * h - e * g);
    }

    // Вырезать матрицу 3х3 из 4х4
    public static Matrix3 fromMatrix4UpperLeft(Matrix4 m4) {
        float[][] result = new float[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                result[i][j] = m4.get(i, j);
            }
        }

        return new Matrix3(result);
    }

    // Обратная матрица
    public Matrix3 inv() {
        float det = det();
        checkNullDet(det);
        float invDet = 1.0f / det;

        float a = data[0][0], b = data[0][1], c = data[0][2];
        float d = data[1][0], e = data[1][1], f = data[1][2];
        float g = data[2][0], h = data[2][1], i = data[2][2];

        // Составление матрицы
        float[][] result = new float[3][3];

        result[0][0] = (e * i - f * h) * invDet;
        result[0][1] = -(b * i - c * h) * invDet;
        result[0][2] = (b * f - c * e) * invDet;

        result[1][0] = -(d * i - f * g) * invDet;
        result[1][1] = (a * i - c * g) * invDet;
        result[1][2] = -(a * f - c * d) * invDet;

        result[2][0] = (d * h - e * g) * invDet;
        result[2][1] = -(a * h - b * g) * invDet;
        result[2][2] = (a * e - b * d) * invDet;

        return new Matrix3(result);
    }

    // ------------------------- ВЫВОД МАТРИЦЫ -----------------------------------
    @Override
    public void print() {
        System.out.println("Матрица 3x3:");

        for (int i = 0; i < 3; i++) {
            System.out.print("[");
            for (int j = 0; j < 3; j++) {
                System.out.printf("%6.2f ", data[i][j]);
            }
            System.out.println("]");
        }
    }

    // ---------------------------- ОШИБКИ ---------------------------------
    // Null матрица в конструкторе
    private static void checkNullMatrix(float[][] data) {
        if (data == null) {
            throw new IllegalArgumentException("Матрица не может быть null");
        }
    }

    // Null значение в строке
    private static void checkNullRows(float[][] data, int index) {
        if (data[index] == null) {
            throw new IllegalArgumentException(index + " ряд матрицы не может быть равен null");
        }
    }

    // Матрица имеет 3 строки в конструкторе
    private static void checkRowMatrix(float[][] data) {
        if (data.length != 3) {
            throw new IllegalArgumentException("Матрица должна иметь 3 строки. Имеется: " + data.length);
        }
    }

    // Строка имеет 3 столбца
    private static void checkColumnMatrix(float[][] data, int index) {
        if (data[index].length != 3) {
            throw new IllegalArgumentException(index + " ряд должен иметь 3 столбца. Имеется: " + data[index].length);
        }
    }

    private void checkIndices(int row, int col) {
        if (row < 0 || row >= 3 || col < 0 || col >= 3) {
            throw new IndexOutOfBoundsException("Индексы [" + row + "][" + col +
                    "] выходят за границы матрицы 3x3");
        }
    }

    // Проверка вектора на null
    private static void checkNullVector(Vector3f v3) {
        if (v3 == null) {
            throw new NullPointerException("Вектор не должен быть равен null");
        }
    }

    // Проверка на нулевой детерминант
    private static void checkNullDet(float det) {
        if (Math.abs(det) < MathUtil.EPS) {
            throw new ArithmeticException("Матрица является вырожденной. Детерминант равен: " +
                    Math.abs(det));
        }
    }
}
