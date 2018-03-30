package ChessW18;

import jdk.nashorn.internal.scripts.JO;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;

public class ChessPanel extends JPanel {

    private JMenuItem resetGame;

	/** button to undo the last move */
    private JMenuItem undoMove;

    /** because extra credit is key */
    private JCheckBoxMenuItem colorBlind;

    private JMenuItem saveGame;
    private JMenuItem loadGame;

    /** holds the buttons that make up the game board */
	private JButton[][] board;

	/** holds the game engine */
	private ChessModel model;

	/** variable to hold the size of the board */
	private final int BOARDSIZE = 8;

	private JLabel outMessage;
    private String message;

    /** images to represent white player pieces */
	private ImageIcon wPawn, wRook, wBishop, wKnight, wKing, wQueen;

	/** images to represent black player pieces */
	private ImageIcon bPawn, bRook, bBishop, bKnight, bKing, bQueen;

	/** the move attempted when selecting buttons on the board */
	private Move move;

	/** pieceChoice is used to tell if the player is choosing a piece
	 * or a position. */
	private boolean pieceChosen;

	/** the current player */
	private Player currentPlayer;

    /** panels to hold images of each player's captures */
    private JPanel whiteCapturePanel, blackCapturePanel;

    /** holds labels that will have an icon set to represent captures for each player */
    private ArrayList<JLabel> whiteCaptures = new ArrayList<>();
    private ArrayList<JLabel> blackCaptures = new ArrayList<>();

    /** if true, competitive rules will apply on the timer */
    private boolean competitiveTimer = true;

    /** timer to count up or down and time gameplay */
    private Timer timer;

    /** variables to hold the amount of time on the timer */
    private int hours, minutes, seconds;

    /** holds amount of time (seconds) left for each player */
    private int whiteTime, blackTime;

    private JLabel timerLabel;

    private JTextArea moveSequence;

    private boolean AIEnabled;

