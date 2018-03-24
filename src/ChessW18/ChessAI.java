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

    private void evaluateBoard() {

    }

    private Player opponent() {
        if (player == Player.BLACK)
            return Player.WHITE;
        return Player.BLACK;
    }

}
