package fr.kotlini.supragui.classes;

public class SlotPosition {

    private int column;

    private int row;

    public SlotPosition(int column, int row) {
        this.column = column;
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int toSlot() {
        return column + row * 9;
    }

    private static int getColumn(int slot) {
        return slot % 9;
    }

    private static int getRow(int slot) {
        return slot / 9;
    }

    public static SlotPosition slotPosition(int slot) {
        return new SlotPosition(getColumn(slot), getRow(slot));
    }
}