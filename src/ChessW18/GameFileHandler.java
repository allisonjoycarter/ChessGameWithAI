package ChessW18;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**********************************************************************
 * Reads and writes move sequences in standard chess notation (as
 * written in pgn files) and converts them to strings or arraylists of
 * strings to be more accessible in the program
 *
 * @author Allison
 * @version 3/29/2018
 *********************************************************************/
public class GameFileHandler {
    /** StringBuilder to store a string of the entire text from the file */
    private StringBuilder fileText = new StringBuilder();

    /** variable to hold the chess board */
    private IChessPiece[][] board;

    /** variable to reference the ChessModel used */
    private ChessModel model;

    /** variable to represent the current player */
    private Player player;

    /** variable to represent the turn in pairs (incremented on white turn) */
    private int turn = 1;

    /** variable to hold the move sequence for the moves in the current game */
    private StringBuilder moveCodes;

    /*****************************************************************
     * Constructor for the GameFileHandler that identifies the model,
     * board, and player. Also initializes moveCodes
     *
     * @param model the ChessModel to be used
     *****************************************************************/
    public GameFileHandler(ChessModel model) {
        this.model = model;
        board = model.getBoard();
        player = model.currentPlayer();
        moveCodes = new StringBuilder();
    }

    /******************************************************************
     * Puts the string of move sequence (gameData) into a file
     *
     * @param fileName the name of the file to save the data to
     *****************************************************************/
    public void recordGameData(String fileName) {
        try {
            FileWriter fileWriter = new FileWriter(fileName, true);
            BufferedWriter out = new BufferedWriter(fileWriter);
            out.write(model.getGameData()); //writing the gameData from model
            out.close();
        } catch (Exception e) {
            System.out.println("Unable to write file " + fileName);
        }
    }

    /******************************************************************
     * Removes the last move off the gameData string, so this can be
     * used with undoLastMove() to completely erase a move from the data
     *
     * @return gameData without the last move
     *****************************************************************/
    public String removeLastMove() {
        player = model.currentPlayer();
        //white's turn includes a turn number in the move sequence
        //so that also needs to be removed
        if (player == Player.WHITE) {
            //removing from number to whitespace
            moveCodes.replace(moveCodes.lastIndexOf(
                    //go back a turn for undoing
                    String.valueOf(--turn) + "."),
                    moveCodes.length(), "");
        } else {
            //removing the space at the end of the string
            moveCodes.replace(moveCodes.length() - 1, moveCodes.length(), "");
            //now removing from the space before the move to the end of the string
            //adding one to keep the space after the previous move
            moveCodes.replace(moveCodes.lastIndexOf(" ") + 1, moveCodes.length(), "");
        }
        return moveCodes.toString();
    }

    /******************************************************************
     * Resets the gameData so a new game can be recorded
     *****************************************************************/
    public void resetGameData() {
        moveCodes = new StringBuilder();
        model.setGameData(moveCodes.toString());
        turn = 1;
    }

    /******************************************************************
     * Reads a file with a move sequence and returns a string of the
     * move sequence
     *
     * @param fileName the file of moves to read
     * @return string of the text from the file
     *****************************************************************/
    public String readGameFile(String fileName) {
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();

            while(line != null) {
                fileText.append(line).append(" ");
//                fileText.append(System.lineSeparator());
                line = bufferedReader.readLine();
            }

            bufferedReader.close();
        } catch (Exception e) {
            System.out.println("Unable to read file");
        }

