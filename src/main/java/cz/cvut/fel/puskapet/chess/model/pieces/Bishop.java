package cz.cvut.fel.puskapet.chess.model.pieces;

import cz.cvut.fel.puskapet.chess.model.Board;
import cz.cvut.fel.puskapet.chess.model.Color;

import java.util.HashSet;

/**
 * @author puskapet
 */
public class Bishop extends Piece {
    /**
     * Constructor
     *
     * @param row
     * @param column
     * @param color
     * @param firstMove
     */
    public Bishop(Integer row, Integer column, Color color, boolean firstMove) {
        super(row, column, PieceType.BISHOP, color, firstMove);
        this.firstMove = false;
    }

    /**
     * Copy constructor
     *
     * @param bishop
     */
    public Bishop(Bishop bishop) {
        super(bishop);
    }

    /**
     * Gets all potential moves according to rules of chess
     *
     * @return set of all potential moves
     */
    @Override
    public HashSet<String> getPotentialMoves() {
        HashSet<String> potentialMoves = new HashSet<>();
        for (int r = row - 1, i = 1; r > -1; --r, ++i) {
            if (Board.isOnBoard(r, column + i))
                potentialMoves.add(convertCoordinatesToString(r, column + i));
            if (Board.isOnBoard(r, column - i))
                potentialMoves.add(convertCoordinatesToString(r, column - i));
        }
        for (int r = row + 1, i = 1; r < 8; ++r, ++i) {
            if (Board.isOnBoard(r, column + i))
                potentialMoves.add(convertCoordinatesToString(r, column + i));
            if (Board.isOnBoard(r, column - i))
                potentialMoves.add(convertCoordinatesToString(r, column - i));
        }
        return potentialMoves;
    }

    /**
     *
     * @return String representation of the piece in Standard algebraic notation
     */
    @Override
    public String toString() {
        return "B";
    }
}
