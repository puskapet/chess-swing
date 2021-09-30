package cz.cvut.fel.puskapet.chess.controller;

import cz.cvut.fel.puskapet.chess.model.Board;
import cz.cvut.fel.puskapet.chess.model.Color;
import cz.cvut.fel.puskapet.chess.model.pieces.Piece;
import cz.cvut.fel.puskapet.chess.model.pieces.PieceType;

import java.util.Vector;
import java.util.logging.Logger;

/**
 * @author puskapet
 */
public class PGNWriter {

    private final String LEFT_BRACKET = "[";
    private String RIGHT_BRACKET_WITH_QUOTES = "\"]";
    private GameController game;
    private final String STARTING_POSITION_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    /**
     * Constructor
     *
     * @param game to generate PGN of
     */
    public PGNWriter(GameController game) {
        this.game = game;
    }

    private String getFenTag() {
        StringBuilder sb = new StringBuilder();
        sb.append(LEFT_BRACKET).append(PGNTagConstants.SETUP_ATTR).append(" \"")
                .append("1").append(RIGHT_BRACKET_WITH_QUOTES).append("\n")
                .append(LEFT_BRACKET).append(PGNTagConstants.FEN_ATTR).append(" \"")
                .append(game.getGameHistory().get(0).getFenRepresentation())
                .append(RIGHT_BRACKET_WITH_QUOTES).append("\n");
        return sb.toString();
    }


    private String getMoves() {
        boolean first = true;
        GameController dummyGame = new Game(new Board(game.getGameHistory().get(0)));
        int index = 0;
        StringBuilder sb = new StringBuilder();
        for (String move : game.getMoves()) {
            if (first) {
                if (dummyGame.getCurrentBoard().getTurnColor().equals(Color.WHITE))
                    sb.append(dummyGame.getCurrentBoard().getMoveNumber()).append(". ");
                else
                    sb.append(dummyGame.getCurrentBoard().getMoveNumber()).append("... ");
            } else {
                if (dummyGame.getCurrentBoard().getTurnColor().equals(Color.WHITE))
                    sb.append(dummyGame.getCurrentBoard().getMoveNumber()).append(". ");
            }
            first = false;
            String from = move.substring(0, 2);
            String dest = move.substring(2, 4);

            if (dummyGame.isCastling(dummyGame.getCurrentBoard(), from, dest)) {
                if (dest.equals("c8") || dest.equals("c1"))
                    sb.append("O-O-O");
                else
                    sb.append("O-O");
            } else {
                if (!dummyGame.getCurrentBoard().getChessboard().get(from).toString().equals("P"))
                sb.append(dummyGame.getCurrentBoard().getChessboard().get(from).toString());
                if (!dummyGame.getCurrentBoard().getChessboard().get(from).getPieceType().equals(PieceType.PAWN)) {
                    String disambiguation;
                    Vector<String> candidates = new Vector<>();
                    for (Piece p : dummyGame.getCurrentBoard().getPieces(dummyGame.getTurn())) {
                        if (p.getPieceType().equals(dummyGame.getCurrentBoard().getChessboard().get(from).getPieceType())) {
                            if (!p.getCurrentCoordinates().equals(from)) {
                                if (dummyGame.getCurrentLegalMovesForPiece(p).contains(dest)) {
                                    candidates.add(p.getCurrentCoordinates());
                                }
                            }
                        }
                    }
                    Logger.getLogger("Candidates").info(candidates.toString());
                    if (candidates.isEmpty()) {
                        disambiguation = "";
                    } else {
                        boolean rankIsEnough = true;
                        for (String coord : candidates) {
                            if (coord.charAt(0) == from.charAt(0))
                                rankIsEnough = false;
                        }
                        if (rankIsEnough)
                            disambiguation = String.valueOf(from.charAt(0));
                        else {
                            boolean fileIsEnough = true;
                            for (String coord : candidates) {
                                if (coord.charAt(1) == from.charAt(1))
                                    fileIsEnough = false;
                            }
                            if (fileIsEnough)
                                disambiguation = String.valueOf(from.charAt(1));
                            else
                                disambiguation = from;
                        }
                    }
                    sb.append(disambiguation);
                }
                if (dummyGame.isCapture(dummyGame.getCurrentBoard(), dest)) {
                    if (dummyGame.getCurrentBoard().getChessboard().get(from).getPieceType().equals(PieceType.PAWN))
                        sb.append(from.charAt(0));
                    sb.append("x");
                }
                if (dummyGame.isEnPassant(dummyGame.getCurrentBoard(), from, dest))
                    sb.append(from.charAt(0)).append("x");
                sb.append(dest);
            }
            if (dummyGame.isPromotion(dummyGame.getCurrentBoard(), from, dest)) {
                sb.append("=");
                sb.append(game.getGameHistory().get(index+2).getChessboard().get(dest).toString());
                dummyGame.setPromotingPiece(game.getGameHistory().get(index+2).getChessboard().get(dest).getPieceType());
            }
            dummyGame.makeMove(from, dest);
            if (dummyGame.isCheckMate()) {
                sb.append("#");
            } else {
                if (dummyGame.isKingInCheck(dummyGame.getCurrentBoard(), dummyGame.getTurn()))
                    sb.append("+");
            }
            sb.append(" ");
            index++;
        }
        sb.append(game.getResult());
        return sb.toString();
    }


   private String getTagSection() {
        StringBuilder sb = new StringBuilder();
        sb.append(LEFT_BRACKET).append(PGNTagConstants.EVENT_ATTR).append(" \"")
                .append(game.getEvent()).append(RIGHT_BRACKET_WITH_QUOTES).append("\n")
                .append(LEFT_BRACKET).append(PGNTagConstants.SITE_ATTR).append(" \"")
                .append(game.getSite()).append(RIGHT_BRACKET_WITH_QUOTES).append("\n")
                .append(LEFT_BRACKET).append(PGNTagConstants.DATE_ATTR).append(" \"")
                .append(game.getDate()).append(RIGHT_BRACKET_WITH_QUOTES).append("\n")
                .append(LEFT_BRACKET).append(PGNTagConstants.ROUND_ATTR).append(" \"")
                .append(game.getRound()).append(RIGHT_BRACKET_WITH_QUOTES).append("\n")
                .append(LEFT_BRACKET).append(PGNTagConstants.WHITE_ATTR).append(" \"")
                .append(game.getWhite()).append(RIGHT_BRACKET_WITH_QUOTES).append("\n")
                .append(LEFT_BRACKET).append(PGNTagConstants.BLACK_ATTR).append(" \"")
                .append(game.getBlack()).append(RIGHT_BRACKET_WITH_QUOTES).append("\n")
                .append(LEFT_BRACKET).append(PGNTagConstants.RESULT_ATTR).append(" \"")
                .append(game.getResult()).append(RIGHT_BRACKET_WITH_QUOTES).append("\n");
        return sb.toString();

    }

    /**
     * Gets PGN file contents
     *
     * @return all PGN file contents
     */
    public String getPGN() {
        StringBuilder sb = new StringBuilder();
        sb.append(getTagSection());
        if (!game.getGameHistory().get(0).getFenRepresentation().equals(STARTING_POSITION_FEN)) {
            sb.append(getFenTag());
        }
        sb.append(getMoves());
        return sb.toString();
    }
}
