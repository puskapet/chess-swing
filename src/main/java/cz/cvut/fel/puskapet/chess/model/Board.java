package cz.cvut.fel.puskapet.chess.model;


import cz.cvut.fel.puskapet.chess.model.pieces.*;

import java.util.*;

/**
 * @author puskapet
 */
public class Board {

    private final Integer MAX_PIECES_PER_PLAYER = 16;
    private final Integer NUMBER_OF_SQUARES = 64;

    private String enPassantSquare = "";
    private String fenRepresentation = "";
    private Color turnColor = Color.WHITE;
    private Integer moveNumber = 1;

    private HashMap<String, Piece> chessboard = new HashMap<>(NUMBER_OF_SQUARES);
    private Vector<Piece> blackPieces = new Vector<>(MAX_PIECES_PER_PLAYER);
    private Vector<Piece> whitePieces = new Vector<>(MAX_PIECES_PER_PLAYER);

    /**
     * Constructor, fills hashmap with algebraic notation of all the squares
     */
    public Board() {
        enPassantSquare = "";
        for (char c = 'a'; c <= 'h'; ++c) {
            for (char ch = '1'; ch <= '8'; ++ch) {
                chessboard.put("" + c + ch, null);
            }
        }
    }

    /**
     * Copy constructor
     *
     * @param board
     */
    public Board(Board board) {
        for (Piece p : board.blackPieces) {
            switch (p.getPieceType()) {
                case KING:
                    this.blackPieces.add(new King((King) p));
                    break;
                case PAWN:
                    this.blackPieces.add(new Pawn((Pawn) p));
                    break;
                case BISHOP:
                    this.blackPieces.add(new Bishop((Bishop) p));
                    break;
                case ROOK:
                    this.blackPieces.add(new Rook((Rook) p));
                    break;
                case QUEEN:
                    this.blackPieces.add(new Queen((Queen) p));
                    break;
                case KNIGHT:
                    this.blackPieces.add(new Knight((Knight) p));
                    break;
            }
        }

        for (Piece p : board.whitePieces) {
            switch (p.getPieceType()) {
                case KING:
                    this.whitePieces.add(new King((King) p));
                    break;
                case PAWN:
                    this.whitePieces.add(new Pawn((Pawn) p));
                    break;
                case BISHOP:
                    this.whitePieces.add(new Bishop((Bishop) p));
                    break;
                case ROOK:
                    this.whitePieces.add(new Rook((Rook) p));
                    break;
                case QUEEN:
                    this.whitePieces.add(new Queen((Queen) p));
                    break;
                case KNIGHT:
                    this.whitePieces.add(new Knight((Knight) p));
                    break;
            }
        }

        for (Piece p : whitePieces) {
            this.chessboard.put(p.getCurrentCoordinates(), p);
        }
        for (Piece p : blackPieces) {
            this.chessboard.put(p.getCurrentCoordinates(), p);
        }
        this.moveNumber = board.moveNumber;
        this.turnColor = board.turnColor;
        this.fenRepresentation = board.fenRepresentation;
        this.enPassantSquare = board.enPassantSquare;
    }

    /**
     * Constructor to get board from provided FEN representation
     *
     * @param FEN
     */
    public Board(String FEN) {
        for (char c = 'a'; c <= 'h'; ++c) {
            for (char ch = '1'; ch <= '8'; ++ch) {
                chessboard.put("" + c + ch, null);
            }
        }
        parseFEN(FEN);
    }

    private void parseFEN(String FEN) {
        StringTokenizer fenTokenizer = new StringTokenizer(FEN, " ");
        if (fenTokenizer.countTokens() != 6)
            throw new RuntimeException("Wrong amount of fields");
        String pos = fenTokenizer.nextToken();
        StringTokenizer rows = new StringTokenizer(pos, "/");
        if (rows.countTokens() != 8)
            throw new RuntimeException("Wrong amount of rows");
        for (int row = 7; row >= 0; row--) {
            String rowString = rows.nextToken();
            int col = 0;
            for (int i = 0; i < rowString.length(); i++) {
                if (col > 7)
                    throw new RuntimeException("Row" + row + " extends beyond board");
                char c = rowString.charAt(i);
                if (Character.isDigit(c)) {
                    int emptyCols = Character.digit(c, 10);
                    while (emptyCols-- > 0) {
                        col++;
                    }
                } else {
                    setPieceAtCoordinates(String.valueOf(c), row, col);
                    col++;
                }
            }
            if (col != 8) throw new RuntimeException("Row" + row + "is a few columns short");
        }

        String colorToMove = fenTokenizer.nextToken();
        if (colorToMove.length() != 1)
            throw new RuntimeException("Wrong amount of characters in active color indicator: " + colorToMove);
        if (colorToMove.equals("w"))
            turnColor = Color.WHITE;
        else if (colorToMove.equals("b"))
            turnColor = Color.BLACK;
        else throw new RuntimeException("Wrong active color indicator: " + colorToMove);

        String castlingOptions = fenTokenizer.nextToken();

        if (!castlingOptions.equals("-")) {
          if (castlingOptions.matches("K?Q?k?q?")) {
          for (int i = 0; i < castlingOptions.length(); i++) {
              if (!castlingOptions.contains("K")) {
                  if (!squareIsEmpty("h1")) {
                      chessboard.get("h1").setFirstMove();
                  }
              }
              if (!castlingOptions.contains("Q")) {
                  if (!squareIsEmpty("a1")) {
                      chessboard.get("a1").setFirstMove();
                  }
              }
              if (!castlingOptions.contains("k")) {
                  if (!squareIsEmpty("h8")) {
                      chessboard.get("h8").setFirstMove();
                  }
              }
              if (!castlingOptions.contains("q")) {
                  if (!squareIsEmpty("a8")) {
                      chessboard.get("a8").setFirstMove();
                  }
              }
          }
          } else throw new RuntimeException("Wrong castling options" + castlingOptions);
        }

        String enPassant = fenTokenizer.nextToken();
        if (enPassant.equals("-")) enPassantSquare = "";
        else if (isOnBoard(enPassant)) enPassantSquare = enPassant;
        else throw new RuntimeException("Wrong enPassant square" + enPassant);

        String halfMove = fenTokenizer.nextToken();

        String moveCount = fenTokenizer.nextToken();
        this.moveNumber = Integer.parseInt(moveCount);

        this.fenRepresentation = FEN;
    }

