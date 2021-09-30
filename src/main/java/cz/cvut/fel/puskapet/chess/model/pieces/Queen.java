package cz.cvut.fel.puskapet.chess.model.pieces;

import cz.cvut.fel.puskapet.chess.model.Color;

import java.util.HashSet;

/**
 * @author puskapet
 */
public class Queen extends Piece {
    /**
     * Constructor
     *
     * @param row
     * @param column
     * @param color
     * @param firstMove
     */
    public Queen(Integer row, Integer column, Color color, boolean firstMove) {
        super(row, column, PieceType.QUEEN, color, firstMove);
        this.firstMove = false;
    }

    /**
     * Copy constructor
     *
     * @param queen
     */
    public Queen(Queen queen) {
        super(queen);
    }

    /**
     * Gets all potential moves according to rules of chess
     *
     * @return set of all potential moves
     */
    @Override
    public HashSet<String> getPotentialMoves() {
        HashSet<String> potentialMoves = new HashSet<>();
        Bishop bishop = new Bishop(row, column, this.COLOR, true);
        potentialMoves.addAll(bishop.getPotentialMoves());
        Rook rook = new Rook(row, column, this.COLOR, true);
        potentialMoves.addAll(rook.getPotentialMoves());
        return potentialMoves;
    }

    /**
     *
     * @return String representation of the piece in Standard algebraic notation
     */
    @Override
    public String toString() {
        return "Q";
    }
}
