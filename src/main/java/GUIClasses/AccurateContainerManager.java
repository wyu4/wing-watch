package GUIClasses;

import DataTypes.FloatCoordinate;
import GUIClasses.Interfaces.AccurateContainer;
import GUIClasses.Interfaces.SuperContainerMethods;

import java.awt.*;

/** Manages the floating point properties of containers, as well as some other additional properties. This was made so that if an algorithm had to be modified, changes would only have to be made once and would apply to all "Accurate" containers. */
public class AccurateContainerManager implements AccurateContainer {
    private final FloatCoordinate accurateLocation, accurateSize, anchorPoint;
    private final Container source;
    private final SuperContainerMethods superMethods;

    /**
     * Create a new{@code AccurateContainerManager} object.
     * @param source The source container
     */
    public AccurateContainerManager(Container source, SuperContainerMethods superMethods) {
        this.source = source;
        this.superMethods = superMethods;

        accurateLocation = new FloatCoordinate(source.getLocation());
        accurateSize = new FloatCoordinate(source.getSize());
        anchorPoint = new FloatCoordinate(0, 0);
    }

    @Override
    public void setSize(Dimension d) {
        setSize(d.width, d.height);
    }

    @Override
    public void setSize(int width, int height) {
        setSize((float) width, (float) height);
    }

    @Override
    public void setSize(float width, float height) {
        accurateSize.set(width, height);
        superMethods.callSuperSetSize(accurateSize.getXAsInt(), accurateSize.getYAsInt());
        setLocation(accurateLocation.getX(), accurateLocation.getY());
    }

    @Override
    public void setSize(FloatCoordinate f) {
        setSize(f.getX(), f.getY());
    }

    @Override
    public void setLocation(Point p) {
        setLocation(p.x, p.y);
    }

    @Override
    public void setLocation(int x, int y) {
        setLocation((float) x, y);
    }

    @Override
    public void setLocation(float x, float y) {
        accurateLocation.set(x, y);
        superMethods.callSuperSetLocation(
                (int)(x - (accurateSize.getX() * anchorPoint.getX())),
                (int)(y - (accurateSize.getY() * anchorPoint.getY()))
        );
    }

    @Override
    public void setLocation(FloatCoordinate f) {
        setLocation(f.getX(), f.getY());
    }

    @Override
    public void setAnchorPoint(float x, float y) {
        anchorPoint.set(x, y);
        setLocation(accurateLocation);
    }

    @Override
    public void setAnchorPoint(FloatCoordinate f) {
        setAnchorPoint(f.getX(), f.getY());
    }

    @Override
    public FloatCoordinate getAccurateLocation() {
        return getAccurateLocation(new FloatCoordinate());
    }

    @Override
    public FloatCoordinate getAccurateLocation(FloatCoordinate rv) {
        rv.set(accurateLocation);
        return rv;
    }

    @Override
    public FloatCoordinate getAccurateSize() {
        return accurateSize.deepCopy();
    }

    @Override
    public FloatCoordinate getAccurateSize(FloatCoordinate rv) {
        rv.set(accurateSize);
        return rv;
    }

    @Override
    public FloatCoordinate getAnchorPoint() {
        return anchorPoint.deepCopy();
    }

    @Override
    public FloatCoordinate getAnchorPoint(FloatCoordinate rv) {
        rv.set(anchorPoint);
        return rv;
    }
}