        return fileText.toString();
    }

    /******************************************************************
     * Separates the games from Database and puts them into an ArrayList
     * so each game can be analyzed individually
     *
     * @return ArrayList of strings, each a move sequence
     *****************************************************************/
    public ArrayList<String> databaseGames() {
        StringBuilder game = new StringBuilder();
        try {
            FileReader fileReader = new FileReader("Database");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();

            while(line != null) {
                game.append(line).append(" ");
//                fileText.append(System.lineSeparator());
                line = bufferedReader.readLine();
            }

            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //~ is the delimiter, so the array just adds a split string of move sequences
        return new ArrayList<>(Arrays.asList(game.toString().split("~")));
    }

    /******************************************************************
     * Separates the moves from a full gameData text, so that each move
     * can be referenced individually
     *
     * @param fullText the complete string of moves
     * @return ArrayList of moves in standard chess notation
     *****************************************************************/
    public ArrayList<String> separateMoves(String fullText) {
        //first splitting into move pairs
        String[] movePairs = fullText.split("\\d+\\.");

        //then splitting the move pairs into individual moves
        ArrayList<String> moves = new ArrayList<>();
        for (String movePair : movePairs) {
            //splitting at whitespace
            moves.addAll(Arrays.asList(movePair.split("\\s")));
        }
        moves.removeAll(Arrays.asList("", null));
        moves.removeAll(Arrays.asList(" ", null));
        return moves;
    }

    /******************************************************************
     * Takes a string move of standard chess notation and returns a
     * valid move in the current game.
     *
     * @param move a string of the standard chess notated move
     * @return a Move that can be executed by ChessModel
     *****************************************************************/
    public Move decodeMove(String move) {
        //info that will be used to determine the move
        player = model.currentPlayer();
        board = model.getBoard();
        Move result = new Move();
        String pieceType = "Pawn";

        //row and column to move to
        int column;
        int row = 0;

        //column to move to and from if specified
        char moveToPos = '0';
        char moveFromPos = '0';

        //to determine if the code specifies a column to move from
        boolean hasTwoLetters = false;

        //to hold what a pawn was promoted to
        IChessPiece promotedTo = null;

        //the move was en passant
        if (move.contains("e.p."))
            result.setWasEnPassant(true);

        char[] letters = move.toCharArray();

        //going through move to get info
        for (int i = 0; i < letters.length; i++) {
            //digit represents row
            if (Character.isDigit(letters[i]))
                row = 8 - Character.getNumericValue(letters[i]); //rows go up from the bottom 1->8

            //if there was a position to move from
            //the second letter represents moving to
            if (Character.isLowerCase(letters[i]) &&
                    moveToPos != '0' && letters[i] != 'x') {
                moveToPos = letters[i];
                hasTwoLetters = true; //there was a from specified

                //otherwise there was only a move to location specified
            } else if (Character.isLowerCase(letters[i]) && letters[i] != 'x') {
                moveFromPos = letters[i];
                moveToPos = letters[i];

                // =X means promotion to X
            } else if (letters[i] == '=') {
                switch (letters[i + 1]) {
                    case 'Q':
                        promotedTo = new Queen(player);
                        break;
                    case 'N':
                        promotedTo = new Knight(player);
                        break;
                    case 'R':
                        promotedTo = new Rook(player);
                        break;
                    case 'B':
                        promotedTo = new Bishop(player);

                }

            }

        }

        //determining the kind of piece that moved
        //no letter specified means it was a pawn
        if (Character.isUpperCase(letters[0])) {
            pieceType = charToType(letters[0]);
        }

        //determining the column represented by a/b/c/etc.
        column = letterToColumn(moveToPos);

        result.newRow = row;
        result.newColumn = column;

        //to store captured piece
        if (move.contains("x"))
            result.setCapturedPiece(model.pieceAt(row, column));

        //to promote without a dialog needed
        //if there was a promotion
        if (promotedTo != null)
            result.setPromotion(promotedTo);

        //setting the from position in the resulting Move if necessary
        if (hasTwoLetters)
            result.oldColumn = letterToColumn(moveFromPos);

        //going through the board to find a piece that can move to
        //the location specified by the move code
        for (int r = 0; r < board.length; r++)
                for (int c = 0; c < board.length; c++)
                    if (board[r][c] != null && //if there is a piece
                            board[r][c].type().equals(pieceType)) { //if the piece is the correct type
                        Move temp;
                        if (hasTwoLetters) { //if there was a from column
                            temp = new Move(r, result.oldColumn, row, column);
                            if (board[r][result.oldColumn] != null &&
                                    model.isValidMove(temp) && //if the move is valid
                                    board[r][result.oldColumn].player() == player && //if the move is for the right player
                                    result.oldColumn == c)
                                result.oldRow = r;
                        }
                        else {
                            //testing for a piece from the right player
                            //that can move to the right location
                            temp = new Move(r, c, row, column);
                            if (model.isValidMove(temp) &&
                                    board[r][c].player() == player) {
                                result.oldRow = r;
                                result.oldColumn = c;
                            }
                        }
                    }


        //dealing with castling
        //can be hard coded because the positions
        //to castle from and to never change
        if (player == Player.WHITE) {
            if (move.equals("O-O-O")) { //queens side castle
                result.oldRow = 7;
                result.oldColumn = 4;
                result.newRow = 7;
                result.newColumn = 2;
            } else if (move.equals("O-O")) { //kings side castle
                result.oldRow = 7;
                result.oldColumn = 4;
                result.newRow = 7;
                result.newColumn = 6;
            }
        } else {
            if (move.equals("O-O-O")) {
                result.oldRow = 0;
                result.oldColumn = 4;
                result.newRow = 0;
                result.newColumn = 2;
            } else if (move.equals("O-O")) {
                result.oldRow = 0;
                result.oldColumn = 4;
                result.newRow = 0;
                result.newColumn = 6;
            }

        }
        return result;
    }

    /******************************************************************
     * To be used WHILE executing a move. This moves a piece and
     * generates a string in standard chess notation to represent that
     * move
     *
     * @param move the Move to be notated and executed
     *****************************************************************/
    public void moveAndAddToSequence(Move move) {
        player = model.currentPlayer();
        //string to create
        StringBuilder moveCode = new StringBuilder();

        //the character to reference the type of piece moving
        char pieceType = typeToChar(board[move.oldRow][move.oldColumn].type());

        //character to reference the column it's moving from
        char moveFrom = columnToLetter(move.oldColumn);

        //character to reference the column it's moving to
        char moveTo = columnToLetter(move.newColumn);

        //the int that's converted from standard chess rows
        //to the reference of the row in board
        int row = rowToGameRow(move.newRow);

        //checking to see if any other piece can move to that location
        //if so, the from column is needed
        boolean flag = false;
        for (int r = 0; r < board.length; r++)
            for (int c = 0; c < board.length; c++)
                //if a piece of the same type and player has a valid move
                //to the same location, the from column is needed
                if (board[r][c] != null &&
                        board[r][c].type().equals(board[move.oldRow][move.oldColumn].type()) &&
                        board[r][c].player() == board[move.oldRow][move.oldColumn].player() &&
                        model.isValidMove(new Move(r, c, move.newRow, move.newColumn)) &&
                        c != move.oldColumn) { //making sure it's not the piece we're currently moving
                    flag = true;
                    break;
                }

        try {
            //now that the validity of other pieces moving to this location
            //has been checked, we can move and see if it put the player in
            //check or was a castle or etc
            model.move(move);

            //add a capital letter if the piece to move isn't a pawn
            if (pieceType != 'P')
                moveCode.append(pieceType);

            //add the letter for the column to move from if it's a pawn
            //capture or if there's another piece that can move there
            if (flag || (move.getCapturedPiece() != null && pieceType == 'P'))
                moveCode.append(moveFrom);

            //adding an "x" for capture
            if (move.getCapturedPiece() != null)
                moveCode.append("x");

            //adding the column and row the piece is moving to
            moveCode.append(moveTo);
            moveCode.append(Integer.toString(row));

            //= means promotion
            if (move.getPromotion() != null)
                moveCode.append("=").append(typeToChar(move.getPromotion().type()));

            //+ means check, # means checkmate
            if (model.inCheck(board[move.newRow][move.newColumn].opponent())) {
                if (model.movesToEscapeCheck(board[move.newRow][move.newColumn].opponent()).isEmpty())
                    moveCode.append("#");
                else
                    moveCode.append("+");
            }

            //code for castles
            if (board[move.newRow][move.newColumn].type().equals("King")) {
                if (move.newColumn == move.oldColumn - 2)
                    moveCode = new StringBuilder("O-O-O");
                else if (move.newColumn == move.oldColumn + 2)
                    moveCode = new StringBuilder("O-O");
            }


        } catch (Exception e) {
            throw new IllegalArgumentException();
        }

        //adding the code to the string of all moves
        if (player == Player.WHITE) {
            moveCodes.append(String.valueOf(turn)).append(".");
            turn++; //increasing the turn
        }
        moveCodes.append(moveCode).append(" "); //always adding a space between moves
        model.setGameData(moveCodes.toString());
    }

    /*****************************************************************
     * Converting a row from a chess board to a row usable by our
     * ChessModel board
     *
     * @param row the row from standard chess notation
     * @return an equivalent row to be used by board
     *****************************************************************/
    private int rowToGameRow(int row) {
        return 8 - row;
    }

    /******************************************************************
     * Converts the int column from board to the character used to
     * reference that column in standard chess notation
     *
     * @param col the column used by board
     * @return the letter that represents the column
     *****************************************************************/
    private char columnToLetter(int col) {
        char column = 'a';
        switch (col) {
            case 1:
                column = 'b';
                break;
            case 2:
                column = 'c';
                break;
            case 3:
                column = 'd';
                break;
            case 4:
                column = 'e';
                break;
            case 5:
                column = 'f';
                break;
            case 6:
                column = 'g';
                break;
            case 7:
                column = 'h';
                break;
        }
        return column;
    }


    /*****************************************************************
     * Converts a letter representing a column to an int that can be
     * referenced by board
     *
     * @param pos the character that represents a column
     * @return int that references the same column in board
     *****************************************************************/
    private int letterToColumn(char pos) {
        int column = 0;
        switch (pos) {
            case 'a':
                column = 0;
                break;
            case 'b':
                column = 1;
                break;
            case 'c':
                column = 2;
                break;
            case 'd':
                column = 3;
                break;
            case 'e':
                column = 4;
                break;
            case 'f':
                column = 5;
                break;
            case 'g':
                column = 6;
                break;
            case 'h':
                column = 7;
        }
        return column;
    }

    /******************************************************************
     * Converts the piece type to a character that represents the piece
     *
     * @param type string that describes the piece type. i.e. "Pawn"
     * @return the character to represent the type of piece
     *****************************************************************/
    private char typeToChar(String type) {
        char pieceType = 'P';
        switch (type) {
            case "Knight":
                pieceType = 'N';
                break;
            case "Rook":
                pieceType = 'R';
                break;
            case "Bishop":
                pieceType = 'B';
                break;
            case "Queen":
                pieceType = 'Q';
                break;
            case "King":
                pieceType = 'K';
        }
        return pieceType;
    }

    /*****************************************************************
     * Converts a character that represents a piece type to a full
     * word describing the type
     *
     * @param piece the character that represents a piece type in
     *              standard chess notation
     * @return the type of piece
     *****************************************************************/
    private String charToType(char piece) {
        String type = "Pawn";
        switch (piece) {
            case 'N':
                type = "Knight";
                break;
            case 'B':
                type = "Bishop";
                break;
            case 'R':
                type = "Rook";
                break;
            case 'Q':
                type = "Queen";
                break;
            case 'K':
                type = "King";
                break;
        }
        return type;
    }
}
