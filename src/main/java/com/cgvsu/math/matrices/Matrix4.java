package com.cgvsu.math.matrices;

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
