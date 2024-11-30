package GUIClasses.AccurateUIComponents;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class AccurateImageIcon extends ImageIcon {
    /** Different ways that the image can be painted onto the screen. */
    public enum PaintMode {
        /** Default ImageIcon behavior. */
        DEFAULT,
        /** Stretch the image to fill its parent container. */
        STRETCH,
        /** Resize the image to while retaining its origin size ratio.  */
        RATIO,
        /** Resize the image to fill its parent container while retaining its origin size ratio.  */
        RATIO_FILL
    }

    private boolean xMirrored, yMirrored;
    private PaintMode mode;
    private float alpha;

    /**
     * Create an empty AccurateImageIcon object.
     */
    public AccurateImageIcon() {
        this(new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB));
    }

    /**
     * Create a new AccurateImageIcon object.
     * @param image The image
     */
    public AccurateImageIcon(Image image) {
        this(image, PaintMode.DEFAULT);
    }

    /**
     * Create a new AccurateImageIcon object.
     * @param image The image
     * @param mode The selected paint mode
     */
    public AccurateImageIcon(Image image, PaintMode mode) {
        super(image);
        this.mode = mode;
        xMirrored = false;
        yMirrored = false;
        alpha = 1f;
    }

    /**
     * Resize the image.
     * @param width New width
     * @param height New height
     */
    public void resizeImage(int width, int height) {
        Image resized = getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        setImage(resized);
    }

    /**
     * Resize the image.
     * @param size New size
     */
    public void resizeImage(Dimension size) {
        resizeImage(size.width, size.height);
    }

    /**
     * Set the image's mirrored state
     * @param x {@code true} if the image should be mirrored on the x-axis, {@code false} if the image should not be mirrored.
     */
    public void setMirrored(boolean x, boolean y) {
        xMirrored = x;
        yMirrored = y;
    }

    /**
     * Set the image's paint mode.
     * @param mode {@code enum} representing the paint mode that should be used.
     * @see PaintMode
     */
    public void setMode(PaintMode mode) {
        this.mode = mode;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    /**
     * Get the image's mirrored state
     * @return {@code true} if the image should be mirrored on the X axis,
     * {@code false} if the image should not be mirrored.
     */
    public boolean getXMirrored() {
        return xMirrored;
    }

    /**
     * Get the image's mirrored state
     * @return {@code true} if the image should be mirrored on the Y axis,
     * {@code false} if the image should not be mirrored.
     */
    public boolean getYMirrored() {
        return yMirrored;
    }

    public float getAlpha() {
        return alpha;
    }

    /**
     * Get the current set paint mode
     * @return {@code enum}
     * @see PaintMode
     */
    public PaintMode getMode() {
        return mode;
    }

    @Override
    public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
        setImageObserver(c);

        // Renders smoothly
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        // Mirror the image if requested
        g2d.translate(
                (xMirrored ? c.getWidth() : 0),
                (yMirrored ? c.getHeight() : 0)
        );
        g2d.scale(
                (xMirrored ? -1 : 1),
                (yMirrored ? -1 : 1)
        );

        // Painting the image
        if (mode == null) {
            mode = PaintMode.DEFAULT;
        }

        switch (mode) {
            case STRETCH -> paintIconStretch(c, g2d, x, y);
            case RATIO -> paintIconRatio(c, g2d, x, y);
            case RATIO_FILL -> paintIconRatioFill(c, g2d, x, y);
            case DEFAULT -> super.paintIcon(c, g, x, y);
        }
    }

    private synchronized void paintIconStretch(Component c, Graphics2D g2d, int x, int y) {
        g2d.drawImage(getImage(), 0, 0, c.getWidth(), c.getHeight(), (getImageObserver() == null ? c : getImageObserver()));
    }

    private synchronized void paintIconRatio(Component c, Graphics2D g2d, int x, int y) {
        ImageObserver observer = (getImageObserver() == null ? c : getImageObserver());

        int cw = c.getWidth();
        int ch = c.getHeight();
        int iw = getIconWidth();
        int ih = getIconHeight();

        float ratioW = (((float) cw)/iw);
        float ratioH = (((float) ch)/ih);
        float ratio = Math.min(ratioW, ratioH);

        // New image size
        int newIw = Math.round(ratio*iw);
        int newIh = Math.round(ratio*ih);

        int newX = (cw/2)-(newIw/2);
        int newY = (ch/2)-(newIh/2);

        g2d.drawImage(getImage(), newX, newY, newIw, newIh, observer); // Use the set image observer instead
    }

    private synchronized void paintIconRatioFill(Component c, Graphics2D g2d, int x, int y) {
        ImageObserver observer = (getImageObserver() == null ? c : getImageObserver());

        int cw = c.getWidth();
        int ch = c.getHeight();
        int iw = getIconWidth();
        int ih = getIconHeight();

        float ratioW = (((float) cw)/iw);
        float ratioH = (((float) ch)/ih);
        float ratio = Math.max(ratioW, ratioH);

        // New image size
        int newIw = Math.round(ratio*iw);
        int newIh = Math.round(ratio*ih);

        int newX = (cw/2)-(newIw/2);
        int newY = (ch/2)-(newIh/2);

        g2d.drawImage(getImage(), newX, newY, newIw, newIh, observer); // Use the set image observer instead
    }
}