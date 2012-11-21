
package yeungeek.tk.util;

/**
 * @ClassName: Constants
 * @Description: TODO
 * @author Anson.Yang
 * @date 2012-11-21 下午05:19:13
 */
public interface Constants {
    public static String VPNURL = "http://fengchinet.com/celogin.php";
    public static String USERNAME = "username";
    public static String PASSWORD = "password";

    // Action for broadcasting a connectivity state.
    public static final String ACTION_VPN_CONNECTIVITY = "vpn.connectivity";
    /** Key to the profile name of a connectivity broadcast event. */
    public static final String BROADCAST_PROFILE_NAME = "profile_name";
    /** Key to the connectivity state of a connectivity broadcast event. */
    public static final String BROADCAST_CONNECTION_STATE = "connection_state";
    /** Key to the error code of a connectivity broadcast event. */
    public static final String BROADCAST_ERROR_CODE = "err";

    /** Error code to indicate a successful connection. */
    public static final int VPN_ERROR_NO_ERROR = 0;
}
