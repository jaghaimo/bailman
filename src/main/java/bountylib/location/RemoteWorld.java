package bountylib.location;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;

import lombok.AllArgsConstructor;

/**
 * Location picker that chooses a non-populated planet for a hideout.
 */
@AllArgsConstructor
public class RemoteWorld implements LocationPicker {

    // For vanilla behaviour, instantiate with:
    // Global.getSettings().getFloat("personBountyNoSpawnRangeAroundPlayerLY");
    private float noSpawnRange;

    public SectorEntityToken pickHideout() {
        StarSystemAPI system = pickSystem();

        if (system == null) {
            // Should we throw here?
            return null;
        }

        return pickPlanet(system);
    }

    /**
     * Starsector implementation.
     */
    private SectorEntityToken pickPlanet(StarSystemAPI system) {
        WeightedRandomPicker<SectorEntityToken> picker = new WeightedRandomPicker<SectorEntityToken>();
        for (SectorEntityToken planet : system.getPlanets()) {
            if (planet.isStar())
                continue;
            if (planet.getMarket() != null && !planet.getMarket().isPlanetConditionMarketOnly())
                continue;

            picker.add(planet);
        }
        return picker.pick();
    }

    /**
     * Starsector implementation.
     */
    protected StarSystemAPI pickSystem() {
        WeightedRandomPicker<StarSystemAPI> systemPicker = new WeightedRandomPicker<StarSystemAPI>();
        for (StarSystemAPI system : Global.getSector().getStarSystems()) {
            float mult = 0f;

            if (system.hasPulsar())
                continue;

            if (system.hasTag(Tags.THEME_MISC_SKIP)) {
                mult = 1f;
            } else if (system.hasTag(Tags.THEME_MISC)) {
                mult = 3f;
            } else if (system.hasTag(Tags.THEME_REMNANT_NO_FLEETS)) {
                mult = 3f;
            } else if (system.hasTag(Tags.THEME_RUINS)) {
                mult = 5f;
            } else if (system.hasTag(Tags.THEME_REMNANT_DESTROYED)) {
                mult = 3f;
            } else if (system.hasTag(Tags.THEME_CORE_UNPOPULATED)) {
                mult = 1f;
            }

            for (MarketAPI market : Misc.getMarketsInLocation(system)) {
                if (market.isHidden())
                    continue;
                mult = 0f;
                break;
            }

            float distToPlayer = Misc.getDistanceToPlayerLY(system.getLocation());
            if (distToPlayer < noSpawnRange)
                mult = 0f;

            if (mult <= 0)
                continue;

            float weight = system.getPlanets().size();
            for (PlanetAPI planet : system.getPlanets()) {
                if (planet.isStar())
                    continue;
                if (planet.getMarket() != null) {
                    float h = planet.getMarket().getHazardValue();
                    if (h <= 0f)
                        weight += 5f;
                    else if (h <= 0.25f)
                        weight += 3f;
                    else if (h <= 0.5f)
                        weight += 1f;
                }
            }

            float dist = system.getLocation().length();
            float distMult = Math.max(0, 50000f - dist);

            systemPicker.add(system, weight * mult * distMult);
        }

        return systemPicker.pick();
    }
}
