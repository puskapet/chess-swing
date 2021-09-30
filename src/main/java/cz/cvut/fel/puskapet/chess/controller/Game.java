package cz.cvut.fel.puskapet.chess.controller;

import cz.cvut.fel.puskapet.chess.model.Board;
import cz.cvut.fel.puskapet.chess.model.Color;
import cz.cvut.fel.puskapet.chess.model.pieces.*;
import cz.cvut.fel.puskapet.chess.view.GameView;

import java.util.Vector;

/**
 * @author puskapet
 */

public class Game implements GameController {

    private boolean ended;
    private Integer historyIndex = 0;
    private Vector<Board> gameHistory = new Vector<>();
    private Vector<String> moves = new Vector<>();
    private Integer moveNumber;
    private Board currentBoard;
    private GameView gameView;
    private Color turn;
    private Thread chessClock;
    private ChessClock clock;
    private PieceType promotingPiece;
    private String event = "Chess";
    private String site = "PJV";
    private String date = String.format(String.valueOf(java.time.LocalDate.now()).replaceAll("-","."));
    private String white = "player";
    private String black = "player";
    private String round = "1";
    private String result = "*";


    /**
     * Constructor for new game
     */
    public Game() {
        newGame();
    }


    /**
     * Constructor for game from provided board and turn
     *
     * @param board board situation to play from
     *
     */
    public Game(Board board) {
        moveNumber = board.getMoveNumber();
        moves.removeAllElements();
        ended = false;
        historyIndex = 0;
        currentBoard = board;
        this.turn = board.getTurnColor();
        clock = new ChessClock(1, turn);
        chessClock = new Thread(clock);
        chessClock.start();
    }

    /**
     * Initialization of all necessary variables for new game
     */
    @Override
    public void newGame() {
        gameHistory.removeAllElements();
        ended = false;
        moveNumber = 1;
        historyIndex = 0;
        currentBoard = new Board();
        currentBoard.startingBoard();
        turn = Color.WHITE;
        moves.removeAllElements();
        gameHistory.add(currentBoard);
        clock = new ChessClock(1, turn);
        chessClock = new Thread(clock);
        chessClock.start();
    }

    /**
     * Gets all legal moves for all pieces
     *
     * @return  Vector of all legal moves for players turn for currentBoard
     */
    public Vector<String> getCurrentLegalMoves() {
        Vector<String> legalMoves = new Vector<>();
        for (Piece p : currentBoard.getPieces(turn)) {
            for (String move : p.getPotentialMoves()) {
                if (isValidMove(p.getCurrentCoordinates(), move)) {
                    legalMoves.add(p + move);
                }
            }
        }
        return legalMoves;
    }


    /**
     * Gets all legal moves for provided piece
     *
     * @param piece Piece to get legal moves for
     * @return  Vector of all legal moves for provided piece
     */
    public Vector<String> getCurrentLegalMovesForPiece(Piece piece) {
        Vector<String> legalMoves = new Vector<>();
        for (String move : piece.getPotentialMoves()) {
            if (isValidMove(piece.getCurrentCoordinates(), move)) {
                legalMoves.add(move);
            }
        }
        return legalMoves;
    }


    /**
     * Moves one step forward in gameHistory or stays at the most recent
     *
     */
    public void moveForwardInHistory() {
        historyIndex++;
        if (historyIndex < gameHistory.size()) {
            setCurrentBoard(gameHistory.get(historyIndex));
        } else {
            historyIndex = gameHistory.size() - 1;
        }
    }

    /**
     * Moves one step backward in gameHistory or stays at the oldest
     */
    public void moveBackwardInHistory() {
        historyIndex--;
        if (historyIndex >= 0) {
            setCurrentBoard(gameHistory.get(historyIndex));
        } else {
            historyIndex = 0;
        }
    }

    /**
     * Getter for current board
     *
     * @return current board
     */
    public Board getCurrentBoard() {
        return currentBoard;
    }

    /**
     * Getter for clock
     *
     * @return clock
     */
    public ChessClock getClock() {
        return clock;
    }

