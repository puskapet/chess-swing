package cz.cvut.fel.puskapet.chess.view;


import cz.cvut.fel.puskapet.chess.controller.Game;
import cz.cvut.fel.puskapet.chess.controller.GameController;

/**
 * @author puskapet
 */
public class LaunchGUI {


    public static void gameLaunchGUI() {
        GameController game = new Game();
        GameView view = new GameViewGUI(game);
        game.initializeController();
    }

    public static void gameLaunchText() {
        GameController game = new Game();
        GameView view = new GameViewCLI(game);
        game.initializeController();

    }

    /**
     * Main of the application
     */
    public static void main(String[] args) {
//        gameLaunchText();
        gameLaunchGUI();
    }

}
