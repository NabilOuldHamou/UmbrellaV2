package fr.umbrellav2.engine.graphics.renderer;

import org.joml.*;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {

    private int programId;
    private int vertexShaderId;
    private int fragmentShaderId;
    private Map<String, Integer> uniforms;

    public ShaderProgram() throws Exception {
        programId = glCreateProgram();
        if (programId == 0) {
            throw new RuntimeException("Could not create the shader program.");
        }

        uniforms = new HashMap<>();
    }

    public void createUniform(String uniform) {
        int uniformLocation = glGetUniformLocation(programId, uniform);

        if (uniformLocation < 0) {
            throw new RuntimeException("Could not find uniform: " + uniform);
        }

        uniforms.put(uniform, uniformLocation);
    }

    public void setUniform(String uniform, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer floatBuffer = stack.mallocFloat(16);
            value.get(floatBuffer);
            glUniformMatrix4fv(uniforms.get(uniform), false, floatBuffer);
        }
    }

    public void setUniform(String uniform, Matrix3f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer floatBuffer = stack.mallocFloat(9);
            value.get(floatBuffer);
            glUniformMatrix3fv(uniforms.get(uniform), false, floatBuffer);
        }
    }

    public void setUniform(String uniform, int value) {
        glUniform1i(uniforms.get(uniform), value);
    }

    public void setUniform(String uniform, float value) {
        glUniform1f(uniforms.get(uniform), value);
    }

    public void setUniform(String uniform, Vector2f vec) {
        glUniform2f(uniforms.get(uniform), vec.x, vec.y);
    }

    public void setUniform(String uniform, Vector3f vec) {
        glUniform3f(uniforms.get(uniform), vec.x, vec.y, vec.z);
    }

    public void setUniform(String uniform, Vector4f vec) {
        glUniform4f(uniforms.get(uniform), vec.x, vec.y, vec.z, vec.w);
    }

    public void createVertexShader(String shaderCode) {
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) {
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
    }

    public int createShader(String shaderCode, int shaderType) {
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new RuntimeException("Could not create the shader. Type: " + shaderType);
        }

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new RuntimeException("Could not compile the shader code. GL20 Info: " +
                    glGetShaderInfoLog(shaderId, 1024));
        }

        glAttachShader(programId, shaderId);

        return shaderId;
    }

    public void link() throws Exception {
        glLinkProgram(programId);

        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new RuntimeException("Could not link the shader code. GL20 Info: " +
                    glGetProgramInfoLog(programId, 1024));
        }

        if (vertexShaderId != 0) {
            glDetachShader(programId, vertexShaderId);
        }

        if (fragmentShaderId != 0) {
            glDetachShader(programId, fragmentShaderId);
        }

        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Error while validating the shader code. GL20 Info: " +
                    glGetProgramInfoLog(programId, 1024));
        }
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanup() {
        unbind();
        if (programId != 0) {
            glDeleteProgram(programId);
        }
    }

}
