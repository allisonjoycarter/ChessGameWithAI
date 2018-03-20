package ChessW18;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class ChessPanel extends JPanel {

	private JButton[][] board;
	private ChessModel model;
	private final int BOARDSIZE = 8;

	private JLabel message;

	private ImageIcon wPawn;
	private ImageIcon wRook;
	private ImageIcon wBishop;
	private ImageIcon wKing;
	private ImageIcon wQueen;
	private ImageIcon wKnight;

	private ImageIcon bPawn;
	private ImageIcon bRook;
	private ImageIcon bBishop;
	private ImageIcon bKing;
	private ImageIcon bQueen;
	private ImageIcon bKnight;

	private Move move;
	/** pieceChoice is used to tell if the player is choosing a piece
	 * or a position. */
	private boolean pieceChosen;

	/** the current player */
	private Player currentPlayer;


	// declare other instance variables as needed

	private ButtonListener buttonListener = new ButtonListener();

	public ChessPanel() {
		model = new ChessModel();
		move = new Move();
		currentPlayer = model.currentPlayer();
		pieceChosen = false;
		board = new JButton[BOARDSIZE][BOARDSIZE];
		boardInit();
		displayBoard();
	}

	/**
	 * Sets up the board, a two dimensional matrix of JButtons,
	 * to contain an icon for a chess piece in each button. The
	 * icons are specific to each button.
	 *
	 * @author George
	 * @version 3/18
	 */
	private void boardInit() {

		//This images still need to be added to the repository
		wPawn = new ImageIcon("wPawn.png");
		wRook = new ImageIcon("wRook.png");
		wBishop = new ImageIcon("wBishop.png");
		wKnight = new ImageIcon("wKnight.png");
		wKing = new ImageIcon("wKing.png");
		wQueen = new ImageIcon("wQueen.png");

		//This images still need to be added to the repository
		bPawn = new ImageIcon("bPawn.png");
		bRook = new ImageIcon("bRook.png");
		bBishop = new ImageIcon("bBishop.png");
		bKnight = new ImageIcon("bKnight.png");
		bKing = new ImageIcon("bKing.png");
		bQueen = new ImageIcon("bQueen.png");

		ButtonListener listener = new ButtonListener();

		//TODO: create a panel that shows captures
//		setLayout(new GridBagLayout()); /** I'm setting this up so that
//		 the buttons will fall into place when created */
        setLayout(new GridLayout(BOARDSIZE, BOARDSIZE)); //this can be changed back, just temporary for seeing the grid
		GridBagConstraints loc;

		//Creates all of the pawns.
		for(int row = 0; row < BOARDSIZE; row++)
			for(int col = 0; col < BOARDSIZE; col++) {
		        board[row][col] = new JButton();
				board[row][col].addActionListener(listener);
                if ((row % 2 == 1 && col % 2 == 1) ||
                        (row % 2 == 0 && col % 2 == 0))
                    board[row][col].setBackground(Color.black);
                else
                    board[row][col].setBackground(Color.white);
				add(board[row][col]);
			}

		message = new JLabel("");
		model.reset();
	}


	/******************************************************************
	 * Method that updates the board and sets the correct icons.
	 *
	 * @author Allison
	 *****************************************************************/
	private void displayBoard() {
		for (int row = 0; row < BOARDSIZE; row++) {
			for (int col = 0; col < BOARDSIZE; col++) {
				IChessPiece temp = model.pieceAt(row,col); //variable to improve readability
				if (temp == null) { //if there is no piece, keep going on the loop
                    board[row][col].setIcon(null);
                    continue;
                }

                switch (temp.type()) {
                    case "Pawn":
                        if (temp.player().equals(Player.WHITE))
                            board[row][col].setIcon(wPawn);
                        else
                            board[row][col].setIcon(bPawn);
                        break;
                    case "Knight":
                        if (temp.player().equals(Player.WHITE))
                            board[row][col].setIcon(wKnight);
                        else
                            board[row][col].setIcon(bKnight);
                        break;
                    case "Rook":
                        if (temp.player().equals(Player.WHITE))
                            board[row][col].setIcon(wRook);
                        else
                            board[row][col].setIcon(bRook);
                        break;
                    case "Bishop":
                        if (temp.player().equals(Player.WHITE))
                            board[row][col].setIcon(wBishop);
                        else
                            board[row][col].setIcon(bBishop);
                        break;
                    case "King":
                        if (temp.player().equals(Player.WHITE))
                            board[row][col].setIcon(wKing);
                        else
                            board[row][col].setIcon(bKing);
                        break;
                    case "Queen":
                        if (temp.player().equals(Player.WHITE))
                            board[row][col].setIcon(wQueen);
                        else
                            board[row][col].setIcon(bQueen);
                }
			}
		}
//		message = new JLabel(model.getMessage(),
//				(model.getMessage()).length());
	}

	/***************************************************************
	 * Puts the board in the starting setup.
	 *
	 **************************************************************/
	private void resetBoard() {
		boardInit();
		repaint();
	}


	/**
	 * Inner class that represents action listener for buttons
	 */
	private class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			/*
			 * We need to store the location of the first button, and
			 * then store the location of the first button.
			 * -If the player chooses the first button, this will be
			 * dealt with through the move in ChessModel. The player
			 * must choose another button.
			 * -If the player chooses a different button, then this will
			 * be stored and model.move is called. The turn will be
			 * switched in chessModel.
			 *
			 * -Somewhere, probably not here, we should make sure a piece of the
			 *  correct team is being chosen.
			 * -Should make the button look differently, but still
			 *  pressable?
			 * */
			for (int row = 0; row < board.length; row++)
				for (int col = 0; col < board.length; col++)
					if (event.getSource() == board[row][col]) {
						if(!pieceChosen &&
                                model.pieceAt(row, col) != null && //need to make sure user is selecting an actual piece
                                model.pieceAt(row, col).player().equals(currentPlayer)) { //prevents playing for opponent
							move.oldColumn = col;
							move.oldRow = row;
							ArrayList<Move> moves = model.findValidMoves(row, col);
//                            System.out.println(" ~~~~~~~~ " + model.pieceAt(row, col).type());
                            for (int i = 0; i < moves.size(); i++) {
                                board[moves.get(i).newRow][moves.get(i).newColumn].setBorder(new LineBorder(Color.blue, 5));
//                                System.out.println("Row: " + moves.get(i).newRow + " Column: " + moves.get(i).newColumn);
                            }
                            //a border so you can see which piece is selected
							board[row][col].setBorder(new LineBorder(Color.orange, 5));
							pieceChosen = true;
						} else if (pieceChosen //stores the second pressed button.
                                    ){
							move.newColumn = col;
							move.newRow = row;
                            for (int r = 0; r < board.length; r++) {
                                for (int c = 0; c < board.length; c++) {
                                    if (board[r][c].getBorder() != null)
                                        board[r][c].setBorder(null);
                                }
                            }
							//not sure if he wants an invalid move to throw an error
                            try {
                                model.move(move);//The move method that is called here will check for validity.
                                model.addToMoveStack(move);
                                model.switchPlayer();
                                currentPlayer = model.currentPlayer();
                                model.addToMoveStack(move);
                                move = new Move(); //to prevent null pointer errors from trying to move a piece that isn't there anymore
                            } catch (IllegalArgumentException e) {
//                                System.out.println("Illegal Move");
                            }
							pieceChosen = false;
                            displayBoard();
						}
					}
		}
	}
}