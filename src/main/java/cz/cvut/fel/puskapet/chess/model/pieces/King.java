package cz.cvut.fel.puskapet.chess.model.pieces;

import cz.cvut.fel.puskapet.chess.model.Board;
import cz.cvut.fel.puskapet.chess.model.Color;

import java.util.HashSet;

public class King extends Piece {

    /**
     * Constructor
     *
     * @param row
     * @param column
     * @param color
     * @param firstMove
     */
    public King(Integer row, Integer column, Color color, boolean firstMove) {
        super(row, column, PieceType.KING, color, firstMove);
        if (!((color.equals(Color.BLACK) && getCurrentCoordinates().equals("e8")) || (color.equals(Color.WHITE) && getCurrentCoordinates().equals("e1"))))
            this.firstMove = false;
    }

    /**
     * Copy constructor
     *
     * @param king
     */
    public King(King king) {
        super(king);
    }

    /**
     * Gets all potential moves according to rules of chess
     *
     * @return set of all potential moves
     */
    @Override
    public HashSet<String> getPotentialMoves() {
        HashSet<String> potentialMoves = new HashSet<>();
        if (Board.isOnBoard(row - 1, column - 1))
            potentialMoves.add(convertCoordinatesToString(row - 1, column - 1));
        if (Board.isOnBoard(row - 1, column))
            potentialMoves.add(convertCoordinatesToString(row - 1, column));
        if (Board.isOnBoard(row - 1, column + 1))
            potentialMoves.add(convertCoordinatesToString(row - 1, column + 1));
        if (Board.isOnBoard(row + 1, column - 1))
            potentialMoves.add(convertCoordinatesToString(row + 1, column - 1));
        if (Board.isOnBoard(row + 1, column))
            potentialMoves.add(convertCoordinatesToString(row + 1, column));
        if (Board.isOnBoard(row + 1, column + 1))
            potentialMoves.add(convertCoordinatesToString(row + 1, column + 1));
        if (Board.isOnBoard(row, column - 1))
            potentialMoves.add(convertCoordinatesToString(row, column - 1));
        if (Board.isOnBoard(row, column + 1))
            potentialMoves.add(convertCoordinatesToString(row, column + 1));

        if (firstMove) {
            potentialMoves.add(convertCoordinatesToString(row, column + 2));
            potentialMoves.add(convertCoordinatesToString(row, column - 2));
        }

        return potentialMoves;
    }

    /**
     *
     * @return String representation of the piece in Standard algebraic notation
     */
    @Override
    public String toString() {
        return "K";
    }
}
