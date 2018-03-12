public abstract class ChessPiece implements IChessPiece {

    private Player owner;

    protected ChessPiece(Player player) {
        this.owner = player;
    }

    public abstract String type();

    public Player player() {
        // complete this
        return Player.BLACK;
    }

    public boolean isValidMove(Move move, IChessPiece[][] board) {
        // complete this
        if ((move.oldRow == move.newRow || move.oldColumn == move.newColumn)|| //moving to the same spot
                board[move.newRow][move.newColumn] != null) //if space is empty, or will there be an object for an empty space?
            return false;
        return true;
    }


}
