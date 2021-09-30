package cz.cvut.fel.puskapet.chess.controller;

import cz.cvut.fel.puskapet.chess.model.Board;
import cz.cvut.fel.puskapet.chess.model.Color;
import cz.cvut.fel.puskapet.chess.model.pieces.Piece;
import cz.cvut.fel.puskapet.chess.model.pieces.PieceType;
import cz.cvut.fel.puskapet.chess.view.GameView;

import java.util.Vector;

/**
 * @author puskapet
 */
public interface GameController {
    void setView(GameView view);
    void newGame();
    void initializeController();
    Board getCurrentBoard();
    void setCurrentBoard(Board board);
    boolean isPromotion(Board board, String fromCoordinates, String destCoordinates);
    Color getTurn();
    void makeMove(String fromCoordinates, String destCoordinates);
    Vector<String> getCurrentLegalMoves();
    boolean isCheckMate();
    boolean isStaleMate();
    Vector<String> getCurrentLegalMovesForPiece(Piece p);
    Color getOppositeColor(Color color);
    void setPromotingPiece(PieceType promotingPiece);
    ChessClock getClock();
    void moveForwardInHistory();
    void moveBackwardInHistory();
    boolean isEnded();
    void setEnded(boolean ended);
    String getResult();
    void setResult(String result);
    Vector<String> getMoves();
    Vector<Board> getGameHistory();
    boolean isCastling(Board board, String fromCoordinates, String destCoordinates);
    boolean isCapture(Board board, String coordinates);
    boolean isKingInCheck(Board board, Color myColor);
    boolean isEnPassant(Board board, String fromCoordinates, String destCoordinates);
    String getEvent();
    String getSite();
    String getWhite();
    String getBlack();
    String getDate();
    String getRound();
}
