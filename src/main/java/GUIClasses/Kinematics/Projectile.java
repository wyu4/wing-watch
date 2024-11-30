package GUIClasses.Kinematics;

import DataTypes.FloatCoordinate;
import GUIClasses.AccurateUIComponents.AccuratePanel;

import java.awt.*;

public class Projectile extends AccuratePanel {
    public static final float GRAVITY = 1960f;

    private final FloatCoordinate initialVelocity, initialDisplacment;
    private float verticalForce;
    private long initialTimeStamp;
    private boolean grounded;

    /**
     * Create a new AccuratePanel object with the properties of a projectile.
     */
    public Projectile() {
        this("UnknownPhyPanel");
    }

    /**
     * Create a new AccuratePanel object with the properties of a projectile.
     * @param name Name of the panel.
     */
    public Projectile(String name) {
        super(name);
        grounded = false;

        initialVelocity = new FloatCoordinate(0, 0);
        initialDisplacment = getAccurateLocation();

        initialTimeStamp = System.currentTimeMillis();
        verticalForce = GRAVITY;
    }

    /**
     * Calculates the final displacement of a projectile (on one axis).
     * @param vI Initial velocity (pixels / s)
     * @param dI Initial displacement (pixels)
     * @param a Acceleration (pixels / s^2)
     * @param t Time elapsed (s)
     * @return The final displacement in pixels as a floating point number. The actual equation is {@code df = vIt + 1/2at^2 + dI}
     */
    public static float calculateDisplacement(float vI, float dI, float a, float t) {
        return (vI*t) + ((0.5f)*a*t*t) + dI;
    }

    public void step() {
        Container parent = getParent();
        if (parent == null) {
            return;
        }

        float secondsElapsed = Math.clamp((System.currentTimeMillis() - initialTimeStamp)/1000f, -3600, 3600); // Hard limit time to an hour

        float newDisplacementX = calculateDisplacement(initialVelocity.getX(), initialDisplacment.getX(), 0, secondsElapsed);
        float newDisplacementY = calculateDisplacement(initialVelocity.getY(), initialDisplacment.getY(), verticalForce, secondsElapsed);

        float groundLimit = parent.getHeight();
        float wallLimit = parent.getWidth();

        setLocation(
                Math.clamp(newDisplacementX, 0, wallLimit),
                Math.clamp(newDisplacementY, 0, groundLimit)
        );

        grounded = (newDisplacementY >= groundLimit);
        if (
                (newDisplacementY <= 0) && (newDisplacementY - getAccurateLocation().getY() < 0) ||
                (grounded && (newDisplacementY - getAccurateLocation().getY() > 0))
        ) {
            setVelocity(grounded ? 0 : initialVelocity.getX(), 0);
        }
    }

    public void setVelocity(float x, float y) {
        initialTimeStamp = System.currentTimeMillis();
        initialVelocity.set(x, y);
        getAccurateLocation(initialDisplacment);
    }

    public void setVelocity(FloatCoordinate v) {
        setVelocity(v.getX(), v.getY());
    }

    public void setVerticalForce(float f) {
        verticalForce = f;
    }

    public FloatCoordinate getInitialVelocity() {
        return getInitialVelocity(new FloatCoordinate());
    }

    public FloatCoordinate getInitialVelocity(FloatCoordinate rv) {
        rv.set(initialVelocity);
        return rv;
    }

    public float getVerticalForce() {
        return verticalForce;
    }

    public boolean isGrounded() {
        return grounded;
    }
}
