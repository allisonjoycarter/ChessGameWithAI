package ChessW18;

import javax.swing.*;

public class ChessGUI {

 private static JMenu     fileMenu;
 private static JMenuItem quitItem;
 private static JMenuItem gameItem;
 private static JMenuItem undoMove;
 private static JMenuItem time;
 private static JMenuItem start;
 private static JMenuItem stop;
 private static JCheckBoxMenuItem countUp;
 private static JMenuItem setTimer;
 private static JMenuBar  menus;

 public static void main(String[] args) {
  JFrame frame = new JFrame("Chess Game");
  frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

  fileMenu = new JMenu("File");
  quitItem = new JMenuItem("Quit");
  quitItem.addActionListener(e -> System.exit(0));
  gameItem = new JMenuItem("New Game");
  undoMove = new JMenuItem("Undo Move");
  JCheckBoxMenuItem enableAI = new JCheckBoxMenuItem("Enable AI");
  JMenuItem saveGame = new JMenuItem("Save Game");
  JMenuItem loadGame = new JMenuItem("Load Game");

  time = new JMenuItem("0:00:00");
  start = new JMenuItem("Start");
  stop = new JMenuItem("Stop");
  countUp = new JCheckBoxMenuItem("Count Up");
  setTimer = new JMenuItem("Set Time");

  JCheckBoxMenuItem colorBlind = new JCheckBoxMenuItem("Color Blind Mode");

  fileMenu.add(gameItem);
  fileMenu.add(quitItem);
  fileMenu.add(saveGame);
  fileMenu.add(loadGame);
  fileMenu.add(colorBlind);

  menus = new JMenuBar();
  frame.setJMenuBar(menus);
  menus.add(fileMenu);
  menus.add(undoMove);
  menus.add(enableAI);

  ChessPanel panel = new ChessPanel(gameItem, saveGame, loadGame, colorBlind, undoMove, enableAI);

  frame.getContentPane().add(panel);

  frame.pack();
  frame.setVisible(true);
 }
}
