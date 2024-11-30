package GUIClasses.Interfaces;

/** Interface containing some of the super methods of a container object. This is to prevent indefinite recursion from Accurate Containers. */
public interface SuperContainerMethods {
    /**
     * Calls the original setLocation() method from the superclass.
     * @param x X-value
     * @param y Y-value
     */
    public void callSuperSetLocation(int x, int y);

    /**
     * Calls the original setSize() method from the superclass.
     * @param width Width
     * @param height Height
     */
    public void callSuperSetSize(int width, int height);

    /**
     * Calls the original setBounds() method from the superclass.
     * @param x Width
     * @param y Height
     * @param width Width
     * @param height Height
     */
    public void callSuperSetBounds(int x, int y, int width, int height);
}
