package cz.cvut.fel.puskapet.chess.model.pieces;

import cz.cvut.fel.puskapet.chess.model.Board;
import cz.cvut.fel.puskapet.chess.model.Color;

import java.util.HashSet;

/**
 * @author puskapet
 */
public class Pawn extends Piece {

    /**
     * Constructor
     *
     * @param row
     * @param column
     * @param color
     * @param firstMove
     */
    public Pawn(Integer row, Integer column, Color color, boolean firstMove) {
        super(row, column, PieceType.PAWN, color, firstMove);
        if (!((color.equals(Color.WHITE) && row == 1) || color.equals(Color.BLACK) && row == 6))
            this.firstMove = false;
    }

    /**
     * Copy constructor
     *
     * @param pawn
     */
    public Pawn(Pawn pawn) {
        super(pawn);
    }

    /**
     * Gets all potential moves according to rules of chess
     *
     * @return set of all potential moves
     */
    @Override
    public HashSet<String> getPotentialMoves() {
        HashSet<String> potentialMoves = new HashSet<>();
        if (COLOR.equals(Color.BLACK)) {
            potentialMoves.add(convertCoordinatesToString(row - 1, column));
            if (Board.isOnBoard(row - 1, column - 1))
                potentialMoves.add(convertCoordinatesToString(row - 1, column - 1));
            if (Board.isOnBoard(row - 1, column + 1))
                potentialMoves.add(convertCoordinatesToString(row - 1, column + 1));
            if (isFirstMove())
                potentialMoves.add(convertCoordinatesToString(row - 2, column));
        }
        if (COLOR.equals(Color.WHITE)) {
            potentialMoves.add(convertCoordinatesToString(row + 1, column));
            if (Board.isOnBoard(row + 1, column - 1))
                potentialMoves.add(convertCoordinatesToString(row + 1, column - 1));
            if (Board.isOnBoard(row + 1, column + 1))
                potentialMoves.add(convertCoordinatesToString(row + 1, column + 1));
            if (isFirstMove())
                potentialMoves.add(convertCoordinatesToString(row + 2, column));
        }
        return potentialMoves;
    }

    /**
     *
     * @return String representation of the piece in Standard algebraic notation
     */
    @Override
    public String toString() {
        return "P";
    }
}
