/**
 * Represents a NoCanDo position.  NoCanDo is a combinatorial game where players play dominoes on empty pairs of spaces on a checkerboard.  The Left player must play dominoes vertically, and the Right player must play them Horizontally.  After each move, all dominos must be adjacent (not diagonally) to at least one uncovered square.
 *
 * This file requires Pair.java, but that is an assignment for my data structures course, so I have included Pair.class instead.
 *
 * @author Kyle Burke <paithanq@gmail.com>
 */

import javax.swing.*;
import java.util.*;
import java.awt.*;


public class NoCanDo extends CombinatorialGame implements SwingDisplayable {

    /* constants */

    /* fields */
    //height of the grid
    private int height;
    
    //width of the grid
    private int width;
    
    //coordinates of the top halves of the vertical pieces
    private Set<Pair<Integer, Integer>> verticalTops;
    
    //coordinates of the left halves of the horizontal pieces
    private Set<Pair<Integer, Integer>> horizontalLefts;
    
    //comparator for Pairs so we can use the TreeSet.
    private final static Comparator<Pair<Integer, Integer>> PAIR_COMP = new Comparator<Pair<Integer, Integer>>() {
        @Override
        public int compare(Pair<Integer, Integer> a, Pair<Integer, Integer> b) {
            if (a.getFirst().equals(b.getFirst())) {
                return a.getSecond() - b.getSecond();
            } else {
                return a.getFirst() - b.getFirst();
            }
        }
    };
    
    /* constructors */
    
    /**
     * Creates an empty NoCanDo position.
     *
     * @param height  The number of rows on the board.
     * @param width  The number of columns on the board.
     * @throws IllegalArgumentException  Never.
     */
    public NoCanDo(int height, int width) throws IllegalArgumentException {
        this(height, width, new TreeSet<Pair<Integer, Integer>>(PAIR_COMP), new TreeSet<Pair<Integer, Integer>>(PAIR_COMP));
    }
    
    /**
     * Builds a generic NoCanDo board of any size with any dominoes.
     *
     * @param height  The number of rows on the board.
     * @param width  The number of columns on the board.
     * @param tops  The top coordinates of the vertical dominoes.
     * @param lefts  The left coordinates of the horizontal dominoes.
     * @throws IllegalArgumentException  When the sets of dominoes do not form a legal game state.
     */
    public NoCanDo(int height, int width, Set<Pair<Integer, Integer>> tops, Set<Pair<Integer,Integer>> lefts) throws IllegalArgumentException {
        this.height = height;
        this.width = width;
        this.verticalTops = copySet(tops);
        this.horizontalLefts = copySet(lefts);
        if (!this.isLegal()) {
            throw new IllegalArgumentException("This is not a legal position!  There are either overlapping dominoes, dominoes over the sides of the board, or dominoes without liberties!");
        }
    }
    
    /**
     * Creates an 8x8 NoCanDo board with any specified dominoes.
     *
     * @param tops  The top coordinates of the vertical dominoes.
     * @param lefts  The left coordinates of the horizontal dominoes.
     * @throws IllegalArgumentException  When the sets of dominoes do not form a legal game state.
     */
    public NoCanDo(Set<Pair<Integer, Integer>> tops, Set<Pair<Integer,Integer>> lefts) throws IllegalArgumentException {
        this(8, 8, tops, lefts);
        //TODO: this should really check the things in tops and lefts to make sure they all fit, then fit the square around that.
    }
    
    /**
     * Creates an empty 8x8 NoCanDoBoard.
     *
     * @throws IllegalArgumentException  Never.
     */
    public NoCanDo() throws IllegalArgumentException {
        this(8,8);
    }
    
    /* public methods */
    
    /**
     * Returns whether this equals another game.
     *
     * @param other  Another NoCanDo position.  Returns false automatically on any game that is not NoCanDo.
     * @return  Whether other equals this.
     */
    public boolean equals(CombinatorialGame other) {
        try {
            return this.equals((NoCanDo) other);
        } catch (ClassCastException e) {
            return false;
        }
    }
    
    /**
     * Returns whether this equals another NoCanDo.
     *
     * @param other  Another NoCanDo position.
     * @return  Whether other equals this, meaning they both have the same sets of vertical and horizontal domino positions.
     */
    public boolean equals(NoCanDo other) {
        return this.getHeight() == other.getHeight() &&
               this.getWidth() == other.getWidth() &&
               this.verticalTops.equals(other.getVerticalTops()) &&
               this.horizontalLefts.equals(other.getHorizontalLefts());
    }
    
    /**
     * Returns the top coordinates of vertical dominoes.
     *
     * @return The coordinates of the tops of the vertical dominoes on this board.
     */
    public Set<Pair<Integer, Integer>> getVerticalTops() {
        return copySet(this.verticalTops);
    }
    
