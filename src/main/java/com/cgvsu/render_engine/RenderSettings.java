package com.cgvsu.render_engine;

public class RenderSettings {
    // Ксюня: настройки режимов отрисовки (пункт 15).
    private boolean drawWireframe;
    private boolean useTexture;
    private boolean useLighting;
    private int baseColor;

    public RenderSettings(boolean drawWireframe, boolean useTexture, boolean useLighting, int baseColor) {
        this.drawWireframe = drawWireframe;
        this.useTexture = useTexture;
        this.useLighting = useLighting;
        this.baseColor = baseColor;
    }

    public boolean isDrawWireframe() {
        return drawWireframe;
    }

    public void setDrawWireframe(boolean drawWireframe) {
        this.drawWireframe = drawWireframe;
    }

    public boolean isUseTexture() {
        return useTexture;
    }

    public void setUseTexture(boolean useTexture) {
        this.useTexture = useTexture;
    }

    public boolean isUseLighting() {
        return useLighting;
    }

    public void setUseLighting(boolean useLighting) {
        this.useLighting = useLighting;
    }

    public int getBaseColor() {
        return baseColor;
    }

    public void setBaseColor(int baseColor) {
        this.baseColor = baseColor;
    }
}
