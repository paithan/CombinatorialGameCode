/**
 * Handles a combinatorial game match between two players. 
 * 
 * @author Kyle Burke <paithanq@gmail.com>
 */
 
import java.lang.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.text.NumberFormat;

public class Referee<Game extends CombinatorialGame> implements Callable<Integer> {

    //instance variables
    
    //two players.  players[0] will be left; players[1] will be right.
    protected ArrayList<Player<Game>> players;
    
    //the number of games each player has forfeited
    private ArrayList<Integer> forfeitsByPlayer;
    
    //current game state
    protected Game position;
    
    //starting game state
    protected PositionFactory<Game> startStateGenerator;
    
    //current player
    protected int currentPlayer;
    
    //time to delay between player turns
    private int delay;
    
    //number of attempts each player gets to choose a proper move
    private int numMoveAttempts;
    
    //output controller for this.
    private Display display;
    
    //display controller.  Implements State Pattern.
    private class Display {
    
        //whether this prints to the screen
        private boolean prints;
    
        //constructor
        public Display(boolean prints) {
            this.prints = prints;
        }
        
        //print out
        public void println(String string) {
            if (this.prints) {
                System.out.println(string);
            }
        }
    }
    
    //constructors
    
    //private constructor
    protected Referee(Player<Game> leftPlayer, Player<Game> rightPlayer) {
        this.players = new ArrayList<Player<Game>>();
        players.add(leftPlayer);
        players.add(rightPlayer);
        this.forfeitsByPlayer = new ArrayList<Integer>();
        this.forfeitsByPlayer.add(0);
        this.forfeitsByPlayer.add(0);
        this.setDelay(3000);
        this.setPrint(true);
        this.setAttempts(1);
    }

    /**
     * Class constructor.
     * 
     * @param players  Array of two players.
     * @param stateGenerator  Generator of states.
     */
    public Referee(Player<Game> leftPlayer, Player<Game> rightPlayer, PositionFactory<Game> stateGenerator) {
        this(leftPlayer, rightPlayer);
        this.startStateGenerator = stateGenerator;
    }

    /**
     * Class constructor.
     * 
     * @param players  Array of two players.
     * @param initialPosition  The position the Referee will always start from.
     */
    public Referee(Player<Game> leftPlayer, Player<Game> rightPlayer, Game initialPosition) {
        this(leftPlayer, rightPlayer);
        final Game startingPosition = initialPosition;
        this.startStateGenerator = new PositionFactory<Game>() {
            private Game position = startingPosition;
            public Game getPosition() { return (Game) position.clone(); }
        };
    }
    
    /**
     * Gets a String representation.
     *
     * @return  A String representation of this.
     */
    public String toString() {
        String string = "A referee between two players.";
        return string;
    }
    
    /**
     * Sets the delay between turns.
     *
     * @param delay  The millisecond delay between turns.
     */
    public void setDelay(int delay) {
        this.delay = delay;
    }
    
    /**
     * Returns the delay.
     *
     * @return  The milliseconds this waits between asking for moves.
     */
    public int getDelay() {
        return this.delay;
    }
    
    /**
     * Sets the number of attempts this will tolerate per turn.
     *
     * @param attempts  The number of times a player can return an incorrect move before we choose a random one for them!
     */
    public void setAttempts(int attempts) {
        this.numMoveAttempts = attempts;
    }
    
    /**
     * Sets whether this prints output.
     *
     * @param doesPrint  Whether this will print to System.out.
     */
    public void setPrint(boolean doesPrint) {
        this.display = new Display(doesPrint);
    }
    
    /**
     * Runs the competition.
     *
     * @return  Index of the winning player.
     */
    public Integer call() {
        int startingPlayer = (int) (Math.floor(Math.random()*2));
        return (Integer) this.call(startingPlayer);
    }
    
    /**
     * Gets a player's name.
     *
     * @param playerId  The index of the sought player.
     * @return  The name of the chosen player, as defined by the player's toString() method.
     */
    public String getPlayerName(int playerId) {
        return this.players.get(playerId).toString();
    }
    