    /**
     * Validates move on currentBoard
     *
     * @param fromCoordinates  String coordinates of piece's original square
     * @param destCoordinates  String coordinates of piece's destination square
     * @return true if the move is valid, false otherwise
     */

    public boolean isValidMove(String fromCoordinates, String destCoordinates) {
        int[] from = Piece.convertStringToCoordinates(fromCoordinates);
        int[] dest = Piece.convertStringToCoordinates(destCoordinates);
        int fromCol = from[0];
        int fromRow = from[1];
        int destCol = dest[0];
        int destRow = dest[1];
        boolean isValidMove = false;
        boolean hasClearPath = false;
        boolean capture;
        //both coordinates must be on board
        if (Board.isOnBoard(fromRow, fromCol) && Board.isOnBoard(destRow, destCol)) {
            //destination square must not be own piece
            capture = !currentBoard.squareIsEmpty(destCoordinates);

            if (capture) {
                if (currentBoard.getChessboard().get(destCoordinates).getColor().equals(turn)) {
                    return false;
                }
            }

            //selected square must not be empty
            if (!currentBoard.squareIsEmpty(fromCoordinates)) {
                Piece selectedPiece = currentBoard.getChessboard().get(fromCoordinates);
                //selected piece must be same color as turn
                if (selectedPiece.getColor().equals(turn)) {
                    //selected piece must have destination in potential moves
                    if (selectedPiece.getPotentialMoves().contains(destCoordinates)) {
                        switch (selectedPiece.getPieceType()) {
                            case KNIGHT:
                                hasClearPath = true;
                                break;
                            case KING:
                                hasClearPath = true;
                                break;
                            case PAWN:
                                hasClearPath = isValidPawnMove(currentBoard, fromCoordinates, destCoordinates, turn);
                                break;
                            // bishop must have clear diagonal path
                            case BISHOP:
                                hasClearPath = isValidDiagonalPath(currentBoard, fromCoordinates, destCoordinates, turn);
                                break;
                            //rook must have clear horizontal or vertical path
                            case ROOK:
                                hasClearPath = isValidHorizontalPath(currentBoard, fromCoordinates, destCoordinates, turn) ||
                                        isValidVerticalPath(currentBoard, fromCoordinates, destCoordinates, turn);
                                break;
                            //queen must have clear horizontal or diagonal or vertical path
                            case QUEEN:
                                hasClearPath = isValidVerticalPath(currentBoard, fromCoordinates, destCoordinates, turn) ||
                                        isValidHorizontalPath(currentBoard, fromCoordinates, destCoordinates, turn) ||
                                        isValidDiagonalPath(currentBoard, fromCoordinates, destCoordinates, turn);
                                break;
                        }
                    }
                }
            }
        }

        if (hasClearPath) {
            if (isCastling(currentBoard, fromCoordinates, destCoordinates)) {
                isValidMove = switch (destCoordinates) {
                    case "g1" -> isValidWhiteKingSideCastle(currentBoard, fromCoordinates, destCoordinates);
                    case "c1" -> isValidWhiteQueenSideCastle(currentBoard, fromCoordinates, destCoordinates);
                    case "g8" -> isValidBlackKingSideCastle(currentBoard, fromCoordinates, destCoordinates);
                    case "c8" -> isValidBlackQueenSideCastle(currentBoard, fromCoordinates, destCoordinates);
                    default -> false;
                };
            } else {
                Board newBoard = makeMove(currentBoard, fromCoordinates, destCoordinates);
                isValidMove = !isKingInCheck(newBoard, turn);
            }
        }
        return isValidMove;
    }


