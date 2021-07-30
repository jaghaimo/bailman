package bountylib.location;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.util.WeightedRandomPicker;

import bountylib.BountyEventData;
import lombok.AllArgsConstructor;

/**
 * Location picker that chooses a non-hidden, participating market as a bounty
 * hideout.
 */
@AllArgsConstructor
public class CoreWorld implements LocationPicker {

    private int level;

    public MarketAPI pickHideout() {
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
}
