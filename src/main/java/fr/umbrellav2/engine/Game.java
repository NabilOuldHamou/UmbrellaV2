package fr.umbrellav2.engine;

public class Game implements IGameLogic {

    private Window window;

    public Game() {
        // TODO Renderer, Camera
    }

    @Override
    public void init(Window window) throws Exception {
        this.window = window;
    }

    @Override
    public void input(Window window) {
        // TODO handling the updates.

    }

    @Override
    public void update() {
        // TODO update the elements of the game (logic)
    }

    @Override
    public void render(Window window) {
        // TODO call the render method of the renderer
    }

    @Override
    public void cleanup() {
        // TODO calling renderer.cleanup();
    }
}
