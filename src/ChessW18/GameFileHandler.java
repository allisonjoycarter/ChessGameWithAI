package ChessW18;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class GameFileHandler {
    private StringBuilder fileText = new StringBuilder();
    private IChessPiece[][] board;
    private ChessModel model;
    private Player player;
    private int turn = 1;
    private StringBuilder moveCodes;

    /**
     * Constructor for the GameFileHandler
     * @param model
     */
    public GameFileHandler(ChessModel model) {
        this.model = model;
        board = model.getBoard();
        player = model.currentPlayer();
        moveCodes = new StringBuilder();
    }

    /**
     * Puts gameData into a file
     *
     * @param fileName
     */
    public void recordGameData(String fileName) {
        try {
            FileWriter fileWriter = new FileWriter(fileName, true);
            BufferedWriter out = new BufferedWriter(fileWriter);
            out.write(model.getGameData());
            out.close();
        } catch (Exception e) {
            System.out.println("Unable to write file " + fileName);
        }
    }

    /**
     * Creates a string that appends on current gameData
     *
     * @param moveCode
     * @return
     */
    public String buildGameData(String moveCode) {
        if (player == Player.WHITE) {
            moveCodes.append(String.valueOf(turn)).append(".");
            turn++;
        }
        moveCodes.append(moveCode).append(" ");
        return moveCodes.toString();

    }

    /**
     * TODO
     * Removes the last move off the gameData string
     *
     * @param gameData
     * @return
     */
    public String removeLastMove(String gameData) {
        StringBuilder builder = new StringBuilder(gameData);

        if (player == Player.BLACK) {
            //removing from number to whitespace
            builder.replace(builder.lastIndexOf(String.valueOf(turn)), builder.lastIndexOf(" "), "");
            turn--; //going back a turn
        } else {
            //the last move will include a space after it
            //so we need to remove the space at the end of the move
            builder.replace(builder.lastIndexOf(" "), builder.length(), "");
            //now removing from the space before the move to the end of the string
            builder.replace(builder.lastIndexOf(" "), builder.length(), "");

        }
        return builder.toString();
    }

    /**
     * Resets the gameData so a new game can be recorded
     */
    public void resetGameData() {
        moveCodes = new StringBuilder();
        turn = 1;
    }

    /**
     * Reads a game file and returns a string of all the moves
     *
     * @param fileName
     * @return
     */
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
            e.printStackTrace();
        }
        return fileText.toString();
    }

    /**
     * Separates the games from Database
     *
     * @return
     */
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
        return new ArrayList<>(Arrays.asList(game.toString().split("~")));
    }

    /**
     * Separates the moves from a full gameData text
     *
     * @param fullText
     * @return
     */
    public ArrayList<String> separateMoves(String fullText) {
        String[] movePairs = fullText.split("\\d+\\.");
        ArrayList<String> moves = new ArrayList<>();
        for (String movePair : movePairs) {
            String[] temp = movePair.split("\\s");
            for (String moveCode : temp) {
                if (moveCode.equals("") || moveCode.equals(" "))
                    continue;
                moves.add(moveCode);
            }
        }
        return moves;
    }

    /**
     * Takes a string move of standard chess format and returns the move.
     *
     * @param move
     * @return
     */
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
            pieceType = findType(letters[0]);
        }

        //determining the column represented by a/b/c/etc.
        column = findColumn(moveToPos);

        result.newRow = row;
        result.newColumn = column;

        //to store captured piece
        if (move.contains("x"))
            result.setCapturedPiece(model.pieceAt(row, column));

        //to promote without a dialog needed
        //if there was a promotion
        if (promotedTo != null)
            result.setPromotion(promotedTo);

        //setting the from position in the move to return if specified
        if (hasTwoLetters)
            result.oldColumn = findColumn(moveFromPos);

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
        if (player == Player.WHITE) {
            if (move.equals("O-O-O")) {
                result.oldRow = 7;
                result.oldColumn = 4;
                result.newRow = 7;
                result.newColumn = 2;
            } else if (move.equals("O-O")) {
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

    /**
     * To be used WHILE executing a move
     *
     * @param move
     * @return
     */
    public String moveAndGenerateCode(Move move) {
        board = model.getBoard();
        player = model.currentPlayer();
        StringBuilder moveCode = new StringBuilder();
        char pieceType = typeToChar(board[move.oldRow][move.oldColumn].type());
        char moveFrom = columnToLetter(move.oldColumn);
        char moveTo = columnToLetter(move.newColumn);
        int row = rowToGameRow(move.newRow);

        boolean flag = false;
        for (int r = 0; r < board.length; r++)
            for (int c = 0; c < board.length; c++)
                if (board[move.oldRow][move.oldColumn].type().equals(findType(pieceType)) &&
                        board[r][c] != null &&
                        model.isValidMove(new Move(r, c, move.newRow, move.newColumn)) &&
                        player != board[move.oldRow][move.oldColumn].player() &&
                        c != move.oldColumn) {
                    flag = true;
                    break;
                }

        try {
            model.move(move);


            if (pieceType != 'P')
                moveCode.append(pieceType);

            if (flag || (move.getCapturedPiece() != null && pieceType == 'P'))
                moveCode.append(moveFrom);

            if (move.getCapturedPiece() != null)
                moveCode.append("x");

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
            System.out.println("Unable to write and execute move");
            return "null";
        }

        return moveCode.toString();
    }

    private int rowToGameRow(int row) {
        return 8 - row;
    }

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

    private String findType(char piece) {
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

    private int findColumn(char pos) {
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


}
