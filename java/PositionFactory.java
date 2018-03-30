/**
 * This is the interface for objects that create positions for a ruleset.
 *
 * @author Kyle Burke <paithanq@gmail.com>
 */
public interface PositionFactory<Game extends CombinatorialGame> {
	
	//public methods
	
	/**
	 * Creates a new position.
	 *
	 * @return  Returns a position of Game.
	 */
	public Game getPosition();

} //end of PositionFactory<Game extends CombinatorialGame>
