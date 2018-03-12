public class ChessModel implements IChessModel {
    private IChessModel[][] board = new IChessModel[8][8];

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public boolean isValidMove(Move move) { //overloaded
        //valid if:
            //spot is empty
            //board[][].isValidMove returns true
            //player will not be in check
            //move will get player out of check if they are in check
        return false;
    }

    @Override
    public void move(Move move) {
        board[move.newRow][move.newColumn] = board[move.oldRow][move.oldColumn];
        board[move.oldRow][move.oldColumn] = null;

    }

    @Override
    public boolean inCheck(Player p) {
        return false;
    }

    @Override
    public Player currentPlayer() {
        return null;
    }
}
