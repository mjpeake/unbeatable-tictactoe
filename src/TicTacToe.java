import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class TicTacToe extends JFrame {
    // Board Dimensions
    private final int BOARD_WIDTH = 670;
    private final int BOARD_HEIGHT = 630;
    private final int BOARD_MARGIN = 100;
    private final int FONT_SIZE = 125;
    private final int DIVIDER_SIZE = 5;
    private final int BOX_SIZE = 150;

    private Game game = new Game();
    private Board board = game.getBoard();
    private Player player = game.getPlayer();
    private AI ai = game.getAI();

    private Canvas canvas;
    private boolean browserReadySent = false;
    private static native void browserReady();

    public TicTacToe() {
        class CanvasMouseListener implements MouseListener
        {
            public void mousePressed(MouseEvent event) {
                if(!game.isOver()) {
                    // Check Click is inside Board
                    if ((event.getX() > (BOARD_MARGIN + DIVIDER_SIZE) && event.getX() < (BOARD_WIDTH - BOARD_MARGIN - DIVIDER_SIZE)) && (event.getY() > (BOARD_MARGIN + DIVIDER_SIZE) && event.getY() < (BOARD_WIDTH - BOARD_MARGIN - DIVIDER_SIZE))) {
                        // Determine square clicked
                        int row = (event.getY() - BOARD_MARGIN - (DIVIDER_SIZE * 3)) / BOX_SIZE;
                        int col = (event.getX() - BOARD_MARGIN - (DIVIDER_SIZE * 3)) / BOX_SIZE;

                        if (board.isSquareEmpty(row, col)) {
                            player.playTurn(row,col,board);

                            // Check Conditions for Player
                            if (board.checkForWin(player.getMark())) {
                                game.setWin(player);
                            } else if (board.isFull()) {
                                game.setDraw();
                            } else {
                                // If no conditions met, AI Plays
                                ai.playTurn(board);

                                // Check Conditions for AI
                                if (board.checkForWin(ai.getMark())) {
                                    game.setWin(ai);
                                } else if (board.isFull()) {
                                    game.setDraw();
                                }
                            }
                        }
                    }
                } else {
                    game.newRound();
                }
                canvas.repaint();
            }
            public void mouseReleased(MouseEvent event) {
                ;
            }
            public void mouseClicked(MouseEvent event) {
                ;
            }
            public void mouseEntered(MouseEvent event) {
                ;
            }
            public void mouseExited(MouseEvent event) {
                ;
            }
        }

        // The web wrapper supplies its own frame; keep desktop launches unchanged.
        if (Boolean.getBoolean("tictactoe.web")) {
            setUndecorated(true);
            setResizable(false);
        }
        setTitle("TicTacToe");
        setLayout(new BorderLayout());  // Layout manager for the frame.

        canvas = new Canvas();
          canvas.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
          canvas.setBackground(new Color(59,71,96));
          canvas.addMouseListener(new CanvasMouseListener());
        add(canvas, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        pack();
        setVisible(true);
    }

    // Drawing area class (inner class).
    class Canvas extends JPanel
    {
        // Called every time there is a change in the canvas contents.
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            draw(g);
            // Tell the wrapper only after the board has actually painted.
            if (Boolean.getBoolean("tictactoe.web") && !browserReadySent) {
                browserReadySent = true;
                browserReady();
            }
        }

        // Called by the canvas' paintComponent method
        void draw(Graphics g)
        {
            // Draw Names
            g.setColor(Color.white);
            g.setFont(new Font("TimesRoman", Font.BOLD, 40));
            g.drawString("YOU", BOARD_MARGIN, 70);
            g.drawString("CPU", BOARD_WIDTH - BOARD_MARGIN - 90, 70);

            // Draw Mark Allocation
            g.setColor(new Color(87,101,128)); // Mark Backdrop Colour
            g.fillOval((BOARD_WIDTH/2) - 55,30,50,50);
            g.fillOval((BOARD_WIDTH/2) + 5,30,50,50);
            g.setColor(Color.white);
            g.drawString("" + player.getMark(), (BOARD_WIDTH/2) - 44, 70);
            g.drawString("" + ai.getMark(), (BOARD_WIDTH/2) + 16, 70);

            // Draw Score
            g.setColor(new Color(42,50 ,68)); // Score Backdrop Colour
            g.fillOval((BOARD_WIDTH/2) - 130,40,30,30);
            g.fillOval((BOARD_WIDTH/2) + 100,40,30,30);
            g.setColor(new Color(168,182,211)); // Score Colour
            g.setFont(new Font("TimesRoman", Font.BOLD, 20));
            // Center number based on digits
            if(player.getScore() < 10) {
                g.drawString("" + player.getScore(), (BOARD_WIDTH / 2) - 120, 62);
            } else {
                g.drawString("" + player.getScore(), (BOARD_WIDTH / 2) - 125, 62);
            }
            if(ai.getScore() < 10) {
                g.drawString("" + ai.getScore(), (BOARD_WIDTH / 2) + 110, 62);
            } else {
                g.drawString("" + ai.getScore(), (BOARD_WIDTH / 2) + 105, 62);
            }


            // Draw Lines
            g.setColor(new Color(43,51,70));
            for(int i = 0; i < 4; i++) {
                g.fillRect(BOARD_MARGIN, i * (BOX_SIZE + DIVIDER_SIZE) + BOARD_MARGIN, board.getSize() * (BOX_SIZE + DIVIDER_SIZE), 4);
                g.fillRect(i * (BOX_SIZE + DIVIDER_SIZE) + BOARD_MARGIN, BOARD_MARGIN, 4 , board.getSize() * (BOX_SIZE + DIVIDER_SIZE) + 4);
            }

            // Draw Marks
            for(int row = 0; row < board.getSize(); row++){
                for(int col = 0; col < board.getSize(); col++){
                    int positionX = col * (BOX_SIZE + DIVIDER_SIZE) + BOARD_MARGIN;
                    int positionY = row * (BOX_SIZE + DIVIDER_SIZE) + BOARD_MARGIN;

                    g.setColor(Color.white);
                    g.setFont(new Font("TimesRoman", Font.BOLD, FONT_SIZE));
                    g.drawString("" + board.getSquare(row,col), positionX + (FONT_SIZE / 4), positionY + (FONT_SIZE));
                }
            }

            // Draw Result if needed
            if(game.isOver()) {
                int boxWidth = 300;
                int boxHeight = 150;

                g.setColor(new Color(42,50 ,68));
                g.fillRect((BOARD_WIDTH/2) - (boxWidth/2),(BOARD_HEIGHT/2) - (boxHeight/2) + 5,boxWidth,boxHeight);

                g.setColor(Color.white);

                if(game.isDraw()) {
                    g.setFont(new Font("TimesRoman", Font.BOLD, 75));
                    g.drawString("draw", (BOARD_WIDTH/2) - (boxWidth/2) + 60, (BOARD_HEIGHT/2) - (boxHeight/2) + 80);
                } else if (game.isWin()) {
                    g.setFont(new Font("TimesRoman", Font.BOLD, 60));
                    if(game.getWinMark() == player.getMark()) {
                        g.drawString("you won!", (BOARD_WIDTH/2) - (boxWidth/2) + 20, (BOARD_HEIGHT/2) - (boxHeight/2) + 75);
                    } else if(game.getWinMark() == ai.getMark()) {
                        g.drawString("cpu won!", (BOARD_WIDTH/2) - (boxWidth/2) + 20, (BOARD_HEIGHT/2) - (boxHeight/2) + 75);
                    }
                }
                g.setFont(new Font("TimesRoman", Font.BOLD, 30));
                g.drawString("click to restart", (BOARD_WIDTH/2) - (boxWidth/2) + 45, (BOARD_HEIGHT/2) - (boxHeight/2) + 120);
            }
        } // end draw method
    } // end inner class Canvas

    public static void main(String[] args) { TicTacToe tictactoe = new TicTacToe(); }
}
