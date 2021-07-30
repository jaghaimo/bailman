package bountylib.entity;

import java.awt.Color;
import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin.ListInfoMode;
import com.fs.starfarer.api.characters.FullName.Gender;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.intel.PersonBountyIntel.BountyResult;
import com.fs.starfarer.api.impl.campaign.intel.PersonBountyIntel.BountyResultType;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.BreadcrumbSpecial;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import bountylib.BountyHelper;
import bountylib.BountyIntel;
import lombok.Getter;

/**
 * Retrieval mission, defeat enemy fleet and retrieve the flagship. If
 * successfully retrieved, give an option to either lie to the employer (keep
 * the ship at the cost of small relationship penalty), or return it as per
 * contract (credits and reputation gain). If not retrieved (either destroyed or
 * scrapped) gain half of the credits and no reputation.
 * 
 * # TODO: This is unfinished
 */
@Getter
public class RetrievalEntity implements BountyEntity {

    private int bountyCredits;
    private int reputationGain;
    private FactionAPI faction;
    private CampaignFleetAPI fleet;
    private PersonAPI person;
    private SectorEntityToken hideout;

    public RetrievalEntity(int b, int r, CampaignFleetAPI c, FactionAPI f, PersonAPI p, SectorEntityToken h) {
        bountyCredits = b;
        reputationGain = r;
        fleet = c;
        faction = f;
        hideout = h;
        person = p;
    }

    public void addBulletPoints(BountyIntel plugin, TooltipMakerAPI info, ListInfoMode mode) {
        float initPad = (mode == ListInfoMode.IN_DESC) ? 10f : 3f;
        float duration = plugin.getDuration();
        float elapsedDays = plugin.getElapsedDays();
        Color h = Misc.getHighlightColor();
        Color tc = plugin.getBulletColorForMode(mode);
        BountyResult result = plugin.getResult();
        plugin.bullet(info);

        if (result == null) {
            if (mode == ListInfoMode.IN_DESC) {
                info.addPara("%s reward", initPad, tc, h, Misc.getDGSCredits(bountyCredits));
                int days = Math.max((int) (duration - elapsedDays), 1);
                plugin.addDays(info, "remaining", days, tc);
            } else {
                info.addPara("Target: " + faction.getDisplayName() + "'s " + person.getRank(), initPad, tc,
                        faction.getBaseUIColor(), faction.getDisplayName());

                if (!plugin.isEnding()) {
                    int days = (int) (duration - elapsedDays);
                    String daysStr = "days";

                    if (days <= 1) {
                        days = 1;
                        daysStr = "day";
                    }

                    info.addPara("%s reward, %s " + daysStr + " remaining", 0f, tc, h,
                            Misc.getDGSCredits(bountyCredits), "" + days);
                }
            }
            String danger = BountyHelper.getDangerLevel(fleet);
            info.addPara("Danger: " + danger, 0f, tc, BountyHelper.getDangerColor(fleet), danger);
        } else if (result.type == BountyResultType.END_PLAYER_BOUNTY) {
            info.addPara("%s received", initPad, tc, h, Misc.getDGSCredits(result.payment));
        }

        plugin.unindent(info);
    }

    public void createSmallDescription(BountyIntel plugin, TooltipMakerAPI info, float width, float height) {
        BountyResult result = plugin.getResult();

        info.addImage(getIcon(), width, 128, 10f);
        info.addPara("An official hailing from " + faction.getDisplayNameWithArticle()
                + " goverment is requesting retrival of a highly experimental flagship that was stolen by "
                + person.getName().getFullName(), 10f);

        // bounty completed
        if (result != null) {
            if (result.type == BountyResultType.END_PLAYER_BOUNTY) {
                info.addPara("You have successfully completed this bounty.", 10f);
            } else {
                info.addPara("This bounty is no longer on offer.", 10f);
            }
        }

        addBulletPoints(plugin, info, ListInfoMode.IN_DESC);

        // bounty is still available for completion
        if (result == null) {
            SectorEntityToken fake = hideout.getContainingLocation().createToken(0, 0);
            fake.setOrbit(Global.getFactory().createCircularOrbit(hideout, 0, 1000, 100));
            String loc = BreadcrumbSpecial.getLocatedString(fake);
            loc = loc.replaceAll("orbiting", "hiding out near");
            loc = loc.replaceAll("located in", "hiding out in");
            String heOrShe = (person.getGender() == Gender.MALE) ? "He " : "She ";
            info.addPara(heOrShe + " rumored to be " + loc + ".", 10f);

            List<FleetMemberAPI> list = fleet.getFleetData().getMembersListCopy();
            int cols = 7;
            int rows = (int) Math.ceil(list.size() / cols) + 1;
            float iconSize = width / cols;
            String h = (person.getGender() == Gender.MALE) ? "his" : "her";
            info.addPara("The contract also contains full intel on the ships under " + h + " command.", 10f);
            info.addShipList(cols, rows, iconSize, plugin.getFactionForUIColors().getBaseUIColor(), list, 10f);
        }
    }

    public int getBountyCredits() {
        return bountyCredits;
    }

    public String getTitle(BountyResult result) {
        String n = person.getName().getFullName();

        if (result == null) {
            return "Retrieval - " + n;
        }

        if (result.type == BountyResultType.END_PLAYER_BOUNTY) {
            return "Retrieval Completed - " + n;
        }

        return "Retrieval Ended - " + n;
    }

    public String getIcon() {
        return person.getPortraitSprite();
    }
}
