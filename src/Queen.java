public class Queen extends ChessPiece {

    protected Queen(Player player) {
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
