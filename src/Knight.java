public class Knight extends ChessPiece {

    protected Knight(Player player) {
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
