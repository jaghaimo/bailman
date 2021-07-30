package bountylib.example;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.intel.BaseEventManager;

import bountylib.BountyHelper;
import bountylib.intel.Assassination;
import bountylib.intel.BountyIntel;
import bountylib.intel.IntelEntity;
import level.DefaultLevel;

/**
 * This is an example of a bounty manager implemented using BountyLib.
 * 
 * Note: This class is not used anywhere
 */
public class BountyManager extends BaseEventManager {

    // Note: Make sure your KEY is unique across the modverse.
    private static final String KEY = "$yourmod_BountyManager";

    // Note: These can come from your own settings file, from Starsector's
    // settings, or be just hardcoded like here.
    private static int MIN_BOUNTIES = 0;
    private static int MAX_BOUNTIES = 4;

    public BountyManager() {
        super();
        Global.getSector().getMemoryWithoutUpdate().set(KEY, this);
    }

    // Note: This is just a convenience method, not needed in your implementation.
    // Just make sure you always fetch the same object.
    public static BountyManager getInstance() {
        Object bountyObject = Global.getSector().getMemoryWithoutUpdate().get(KEY);

        return (BountyManager) bountyObject;
    }

    @Override
    protected EveryFrameScript createEvent() {
        try {
            int level = new DefaultLevel().pickLevel();
            int bountyCredits = BountyHelper.calculateBountyCredits(level);
            MarketAPI hideout = BountyHelper.pickHideout(level);
            PersonAPI person = BountyHelper.createPerson(level, hideout.getFactionId());
            CampaignFleetAPI fleet = BountyHelper.spawnFleet(level, hideout, person);
            IntelEntity entity = new Assassination(bountyCredits, fleet, person, hideout.getPrimaryEntity());

            return new BountyIntel(entity, fleet, person, fleet);
        } catch (Exception exception) {
            return null;
        }
    }

    @Override
    protected int getMaxConcurrent() {
        return MAX_BOUNTIES;
    }

    @Override
    protected int getMinConcurrent() {
        return MIN_BOUNTIES;
    }
}
