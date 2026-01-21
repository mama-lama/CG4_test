package com.cgvsu.math.vectors;

public interface Vector<T extends Vector<T>> {
    // Basic vector arithmetic.
    T add(T other);
    T sub(T other);
    T mult(float scalar);
    T div(float scalar);

    // Length and scalar operations.
    float length();
    T normalize();
    float scalarProd(T other);

    // Debug representation.
    String toString();
}
