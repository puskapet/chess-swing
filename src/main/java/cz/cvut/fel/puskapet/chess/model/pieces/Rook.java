package cz.cvut.fel.puskapet.chess.model.pieces;

import cz.cvut.fel.puskapet.chess.model.Color;

import java.util.HashSet;

/**
 * @author puskapet
 */
public class Rook extends Piece {

    /**
     * Constructor
     *
     * @param row
     * @param column
     * @param color
     * @param firstMove
     */
    public Rook(Integer row, Integer column, Color color, boolean firstMove) {
        super(row, column, PieceType.ROOK, color, firstMove);
        if (color.equals(Color.WHITE)) {
            if (!(getCurrentCoordinates().equals("a1") || getCurrentCoordinates().equals("h1")))
                this.firstMove = false;
        } else {
            if (!(getCurrentCoordinates().equals("a8") || getCurrentCoordinates().equals("h8")))
                this.firstMove = false;
        }
    }

    /**
     * Copy constructor
     *
     * @param rook
     */
    public Rook(Rook rook) {
        super(rook);
    }

    /**
     * Gets all potential moves according to rules of chess
     *
     * @return set of all potential moves
     */
    @Override
    public HashSet<String> getPotentialMoves() {
        HashSet<String> potentialMoves = new HashSet<>();
        for (int c = column + 1; c < 8; ++c){
            potentialMoves.add(convertCoordinatesToString(row, c));
        }
        for (int c = column - 1; c > -1; --c)
            potentialMoves.add(convertCoordinatesToString(row, c));
        for (int r = row - 1; r > -1; --r)
            potentialMoves.add(convertCoordinatesToString(r, column));
        for (int r = row + 1; r < 8; ++r)
            potentialMoves.add(convertCoordinatesToString(r, column));
        return potentialMoves;
    }

    /**
     *
     * @return String representation of the piece in Standard algebraic notation
     */
    @Override
    public String toString() {
        return "R";
    }
}
