package ChessW18;

import java.util.ArrayList;

public class ChessAI extends ChessModel{
    IChessPiece[][] board = getBoard();
    Player player;

    //for scoring moves?
    private final int PAWN = 1;
    private final int KNIGHT = 3;
    private final int BISHOP = 3;
    private final int ROOK = 5;
    private final int QUEEN = 9;
    private final int KING = 100;

    public ChessAI(Player player) {
        this.player = player;
    }

    /**
     * evaluates the score for a player based on the pieces they own
     *
     * @param player
     * @return
     */
    private int evaluateScore(Player player) {
        int score = 0;
        for (int row = 0; row < board.length; row++)
            for (int col = 0; col < board.length; col++) {
                if (board[row][col] != null)
                    if (board[row][col].player() == player)
                        //add score for the player's existing pieces
                        score += getPieceValue(board[row][col].type());

                //remove score for their pieces that have been captured
                ArrayList<IChessPiece> captures = (player == Player.BLACK ? getWhiteCaptures() : getBlackCaptures());
                for (IChessPiece piece : captures) {
                    score -= getPieceValue(piece.type());
                }
            }
        return score;
    }

    /**
     * If the king is in check, this method will check every black
     * piece on the board to see if it can be moved to save the king.
     *
     * @return True if a move was made to save the king.
     *
     * @author George
     * @version 3/26/18
     */
    private boolean saveKing() {
        //If the AI is in check, get it out of check.
        if (inCheck(Player.BLACK)) {
            for (int row = 0; row < board.length; row++)
                for (int col = 0; col < board.length; col++) {

                    //Looks if this place on the board has a Black piece on it.
                    if (board[row][col].player() == Player.BLACK) {
                        ArrayList<Move> possibilities = legalMoves(row, col);

                        //Goes through every possible move.
                        for (int index = possibilities.size(); index >= 0; index--) {
                            Move newMove = possibilities.get(index);

                            //In case an opponents piece is going to be taken, we need to know what it was.
                            IChessPiece oldPiece = board[newMove.newRow][newMove.newColumn];
                            board[newMove.newRow][newMove.newColumn] = board[row][col];

                            //If the AI is still in check, the move in excecuted with the Move method.
                            if (inCheck(Player.BLACK)) {
                                board[row][col] = oldPiece;
                                move(newMove);
                                return true;
                            } else
                                board[newMove.newRow][newMove.newColumn] = null;

                        }
                    }
                }
        }
        return false;
    }

    /******************************************************************
     * This can be used to determine if a black piece is in danger.
     *
     * @return True if a move was made to save a piece.
     *
     * @author George
     * @version 3/26/18
     *****************************************************************/
    private boolean pieceInDanger() {
    //I had to go program a gate so my 4-intersection would't have train accidents. I'll get back to this later.
    return false;
    }

    private Player opponent() {
        return (player == Player.BLACK ? Player.WHITE : Player.BLACK);
    }

    private int getPieceValue(String type) {
        switch (type) {
            case "Pawn":
                return PAWN;
            case "Knight":
                return KNIGHT;
            case "Bishop":
                return BISHOP;
            case "Rook":
                return ROOK;
            case "Queen":
                return QUEEN;
            case "King":
                return KING;
        }
        return 0;
    }

}
