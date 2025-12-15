package com.cgvsu.math.matrices;

import com.cgvsu.math.vectors.Vector2f;
import com.cgvsu.math.vectors.Vector3f;

public class Matrix2 {
    private float[][] data = new float[2][2];

    // -------------------------- Конструкторы ---------------------------
    public Matrix2() {}
    public Matrix2(float[][] data) {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                this.data[i][j] = data[i][j];
            }
        }
    }

    // ----------------------- ГЕТТЕРЫ И СЕТТЕРЫ ---------------------------
    public double get(int row, int col) {
        return data[row][col];
    }
    public void set(int row, int col, float value) {
        data[row][col] = value;
    }

    // ----------------------- СОЗДАНИЕ МАТРИЦ ---------------------
    // Единичная
    public static Matrix2 identityMatrix() {
        float[][] id = new float[2][2];

        for (int i = 0; i < 2; i++) {
            id[i][i] = 1.0F;
        }

        return new Matrix2(id);
    }
    // Нулевая
    public static Matrix2 zeroMatrix() {
        return new Matrix2();
    }

    // --------------------------- РАБОТА С МАТРИЦАМИ --------------------------
    // Сложение
    public Matrix2 addition(Matrix2 other) {
        float[][] result = new float[2][2];

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                result[i][j] = this.data[i][j] + other.data[i][j];
            }
        }

        return new Matrix2(result);
    }
    // Вычитание
    public Matrix2 subtraction(Matrix2 other) {
        float[][] result = new float[2][2];

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                result[i][j] = this.data[i][j] - other.data[i][j];
            }
        }

        return new Matrix2(result);
    }
    // Умножение на вектор
    public Vector2f multiplication(Vector2f vector) {
        float x = data[0][0] * vector.getX() + data[0][1] * vector.getY();
        float y = data[1][0] * vector.getX() + data[1][1] * vector.getY();

        return new Vector2f(x, y);
    }
    // Умножение на матрицу
    public Matrix2 multiplication(Matrix2 other) {
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
    public Matrix2 transposition() {
        float[][] result = new float[2][2];

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                result[j][i] = this.data[i][j];
            }
        }

        return new Matrix2(result);
    }

    // ------------------------- ВЫВОД МАТРИЦЫ -----------------------------------
    public void print() {
        System.out.println("Matrix 2x2:");

        for (int i = 0; i < 2; i++) {
            System.out.print("[ ");
            for (int j = 0; j < 2; j++) {
                System.out.printf("%6.2f ", data[i][j]);
            }
            System.out.println("]");
        }
    }
}
