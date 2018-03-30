import javax.swing.*;

/**
 * The Interface for objects that produce a Swing component.
 *
 * @author Kyle Burke <paithanq@gmail.com>
 */
public interface SwingDisplayable {

    /* constants */
    
    /* public methods */
    
    /**
     * Returns a Swing display of this.
     *
     * @return  A JComponent that displays the state of this Object.
     */
    public JComponent toSwingComponent();

} //end of SwingDisplayable.java
