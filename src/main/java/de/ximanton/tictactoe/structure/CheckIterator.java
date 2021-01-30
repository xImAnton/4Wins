package de.ximanton.tictactoe.structure;

import org.apache.commons.lang.ArrayUtils;

import java.util.Iterator;

public class CheckIterator implements Iterator<FourWinsTableItem> {

    private final FourWinsTable table;
    private final int startCol;
    private final int startRow;
    private int currentIndex;
    private int currentRelativeX = 0;
    private int currentRelativeY = 0;

    @Override
    public boolean hasNext() {
        return currentIndex <= 28;
    }

    @Override
    public FourWinsTableItem next() {
        if (currentIndex == 1) {
            currentRelativeX = -3;
            currentRelativeY = -3;
        } else if (currentIndex == 8) {
            currentRelativeY = -3;
            currentRelativeX = 0;
        } else if (currentIndex == 15) {
            currentRelativeY = -3;
            currentRelativeX = 3;
        } else if (currentIndex == 22) {
            currentRelativeY = 0;
            currentRelativeX = 3;
        }

        if (currentIndex > 1 & currentIndex < 8) {
            currentRelativeX++;
            currentRelativeY++;
        } else if (currentIndex > 8 & currentIndex < 15) {
            currentRelativeY++;
        } else if (currentIndex > 15 & currentIndex < 22) {
            currentRelativeY++;
            currentRelativeX--;
        } else if (currentIndex > 22 & currentIndex < 29) {
            currentRelativeX--;
        }
        int currentCol = startCol + currentRelativeX;
        int currentRow = startRow + currentRelativeY;
        if (!ArrayUtils.contains(FourWinsTable.columns, currentCol) | !ArrayUtils.contains(FourWinsTable.rows, currentRow)) {
            currentIndex++;
            return null;
        }
        currentIndex++;
        return table.getItem(currentCol, currentRow);
    }

    public boolean isLastElementOfLine() {
        return currentIndex == 1 | currentIndex == 8 | currentIndex == 15 | currentIndex == 22 | currentIndex == 29;
    }

    public CheckIterator(FourWinsTable table, int startCol, int startRow) {
        this.table = table;
        this.startCol = startCol;
        this.startRow = startRow;
        this.currentIndex = 0;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }
}
