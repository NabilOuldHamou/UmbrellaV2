package fr.umbrellav2.engine.graphics.renderer;

import fr.umbrellav2.engine.GameObject;
import fr.umbrellav2.engine.components.SpriteRenderer;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class Renderer {

    private final int MAX_BATCH_SIZE = 1000;
    private List<BatchRenderer> batches;

    public Renderer() {
        this.batches = new ArrayList<>();
    }

    public void add(GameObject gameObject) {
        SpriteRenderer spr = gameObject.getComponent(SpriteRenderer.class);
        if (spr != null) {
            add(spr);
        }
    }

    private void add(SpriteRenderer spr) {
        boolean added = false;
        for (BatchRenderer batch : batches) {
            if (batch.hasRoom()) {
                batch.addSprite(spr);
                added = true;
                break;
            }
        }

        if (!added) {
            try {
                BatchRenderer newBatch = new BatchRenderer(MAX_BATCH_SIZE);
                newBatch.init();
                batches.add(newBatch);
                newBatch.addSprite(spr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void render() {
        for (BatchRenderer batch : batches) {
            clear();
            batch.render();
        }
    }

    private void clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
    }
}