    /**
     * Gets a player's role.
     *
     * @param playerId  The index of the sought player.
     * @return  The name of the role of the chosen player.  E.g. Blue vs. Red, Left vs Right, etc.
     */
    public String getPlayerRole(int playerId) {
        return this.position.getPlayerName(playerId);
    }
    
    
    /**
     * Gets the current player's name.
     *
     * @return The result of calling the toString() method on the current player.
     */
    public String getCurrentPlayerName() {
        return this.getPlayerName(this.currentPlayer);
    }
    
    /**
     * Gets the current player's role.
     *
     * @return A String with the current player's role.  E.g. "Left" or "Right", "Blue" or "Red", etc.
     */
    public String getCurrentPlayerRole() {
        return this.getPlayerRole(this.currentPlayer);
    }
    
    //starts the game, with a specified initial player
    private int call(int startingPlayer) {
        this.currentPlayer = startingPlayer;
        this.position = this.startStateGenerator.getPosition();
        this.display.println("Let's get ready to rumble!");
        for (int i = 0; i < 2; i++) {
            this.display.println("In this corner ... playing as " + this.getPlayerRole(i) + " ... " + this.getPlayerName(i) + "!");
        } 
        this.display.println("Starting board:\n" + this.position);
        this.display.println(this.getCurrentPlayerName() + " will start us off.  Begin!");
        return this.requestMoves();
    }
    
    //asks for a move from one person
    protected Game getNextMove() {
        Game option;
        int attemptsRemaining = this.numMoveAttempts;
        while (attemptsRemaining > 0) {
            try {
                option = this.players.get(this.currentPlayer).getMove((Game) this.position.clone(), this.currentPlayer);
                if (this.position.hasOption(this.currentPlayer, option)) {
                    return option;
                } else {
                    this.display.println("Player " + this.getCurrentPlayerName() + " (" + this.getCurrentPlayerRole() + ") tried to move from \n" + this.position + "\n  to  \n" + option + ", which is not a legal option.  They forfeit the game!");
                    throw new RuntimeException(this.getCurrentPlayerRole() + " tried to move from \n" + this.position + "\n  to  \n" + option + ", which is not a legal option.  They forfeit the game!");
                }
            } catch (NoSuchElementException nsee) {
                this.display.println("We experienced a problem!  A player is telling us that there is no option for " + this.getCurrentPlayerRole() + " from position " + this.position + "\nThat can't be right!  Something fishy is going on here!");
            }
            attemptsRemaining --;
            this.display.println(this.getCurrentPlayerName() + " has " + attemptsRemaining + " tries left.");
            try {
                Thread.sleep(this.delay);
            } catch (Exception e) {
                this.display.println("Couldn't sleep!");
            }
        }
        this.display.println("Choosing a random move for " + this.players.get(this.currentPlayer) + " instead!");

        //get a random option
        Random randomGenerator = new Random();
        Collection<Game> optionCollection = (Collection<Game>) this.position.getOptions(this.currentPlayer);
        Object[] possibleOptions = optionCollection.toArray();
        Object randomOption =  possibleOptions[randomGenerator.nextInt(possibleOptions.length)];
        return (Game) randomOption;
    }
    
    //determines whether the next player has any moves
    protected boolean movesExist() {
        return this.position.playerHasAnOption(this.currentPlayer);
    }
    
    //uses the display to print a statement
    protected void printLine(String s) {
        this.display.println(s);
    }
    
    //moves to a new game
    //does not test that option is legal!  Should already have been tested!
    protected void move(Game option) {
        this.position = option;
        this.display.println(this.getCurrentPlayerName() + " (" + this.getCurrentPlayerRole() + ") moved to \n" + this.position);
        this.currentPlayer += 1;
        if (this.currentPlayer == this.players.size()) {
            this.currentPlayer = 0;
        }
    }
    
    //performs a forfeit for a player
    protected void currentPlayerForfeit() {
        int previousForfeits = this.forfeitsByPlayer.get(this.currentPlayer);
        if (previousForfeits == 0) {
            //print out some information about this forfeit
            System.out.println(this.getCurrentPlayerName() + " forfeited while trying to make a move on: " + this.position);
            
        }
        this.forfeitsByPlayer.set(this.currentPlayer, previousForfeits + 1);
    }
    
