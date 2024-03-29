
package yeungeek.tk.ui;

import static yeungeek.tk.util.Constants.EXPIRE;
import static yeungeek.tk.util.Constants.IS_LOGINED;
import static yeungeek.tk.util.Constants.PASSWORD;
import static yeungeek.tk.util.Constants.SEED;
import static yeungeek.tk.util.Constants.USERNAME;
import static yeungeek.tk.util.Constants.VPNURL;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xink.vpn.VpnProfileRepository;
import yeungeek.tk.R;
import yeungeek.tk.util.Crypto;
import yeungeek.tk.util.PreferencesTools;
import yeungeek.tk.util.RepositoryHelper;

/**
 * @ClassName: LoginActivity
 * @Description: TODO
 * @author Anson.Yang
 * @date 2012-11-21 下午04:43:30
 */
public class LoginActivity extends BaseActivity {
    private static final Logger logger = LoggerFactory.getLogger(LoginActivity.class);

    private final static int IS_LOGING = 0x1000;
    private Button mLoginBtn;
    private String mUsername;
    private String mPassword;

    private VpnProfileRepository repository;
    private RepositoryHelper reposityoryHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        repository = VpnProfileRepository.getInstance(getApplicationContext());

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

                if (TextUtils.isEmpty(mUsername) || TextUtils.isEmpty(mPassword)) {
                    showToast(R.string.login_username_password_null);
                } else if (!checkNetStatus()) {
                    showToast(R.string.vpn_netstat_tip);
                } else {
                    // show dialog
                    showDialog(IS_LOGING);
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
            logger.debug("login request: {}", params[0]);
            HttpGet hg = new HttpGet(params[0]);
            try {
                HttpResponse hr = new DefaultHttpClient().execute(hg);
                if (hr.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    String result = EntityUtils.toString(hr.getEntity());
                    logger.debug("result: {}", result);
                    return result;
                }
            } catch (Exception e) {
                logger.error("login request error {}", e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String result) {
            logger.debug("recevie result: {}", result);
            removeDialog(IS_LOGING);

            if (!TextUtils.isEmpty(result)) {
                String[] rs = result.split(",");
                if (rs.length >= 2) {
                    int code = Integer.parseInt(rs[0]);
                    String expire = rs[1];
                    if (200 == code) {
                        PreferencesTools.saveData(USERNAME, mUsername);
                        PreferencesTools.saveData(EXPIRE, expire);
                        try {
                            PreferencesTools.saveData(PASSWORD, Crypto.encrypt(SEED, mPassword));
                            PreferencesTools.saveData(IS_LOGINED, "true");
                            initVpn();
                            startToVpn();
                        } catch (Exception e) {
                            PreferencesTools.saveData(IS_LOGINED, "false");
                            logger.error("save password error {}", e);
                            showToast(R.string.login_failed);
                            e.printStackTrace();
                        }
                    }
                } else {
                    showToast(R.string.login_failed);
                }
            } else {
                showToast(R.string.login_failed);
            }
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case IS_LOGING:
                return ProgressDialog.show(this, getString(R.string.tip),
                        getString(R.string.is_loging), true, true);
            default:
                break;
        }
        return null;
    }

    public void startToVpn() {
        Intent intent = new Intent(LoginActivity.this, VpnListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void initVpn() {
        final String[] vpnNames = getResources().getStringArray(
                R.array.vpn_names);
        final String[] vpnIps = getResources().getStringArray(R.array.vpn_ips);
        // 加入list
        reposityoryHelper = new RepositoryHelper(getApplicationContext());
        reposityoryHelper.populatePptpRepository(mUsername, mPassword, vpnNames,
                vpnIps);

        save();
    }

    private void save() {
        repository.save();
    }
}
