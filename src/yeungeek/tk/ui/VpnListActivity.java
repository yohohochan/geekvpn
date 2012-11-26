
package yeungeek.tk.ui;

import static yeungeek.tk.util.Constants.ACTION_VPN_CONNECTIVITY;
import static yeungeek.tk.util.Constants.BROADCAST_ERROR_CODE;
import static yeungeek.tk.util.Constants.BROADCAST_PROFILE_NAME;
import static yeungeek.tk.util.Constants.EXPIRE;
import static yeungeek.tk.util.Constants.USERNAME;
import static yeungeek.tk.util.Constants.VPN_ERROR_NO_ERROR;
import static yeungeek.tk.util.Constants.VPN_QUIT;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xink.vpn.Utils;
import xink.vpn.VpnActor;
import xink.vpn.VpnProfileRepository;
import xink.vpn.wrapper.VpnProfile;
import xink.vpn.wrapper.VpnState;
import xink.vpn.wrapper.VpnType;
import yeungeek.tk.R;
import yeungeek.tk.ui.adapter.VpnListAdapter;
import yeungeek.tk.ui.adapter.VpnViewItem;
import yeungeek.tk.util.PreferencesTools;
import yeungeek.tk.util.RepositoryHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: VpnListActivity
 * @Description: TODO
 * @author Anson.Yang
 * @date 2012-11-21 下午05:21:45
 */
public class VpnListActivity extends BaseActivity {
    private static final Logger logger = LoggerFactory.getLogger(VpnListActivity.class);

    private VpnProfileRepository repository;
    private VpnActor actor;
    private BroadcastReceiver stateBroadcastReceiver;
    private ListView mVpnList;

    private ArrayList<VpnViewItem> mVpnItems;
    private VpnListAdapter mVpnListAdapter;

    private Button mQuitBtn;
    private Button mHideBtn;

    // private RadioGroup mVpnype;
    // private RadioButton mPptp;
    // private RadioButton mL2tp;

