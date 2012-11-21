
package yeungeek.tk.ui.adapter;

import xink.vpn.wrapper.VpnProfile;

/**
 * @ClassName: VpnViewItem
 * @Description: TODO
 * @author Anson.Yang
 * @date 2012-11-21 下午08:24:22
 */
public class VpnViewItem {
    private VpnProfile profile;
    private boolean isActive;

    /**
     * @return the profile
     */
    public VpnProfile getProfile() {
        return profile;
    }

    /**
     * @param profile the profile to set
     */
    public void setProfile(VpnProfile profile) {
        this.profile = profile;
    }

    /**
     * @return the isActive
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * @param isActive the isActive to set
     */
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
}