    /**
     * Returns the left coordinates of horizontal dominoes.
     *
     * @return The coordinates of the lefts of the horizontal dominoes on this board.
     */
    public Set<Pair<Integer, Integer>> getHorizontalLefts() {
        return copySet(this.horizontalLefts);
    }
    
    /**
     * Returns the height.
     *
     * @return  The height of this.
     */
    public int getHeight() {
        return this.height;
    }
    
    /**
     * Returns the width.
     *
     * @return  The width of the grid for this graph.
     */
    public int getWidth() {
        return this.width;
    }
    
    //returns the total number of dominoes
    private int getNumDominoes() {
        return this.verticalTops.size() + this.horizontalLefts.size();
    }
    
    //returns whether a spot is open
    private boolean isFree(int column, int row) {
        if (column >= this.getWidth() || 
            column < 0 ||
            row >= this.getHeight() ||
            row < 0) {
            return false; //the coordinates are beyond the bounds of the board
        }
        for (Pair<Integer, Integer> vertTop : this.verticalTops) {
            if ((vertTop.getFirst().equals(column) && vertTop.getSecond().equals(row)) ||
                (vertTop.getFirst().equals(column) && vertTop.getSecond().equals(row-1))) {
                return false;
            }
        }
        for (Pair<Integer, Integer> horizLeft : this.horizontalLefts) {
            if ((horizLeft.getFirst().equals(column) && horizLeft.getSecond().equals(row)) ||
                (horizLeft.getFirst().equals(column-1) && horizLeft.getSecond().equals(row))) {
                return false;
            }
        }
        return true;
    }
    
    //returns whether two horizontal dominoes overlap
    private static boolean twoHorizontalsOverlap(Pair<Integer, Integer> horizLeftA, Pair<Integer, Integer> horizLeftB) {
        int leftColA = horizLeftA.getFirst();
        int rowA = horizLeftA.getSecond();
        int leftColB = horizLeftB.getFirst();
        int rowB = horizLeftB.getSecond();
        if (rowA == rowB) {
            //uhoh, we're in the same row!
            return Math.abs(leftColA - leftColB) == 1; 
        } else {
            //not in the same row; everything's fine
            return false;
        }
    }
    
    //returns whether two vertical dominoes overlap
    private static boolean twoVerticalsOverlap(Pair<Integer, Integer> vertTopA, Pair<Integer, Integer> vertTopB) {
        int columnA = vertTopA.getFirst();
        int topRowA = vertTopA.getSecond();
        int columnB = vertTopB.getFirst();
        int topRowB = vertTopB.getSecond();
        if (columnA == columnB) {
            //uhoh, we're in the same column!
            return Math.abs(topRowA - topRowB) == 1; // if it's zero, we're the same thing; no problem!
        } else {
            //not in the same column; everything's cool
            return false;
        }
    }
    
    //returns whether a vertical overlaps a horizontal
    private static boolean verticalOverlapsHorizontal(Pair<Integer, Integer> vertTop, Pair<Integer, Integer> horizLeft) {
        int vertColumn = vertTop.getFirst();
        int vertTopRow = vertTop.getSecond();
        int vertBottomRow = vertTopRow + 1;
        int horizLeftCol = horizLeft.getFirst();
        int horizRightCol = horizLeftCol + 1;
        int horizRow = horizLeft.getSecond();
        if (vertColumn == horizLeftCol || vertColumn == horizRightCol) {
            //they share a column
            if (horizRow == vertTopRow || horizRow == vertBottomRow) {
                //they share both a row and a column!  They overlap!
                return true;
            }
            
        }
        return false;
    }
    
    //returns whether a vertical domino has a liberty
    private boolean verticalDominoHasLiberty(int topColumn, int topRow) {
        return isFree(topColumn, topRow-1) || //above free?
               isFree(topColumn + 1, topRow) || //right top free?
               isFree(topColumn + 1, topRow + 1) || //right bottom free?
               isFree(topColumn, topRow + 2) ||  //below free?
               isFree(topColumn - 1, topRow + 1) || //left bottom free?
               isFree(topColumn - 1, topRow); //left top free?
    }
    
    //returns whether a horizontal domino has a liberty
    private boolean horizontalDominoHasLiberty(int leftColumn, int row) {
        return isFree(leftColumn, row - 1) || //above left free?
               isFree(leftColumn + 1, row - 1) || //above right free?
               isFree(leftColumn + 2, row) || //right free?
               isFree(leftColumn + 1, row + 1) || //below right free?
               isFree(leftColumn, row + 1) || //below left free?
               isFree(leftColumn - 1, row); //left free?
    }
    
    //returns whether all the dominoes are in a legal position
    private boolean isLegal() {
        return this.dominoesInBounds() && this.allHaveLiberties() && this.noOverlaps();
    }
    
