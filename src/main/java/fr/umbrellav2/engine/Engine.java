package fr.umbrellav2.engine;

import org.jetbrains.annotations.NotNull;

public class Engine implements Runnable {

    private int fps;
    private int tps;

    private boolean debug;

    private final Window window;
    private IGameLogic gameLogic;

    public Engine(String title, int width, int height, boolean vSync, IGameLogic gameLogic) throws Exception {
        window = new Window(width, height, title, vSync);
        this.gameLogic = gameLogic;
    }

    @Override
    public void run() {
        try {
            init();
            gameLoop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    protected void init() throws Exception {
        window.init();
        gameLogic.init(window);
    }

    private void gameLoop() {

        boolean running = true;

        long lastTime = System.nanoTime();
        double ticks = 30.0;
        double tickRate = 1000000000 / ticks;
        double delta = 0;
        long timer = System.currentTimeMillis();

        while (running && !window.windowShouldClose()) {
            long now = System.nanoTime();
            delta += (now - lastTime) / tickRate;
            lastTime = now;
            while (delta >= 1) {
                update();
                delta--;
            }

            render();

            // TODO only print when debug mode is on.
            if(System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                System.out.println("FPS: " + fps);
                System.out.println("TPS: " + tps);
                fps = 0;
                tps = 0;
            }
        }
    }

    protected void cleanup() {
        gameLogic.cleanup();
    }

    protected void update() {
        gameLogic.update();
        tps++;
    }

    protected void render() {
        gameLogic.render();
        window.update();
        fps++;
    }

    public int getTPS() {
        return tps;
    }

    public int getFPS() {
        return fps;
    }

}
