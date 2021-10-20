package bountylib.difficulty.fleetpoints;

import lombok.RequiredArgsConstructor;

/**
 * Vanilla implementation of fleet point calculation.
 */
@RequiredArgsConstructor
public class LevelBasedFleetPoints {

    private final int level;

    public float calculate() {
        float fp = (5 + level * 5) * 5f;
        fp *= 0.75f + (float) Math.random() * 0.25f;

        if (level >= 7) {
            fp += 20f;
        }
        if (level >= 8) {
            fp += 30f;
        }
        if (level >= 9) {
            fp += 50f;
        }
        if (level >= 10) {
            fp += 50f;
        }

        return fp;
    }
}
