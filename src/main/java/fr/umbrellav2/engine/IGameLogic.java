package fr.umbrellav2.engine;

public interface IGameLogic {

    void init(Window window) throws Exception;

    void input(Window window); // Todo add the mouse and keyboard input

    void update(); // TODO add the input as well

    void render(Window window);

    void cleanup();

}
