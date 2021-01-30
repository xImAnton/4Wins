package de.ximanton.tictactoe.structure;


public class FourWinsTableItem {
    private int playerIndex;
    private boolean isPartOfWinPosition;
    private final int row;
    private final int col;

    public FourWinsTableItem(int col, int row) {
        this.playerIndex = 0;
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setPlayer(int index) {
        if (index < 3 & index > -1) {
            this.playerIndex = index;
        }
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

    public void setPartOfWinPosition() {
        isPartOfWinPosition = true;
    }

    public boolean isPartOfWinPosition() {
        return isPartOfWinPosition;
    }
}
