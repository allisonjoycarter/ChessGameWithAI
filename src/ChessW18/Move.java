package ChessW18;

public class Move {
    int oldRow;
    int oldColumn;
    int newRow;
    int newColumn;

    /******************************************************************
     * Object to store a move
     * Could use this to make an undo function?
     *
     * Ferguson used fromRow and toRow as names for these ints
     * Same thing, I just happened to use these names
     * We can change it if necessary
     *
     *
     * @param oldRow
     * @param oldColumn
     * @param newRow
     * @param newColumn
     *****************************************************************/
    public Move(int oldRow, int oldColumn, int newRow, int newColumn) {
        this.oldRow = oldRow;
        this.oldColumn = oldColumn;
        this.newRow = newRow;
        this.newColumn = newColumn;
    }

    public int getOldRow() {
        return oldRow;
    }

    public void setOldRow(int oldRow) {
        this.oldRow = oldRow;
    }

    public int getOldColumn() {
        return oldColumn;
    }

    public void setOldColumn(int oldColumn) {
        this.oldColumn = oldColumn;
    }

    public int getNewRow() {
        return newRow;
    }

    public void setNewRow(int newRow) {
        this.newRow = newRow;
    }

    public int getNewColumn() {
        return newColumn;
    }

    public void setNewColumn(int newColumn) {
        this.newColumn = newColumn;
    }
}
