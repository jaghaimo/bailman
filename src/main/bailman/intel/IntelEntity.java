package bailman.intel;

import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin.ListInfoMode;
import com.fs.starfarer.api.impl.campaign.intel.PersonBountyIntel.BountyResult;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

public interface IntelEntity {

    public void addBulletPoints(BountyIntel plugin, TooltipMakerAPI info, ListInfoMode mode);

    public void createSmallDescription(BountyIntel plugin, TooltipMakerAPI info, float width, float height);

    public int getBountyCredits();

    public String getTitle(BountyResult result);

    public String getIcon();
}
