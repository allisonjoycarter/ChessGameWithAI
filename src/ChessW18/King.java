package ChessW18;

/**********************************************************************
 * ChessW18.ChessPiece object to represent a ChessW18.King. Holds the ChessW18.Player that owns
 * the piece, whether the piece can Castle, the type of piece, and
 * whether a move proposed is valid given the general rules of Chess
 *
 * @author Allison
 * @version 3/15/2018
 *********************************************************************/
public class King extends ChessPiece {

    public boolean canCastle; //set false if move is made

    public King(Player player) {
        super(player);
        canCastle = true;
    }

    @Override
    public String type() {
        return "King";
    }

    public boolean isValidMove(Move move, IChessPiece[][] board) {
        if (super.isValidMove(move, board)) {

            if(canCastle) {
                boolean valid = true;

                if((move.oldRow == move.newRow) && (move.oldRow == 0 || move.oldRow == 7)
                        && (move.newColumn == 1 || move.newColumn == 6)) {
                    switch ((int) move.newColumn) {
                        case 1:
                            for (int i = 3; i > 0 && valid; i--)
                                if (board[move.oldRow][i] != null)
                                    valid = false;
                            break;

                        case 6:
                            for (int i = 3; i > 0 && valid; i--)
                                if (board[move.oldRow][i] != null)
                                    valid = false;
                            break;
                        default:
                            valid = false;
                    }
                    return valid;
                }
            }

            //if the move is more than 1 square out, return false
            //checks for out of bounds, but this may be unnecessary
            return (move.oldColumn >= board.length - 1 || move.newColumn <= move.oldColumn + 1) &&
                    (move.oldColumn < 1 || move.newColumn >= move.oldColumn - 1) &&
                    (move.oldRow >= board.length - 1 || move.newRow <= move.oldRow + 1) &&
                    (move.oldRow < 1 || move.newRow >= move.oldRow - 1);
        }
        return false;
    }

    public void setCanCastle(boolean canCastle) {
        this.canCastle = canCastle;
    }
}
