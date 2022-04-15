package fr.umbrellav2.game;

import fr.umbrellav2.engine.GameObject;
import fr.umbrellav2.engine.IGameLogic;
import fr.umbrellav2.engine.Window;
import fr.umbrellav2.engine.components.SpriteRenderer;
import fr.umbrellav2.engine.graphics.Camera;
import fr.umbrellav2.engine.graphics.Texture;
import fr.umbrellav2.engine.graphics.renderer.Renderer;
import fr.umbrellav2.engine.utils.Transform;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class Game implements IGameLogic {

    private static Game instance;

    private Window window;
    private Camera camera;
    private Renderer renderer;

    // TEMP
    GameObject go;

    public Game() {
        instance = this;
        this.camera = new Camera(new Vector2f(10, 10));
        this.renderer = new Renderer();
        camera.adjust();
    }

    @Override
    public void init(Window window) throws Exception {
        this.window = window;

        go = new GameObject(new Transform(new Vector2f(200, 200), new Vector2f(100, 100)));
        go.addComponent(new SpriteRenderer(new Texture("textures/grassblock.png")));

        go.init();

        renderer.add(go);
    }

    @Override
    public void input(Window window) {
        // TODO handling the updates.
    }


    @Override
    public void update() {
        window.update();
        go.update();
    }

    @Override
    public void render() {
        renderer.render();
    }

    @Override
    public void cleanup() {
        // TODO calling renderer.cleanup();
        // TODO Cleanup the gameobjects
    }

    public static Game getInstance() {
        return instance;
    }

    public Camera getCamera() {
        return camera;
    }
}
