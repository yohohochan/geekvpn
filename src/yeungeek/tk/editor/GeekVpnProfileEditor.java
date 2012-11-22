
package yeungeek.tk.editor;

import android.content.Context;

import xink.vpn.VpnProfileRepository;
import xink.vpn.wrapper.InvalidProfileException;
import xink.vpn.wrapper.VpnProfile;
import xink.vpn.wrapper.VpnState;

/**
 * @author Anson.Yang
 */
public abstract class GeekVpnProfileEditor {
    private VpnProfile profile;
    private final VpnProfileRepository repository;
    protected Context mContext;

    public GeekVpnProfileEditor(final Context mContext) {
        this.mContext = mContext;
        repository = VpnProfileRepository.getInstance(mContext);
    }

    public void onSave() {
        try {
            profile = createProfile();
            populateProfile();
            saveProfile();
        } catch (InvalidProfileException e) {
            throw e;
        }
    }

    private void populateProfile() {
        profile.setState(VpnState.IDLE);
        doPopulateProfile();
        repository.checkProfile(profile);
    }

    private void saveProfile() {
        repository.addVpnProfile(profile);
    }

    @SuppressWarnings("unchecked")
    protected <T extends VpnProfile> T getProfile() {
        return (T) profile;
    }

    protected abstract VpnProfile createProfile();

    protected abstract void doPopulateProfile();
}
