package ChessW18;

public class Move {
    int oldRow;
    int oldColumn;
    int newRow;
    int newColumn;

    private boolean wasCastle;
    private boolean wasEnPassant;
    private IChessPiece capturedPiece;
    private IChessPiece promotion;

    /******************************************************************
     * Object to store a move
     *
     * @param oldRow the row a piece is moving from
     * @param oldColumn the column a piece is moving from
     * @param newRow the row a piece is moving to
     * @param newColumn the column a piece is moving to
     *****************************************************************/
    public Move(int oldRow, int oldColumn, int newRow, int newColumn) {
        this.oldRow = oldRow;
        this.oldColumn = oldColumn;
        this.newRow = newRow;
        this.newColumn = newColumn;
        wasCastle = false;
        wasEnPassant = false;
        capturedPiece = null;
        promotion = null;
    }

    public Move() {
        this.oldColumn = 0;
        this.oldRow = 0;
        this.newRow = 0;
        this.newColumn = 0;
        wasCastle = false;
        wasEnPassant = false;
        capturedPiece = null;
        promotion = null;
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

    public IChessPiece getCapturedPiece() {
        return capturedPiece;
    }

    public void setCapturedPiece(IChessPiece capturedPiece) {
        this.capturedPiece = capturedPiece;
    }

    public IChessPiece getPromotion() {
        return promotion;
    }

    public void setPromotion(IChessPiece promotion) {
        this.promotion = promotion;
    }
}
