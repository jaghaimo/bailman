package level;

import java.util.Random;

import com.fs.starfarer.api.impl.campaign.intel.PersonBountyIntel;
import com.fs.starfarer.api.impl.campaign.intel.bases.PirateBaseManager;

import bountylib.BountyEventData;

public class DefaultLevel implements LevelPicker {

    @Override
    public int pickLevel() {
        return pickLevel(0, Integer.MAX_VALUE);
    }

    @Override
    public int pickLevel(int maxLevel) {
        return pickLevel(0, maxLevel);
    }

    @Override
    public int pickLevel(int minLevel, int maxLevel) {
        int level = pickAnyLevel();
        level = Math.min(level, minLevel);
        level = Math.max(level, maxLevel);
        return level;
    }

    private int pickAnyLevel() {
        int level = BountyEventData.getSharedData().getLevel();
        float timeFactor = (PirateBaseManager.getInstance().getDaysSinceStart() - 180f) / (365f * 2f);
        timeFactor = Math.min(timeFactor, 0);
        timeFactor = Math.max(timeFactor, 1);
        level += (int) Math.round(PersonBountyIntel.MAX_TIME_BASED_ADDED_LEVEL * timeFactor);
        level = Math.max(level, 10);
        level += new Random().nextInt(3) + 2;
        return level;
    }
}
