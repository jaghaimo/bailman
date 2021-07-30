package bountylib.entity;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;

import bountylib.BountyHelper;
import bountylib.location.CoreWorld;
import level.DefaultLevel;

/**
 * A convenience class for providing supported bounties
 */
public class EntityProvider {

    public static AssassinationEntity makeAssassinationEntity() {
        int level = new DefaultLevel().pickLevel();
        int bountyCredits = BountyHelper.calculateBountyCredits(level);
        MarketAPI hideout = new CoreWorld(level).pickHideout();
        PersonAPI person = BountyHelper.createPerson(level, hideout.getFactionId());
        CampaignFleetAPI fleet = BountyHelper.spawnFleet(level, hideout, person);

        return new AssassinationEntity(bountyCredits, fleet, person, hideout.getPrimaryEntity());

    }
}