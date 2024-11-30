package GUIClasses.AccurateUIComponents;

import DataTypes.FloatCoordinate;
import GUIClasses.AccurateContainerManager;
import GUIClasses.Interfaces.AccurateContainer;
import GUIClasses.Interfaces.SuperContainerMethods;

import javax.swing.*;
import java.awt.*;

public class AccurateLabel extends JLabel implements AccurateContainer, SuperContainerMethods {
    private final AccurateContainerManager manager;
    private boolean wrap;

    /**
     * Create a new AccuratePanel object.
     */
    public AccurateLabel() {
        this("UnknownAccLabel");
    }

    /**
     * Create a new AccuratePanel object.
     * @param name Name of the panel.
     */
    public AccurateLabel(String name) {
        manager = new AccurateContainerManager(this, this);
        setName(name);
        wrap = false;
    }

    public void setWrapped(boolean wrap) {
        this.wrap = wrap;
    }

    public void setText(String text) {
        if (wrap) {
            text = wrapString(text, getFontMetrics(getFont()), getWidth());
        }
        super.setText(text);
    }

    public static String wrapString(String text, FontMetrics metrics, int maxWidth) {
        String wrappedText = "";
        String[] words = text.split(" ");
        int lineWidth = 0;

        for (String word : words) {
            int wordWidth = metrics.stringWidth(word + " ");
            if (lineWidth + wordWidth > maxWidth) {
                // Start a new line if adding the word exceeds max width
                wrappedText += "\n";
                lineWidth = 0;
            }
            wrappedText += word + " ";
            lineWidth += wordWidth;
        }

        return "<html><pre>" + wrappedText + "</pre></html>";
    }

    @Override
    public void setSize(Dimension d) {
        manager.setSize(d);
    }

    @Override
    public void setSize(int width, int height) {
        manager.setSize(width, height);
    }

    @Override
    public void setSize(float width, float height) {
        manager.setSize(width, height);
    }

    @Override
    public void setSize(FloatCoordinate f) {
        manager.setSize(f);
    }

    @Override
    public void setLocation(Point p) {
        manager.setLocation(p);
    }

    @Override
    public void setLocation(int x, int y) {
        manager.setLocation(x, y);
    }

    @Override
    public void setLocation(float x, float y) {
        manager.setLocation(x, y);
    }

    @Override
    public void setLocation(FloatCoordinate f) {
        manager.setLocation(f);
    }

    @Override
    public void setAnchorPoint(float x, float y) {
        manager.setAnchorPoint(x, y);
    }

    @Override
    public void setAnchorPoint(FloatCoordinate f) {
        manager.setAnchorPoint(f);
    }

    @Override
    public FloatCoordinate getAccurateLocation() {
        return manager.getAccurateLocation();
    }

    @Override
    public FloatCoordinate getAccurateLocation(FloatCoordinate rv) {
        return manager.getAccurateLocation(rv);
    }

    @Override
    public FloatCoordinate getAccurateSize() {
        return manager.getAccurateSize();
    }

    @Override
    public FloatCoordinate getAccurateSize(FloatCoordinate rv) {
        return manager.getAccurateSize(rv);
    }

    @Override
    public FloatCoordinate getAnchorPoint() {
        return manager.getAnchorPoint();
    }

    @Override
    public FloatCoordinate getAnchorPoint(FloatCoordinate rv) {
        return manager.getAnchorPoint(rv);
    }

    @Override
    public void callSuperSetLocation(int x, int y) {
        super.setLocation(x, y);
    }

    @Override
    public void callSuperSetSize(int width, int height) {
        super.setSize(width, height);
    }

    @Override
    public void callSuperSetBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
    }
}