    private boolean isValidWhiteKingSideCastle(Board board, String fromCoordinates, String destCoordinates) {
        boolean isValid = false;
        Board newBoard = new Board(board);
        if (fromCoordinates.equals("e1") && destCoordinates.equals("g1")) {
            if (!board.squareIsEmpty("e1") && board.squareIsEmpty("f1") && board.squareIsEmpty("g1") && !board.squareIsEmpty("h1")) {
                Piece h1 = board.getChessboard().get("h1");
                Piece e1 = board.getChessboard().get("e1");
                if (e1.isFirstMove() && e1.getColor().equals(Color.WHITE) && e1.getPieceType().equals(PieceType.KING)
                        && h1.isFirstMove() && h1.getColor().equals(Color.WHITE) && h1.getPieceType().equals(PieceType.ROOK)) {
                    boolean e1Check = isKingInCheck(newBoard, Color.WHITE);
                    newBoard = makeMove(newBoard, "e1", "f1");
                    boolean f1Check = isKingInCheck(newBoard, Color.WHITE);
                    newBoard = makeMove(newBoard, "f1", "g1");
                    boolean g1Check = isKingInCheck(newBoard, Color.WHITE);
                    isValid = !e1Check && !f1Check && !g1Check;
                }
            }
        }
        return isValid;
    }

    private boolean isValidWhiteQueenSideCastle(Board board, String fromCoordinates, String destCoordinates) {
        boolean isValid = false;
        Board newBoard = new Board(board);
        if (fromCoordinates.equals("e1") && destCoordinates.equals("c1")) {
            if (!board.squareIsEmpty("e1") && board.squareIsEmpty("d1") && board.squareIsEmpty("c1") && board.squareIsEmpty("b1")
                    && !board.squareIsEmpty("a1")) {
                Piece a1 = board.getChessboard().get("a1");
                Piece e1 = board.getChessboard().get("e1");
                if (e1.isFirstMove() && e1.getColor().equals(Color.WHITE) && e1.getPieceType().equals(PieceType.KING)
                        && a1.isFirstMove() && a1.getColor().equals(Color.WHITE) && a1.getPieceType().equals(PieceType.ROOK)) {
                    boolean e1Check = isKingInCheck(newBoard, Color.WHITE);
                    newBoard = makeMove(newBoard, "e1", "d1");
                    boolean d1Check = isKingInCheck(newBoard, Color.WHITE);
                    newBoard = makeMove(newBoard, "d1", "c1");
                    boolean c1Check = isKingInCheck(newBoard, Color.WHITE);
                    isValid = !e1Check && !d1Check && !c1Check;
                }
            }
        }
        return isValid;
    }

    private boolean isValidBlackKingSideCastle(Board board, String fromCoordinates, String destCoordinates) {
        boolean isValid = false;
        Board newBoard = new Board(board);
        if (fromCoordinates.equals("e8") && destCoordinates.equals("g8")) {
            if (!board.squareIsEmpty("e8") && board.squareIsEmpty("f8") && board.squareIsEmpty("g8") && !board.squareIsEmpty("h8")) {
                Piece h8 = board.getChessboard().get("h8");
                Piece e8 = board.getChessboard().get("e8");
                if (e8.isFirstMove() && e8.getColor().equals(Color.BLACK) && e8.getPieceType().equals(PieceType.KING)
                        && h8.isFirstMove() && h8.getColor().equals(Color.BLACK) && h8.getPieceType().equals(PieceType.ROOK)) {
                    boolean e8Check = isKingInCheck(newBoard, Color.BLACK);
                    newBoard = makeMove(newBoard, "e8", "f8");
                    boolean f8Check = isKingInCheck(newBoard, Color.BLACK);
                    newBoard = makeMove(newBoard, "f8", "g8");
                    boolean g8Check = isKingInCheck(newBoard, Color.BLACK);
                    isValid = !e8Check && !f8Check && !g8Check;
                }
            }
        }
        return isValid;
    }

    private boolean isValidBlackQueenSideCastle(Board board, String fromCoordinates, String destCoordinates) {
        boolean isValid = false;
        Board newBoard = new Board(board);
        if (fromCoordinates.equals("e8") && destCoordinates.equals("c8")) {
            if (!board.squareIsEmpty("e8") && board.squareIsEmpty("d8") && board.squareIsEmpty("c8") && board.squareIsEmpty("b8")
                    && !board.squareIsEmpty("a8")) {
                Piece a8 = board.getChessboard().get("a8");
                Piece e8 = board.getChessboard().get("e8");
                if (e8.isFirstMove() && e8.getColor().equals(Color.BLACK) && e8.getPieceType().equals(PieceType.KING)
                        && a8.isFirstMove() && a8.getColor().equals(Color.BLACK) && a8.getPieceType().equals(PieceType.ROOK)) {
                    boolean e8Check = isKingInCheck(newBoard, Color.BLACK);
                    newBoard = makeMove(newBoard, "e8", "d8");
                    boolean d8Check = isKingInCheck(newBoard, Color.BLACK);
                    newBoard = makeMove(newBoard, "d8", "c8");
                    boolean c8Check = isKingInCheck(newBoard, Color.BLACK);
                    isValid = !e8Check && !d8Check && !c8Check;
                }
            }
        }
        return isValid;
    }

