package bountylib.person;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.events.OfficerManagerEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SameFactionPerson implements PersonProvider {

    private final String factionId;
    private final int level;

    public PersonAPI createPerson() {
        FactionAPI faction = Global.getSector().getFaction(factionId);
        int personLevel = (int) (5 + level * 1.5f);
        return OfficerManagerEvent.createOfficer(faction, personLevel);
    }
}
