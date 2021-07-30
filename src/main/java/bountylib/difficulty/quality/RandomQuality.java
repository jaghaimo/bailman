package bountylib.difficulty.quality;

import bountylib.difficulty.RandomCalculator;

/**
 * Returns random quality value centered on a value.
 */
public class RandomQuality extends RandomCalculator implements QualityCalculator {

    public RandomQuality(float value, float minmax) {
        super(value, minmax);
    }

    public RandomQuality(float value) {
        super(value, 0);
    }
}
