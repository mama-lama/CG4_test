package com.cgvsu.math.vectors;

public class Vector4f implements Vector<Vector4f> {
    private float x;
    private float y;
    private float z;
    private float w;

    // Constructors.
    public Vector4f() {
        this(0, 0, 0, 0);
    }
    public Vector4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    public Vector4f(float[] components) {
        if (components.length != 4) {
            throw new IllegalArgumentException("Vector4f requires 4 components.");
        }
        this.x = components[0];
        this.y = components[1];
        this.z = components[2];
        this.w = components[3];
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
    public float getW() {
        return w;
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
    public void setW(float newW) {
        this.w = newW;
    }
    public void set(float newX, float newY, float newZ, float newW) {
        this.x = newX;
        this.y = newY;
        this.z = newZ;
        this.w = newW;
    }

    // Basic arithmetic.
    @Override
    public Vector4f add(Vector4f other) {
        return new Vector4f(this.x + other.x, this.y + other.y, this.z + other.z, this.w + other.w);
    }
    @Override
    public Vector4f sub(Vector4f other) {
        return new Vector4f(this.x - other.x, this.y - other.y, this.z - other.z, this.w - other.w);
    }
    @Override
    public Vector4f mult(float scalar) {
        return new Vector4f(this.x * scalar, this.y * scalar, this.z * scalar, this.w * scalar);
    }
    @Override
    public Vector4f div(float scalar) {
        checkDivisionByZero(scalar);
        return new Vector4f(this.x / scalar, this.y / scalar, this.z / scalar, this.w / scalar);
    }

    // Length and scalar operations.
    @Override
    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z + w * w);
    }
    @Override
    public Vector4f normalize() {
        float len = length();
        if (Math.abs(len) < 1e-7) {
            return new Vector4f();
        }
        return this.div(len);
    }
    @Override
    public float scalarProd(Vector4f other) {
        return this.x * other.x + this.y * other.y + this.z * other.z + this.w * other.w;
    }

    // Debug representation.
    @Override
    public String toString() {
        return "(" + x + "; " + y + "; " + z + "; " + w + ")";
    }

    private static void checkDivisionByZero(float scalar) {
        if (Math.abs(scalar) < 1e-7f) {
            throw new ArithmeticException("Division by zero in vector division: " + scalar);
        }
    }
}
