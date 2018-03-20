package ChessW18;

public abstract class ChessPiece implements IChessPiece {

    private Player owner;

    protected ChessPiece(Player player) {
        this.owner = player;
    }

    public abstract String type();

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

    public boolean isValidMove(Move move, IChessPiece[][] board) {
        if (move.oldRow == move.newRow && move.oldColumn == move.newColumn)
            return false; //moving to the same spot
        else if (board[move.newRow][move.newColumn] == null)
            return true;
        return (board[move.newRow][move.newColumn].player().equals(board[move.oldRow][move.oldColumn].player()));
    }


}
