public class Bishop extends ChessPiece {

    public Bishop(Player player) {
        super(player);
    }

    @Override
    public String type() {
        return "Bishop";
    }

    /******************************************************************
     * Checks if a move for a bishop is valid. First checks the super
     * method for isValidMove, then checks if there is an intercepting
     * piece, and finally checks if the move is diagonal
     *
     * @author Allison
     * @param move the move to check
     * @param board the chess board
     * @return true if the move follows the rules, false if there is
     * a piece in between or the move is not a square diagonal
     *****************************************************************/
    @Override
    public boolean isValidMove(Move move, IChessPiece[][] board) {
        if (super.isValidMove(move,board)) {
            //for moving right diagonal
            if (move.oldRow > move.newRow) {
                for (int row = move.oldRow; row > move.newRow; row--)
                    for (int column = move.oldColumn; column > move.newColumn; column--)
                        if (board[row][column] != null)
                            return false;
            //for moving left diagonal
            } else if (move.oldRow < move.newRow) {
                for (int row = move.oldRow; row < move.newRow; row++)
                    for (int column = move.oldColumn; column < move.newColumn; column++)
                        if (board[row][column] != null)
                            return false;
            }
            //if the amount of rows moved is equal to the amount of columns moved
            if (Math.abs(move.oldRow - move.newRow) == Math.abs(move.oldColumn - move.newColumn))
                return true;
        }
        return false;
    }
}
