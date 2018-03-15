/**
 * A Referee that uses Swing to display graphics.  The position of the game does not automatically advance and is manually advanced by the user.
 *
 * @author Kyle Burke <paithanq@gmail.com>
 */
 
import java.lang.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.text.NumberFormat;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ManualAdvancingSwingReferee<Game extends CombinatorialGame & SwingDisplayable> extends Referee<Game> implements Callable<Integer> {

    //instance variables
    
    //window that the game is displayed in
    private JFrame window;
    
    //Semaphore that controls waiting for the button to be pressed.
    private final Semaphore buttonGate;
    
    //panel that contains the game display and the advance button
    private JPanel mainPanel;
    
    //button to advance the game by one move
    private JButton advanceButton;
    
    //panel containing the game display
    private JComponent gameDisplayPanel;
    
    //constructors

    /**
     * Class constructor.
     * 
     * @param players  Array of two players.
     * @param stateGenerator  Generator of states.
     */
    public ManualAdvancingSwingReferee(Player<Game> leftPlayer, Player<Game> rightPlayer, PositionFactory<Game> stateGenerator) {
        super(leftPlayer, rightPlayer, stateGenerator);
        //set up the window
        this.window = new JFrame(Game.getName());
        this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.window.setPreferredSize(new Dimension(800, 800));
        
        //the main panel will hold the GUI elements
        this.mainPanel = new JPanel(new BorderLayout());
        this.window.add(mainPanel);
        this.advanceButton = new JButton("Next move");
        mainPanel.add(this.advanceButton, BorderLayout.SOUTH); //adds the button to the bottom of the window
        
        //The button stuff that won't be available to the students.
        this.advanceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                buttonGate.release();
            }
        });
        //gameDisplayPanel will hold the graphical display of the game
        this.gameDisplayPanel = new JPanel(new BorderLayout());
        this.mainPanel.add(this.gameDisplayPanel, BorderLayout.CENTER);
        this.window.pack();
        this.window.setVisible(true);
        
        //create a semaphore with just one resource
        this.buttonGate = new Semaphore(1, true);
        try {
            //acquire the resource so that the first move won't be made until buttonGate.release() is called
            this.buttonGate.acquire();
        } catch (InterruptedException exception) {
            System.err.println(exception.getMessage());
            exception.printStackTrace();
        }
    }
    
    /**
     * Gets a String representation.
     *
     * @return  A String representation of this.
     */
    public String toString() {
        String string = "A ManualAdvancingSwingReferee between two players.";
        return string;
    }
    
    // repeatedly asks for moves until someone loses.
    // (This is the main loop for this code
    protected int requestMoves() {
        System.out.println("In subclass!");
        this.gameDisplayPanel.add(this.position.toSwingComponent());
        //the loop that continuously asks for the next game
        while (this.movesExist()) {
            //redraw/pack the display
            this.window.revalidate();
            this.window.pack();
            
            //get the next move.  Catch exceptions that happen along the way.
            try {
                this.buttonGate.acquire(); //blocks until the button is pressed, using the semaphore
                Game option = this.getNextMove();
                this.move(option);
                this.gameDisplayPanel.removeAll();
                this.gameDisplayPanel.add(this.position.toSwingComponent());
            } catch (InterruptedException interrupt) {
                System.err.println("Semaphore was interrupted: " + interrupt.getMessage());
            } catch (Exception e) {
                this.printLine("A problem occurred (" + e.getMessage() + ") while " + this.getPlayerName(this.currentPlayer) + " was taking their turn.  The other player wins by default!  Adding a forfeit.");
                this.forfeit(this.currentPlayer);
                return 1 - this.currentPlayer;
            }
        }
        //the game is over.  Turn off the button and report the results of the winner.
        this.window.revalidate();
        this.window.pack();
        this.advanceButton.setEnabled(false);
        int winningPlayer = 1 - this.currentPlayer;
        this.printLine("There are no options for " + this.getPlayerName(this.currentPlayer) + "!  " + this.getPlayerName(winningPlayer) + " wins!\nCongratulations to " + this.players.get(winningPlayer) + "!");
        return winningPlayer;
    }
    
    //main method for testing
    public static void main(String[] args) {
        //TODO: need to add a unit test that works for any type of game.  Unfortunately, no such thing exists...
    }

}  //end of ManualAdvancingSwingReferee.java
