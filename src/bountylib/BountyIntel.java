package bountylib;

import java.awt.Color;
import java.util.Set;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BattleAPI;
import com.fs.starfarer.api.campaign.CampaignEventListener.FleetDespawnReason;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.FleetAssignment;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.listeners.FleetEventListener;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.PersonBountyIntel;
import com.fs.starfarer.api.impl.campaign.intel.PersonBountyIntel.BountyResult;
import com.fs.starfarer.api.impl.campaign.intel.PersonBountyIntel.BountyResultType;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import bountylib.entity.BountyEntity;

public class BountyIntel extends BaseIntelPlugin implements FleetEventListener {

    private float elapsedDays = 0f;
    private float duration = PersonBountyIntel.MAX_DURATION / 3;

    private BountyResult result;
    private CampaignFleetAPI fleet;
    private BountyEntity entity;
    private PersonAPI person;
    private SectorEntityToken hideout;

    public BountyIntel(BountyEntity e, CampaignFleetAPI f, PersonAPI p, SectorEntityToken h) {
        entity = e;
        fleet = f;
        hideout = h;
        person = p;

        fleet.addEventListener(this);
        Misc.makeImportant(fleet, "pbe", duration + 20f);
        Global.getSector().getIntelManager().queueIntel(this);
    }

    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        info.addPara(entity.getTitle(result), getTitleColor(mode), 0f);
        entity.addBulletPoints(this, info, mode);
    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        entity.createSmallDescription(this, info, width, height);
    }

    @Override
    public FactionAPI getFactionForUIColors() {
        return fleet.getFaction();
    }

    @Override
    public String getIcon() {
        return entity.getIcon();
    }

    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        Set<String> tags = super.getIntelTags(map);
        tags.add(Tags.INTEL_BOUNTY);
        tags.add(fleet.getFaction().getId());

        return tags;
    }

    @Override
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        return hideout;
    }

    @Override
    public String getSmallDescriptionTitle() {
        return entity.getTitle(result);
    }

    @Override
    public void reportBattleOccurred(CampaignFleetAPI f, CampaignFleetAPI p, BattleAPI b) {
        boolean isDone = isDone() || result != null;
        boolean isNotInvolved = !b.isPlayerInvolved() || !b.isInvolved(f) || b.onPlayerSide(f);
        boolean isFlagshipAlive = f.getFlagship() != null && f.getFlagship().getCaptain() == person;

        if (isDone || isNotInvolved || isFlagshipAlive) {
            return;
        }

        if (b.isInvolved(f) && !b.isPlayerInvolved()) {
            if (f.getFlagship() == null || f.getFlagship().getCaptain() != person) {
                f.setCommander(f.getFaction().createRandomPerson());
                result = new BountyResult(BountyResultType.END_OTHER, 0, null);
                cleanUp(true);
                return;
            }
        }

        int payment = (int) (entity.getBountyCredits() * b.getPlayerInvolvementFraction());
        if (payment <= 0) {
            result = new BountyResult(BountyResultType.END_OTHER, 0, null);
            cleanUp(true);
            return;
        }

        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        playerFleet.getCargo().getCredits().add(payment);
        result = new BountyResult(BountyResultType.END_PLAYER_BOUNTY, payment, null);
        SharedData.getData().getPersonBountyEventData().reportSuccess();
        cleanUp(false);
    }

    @Override
    public void reportFleetDespawnedToListener(CampaignFleetAPI f, FleetDespawnReason r, Object p) {
        if (isDone() || result != null) {
            return;
        }

        if (fleet == f) {
            f.setCommander(f.getFaction().createRandomPerson());
            result = new BountyResult(BountyResultType.END_OTHER, 0, null);
            cleanUp(true);
        }
    }

    public float getDuration() {
        return duration;
    }

    public float getElapsedDays() {
        return elapsedDays;
    }

    public BountyResult getResult() {
        return result;
    }

    public void addDays(TooltipMakerAPI info, String after, float days, Color c) {
        super.addDays(info, after, days, c);
    }

    public void bullet(TooltipMakerAPI info) {
        super.bullet(info);
    }

    public Color getBulletColorForMode(ListInfoMode mode) {
        return super.getBulletColorForMode(mode);
    }

    public void unindent(TooltipMakerAPI info) {
        super.unindent(info);
    }

    @Override
    protected void advanceImpl(float amount) {
        float days = Global.getSector().getClock().convertToDays(amount);
        elapsedDays += days;

        if (elapsedDays >= duration && !isDone()) {
            boolean canEnd = fleet == null || !fleet.isInCurrentLocation();
            if (canEnd) {
                result = new BountyResult(BountyResultType.END_TIME, 0, null);
                cleanUp(true);
                return;
            }
        }

        if (fleet == null) {
            return;
        }

        if (fleet.getFlagship() == null || fleet.getFlagship().getCaptain() != person) {
            result = new BountyResult(BountyResultType.END_OTHER, 0, null);
            cleanUp(!fleet.isInCurrentLocation());
            return;
        }

        // TODO: Add AiManager that will update fleet's AI as needed (for compound AIs)
    }

    @Override
    protected void notifyEnding() {
        super.notifyEnding();
        cleanUpFleetAndEndIfNecessary();
    }

    private void cleanUp(boolean onlyIfImportant) {
        sendUpdateIfPlayerHasIntel(result, onlyIfImportant);
        cleanUpFleetAndEndIfNecessary();
    }

    private void cleanUpFleetAndEndIfNecessary() {
        if (fleet != null) {
            Misc.makeUnimportant(fleet, "pbe");
            fleet.clearAssignments();

            if (hideout != null) {
                fleet.getAI().addAssignment(FleetAssignment.GO_TO_LOCATION_AND_DESPAWN, hideout, 1000000f, null);
            } else {
                fleet.despawn();
            }
        }

        if (!isEnding() && !isEnded()) {
            endAfterDelay();
        }
    }
}
