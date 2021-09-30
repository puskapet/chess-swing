package cz.cvut.fel.puskapet.chess.model.pieces;

import cz.cvut.fel.puskapet.chess.model.Color;

import java.util.HashSet;

/**
 * @author puskapet
 */

public abstract class Piece {
    protected Integer row;
    protected Integer column;
    protected boolean firstMove;
    protected final Color COLOR;
    protected final PieceType TYPE;

    /**
     * Constructor for Piece
     *
     * @param row value on the board
     * @param column value on the board
     * @param TYPE of the piece
     * @param COLOR  of the piece
     * @param firstMove should be true if piece has not yet moved, false otherwise
     */
    public Piece(Integer row, Integer column, PieceType TYPE, Color COLOR, boolean firstMove) {
        this.row = row;
        this.column = column;
        this.TYPE = TYPE;
        this.COLOR = COLOR;
        this.firstMove = firstMove;
    }

    /**
     * Copy constructor
     *
     * @param piece to make copy of
     */
    public Piece(Piece piece) {
        this.row = piece.row;
        this.column = piece.column;
        this.COLOR = piece.COLOR;
        this.TYPE = piece.TYPE;
        this.firstMove = piece.firstMove;
    }

    /**
     * Converts integer coordinates to algebraic notation
     *
     * @param row
     * @param column
     * @return algebraic notation of square on board
     */
    public static String convertCoordinatesToString(Integer row, Integer column) {
        return String.valueOf((char) (column + 'a')) + (char) (row + '1');
    }

    /**
     * Converts algebraic notation to integer coordinates
     *
     * @param coordinatesToConvert algebraic notation of the coordinates to convert
     * @return array of 2 values, 1st is column, 2nd is row
     */
    public static int[] convertStringToCoordinates(String coordinatesToConvert) {
        int[] coordinates = new int[2];
        coordinates[0] = coordinatesToConvert.charAt(0) - 'a';
        coordinates[1] = coordinatesToConvert.charAt(1) - '1';
        return coordinates;
    }

    /**
     * Getter of the algebraic notation of the current coordinates
     *
     * @return current coordinates
     */
    public String getCurrentCoordinates() {
        return convertCoordinatesToString(row, column);
    }

    /**
     * Getter of the row value from algebraic notation
     *
     * @param coordinates
     * @return
     */
    public static int getRowFromCoordinates(String coordinates) {
        return coordinates.charAt(1) - '1';
    }

    /**
     * Getter of the column value from algrebraic notation
     *
     * @param coordinates
     * @return
     */
    public static int getColumnFromCoordinates(String coordinates) {
        return coordinates.charAt(0) - 'a';
    }

    /**
     * Setter of the currentCoordinates
     *
     * @param coordinates coordinates in algebraic notation
     */
    public void setCurrentCoordinates(String coordinates) {
        this.row = coordinates.charAt(1) - '1';
        this.column = coordinates.charAt(0) - 'a';
    }

    /**
     * Getter of the potential moves of the piece
     *
     * @return
     */
    public abstract HashSet<String> getPotentialMoves();

    /**
     * Getter
     *
     * @return
     */
    public boolean isFirstMove() {
        return firstMove;
    }

    /**
     * Getter of the piece's color
     *
     * @return
     */
    public Color getColor() {
        return COLOR;
    }

    /**
     * Getter of the piece's type
     *
     * @return
     */
    public PieceType getPieceType() {
        return TYPE;
    }

    /**
     * Setter of the piece's first move
     * Sets to false
     */
    public void setFirstMove() {
        firstMove = false;
    }
}
