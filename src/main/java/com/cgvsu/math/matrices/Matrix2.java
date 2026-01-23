package com.cgvsu.math.matrices;

import com.cgvsu.math.vectors.Vector2f;

public class Matrix2 implements Matrix<Matrix2, Vector2f> {
    private float[][] data = new float[2][2];

    // -------------------------- Конструкторы ---------------------------
    // Без параметров (нулевая матрица)
    public Matrix2() {}

    // Передается матрица (Исключительно 2х2 и без значений null!)
    public Matrix2(float[][] data) {
        checkNullMatrix(data);
        checkRowMatrix(data);

        for (int i = 0; i < 2; i++) {
            checkNullRows(data, i);
            checkColumnMatrix(data, i);

            for (int j = 0; j < 2; j++) {
                this.data[i][j] = data[i][j];
            }
        }
    }

    // Передаются числа по очереди
    public Matrix2(float a11, float a12,
                   float a21, float a22) {
        data[0][0] = a11;
        data[0][1] = a12;
        data[1][0] = a21;
        data[1][1] = a22;
    }

    // Копирование матрицы
    public Matrix2(Matrix2 other) {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                this.data[i][j] = other.data[i][j];
            }
        }
    }

    // Единичная
    @Override
    public Matrix2 identity() {
        float[][] id = new float[2][2];

        for (int i = 0; i < 2; i++) {
            id[i][i] = 1.0F;
        }

        return new Matrix2(id);
    }

    // Нулевая
    @Override
    public Matrix2 zero() {
        return new Matrix2();
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
    public void setRow(int row, float a, float b) {
        checkIndices(row, 0);
        data[row][0] = a;
        data[row][1] = b;
    }

    // Установить значения столбца
    public void setColumn(int col, float a, float b) {
        checkIndices(0, col);
        data[0][col] = a;
        data[1][col] = b;
    }

    // --------------------------- РАБОТА С МАТРИЦАМИ --------------------------
    // Сложение
    @Override
    public Matrix2 add(Matrix2 other) {
        float[][] result = new float[2][2];

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                result[i][j] = this.data[i][j] + other.data[i][j];
            }
        }

        return new Matrix2(result);
    }

    // Вычитание
    @Override
    public Matrix2 sub(Matrix2 other) {
        float[][] result = new float[2][2];

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                result[i][j] = this.data[i][j] - other.data[i][j];
            }
        }

        return new Matrix2(result);
    }

    // Умножение на вектор
    @Override
    public Vector2f mult(Vector2f vector) {
        checkNullVector(vector);
        float x = data[0][0] * vector.getX() + data[0][1] * vector.getY();
        float y = data[1][0] * vector.getX() + data[1][1] * vector.getY();

        return new Vector2f(x, y);
    }

    // Умножение на матрицу
    @Override
    public Matrix2 mult(Matrix2 other) {
        float[][] result = new float[2][2];

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                float sum = 0;
                for (int k = 0; k < 2; k++) {
                    sum += this.data[i][k] * other.data[k][j];
                }
                result[i][j] = sum;
            }
        }

        return new Matrix2(result);
    }

    // Транспонирование
    @Override
    public Matrix2 trans() {
        float[][] result = new float[2][2];

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                result[j][i] = this.data[i][j];
            }
        }

        return new Matrix2(result);
    }

    // Детерминант матрицы
    public float det() {
        float a = data[0][0], b = data[0][1];
        float d = data[0][0], e = data[0][1];
        return a * e - b * d;
    }

    // ------------------------- ВЫВОД МАТРИЦЫ -----------------------------------
    @Override
    public void print() {
        System.out.println("Матрица 2x2:");

        for (int i = 0; i < 2; i++) {
            System.out.print("[");
            for (int j = 0; j < 2; j++) {
                System.out.printf("%6.2f ", data[i][j]);
            }
            System.out.println("]");
        }
    }

    // ------------------ ОШИБКИ --------------------------
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

    // Матрица не имеет 2 строки в конструкторе
    private static void checkRowMatrix(float[][] data) {
        if (data.length != 2) {
            throw new IllegalArgumentException("Матрица должна иметь 2 строки. Имеется: " + data.length);
        }
    }

    // Строка имеет 2 столбца
    private static void checkColumnMatrix(float[][] data, int index) {
        if (data[index].length != 2) {
            throw new IllegalArgumentException(index + " ряд должен иметь 2 столбца. Имеется: " + data[index].length);
        }
    }

    private void checkIndices(int row, int col) {
        if (row < 0 || row >= 2 || col < 0 || col >= 2) {
            throw new IndexOutOfBoundsException("Индексы [" + row + "][" + col +
                    "] выходят за границы матрицы 2x2");
        }
    }

    // Проверка вектора на null
    private static void checkNullVector(Vector2f v2) {
        if (v2 == null) {
            throw new NullPointerException("Вектор не должен быть равен null");
        }
    }
}
