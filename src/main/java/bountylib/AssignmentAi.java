package bountylib;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.fs.starfarer.api.campaign.FleetAssignment;

/**
 * Manages assignment AI.
 * 
 * TODO: Needs much better implementation.
 * 
 * TODO: Needs support compound AIs (e.g. go to X, wait Z days, go to Y,
 * despawn).
 */
public class AssignmentAi {

    public static FleetAssignment getRandomAssignment() {
        Random rand = new Random();
        List<FleetAssignment> list = Arrays.asList(FleetAssignment.DEFEND_LOCATION, FleetAssignment.DELIVER_CREW,
                FleetAssignment.DELIVER_FUEL, FleetAssignment.DELIVER_MARINES, FleetAssignment.DELIVER_PERSONNEL,
                FleetAssignment.DELIVER_RESOURCES, FleetAssignment.ORBIT_AGGRESSIVE, FleetAssignment.PATROL_SYSTEM,
                FleetAssignment.RESUPPLY);

        return list.get(rand.nextInt(list.size()));
    }
}
