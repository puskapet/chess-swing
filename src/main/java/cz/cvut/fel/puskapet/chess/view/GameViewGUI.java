package cz.cvut.fel.puskapet.chess.view;

import cz.cvut.fel.puskapet.chess.controller.Game;
import cz.cvut.fel.puskapet.chess.controller.GameController;
import cz.cvut.fel.puskapet.chess.controller.PGNReader;
import cz.cvut.fel.puskapet.chess.controller.PGNWriter;
import cz.cvut.fel.puskapet.chess.model.Board;
import cz.cvut.fel.puskapet.chess.model.pieces.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author puskapet
 */
public class GameViewGUI extends JFrame implements GameView {


    private PGNWriter writer;
    private final ImageIcon blackKing = new ImageIcon(getClass().getResource("/BlackKing.png"));
    private final ImageIcon blackQueen = new ImageIcon(getClass().getResource("/BlackQueen.png"));
    private final ImageIcon blackBishop = new ImageIcon(getClass().getResource("/BlackBishop.png"));
    private final ImageIcon blackKnight = new ImageIcon(getClass().getResource( "/BlackKnight.png"));
    private final ImageIcon blackRook = new ImageIcon(getClass().getResource( "/BlackRook.png"));
    private final ImageIcon blackPawn = new ImageIcon(getClass().getResource( "/BlackPawn.png"));
    private final ImageIcon whiteKing = new ImageIcon(getClass().getResource( "/WhiteKing.png"));
    private final ImageIcon whiteQueen = new ImageIcon(getClass().getResource("/WhiteQueen.png"));
    private final ImageIcon whiteBishop = new ImageIcon(getClass().getResource( "/WhiteBishop.png"));
    private final ImageIcon whiteKnight = new ImageIcon(getClass().getResource( "/WhiteKnight.png"));
    private final ImageIcon whiteRook = new ImageIcon(getClass().getResource( "/WhiteRook.png"));
    private final ImageIcon whitePawn = new ImageIcon(getClass().getResource( "/WhitePawn.png"));

    private final GameView g = this;
    private GameController game;
    private final Integer NUMBER_OF_SQUARES = 64;
    private final Integer BOARD_SIDE = 8;
    private final Integer SQUARE_SIZE = 60;
    private final Color DARK_SQUARE = new Color(147, 112, 219);
    private final Color LIGHT_SQUARE = new Color(239, 239, 239);
    private final Color LIGHT_SQUARE_SELECTED = new Color(173, 216, 230);
    private final Color DARK_SQUARE_SELECTED = new Color(65, 114, 220);
    private final Color DARK_SQUARE_VALID = new Color(65, 148, 220);
    private final Color LIGHT_SQUARE_VALID = new Color(129, 167, 248);
    private HashMap<String, JButton> squares = new HashMap<>(NUMBER_OF_SQUARES);
    private HashMap<String, JButton> editorSquares = new HashMap<>(NUMBER_OF_SQUARES);
    private GridLayout chessboardLayout = new GridLayout(BOARD_SIDE, BOARD_SIDE);
    private JPanel editorChessboard;
    private JPanel chessboard;
    private JMenuBar optionsBar = new JMenuBar();
    private JMenu optionsMenu = new JMenu("Options");
    private JButton editorDone = new JButton("Done");
    private JMenuItem newGame = new JMenuItem("New Game");
    private JMenuItem editBoard = new JMenuItem("Edit Board");
    private JMenuItem exportPGN = new JMenuItem("Export as PGN");
    private JMenuItem loadPGN = new JMenuItem("Load PGN");
    private JMenuItem loadFEN = new JMenuItem("Load FEN");
    private JMenuItem saveFEN = new JMenuItem("Save as FEN");
    private ActionListener squareHandler = new SquareHandler();
    private ActionListener promotionHandler = new PromotionHandler();
    private JPanel bottomBar = new JPanel();
    private JLabel statusBar = new JLabel();
    private JPanel figurePanel = new JPanel();
    private JLabel whiteTime = new JLabel();
    private JLabel blackTime = new JLabel();
    private Icon currentIcon;
    private JPanel editorView;
    private JPanel gameView;
    private JDialog promotion;
    private String source = "";
    private String target = "";
    private Thread update = new Thread(new ClockView());
    private JButton forward = new JButton("Forward");
    private JButton backward = new JButton("Backward");
    private JRadioButton white = new JRadioButton("White", true);
    private JRadioButton black = new JRadioButton("Black");
    private ButtonGroup group = new ButtonGroup();
    private JPanel top = new JPanel();

