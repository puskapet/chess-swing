package cz.cvut.fel.puskapet.chess.controller;

import cz.cvut.fel.puskapet.chess.model.Color;

/**
 * @author puskapet
 */
public class ChessClock implements Runnable {

    private final int SECONDS_IN_MINUTE = 60;
    private final int NTHS_OF_SECOND = 10;
    private int whiteRemaining;
    private int blackRemaining;
    private boolean started;
    Color turn;

    /**
     * Constructor
     *
     * @param timeLimitInMinutes
     * @param turn
     */
    public ChessClock(int timeLimitInMinutes, Color turn) {
        started = false;
        this.turn = turn;
        this.whiteRemaining = timeLimitInMinutes * SECONDS_IN_MINUTE * NTHS_OF_SECOND;
        this.blackRemaining = timeLimitInMinutes * SECONDS_IN_MINUTE * NTHS_OF_SECOND;
    }

    /**
     * Method to switch turn
     */
    public void switchTurn() {
        turn = turn.equals(Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    /**
     * Run method
     */
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(100); //sleep for 1 10th of second
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (started) {
                switch (turn) {
                    case WHITE -> whiteRemaining--;
                    case BLACK -> blackRemaining--;
                }
            }
            if (whiteRemaining == 0 || blackRemaining == 0) {
                stopClock();
                break;
            }
        }
    }

    /**
     * Starts clock
     */
    public void startClock() {
        started = true;
    }

    /**
     * Stops clock
     */
    public void stopClock() {
        started = false;
    }

    /**
     * Getter of the white's time
     *
     * @return
     */
    public int getWhiteRemaining() {
        return whiteRemaining;
    }

    /**
     * Getter of the black's time
     *
     * @return
     */
    public int getBlackRemaining() {
        return blackRemaining;
    }
}
