package bountylib.level;

public interface LevelPicker {

    public int pickLevel();

    public int pickLevel(int maxLevel);

    public int pickLevel(int minLevel, int maxLevel);
}
