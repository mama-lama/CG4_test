package com.cgvsu.math.matrices;

import com.cgvsu.math.vectors.Vector4f;

public class Matrix4 implements Matrix<Matrix4, Vector4f> {
    private float[][] data = new float[4][4];

    // Constructors.
    public Matrix4() {}
    public Matrix4(float[][] data) {
        if (data.length != 4 || data[0].length != 4) {
            throw new IllegalArgumentException("Matrix4 requires a 4x4 array.");
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                this.data[i][j] = data[i][j];
            }
        }
    }
    public Matrix4(float a00, float a01, float a02, float a03,
                   float a10, float a11, float a12, float a13,
                   float a20, float a21, float a22, float a23,
                   float a30, float a31, float a32, float a33) {
        data[0][0] = a00; data[0][1] = a01; data[0][2] = a02; data[0][3] = a03;
        data[1][0] = a10; data[1][1] = a11; data[1][2] = a12; data[1][3] = a13;
        data[2][0] = a20; data[2][1] = a21; data[2][2] = a22; data[2][3] = a23;
        data[3][0] = a30; data[3][1] = a31; data[3][2] = a32; data[3][3] = a33;
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
    public void setRow(int row, float a, float b, float c, float d) {
        checkIndices(row, 0);
        data[row][0] = a;
        data[row][1] = b;
        data[row][2] = c;
        data[row][3] = d;
    }
    public void setColumn(int col, float a, float b, float c, float d) {
        checkIndices(0, col);
        data[0][col] = a;
        data[1][col] = b;
        data[2][col] = c;
        data[3][col] = d;
    }

    // Identity and zero.
    @Override
    public Matrix4 identity() {
        float[][] id = new float[4][4];

        for (int i = 0; i < 4; i++) {
            id[i][i] = 1.0F;
        }

        return new Matrix4(id);
    }
    @Override
    public Matrix4 zero() {
        return new Matrix4();
    }

    // Arithmetic.
    @Override
    public Matrix4 add(Matrix4 other) {
        float[][] result = new float[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[i][j] = this.data[i][j] + other.data[i][j];
            }
        }

        return new Matrix4(result);
    }
    @Override
    public Matrix4 sub(Matrix4 other) {
        float[][] result = new float[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[i][j] = this.data[i][j] - other.data[i][j];
            }
        }

        return new Matrix4(result);
    }
    @Override
    public Vector4f mult(Vector4f vector) {
        float x = data[0][0] * vector.getX() + data[0][1] * vector.getY() +
                data[0][2] * vector.getZ() + data[0][3] * vector.getW();

        float y = data[1][0] * vector.getX() + data[1][1] * vector.getY() +
                data[1][2] * vector.getZ() + data[1][3] * vector.getW();

        float z = data[2][0] * vector.getX() + data[2][1] * vector.getY() +
                data[2][2] * vector.getZ() + data[2][3] * vector.getW();

        float w = data[3][0] * vector.getX() + data[3][1] * vector.getY() +
                data[3][2] * vector.getZ() + data[3][3] * vector.getW();

        return new Vector4f(x, y, z, w);
    }
    @Override
    public Matrix4 mult(Matrix4 other) {
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

    @Override
    public Matrix4 trans() {
        float[][] result = new float[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[j][i] = this.data[i][j];
            }
        }

        return new Matrix4(result);
    }

    @Override
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

    private void checkIndices(int row, int col) {
        if (row < 0 || row >= 4 || col < 0 || col >= 4) {
            throw new IndexOutOfBoundsException(
                    String.format("Index [%d][%d] out of bounds for 4x4 matrix", row, col));
        }
    }
}
