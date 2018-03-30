package ChessW18;

public abstract class ChessPiece implements IChessPiece {

    /** the player that owns this chess piece */
    private Player owner;

    /******************************************************************
     * Constructor for a chess piece
     *
     * @param player the player that owns this chess piece
     *****************************************************************/
    protected ChessPiece(Player player) {
        this.owner = player;
    }

    /** to represent the type of piece */
    public abstract String type();

    /******************************************************************
     * Getter for the player that owns this chess piece
     *
     * @return the player who controls this chess piece
     *****************************************************************/
    public Player player() {
        return owner;
    }

    /******************************************************************
     * Used to reference the opponent of owner, mainly for use in
     * recognizing possible capture
     *
     * @author Allison
     * @return Player opposite to owner, i.e. the opponent
     *****************************************************************/
    public Player opponent() {
        if (owner.equals(Player.BLACK))
            return Player.WHITE;
        return Player.BLACK;
    }

    /******************************************************************
     * Checks if a move is valid under standard rules for all chess
     * pieces
     *
     * @param move {@link Move} object describing the move to be made.
     * @param board the {@link IChessModel} in which this piece resides
     * @return true if a move is valid, false if it breaks chess rules
     *****************************************************************/
    public boolean isValidMove(Move move, IChessPiece[][] board) {
        if (move.oldRow == move.newRow && move.oldColumn == move.newColumn)
            return false; //moving to the same spot
        else if (board[move.newRow][move.newColumn] == null)
            return true; //the destination is empty
        return (board[move.newRow][move.newColumn] != null &&
                board[move.newRow][move.newColumn].player() != board[move.oldRow][move.oldColumn].player());
    }


}