    public GameViewGUI(GameController game) {
        this.game = game;
        this.game.setView(this);
    }

    private void initializeBottomBar() {
        bottomBar.setLayout(new FlowLayout());
        bottomBar.setPreferredSize(new Dimension(480, 40));
        bottomBar.add(statusBar);
        bottomBar.add(whiteTime);
        bottomBar.add(blackTime);
        bottomBar.add(forward);
        forward.setBackground(LIGHT_SQUARE);
        forward.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (game.isEnded()) {
                    game.moveForwardInHistory();
                    drawBoard();
                }
            }
        });
        backward.setBackground(LIGHT_SQUARE);
        backward.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (game.isEnded()) {
                    game.moveBackwardInHistory();
                    drawBoard();
                }
            }
        });
        bottomBar.add(backward);
        update.start();
        bottomBar.setVisible(true);
    }

    private void initializeFigurePanel() {
        figurePanel.setLayout(new GridLayout(2, 8));
        figurePanel.setPreferredSize(new Dimension(480, 120));
        ArrayList<JButton> figures = new ArrayList<>();
        figures.add(initializeEditorSidePanelButton(whiteKing));
        figures.add(initializeEditorSidePanelButton(whiteQueen));
        figures.add(initializeEditorSidePanelButton(whiteRook));
        figures.add(initializeEditorSidePanelButton(whiteBishop));
        figures.add(initializeEditorSidePanelButton(whiteKnight));
        figures.add(initializeEditorSidePanelButton(whitePawn));
        figures.add(initializeEditorSidePanelButton(null));
        figures.add(initializeEditorSidePanelButton(null));
        figures.add(initializeEditorSidePanelButton(blackKing));
        figures.add(initializeEditorSidePanelButton(blackQueen));
        figures.add(initializeEditorSidePanelButton(blackRook));
        figures.add(initializeEditorSidePanelButton(blackBishop));
        figures.add(initializeEditorSidePanelButton(blackKnight));
        figures.add(initializeEditorSidePanelButton(blackPawn));
        figures.add(initializeEditorSidePanelButton(null));
        figures.add(initializeEditorSidePanelButton(null));
        for (JButton button : figures) {
            figurePanel.add(button);
        }

    }

    private JButton initializeEditorSidePanelButton(Icon icon) {
        JButton button = new JButton();
        button.setBackground(LIGHT_SQUARE);
        button.setFocusable(false);
        button.setSize(SQUARE_SIZE, SQUARE_SIZE);
        button.setIcon(icon);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JButton but = (JButton) actionEvent.getSource();
                currentIcon = but.getIcon();
            }
        });
        return button;
    }

    private void initializeStatusBar() {
        statusBar.setPreferredSize(new Dimension(80, 40));
    }

    private void initializeOptionsBar() {
        optionsBar.setPreferredSize(new Dimension(480, 40));
        optionsBar.add(optionsMenu);
        newGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                game.newGame();
                game.setView(g);
                drawBoard();
                updateStatusBar();
            }
        });
        optionsMenu.add(newGame);
        GameViewGUI gui = this;
        editBoard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                gameView.setVisible(false);
                editorView.setVisible(true);
                gui.setSize(480, 620);
            }
        });
        optionsMenu.add(editBoard);
        exportPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                writer = new PGNWriter(game);
                System.out.println(writer.getPGN());
                JFileChooser c = new JFileChooser();
                int r = c.showSaveDialog(gui);
                PrintWriter out = null;
                if (r == JFileChooser.APPROVE_OPTION) {
                    try {
                        out = new PrintWriter(c.getCurrentDirectory() + File.separator + c.getSelectedFile().getName());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (out != null) {
                        out.write(writer.getPGN());
                        out.close();
                    }
                }
            }
        });
        optionsMenu.add(exportPGN);
        loadPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser c = new JFileChooser();
                int r = c.showOpenDialog(gui);
                if (r == JFileChooser.APPROVE_OPTION) {
                    String filename = c.getSelectedFile().getName();
                    String path = c.getCurrentDirectory().getPath();
                    PGNReader reader = new PGNReader(path + File.separator + filename);
                    try {
                    game = reader.parseGame();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    game.getClock().stopClock();
                    game.setView(g);
                    drawBoard();
                    updateStatusBar();
                }
            }
        });

        optionsMenu.add(loadPGN);


        saveFEN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser c = new JFileChooser();
                int r = c.showSaveDialog(gui);
                PrintWriter out = null;
                if (r == JFileChooser.APPROVE_OPTION) {
                    try {
                        out = new PrintWriter(c.getCurrentDirectory() + File.separator + c.getSelectedFile().getName());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (out != null) {
                        out.write(game.getCurrentBoard().getFenRepresentation());
                        out.close();
                    }
                }
            }
        });
        optionsMenu.add(saveFEN);

        loadFEN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser c = new JFileChooser();
                int r = c.showOpenDialog(gui);
                if (r == JFileChooser.APPROVE_OPTION) {
                    String filename = c.getSelectedFile().getName();
                    String path = c.getCurrentDirectory().getPath();
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(path + File.separator + filename));
                        String fen = br.readLine();
                        game = new Game(new Board(fen));
                        game.setView(g);
                        drawBoard();
                        updateStatusBar();
                    } catch (FileNotFoundException e) {
                        Logger.getLogger("File").info("File" + filename + path + " not found");
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        optionsMenu.add(loadFEN);


    }

    private void updateStatusBar() {
        if (game.isStaleMate()) {
            statusBar.setText("DRAW!");
            game.getClock().stopClock();
            game.setEnded(true);
            game.setResult("1/2-1/2");
        } else if (game.isCheckMate()) {
            statusBar.setText(game.getOppositeColor(game.getTurn()) + " WINS!");
            game.setResult(game.getTurn().equals(cz.cvut.fel.puskapet.chess.model.Color.WHITE) ? "0-1" : "1-0");
            game.getClock().stopClock();
            game.setEnded(true);
        } else if (game.getClock().getWhiteRemaining() == 0) {
            statusBar.setText("BLACK WINS!");
            game.setResult("0-1");
            game.setEnded(true);
        } else if (game.getClock().getBlackRemaining() == 0) {
            statusBar.setText("WHITE WINS!");
            game.setResult("1-0");
            game.setEnded(true);
        } else
            statusBar.setText(game.getTurn() + "'s turn");
    }

    private void initializeBoard() {
        chessboard = new JPanel();
        chessboard.setLayout(chessboardLayout);
        chessboard.setPreferredSize(new Dimension(480, 480));
        for (char c = '8'; c >= '1'; c--) {
            for (char ch = 'a'; ch <= 'h'; ch++) {
                JButton square = new JButton();
                square.setBackground(isDarkSquare("" + ch + c) ? DARK_SQUARE : LIGHT_SQUARE);
                square.setSize(SQUARE_SIZE, SQUARE_SIZE);
                square.setFocusable(false);
                square.addActionListener(squareHandler);
                squares.put("" + ch + c, square);
                chessboard.add(square);
            }
        }
    }

    private void initializeGameView() {
        gameView = new JPanel();
        gameView.setFocusable(true);
        gameView.setLayout(new BorderLayout());
        initializeOptionsBar();
        gameView.add(optionsBar, BorderLayout.NORTH);
        initializeBoard();
        gameView.add(chessboard, BorderLayout.CENTER);
        initializeStatusBar();
        initializeBottomBar();
        gameView.add(bottomBar, BorderLayout.SOUTH);
    }

    private void initializeEditorView() {
        editorView = new JPanel();
        editorView.setPreferredSize(new Dimension(480, 560));
        editorView.setLayout(new BorderLayout());
        editorDone.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Board board = new Board();
                for (String coordinates : editorSquares.keySet()) {
                    Icon icon = editorSquares.get(coordinates).getIcon();
                    if (icon != null) {
                        Integer row = Piece.getRowFromCoordinates(coordinates);
                        Integer column = Piece.getColumnFromCoordinates(coordinates);
                        if (icon.equals(whiteKing)) {
                            board.getWhitePieces().add(new King(row, column, cz.cvut.fel.puskapet.chess.model.Color.WHITE, true));
                        }
                        if (icon.equals(whiteQueen)) {
                            board.getWhitePieces().add(new Queen(row, column, cz.cvut.fel.puskapet.chess.model.Color.WHITE, true));
                        }
                        if (icon.equals(whiteBishop)) {
                            board.getWhitePieces().add(new Bishop(row, column, cz.cvut.fel.puskapet.chess.model.Color.WHITE, true));
                        }
                        if (icon.equals(whiteKnight)) {
                            board.getWhitePieces().add(new Knight(row, column, cz.cvut.fel.puskapet.chess.model.Color.WHITE, true));
                        }
                        if (icon.equals(whitePawn)) {
                            board.getWhitePieces().add(new Pawn(row, column, cz.cvut.fel.puskapet.chess.model.Color.WHITE, true));
                        }
                        if (icon.equals(whiteRook)) {
                            board.getWhitePieces().add(new Rook(row, column, cz.cvut.fel.puskapet.chess.model.Color.WHITE, true));
                        }
                        if (icon.equals(blackKing)) {
                            board.getBlackPieces().add(new King(row, column, cz.cvut.fel.puskapet.chess.model.Color.BLACK, true));
                        }
                        if (icon.equals(blackQueen)) {
                            board.getBlackPieces().add(new Queen(row, column, cz.cvut.fel.puskapet.chess.model.Color.BLACK, true));
                        }
                        if (icon.equals(blackBishop)) {
                            board.getBlackPieces().add(new Bishop(row, column, cz.cvut.fel.puskapet.chess.model.Color.BLACK, true));
                        }
                        if (icon.equals(blackKnight)) {
                            board.getBlackPieces().add(new Knight(row, column, cz.cvut.fel.puskapet.chess.model.Color.BLACK, true));
                        }
                        if (icon.equals(blackPawn)) {
                            board.getBlackPieces().add(new Pawn(row, column, cz.cvut.fel.puskapet.chess.model.Color.BLACK, true));
                        }
                        if (icon.equals(blackRook)) {
                            board.getBlackPieces().add(new Rook(row, column, cz.cvut.fel.puskapet.chess.model.Color.BLACK, true));
                        }
                        for (Piece p : board.getBlackPieces()) {
                            board.getChessboard().put(p.getCurrentCoordinates(), p);
                        }
                        for (Piece p : board.getWhitePieces()) {
                            board.getChessboard().put(p.getCurrentCoordinates(), p);
                        }
                        for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements(); ) {
                            AbstractButton but = buttons.nextElement();
                            if (but.isSelected()) {
                                if (but.equals(white)) board.setTurnColor(cz.cvut.fel.puskapet.chess.model.Color.WHITE);
                                if (but.equals(black)) board.setTurnColor(cz.cvut.fel.puskapet.chess.model.Color.BLACK);
                            }
                        }
                        game = new Game(board);
                        game.setView(g);
                        drawBoard();
                        updateStatusBar();
                    }
                }
                editorView.setVisible(false);
                gameView.setVisible(true);
            }
        });

        top.setLayout(new FlowLayout());
        top.add(editorDone);
        top.add(white);
        top.add(black);
        group.add(white);
        group.add(black);
        editorView.add(top, BorderLayout.NORTH);
        initializeEditorBoard();
        editorView.add(editorChessboard, BorderLayout.CENTER);
        initializeFigurePanel();
        editorView.add(figurePanel, BorderLayout.SOUTH);
    }

    private void initializeEditorBoard() {
        editorChessboard = new JPanel();
        editorChessboard.setLayout(chessboardLayout);
        editorChessboard.setPreferredSize(new Dimension(480, 480));
        for (char c = '8'; c >= '1'; c--) {
            for (char ch = 'a'; ch <= 'h'; ch++) {
                JButton square = new JButton();
                square.setBackground(isDarkSquare("" + ch + c) ? DARK_SQUARE : LIGHT_SQUARE);
                square.setSize(SQUARE_SIZE, SQUARE_SIZE);
                square.setFocusable(false);
                square.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        JButton but = (JButton) actionEvent.getSource();
                        but.setIcon(currentIcon);
                    }
                });
                editorSquares.put("" + ch + c, square);
                editorChessboard.add(square);
            }
        }
    }

    private void initializePromotion(cz.cvut.fel.puskapet.chess.model.Color turn) {
        promotion = new JDialog();
        promotion.setSize(250, 65);
        promotion.setPreferredSize(new Dimension(250, 65));
        promotion.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton queen = initializeEditorSidePanelButton(turn.equals(cz.cvut.fel.puskapet.chess.model.Color.WHITE) ? whiteQueen : blackQueen);
        JButton bishop = initializeEditorSidePanelButton(turn.equals(cz.cvut.fel.puskapet.chess.model.Color.WHITE) ? whiteBishop : blackBishop);
        JButton rook = initializeEditorSidePanelButton(turn.equals(cz.cvut.fel.puskapet.chess.model.Color.WHITE) ? whiteRook : blackRook);
        JButton knight = initializeEditorSidePanelButton(turn.equals(cz.cvut.fel.puskapet.chess.model.Color.WHITE) ? whiteKnight : blackKnight);
        queen.addActionListener(promotionHandler);
        bishop.addActionListener(promotionHandler);
        rook.addActionListener(promotionHandler);
        knight.addActionListener(promotionHandler);
        promotion.add(queen);
        promotion.add(rook);
        promotion.add(bishop);
        promotion.add(knight);
        promotion.setVisible(true);

    }

    private class PromotionHandler implements ActionListener {

        public PromotionHandler() {

        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            JButton but = (JButton) actionEvent.getSource();
            if (but.getIcon().equals(whiteQueen) || but.getIcon().equals(blackQueen)) {
                game.setPromotingPiece(PieceType.QUEEN);
            }
            if (but.getIcon().equals(whiteRook) || but.getIcon().equals(blackRook)) {
                game.setPromotingPiece(PieceType.ROOK);
            }
            if (but.getIcon().equals(whiteKnight) || but.getIcon().equals(blackKnight)) {
                game.setPromotingPiece(PieceType.KNIGHT);
            }
            if (but.getIcon().equals(whiteBishop) || but.getIcon().equals(blackBishop)) {
                game.setPromotingPiece(PieceType.BISHOP);
            }
            game.makeMove(source, target);
            promotion.setVisible(false);
            drawBoard();
            updateStatusBar();
        }
    }

    /**
     * Checks whether square on the coordinates is dark
     *
     * @param coordinates
     * @return true if the square is dark, false if it is light square
     */
    public static boolean isDarkSquare(String coordinates) {
        return (coordinates.charAt(0) + coordinates.charAt(1)) % 2 == 0;
    }

    private void drawBoard() {

        //reset icons
        for (JButton button : squares.values()) {
            button.setIcon(null);
        }

        //reset square colors
        for (char c = '1'; c <= '8'; c++) {
            for (char ch = 'a'; ch <= 'h'; ch++) {
                squares.get("" + ch + c).setBackground((ch + c) % 2 == 0 ? DARK_SQUARE : LIGHT_SQUARE);
            }
        }

        //draw white pieces
        for (Piece p : game.getCurrentBoard().getWhitePieces()) {
            ImageIcon icon = switch (p.getPieceType()) {
                case KING -> whiteKing;
                case PAWN -> whitePawn;
                case KNIGHT -> whiteKnight;
                case ROOK -> whiteRook;
                case BISHOP -> whiteBishop;
                case QUEEN -> whiteQueen;
            };
            squares.get(p.getCurrentCoordinates()).setIcon(icon);
        }

        //draw black pieces
        for (Piece p : game.getCurrentBoard().getBlackPieces()) {
            ImageIcon icon = switch (p.getPieceType()) {
                case KING -> blackKing;
                case PAWN -> blackPawn;
                case KNIGHT -> blackKnight;
                case ROOK -> blackRook;
                case BISHOP -> blackBishop;
                case QUEEN -> blackQueen;
            };
            squares.get(p.getCurrentCoordinates()).setIcon(icon);
        }

        Logger.getLogger("FEN String").info(game.getCurrentBoard().getFenRepresentation());
    }

    /**
     * Main window initialization
     */
    @Override
    public void initializeView() {
        this.setResizable(false);
        this.setSize(480, 560);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initializeGameView();
        initializeEditorView();
        this.setLayout(new CardLayout());
        this.add(gameView);
        this.add(editorView);
        gameView.setVisible(true);
        editorView.setVisible(false);

        updateStatusBar();
        drawBoard();
        this.setVisible(true);
    }

    /**
     * Generic method to get key from map by its value, expects 1 to 1 mapping
     *
     * @param map
     * @param value
     * @param <T>
     * @param <E>
     * @return
     */
    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    private class SquareHandler implements ActionListener {
        private boolean selected;

        private final Logger LOGGER = Logger.getLogger("clicked square");

        public SquareHandler() {
            this.selected = false;
        }


        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String clickedCoordinates = getKeyByValue(squares, (JButton) actionEvent.getSource());
            if (!selected) {
                source = clickedCoordinates;
                squares.get(source).setBackground(isDarkSquare(source) ? DARK_SQUARE_SELECTED : LIGHT_SQUARE_SELECTED);
                if (!game.getCurrentBoard().squareIsEmpty(clickedCoordinates) &&
                        game.getCurrentBoard().getChessboard().get(clickedCoordinates).getColor().equals(game.getTurn())) {
                    for (String coordinates : game.getCurrentLegalMovesForPiece(game.getCurrentBoard().getChessboard().get(clickedCoordinates))) {
                        squares.get(coordinates).setBackground(isDarkSquare(coordinates) ? DARK_SQUARE_VALID : LIGHT_SQUARE_VALID);
                    }
                }
                selected = true;
            } else {
                target = clickedCoordinates;
                if (game.isPromotion(game.getCurrentBoard(), source, target)) {
                    initializePromotion(game.getTurn());
                } else {
                    game.makeMove(source, target);
                }
                drawBoard();
                updateStatusBar();
                selected = false;
            }
            LOGGER.info("Clicked on " + clickedCoordinates);
            LOGGER.info("Current legal moves " + game.getCurrentLegalMoves());
            LOGGER.info("EnPassant square: " + game.getCurrentBoard().getEnPassantSquare());
            StringBuilder sb = new StringBuilder();
            for (Board b : game.getGameHistory()) {
                sb.append(b.getFenRepresentation()).append("\n");
            }
            LOGGER.info(sb.toString());
        }
    }

    private class ClockView implements Runnable {

        public ClockView() {

        }

        String parseTime(int time) {
            String result = "";
            int tenthsOfSecond = time % 10;
            time /= 10;
            int seconds = time % 60;
            time /= 60;
            int minutes = time;

            result = minutes + ":" + seconds + ":" + tenthsOfSecond;
            return result;
        }

        @Override
        public void run() {
            while (true) {
                whiteTime.setText("WHITE " + parseTime(game.getClock().getWhiteRemaining()));
                blackTime.setText("BLACK " + parseTime(game.getClock().getBlackRemaining()));
            }

        }
    }

}
