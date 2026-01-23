package com.cgvsu.math.matrices;

public interface Matrix<T extends Matrix<T, V>, V> {
    // Работа с матрицами
    T add(T other);
    T sub(T other);
    T mult(T other);
    V mult(V vector);
    T trans();

    // Создание матриц
    T identity();
    T zero();

    // Геттеры и Сеттеры
    float get(int row, int col);
    void set(int row, int col, float value);
    void print();
}
