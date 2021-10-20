package bountylib.entity;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin.ListInfoMode;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.intel.PersonBountyIntel.BountyResult;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import bountylib.BountyIntel;

public interface BountyEntity {

    public CampaignFleetAPI getFleet();

    public PersonAPI getPerson();

    public SectorEntityToken getHideout();

    public String getIcon();

    public String getTitle(BountyResult result);

    public int getBountyCredits();

    public void addBulletPoints(BountyIntel plugin, TooltipMakerAPI info, ListInfoMode mode);

    public void createSmallDescription(BountyIntel plugin, TooltipMakerAPI info, float width, float height);
}