    private TextView mUsername;
    private TextView mExpire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vpn_list);

        repository = VpnProfileRepository.getInstance(getApplicationContext());
        actor = new VpnActor(getApplicationContext());

        mVpnItems = new ArrayList<VpnViewItem>();
        mVpnList = (ListView) findViewById(R.id.vpn_list);

        mQuitBtn = (Button) findViewById(R.id.vpn_list_quit);
        mQuitBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(VPN_QUIT);
            }
        });

        mHideBtn = (Button) findViewById(R.id.vpn_list_hide);
        mHideBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideActivity();
            }
        });

        // mVpnype = (RadioGroup) findViewById(R.id.vpn_type);
        // mPptp = (RadioButton) findViewById(R.id.vpn_type_pptp);
        // mL2tp = (RadioButton) findViewById(R.id.vpn_type_l2tp);
        // mVpnype.setOnCheckedChangeListener(new OnCheckedChangeListener() {
        // @Override
        // public void onCheckedChanged(RadioGroup group, int checkedId) {
        // if (checkedId == mPptp.getId()) {
        // actor.disconnect();
        // buildVpnListView(VpnType.PPTP.getName());
        // } else if (checkedId == mL2tp.getId()) {
        // actor.disconnect();
        // buildVpnListView(VpnType.L2TP_IPSEC_PSK.getName());
        // }
        // }
        // });

        mUsername = (TextView) findViewById(R.id.vpn_username);
        mExpire = (TextView) findViewById(R.id.vpn_expire_time);

        mUsername.setText(getString(R.string.vpn_username).concat(
                PreferencesTools.getDate(USERNAME, "")));
        mExpire.setText(getString(R.string.vpn_expire).concat(PreferencesTools.getDate(EXPIRE, ""))
                .concat(getString(R.string.vpn_expire_unit)));
        // 默认加载pptp
        buildVpnListView(VpnType.PPTP.getName());

        registerReceivers();

        checkAllVpnStatus();
    }

    private void buildVpnListView(String vpnType) {
        loadContent(vpnType);
        mVpnListAdapter = new VpnListAdapter(this, actor);
        mVpnListAdapter.setItems(mVpnItems);
        mVpnList.setAdapter(mVpnListAdapter);
    }

    private void loadContent(String vpnType) {
        mVpnItems.clear();
        String activeProfileId = repository.getActiveProfileId();
        List<VpnProfile> allVpnProfiles = repository.getAllVpnProfiles();

        for (VpnProfile vpnProfile : allVpnProfiles) {
            if (vpnProfile.getType().getName().equals(vpnType)) {
                addToVpnListView(activeProfileId, vpnProfile);
            }
        }
    }

    private void addToVpnListView(String activeProfileId, VpnProfile vpnProfile) {
        if (vpnProfile == null)
            return;
        VpnViewItem item = makeVpnViewItem(activeProfileId, vpnProfile);
        mVpnItems.add(item);
    }

    private VpnViewItem makeVpnViewItem(final String activeProfileId,
            final VpnProfile vpnProfile) {
        VpnViewItem item = new VpnViewItem();
        item.setProfile(vpnProfile);

        if (vpnProfile.getId().equals(activeProfileId)) {
            item.setActive(true);
            // activeVpnItem = item;
        }
        return item;
    }

    private void registerReceivers() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_VPN_CONNECTIVITY);
        stateBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                String action = intent.getAction();

                if (ACTION_VPN_CONNECTIVITY.equals(action)) {
                    onStateChanged(intent);
                } else {
                    logger.debug("VPNSettings receiver ignores intent:{}", intent); //$NON-NLS-1$
                }
            }
        };
        registerReceiver(stateBroadcastReceiver, filter);
    }

    private void onStateChanged(final Intent intent) {
        //Log.d(TAG, "onStateChanged: " + intent); //$NON-NLS-1$

        final String profileName = intent
                .getStringExtra(BROADCAST_PROFILE_NAME);
        final VpnState state = Utils.extractVpnState(intent);
        final int err = intent.getIntExtra(BROADCAST_ERROR_CODE,
                VPN_ERROR_NO_ERROR);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stateChanged(profileName, state, err);
            }
        });
    }

    private void stateChanged(final String profileName, final VpnState state,
            final int errCode) {
        //Log.d(TAG, "stateChanged, '" + profileName + "', state: " + state + ", errCode=" + errCode); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        VpnProfile p = repository.getProfileByName(profileName);

        if (p == null) {
            logger.warn("{} NOT found", profileName); //$NON-NLS-1$
            return;
        }

        p.setState(state);
        refreshVpnListView();
    }

    private void refreshVpnListView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mVpnListAdapter.notifyDataSetChanged();
            }
        });
    }

    private void checkAllVpnStatus() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                actor.checkAllStatus();
            }
        }, "vpn-state-checker").start(); //$NON-NLS-1$
    }

    @Override
    protected void onDestroy() {
        unregisterReceivers();
        super.onDestroy();
    }

    private void unregisterReceivers() {
        if (stateBroadcastReceiver != null) {
            unregisterReceiver(stateBroadcastReceiver);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(R.string.tip);
        dialogBuilder.setIcon(android.R.drawable.ic_dialog_info);
        switch (id) {
            case VPN_QUIT:
                dialogBuilder.setMessage(R.string.quit_info);
                dialogBuilder.setPositiveButton(R.string.ok,
                        new Dialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                actor.disconnect();
                                PreferencesTools.clear();
                                new RepositoryHelper(getApplicationContext()).clearRepository();
                                finish();
                            }
                        }).setNegativeButton(R.string.cancel, new Dialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                break;
        }
        dialog = dialogBuilder.create();
        dialog.setCancelable(true);
        return dialog;
    }

    @Override
    public void onBackPressed() {
        hideActivity();
    }

    private void hideActivity() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}
