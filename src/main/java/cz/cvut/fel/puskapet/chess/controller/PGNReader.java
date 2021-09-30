package cz.cvut.fel.puskapet.chess.controller;

import cz.cvut.fel.puskapet.chess.model.pieces.King;
import cz.cvut.fel.puskapet.chess.model.pieces.Knight;
import cz.cvut.fel.puskapet.chess.model.pieces.Piece;
import cz.cvut.fel.puskapet.chess.model.pieces.PieceType;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * @author puskapet
 */
public class PGNReader {

    private static final String KNIGHT = "N";
    private static final String BISHOP = "B";
    private static final String KING = "K";
    private static final String QUEEN = "Q";
    private static final String ROOK = "R";
    private static final String QUEEN_SIDE_CASTLE = "O-O-O";
    private static final String KING_SIDE_CASTLE = "O-O";
    private static final Pattern tagPattern = Pattern.compile("\\[\\s*.*\\s*\".*\"\\s+\\]");
    private String filePath;

    public PGNReader(String filePath) {
        this.filePath = filePath;
    }

    public Game parseGame() throws IOException {
        Game game = new Game();
        String line;
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        StringTokenizer moves;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("[")) {

            } else {
                if (!line.isEmpty()) {
                    moves = new StringTokenizer(line, " ");
                    while (moves.hasMoreTokens()) {
                        String token = moves.nextToken();
                        if (token.matches("[0-9]+\\.")) {

                        } else {
                            System.out.println(token);
                            parseMove(token, game);
                        }
                    }
                }

            }
        }
        game.setEnded(true);
        return game;
    }

    private boolean isCastling(String move) {
        return move.contains(KING_SIDE_CASTLE);
    }

    private boolean isPromotion(String move) {
        return move.contains("=");
    }

    private void parseMove(String move, Game game) {
        if (isCastling(move)) {
            if (move.contains(QUEEN_SIDE_CASTLE)) {
                switch (game.getTurn()) {
                    case WHITE -> game.makeMove("e1", "c1");
                    case BLACK -> game.makeMove("e8", "c8");
                }
            } else {
                switch (game.getTurn()) {
                    case WHITE -> game.makeMove("e1", "g1");
                    case BLACK -> game.makeMove("e8", "g8");
                }
            }
            return;
        }
        if (isPromotion(move)) {
            try {
                String promotingPiece = move.substring(move.indexOf("=") + 1);
                PieceType promote =
                        switch (promotingPiece) {
                            case KNIGHT -> PieceType.KNIGHT;
                            case ROOK -> PieceType.ROOK;
                            case QUEEN -> PieceType.QUEEN;
                            case BISHOP -> PieceType.BISHOP;
                            default -> null;
                        };
                game.setPromotingPiece(promote);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
        PieceType selectedPiece;
        if (move.startsWith(KNIGHT)) {
            selectedPiece = PieceType.KNIGHT;
        } else if (move.startsWith(ROOK)) {
            selectedPiece = PieceType.ROOK;
        } else if (move.startsWith(QUEEN)) {
            selectedPiece = PieceType.QUEEN;
        } else if (move.startsWith(KING)) {
            selectedPiece = PieceType.KING;
        } else if (move.startsWith(BISHOP)) {
            selectedPiece = PieceType.BISHOP;
        } else {
            selectedPiece = PieceType.PAWN;
        }
        Vector<String> candidates = new Vector<>();
        String mv = "";
        for (Piece p : game.getCurrentBoard().getPieces(game.getTurn())) {
            if (p.getPieceType().equals(selectedPiece)) {
                for (String legalMove : game.getCurrentLegalMovesForPiece(p)) {
                    if (move.contains(legalMove)) {
                        candidates.add(p.getCurrentCoordinates());
                        mv = legalMove;
                    }
                }
            }
        }
        if (candidates.size() == 1) {
        game.makeMove(candidates.firstElement(), mv);
        } else {
            if (selectedPiece.equals(PieceType.PAWN)) {
                for (String candidate: candidates) {
                    if (candidate.substring(0,1).equals(move.substring(0,1))) {
                        game.makeMove(candidate, mv);
                        return;
                    }
                }
            } else {
                String identifier = move.substring(1,2);
                if (identifier.matches("[1-8]")) {
                    for (String candidate : candidates) {
                        if (candidate.substring(1).equals(identifier)) {
                            game.makeMove(candidate, mv);
                            return;
                        }
                    }
                } else if (identifier.matches("[a-h]")) {
                    String additional_identifier = move.substring(2,3);
                    if (additional_identifier.matches("[1-8]")) {
                        game.makeMove(identifier + additional_identifier, mv);
                        return;
                    } else {
                        for (String candidate : candidates) {
                            if (candidate.substring(0,1).equals(identifier)) {
                                game.makeMove(candidate, mv);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
}
