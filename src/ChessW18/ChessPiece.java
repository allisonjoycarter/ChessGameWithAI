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
     * @return ChessW18.Player opposite to owner, i.e. the opponent
     *****************************************************************/
    public Player opponent() {
        if (owner.equals(Player.BLACK))
            return Player.WHITE;
        return Player.BLACK;
    }

    public boolean isValidMove(Move move, IChessPiece[][] board) {
        if ((move.oldRow == move.newRow || move.oldColumn == move.newColumn)|| //moving to the same spot
                board[move.newRow][move.newColumn] != null) //space is taken
            return false;
        return true;
    }


}
