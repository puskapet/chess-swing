package cz.cvut.fel.puskapet.chess.view;

import cz.cvut.fel.puskapet.chess.controller.GameController;
import cz.cvut.fel.puskapet.chess.model.Color;
import cz.cvut.fel.puskapet.chess.model.pieces.Piece;

import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * @author puskapet
 */

@Deprecated
public class GameViewCLI implements GameView {
    private GameController game;
    private static final Logger LOGGER = Logger.getLogger("Possible moves");
    

    public GameViewCLI(GameController game) {
        this.game = game;
        this.game.setView(this);
    }

    void printBoard() {
        HashMap<String, Piece> chessboard = game.getCurrentBoard().getChessboard();
        for (char r = '8'; r >= '1'; --r) {
            System.out.print(r);
            for (char c = 'a'; c <= 'h'; ++c) {
                System.out.print(" ");
                if (game.getCurrentBoard().squareIsEmpty("" + c + r)) {
                    System.out.print('-');
                } else {
                    System.out.print(chessboard.get("" + c + r));
                }
            }
            System.out.println();
        }
        System.out.print(" ");
        for (char c = 'a'; c <= 'h'; ++c) {
            System.out.print(" " + c);
        }
        System.out.println();
    }

    @Override
    public void initializeView() {
        System.out.println("Welcome to Java chess!");
        Scanner scan = new Scanner(System.in);
        do {
            System.out.println(game.getTurn() + "'s move.");
            printBoard();
            if (game.getTurn().equals(Color.WHITE)) {
                String result = "";
                for (Piece p : game.getCurrentBoard().getWhitePieces()) {
                    result += p + p.getCurrentCoordinates();
                    result += p.getPotentialMoves().toString();
                    result += '\n';
                }
                LOGGER.info("All potential moves for white\n" + result);
            }
            if (game.getTurn().equals(Color.BLACK)) {
                String result = "";
                for (Piece p : game.getCurrentBoard().getBlackPieces()) {
                    result += p + p.getCurrentCoordinates();
                    result += p.getPotentialMoves().toString();
                    result += '\n';
                }
                LOGGER.info("All potential moves for black\n" + result);
            }
            Vector<String> legalMoves = game.getCurrentLegalMoves();
            LOGGER.info(game.getTurn().toString() + " has " + legalMoves.size() + "legal move(s)." + legalMoves.toString());
            String move = scan.nextLine();
            if (move.length() == 4) {
                game.makeMove(move.substring(0,2), move.substring(2,4));
            }
        } while (true);
    }
}