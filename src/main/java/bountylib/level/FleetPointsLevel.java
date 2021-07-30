package bountylib.level;

import lombok.RequiredArgsConstructor;

/**
 * Returns level based on fleet points and desired minmax (10% by default).
 */
@RequiredArgsConstructor
public class FleetPointsLevel extends DefaultLevel {

    // The average value that will be returned.
    private final float fleetPoints;

    // The min and max value to randomly alter fleetPoints.
    private final float minmax;

    public FleetPointsLevel(float fleetPoints) {
        this(fleetPoints, fleetPoints * 0.1f);
    }

    @Override
    protected int pickAnyLevel() {
        return (int) Math.round(fleetPoints + (Math.random() - 0.5f) * minmax);
    }
}