    public ChessPanel(JMenuItem pgameItem, JMenuItem saveGame, JMenuItem loadGame,
                      JCheckBoxMenuItem colorBlind, JMenuItem undoMove, JCheckBoxMenuItem enableAI) {

        model = new ChessModel();
		move = new Move();
		currentPlayer = model.currentPlayer();
		pieceChosen = false;
		board = new JButton[BOARDSIZE][BOARDSIZE];
        JPanel boardPanel = boardInit();
        displayBoard();

        resetGame = pgameItem;
        resetGame.addActionListener(e -> resetBoard());

        this.saveGame = saveGame;
        this.saveGame.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int value = fileChooser.showSaveDialog(null);
            if (value == JFileChooser.APPROVE_OPTION)
                model.getHandler().recordGameData(
                        fileChooser.getSelectedFile().getAbsolutePath());
        });

        this.loadGame = loadGame;
        this.loadGame.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int value = fileChooser.showOpenDialog(null);
            if (value == JFileChooser.APPROVE_OPTION) {
                try {
                    resetBoard();
                    ArrayList<String> moves = model.getHandler().separateMoves(
                            model.getHandler().readGameFile(
                                    fileChooser.getSelectedFile().getAbsolutePath()));
                        for (String move : moves) {
                            Move temp = model.getHandler().decodeMove(move);
                            model.getHandler().moveAndAddToSequence(temp);
                            model.switchPlayer();
                            currentPlayer = model.currentPlayer();
                        }
                        displayBoard();
                        updateCaptures();
                        moveSequence.setText(model.getGameData());
                } catch (IllegalArgumentException err) {
                    JOptionPane.showMessageDialog(null,
                            "Illegal moves in document. Failed to load moves.",
                            "Could Not Load Moves", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
		this.undoMove = undoMove;
		this.colorBlind = colorBlind;
		AIEnabled = false;
		enableAI.setSelected(false);
		enableAI.addActionListener(e -> {
		    AIEnabled = !AIEnabled;
		    resetBoard();
        });

        timer = new Timer(1000, new TimerListener());
        timer.setActionCommand("Down");
        whiteTime = blackTime = 30 * 60;

		this.undoMove.addActionListener(e -> {
            model.undoLastMove(); //undo's previous move
            currentPlayer = model.currentPlayer(); //make sure player is switched
            moveSequence.setText(model.getGameData());
            displayBoard(); //set the icons
            updateCaptures();
        });
		this.colorBlind.addActionListener(e -> {
		    if (this.colorBlind.isSelected())
		        //setting the color for the grid of icons
		        //black and white is harsh on the eyes and
		        //doesn't show the pieces very well
		        for(int row = 0; row < BOARDSIZE; row++)
		            for(int col = 0; col < BOARDSIZE; col++)
		                if ((row % 2 == 1 && col % 2 == 1) ||
                        (row % 2 == 0 && col % 2 == 0)) {
		                    //color blind friendly white
                            board[row][col].setBackground(new Color(240, 228, 66));
                        } else {
		                    //color blind friendly black
                            board[row][col].setBackground(new Color(0, 114, 178));
                        }
		    else
                for (int row = 0; row < BOARDSIZE; row++)
                    for (int col = 0; col < BOARDSIZE; col++)
                        if ((row % 2 == 1 && col % 2 == 1) ||
                                (row % 2 == 0 && col % 2 == 0))
                            //light yellow to contrast white pieces
                            board[row][col].setBackground(new Color(246, 249, 182));
                        else
                            //dark green to contrast black pieces
                            board[row][col].setBackground(new Color(72, 109, 42));

            });

		//setting the timer to start with a modest amount of time
		minutes = 30;

        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        //panel with the chess board
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.gridwidth = 2;
        constraints.gridheight = 2;
        add(boardPanel, constraints);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHEAST;
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        add(createTimerPanel(), constraints);

        //panel for the captures
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 2;
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.SOUTHEAST;
        constraints.gridwidth = 1;
        constraints.gridheight = 2;
        add(createCapturesPanel(), constraints);

        moveSequence = new JTextArea();
        moveSequence.setBackground(new Color(189, 221, 175));
        moveSequence.setFont(new Font(UIManager.getDefaults().getFont("Label.font").getName(), Font.PLAIN, 25));
        moveSequence.setWrapStyleWord(true);
        JScrollPane movesPane = new JScrollPane(moveSequence);
        movesPane.setPreferredSize(new Dimension(this.getWidth(), 100));
        movesPane.setBorder(new LineBorder(new Color(0, 114, 178), 5));
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.anchor = GridBagConstraints.SOUTHWEST;
        constraints.gridwidth = 3;
        constraints.gridheight = 1;
        add(movesPane, constraints);



    }

    /**
     * sets minutes and hours correctly if seconds > 60 or seconds < -60
     */
    private void fixTime() {

        //if in negative time, set correct hours
        while (minutes < 0) {
            minutes += 60;
            hours--;
        }

        //if in negative time, set the correct minutes
        while (seconds < 0) {
            seconds += 60;
            minutes--;
        }

        //if there are too many seconds, add to minutes
        while (seconds >= 60) {
            seconds -= 60;
            minutes++;
        }

        //if there are too many minutes, add to hours
        while (minutes >= 60) {
            minutes -= 60;
            hours++;
        }
    }

    /******************************************************************
     * Method to set the time to desired amount of seconds and convert
     * to minutes and hours
     *
     * @param seconds the amount of seconds to set the timer to
     *****************************************************************/
    private void setTime(int seconds) {
        minutes = hours = 0;
        this.seconds = seconds;
        fixTime();
    }

    /******************************************************************
     * Listener for the timer to increment or decrement time
     *****************************************************************/
    public class TimerListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (timer.getActionCommand().equals("Up")) {
                seconds++;
            } else if (timer.getActionCommand().equals("Down")) {
                    seconds--;
                    //if using the competitive timer, time should be taken away
                    //from the right player
                    if (competitiveTimer) {
                        if (currentPlayer == Player.WHITE)
                            whiteTime--;
                        else
                            blackTime--;
                    }
            }
            fixTime();

            //setting the label to take the form 0:00:00
            if (seconds < 0 || minutes < 0 || hours < 0)
                timerLabel.setText(hours + ":" + minutes + ":" + seconds);
            else if (minutes >= 10 && seconds >= 10)
                timerLabel.setText(hours + ":" + minutes + ":" + seconds);
            else if (minutes >= 10 && seconds < 10)
                timerLabel.setText(hours + ":" + minutes + ":0" + seconds);
            else if (minutes < 10 && seconds >= 10)
                timerLabel.setText(hours + ":0" + minutes + ":" + seconds);
            else
                timerLabel.setText(hours + ":0" + minutes + ":0" + seconds);

            //show a dialog if a player runs out of time
            if (competitiveTimer)
                if (whiteTime == 0)
                    JOptionPane.showMessageDialog(null,
                            "White Player has run out of time!",
                            "Time Out", JOptionPane.WARNING_MESSAGE);
                else if (blackTime == 0)
                    JOptionPane.showMessageDialog(null,
                            "Black Player has run out of time!",
                            "Time Out", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     *
     * @return the panel that holds each player's captures
     */
    private JPanel createCapturesPanel() {

        whiteCapturePanel = new JPanel();
        whiteCapturePanel.setLayout(new GridLayout(4, 4, 7, 7));
        whiteCapturePanel.setBorder(new LineBorder(Color.white, 7));
        whiteCapturePanel.setBackground(new Color(189, 221, 175));

        blackCapturePanel = new JPanel();
        blackCapturePanel.setLayout(new GridLayout(4, 4, 7, 7));
        blackCapturePanel.setBorder(new LineBorder(Color.black, 7));
        blackCapturePanel.setBackground(new Color(189, 221, 175));

        JPanel captures = new JPanel();
        captures.setLayout(new GridLayout(2, 1));
        captures.add(whiteCapturePanel);
        captures.add(blackCapturePanel);
        captures.setPreferredSize(new Dimension(400, 500));

        return captures;
    }

    /**
     * Adds to the panel of captures if a piece is taken
     *
     */
    private void updateCaptures() {

        //adds the last element of the model.blackCaptures array if
        //its size is bigger than the blackCaptures JLabel array
	    if (model.getBlackCaptures().size() > blackCaptures.size())  {
            for (int i = model.getBlackCaptures().size() - blackCaptures.size(); i > 0; i--) {
                JLabel label = new JLabel();
                label.setIcon(getPieceIcon(Player.WHITE,
                        model.getBlackCaptures().get(model.getBlackCaptures().size() - i).type()));
                blackCaptures.add(label);
                blackCapturePanel.add(label);
            }

	        //or remove if needed
        } else if (model.getBlackCaptures().size() < blackCaptures.size()) {
	        blackCapturePanel.remove(blackCaptures.size() - 1);
	        blackCaptures.remove(blackCaptures.size() - 1);
        }

        //adds to the white captures panel if needed
        if (model.getWhiteCaptures().size() > whiteCaptures.size()) {
            for (int i = model.getWhiteCaptures().size() - whiteCaptures.size(); i > 0; i--) {
                JLabel label = new JLabel();
                label.setIcon(getPieceIcon(Player.BLACK, model.getWhiteCaptures().get(
                        model.getWhiteCaptures().size() - i).type()));
                whiteCaptures.add(label);
                whiteCapturePanel.add(label);
            }

	        //or remove if needed
        } else if (model.getWhiteCaptures().size() < whiteCaptures.size()) {
	        whiteCapturePanel.remove(whiteCaptures.size() - 1);
	        whiteCaptures.remove(whiteCaptures.size() - 1);
        }
        blackCapturePanel.repaint();
	    whiteCapturePanel.repaint();
    }

	/**
	 * Sets up the board, a two dimensional matrix of JButtons,
	 * to contain an icon for a chess piece in each button. The
	 * icons are specific to each button.
	 *
	 * @author George
	 * @version 3/18
	 */
	private JPanel boardInit() {

	    JPanel boardPanel = new JPanel();

		wPawn = new ImageIcon("wPawn.png");
		wRook = new ImageIcon("wRook.png");
		wBishop = new ImageIcon("wBishop.png");
		wKnight = new ImageIcon("wKnight.png");
		wKing = new ImageIcon("wKing.png");
		wQueen = new ImageIcon("wQueen.png");

		bPawn = new ImageIcon("bPawn.png");
		bRook = new ImageIcon("bRook.png");
		bBishop = new ImageIcon("bBishop.png");
		bKnight = new ImageIcon("bKnight.png");
		bKing = new ImageIcon("bKing.png");
		bQueen = new ImageIcon("bQueen.png");

		ButtonListener listener = new ButtonListener();

        boardPanel.setLayout(new GridLayout(9, 9));
        boardPanel.setPreferredSize(new Dimension(700, 700));

        int r = 8;
        char c = 'a';
		//Creating the grid of buttons
		for(int row = 0; row < 9; row++)
			for(int col = -1; col < 8; col++) {
                JLabel label = new JLabel();
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setVerticalAlignment(SwingConstants.CENTER);
                label.setFont(new Font("Serif", Font.PLAIN, 25));
                label.setForeground(new Color(165, 165, 165));
                label.setOpaque(true);
                label.setBackground(new Color(70, 70, 70));
		        if (col == -1 && row != 8) {
		            label.setText(Integer.toString(r));
		            boardPanel.add(label);
		            r--;
                }
                if (row == 8) {
		            if (col == -1)
		                boardPanel.add(label);
		            else {
                        label.setText(Character.toString(c));
                        boardPanel.add(label);
                        c++;
                    }
                } else if (col >= 0){
                    board[row][col] = new JButton();
                    board[row][col].addActionListener(listener);
                    //making a grid. maybe it should have its own panel?
                    if ((row % 2 == 1 && col % 2 == 1) ||
                            (row % 2 == 0 && col % 2 == 0))
                        board[row][col].setBackground(new Color(72, 109, 42));
                    else
                        board[row][col].setBackground(new Color(246, 249, 182));
                    boardPanel.add(board[row][col]);
                }
			}

		outMessage = new JLabel("");
        return boardPanel;
	}

	private JPanel createTimerPanel() {
	    JPanel panel = new JPanel();
	    panel.setLayout(new GridLayout(2, 3, 2 ,2));
	    timerLabel = new JLabel("0:30:00");
	    timerLabel.setFont(new Font(timerLabel.getFont().getName(), Font.PLAIN, 20));
	    timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    timerLabel.setVerticalAlignment(SwingConstants.CENTER);
	    timerLabel.setBorder(new LineBorder(new Color(165, 165, 165), 2));
	    JButton start = new JButton("Start");
	    start.addActionListener(e -> timer.start());
	    JButton stop = new JButton("Stop");
	    stop.addActionListener(e -> timer.stop());
	    JCheckBox competitive = new JCheckBox("Competitive Time");
        competitive.setSelected(true);
	    competitive.addActionListener(e -> competitiveTimer = !competitiveTimer);
	    competitive.setBorder(new LineBorder(new Color(165, 165, 165), 2));
	    competitive.setBorderPainted(true);
	    competitive.setHorizontalAlignment(SwingConstants.CENTER);
	    competitive.setVerticalAlignment(SwingConstants.CENTER);
	    JCheckBox countDown = new JCheckBox("Count Down");
	    countDown.setBorder(new LineBorder(new Color(165, 165, 165), 2));
	    countDown.setBorderPainted(true);
	    countDown.setVerticalAlignment(SwingConstants.CENTER);
	    countDown.setHorizontalAlignment(SwingConstants.CENTER);
	    countDown.setSelected(true);
	    countDown.addActionListener(e -> {
	        if (timer.getActionCommand().equals("Down"))
	            timer.setActionCommand("Up");
	        else
	            timer.setActionCommand("Down");
        });
	    JButton setTimer = new JButton("Set Timer");
	    setTimer.addActionListener(e -> {
            try {
                whiteTime = blackTime = seconds = 60 * Integer.parseInt(
                        JOptionPane.showInputDialog(null,
                                "Set the timer in minutes.", "Set Timer",
                                JOptionPane.QUESTION_MESSAGE));
            }
            catch(NumberFormatException event) {
                message = "Please enter a time of only minutes.";
            }
            fixTime();
            timerLabel.setText(hours + ":" + minutes + ":" + seconds);
        });
        panel.add(timerLabel);
        panel.add(start);
        panel.add(stop);
        panel.add(competitive);
        panel.add(countDown);
        panel.add(setTimer);
        panel.setPreferredSize(new Dimension(400, 200));

	    return panel;
    }


	/******************************************************************
	 * Method that updates the board by setting the correct icons.
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

                //set the icon to the correct piece and player
                board[row][col].setIcon(getPieceIcon(temp.player(), temp.type()));
			}
		}
//		outMessage.setText(message);
	}

	/***************************************************************
	 * Puts the board in the starting setup.
	 **************************************************************/
	private void resetBoard() {
		model.reset();
		currentPlayer = model.currentPlayer();
		blackCaptures = new ArrayList<>();
		whiteCaptures = new ArrayList<>();
		blackCapturePanel.removeAll();
		blackCapturePanel.repaint();
		whiteCapturePanel.removeAll();
		whiteCapturePanel.repaint();
		moveSequence.setText(model.getGameData());
		displayBoard();
		repaint();
	}


    /**
     * Returns the icon that matches a given player and piece type
     *
     * @param player player that owns the piece
     * @param type type of piece to get an icon for
     * @return the icon that matches a player and piece type
     */
    private ImageIcon getPieceIcon(Player player, String type) {
        ImageIcon icon = new ImageIcon();
        switch (type) {
            case "Pawn":
                if (player == Player.BLACK)
                    icon = bPawn;
                else
                    icon = wPawn;
                break;
            case "Knight":
                if (player == Player.BLACK)
                    icon = bKnight;
                else
                    icon = wKnight;
                break;
            case "Bishop":
                if (player == Player.BLACK)
                    icon = bBishop;
                else
                    icon = wBishop;
                break;
            case "Rook":
                if (player == Player.BLACK)
                    icon = bRook;
                else
                    icon = wRook;
                break;
            case "King":
                if (player == Player.BLACK)
                    icon = bKing;
                else
                    icon = wKing;
                break;
            case "Queen":
                if (player == Player.BLACK)
                    icon = bQueen;
                else
                    icon = wQueen;

        }
        return icon;
    }


	/**
	 * Inner class that represents action listener for buttons
	 */
	private class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			/*
			 * We need to store the location of the first button, and
			 * then store the location of the second button.
			 * -If the player chooses the first button, this will be
			 * dealt with through the move in ChessModel. The player
			 * must choose another button.
			 * -If the player chooses a different button, then this will
			 * be stored and model.move is called. The turn will be
			 * switched in chessModel.
			 *
			 * */
            String game;
			for (int row = 0; row < board.length; row++)
				for (int col = 0; col < board.length; col++)
					if (event.getSource() == board[row][col]) {
			            boolean check = model.inCheck(currentPlayer);
						if(!pieceChosen &&
                                model.pieceAt(row, col) != null && //need to make sure user is selecting an actual piece
                                model.pieceAt(row, col).player().equals(currentPlayer)) {//prevents playing for opponent
						        move.oldRow = row;
                                move.oldColumn = col;
						    if (!check) {
                                ArrayList<Move> moves = model.filterLegalMoves(model.legalMoves(row, col));
                                for (Move move : moves)
                                    board[move.newRow][move.newColumn].setBorder(new LineBorder(Color.blue, 5));

                                //a border so you can see which piece is selected
                                board[row][col].setBorder(new LineBorder(Color.orange, 5));
                            } else { //player in check should only be able to move out of check

                                ArrayList<Move> moves = model.movesToEscapeCheck(currentPlayer);
                                if (model.isComplete()) { //if there are no moves to escape check, then checkmate
                                    if (currentPlayer == Player.WHITE)
                                        JOptionPane.showMessageDialog(null,
                                                "Black has won!", "Checkmate",
                                                JOptionPane.INFORMATION_MESSAGE);
                                    else
                                        JOptionPane.showMessageDialog(null,
                                                "White has won!", "Checkmate",
                                                JOptionPane.INFORMATION_MESSAGE);
                                }
                                for (Move move : moves) {//show border for moves that get player out of check
                                    board[move.newRow][move.newColumn].setBorder(new LineBorder(Color.green, 5));
                                    board[move.oldRow][move.oldColumn].setBorder(new LineBorder(Color.red, 2));
                                }

                            }
                                pieceChosen = true;
						} else if (pieceChosen){ //stores the second pressed button.
							move.newColumn = col;
							move.newRow = row;
                            for (int r = 0; r < board.length; r++)
                                for (int c = 0; c < board.length; c++)
                                    if (board[r][c].getBorder() != null)
                                        board[r][c].setBorder(null);

							//not sure if he wants an invalid move to throw an error
                            try {
                                //this moves the pieces
                                //also creates a string in standard chess pieces to represent the move
                                //this also moves the piece
                                model.getHandler().moveAndAddToSequence(move);
                                moveSequence.setText(model.getGameData());
                                model.switchPlayer();
                                currentPlayer = model.currentPlayer();
                                move = new Move(); //to prevent null pointer errors from trying to move a piece that isn't there anymore
                                //set the timer to the current player's time
                                if (competitiveTimer)
                                    setTime(currentPlayer == Player.WHITE ? whiteTime : blackTime);

                                if (AIEnabled) {
                                    ChessAI ai = new ChessAI(Player.BLACK, model);
                                    Move aiMove = ai.aiMove();
                                    if (aiMove != null)
                                        model.getHandler().moveAndAddToSequence(aiMove);
                                    model.switchPlayer();
                                    currentPlayer = model.currentPlayer();

                                }
                            } catch (Exception e) {
                                message = "Illegal Move";
                            }

							pieceChosen = false;
                            displayBoard();
						}
						updateCaptures();
					}
		}
	}
}


