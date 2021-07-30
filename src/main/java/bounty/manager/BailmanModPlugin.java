package bounty.manager;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;

import org.json.JSONObject;

import bounty.manager.intel.BountyManager;

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
        BountyManager bountyManager = BountyManager.getInstance();
        if (bountyManager == null) {
            bountyManager = new BountyManager();
        }

        // apply current settings
        bountyManager.setMinBounties(settings.getMin());
        bountyManager.setMaxBounties(settings.getMax());

        // add to scripts
        Global.getSector().addScript(bountyManager);
    }
}
