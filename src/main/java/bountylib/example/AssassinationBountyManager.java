package bountylib.example;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.intel.BaseEventManager;

import bountylib.BountyIntel;
import bountylib.entity.AssassinationEntity;
import bountylib.entity.EntityProvider;

/**
 * This is an example bounty manager implemented using BountyLib. It can be
 * loaded alongisde vanilla bounty manager to additionally provide up to 4
 * Assassination bounties.
 */
public class AssassinationBountyManager extends BaseEventManager {

    // Note: Make sure your KEY is unique across the modverse.
    private static final String KEY = "$yourmod_BountyManager";

    // Note: These can come from your own settings file, from Starsector's
    // settings, or be just hardcoded like here.
    private static int MIN_BOUNTIES = 0;
    private static int MAX_BOUNTIES = 4;

    public AssassinationBountyManager() {
        super();
        Global.getSector().getMemoryWithoutUpdate().set(KEY, this);
    }

    // Note: This is just a convenience method, not needed in your implementation.
    // Just make sure you always fetch the same object.
    public static AssassinationBountyManager getInstance() {
        Object bountyObject = Global.getSector().getMemoryWithoutUpdate().get(KEY);

        return (AssassinationBountyManager) bountyObject;
    }

    @Override
    protected EveryFrameScript createEvent() {
        try {

            AssassinationEntity entity = EntityProvider.makeAssassinationEntity();

            return new BountyIntel(entity, entity.getFleet(), entity.getPerson(), entity.getHideout());
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
