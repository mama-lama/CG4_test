package com.cgvsu.math.matrices;

import com.cgvsu.math.vectors.Vector2f;

public class Matrix2 implements Matrix<Matrix2, Vector2f> {
    private float[][] data = new float[2][2];

    // Constructors.
    public Matrix2() {}
    public Matrix2(float[][] data) {
        if (data.length != 2 || data[0].length != 2) {
            throw new IllegalArgumentException("Matrix2 requires a 2x2 array.");
        }
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                this.data[i][j] = data[i][j];
            }
        }
    }
    public Matrix2(float a11, float a12, float a21, float a22) {
        data[0][0] = a11;
        data[0][1] = a12;
        data[1][0] = a21;
        data[1][1] = a22;
    }

    // Element access.
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
    public void setRow(int row, float a, float b) {
        checkIndices(row, 0);
        data[row][0] = a;
        data[row][1] = b;
    }
    public void setColumn(int col, float a, float b) {
        checkIndices(0, col);
        data[0][col] = a;
        data[1][col] = b;
    }

    // Identity and zero.
    @Override
    public Matrix2 identity() {
        float[][] id = new float[2][2];

        for (int i = 0; i < 2; i++) {
            id[i][i] = 1.0F;
        }

        return new Matrix2(id);
    }
    @Override
    public Matrix2 zero() {
        return new Matrix2();
    }

    // Arithmetic.
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
    @Override
    public Vector2f mult(Vector2f vector) {
        float x = data[0][0] * vector.getX() + data[0][1] * vector.getY();
        float y = data[1][0] * vector.getX() + data[1][1] * vector.getY();

        return new Vector2f(x, y);
    }
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

    @Override
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

    private void checkIndices(int row, int col) {
        if (row < 0 || row >= 2 || col < 0 || col >= 2) {
            throw new IndexOutOfBoundsException(
                    String.format("Index [%d][%d] out of bounds for 2x2 matrix", row, col));
        }
    }
}
