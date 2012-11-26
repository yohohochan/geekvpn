
package yeungeek.tk;

import android.app.Application;

import junit.framework.Assert;

/**
 * @ClassName: VpnApp
 * @Description: TODO
 * @author Anson.Yang
 * @date 2012-11-22 下午04:26:34
 */
public class VpnApp extends Application {
    private static VpnApp instance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static VpnApp getInstance() {
        if (instance == null) {
            Assert.assertTrue("application is null", instance != null);
        }
        return instance;
    }
}