    //returns whether none of the dominoes overlap
    private boolean noOverlaps() {
        //check all the horizontal dominoes
        for (Pair<Integer, Integer> left : this.horizontalLefts) {
            for (Pair<Integer, Integer> otherLeft : this.horizontalLefts) {
                if (twoHorizontalsOverlap(left, otherLeft)) return false;
            }
            //check them against the verticals
            for (Pair<Integer, Integer> top : this.verticalTops) {
                if (verticalOverlapsHorizontal(top, left)) return false;
            }
        }
        
        //check all the verticals
        for (Pair<Integer, Integer> top : this.verticalTops) {
            for (Pair<Integer, Integer> otherTop : this.verticalTops) {
                if (twoVerticalsOverlap(top, otherTop)) return false;
            }
        }
        //all good
        return true;
    }
    
    //returns whether all of the dominoes are within the bounds of the board
    private boolean dominoesInBounds() {
        //check all the horizontal dominoes
        for (Pair<Integer, Integer> left : this.horizontalLefts) {
            int leftCol = left.getFirst();
            int rightCol = leftCol + 1;
            int row = left.getSecond();
            if (row < 0 ||
                row >= this.getHeight() ||
                leftCol < 0 ||
                rightCol >= this.getWidth()) {
                return false; //outside the bounds!
            }
        }
        
        //check the vertical ones
        for (Pair<Integer, Integer> top: this.verticalTops) {
            int column = top.getFirst();
            int topRow = top.getSecond();
            int bottomRow = topRow + 1;
            if (topRow < 0 ||
                bottomRow >= this.getHeight() ||
                column < 0 ||
                column >= this.getWidth()) {
                return false;
            }
        }
        
        //everything okay!
        return true;
    }
    
    //returns whether each domino has at least one liberty.
    private boolean allHaveLiberties() {
        //check all the horizontal dominoes
        for (Pair<Integer, Integer> left : this.horizontalLefts) {
            if (!horizontalDominoHasLiberty(left.getFirst(), left.getSecond())) {
                return false; //no liberty here!
            }
        }
        
        //now check the vertical ones
        for (Pair<Integer, Integer> top: this.verticalTops) {
            if (!verticalDominoHasLiberty(top.getFirst(), top.getSecond())) {
                return false; //no liberty here!
            }
        }
        
        //everything has liberties!
        return true;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Vertical Tops: \n");
        for (Pair<Integer, Integer> top : this.getVerticalTops()) {
            builder.append("(" + top.getFirst() + ", " + top.getSecond() + "),  ");
        }
        builder.append("\nHorizontal Lefts: \n");
        for (Pair<Integer, Integer> left : this.getHorizontalLefts()) {
            builder.append("(" + left.getFirst() + ", " + left.getSecond() + "),  ");
        }
        return builder.toString();
    }
    
    
    @Override
    public Collection<CombinatorialGame> getOptions(int playerId) {
        Collection<CombinatorialGame> options = new ArrayList<CombinatorialGame>();
        for (int column = 0; column < this.width; column ++) {
            for (int row = 0; row < this.height; row++) {
                Set<Pair<Integer, Integer>> vertTops = this.getVerticalTops();
                Set<Pair<Integer, Integer>> horizLefts = this.getHorizontalLefts();
                if (playerId == CombinatorialGame.LEFT) {
                    //add the new vertical domino
                    vertTops.add(new Pair<Integer, Integer>(column, row));
                } else {
                    //(new horizontal domino!)
                    horizLefts.add(new Pair<Integer, Integer>(column, row));
                }
                
                //now add the position, if it's legal
                try {
                    NoCanDo option = new NoCanDo(this.height, this.width, vertTops, horizLefts);
                    if (!option.equals(this)) options.add(option);
                } catch (IllegalArgumentException e) {
                    //just don't add this one!
                }
            }   
        }
        return options;
    }
    
    //copies a set
    private Set<Pair<Integer, Integer>> copySet(Set<Pair<Integer, Integer>> set) {
        Set<Pair<Integer, Integer>> copySet = new TreeSet<Pair<Integer, Integer>>(PAIR_COMP);
        for (Pair<Integer, Integer> pair : set) {
            copySet.add(new Pair<Integer, Integer>(pair.getFirst(), pair.getSecond()));
        }
        return copySet;
    }
    
    /**
     * Returns a copy of this.
     *
     * @return  A deepish copy of this.
     */
    public CombinatorialGame clone() {
        return new NoCanDo(this.getHeight(), this.getWidth(), this.getVerticalTops(), this.getHorizontalLefts());
    }
    
    /**
     * Gets a Swing representation of this.
     *
     * @return  A JComponent containing a graphical display of the current position.
     */
    public JComponent toSwingComponent() {
        return new NoCanDoPanel(this);
    }
    
