/**
 * Represents a Combinatorial Game.  
 *
 * @author Kyle Burke <paithanq@gmail.com> 
 * 
 */
//package something;
 
import java.lang.*;
import java.io.*;
import java.util.*;

public abstract class CombinatorialGame {

    //instance variables
    
    //the names of the players
    protected String[] playerNames = new String[] {"Left", "Right"};
    
    //constants
    
    /**
     * Index to represent the left player's ID.
     */
    public static int LEFT = 0;
    
    /**
     * Index to represent the right player's ID.
     */
    public static int RIGHT = 1;
    
    /**
     * Index for parts that are uncolored/unplayed.
     */
    public static int UNCOLORED = 2;
    
    
    /**
     * Returns the opposite of the player given.
     *
     * @param player	Either Left or Right.
     * @return		Opposite of the given player.
     */
    public static int otherPlayer(int player) {
        return 1-player;
    }
    
    //public methods
    
    /**
     * Move options for one of the players.
     *
     * @param player    The player to get the options for.
     * @return          The options the given player can choose to play from.
     *
     */
    public abstract Collection<CombinatorialGame> getOptions(int player);
    
    /**
     * Returns a deep clone of this.
     *
     * @return  A deep clone of this position.
     */
    public abstract CombinatorialGame clone();
    
    //@override
    public boolean equals(Object other) {
        try {
            CombinatorialGame otherGame = (CombinatorialGame) other;
            return this.equals(otherGame);
        } catch (ClassCastException cce) {
            return false;
        }
    }
    
    /**
     * Returns whether this equals another position.
     *
     * @param other     Another position.
     * @return          Whether this position equals the other.  This should compare identity, *NOT* equivalence.
     */
    public abstract boolean equals(CombinatorialGame other);
    
    /**
     * Returns a hash code of this.
     *
     * @return  A hash code for this position, based on the results of the toString method.
     */
    public int hashCode() {
        return this.toString().hashCode();
    }
    
    /**
     * Determines whether a position is an option of this.
     *
     * @param player    The player who can move to the option.
     * @param option	 A potential option.
     * @return 		 Whether option is a move option of this for player.
     */
    public boolean hasOption(int player, CombinatorialGame option) {
        return this.getOptions(player).contains(option);
    }
    
    /**
     * Determines whether the given player can still make moves.
     *
     * @param player	int representing the player identity.
     * @return		Whether the given player has an available move on the board.
     */
    public boolean playerHasAnOption(int player) {
        return !(this.getOptions(player).isEmpty());
    }
    
    /**
     * Returns a display of this game.
     * 
     * @return 		A Graphical version of this.
     *
    public abstract GameDisplay getDisplay();
     */
     
    /**
     * Returns the canonical name of a player.
     *
     * @param playerId  The index of the player (LEFT or RIGHT).
     * @return  The canonical name of the player.
     */
    public String getPlayerName(int playerId) {
        return this.playerNames[playerId];
    }
    
    
    /**
     * Returns a the name of the rules for this game.
     * 
     * @return  Name of this game as a String.
     */
    public static String getName() {
        return "CombinatorialGame";
    }
    
    
    /**
     * Returns a string version of this board.
     */
    public String toString() {
        return "A Combinatorial Game.";
    }
   
   
} //end of CombinatorialGame.java
