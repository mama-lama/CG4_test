package com.cgvsu.math.matrices;

import com.cgvsu.math.vectors.Vector3f;
import com.cgvsu.math.vectors.Vector4f;

public class Matrix4 implements Matrix<Matrix4, Vector4f> {
    private float[][] data = new float[4][4];

    // -------------------------- Конструкторы ---------------------------
    // Без параметров (нулевая матрица)
    public Matrix4() {}

    // Передается матрица (Исключительно 4х4 и без значений null!)
    public Matrix4(float[][] data) {
        checkNullMatrix(data);
        checkRowMatrix(data);

        for (int i = 0; i < 4; i++) {
            checkNullRows(data, i);
            checkColumnMatrix(data, i);
            for (int j = 0; j < 4; j++) {
                this.data[i][j] = data[i][j];
            }
        }
    }

    // Передаются числа по очереди
    public Matrix4(float a00, float a01, float a02, float a03,
                   float a10, float a11, float a12, float a13,
                   float a20, float a21, float a22, float a23,
                   float a30, float a31, float a32, float a33) {
        data[0][0] = a00; data[0][1] = a01; data[0][2] = a02; data[0][3] = a03;
        data[1][0] = a10; data[1][1] = a11; data[1][2] = a12; data[1][3] = a13;
        data[2][0] = a20; data[2][1] = a21; data[2][2] = a22; data[2][3] = a23;
        data[3][0] = a30; data[3][1] = a31; data[3][2] = a32; data[3][3] = a33;
    }

    // Копирование матрицы
    public Matrix4(Matrix4 other) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                this.data[i][j] = other.data[i][j];
            }
        }
    }

    // Единичная
    @Override
    public Matrix4 identity() {
        float[][] id = new float[4][4];

        for (int i = 0; i < 4; i++) {
            id[i][i] = 1.0F;
        }

        return new Matrix4(id);
    }

    // Нулевая
    @Override
    public Matrix4 zero() {
        return new Matrix4();
    }

    // ----------------------- ГЕТЕРЫ И СЕТЕРЫ ---------------------------
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
    public void setRow(int row, float a, float b, float c, float d) {
        checkIndices(row, 0);
        data[row][0] = a;
        data[row][1] = b;
        data[row][2] = c;
        data[row][3] = d;
    }

    // Установить значения столбца
    public void setColumn(int col, float a, float b, float c, float d) {
        checkIndices(0, col);
        data[0][col] = a;
        data[1][col] = b;
        data[2][col] = c;
        data[3][col] = d;
    }

    // --------------------------- РАБОТА С МАТРИЦАМИ --------------------------
    // Сложение
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

    // Вычитание
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

    // Умножение на вектор
    @Override
    public Vector4f mult(Vector4f vector) {
        checkNullVector(vector);
        float newX = data[0][0] * vector.getX() + data[0][1] * vector.getY() +
                data[0][2] * vector.getZ() + data[0][3] * vector.getW();

        float newY = data[1][0] * vector.getX() + data[1][1] * vector.getY() +
                data[1][2] * vector.getZ() + data[1][3] * vector.getW();

        float newZ = data[2][0] * vector.getX() + data[2][1] * vector.getY() +
                data[2][2] * vector.getZ() + data[2][3] * vector.getW();

        float newW = data[3][0] * vector.getX() + data[3][1] * vector.getY() +
                data[3][2] * vector.getZ() + data[3][3] * vector.getW();

        return new Vector4f(newX, newY, newZ, newW);
    }

    // Умножение на матрицу
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

    // Транспонирование
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

    // ------------------------- АФИННЫЕ ПРЕОБРАЗОВАНИЯ -----------------------------------
    // Перенос
    public static Matrix4 translate(float tx, float ty, float tz) {
        float[][] result = new float[4][4];

        for (int i = 0; i < 4; i++) {
            result[i][i] = 1.0f;
        }
        result[0][3] = tx;
        result[1][3] = ty;
        result[2][3] = tz;

        return new Matrix4(result);
    }

    // Сжатие
    public static Matrix4 scale(float sx, float sy, float sz) {
        float[][] result = new float[4][4];

        result[0][0] = sx;
        result[1][1] = sy;
        result[2][2] = sz;
        result[3][3] = 1.0f;

        return new Matrix4(result);
    }

    // Поворот вокруг Х
    public static Matrix4 rotateX(float a) {
        float[][] result = new float[4][4];

        for (int i = 0; i < 4; i++) {
            result[i][i] = 1.0f;
        }
        float c = (float) Math.cos(a);
        float s = (float) Math.sin(a);

        result[1][1] = c;
        result[1][2] = -s;
        result[2][1] = s;
        result[2][2] = c;

        return new Matrix4(result);
    }

    // Поворот вокруг Y
    public static Matrix4 rotateY(float a) {
        float[][] result = new float[4][4];

        for (int i = 0; i < 4; i++) {
            result[i][i] = 1.0f;
        }
        float c = (float) Math.cos(a);
        float s = (float) Math.sin(a);

        result[1][1] = c;
        result[1][2] = s;
        result[2][1] = -s;
        result[2][2] = c;

        return new Matrix4(result);
    }

    // Поворот вокруг Z
    public static Matrix4 rotateZ(float a) {
        float[][] result = new float[4][4];

        for (int i = 0; i < 4; i++) {
            result[i][i] = 1.0f;
        }
        float c = (float) Math.cos(a);
        float s = (float) Math.sin(a);

        result[1][1] = c;
        result[1][2] = -s;
        result[2][1] = s;
        result[2][2] = c;

        return new Matrix4(result);
    }



    // ------------------------- ВЫВОД МАТРИЦЫ -----------------------------------
    @Override
    public void print() {
        System.out.println("Матрица 4x4:");

        for (int i = 0; i < 4; i++) {
            System.out.print("[");
            for (int j = 0; j < 4; j++) {
                System.out.printf("%6.2f ", data[i][j]);
            }
            System.out.println("]");
        }
    }

    // ----------------------- ОШИБКИ --------------------------
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
        if (data.length != 4) {
            throw new IllegalArgumentException("Матрица должна иметь 4 строки. Имеется: " + data.length);
        }
    }

    // Строка имеет 3 столбца
    private static void checkColumnMatrix(float[][] data, int index) {
        if (data[index].length != 4) {
            throw new IllegalArgumentException(index + " ряд должен иметь 4 столбца. Имеется: " + data[index].length);
        }
    }

    private void checkIndices(int row, int col) {
        if (row < 0 || row >= 4 || col < 0 || col >= 4) {
            throw new IndexOutOfBoundsException("Индексы [" + row + "][" + col +
                    "] выходят за границы матрицы 4x4");
        }
    }

    // Проверка вектора на null
    private static void checkNullVector(Vector4f v4) {
        if (v4 == null) {
            throw new NullPointerException("Вектор не должен быть равен null");
        }
    }
}
