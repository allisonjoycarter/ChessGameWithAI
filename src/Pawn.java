public class Pawn extends ChessPiece {


    public Pawn(Player player) {
        super(player); //create chess piece with given player
    }

    @Override
    public String type() {
        return "Pawn";
    }


    public boolean isValidMove(Move move, IChessPiece[][] board) {
        if (super.isValidMove(move, board)) {
            //if there is a piece of opposite player to capture
            //if move is 1 square forward
        }
        return false;
    }


}
