import cz.cvut.fel.puskapet.chess.controller.Game;
import cz.cvut.fel.puskapet.chess.model.Board;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author puskapet
 */
public class GameTest {

    @Test
    public void isValidMove_KingSideCastlingThroughCheck_False() {

        //arrange
        Board board = new Board("4k3/8/b7/8/8/8/8/4K2R w K - 0 1");

        Game game = new Game(board);
        //act
        boolean result = game.isValidMove("e1", "g1");

        //assert
        assertFalse(result);
    }

    @Test
    public void isValidMove_QueenSideCastlingThroughCheck_False() {

        //arrange
        Board board = new Board("4k3/8/8/7b/8/8/8/R3K3 w Q - 0 1");

        Game game = new Game(board);
        //act
        boolean result = game.isValidMove("e1", "c1");

        //assert
        assertFalse(result);
    }

    @Test
    public void isCheckmate_BlackCheckmated_True() {

        //arrange
        Board board = new Board("4k3/4Q3/4K3/8/8/8/8/8 b - - 0 1");

        Game game = new Game(board);

        //act
        boolean result = game.isCheckMate();

        assertTrue(result);
    }

    @Test
    public void isStalemate_BlackStalemated_True() {

        //arrange
        Board board = new Board("k7/2Q5/8/8/8/8/8/K7 b - - 0 1");

        Game game = new Game(board);

        //act
        boolean result = game.isStaleMate();

        assertTrue(result);
    }


}
