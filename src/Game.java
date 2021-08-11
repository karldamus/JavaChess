import game.Board;
import game.Constants;
import game.Space;
import pieces.Piece;
//import stockfish.Stockfish;
import stockfish.Client;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class Game extends JLayeredPane implements Constants {

    private JFrame gameJFrame;
    private JPanel boardPanel;
    private Board board;
    private int stockfishLevel = 3000; // difficulty -- wait time in milliseconds (1000 - 10000)

    private boolean twoPlayer = false;

    JPanel fenStringContainer;
    JTextPane fenString;

    /**
     *
     */
    public Game() {
        board = new Board();

        setUpGui();
    }

    public void update() {

    }

    /**
     * Create Game window with all elements:
     * <li>Board</li>
     * <li>Menus</li>
     * <li>Icons</li>
     */
    public void setUpGui() {
        // setup JFrame
        gameJFrame = new JFrame(GAME_TITLE);
        gameJFrame.setMinimumSize(BOARD_SIZE);

        // setup board JPanel
        boardPanel = new JPanel(BOARD_GRID_LAYOUT);
        boardPanel.setSize(BOARD_WIDTH, BOARD_HEIGHT);
        boardPanel.setMaximumSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        boardPanel.setMinimumSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));

        // add spaces to board gui
        for (int rank = 0; rank < RANKS; rank++) {
            for (int file = 0; file < FILES; file++) {
                boardPanel.add(board.getBoard()[rank][file].getSpaceJPanel());
            }
        }

        // add JPanel to JFrame
        gameJFrame.getContentPane().add(boardPanel, BorderLayout.CENTER);

        // setup custom mouse adapter
        MyMouseAdapter myMouseAdapter = new MyMouseAdapter();
        boardPanel.addMouseListener(myMouseAdapter);
        boardPanel.addMouseMotionListener(myMouseAdapter);

        // add fenString
        fenStringContainer = new JPanel();
        fenString = new JTextPane();
        fenString.setContentType("text/html");
        fenString.setText("<html>"+board.getFen().getFenString()+"</html>");
        fenString.setEditable(false);
        fenStringContainer.add(fenString);
        gameJFrame.add(fenStringContainer, BorderLayout.SOUTH);

        // make JFrame visible
        gameJFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameJFrame.pack();
        gameJFrame.setLocationRelativeTo(null);
        gameJFrame.setVisible(true);
        gameJFrame.revalidate();
        gameJFrame.repaint();
    }


    /**
     * MouseAdapter class allows movement on gui, valid moves are still determined via Board class.
     * This custom private MouseAdapter is sourced from 'Hovercraft Full Of Eels' on Stackoverflow:
     * @see <a href="https://stackoverflow.com/a/4894516/13280626">Hovercraft Full of Eels on Stackoverflow</a>
     */
    private class MyMouseAdapter extends MouseAdapter {
        private JLabel dragLabel = null;
        private int dragLabelWidthDiv2;
        private int dragLabelHeightDiv2;
        private JPanel clickedPanel = null;

        @Override
        public void mousePressed(MouseEvent me) {
            clickedPanel = (JPanel) boardPanel.getComponentAt(me.getPoint());
            Component[] components = clickedPanel.getComponents();
            if (components.length == 0) {
                return;
            }
            // if JLabel is component of the clickedPanel
            if (components[0] instanceof JLabel) {
                // remove label from panel
                dragLabel = (JLabel) components[0];
                clickedPanel.remove(dragLabel);
                clickedPanel.revalidate();
                clickedPanel.repaint();

                // setup dragLabel
                dragLabelWidthDiv2 = dragLabel.getWidth() / 2;
                dragLabelHeightDiv2 = dragLabel.getHeight() / 2;
                int x = me.getPoint().x - dragLabelWidthDiv2;
                int y = me.getPoint().y - dragLabelHeightDiv2;
                dragLabel.setLocation(x, y);

                // add dragLabel to Layered Pane
                add(dragLabel, JLayeredPane.DRAG_LAYER);
                repaint();
            }
        }

        @Override
        public void mouseDragged(MouseEvent me) {
            if (dragLabel == null) {
                return;
            }

            // continuously update position of dragLabel
            int x = me.getPoint().x - dragLabelWidthDiv2;
            int y = me.getPoint().y - dragLabelHeightDiv2;
            dragLabel.setLocation(x, y);
            repaint();
        }

        @Override
        public void mouseReleased(MouseEvent me) {
            // check if a piece is being dragged across the board
            if (dragLabel == null) {
                return;
            }

            // remove JLabel from Layered Pane
            remove(dragLabel);

            // create droppedPanel where mouseReleased
            JPanel droppedPanel = (JPanel) boardPanel.getComponentAt(me.getPoint());

            // if off the grid, return label to home
            if (droppedPanel == null) {
                clickedPanel.add(dragLabel);
                clickedPanel.revalidate();
            }
            // find location where piece dropped
            else {
                int r = -1;
                int f = -1;
                search_board_getBoard:
                for (int rank = 0; rank < board.getBoard().length; rank++) {
                    for (int file = 0; file < board.getBoard()[rank].length; file++) {
                        if (board.getBoard()[rank][file].getSpaceJPanel() == droppedPanel) {
                            r = rank;
                            f = file;
                            break search_board_getBoard;
                        }
                    }
                }

                // if off the grid, return label to origin
                if (r == -1 || f == -1) {
                    clickedPanel.add(dragLabel);
                    clickedPanel.revalidate();
                }
                // test movement
                else {
                    // get origin and final ranks and files
                    int originFile = getFileIndex(clickedPanel.getName().split("")[0]);
                    int finalFile = getFileIndex(droppedPanel.getName().split("")[0]);
                    int originRank = Integer.parseInt(clickedPanel.getName().split("")[1]);
                    int finalRank = Integer.parseInt(droppedPanel.getName().split("")[1]);

                    // move piece on back-end (board.getBoard())
                    board.movePiece(originRank, originFile, finalRank, finalFile);

                    // move ok! update on gui
                    if (board.moved) {
                        droppedPanel.remove(0);
                        droppedPanel.add(dragLabel);
                        droppedPanel.revalidate();

                        fenString.setText("<html>"+board.getFen().getFenString()+"</html>");

                        boardPanel.repaint();
                        boardPanel.revalidate();
                    }
                    // move not ok! return draglabel to origin (clickedPanel)
                    else {
                        clickedPanel.add(dragLabel);
                        clickedPanel.revalidate();
                    }

                    // reset board.moved
                    board.moved = false;
                }
            }

            // end mouseReleased()
            repaint();
            dragLabel = null;

//            aiMove();
        }
    }

    public void aiMove() {
        if (!twoPlayer) {
            String bestMove = "";
            try {
                bestMove = board.bestMove(board.getFen().getFenString());
            } catch (IOException | ExecutionException | InterruptedException | TimeoutException e) {
                e.printStackTrace();
            }

            System.out.println("Fen: " + board.getFen().getFenString());
            System.out.println("Best move: " + bestMove);
            String[] bestMoveArr = bestMove.split("");

            String[] bestMoveOrigin = new String[] { bestMoveArr[0], bestMoveArr[1]};
            String[] bestMoveDestination = new String[] {bestMoveArr[2], bestMoveArr[3]};
            int[] bestMoveOriginCoordinates = board.getArrayPositionFromBoardCoordinates(bestMoveOrigin);
            int[] bestMoveDestinationCoordinates = board.getArrayPositionFromBoardCoordinates(bestMoveDestination);

            Component[] originComponents = board.getBoard()[bestMoveOriginCoordinates[0]][bestMoveOriginCoordinates[1]].getSpaceJPanel().getComponents();

            board.getBoard()[bestMoveDestinationCoordinates[0]][bestMoveDestinationCoordinates[1]].getSpaceJPanel().remove(0);
            board.getBoard()[bestMoveDestinationCoordinates[0]][bestMoveDestinationCoordinates[1]].getSpaceJPanel().add(originComponents[0]);

            board.getBoard()[bestMoveDestinationCoordinates[0]][bestMoveDestinationCoordinates[1]].getSpaceJPanel().repaint();
            board.getBoard()[bestMoveDestinationCoordinates[0]][bestMoveDestinationCoordinates[1]].getSpaceJPanel().revalidate();

            board.moved = false;
            board.movePiece(bestMove);
            board.moved = false;

//            board.getBoard()[bestMoveDestinationCoordinates[0]][bestMoveDestinationCoordinates[1]].getSpaceJPanel().repaint();
//            board.getBoard()[bestMoveDestinationCoordinates[0]][bestMoveDestinationCoordinates[1]].getSpaceJPanel().revalidate();

            // update gui
            fenString.setText("<html>"+board.getFen().getFenString()+"</html>");

            boardPanel.repaint();
            boardPanel.revalidate();
        }
    }

    public int getFileIndex(String file) {
        System.out.println("File " + file + " is: " + file.getClass());
        String[] fileArray = new String[] {"a", "b", "c", "d", "e", "f", "g", "h"};

        for (int i = 0; i < fileArray.length; i++) {
            if (fileArray[i].equals(file)) {
                return i;
            }
        }

        return -1;
    }

    private void revalidateSquarePanels(int x_origin, int x_destination, int y_origin, int y_destination) {
        board.getBoard()[x_origin][y_origin].getSpaceJPanel().revalidate();
        board.getBoard()[x_origin][y_origin].getSpaceJPanel().repaint();
        board.getBoard()[x_destination][y_destination].getSpaceJPanel().revalidate();
        board.getBoard()[x_destination][y_destination].getSpaceJPanel().repaint();
        boardPanel.repaint();
        boardPanel.revalidate();
    }



    public static void main(String[] args) {
        Game game = new Game();
        game.board.getFen().printFenString();

    }


}
