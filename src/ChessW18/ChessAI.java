package ChessW18;

import java.util.ArrayList;

public class ChessAI extends ChessModel{
//    IChessPiece[][] board = getBoard();
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
