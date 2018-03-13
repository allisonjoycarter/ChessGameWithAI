public class King extends ChessPiece {

    private boolean canCastle; //set false if move is made

    public King(Player player) {
        super(player);
    }

    @Override
    public String type() {
        return "King";
    }

    public boolean isValidMove(Move move, IChessPiece[][] board) {

        return false;
    }
}
