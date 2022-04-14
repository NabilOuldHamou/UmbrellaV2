package fr.umbrellav2;

import fr.umbrellav2.engine.Engine;
import fr.umbrellav2.engine.Game;
import fr.umbrellav2.engine.IGameLogic;

public class Main {

    public static void main(String[] args) throws Exception {
        try {
            IGameLogic game = new Game();

            Engine engine = new Engine("Game", 1280, 720, true, game);

            engine.run();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }

}
