public class Rook extends ChessPiece {

    private boolean canCastle; //set false if move is made

    public Rook(Player player) {
        super(player);
    }

    @Override
    public String type() {
        return null;
    }

    public boolean isValidMove(Move move, IChessPiece[][] board) {

        return false;
    }
}
