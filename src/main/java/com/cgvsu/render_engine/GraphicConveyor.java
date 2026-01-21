package com.cgvsu.render_engine;

import com.cgvsu.math.matrices.Matrix4;
import com.cgvsu.math.vectors.Vector2f;
import com.cgvsu.math.vectors.Vector3f;
import com.cgvsu.math.vectors.Vector4f;

public class GraphicConveyor {

    public static Matrix4 rotateScaleTranslate(Vector3f translation, Vector3f rotationDeg, Vector3f scale) {
        // Point 9/10: model matrix for column vectors (T * R * S).
        Matrix4 scaleMatrix = scaleMatrix(scale);
        Matrix4 rotationX = rotationXMatrix((float) Math.toRadians(rotationDeg.getX()));
        Matrix4 rotationY = rotationYMatrix((float) Math.toRadians(rotationDeg.getY()));
        Matrix4 rotationZ = rotationZMatrix((float) Math.toRadians(rotationDeg.getZ()));
        Matrix4 translationMatrix = translationMatrix(translation);

        Matrix4 rotation = rotationZ.mult(rotationY).mult(rotationX);
        return translationMatrix.mult(rotation).mult(scaleMatrix);
    }

    public static Matrix4 lookAt(Vector3f eye, Vector3f target) {
        return lookAt(eye, target, new Vector3f(0F, 1.0F, 0F));
    }

    public static Matrix4 lookAt(Vector3f eye, Vector3f target, Vector3f up) {
        // Point 8/11: view matrix for column-vector convention.
        Vector3f forward = eye.sub(target).normalize();
        Vector3f right = up.vectorProd(forward).normalize();
        Vector3f upCorrected = forward.vectorProd(right).normalize();

        return new Matrix4(
                right.getX(), right.getY(), right.getZ(), -right.scalarProd(eye),
                upCorrected.getX(), upCorrected.getY(), upCorrected.getZ(), -upCorrected.scalarProd(eye),
                forward.getX(), forward.getY(), forward.getZ(), -forward.scalarProd(eye),
                0, 0, 0, 1
        );
    }

    public static Matrix4 perspective(
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        // Point 8: column-vector projection matrix.
        float f = (float) (1.0F / Math.tan(fov * 0.5F));
        return new Matrix4(
                f / aspectRatio, 0, 0, 0,
                0, f, 0, 0,
                0, 0, (farPlane + nearPlane) / (nearPlane - farPlane), (2 * farPlane * nearPlane) / (nearPlane - farPlane),
                0, 0, -1, 0
        );
    }

    public static Vector3f transformToNdc(Matrix4 matrix, Vector3f vertex) {
        // Point 8: clip-space to NDC (divide by W).
        Vector4f clip = matrix.mult(new Vector4f(vertex.getX(), vertex.getY(), vertex.getZ(), 1.0F));
        if (Math.abs(clip.getW()) < 1e-7f) {
            return new Vector3f();
        }
        return new Vector3f(clip.getX() / clip.getW(), clip.getY() / clip.getW(), clip.getZ() / clip.getW());
    }

    public static Vector2f vertexToPoint(final Vector3f ndcVertex, final int width, final int height) {
        float x = (ndcVertex.getX() + 1.0F) * 0.5F * width;
        float y = (1.0F - ndcVertex.getY()) * 0.5F * height;
        return new Vector2f(x, y);
    }

    private static Matrix4 translationMatrix(Vector3f translation) {
        return new Matrix4(
                1, 0, 0, translation.getX(),
                0, 1, 0, translation.getY(),
                0, 0, 1, translation.getZ(),
                0, 0, 0, 1
        );
    }

    private static Matrix4 scaleMatrix(Vector3f scale) {
        return new Matrix4(
                scale.getX(), 0, 0, 0,
                0, scale.getY(), 0, 0,
                0, 0, scale.getZ(), 0,
                0, 0, 0, 1
        );
    }

    private static Matrix4 rotationXMatrix(float angle) {
        float cosA = (float) Math.cos(angle);
        float sinA = (float) Math.sin(angle);
        return new Matrix4(
                1, 0, 0, 0,
                0, cosA, -sinA, 0,
                0, sinA, cosA, 0,
                0, 0, 0, 1
        );
    }

    private static Matrix4 rotationYMatrix(float angle) {
        float cosA = (float) Math.cos(angle);
        float sinA = (float) Math.sin(angle);
        return new Matrix4(
                cosA, 0, sinA, 0,
                0, 1, 0, 0,
                -sinA, 0, cosA, 0,
                0, 0, 0, 1
        );
    }

    private static Matrix4 rotationZMatrix(float angle) {
        float cosA = (float) Math.cos(angle);
        float sinA = (float) Math.sin(angle);
        return new Matrix4(
                cosA, -sinA, 0, 0,
                sinA, cosA, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        );
    }
}
