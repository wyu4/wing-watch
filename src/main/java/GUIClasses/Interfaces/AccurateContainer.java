package GUIClasses.Interfaces;

import DataTypes.FloatCoordinate;

import java.awt.*;

/** Contains all the properties that an {@code AccurateComponent} possesses. */
public interface AccurateContainer {
    /**
     * Resize the component.
     * @param d Object containing the new size.
     */
    public void setSize(Dimension d);

    /**
     * Resize the component.
     * @param width New width of the component
     * @param height New height of the component
     */
    public void setSize(int width, int height);

    /**
     * Resize the component.
     * @param width New width of the component
     * @param height New height of the component
     */
    public void setSize(float width, float height);

    /**
     * Resize the component.
     * @param f Object containing the new size.
     */
    public void setSize(FloatCoordinate f);

    /**
     * Move the component.
     * @param p Object containing the new location.
     */
    public void setLocation(Point p);

    /**
     * Move the component
     * @param x New X-value
     * @param y New Y-value
     */
    public void setLocation(int x, int y);

    /**
     * Move the component
     * @param x New X-value
     * @param y New Y-value
     */
    public void setLocation(float x, float y);

    /**
     * Move the component.
     * @param f Object containing the new location.
     */
    public void setLocation(FloatCoordinate f);

    /**
     * Set the anchor point of this component.
     * @param x X-value
     * @param y Y-value
     */
    public void setAnchorPoint(float x, float y);

    /**
     * Set the anchor point of this component.
     * @param f {@code FloatCoordinate} containing a new anchor point.
     */
    public void setAnchorPoint(FloatCoordinate f);

    /**
     * Get the component's accurate location on the screen.
     * @return New {@code FloatCoordinate} object containing the coordinates as floating point numbers.
     * @see FloatCoordinate
     */
    public FloatCoordinate getAccurateLocation();

    /**
     * Store the component's location in {@code rv}.
     * @param rv Return value, {@code FloatCoordinate} object modified to the component's accurate location.
     * @return rv
     * @see FloatCoordinate
     */
    public FloatCoordinate getAccurateLocation(FloatCoordinate rv);

    /**
     * Get the component's accurate size on the screen.
     * @return New {@code FloatCoordinate} object containing the coordinates as floating point numbers.
     * @see FloatCoordinate
     */
    public FloatCoordinate getAccurateSize();

    /**
     * Store the component's size in {@code rv}.
     * @param rv Return value, {@code FloatCoordinate} object modified to the component's accurate size.
     * @return rv
     * @see FloatCoordinate
     */
    public FloatCoordinate getAccurateSize(FloatCoordinate rv);

    /**
     * Get the component's anchor point.
     * @return New {@code FloatCoordinate} object containing the coordinates as floating point numbers.
     * @see FloatCoordinate
     */
    public FloatCoordinate getAnchorPoint();

    /**
     * Store the component's anchor point in {@code rv}.
     * @param rv Return value, {@code FloatCoordinate} object modified to the component's anchor point.
     * @return rv
     * @see FloatCoordinate
     */
    public FloatCoordinate getAnchorPoint(FloatCoordinate rv);
}
