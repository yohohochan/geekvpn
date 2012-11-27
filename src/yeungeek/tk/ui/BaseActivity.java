
package yeungeek.tk.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import yeungeek.tk.R;

/**
 * @ClassName: BaseActivity
 * @Description: TODO
 * @author Anson.Yang
 * @date 2012-11-21 下午05:07:50
 */
public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkNetStatus();
    }

    protected void showToast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }

    protected void showToast(int resId) {
        showToast(getString(resId));
    }

    public boolean checkNetStatus() {
        boolean netSataus = false;
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager.getActiveNetworkInfo() != null) {
            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                netSataus = networkInfo.isAvailable();
            }
        }

        if (!netSataus) {
            Builder b = new AlertDialog.Builder(this).setTitle(
                    R.string.vpn_netstat_tip)
                    .setMessage(R.string.vpn_netstat_set);
            b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                }
            }).setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.cancel();
                }
            }).show();
        }

        return netSataus;
    }
}
