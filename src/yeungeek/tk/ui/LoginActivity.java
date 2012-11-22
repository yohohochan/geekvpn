
package yeungeek.tk.ui;

import static yeungeek.tk.util.Constants.IS_LOGINED;
import static yeungeek.tk.util.Constants.PASSWORD;
import static yeungeek.tk.util.Constants.SEED;
import static yeungeek.tk.util.Constants.USERNAME;
import static yeungeek.tk.util.Constants.VPNURL;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import yeungeek.tk.R;
import yeungeek.tk.util.Crypto;
import yeungeek.tk.util.PreferencesTools;

/**
 * @ClassName: LoginActivity
 * @Description: TODO
 * @author Anson.Yang
 * @date 2012-11-21 下午04:43:30
 */
public class LoginActivity extends BaseActivity {
    private final static String TAG = LoginActivity.class.getSimpleName();

    private Button mLoginBtn;
    private String mUsername;
    private String mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        String isLogined = PreferencesTools.getDate(IS_LOGINED, "false");
        if (isLogined.equals("true")) {
            startToVpn();
        }

        mLoginBtn = (Button) findViewById(R.id.longin_btn);
        mLoginBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mUsername = getEditorText(R.id.login_username);
                mPassword = getEditorText(R.id.login_password);

                mUsername = "valesail";
                mPassword = "huanglei";

                if (TextUtils.isEmpty(mUsername) || TextUtils.isEmpty(mPassword)) {
                    showToast(R.string.login_username_password_null);
                } else {
                    new LoginTask().execute(VPNURL.concat("?userid=").concat(mUsername)
                                .concat("&pwd=").concat(mPassword));
                }
            }
        });
    }

    public String getEditorText(final int id) {
        String value = null;
        EditText editor = (EditText) findViewById(id);
        if (editor != null) {
            Editable editable = editor.getText();
            if (editable != null) {
                value = editable.toString().trim();
            }
        }
        return value;
    }

    class LoginTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(final String... params) {
            Log.d(TAG, "login request: " + params[0]);
            HttpGet hg = new HttpGet(params[0]);
            try {
                HttpResponse hr = new DefaultHttpClient().execute(hg);
                if (hr.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    String result = EntityUtils.toString(hr.getEntity());
                    Log.d(TAG, "result:" + result);
                    return result;
                }
            } catch (Exception e) {
                Log.d(TAG, "login request error " + e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String result) {
            Log.d(TAG, "recevie result:" + result);
            if (!TextUtils.isEmpty(result)) {
                String[] rs = result.split(",");
                int code = Integer.parseInt(rs[0]);
                if (200 == code) {
                    PreferencesTools.saveData(USERNAME, mUsername);
                    try {
                        PreferencesTools.saveData(PASSWORD, Crypto.encrypt(SEED, mPassword));
                    } catch (Exception e) {
                        Log.e(TAG, "save password error" + e);
                        e.printStackTrace();
                    }
                    PreferencesTools.saveData(IS_LOGINED, "true");

                    startToVpn();

                } else {
                    showToast(R.string.login_failed);
                }
            } else {
                showToast(R.string.login_failed);
            }
        }
    }

    public void startToVpn() {
        Intent intent = new Intent(LoginActivity.this, VpnListActivity.class);
        startActivity(intent);
        finish();
    }
}
