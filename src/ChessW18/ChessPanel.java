package ChessW18;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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


	// declare other instance variables as needed

	private ButtonListener buttonListener = new ButtonListener();

	public ChessPanel() {
		model = new ChessModel();
		move = new Move();
		board = new JButton[BOARDSIZE][BOARDSIZE];
		boardInit();
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
		wKnight = new ImageIcon("wKnight");
		wKing = new ImageIcon("wKing");
		wQueen = new ImageIcon("wQueen");

		//This images still need to be added to the repository
		bPawn = new ImageIcon("bPawn");
		bRook = new ImageIcon("bRook");
		bBishop = new ImageIcon("bBishop");
		bKnight = new ImageIcon("bKnight");
		bKing = new ImageIcon("bKing");
		bQueen = new ImageIcon("bQueen");

		ButtonListener listener = new ButtonListener();

		//Creates buttons for the white pieces
		board[0][0] = new JButton(wRook);
		board[0][1] = new JButton(wKnight);
		board[0][2] = new JButton(wBishop);
		board[0][3] = new JButton(wKing);
		board[0][4] = new JButton(wQueen);
		board[0][5] = new JButton(wBishop);
		board[0][6] = new JButton(wKnight);
		board[0][7] = new JButton(wRook);

		//Creates buttons for the black pieces
		board[7][0] = new JButton(bRook);
		board[7][1] = new JButton(bKnight);
		board[7][2] = new JButton(bBishop);
		board[7][3] = new JButton(bKing);
		board[7][4] = new JButton(bQueen);
		board[7][5] = new JButton(bBishop);
		board[7][6] = new JButton(bKnight);
		board[7][7] = new JButton(bRook);


		setLayout(new GridBagLayout()); /** I'm setting this up so that
		 the buttons will fall into place when created */
		GridBagConstraints loc;

		//Creates all of the pawns.
		for(int row = 0; row < BOARDSIZE; row++)
			for(int col = 0; col < BOARDSIZE; col++) {

				if(row == 1)
					board[row][col] = new JButton(wPawn);

				else if(row == 6)
					board[row][col] = new JButton(bPawn);

				else
					board[row][col] = new JButton();

				board[row][col].addActionListener(listener);
				add(board[row][col]);
			}

		message = new JLabel("");
		model.reset();
	}


	/**
	 * Method that updates the board.
	 *
	 * needs to be finished
	 */
	private void displayBoard() {
		board[move.newRow][move.newColumn] =
				board[move.oldRow][move.oldColumn];
		board[move.oldRow][move.oldColumn] = new JButton();

		message = new JLabel(model.getMessage(),
				(model.getMessage()).length());

		repaint();
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
			for (int row = 0; row < 8; row++)
				for (int col = 0; col < 8; col++)
					if (event.equals(board[row][col])) {
						if(!pieceChosen) { //stores the first pressed button.
							move.oldColumn = col;
							move.oldRow = row;
							pieceChosen = false;
						}
						else { //stores the second pressed button.
							move.newColumn = col;
							move.oldRow = row;
							pieceChosen = true;
						}
					}

			model.move(move);//The move method that is called here will check for validity.
			displayBoard();
		}



	}


}