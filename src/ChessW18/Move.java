package ChessW18;

public class Move {
    int oldRow;
    int oldColumn;
    int newRow;
    int newColumn;

    private boolean wasCastle;
    private boolean wasEnPassant;

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
        wasCastle = false;
        wasEnPassant = false;
    }

    public Move() {
        this.oldColumn = 0;
        this.oldRow = 0;
        this.newRow = 0;
        this.newColumn = 0;
        wasCastle = false;
        wasEnPassant = false;
    }

    public boolean wasCastle() {
        return wasCastle;
    }

    public void setWasCastle(boolean wasCastle) {
        this.wasCastle = wasCastle;
    }

    public boolean wasEnPassant() {
        return wasEnPassant;
    }

    public void setWasEnPassant(boolean wasEnPassant) {
        this.wasEnPassant = wasEnPassant;
    }
}
