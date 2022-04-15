package fr.umbrellav2.engine.components;

import org.joml.Vector4f;

public class SpriteRenderer extends Component {

    private Vector4f color;

    public SpriteRenderer(Vector4f color) {
        this.color = color;
    }

    public SpriteRenderer() {
        this.color = new Vector4f(0.0f, 0.0f, 0.0f, 0.0f);
    }

    @Override
    public void init() {

    }

    @Override
    public void update() {

    }

    public Vector4f getColor() {
        return color;
    }

    public void setColor(Vector4f color) {
        this.color = color;
    }
}