    /**
     * Gets FEN representation of the current board
     *
     * @return FEN string
     */
    public String getFenRepresentation() {
        StringBuilder sb = new StringBuilder();
        for (int row = 7; row >= 0; row--) {
            int emptyCols = 0;
            for (int col = 0; col <= 7; col++) {
                Piece piece = chessboard.get(Piece.convertCoordinatesToString(row, col));
                if (piece == null) emptyCols++;
                else {
                    if (emptyCols != 0) {
                        sb.append(emptyCols);
                    }
                    emptyCols = 0;
                    String c = piece.getColor().equals(Color.BLACK) ? piece.toString().toLowerCase() : piece.toString();
                    sb.append(c);
                }
                if (col == 7 && emptyCols != 0) sb.append(emptyCols);
            }
            if (row != 0) sb.append("/");
        }

        sb.append(" ");
        sb.append(turnColor.equals(Color.WHITE) ? "w" : "b");
        sb.append(" ");
        sb.append(getCastlingOptions());
        sb.append(" ");
        if (enPassantSquare.equals("")) sb.append("-");
        else sb.append(enPassantSquare);
        sb.append(" ");
        sb.append(0);
        sb.append(" ");
        sb.append(moveNumber);
        return sb.toString();
    }

    private String getCastlingOptions() {
        boolean whiteK = false, whiteQ = false, blackK = false, blackQ = false;
        Piece e1 = chessboard.get("e1");
        Piece h1 = chessboard.get("h1");
        Piece a1 = chessboard.get("a1");
        Piece e8 = chessboard.get("e8");
        Piece h8 = chessboard.get("h8");
        Piece a8 = chessboard.get("a8");
        if (e1 != null && e1.getPieceType().equals(PieceType.KING) && e1.isFirstMove()) {
            if (h1 != null && h1.getPieceType().equals(PieceType.ROOK) && h1.isFirstMove())
                whiteK = true;
            if (a1 != null && a1.getPieceType().equals(PieceType.ROOK) && a1.isFirstMove())
                whiteQ = true;
        }
        if (e8 != null && e8.getPieceType().equals(PieceType.KING) && e8.isFirstMove()) {
            if (h8 != null && h8.getPieceType().equals(PieceType.ROOK) && h8.isFirstMove())
                blackK = true;
            if (a8 != null && a8.getPieceType().equals(PieceType.ROOK) && a8.isFirstMove())
                blackQ = true;
        }
        StringBuilder sb = new StringBuilder();
        if (!(whiteK || whiteQ || blackK || blackQ))
            sb.append("-");
        else {
            if (whiteK) sb.append("K");
            if (whiteQ) sb.append("Q");
            if (blackK) sb.append("k");
            if (blackQ) sb.append("q");
        }
        return sb.toString();
    }

    private void setPieceAtCoordinates(String piece, int row, int col) {
        Color pieceColor;
        PieceType pieceType;
        pieceColor = Character.isUpperCase(piece.charAt(0)) ? Color.WHITE : Color.BLACK;
        pieceType = switch (Character.toUpperCase(piece.charAt(0))) {
            case 'R' -> PieceType.ROOK;
            case 'K' -> PieceType.KING;
            case 'Q' -> PieceType.QUEEN;
            case 'P' -> PieceType.PAWN;
            case 'B' -> PieceType.BISHOP;
            case 'N' -> PieceType.KNIGHT;
            default -> null;
        };
        if (pieceType != null) {
            Piece newPiece = switch (pieceType) {
                case QUEEN -> new Queen(row, col, pieceColor, false);
                case BISHOP -> new Bishop(row, col, pieceColor, false);
                case KING -> new King(row, col, pieceColor, true);
                case ROOK -> new Rook(row, col, pieceColor, true);
                case KNIGHT -> new Knight(row, col, pieceColor, false);
                case PAWN -> new Pawn(row, col, pieceColor, true);
            };
            chessboard.put(newPiece.getCurrentCoordinates(), newPiece);
            switch (pieceColor) {
                case BLACK -> blackPieces.add(newPiece);
                case WHITE -> whitePieces.add(newPiece);
            }
        }
    }