    // repeatedly asks for moves until someone loses.
    // (This is the main loop for this code
    protected int requestMoves() {
        while (this.position.playerHasAnOption(this.currentPlayer)) {
            try {
                Thread.sleep(this.delay);
            } catch (Exception e) {
                this.display.println("Couldn't sleep!");
            }
            try {
                Game option = this.getNextMove();
                this.move(option);
            } catch (Exception e) {
                int errorLine = -1;
                String className = "";
                StackTraceElement[] stackFrames = e.getStackTrace();
                for (StackTraceElement frame : stackFrames) {
                    errorLine = frame.getLineNumber();
                    className = frame.getClassName();
                    if (errorLine != -1) break;
                }
                
                
                this.display.println("A problem occurred (" + e.toString() + ") in " + className + " on line " + errorLine + " while " + this.getCurrentPlayerName() + " was taking their turn.  The other player wins by default!");
                e.printStackTrace();
                return 1 - this.currentPlayer;
            }
        }
        try {
            Thread.sleep(this.delay);
        } catch (Exception e) {
            this.display.println("Couldn't sleep!");
        }
        int winningPlayer = 1 - this.currentPlayer;
        this.display.println("There are no options for " + this.getCurrentPlayerName() + "!  " + this.getPlayerName(winningPlayer) + " wins!\nCongratulations to " + this.getPlayerName(winningPlayer) + "!");
        return winningPlayer;
    }
    
    //adds a forfeiture to one of the players
    protected void forfeit(int playerId) {
        this.forfeitsByPlayer.set(this.currentPlayer, this.forfeitsByPlayer.get(this.currentPlayer) + 1);
    
    }
    
    /**
     * Pits two players against each other multiple times, while subduing print statements mid-tournament.
     *
     * @param numGames  The number of games in the competition.
     * @return  An Array of doubles.  The zeroeth element is the percentage Left won, oneth the percentage Right won, twoeth the number of total forfeits.
     */
    public double[] gauntlet(int numGames) {
        return this.gauntlet(numGames, false);
    }
    
    /**
     * Pits two players against each other multiple times.
     *
     * @param numGames  The number of games in the competition.
     * @param printDetails  Whether or not to print details about each game.
     * @return  An Array of doubles.  The zeroeth element is the percentage Left won, oneth the percentage Right won, twoeth the number of total forfeits.
     */
    public double[] gauntlet(int numGames, boolean printDetails) {
        this.forfeitsByPlayer.set(0, 0);
        this.forfeitsByPlayer.set(1, 0);
        int[] gamesWon = new int[] {0, 0};
        int winner;
        this.setPrint(true);
        this.display.println("Beginning the competition!  There will be " + numGames + " games played!");
        this.setPrint(printDetails);
        this.setDelay(0);
        for (int gameIndex = 0; gameIndex < numGames; gameIndex ++) {
            winner = this.call(gameIndex % 2);
            gamesWon[winner] ++;
        }
        this.setPrint(true);
        this.display.println("Competition complete!  Games won:");
        double[] percentagesWon = new double[] {((double) gamesWon[0]) / numGames, ((double) gamesWon[1]) / numGames, (double) this.forfeitsByPlayer.get(0) + this.forfeitsByPlayer.get(1)};
        NumberFormat percentFormat = NumberFormat.getPercentInstance();
        for (int i = 0; i < 2; i ++) {
            this.display.println("    " + this.getPlayerName(i) + " (" + this.getPlayerRole(i) + ") : " + gamesWon[i] + " (" + percentFormat.format(((double) gamesWon[i]) / numGames) + ")  forfeits: " + this.forfeitsByPlayer.get(i));
        } 
        return percentagesWon;
    }
    
    //main method for testing
    public static void main(String[] args) {
        //TODO: need to add a unit test that works for any type of game.  Unfortunately, no such thing exists...
    }

}  //end of Referee.java
