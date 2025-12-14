package com.cgvsu.math.matrices;

import com.cgvsu.math.vectors.Vector4f;

public class Matrix4 {
    private float[][] data = new float[4][4];

    // -------------------------- Конструкторы ---------------------------
    public Matrix4() {}
    public Matrix4(float[][] data) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                this.data[i][j] = data[i][j];
            }
        }
    }

    // ----------------------- ГЕТЕРЫ И СЕТЕРЫ ---------------------------
    public float get(int row, int col) {
        return data[row][col];
    }
    public void set(int row, int col, float value) {
        data[row][col] = value;
    }

    // ----------------------- СОЗДАНИЕ МАТРИЦ ---------------------
    // Единичная
    public static Matrix4 identityMatrix() {
        float[][] id = new float[4][4];

        for (int i = 0; i < 4; i++) {
            id[i][i] = 1.0F;
        }

        return new Matrix4(id);
    }
    // Нулевая
    public static Matrix4 zeroMatrix() {
        return new Matrix4();
    }

    // --------------------------- РАБОТА С МАТРИЦАМИ --------------------------
    // Сложение
    public Matrix4 addition(Matrix4 other) {
        float[][] result = new float[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[i][j] = this.data[i][j] + other.data[i][j];
            }
        }

        return new Matrix4(result);
    }
    // Вычитание
    public Matrix4 subtraction(Matrix4 other) {
        float[][] result = new float[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[i][j] = this.data[i][j] - other.data[i][j];
            }
        }

        return new Matrix4(result);
    }
    // Умножение на вектор
    public Vector4f multiplication(Vector4f vector) {
        float x = data[0][0] * vector.getX() + data[0][1] * vector.getY() +
                data[0][2] * vector.getZ() + data[0][3] * vector.getW();

        float y = data[1][0] * vector.getX() + data[1][1] * vector.getY() +
                data[1][2] * vector.getZ() + data[1][3] * vector.getW();

        float z = data[2][0] * vector.getX() + data[2][1] * vector.getY() +
                data[2][2] * vector.getZ() + data[2][3] * vector.getW();;

        float w = data[3][0] * vector.getX() + data[3][1] * vector.getY() +
                data[3][2] * vector.getZ() + data[3][3] * vector.getW();;

        return new Vector4f(x, y, z, w);
    }
    // Умножение на матрицу
    public Matrix4 multiplication(Matrix4 other) {
        float[][] result = new float[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                float sum = 0;
                for (int k = 0; k < 4; k++) {
                    sum += this.data[i][k] * other.data[k][j];
                }
                result[i][j] = sum;
            }
        }

        return new Matrix4(result);
    }

    // Транспонирование
    public Matrix4 transposition() {
        float[][] result = new float[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[j][i] = this.data[i][j];
            }
        }

        return new Matrix4(result);
    }

    // ------------------------- ВЫВОД МАТРИЦЫ -----------------------------------
    public void print() {
        System.out.println("Matrix 4x4:");

        for (int i = 0; i < 4; i++) {
            System.out.print("[ ");
            for (int j = 0; j < 4; j++) {
                System.out.printf("%6.2f ", data[i][j]);
            }
            System.out.println("]");
        }
    }
}
