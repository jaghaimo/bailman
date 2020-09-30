package bailman.intel;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.intel.BaseEventManager;

import bailman.Settings;
import bailman.helper.BountyHelper;
import bailman.intel.entity.Assassination;

public class BountyManager extends BaseEventManager {

    private static final String KEY = "$bailman_BountyManager";

    private int minBounties = Settings.BOUNTIES_MIN;
    private int maxBounties = Settings.BOUNTIES_MAX;

    public BountyManager() {
        super();
        Global.getSector().getMemoryWithoutUpdate().set(KEY, this);
    }

    public static BountyManager getInstance() {
        Object bountyObject = Global.getSector().getMemoryWithoutUpdate().get(KEY);

        return (BountyManager) bountyObject;
    }

    public void setMaxBounties(int m) {
        maxBounties = m;
    }

    public void setMinBounties(int m) {
        minBounties = m;
    }

    @Override
    protected EveryFrameScript createEvent() {
        try {
            int level = BountyHelper.pickLevel();
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
        return maxBounties;
    }

    @Override
    protected int getMinConcurrent() {
        return minBounties;
    }
}
