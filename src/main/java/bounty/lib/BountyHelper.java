package bounty.lib;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.events.OfficerManagerEvent;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;

import bounty.manager.Constants;

/**
 * TODO: This monolitic helper needs to be split up.
 */
public class BountyHelper {

    public static int calculateBountyCredits(int level) {
        float base = Global.getSettings().getFloat("basePersonBounty");
        float perLevel = Global.getSettings().getFloat("personBountyPerLevel");
        float random = perLevel * (int) (Math.random() * 30) / 15f;

        return (int) ((base + perLevel * level + random) * 1.5);
    }

    public static PersonAPI createPerson(int level, String factionId) {
        FactionAPI faction = Global.getSector().getFaction(factionId);
        int personLevel = (int) (5 + level * 1.5f);
        return OfficerManagerEvent.createOfficer(faction, personLevel);
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
        int maxDangerLevel = Constants.DANGER_LEVEL.length;

        return Constants.DANGER_LEVEL[Math.min(dangerLevel, maxDangerLevel) - 1];
    }

    public static MarketAPI pickHideout(int level) {
        WeightedRandomPicker<MarketAPI> picker = new WeightedRandomPicker<MarketAPI>();

        for (MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
            boolean isNotParticipating = !BountyEventData.isParticipating(market);
            boolean isHidden = market.isHidden();
            boolean isPlayerFaction = market.getFaction().isPlayerFaction();

            if (isNotParticipating || isHidden || isPlayerFaction) {
                continue;
            }

            float weight = 10 - Math.abs(market.getSize() - level);
            picker.add(market, weight);
        }

        return picker.pick();
    }

    public static CampaignFleetAPI spawnFleet(int level, MarketAPI hideout, PersonAPI person) {
        String fleetFactionId = person.getFaction().getId();
        String fleetName = "";
        fleetName = person.getName().getLast() + "'s Fleet";
        float qf = Math.max((float) level / 10f, 1) + 0.3f;
        float fp = calculateFp(level);

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
        fleet.getAI().addAssignment(AssignmentAi.getRandomAssignment(), hideout.getPrimaryEntity(), 1000000f, null);

        return fleet;
    }

    private static float calculateFp(int level) {
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
