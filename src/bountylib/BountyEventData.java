package bountylib;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.shared.PersonBountyEventData;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;

public class BountyEventData {

    public static PersonBountyEventData getSharedData() {
        return SharedData.getData().getPersonBountyEventData();
    }

    public static boolean isParticipating(MarketAPI market) {
        return getSharedData().isParticipating(market.getFactionId());
    }
}
