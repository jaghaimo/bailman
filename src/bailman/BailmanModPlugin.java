package bailman;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.intel.BaseEventManager;

import org.json.JSONObject;

public class BailmanModPlugin extends BaseModPlugin {

    private final String SETTINGS_FILE = "settings.json";

    private Settings settings;

    @Override
    public void onApplicationLoad() throws Exception {
        JSONObject rawSettings;

        try {
            rawSettings = Global.getSettings().loadJSON(SETTINGS_FILE);
        } catch (Exception exception) {
            // use default settings
            rawSettings = new JSONObject();
        }

        settings = new Settings(rawSettings);
    }

    @Override
    public void onNewGameAfterEconomyLoad() {
        registerBountyManager();
    }

    @Override
    public void onGameLoad(boolean newGame) {
        registerBountyManager();
    }

    private void registerBountyManager() {
        BaseEventManager bountyManager = AssassinationBountyManager.getInstance();
        if (bountyManager == null) {
            bountyManager = new AssassinationBountyManager();
        }

        // apply current settings
        AssassinationBountyManager.MIN_BOUNTIES = settings.getMin();
        AssassinationBountyManager.MAX_BOUNTIES = settings.getMin();

        // add to scripts
        Global.getSector().addScript(bountyManager);
    }
}
