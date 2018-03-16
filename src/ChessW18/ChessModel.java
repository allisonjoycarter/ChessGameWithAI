package ChessW18;

import java.util.ArrayList;

public class ChessModel implements IChessModel {

    private IChessPiece[][] board = new IChessPiece[8][8];
    private Player currentPlayer = Player.WHITE; //to be changed
    private ArrayList<IChessPiece> whiteCaptures = new ArrayList<>();
    private ArrayList<IChessPiece> blackCaptures = new ArrayList<>();

    public ChessModel() {

        //placing the pawns on the board
        for (int column = 0; column < board.length; column++) {
            board[1][column] = new Pawn(Player.BLACK);
            board[7][column] = new Pawn(Player.WHITE);
        }
        //placing the rest of the pieces
        board[0][0] = new Rook(Player.BLACK);
        board[0][1] = new Knight(Player.BLACK);
        board[0][2] = new Bishop(Player.BLACK);
        board[0][3] = new Queen(Player.BLACK);
        board[0][4] = new King(Player.BLACK);
        board[0][5] = new Bishop(Player.BLACK);
        board[0][6] = new Knight(Player.BLACK);
        board[0][7] = new Rook(Player.BLACK);

        board[7][0] = new Rook(Player.WHITE);
        board[7][1] = new Knight(Player.WHITE);
        board[7][2] = new Bishop(Player.WHITE);
        board[7][3] = new Queen(Player.WHITE);
        board[7][4] = new King(Player.WHITE);
        board[7][5] = new Bishop(Player.WHITE);
        board[7][6] = new Knight(Player.WHITE);
        board[7][7] = new Rook(Player.WHITE);
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public boolean isValidMove(Move move) { //overloaded
        //valid if:
            //board[][].isValidMove returns true
        if (board[move.oldRow][move.oldColumn].isValidMove(move, board))
            return true;
            //player will not be in check
            //move will get player out of check if they are in check
        return false;
    }

    /******************************************************************
     * Checks if a move is a capture and returns the captured piece
     *
     * @return
     * @param move
     * @param board
     *****************************************************************/
    public IChessPiece checkForAndCapture(Move move, IChessPiece[][] board) {
        IChessPiece tempPiece = board[move.newRow][move.newColumn];
        //if the player of the place to move to is opposite of the player of the moving piece
        if (!tempPiece.player().equals(board[move.oldRow][move.oldColumn].opponent())) {
            return tempPiece;
        }
        return null;
    }

    @Override
    public void move(Move move) {
        if (isValidMove(move)) {
            if (checkForAndCapture(move, board) != null) //if there is a piece to be captured
                //add to list of respective player's captures
                if (currentPlayer.equals(Player.BLACK))
                    blackCaptures.add(checkForAndCapture(move, board));
                else
                    whiteCaptures.add(checkForAndCapture(move, board));

            //transferring piece from old square to new square
            board[move.newRow][move.newColumn] = board[move.oldRow][move.oldColumn];
            board[move.oldRow][move.oldColumn] = null;

            //if the ChessW18.King or ChessW18.Rook is moved, Castling is no longer an option
            IChessPiece temp = board[move.newRow][move.newColumn];
            if (temp.type().equals("ChessW18.King"))
                ((King) temp).canCastle = false;
            if (temp.type().equals("ChessW18.Rook"))
                ((Rook) temp).canCastle = false;

        } else {
            System.out.println("Invalid ChessW18.Move");
        }
    }

    @Override
    public boolean inCheck(Player p) {
        return false;
    }

    @Override
    public Player currentPlayer() {
        return currentPlayer;
    }

    public Player switchPlayer() {
        if (currentPlayer().equals(Player.BLACK))
            return Player.WHITE;
        return Player.BLACK;
    }

    public boolean gameOver() {
        return false;
    }

    public IChessPiece pieceAt(int row, int column) {
        return board[row][column];
    }

    public void reset() {
        blackCaptures.clear();
        whiteCaptures.clear();
        board = new IChessPiece[8][8];
    }
}
