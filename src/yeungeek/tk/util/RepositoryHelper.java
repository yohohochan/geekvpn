
package yeungeek.tk.util;

import android.content.Context;

import xink.vpn.VpnProfileRepository;
import xink.vpn.wrapper.VpnProfile;
import xink.vpn.wrapper.VpnType;
import yeungeek.tk.editor.GeekL2tpIpsecPskProfileEditor;
import yeungeek.tk.editor.GeekPptpVpnProfileEditor;

import java.util.List;

/**
 * @author Anson.Yang
 */
public class RepositoryHelper {
    private final Context context;
    private final VpnProfileRepository repository;
    private String username;
    private String password;

    public RepositoryHelper(final Context ctx) {
        this.context = ctx;
        repository = VpnProfileRepository.getInstance(ctx);
    }

    public void populatePptpRepository(final String username, final String password,
            final String[] vpnNames,
            final String[] vpnips) {
        GeekPptpVpnProfileEditor pptp = null;
        GeekL2tpIpsecPskProfileEditor l2tp = null;
        for (int i = 0; i < vpnNames.length; i++) {
            pptp = new GeekPptpVpnProfileEditor(context);
            pptp.setName(vpnNames[i]);
            pptp.setServerName(vpnips[i]);
            pptp.setUsername(username);
            pptp.setPassword(password);
            if (!containsRepository(pptp.getName(), VpnType.PPTP.getName())) {
                pptp.onSave();
            }
            l2tp = new GeekL2tpIpsecPskProfileEditor(context, vpnNames[i] + "1", vpnips[i],
                    username,
                    password);

            if (!containsRepository(pptp.getName(), VpnType.L2TP_IPSEC_PSK.getName())) {
                l2tp.onSave();
            }
        }
    }

    public void clearRepository() {
        List<VpnProfile> profileList = repository.getAllVpnProfiles();

        if (!profileList.isEmpty()) {
            VpnProfile[] ps = profileList.toArray(new VpnProfile[0]);
            for (VpnProfile p : ps) {
                repository.deleteVpnProfile(p);
            }
        }
    }

    public boolean containsRepository(final String name, final String vpnType) {
        List<VpnProfile> profileList = repository.getAllVpnProfiles();
        if (!profileList.isEmpty()) {
            VpnProfile[] ps = profileList.toArray(new VpnProfile[0]);
            for (VpnProfile p : ps) {
                final boolean typeEqual = p.getType().getName().equals(vpnType);
                if (p.getName().equals(name) && typeEqual)
                    return true;
            }
        }

        return false;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(final String password) {
        this.password = password;
    }
}
