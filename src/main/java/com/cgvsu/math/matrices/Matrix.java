package com.cgvsu.math.matrices;

public interface Matrix<T extends Matrix<T, V>, V> {
    // Basic matrix arithmetic and transforms.
    T add(T other);
    T sub(T other);
    T mult(T other);
    V mult(V vector);
    T trans();

    T identity();
    T zero();

    // Element access and debug output.
    float get(int row, int col);
    void set(int row, int col, float value);
    void print();
}
