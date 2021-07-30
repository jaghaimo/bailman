package bountylib.difficulty;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RandomCalculator {

    // The average value that will be returned.
    private final float value;

    // The min and max value to randomly alter value.
    private final float minmax;

    public float calculate() {
        return value + (float) (Math.random() - 0.5f) * minmax;
    }
}
