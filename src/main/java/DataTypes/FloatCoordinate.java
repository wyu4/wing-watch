package DataTypes;

import java.awt.*;

/** Stores two floating point numbers as (x,y) coordinates. */
public class FloatCoordinate {
    private float x, y;

    /**
     * Create a new FloatCoordinate object with values (0,0).
     */
    public FloatCoordinate() {
        this(0, 0);
    }

    /**
     * Create a new FloatCoordinate object with values (x,y).
     * @param x X-value
     * @param y Y-value
     */
    public FloatCoordinate(float x, float y) {
        setX(x);
        setY(y);
    }

    /**
     * Create a new FloatCoordinate object from a Point object.
     * @param p Point object
     */
    public FloatCoordinate(Point p) {
        this(p.x, p.y);
    }

    /**
     * Create a new FloatCoordinate object from a Dimension object.
     * @param d Dimension object
     */
    public FloatCoordinate(Dimension d) {
        this(d.width, d.height);
    }

    /**
     * Overwrite the X-value.
     * @param x New X-value
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Overwrite the Y-value.
     * @param y New Y-value
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * Overwrite the XY-values.
     * @param x New X-value
     * @param y New Y-value
     */
    public void set(float x, float y) {
        setX(x);
        setY(y);
    }

    /**
     * Copy the values of another FloatCoordinate object.
     * @param f FloatCoordinate object
     */
    public void set(FloatCoordinate f) {
        set(f.getX(), f.getY());
    }

    /**
     * Set the XY values using a Point object.
     * @param p Point object containing XY values.
     * @see Point
     */
    public void set(Point p) {
        this.x = p.x;
        this.y = p.y;
    }

    /**
     * Set the XY values using a Dimension object.
     * @param p Dimension object containing XY values.
     * @see Dimension
     */
    public void set(Dimension d) {
        this.x = d.width;
        this.y = d.height;
    }

    /**
     * Get the X-value
     * @return The current value as a floating point number
     */
    public float getX() {
        return x;
    }

    /**
     * Get the Y-value
     * @return The current value as a floating point number
     */
    public float getY() {
        return y;
    }

    /**
     * Get the X-value
     * @return The current value as an integer.
     */
    public int getXAsInt() {
        return (int) x;
    }

    /**
     * Get the Y-value
     * @return The current value as an integer.
     */
    public int getYAsInt() {
        return (int) y;
    }

    /**
     * Convert to a Point object.
     * @return Point object containing the same XY values (as integers).
     * @see Point
     */
    public Point toPoint() {
        return new Point(getXAsInt(), getYAsInt());
    }

    /**
     * Convert to a Dimension object.
     * @return Dimension object containing the same XY values (as integers, and properties being "width" and "height").
     * @see Dimension
     */
    public Dimension toDimension() {
        return new Dimension(getXAsInt(), getYAsInt());
    }

    /**
     * Multiply both XY values by a multiplier.
     * @param multiplier Multiplier
     * @return New {@code FloatCoordinate} object storing products.
     */
    public FloatCoordinate multiply(float multiplier) {
        return new FloatCoordinate(x * multiplier, y * multiplier);
    }

    /**
     * Create a deep copy of this object.
     * @return A new {@code FloatCoordinate} object with the same properties as this object.
     */
    public FloatCoordinate deepCopy() {
        return new FloatCoordinate(x, y);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FloatCoordinate c) {
            return ((c.x == this.x) && (c.y == this.y));
        }
        return false;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
