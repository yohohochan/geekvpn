
package yeungeek.tk.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import yeungeek.tk.VpnApp;

/**
 * @ClassName: PreferencesTools
 * @Description: TODO
 * @author Anson.Yang
 * @date 2012-11-22 下午04:18:45
 */
public class PreferencesTools {
    private static final String TAG = PreferencesTools.class.getSimpleName();
    private final static String GEEKVPN_PREFERENCES = "geekvpn_preferences";

    private static SharedPreferences getSharedPreferences() {
        return VpnApp.getInstance().getSharedPreferences(GEEKVPN_PREFERENCES, Context.MODE_PRIVATE);
    }

    public static boolean saveData(String key, Object objValue) {
        SharedPreferences sp = getSharedPreferences();
        Editor editor = sp.edit();
        editor.putString(key, objValue.toString());
        return editor.commit();
    }

    public static String getDate(String key, String defValue) {
        Log.d(TAG, "get data by key:" + key);
        SharedPreferences sp = getSharedPreferences();
        return sp.getString(key, defValue);
    }

    public static void clear() {
        SharedPreferences sp = getSharedPreferences();
        if (sp != null) {
            Editor editor = sp.edit();
            editor.clear();
            editor.commit();
        }
    }
}
