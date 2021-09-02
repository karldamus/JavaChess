import game.Board;
import game.Constants;
//import stockfish.Stockfish;
import users.Settings;
import users.User;


import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.nio.Buffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class Game extends JLayeredPane implements Constants {

    // gui
    private JFrame gameJFrame;
    private JPanel boardPanel;
    private Board board;
    JPanel fenStringContainer;
    JTextPane fenString;
    JPanel leftMenuBar;

    JMenuBar menuBar;
    JMenu menu, submenu;
    JMenuItem menuItem;
    JRadioButtonMenuItem rbMenuItem;
    JCheckBoxMenuItem cbMenuItem;

    JOptionPane popup;
//    JFrame loginWindow;
//    JFrame registerWindow;
    JFrame authenticationWindow;

    // game data
    private int stockfishLevel = 3000; // difficulty -- wait time in milliseconds (1000 - 10000)
    private Settings settings; // default if no user

    // user data
    private boolean loggedIn;
    private User user;
    String tmpUsername;
    char[] tmpPassword;

    /**
     *
     */
    public Game() {
        board = new Board();

        setUpUser();
        setUpSettings();
        setUpGui();
        setUpMenuBar();
    }

    /**
     * Start the program.
     */
    public static void main(String[] args) {
        Game game = new Game();
    }

    public void update() {

    }

    // ================
    //     USERS
    // ================

    public void setUpUser() {
//        this.loggedIn = false;
//        user = null;
    }

    // backend login
    public void login(String username, char[] password) {
//        if (tmpUsername.equals("") || tmpPassword.length == 0) {
//            System.out.println("Username or password not filled out.");
//            displayAuthenticationWindow(true);
//        } else {
//            // check for user info in database
//            System.out.println("Login...");
//            System.out.println("Username: " + tmpUsername);
//            System.out.println("Password: " + tmpPassword);
//            this.loggedIn = true;
//            setUpSettings();
//            setUpMenuBar();
//        }

    }

    // backend logout
    public void logout() {
        if (this.loggedIn) {
            this.loggedIn = false;
            user = null;
        }
    }

    // backend registration
    public void register(String username, char[] password) {
        try {
            File usersDatabase = new File("src/users","users.txt");
            usersDatabase.createNewFile();

            // check if user already exists
            BufferedReader br = new BufferedReader(new FileReader(usersDatabase));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("username:")) {
                    String tmpUsername = line.split(":")[1];
                    if (username.equals(tmpUsername)) {
                        System.out.println("That username is taken");
                        return;
                    }
                }
            }

            // add user
            try(FileWriter fw = new FileWriter(usersDatabase, true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw))
            {
                out.println("USER {");
                out.println("username:" + username);
                out.println("password:" + new String(password));
                out.println("}");

                // successful registration, display login window
                displayAuthenticationWindow(true);

            } catch (IOException e) {
                System.out.println("Failed to add a new user to the database.");
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("An error occurred with registration.");
            e.printStackTrace();
        }
    }

    // visual login and registration
    public void displayAuthenticationWindow(boolean login) {
        // dispose of any current authentication window
        try {
            authenticationWindow.dispose();
        } catch (Exception e) { }

        authenticationWindow = new JFrame(login ? "Login" : "Register");

        // create panels
        JPanel mainPanel = new JPanel(new GridLayout(2,1));
        JPanel fieldsPanel = new JPanel(new GridLayout(2,1));
        JPanel usernamePanel = new JPanel();
        JPanel passwordPanel = new JPanel();
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // create components
        JLabel usernameMsg = new JLabel("Username:");
        JLabel passwordMsg = new JLabel("Password:");
        JTextField username = new JTextField();
        JPasswordField password = new JPasswordField();
        JButton confirmBtn = new JButton(login ? "Login" : "Register");
        JButton cancelBtn = new JButton("Cancel");

        // add event listeners
        confirmBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tmpUsername = username.getText();
                tmpPassword = password.getPassword();
                // todo: dispose of authenticationWindow after successful login
//                authenticationWindow.dispose();
                // todo: add arguments to login() and register()
                if (login)
                    login(tmpUsername, tmpPassword);
                else
                    register(tmpUsername, tmpPassword);
            }
        });

        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                authenticationWindow.dispose();
            }
        });

        // sizing
        authenticationWindow.setSize(BOARD_SIZE);
        mainPanel.setSize(BOARD_SIZE);
        username.setColumns(20);
        password.setColumns(20);

        // colouring
        confirmBtn.setBackground(Color.decode("#FF5733"));
        confirmBtn.setOpaque(true);
        confirmBtn.setBorderPainted(false);
        cancelBtn.setBackground(Color.decode("#C2C2C2"));
        cancelBtn.setOpaque(true);
        cancelBtn.setBorderPainted(false);


        // add components
        usernamePanel.add(usernameMsg);
        usernamePanel.add(username);
        passwordPanel.add(passwordMsg);
        passwordPanel.add(password);
        buttonsPanel.add(confirmBtn);
        buttonsPanel.add(cancelBtn);

        // add panels
        fieldsPanel.add(usernamePanel);
        fieldsPanel.add(passwordPanel);
        mainPanel.add(fieldsPanel);
        mainPanel.add(buttonsPanel);
        mainPanel.repaint();
        authenticationWindow.add(mainPanel);

        // visibility
        authenticationWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        authenticationWindow.setLocationRelativeTo(null);
        authenticationWindow.pack();
        authenticationWindow.setVisible(true);
    }

    // ================
    //     IN-GAME
    // ================

    /**
     * Start a new game.
     * @param twoPlayer whether to play against computer or not.
     * @see Board#twoPlayer
     */
    public void newGame(boolean twoPlayer) {
        if (board.gameInProgress) {
            int confirmNewGame = warningPopup("Starting a new game will forfeit this current game. Do you forfeit?",
                    new Object[]{"Forfeit game", "Continue playing"});

            if (confirmNewGame == JOptionPane.YES_OPTION)
                System.out.println("Forfeit");
            if (confirmNewGame == JOptionPane.NO_OPTION)
                return;
        }

        if (this.loggedIn) {
            int saveGame = confirmationPopup("Would you like to save your current game?");

            if (saveGame == JOptionPane.YES_OPTION)
                saveGame();
            if (saveGame == JOptionPane.CANCEL_OPTION)
                return;
        } else {
            int register = confirmationPopup("Would you like to login to save your current game?");

            // todo: BUG FIX - checks this.loggedIn before user gets chance to login
            if (register == JOptionPane.YES_OPTION) {
                displayAuthenticationWindow(true);
                // if successfully registered and/or logged in, repeat newGame()
                if (this.loggedIn)
                    newGame(twoPlayer);
            }
            if (register == JOptionPane.CANCEL_OPTION)
                return;
        }

        board = new Board();
        board.twoPlayer = twoPlayer;
        gameJFrame.setVisible(false);
        setUpGui();
        setUpMenuBar();
    }

    public void saveGame() {
        System.out.println("Saving game");
    }

    public void setUpSettings() {
        if (this.loggedIn) {
            // apply settings from user
            settings = user.getSettings();
        } else {
            setUpDefaultSettings();
        }
    }

    public void setUpDefaultSettings() {

    }

    // ================
    //     POP-UPS
    // ================

    public int confirmationPopup(String message) {
        return JOptionPane.showConfirmDialog(gameJFrame, message);
    }

    public int warningPopup(String message, Object[] options) {
        return JOptionPane.showOptionDialog(gameJFrame,
                message, "Warning",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options, options[1]);
    }


    // ================
    //       GUI
    // ================

    /**
     * Create Game window.
     */
    public void setUpGui() {
        // setup JFrame
        gameJFrame = new JFrame(GAME_TITLE);
        gameJFrame.setMinimumSize(BOARD_SIZE);
        gameJFrame.setMaximumSize(BOARD_SIZE);

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
     * Set up in-game menu bar.
     */
    public void setUpMenuBar() {
        String play = "Play";
        String theme = "Theme";
        String account = "Account";
        String settings = "Settings";

        menuBar = new JMenuBar();

        // play
        menu = new JMenu(play);
        menuItem = new JMenuItem(new AbstractAction("Computer") {
            @Override
            public void actionPerformed(ActionEvent e) {
                newGame(false);
            }
        });
        menu.add(menuItem);
        menuItem = new JMenuItem(new AbstractAction("Pass n Play") {
            @Override
            public void actionPerformed(ActionEvent e) {
                newGame(true);
            }
        });
        menu.add(menuItem);
        menuBar.add(menu);

        // theme
        menu = new JMenu(theme);
        menuBar.add(menu);

        // account
        menu = new JMenu(account);
        menuItem = new JMenuItem(new AbstractAction(loggedIn ? "Logout" : "Login") {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayAuthenticationWindow(true);
            }
        });
        menu.add(menuItem);
        menuItem = new JMenuItem(new AbstractAction("Register") {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayAuthenticationWindow(false);
            }
        });
        menu.add(menuItem);
        menuBar.add(menu);

        menu = new JMenu(settings);
        menuBar.add(menu);

        // add menuBar to JFrame
        gameJFrame.setJMenuBar(menuBar);

        gameJFrame.setVisible(false);
        gameJFrame.setVisible(true);
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

                    if (board.moved) {
                        // move ok! update on gui
                        droppedPanel.remove(0);
                        droppedPanel.add(dragLabel);
                        droppedPanel.revalidate();

                        fenString.setText("<html>"+board.getFen().getFenString()+"</html>");

                        droppedPanel.repaint();
                        boardPanel.repaint();
                        boardPanel.revalidate();

                        if (board.twoPlayer)
                            board.moved = false;
                    }
                    else {
                        // move not ok! return draglabel to origin (clickedPanel)
                        clickedPanel.add(dragLabel);
                        clickedPanel.revalidate();
                    }

                    // reset board.moved
