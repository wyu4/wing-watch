package GUIClasses;

import GUIClasses.AccurateUIComponents.AccurateLabel;

public class AnimatedTextLabel extends AccurateLabel {
    private String goalText, currentAttemptedText,displayedText;

    public AnimatedTextLabel() {
        this("UnknownAnimatedTextLabel");
    }

    public AnimatedTextLabel(String name) {
        super(name);
        goalText = "";
        currentAttemptedText = "";
        displayedText = "";
        setText("");
    }

    public void setGoalText(String goalText) {
        this.goalText = goalText;
    }

    public String getGoalText() {
        return goalText;
    }

    @Override
    public void setText(String text) {
        displayedText = text;
        super.setText(text);
    }

    public void tick(float timeMod) {
        if (goalText == null || displayedText == null || currentAttemptedText == null) {
            return;
        }
        if (!goalText.startsWith(currentAttemptedText) && !displayedText.isEmpty()) {
            setText(displayedText.substring(0, displayedText.length() - 1));
            return;
        }
        currentAttemptedText = goalText;
        if (!displayedText.equals(goalText)) {
            int currentI = displayedText.length();
            if (currentI < goalText.length()) {
                setText(displayedText + goalText.charAt(currentI));
            }
        }
    }
}