    /**
     * Method to check whether coordinates are on the board
     *
     * @param row
     * @param column
     * @return true if coordinates are on the board, false otherwise
     */
    public static Boolean isOnBoard(Integer row, Integer column) {
        return (row >= 0 && row < 8 && column >= 0 && column < 8);
    }

    /**
     * Method to check whether coordinates are on the board
     *
     * @param coordinates algebraic notation
     * @return true if square is on the board, false otherwise
     */
    public static Boolean isOnBoard(String coordinates) {
        if (coordinates.length() == 2)
            return 'a' <= coordinates.charAt(0) && coordinates.charAt(0) <= 'h'
                    && '1' <= coordinates.charAt(1) && coordinates.charAt(1) <= '8';
        return false;
    }

    /**
     * Method to set board to starting position
     */
    public void startingBoard() {
        moveNumber = 1;
        turnColor = Color.WHITE;
        blackPieces.removeAllElements();
        whitePieces.removeAllElements();
        whitePieces.add(new Rook(0, 0, Color.WHITE, true));
        whitePieces.add(new Knight(0, 1, Color.WHITE, true));
        whitePieces.add(new Bishop(0, 2, Color.WHITE, true));
        whitePieces.add(new Queen(0, 3, Color.WHITE, true));
        whitePieces.add(new King(0, 4, Color.WHITE, true));
        whitePieces.add(new Bishop(0, 5, Color.WHITE, true));
        whitePieces.add(new Knight(0, 6, Color.WHITE, true));
        whitePieces.add(new Rook(0, 7, Color.WHITE, true));
        for (int i = 0; i < 8; i++) {
            whitePieces.add(new Pawn(1, i, Color.WHITE, true));
            blackPieces.add(new Pawn(6, i, Color.BLACK, true));
        }
        blackPieces.add(new Rook(7, 0, Color.BLACK, true));
        blackPieces.add(new Knight(7, 1, Color.BLACK, true));
        blackPieces.add(new Bishop(7, 2, Color.BLACK, true));
        blackPieces.add(new Queen(7, 3, Color.BLACK, true));
        blackPieces.add(new King(7, 4, Color.BLACK, true));
        blackPieces.add(new Bishop(7, 5, Color.BLACK, true));
        blackPieces.add(new Knight(7, 6, Color.BLACK, true));
        blackPieces.add(new Rook(7, 7, Color.BLACK, true));

        for (Piece piece : whitePieces) {
            chessboard.put(piece.getCurrentCoordinates(), piece);
        }
        for (Piece piece : blackPieces) {
            chessboard.put(piece.getCurrentCoordinates(), piece);
        }
    }

    /**
     * Method to check if the square is empty
     *
     * @param square
     * @return false if square is occupied, true otherwise
     */
    public boolean squareIsEmpty(String square) {
        if (chessboard.containsKey(square)) {
            return chessboard.get(square) == null;
        }
        return true;
    }

    /**
     * Getter
     *
     * @return
     */
    public Vector<Piece> getBlackPieces() {
        return blackPieces;
    }

    /**
     * Getter
     *
     * @return
     */
    public Vector<Piece> getWhitePieces() {
        return whitePieces;
    }

    /**
     * Getter
     *
     * @param color
     * @return
     */
    public Vector<Piece> getPieces(Color color) {
        return color.equals(Color.WHITE) ? getWhitePieces() : getBlackPieces();
    }

    /**
     * Getter
     *
     * @return
     */
    public HashMap<String, Piece> getChessboard() {
        return chessboard;
    }

    /**
     * Getter
     *
     * @return
     */
    public String getEnPassantSquare() {
        return enPassantSquare;
    }

    /**
     * Setter
     *
     * @param enPassantSquare
     */
    public void setEnPassantSquare(String enPassantSquare) {
        this.enPassantSquare = enPassantSquare;
    }

    /**
     * Setter
     *
     * @param turnColor
     */
    public void setTurnColor(Color turnColor) {
        this.turnColor = turnColor;
    }

    /**
     * Getter
     *
     * @return
     */
    public Color getTurnColor() {
        return turnColor;
    }

    /**
     * Setter
     *
     * @param moveNumber
     */
    public void setMoveNumber(Integer moveNumber) {
        this.moveNumber = moveNumber;
    }

    /**
     * Getter
     *
     * @return
     */
    public Integer getMoveNumber() {
        return moveNumber;
    }
}