//                    board.moved = false;
                }
            }

            // end mouseReleased()
            repaint();
            dragLabel = null;

            // ai move!
            if (board.moved && !board.twoPlayer) {
                aiMove();
                board.moved = false;
            }
        }
    }

    // ================
    //       AI
    // ================

    public void aiMove() {

        // find best move
        String bestMove = "";
        try {
            bestMove = board.bestMove(board.getFen().getFenString());
        } catch (Exception e) {
            System.out.println("Exception in aiMove method. Cannot find best move with given FEN string.");
            e.printStackTrace();
        }

        // move piece on backend
        board.movePiece(bestMove);

        if (board.moved) {
            revalidateAllSquarePanels();
            board.moved = false;
        }

    }

    // ================
    //     HELPERS
    // ================

    public int getFileIndex(String file) {
        String[] fileArray = new String[] {"a", "b", "c", "d", "e", "f", "g", "h"};

        for (int i = 0; i < fileArray.length; i++) {
            if (fileArray[i].equals(file)) {
                return i;
            }
        }

        return -1;
    }

    private void revalidateAllSquarePanels() {
        for (int rank = 0; rank < RANKS; rank++) {
            for (int file = 0; file < FILES; file++) {
                board.getBoard()[rank][file].getSpaceJPanel().revalidate();
                board.getBoard()[rank][file].getSpaceJPanel().repaint();
            }
        }
        boardPanel.repaint();
        boardPanel.revalidate();
    }

    private void repaintGui() {
        for (int rank = 0; rank < RANKS; rank++) {
            for (int file = 0; file < FILES; file++) {
                board.getBoard()[rank][file].getSpaceJPanel().repaint();
                board.getBoard()[rank][file].getSpaceJPanel().revalidate();
            }
        }
        boardPanel.repaint();
        boardPanel.revalidate();
        gameJFrame.pack();
        gameJFrame.setLocationRelativeTo(null);
        gameJFrame.setVisible(false);
        gameJFrame.setVisible(true);
        gameJFrame.revalidate();
        gameJFrame.repaint();
    }


}