    private Board makeMove(Board board, String fromCoordinates, String destCoordinates) {
        Board newBoard = new Board(board);
        if (isCapture(newBoard, destCoordinates)) {
            removeCapturedPiece(newBoard, destCoordinates);
        }
        newBoard.getChessboard().put(destCoordinates, newBoard.getChessboard().get(fromCoordinates));
        newBoard.getChessboard().put(fromCoordinates, null);
        newBoard.getChessboard().get(destCoordinates).setCurrentCoordinates(destCoordinates);
        newBoard.getChessboard().get(destCoordinates).setFirstMove();
        return newBoard;
    }

    private boolean isPawnFirstMoveAndJump(Board board, String fromCoordinates, String destCoordinates) {
        if (!board.squareIsEmpty(fromCoordinates) &&
                board.getChessboard().get(fromCoordinates).getPieceType().equals(PieceType.PAWN)) {
            return Piece.getColumnFromCoordinates(fromCoordinates) == Piece.getColumnFromCoordinates(destCoordinates) &&
                    Math.abs(Piece.getRowFromCoordinates(fromCoordinates) - Piece.getRowFromCoordinates(destCoordinates)) == 2;
        }
        return false;
    }

    /**
     * Checks for EnPassant
     * @see "https://en.wikipedia.org/wiki/En_passant"
     *
     * @param board board to check on
     * @param fromCoordinates original to move from
     * @param destCoordinates destination coordinates to move to
     * @return true if move is EnPassant, false otherwise
     */
    public boolean isEnPassant(Board board, String fromCoordinates, String destCoordinates) {
        return board.getEnPassantSquare().equals(destCoordinates) &&
                !board.squareIsEmpty(fromCoordinates) &&
                board.getChessboard().get(fromCoordinates).getPieceType().equals(PieceType.PAWN);
    }


    /**
     * Makes move if it is valid
     *
     * @param fromCoordinates original coordinates to move from
     * @param destCoordinates destination coordinates to move to
     */
    public void makeMove(String fromCoordinates, String destCoordinates) {
        if (isValidMove(fromCoordinates, destCoordinates)) {
            clock.startClock();
            currentBoard = new Board(currentBoard);
//            gameHistory.add(currentBoard);
            currentBoard.setTurnColor(getOppositeColor(turn));
            if (turn.equals(Color.WHITE)) moveNumber++;
            currentBoard.setMoveNumber(moveNumber);
            if (isCastling(currentBoard, fromCoordinates, destCoordinates)) {
                currentBoard = switch (destCoordinates) {
                    case "g1" -> whiteCastleKingSide(currentBoard);
                    case "c1" -> whiteCastleQueenSide(currentBoard);
                    case "g8" -> blackCastleKingSide(currentBoard);
                    case "c8" -> blackCastleQueenSide(currentBoard);
                    default -> currentBoard;
                };
                currentBoard.setEnPassantSquare("");
            } else if (isPromotion(currentBoard, fromCoordinates, destCoordinates)) {
                currentBoard = promoteToPiece(currentBoard, fromCoordinates, destCoordinates);
                currentBoard.setEnPassantSquare("");
            } else if (isEnPassant(currentBoard, fromCoordinates, destCoordinates)) {
                Integer row = Piece.getRowFromCoordinates(destCoordinates);
                Integer column = Piece.getColumnFromCoordinates(destCoordinates);
                row = (row == 5 ? 4 : 3);
                removeCapturedPiece(currentBoard, Piece.convertCoordinatesToString(row, column));
                currentBoard = makeMove(currentBoard, fromCoordinates, destCoordinates);
                currentBoard.setEnPassantSquare("");
            } else {
                if (isPawnFirstMoveAndJump(currentBoard, fromCoordinates, destCoordinates)) {
                    Integer row = Piece.getRowFromCoordinates(fromCoordinates);
                    Integer column = Piece.getColumnFromCoordinates(fromCoordinates);
                    if (currentBoard.getChessboard().get(fromCoordinates).getColor().equals(Color.WHITE)) {
                        currentBoard.setEnPassantSquare(Piece.convertCoordinatesToString(row + 1, column));
                    } else {
                        currentBoard.setEnPassantSquare(Piece.convertCoordinatesToString(row - 1, column));
                    }
                } else {
                    currentBoard.setEnPassantSquare("");
                }
                currentBoard = makeMove(currentBoard, fromCoordinates, destCoordinates);
            }
            turn = getOppositeColor(turn);
            clock.switchTurn();
            gameHistory.add(currentBoard);
            moves.add(fromCoordinates + destCoordinates);
            historyIndex = gameHistory.size() - 1;
        }
    }

