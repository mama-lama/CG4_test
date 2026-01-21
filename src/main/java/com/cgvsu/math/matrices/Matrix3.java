package com.cgvsu.math.matrices;

import com.cgvsu.math.vectors.Vector3f;

public class Matrix3 implements Matrix<Matrix3, Vector3f> {
    private float[][] data = new float[3][3];

    // Constructors.
    public Matrix3() {}
    public Matrix3(float[][] data) {
        if (data.length != 3 || data[0].length != 3) {
            throw new IllegalArgumentException("Matrix3 requires a 3x3 array.");
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.data[i][j] = data[i][j];
            }
        }
    }
    public Matrix3(float a00, float a01, float a02,
                   float a10, float a11, float a12,
                   float a20, float a21, float a22) {
        data[0][0] = a00; data[0][1] = a01; data[0][2] = a02;
        data[1][0] = a10; data[1][1] = a11; data[1][2] = a12;
        data[2][0] = a20; data[2][1] = a21; data[2][2] = a22;
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
    public void setRow(int row, float a, float b, float c) {
        checkIndices(row, 0);
        data[row][0] = a;
        data[row][1] = b;
        data[row][2] = c;
    }
    public void setColumn(int col, float a, float b, float c) {
        checkIndices(0, col);
        data[0][col] = a;
        data[1][col] = b;
        data[2][col] = c;
    }

    // Identity and zero.
    @Override
    public Matrix3 identity() {
        float[][] id = new float[3][3];

        for (int i = 0; i < 3; i++) {
            id[i][i] = 1.0F;
        }

        return new Matrix3(id);
    }
    @Override
    public Matrix3 zero() {
        return new Matrix3();
    }

    // Arithmetic.
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
    @Override
    public Vector3f mult(Vector3f vector) {
        float x = data[0][0] * vector.getX() + data[0][1] * vector.getY() + data[0][2] * vector.getZ();
        float y = data[1][0] * vector.getX() + data[1][1] * vector.getY() + data[1][2] * vector.getZ();
        float z = data[2][0] * vector.getX() + data[2][1] * vector.getY() + data[2][2] * vector.getZ();

        return new Vector3f(x, y, z);
    }
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

    @Override
    public void print() {
        System.out.println("Matrix 3x3:");

        for (int i = 0; i < 3; i++) {
            System.out.print("[ ");
            for (int j = 0; j < 3; j++) {
                System.out.printf("%6.2f ", data[i][j]);
            }
            System.out.println("]");
        }
    }

    private void checkIndices(int row, int col) {
        if (row < 0 || row >= 3 || col < 0 || col >= 3) {
            throw new IndexOutOfBoundsException(
                    String.format("Index [%d][%d] out of bounds for 3x3 matrix", row, col));
        }
    }
}
