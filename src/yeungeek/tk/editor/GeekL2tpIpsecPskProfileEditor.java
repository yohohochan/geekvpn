
package yeungeek.tk.editor;

import static yeungeek.tk.util.Constants.L2TP_SECRET;

import android.content.Context;

import xink.vpn.wrapper.L2tpIpsecPskProfile;
import xink.vpn.wrapper.VpnProfile;

/**
 * @ClassName: GeekL2tpProfileEditor
 * @Description: TODO
 * @author Anson.Yang
 * @date 2012-11-22 下午07:49:43
 */
public class GeekL2tpIpsecPskProfileEditor extends GeekVpnProfileEditor {
    private String name;
    private String serverName;
    private String username;
    private String password;

    public GeekL2tpIpsecPskProfileEditor(Context mContext) {
        super(mContext);
    }

    @Override
    protected VpnProfile createProfile() {
        return new L2tpIpsecPskProfile(mContext);
    }

    public GeekL2tpIpsecPskProfileEditor(final Context mContext, final String name,
            final String serverName,
            final String username, final String password) {
        super(mContext);
        this.name = name;
        this.serverName = serverName;
        this.username = username;
        this.password = password;
    }

    @Override
    protected void doPopulateProfile() {
        L2tpIpsecPskProfile profile = getProfile();
        profile.setName(name);
        profile.setServerName(serverName);
        // 可以不设置
        profile.setDomainSuffices("8.8.8.8");
        profile.setUsername(username);
        profile.setPassword(password);

        profile.setPresharedKey(L2TP_SECRET);

        // profile.setSecretEnabled(false);
        // profile.setSecretString("");
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the serverName
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * @param serverName the serverName to set
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
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
    public void setUsername(String username) {
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
    public void setPassword(String password) {
        this.password = password;
    }
}
