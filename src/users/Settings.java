package users;

public class Settings {
    private boolean soundOn;
    private int difficulty;
    private ColourScheme colourScheme;

    public Settings() {
        this.soundOn = true;
        this.difficulty = 3000;
        this.colourScheme = new ColourScheme(SchemePresets.DEFAULT);
    }

    // ========================

    public boolean isSoundOn() {
        return soundOn;
    }

    public void setSoundOn(boolean soundOn) {
        this.soundOn = soundOn;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public ColourScheme getColourScheme() {
        return colourScheme;
    }

    public void setColourScheme(ColourScheme colourScheme) {
        this.colourScheme = colourScheme;
    }
}
