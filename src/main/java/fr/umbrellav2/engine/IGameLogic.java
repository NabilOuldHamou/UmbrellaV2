package fr.umbrellav2.engine;

import org.jetbrains.annotations.NotNull;

public interface IGameLogic {

    void init(Window window) throws Exception;

    void input(Window window); // Todo add the mouse and keyboard input

    void update(); // TODO add the input as well

    void render();

    void cleanup();

}
