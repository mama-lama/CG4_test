package com.cgvsu.math.vectors;

public class Vector3f implements Vector<Vector3f> {
    private float x;
    private float y;
    private float z;

    // Constructors.
    public Vector3f() {
        this(0, 0, 0);
    }
    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Vector3f(float[] components) {
        if (components.length != 3) {
            throw new IllegalArgumentException("Vector3f requires 3 components.");
        }
        this.x = components[0];
        this.y = components[1];
        this.z = components[2];
    }

    // Accessors.
    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    public float getZ() {
        return z;
    }

    // Mutators.
    public void setX(float newX) {
        this.x = newX;
    }
    public void setY(float newY) {
        this.y = newY;
    }
    public void setZ(float newZ) {
        this.z = newZ;
    }
    public void set(float newX, float newY, float newZ) {
        this.x = newX;
        this.y = newY;
        this.z = newZ;
    }

    // Basic arithmetic.
    @Override
    public Vector3f add(Vector3f other) {
        return new Vector3f(this.x + other.x, this.y + other.y, this.z + other.z);
    }
    @Override
    public Vector3f sub(Vector3f other) {
        return new Vector3f(this.x - other.x, this.y - other.y, this.z - other.z);
    }
    @Override
    public Vector3f mult(float scalar) {
        return new Vector3f(this.x * scalar, this.y * scalar, this.z * scalar);
    }
    @Override
    public Vector3f div(float scalar) {
        checkDivisionByZero(scalar);
        return new Vector3f(this.x / scalar, this.y / scalar, this.z / scalar);
    }

    // Length and scalar operations.
    @Override
    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }
    @Override
    public Vector3f normalize() {
        float len = length();
        if (Math.abs(len) < 1e-7) {
            return new Vector3f();
        }
        return this.div(len);
    }
    @Override
    public float scalarProd(Vector3f other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }
    public Vector3f vectorProd(Vector3f other) {
        return new Vector3f(
                this.y * other.z - this.z * other.y,
                this.z * other.x - this.x * other.z,
                this.x * other.y - this.y * other.x
        );
    }

    // Debug representation.
    @Override
    public String toString() {
        return "(" + x + "; " + y + "; " + z + ")";
    }

    private static void checkDivisionByZero(float scalar) {
        if (Math.abs(scalar) < 1e-7f) {
            throw new ArithmeticException("Division by zero in vector division: " + scalar);
        }
    }
}
