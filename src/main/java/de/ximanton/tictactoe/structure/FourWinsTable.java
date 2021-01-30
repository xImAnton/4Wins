package de.ximanton.tictactoe.structure;

import de.ximanton.tictactoe.enums.GameEnding;
import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;

public class FourWinsTable {

    private Game game = null;
    public static final int[] columns = {0, 1, 2, 3, 4, 5, 6};
    public static final int[] rows = {0, 1, 2, 3, 4, 5};
    private ArrayList<ArrayList<FourWinsTableItem>> table = new ArrayList<>();
    private FourWinsTableItem lastPlacedItem = null;
    private int emptyFieldsLast = 42;

    /*public FourWinsTable(FourWinsTable copy) {
        this.game = copy.game;
        this.table = (ArrayList<ArrayList<FourWinsTableItem>>) copy.table.clone();
        this.emptyFieldsLast = copy.emptyFieldsLast;
        this.lastPlacedItem = getItem(copy.lastPlacedItem.getCol(), copy.lastPlacedItem.getRow());
        this.emptyFieldsLast = copy.emptyFieldsLast;
    }*/

    public FourWinsTable(Game game) {
        this.game = game;

        for (int col : columns) {
            ArrayList<FourWinsTableItem> colSet = new ArrayList<>();
            for (int row : rows) {
                colSet.add(new FourWinsTableItem(col, row));
            }
            table.add(colSet);
        }
    }

    public Game getGame() {
        return game;
    }

    public boolean addItem(int playerIndex, int column) {
        boolean changed = false;
        for (int item : rows) {
            if (item != 5) {
                if (getItem(column, item + 1).getPlayerIndex() != 0) {
                    if (getItem(column, item).getPlayerIndex() != 0) {
                        continue;
                    }
                    getItem(column, item).setPlayer(playerIndex);
                    lastPlacedItem = getItem(column, item);
                    emptyFieldsLast--;
                    changed = true;
                }
            } else {
                if (getItem(column, 5).getPlayerIndex() == 0) {
                    getItem(column, item).setPlayer(playerIndex);
                    lastPlacedItem = getItem(column, item);
                    emptyFieldsLast--;
                    changed = true;
                }
            }
        }
        return changed;
    }

    public FourWinsTableItem getItem(int col, int row) {
        return table.get(col).get(row);
    }

    public void removeLastItem(int column) {
        for (int row : rows) {
            if (getItem(column, row).getPlayerIndex() != 0) {
                getItem(column, row).setPlayer(0);
            }
        }
    }

    public boolean canPlace(int column) {
        boolean changed = false;
        for (int item : rows) {
            if (item != 5) {
                if (getItem(column, item + 1).getPlayerIndex() != 0) {
                    if (getItem(column, item).getPlayerIndex() != 0) {
                        continue;
                    }
                    changed = true;
                }
            } else {
                if (getItem(column, 5).getPlayerIndex() == 0) {
                    changed = true;
                }
            }
        }
        return changed;
    }

    public GameEnding checkWinner() {
        if (emptyFieldsLast == 0) {
            return GameEnding.TIE;
        }
        ArrayList<FourWinsTableItem> streak = new ArrayList<>();
        int player = 0;
        for (CheckIterator iterator = new CheckIterator(this, lastPlacedItem.getCol(), lastPlacedItem.getRow()); iterator.hasNext(); ) {
            FourWinsTableItem currentItem = iterator.next();
            if (iterator.getCurrentIndex() == 1) {
                player = currentItem.getPlayerIndex();
            }
            if (currentItem == null) {
                if (iterator.isLastElementOfLine()) {
                    player = 0;
                    streak.clear();
                }
                continue;
            }
            if (currentItem.getPlayerIndex() != player) {
                player = currentItem.getPlayerIndex();
                streak.clear();
            }
            streak.add(currentItem);
            if (streak.size() == 4 & player != 0) {
                return player == 1 ? GameEnding.PLAYER_1_WON : GameEnding.PLAYER_2_WON;
            }
            if (iterator.isLastElementOfLine()) {
                player = 0;
                streak.clear();
            }
        }
        return null;
    }

    public void checkForResults(int startCol, int startRow) {
        if (emptyFieldsLast == 0) {
            getGame().tie();
        }
        ArrayList<FourWinsTableItem> streak = new ArrayList<>();
        int player = 0;
        for (CheckIterator iterator = new CheckIterator(this, startCol, startRow); iterator.hasNext(); ) {
            FourWinsTableItem currentItem = iterator.next();
            if (iterator.getCurrentIndex() == 1) {
                player = currentItem.getPlayerIndex();
            }
            if (currentItem == null) {
                if (iterator.isLastElementOfLine()) {
                    player = 0;
                    streak.clear();
                }
                continue;
            }
            if (currentItem.getPlayerIndex() != player) {
                player = currentItem.getPlayerIndex();
                streak.clear();
            }
            streak.add(currentItem);
            if (streak.size() == 4 & player != 0) {
                for (FourWinsTableItem winItem : streak) {
                    winItem.setPartOfWinPosition();
                }
                getGame().end(player);
            }
            if (iterator.isLastElementOfLine()) {
                player = 0;
                streak.clear();
            }
        }
    }

    public FourWinsTableItem getLastPlacedItem() {
        return lastPlacedItem;
    }
}
