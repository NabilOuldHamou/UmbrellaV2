package fr.umbrellav2.engine;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Class handling the window.
 */
public class Window {

    // Width and Height of the window.
    private int width, height;

    // Title of the window.
    private String title;

    // OpenGL Window, this being a bind of the C version, it does not have a class thus it is a long.
    private long window;

    // If the window has been resized and if is the vSync active.
    private boolean resized, vSync;

    public Window(int width, int height, String title, boolean vSync) {
        this.width = width;
        this.height = height;
        this.title = title;
        this.vSync = vSync;
        this.resized = false;
    }

    public void init() {

        // Defining for error callback for GLFW.
        GLFWErrorCallback.createPrint(System.err).set();

        // If GLFW cannot be initialized it will throw an error.
        if(!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW.");
        }

        // Defining the window hints for GLFW.
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GL11.GL_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GL11.GL_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);

        // Creating the window.
        window = glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);

        // Checks if the window was created.
        if (window == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create the window.");
        }

        // Called whenever the window is resized, allows to get the new width and height.
        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            this.width = width;
            this.height = height;
            this.resized = true;
        });

        // Getting the Video Mode of the primary monitor.
        GLFWVidMode vidMode = glfwGetVideoMode(org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor());

        // Setting the window in the middle of the screen.
        glfwSetWindowPos(
                window,
                (vidMode.width() - width) / 2,
                (vidMode.height() - height) / 2
        );

        // Makes the context of the window in the current thread.
        glfwMakeContextCurrent(window);

        if (vSync) {
            glfwSwapInterval(1);
        } else {
           glfwSwapInterval(0);
        }

        glfwShowWindow(window);

        GL.createCapabilities();

        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        GL11.glEnable(GL11.GL_DEPTH_TEST);

    }

    public boolean windowShouldClose() {
        return glfwWindowShouldClose(window);
    }

    public void update() {
        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    public String getTitle() {
        return title;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isResized() {
        return resized;
    }

    public void setResized(boolean resized) {
        this.resized = resized;
    }

    public boolean isvSync() {
        return vSync;
    }

    public void setvSync(boolean vSync) {
        this.vSync = vSync;
    }

    public long getWindow() {
        return window;
    }

}