    /**
     * Checks for promotion
     * @see "https://en.wikipedia.org/wiki/Promotion_(chess)"
     *
     * @param board board to check promotion on
     * @param fromCoordinates original coordinates to move from
     * @param destCoordinates destination coordinates to move to
     * @return true if it is promotion, false otherwise
     */
    public boolean isPromotion(Board board, String fromCoordinates, String destCoordinates) {
        if (!board.squareIsEmpty(fromCoordinates)) {
            if (board.getChessboard().get(fromCoordinates).getPieceType().equals(PieceType.PAWN)) {
                if (board.getChessboard().get(fromCoordinates).getColor().equals(Color.WHITE)) {
                    return Piece.getRowFromCoordinates(fromCoordinates) == 6 && Piece.getRowFromCoordinates(destCoordinates) == 7;
                }
                if (board.getChessboard().get(fromCoordinates).getColor().equals(Color.BLACK)) {
                    return Piece.getRowFromCoordinates(fromCoordinates) == 1 && Piece.getRowFromCoordinates(destCoordinates) == 0;
                }
            }
        }
        return false;
    }

    private Board promoteToPiece(Board board, String fromCoordinates, String destCoordinates) {
        Board newBoard = makeMove(board, fromCoordinates, destCoordinates);
        if (Piece.getRowFromCoordinates(destCoordinates) == 7) {
            newBoard.getWhitePieces().remove(newBoard.getChessboard().get(destCoordinates));
            Piece p = switch (promotingPiece) {
                case ROOK -> new Rook(0, 0, Color.WHITE, false);
                case KNIGHT -> new Knight(0, 0, Color.WHITE, false);
                case QUEEN -> new Queen(0, 0, Color.WHITE, false);
                case BISHOP -> new Bishop(0, 0, Color.WHITE, false);
                default -> null;
            };
            newBoard.getChessboard().put(destCoordinates, p);
            newBoard.getWhitePieces().add(newBoard.getChessboard().get(destCoordinates));
        } else if (Piece.getRowFromCoordinates(destCoordinates) == 0) {
            newBoard.getBlackPieces().remove(newBoard.getChessboard().get(destCoordinates));
            Piece p = switch (promotingPiece) {
                case ROOK -> new Rook(0, 0, Color.BLACK, false);
                case KNIGHT -> new Knight(0, 0, Color.BLACK, false);
                case QUEEN -> new Queen(0, 0, Color.BLACK, false);
                case BISHOP -> new Bishop(0, 0, Color.BLACK, false);
                default -> null;
            };
            newBoard.getChessboard().put(destCoordinates, p);
            newBoard.getBlackPieces().add(newBoard.getChessboard().get(destCoordinates));
        }
        newBoard.getChessboard().get(destCoordinates).setCurrentCoordinates(destCoordinates);
        return newBoard;
    }

    /**
     * Checks if capture happens on provided coordinates
     * @see "https://en.wikipedia.org/wiki/Glossary_of_chess#Capture"
     *
     * @param board board to check capture on
     * @param coordinates square to check capture on
     * @return true if square is occupied, false if it is not
     */
    public boolean isCapture(Board board, String coordinates) {
        return !board.squareIsEmpty(coordinates);
    }

