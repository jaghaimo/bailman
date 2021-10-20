package bountylib.difficulty.fleetpoints;

import bountylib.difficulty.Calculator;
import bountylib.difficulty.RandomCalculator;

/**
 * Fleet point calculation randomized around desired value and minmax (10% by
 * default).
 */
public class RandomFleetPoints extends RandomCalculator implements Calculator {

    public RandomFleetPoints(float value, float minmax) {
        super(value, minmax);
    }

    public RandomFleetPoints(float value) {
        this(value, value * 0.1f);
    }
}
