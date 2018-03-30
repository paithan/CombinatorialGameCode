import java.lang.*;
import java.io.*;
import java.util.*;

/**
 * A player of combinatorial games.  Extend this to implement a player of a specific game.
 *
 * @author Kyle Burke <paithanq@gmail.com> 
 * 
 */
public abstract class Player<G extends CombinatorialGame> {

    //instance variables
    
    //constants
    
    //public methods
    
    /**
     * Chooses an option to move to.
     *
     * @param position  The position to choose an option of.
     * @param playerId  The index of the current player.  (Either CombinatorialGame.LEFT or CombinatorialGame.RIGHT.)
     * @return  An option of position.
     */
    public abstract G getMove(G position, int playerId);
    
    /**
     * Returns a string version of this.
     *
     * @return  A string representation of this player.
     */
    public String toString() {
        return "A " + G.getName() + " player.";
    }
   
} //end of Player.java
