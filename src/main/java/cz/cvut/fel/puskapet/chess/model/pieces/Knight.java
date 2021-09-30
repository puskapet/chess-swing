package cz.cvut.fel.puskapet.chess.model.pieces;

import cz.cvut.fel.puskapet.chess.model.Board;
import cz.cvut.fel.puskapet.chess.model.Color;

import java.util.HashSet;

/**
 * @author puskapet
 */
public class Knight extends Piece {
    public Knight(Integer row, Integer column, Color color, boolean firstMove) {
        super(row, column, PieceType.KNIGHT, color, firstMove);
        this.firstMove = false;
    }

    /**
     * Copy constructor
     *
     * @param knight
     */
    public Knight(Knight knight) {
        super(knight);
    }

    /**
     * Gets all potential moves according to rules of chess
     *
     * @return set of all potential moves
     */
    @Override
    public HashSet<String> getPotentialMoves() {
        HashSet<String> potentialMoves = new HashSet<>();
        if (Board.isOnBoard(row - 1, column + 2))
            potentialMoves.add(convertCoordinatesToString(row - 1, column + 2));
        if (Board.isOnBoard(row - 1, column - 2))
            potentialMoves.add(convertCoordinatesToString(row - 1, column - 2));
        if (Board.isOnBoard(row + 1, column + 2))
            potentialMoves.add(convertCoordinatesToString(row + 1, column + 2));
        if (Board.isOnBoard(row + 1, column - 2))
            potentialMoves.add(convertCoordinatesToString(row + 1, column - 2));
        if (Board.isOnBoard(row - 2, column - 1))
            potentialMoves.add(convertCoordinatesToString(row - 2, column - 1));
        if (Board.isOnBoard(row - 2, column + 1))
            potentialMoves.add(convertCoordinatesToString(row - 2, column + 1));
        if (Board.isOnBoard(row + 2, column - 1))
            potentialMoves.add(convertCoordinatesToString(row + 2, column - 1));
        if (Board.isOnBoard(row + 2, column + 1))
            potentialMoves.add(convertCoordinatesToString(row + 2, column + 1));
        return potentialMoves;
    }

    /**
     *
     * @return String representation of the piece in Standard algebraic notation
     */
    @Override
    public String toString() {
        return "N";
    }
}
