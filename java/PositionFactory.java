/**
 * This is the interface for objects that create positions for a ruleset.
 * @author Kyle Burke <paithanq@gmail.com>
 */

//import java.xxxxx.*;
public interface PositionFactory<Game extends CombinatorialGame> {
	
	//public methods
	public Game getPosition();

} //end of PositionFactory<Game extends CombinatorialGame>
