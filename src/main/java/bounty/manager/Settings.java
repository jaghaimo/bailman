package bounty.manager;

import org.json.JSONException;
import org.json.JSONObject;

public class Settings {

    public final static int BOUNTIES_MIN = 0;
    public final static int BOUNTIES_MAX = 9;

    private JSONObject settings;

    private int minBounties;
    private int maxBounties;

    public Settings(JSONObject settings) {
        minBounties = get("bountiesMin", BOUNTIES_MIN);
        maxBounties = get("bountiesMax", BOUNTIES_MAX);
    }

    public int getMax() {
        return maxBounties;
    }

    public int getMin() {
        return minBounties;
    }

    protected boolean get(String key, boolean defaultValue) {
        try {
            return settings.getBoolean(key);
        } catch (JSONException exception) {
            return defaultValue;
        }
    }

    protected double get(String key, double defaultValue) {
        try {
            return settings.getDouble(key);
        } catch (JSONException exception) {
            return defaultValue;
        }
    }

    protected int get(String key, int defaultValue) {
        try {
            return settings.getInt(key);
        } catch (JSONException exception) {
            return defaultValue;
        }
    }
}
