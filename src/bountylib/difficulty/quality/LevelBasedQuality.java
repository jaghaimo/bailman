package bountylib.difficulty.quality;

import lombok.RequiredArgsConstructor;

/**
 * Implements vanilla logic for fleet quality calculation.
 */
@RequiredArgsConstructor
public class LevelBasedQuality implements QualityCalculator {

    private final int level;

    @Override
    public float calculate() {
        return Math.max((float) level / 10f, 1) + 0.3f;
    }
}
