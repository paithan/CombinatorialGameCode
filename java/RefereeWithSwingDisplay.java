/**
 * A Referee for Combinatorial Games that displays the current position of the game using Swing graphics. 
 *
 * @author Kyle Burke <paithanq@gmail.com>
 */
 
import java.lang.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.text.NumberFormat;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class RefereeWithSwingDisplay<Game extends CombinatorialGame & SwingDisplayable> extends Referee<Game> implements Callable<Integer> {

    //instance variables
    
    //the frame that contains the display for the class
    private JFrame window;
    
    //whether this has received the command to stop playing
    private boolean haltPlay;
    
    //class for the close-button listener
    private class CloseListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            haltPlay = true;
            window.dispose();
        }
    }
    
    
    //constructors
    
    //private constructor
    private RefereeWithSwingDisplay(Player<Game> leftPlayer, Player<Game> rightPlayer) {
        super(leftPlayer, rightPlayer);
        this.haltPlay = false;
        this.window = new JFrame("Game!");
        //Container content = this.window.getContentPane();
        this.window.setLayout(new BorderLayout());
        this.window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.window.setPreferredSize(new Dimension(500, 500));
        JButton closeButton = new JButton("End Game");
        closeButton.addActionListener(new CloseListener());
            /*new ActionListener(){
                public void actionPerformed(ActionEvent event) {
                    haltPlay = true;
                    window.dispose();
                }
            });*/
        
        this.window.add(closeButton, BorderLayout.SOUTH);
        this.window.add(new JPanel(), BorderLayout.CENTER);
        this.window.pack();
        this.window.setVisible(true);
    }

    /**
     * Class constructor.
     * 
     * @param leftPlayer  Player that plays as Left.
     * @param rightPlayer  Player that plays as Right.
     * @param position  Game position that all games will start from.
     */
    public RefereeWithSwingDisplay(Player<Game> leftPlayer, Player<Game> rightPlayer, Game startingPosition) {
        this(leftPlayer, rightPlayer);
        final Game startingGame = startingPosition;
        this.startStateGenerator = new PositionFactory<Game>() {
            private Game position = startingGame;
            public Game getPosition() { return (Game) position.clone(); }
        };
    }

    /**
     * Class constructor.
     * 
     * @param leftPlayer  Player that plays as Left.
     * @param rightPlayer  Player that plays as Right.
     * @param stateGenerator  Generator of states.
     */
    public RefereeWithSwingDisplay(Player<Game> leftPlayer, Player<Game> rightPlayer, PositionFactory<Game> stateGenerator) {
        this(leftPlayer, rightPlayer);
        this.startStateGenerator = stateGenerator;
    }
    
    /**
     * Ends the game and closes the window.
     */
    public void endGame() {
        this.haltPlay = true;
        this.window.dispose();
    }
    
    /**
     * Gets a String representation.
     *
     * @return  A String representation of this.
     */
    public String toString() {
        String string = "A RefereeWithSwingDisplay between two players.";
        return string;
    }
    
    // repeatedly asks for moves until someone loses.
    // (This is the main loop for this code
    protected int requestMoves() {
        this.updateGameDisplay();
        while (this.movesExist() && !this.haltPlay) {
            try {
                Thread.sleep(this.getDelay());
            } catch (Exception e) {
                this.printLine("Couldn't sleep!");
            }
            try {
                Game option = this.getNextMove();
                this.move(option);
            } catch (Exception e) {
                this.printLine("A problem occurred (" + e.getMessage() + ") while " + this.getPlayerName(this.currentPlayer) + " was taking their turn.  The other player wins by default!  Adding a forfeit.");
                this.forfeit(this.currentPlayer);
                return 1 - this.currentPlayer;
            }
            //this.window.setContentPane(this.position.toSwingComponent());
            this.updateGameDisplay();
        }
        if (this.haltPlay) {
            System.out.println("I was told to stop the game!  It's over!");
            //this.window.dispose();
            return 2;
        }
        try {
            Thread.sleep(this.getDelay());
        } catch (Exception e) {
            this.printLine("Couldn't sleep!");
        }
        
        int winningPlayer = 1 - this.currentPlayer;
        this.printLine("There are no options for " + this.getPlayerName(this.currentPlayer) + "!  " + this.getPlayerName(winningPlayer) + " wins!\nCongratulations to " + this.players.get(winningPlayer) + "!");
        return winningPlayer;
    }
    
    // refreshes the game display
    private void updateGameDisplay() {
        this.window.add(this.position.toSwingComponent(), BorderLayout.CENTER);
        this.window.revalidate();
    }
    
    //main method for testing
    public static void main(String[] args) {
        //TODO: need to add a unit test that works for any type of game.  Unfortunately, no such thing exists...
    }

}  //end of RefereeWithSwingDisplay.java
