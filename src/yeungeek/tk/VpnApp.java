
package yeungeek.tk;

import android.app.Application;
import android.os.Environment;

import junit.framework.Assert;

import yeungeek.tk.util.DomHandler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

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
        initVpnList();
    }

    public static VpnApp getInstance() {
        if (instance == null) {
            Assert.assertTrue("application is null", instance != null);
        }
        return instance;
    }

    public void initVpnList() {
        try {
            DomHandler.readXml(new FileInputStream(Environment.getExternalStorageDirectory()
                    .getName().concat("/arrays.xml")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
