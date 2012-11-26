
package yeungeek.tk.util;

/**
 * @ClassName: Constants
 * @Description: TODO
 * @author Anson.Yang
 * @date 2012-11-21 下午05:19:13
 */
public interface Constants {
    public final static String VPNURL = "http://fengchinet.com/celogin.php";
    public final static String USERNAME = "username";
    public final static String PASSWORD = "password";
    public final static String EXPIRE = "expire";
    public final static String SEED = "geekvpn";

    public final static String IS_LOGINED = "is_logined";

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

    public static final int VPN_QUIT = 100;

    /** L2TP连接密钥 **/
    public static final String L2TP_SECRET = "fengchinet.com";
}