    /**
     * Checks if the move is castling
     * @see "https://en.wikipedia.org/wiki/Castling"
     *
     * @param board board to check castling on
     * @param fromCoordinates original coordinates to move from
     * @param destCoordinates destination coordinates to move to
     * @return true if move is castling, false otherwise
     */
    public boolean isCastling(Board board, String fromCoordinates, String destCoordinates) {
        if (!board.squareIsEmpty(fromCoordinates)) {
            if (board.getChessboard().get(fromCoordinates).getPieceType().equals(PieceType.KING)) {
                boolean isWhiteCastling = board.getChessboard().get(fromCoordinates).getColor().equals(Color.WHITE) &&
                        fromCoordinates.equals("e1") && (destCoordinates.equals("g1") || destCoordinates.equals("c1"));
                boolean isBlackCastling = board.getChessboard().get(fromCoordinates).getColor().equals(Color.BLACK) &&
                        fromCoordinates.equals("e8") && (destCoordinates.equals("g8") || destCoordinates.equals("c8"));
                return isBlackCastling || isWhiteCastling;
            }
        }
        return false;
    }

    /**
     * Checks whether provided player's King is in check
     * @see "https://en.wikipedia.org/wiki/Check_(chess)"
     *
     * @param board board to check for check on
     * @param myColor color of player
     * @return true if king is attacked for player on board, false otherwise
     */
    public boolean isKingInCheck(Board board, Color myColor) {
        boolean isInCheck = false;
        String kingPosition = "";
        for (Piece p : board.getPieces(myColor)) {
            if (p.getPieceType().equals(PieceType.KING)) {
                kingPosition = p.getCurrentCoordinates();
            }
        }
        if (getOppositeColor(myColor).equals(Color.WHITE)) {
            for (Piece p : board.getWhitePieces()) {
                if (p.getPotentialMoves().contains(kingPosition)) {
                    isInCheck = switch (p.getPieceType()) {
                        case PAWN, BISHOP -> isValidDiagonalPath(board, p.getCurrentCoordinates(), kingPosition, getOppositeColor(myColor));
                        case ROOK -> isValidHorizontalPath(board, p.getCurrentCoordinates(), kingPosition, getOppositeColor(myColor))
                                || isValidVerticalPath(board, p.getCurrentCoordinates(), kingPosition, getOppositeColor(myColor));
                        case KNIGHT, KING -> true;
                        case QUEEN -> isValidHorizontalPath(board, p.getCurrentCoordinates(), kingPosition, getOppositeColor(myColor))
                                || isValidVerticalPath(board, p.getCurrentCoordinates(), kingPosition, getOppositeColor(myColor)) ||
                                isValidDiagonalPath(board, p.getCurrentCoordinates(), kingPosition, getOppositeColor(myColor));
                    };
                }
            }
        }
        if (getOppositeColor(myColor).equals(Color.BLACK)) {
            for (Piece p : board.getBlackPieces()) {
                if (p.getPotentialMoves().contains(kingPosition)) {
                    isInCheck = switch (p.getPieceType()) {
                        case PAWN, BISHOP -> isValidDiagonalPath(board, p.getCurrentCoordinates(), kingPosition, getOppositeColor(myColor));
                        case ROOK -> isValidHorizontalPath(board, p.getCurrentCoordinates(), kingPosition, getOppositeColor(myColor))
                                || isValidVerticalPath(board, p.getCurrentCoordinates(), kingPosition, getOppositeColor(myColor));
                        case KNIGHT, KING -> true;
                        case QUEEN -> isValidHorizontalPath(board, p.getCurrentCoordinates(), kingPosition, getOppositeColor(myColor))
                                || isValidVerticalPath(board, p.getCurrentCoordinates(), kingPosition, getOppositeColor(myColor)) ||
                                isValidDiagonalPath(board, p.getCurrentCoordinates(), kingPosition, getOppositeColor(myColor));
                    };
                }
            }
        }
        return isInCheck;
    }

