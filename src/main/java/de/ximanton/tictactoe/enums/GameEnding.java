package de.ximanton.tictactoe.enums;

public enum GameEnding {
    PLAYER_1_WON(-1), PLAYER_2_WON(1), TIE(0);

    private final int score;

    GameEnding(int score) {
        this.score = score;
    }

    public int getScore() {
        return this.score;
    }

}