//    /**
//     * Creates the timer panel and adds buttons to it
//     *
//     * @return the panel that holds the timer and its buttons
//     */
//    private JPanel createTimerPanel() {
//
//        JPanel timerPanel = new JPanel();
//        timerLabel = new JLabel("0:30:00");
//        timer = new Timer(1000, new TimerListener());
//        timer.setActionCommand("Down"); //timer starts counting down
//        timerPanel.add(timerLabel);
//
//        //to start the timer
//        JButton start = new JButton("Start");
//        start.addActionListener(e -> timer.start());
//        timerPanel.add(start);
//
//        //to stop the timer
//        JButton stop = new JButton("Stop");
//        stop.addActionListener(e -> timer.stop());
//        timerPanel.add(stop);
//
//        //to change whether timer counts up or down
//        JCheckBox countUp = new JCheckBox("Count Up");
////        countUp.setSelected(true);
//        countUp.addActionListener(e -> {
//            if (timer.getActionCommand().equals("Up"))
//                timer.setActionCommand("Down");
//            else
//                timer.setActionCommand("Up");
//        });
//        timerPanel.add(countUp);
//
//        //to set the timer
//        JButton setTimer = new JButton("Set Timer");
//        setTimer.addActionListener(e -> {
//            whiteTime = blackTime = seconds = 60 * Integer.parseInt(
//                    JOptionPane.showInputDialog(null,
//                            "Set the timer in minutes.", "Set Timer",
//                            JOptionPane.QUESTION_MESSAGE));
//            fixTime();
//            timerLabel.setText(hours + ":" + minutes + ":" + seconds);
//        });
//        timerPanel.add(setTimer);
//
//        //to change whether using competitive timer mode or not
//        JCheckBox useCompetitiveTimer = new JCheckBox("Competitive Timer");
//        useCompetitiveTimer.addActionListener(e -> {
//            competitiveTimer = !competitiveTimer;
//        });
//        timerPanel.add(useCompetitiveTimer);
//
//        return timerPanel;
//    }