    /**
     *  A Graphical display for a NoCanDo position.
     */
    public static class NoCanDoPanel extends JComponent {
        
        //the NoCanDo position
        private NoCanDo position;

        //Map of integers to appropriate colors
        //initialize this outside of the constructor because it's static.
        private final Map<Integer, Color> intsToColors = new TreeMap<Integer, Color>();

        /* constructors */

        /**
         * Creates a new instance of GridColPanel.
         *
         * @param position  The NoCanDo position to display.
         */
        public NoCanDoPanel(NoCanDo position) {
            this.position = position;
            //set up the ints->Colors map
            this.intsToColors.put(CombinatorialGame.LEFT, Color.BLUE);
            this.intsToColors.put(CombinatorialGame.RIGHT, Color.RED);
            this.intsToColors.put(CombinatorialGame.UNCOLORED, Color.WHITE);
        }

        @Override
        public void paintComponent(Graphics g) {
            int gridHeight = this.position.getHeight();
            int gridWidth = this.position.getWidth();
            int panelHeightInPixels = this.getHeight();
            int panelWidthInPixels = this.getWidth();
            int pixelsPerBoxSide = (int) Math.min(Math.floor(panelHeightInPixels/gridHeight), Math.floor(panelWidthInPixels/gridWidth));
            int boardWidthInPixels = pixelsPerBoxSide * gridWidth;
            int boardHeightInPixels = pixelsPerBoxSide * gridHeight;
            g.clearRect(0, 0, boardWidthInPixels, boardHeightInPixels);
            
            //draw the background
            for (int column = 0; column < gridWidth; column ++) {
                for (int row = 0; row < gridHeight; row++) {
                    //draw the space
                    g.setColor(Color.BLACK);
                    g.fillRect(column * pixelsPerBoxSide, row * pixelsPerBoxSide, pixelsPerBoxSide, pixelsPerBoxSide);
                    //draw the box
                    g.setColor(Color.LIGHT_GRAY);
                    if ((column + row) % 2 == 1) g.setColor(Color.GRAY); //TODO: change this color to tan
                    g.fillRect(column * pixelsPerBoxSide + 2, row * pixelsPerBoxSide + 2, pixelsPerBoxSide - 4, pixelsPerBoxSide - 4);
                }
            } 
            
            //draw the verticals
            for (Pair<Integer, Integer> verticalTop : this.position.getVerticalTops()) {
                int column = verticalTop.getFirst();
                int rowTop = verticalTop.getSecond();
                g.setColor(Color.BLACK);
                g.fillRect(column * pixelsPerBoxSide + 5, rowTop * pixelsPerBoxSide + 5, pixelsPerBoxSide - 10, 2 * pixelsPerBoxSide - 10);
                g.setColor(Color.WHITE);
                g.fillRect(column * pixelsPerBoxSide + 7, rowTop * pixelsPerBoxSide + 7, pixelsPerBoxSide - 14, 2 * pixelsPerBoxSide - 14);
            }
            
            //draw the horizontals
            for (Pair<Integer, Integer> horizontalLeft : this.position.getHorizontalLefts()) {
                int leftColumn = horizontalLeft.getFirst();
                int row = horizontalLeft.getSecond();
                g.setColor(Color.BLACK);
                g.fillRect(leftColumn * pixelsPerBoxSide + 5, row * pixelsPerBoxSide + 5, 2 * pixelsPerBoxSide - 10, pixelsPerBoxSide - 10);
                g.setColor(Color.WHITE);
                g.fillRect(leftColumn * pixelsPerBoxSide + 7, row * pixelsPerBoxSide + 7, 2 * pixelsPerBoxSide - 14, pixelsPerBoxSide - 14);
            }
            
        }
        
    }
    
    /**
     * Represents a factory that creates instances of NoCanDo.  Always creates an initial 8 x 8 board
     */
    public static class PositionBuilder implements PositionFactory<NoCanDo> {
        
        /**
         * Class constructor.
         */
        public PositionBuilder() {
            //do nothing
        }
        
        /**
         * Returns an empty position.
         *
         * @return  An empty 8x8 NoCanDo position.  (No dominoes.)
         */
        public NoCanDo getPosition() {
            return new NoCanDo(8, 8);
        }
    }
    
    /* hidden methods (private/protected) (JavaDoc not necessary) */
    
    /* main method for testing */
    
    /** 
     * Unit test for NoCanDo
     * @param args  Arguments used to test this class. (Unused)
     */
    public static void main(String[] args) {
    
        PositionFactory<NoCanDo> builder = new NoCanDo.PositionBuilder();
        NoCanDo position = builder.getPosition();
        Player<NoCanDo> random = new RandomPlayer<NoCanDo>();
        Referee<NoCanDo> ref = new ManualAdvancingSwingReferee<NoCanDo>(random, random, builder);
        ref.call();
    }

} //end of NoCanDo