    /**
     * Checks for checkmate
     * @see "https://en.wikipedia.org/wiki/Checkmate"
     *
     * @return true if it is checkmate for currentBoard
     */
    public boolean isCheckMate() {
        return getCurrentLegalMoves().size() == 0 && isKingInCheck(currentBoard, turn);
    }


    /**
     * Checks for stalemate
     * @see "https://en.wikipedia.org/wiki/Stalemate"
     *
     * @return true if it is stalemate for currentBoard
     */
    public boolean isStaleMate() {
        return getCurrentLegalMoves().size() == 0 && !isKingInCheck(currentBoard, turn);
    }


    private boolean isValidVerticalPath(Board board, String fromCoordinates, String destCoordinates,
                                        Color myColor) {
        int[] from = Piece.convertStringToCoordinates(fromCoordinates);
        int[] dest = Piece.convertStringToCoordinates(destCoordinates);
        int fromCol = from[0];
        int fromRow = from[1];
        int destCol = dest[0];
        int destRow = dest[1];

        //must be same column
        if (fromCol != destCol) {
            return false;
        }

        //destination must not have my piece
        if (!board.squareIsEmpty(destCoordinates)) {
            if (board.getChessboard().get(destCoordinates).getColor().equals(myColor)) {
                return false;
            }
        }
        int startRow, endRow;
        if (destRow < fromRow) {
            startRow = destRow;
            endRow = fromRow;
        } else {
            startRow = fromRow;
            endRow = destRow;
        }

        //squares on the path must be empty
        for (int row = startRow + 1; row < endRow; ++row) {
            if (!board.squareIsEmpty(Piece.convertCoordinatesToString(row, fromCol))) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidHorizontalPath(Board board, String fromCoordinates, String destCoordinates, Color myColor) {
        int[] from = Piece.convertStringToCoordinates(fromCoordinates);
        int[] dest = Piece.convertStringToCoordinates(destCoordinates);
        int fromCol = from[0];
        int fromRow = from[1];
        int destCol = dest[0];
        int destRow = dest[1];

        //must be same row
        if (fromRow != destRow) {
            return false;
        }

        //destination must not have my piece
        if (!board.squareIsEmpty(destCoordinates)) {
            if (board.getChessboard().get(destCoordinates).getColor().equals(myColor)) {
                return false;
            }
        }

        int startCol, endCol;
        if (destCol < fromCol) {
            startCol = destCol;
            endCol = fromCol;
        } else {
            startCol = fromCol;
            endCol = destCol;
        }

        //squares on the path must be empty
        for (int col = startCol + 1; col < endCol; ++col) {
            if (!board.squareIsEmpty(Piece.convertCoordinatesToString(fromRow, col))) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidDiagonalPath(Board board, String fromCoordinates, String destCoordinates, Color myColor) {
        int[] from = Piece.convertStringToCoordinates(fromCoordinates);
        int[] dest = Piece.convertStringToCoordinates(destCoordinates);
        int fromCol = from[0];
        int fromRow = from[1];
        int destCol = dest[0];
        int destRow = dest[1];

        //squares on same diagonal have same difference of row and column
        if (Math.abs(destCol - fromCol) != Math.abs(destRow - fromRow)) {
            return false;
        }

        //destination must not have my piece
        if (!board.squareIsEmpty(destCoordinates)) {
            if (board.getChessboard().get(destCoordinates).getColor().equals(myColor)) {
                return false;
            }
        }

        int directionRow = 1, directionCol = 1;
        if (destCol < fromCol)
            directionCol = -1;
        if (destRow < fromRow)
            directionRow = -1;
        int row = fromRow, col = fromCol;
        row += directionRow;
        col += directionCol;
        while (!Piece.convertCoordinatesToString(row, col).equals(destCoordinates)) {
            if (!board.squareIsEmpty(Piece.convertCoordinatesToString(row, col))) {
                return false;
            }
            row += directionRow;
            col += directionCol;

        }

        return true;
    }

    private boolean isValidPawnMove(Board board, String fromCoordinates, String destCoordinates, Color turn) {
        int[] from = Piece.convertStringToCoordinates(fromCoordinates);
        int[] dest = Piece.convertStringToCoordinates(destCoordinates);
        int fromCol = from[0];
        int fromRow = from[1];
        int destCol = dest[0];
        int destRow = dest[1];
        if (destCol == fromCol) {
            if (Math.abs(fromRow - destRow) == 2) {
                return board.squareIsEmpty(destCoordinates) && board.squareIsEmpty(Piece.convertCoordinatesToString(fromRow +
                        (turn.equals(Color.WHITE) ? 1 : -1), fromCol));
            } else {
                return board.squareIsEmpty(destCoordinates);
            }
        } else {
            return !board.squareIsEmpty(destCoordinates) || board.getEnPassantSquare().equals(destCoordinates);
        }
    }

    /**
     * Setter of the promotingPiece
     *
     * @param promotingPiece type of piece to promote to
     */
    public void setPromotingPiece(PieceType promotingPiece) {
        this.promotingPiece = promotingPiece;
    }


    private Board whiteCastleKingSide(Board board) {
        return makeMove(makeMove(board, "e1", "g1"), "h1", "f1");
    }

    private Board whiteCastleQueenSide(Board board) {
        return makeMove(makeMove(board, "e1", "c1"), "a1", "d1");
    }

    private Board blackCastleKingSide(Board board) {
        return makeMove(makeMove(board, "e8", "g8"), "h8", "f8");
    }

    private Board blackCastleQueenSide(Board board) {
        return makeMove(makeMove(board, "e8", "c8"), "a8", "d8");
    }

    private void removeCapturedPiece(Board board, String coordinates) {
            if (board.getChessboard().get(coordinates).getColor().equals(Color.WHITE)) {
                board.getWhitePieces().remove(board.getChessboard().get(coordinates));
            } else {
                board.getBlackPieces().remove(board.getChessboard().get(coordinates));
            }
            board.getChessboard().put(coordinates, null);
    }

    /**
     * Getter of the current turn
     *
     * @return Color of current player's turn
     */
    public Color getTurn() {
        return turn;
    }

    /**
     * Getter of the color of the opponent's pieces
     *
     * @param color
     * @return Black if color is White, White if color is Black
     */
    public Color getOppositeColor(Color color) {
        return color.equals(Color.BLACK) ? Color.WHITE : Color.BLACK;
    }

    /**
     * Setter of view
     *
     * @param view
     */
    @Override
    public void setView(GameView view) {
        this.gameView = view;
    }

    /**
     * Setter of currentBoard
     *
     * @param board to set currentBoard to
     */
    @Override
    public void setCurrentBoard(Board board) {
        currentBoard = board;
    }



    /**
     * Getter of ended
     *
     * @return true if game has ended, false otherwise
     */
    public boolean isEnded() {
        return ended;
    }

    /**
     * Setter of ended
     *
     * @param ended boolean value to set to
     */
    public void setEnded(boolean ended) {
        this.ended = ended;
    }

    @Override
    public void initializeController() {
        this.gameView.initializeView();
    }


    /**
     * Getter of the result of the game
     *
     * @return current result of the game
     */
    public String getResult() {
        return result;
    }

    /**
     * Setter of the result
     *
     * @param result result to set to
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * Getter of the history of the moves that have been made
     *
     * @return Vector of String moves
     */
    public Vector<String> getMoves() {
        return moves;
    }

    /**
     * Getter of the gameHistory
     *
     * @return Vector of Boards
     */
    public Vector<Board> getGameHistory() {
        return gameHistory;
    }

    /**
     * Getter for PGN
     *
     * @return
     */
    public String getEvent() {
        return event;
    }

    /**
     * Getter for PGN
     *
     * @return
     */
    public String getSite() {
        return site;
    }

    /**
     * Getter for PGN
     *
     * @return
     */
    public String getDate() {
        return date;
    }

    /**
     * Getter for PGN
     *
     * @return
     */
    public String getWhite() {
        return white;
    }

    /**
     * Getter for PGN
     *
     * @return
     */
    public String getBlack() {
        return black;
    }

    /**
     * Getter for PGN
     *
     * @return
     */
    public String getRound() {
        return round;
    }
}
