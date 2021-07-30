package bountylib;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetAssignment;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.util.Misc;

/**
 * TODO: This monolitic helper needs to be split up.
 */
public class BountyHelper {

    public final static String[] DANGER_LEVEL = { "none", "low", "medium", "high", "very high" };

    public static int calculateBountyCredits(int level) {
        float base = Global.getSettings().getFloat("basePersonBounty");
        float perLevel = Global.getSettings().getFloat("personBountyPerLevel");
        float random = perLevel * (int) (Math.random() * 30) / 15f;

        return (int) ((base + perLevel * level + random) * 1.5);
    }

    public static Color getDangerColor(CampaignFleetAPI fleet) {
        int dangerLevel = Misc.getDangerLevel(fleet);

        if (dangerLevel < 3) {
            return Misc.getPositiveHighlightColor();
        }

        if (dangerLevel > 3) {
            return Misc.getNegativeHighlightColor();
        }

        return Misc.getHighlightColor();
    }

    public static String getDangerLevel(CampaignFleetAPI fleet) {
        int dangerLevel = Misc.getDangerLevel(fleet);
        int maxDangerLevel = DANGER_LEVEL.length;

        return DANGER_LEVEL[Math.min(dangerLevel, maxDangerLevel) - 1];
    }

    public static CampaignFleetAPI spawnFleet(float fp, float qf, MarketAPI hideout, PersonAPI person) {
        String fleetFactionId = person.getFaction().getId();
        String fleetName = "";
        fleetName = person.getName().getLast() + "'s Fleet";

        FleetParamsV3 params = new FleetParamsV3(null, hideout.getLocationInHyperspace(), fleetFactionId, //
                qf, // quality
                FleetTypes.PERSON_BOUNTY_FLEET, fp, // combatPts
                0, // freighterPts
                0, // tankerPts
                0f, // transportPts
                0f, // linerPts
                0f, // utilityPts
                0f // qualityMod
        );
        params.ignoreMarketFleetSizeMult = true;

        CampaignFleetAPI fleet = FleetFactoryV3.createFleet(params);
        fleet.setCommander(person);
        fleet.getFlagship().setCaptain(person);
        fleet.setFaction(fleetFactionId, true);
        fleet.setName(fleetName);
        FleetFactoryV3.addCommanderSkills(person, fleet, null);
        LocationAPI location = hideout.getContainingLocation();
        location.addEntity(fleet);
        fleet.setLocation(hideout.getLocation().x - 500, hideout.getLocation().y + 500);
        fleet.getAI().addAssignment(FleetAssignment.ORBIT_AGGRESSIVE, hideout.getPrimaryEntity(), 1000000f, null);

        return fleet;
    }
}
