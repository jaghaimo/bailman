package bountylib.person;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.util.WeightedRandomPicker;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OpposingFactionPerson implements PersonProvider {

    private final String factionId;
    private final int level;

    @Override
    public PersonAPI createPerson() {
        String opposingFactionId = findOpposingFaction();
        if (opposingFactionId == null) {
            // Should we throw here?
            return null;
        }
        SameFactionPerson sameFactionPerson = new SameFactionPerson(opposingFactionId, level);
        return sameFactionPerson.createPerson();
    }

    /**
     * The more they hate each other, the more likely they will be picked.
     */
    private String findOpposingFaction() {
        WeightedRandomPicker<String> picker = new WeightedRandomPicker<>();
        for (FactionAPI faction : Global.getSector().getAllFactions()) {
            String id = faction.getId();
            float rel = Global.getSector().getFaction(factionId).getRelationship(id);
            if (rel > 0) {
                continue;
            }
            picker.add(id, -rel);
        }
        return picker.pick();
    }
}
