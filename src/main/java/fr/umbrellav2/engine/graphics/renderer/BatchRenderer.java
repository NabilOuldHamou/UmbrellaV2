package fr.umbrellav2.engine.graphics.renderer;

import fr.umbrellav2.engine.components.SpriteRenderer;
import fr.umbrellav2.engine.graphics.Texture;
import fr.umbrellav2.engine.utils.Utils;
import fr.umbrellav2.game.Game;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.*;

import java.util.ArrayList;
import java.util.List;

public class BatchRenderer {

    private final int POS_SIZE = 2;
    private final int COLOR_SIZE = 4;

    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;

    private final int TEXT_COORDS_SIZE = 2;
    private final int TEXT_ID_SIZE = 1;

    private final int TEXT_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private final int TEXT_ID_OFFSET = TEXT_COORDS_OFFSET + TEXT_COORDS_SIZE * Float.BYTES;

    private final int VERTEX_SIZE = 9;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private SpriteRenderer[] sprites;
    private int numSprites;
    private boolean hasRoom;
    private float[] vertices;
    private List<Texture> textures;
    private int[] textureSlots = {0, 1, 2, 3, 4, 5, 6, 7};


    private int vaoId, vboId;
    private int maxBatchSize;
    private ShaderProgram shader;

    public BatchRenderer(int maxBatchSize) throws Exception {
        shader = new ShaderProgram();
        shader.createVertexShader(Utils.loadResource("/shaders/vertex.glsl"));
        shader.createFragmentShader(Utils.loadResource("/shaders/fragment.glsl"));
        shader.link();
        shader.createUniform("projection");
        shader.createUniform("view");
        shader.createUniform("textures");

        this.sprites = new SpriteRenderer[maxBatchSize];
        this.maxBatchSize = maxBatchSize;

        vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];

        this.numSprites = 0;
        this.hasRoom = true;
        this.textures = new ArrayList<>();
    }

    public void init() {
        vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);

        vboId = GL30.glGenBuffers();
        GL30.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL30.glBufferData(GL15.GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL15.GL_DYNAMIC_DRAW);

        int eboId = GL30.glGenBuffers();
        int[] indices = generateIndices();
        GL30.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, eboId);
        GL30.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);

        GL30.glVertexAttribPointer(0, POS_SIZE, GL11.GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        GL30.glEnableVertexAttribArray(0);

        GL30.glVertexAttribPointer(1, COLOR_SIZE, GL11.GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        GL30.glEnableVertexAttribArray(1);

        GL30.glVertexAttribPointer(2, TEXT_COORDS_SIZE, GL11.GL_FLOAT, false, VERTEX_SIZE_BYTES, TEXT_COORDS_OFFSET);
        GL30.glEnableVertexAttribArray(2);

        GL30.glVertexAttribPointer(3, TEXT_ID_SIZE, GL11.GL_FLOAT, false, VERTEX_SIZE_BYTES, TEXT_ID_OFFSET);
        GL30.glEnableVertexAttribArray(3);
    }

    public void render() {
        GL30.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL30.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, vertices);

        shader.bind();
        shader.setUniform("projection", Game.getInstance().getCamera().getProjectionMatrix());
        shader.setUniform("view", Game.getInstance().getCamera().getViewMatrix());
        for (int i = 0; i < textures.size(); i++) {
            GL30.glActiveTexture(GL13.GL_TEXTURE0 + i + 1);
            textures.get(i).bind();
        }
        shader.setUniform("textures", textureSlots);

        GL30.glBindVertexArray(vaoId);
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);

        GL30.glDrawElements(GL11.GL_TRIANGLES, this.numSprites * 6, GL11.GL_UNSIGNED_INT, 0);

        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);

        for (int i = 0; i < textures.size(); i++) {
            GL30.glActiveTexture(GL13.GL_TEXTURE0 + i + 1);
            textures.get(i).unbind();
        }
        shader.unbind();
    }

    public void addSprite(SpriteRenderer spriteRenderer) {
        int index = this.numSprites;
        this.sprites[index] = spriteRenderer;
        this.numSprites++;

        if (spriteRenderer.getTexture() != null) {
            if (!textures.contains(spriteRenderer.getTexture())) {
                textures.add(spriteRenderer.getTexture());
            }
        }

        loadVertexProperties(index);

        if (numSprites >= this.maxBatchSize) {
            this.hasRoom = false;
        }
    }

    private void loadVertexProperties(int index) {
        SpriteRenderer spriteRenderer = this.sprites[index];

        int offset = index * 4 * VERTEX_SIZE;

        Vector4f color = spriteRenderer.getColor();
        Vector2f[] texCoords = spriteRenderer.getTextureCoords();

        int textId = 0;
        if (spriteRenderer.getTexture() != null) {
            for (int i = 0; i < textures.size(); i++) {
                if (textures.get(i).equals(spriteRenderer.getTexture())) {
                    textId = i + 1;
                    break;
                }
            }
        }

        float xAdd = 1.0f;
        float yAdd = 1.0f;

        for (int i = 0; i < 4; i++) {
            switch (i) {
                case 1:
                    yAdd = 0.0f;
                    break;
                case 2:
                    xAdd = 0.0f;
                    break;
                case 3:
                    yAdd = 1.0f;
                    break;
            }

            vertices[offset] = spriteRenderer.getParentGameObject().getTransform().getPosition().x +
                    (xAdd * spriteRenderer.getParentGameObject().getTransform().getScale().x);

            vertices[offset + 1] = spriteRenderer.getParentGameObject().getTransform().getPosition().y +
                    (yAdd * spriteRenderer.getParentGameObject().getTransform().getScale().y);

            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

            //TEXT COORDS
            vertices[offset + 6] = texCoords[i].x;
            vertices[offset + 7] = texCoords[i].y;

            //TEXT ID
            vertices[offset + 8] = textId;

            offset += VERTEX_SIZE;
        }
    }

    private int[] generateIndices() {
        int[] elements = new int[6 * maxBatchSize];

        for (int i = 0; i < maxBatchSize; i++) {
            loadElementsIndices(elements, i);
        }
        return elements;
    }

    private void loadElementsIndices(int[] elements, int index) {
        int offsetArrayIndex = 6 * index;
        int offset = 4 * index;

        elements[offsetArrayIndex]     = offset + 3;
        elements[offsetArrayIndex + 1] = offset + 2;
        elements[offsetArrayIndex + 2] = offset;

        elements[offsetArrayIndex + 3] = offset;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;
    }

    public void cleanup() {
        shader.unbind();
        shader.cleanup();
    }

    public boolean hasRoom() {
        return hasRoom;
    }
}
