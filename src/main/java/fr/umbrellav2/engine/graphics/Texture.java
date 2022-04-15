package fr.umbrellav2.engine.graphics;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Texture {

    private final int textureId;

    private int vaoId, vboId, eboId;

    private final int width;
    private final int height;


    // Vertex array for square
    private float[] vertexArray = {
            0.5f, -0.5f, 0.0f,        1.0f, 0.0f, 0.0f, 0.0f,   1, 0,
            -0.5f, 0.5f, 0.0f,        1.0f, 0.0f, 0.0f, 0.0f,   0, 1,
            0.5f, 0.5f, 0.0f,         1.0f, 0.0f, 0.0f, 0.0f,   1, 1,
            -0.5f, -0.5f, 0.0f,       1.0f, 0.0f, 0.0f, 0.0f,   0, 0,
    };

    private int[] elementArray = {
            2, 1, 0,
            0, 1, 3,
    };


    public Texture(String fileName) throws Exception {
        ByteBuffer buffer;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            buffer = STBImage.stbi_load(fileName, w, h, channels, 4);
            if (buffer == null) {
                throw new Exception("Image file (" + fileName + ") not loaded:" + STBImage.stbi_failure_reason());
            }

            width = w.get();
            height = h.get();
        }

        this.textureId = createTexture(buffer);
        STBImage.stbi_image_free(buffer);
    }

    private int createTexture(ByteBuffer buffer) {
        int textureId = GL11.glGenTextures();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0,
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

        return textureId;
    }

    private void createVertices() {
        vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);

        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        vboId = GL30.glGenBuffers();
        GL30.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL30.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);

        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboId = GL30.glGenBuffers();
        GL30.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, eboId);
        GL30.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL15.GL_STATIC_DRAW);

        // POINTERS TIME
        int positionsSize = 3;
        int colorSize = 4;
        int texCoordSize = 2;
        int vertexSizeBytes = (positionsSize + colorSize) * Float.BYTES;

        // POSITIONS
        GL30.glVertexAttribPointer(0, positionsSize, GL11.GL_FLOAT, false, vertexSizeBytes, 0);
        GL30.glEnableVertexAttribArray(0);

        // TEXTURE COORDS
        GL30.glVertexAttribPointer(1, texCoordSize, GL11.GL_FLOAT, false, vertexSizeBytes,
                (positionsSize + colorSize) * Float.BYTES);
        GL30.glEnableVertexAttribArray(1);
    }

    public void render() {
        GL30.glActiveTexture(GL13.GL_TEXTURE0);

        GL30.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

        GL30.glBindVertexArray(vaoId);

        GL30.glDrawElements(GL11.GL_TRIANGLES, elementArray.length, GL11.GL_UNSIGNED_INT, 2);

        GL30.glBindVertexArray(0);
        GL30.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTextureId() {
        return textureId;
    }

    public void bind() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
    }

    public void unbind() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public void cleanup() {
        GL30.glDeleteTextures(textureId);
    }

}
