package yeungeek.tk.ui;

import static yeungeek.tk.util.Constants.ACTION_VPN_CONNECTIVITY;
import static yeungeek.tk.util.Constants.BROADCAST_ERROR_CODE;
import static yeungeek.tk.util.Constants.BROADCAST_PROFILE_NAME;
import static yeungeek.tk.util.Constants.PASSWORD;
import static yeungeek.tk.util.Constants.USERNAME;
import static yeungeek.tk.util.Constants.VPN_ERROR_NO_ERROR;

import java.util.ArrayList;
import java.util.List;

import xink.vpn.Utils;
import xink.vpn.VpnActor;
import xink.vpn.VpnProfileRepository;
import xink.vpn.wrapper.VpnProfile;
import xink.vpn.wrapper.VpnState;
import yeungeek.tk.R;
import yeungeek.tk.ui.adapter.VpnListAdapter;
import yeungeek.tk.ui.adapter.VpnViewItem;
import yeungeek.tk.util.RepositoryHelper;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * @ClassName: VpnListActivity
 * @Description: TODO
 * @author Anson.Yang
 * @date 2012-11-21 下午05:21:45
 */
public class VpnListActivity extends BaseActivity {
	private final static String TAG = VpnListActivity.class.getSimpleName();

	private VpnProfileRepository repository;
	private VpnActor actor;
	private RepositoryHelper reposityoryHelper;
	private BroadcastReceiver stateBroadcastReceiver;
	private ListView mVpnList;

	private ArrayList<VpnViewItem> mVpnItems;
	private VpnListAdapter mVpnListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vpn_list);
		Intent intent = getIntent();
		final String username = intent.getStringExtra(USERNAME);
		final String password = intent.getStringExtra(PASSWORD);

		repository = VpnProfileRepository.getInstance(getApplicationContext());
		actor = new VpnActor(getApplicationContext());

		final String[] vpnNames = getResources().getStringArray(
				R.array.vpn_names);
		final String[] vpnIps = getResources().getStringArray(R.array.vpn_ips);

		// 加入list
		reposityoryHelper = new RepositoryHelper(getApplicationContext());
		reposityoryHelper.populatePptpRepository(username, password, vpnNames,
				vpnIps);

		mVpnItems = new ArrayList<VpnViewItem>();
		mVpnList = (ListView) findViewById(R.id.vpn_list);
		save();

		buildVpnListView();

		registerReceivers();

		checkAllVpnStatus();
	}

	private void buildVpnListView() {
		loadContent();
		mVpnListAdapter = new VpnListAdapter(this,actor);
		mVpnListAdapter.setItems(mVpnItems);
		mVpnList.setAdapter(mVpnListAdapter);
	}

	private void loadContent() {
		mVpnItems.clear();
		String activeProfileId = repository.getActiveProfileId();
		List<VpnProfile> allVpnProfiles = repository.getAllVpnProfiles();

		for (VpnProfile vpnProfile : allVpnProfiles) {
			addToVpnListView(activeProfileId, vpnProfile);
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
					Log.d(TAG, "VPNSettings receiver ignores intent:" + intent); //$NON-NLS-1$
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
			Log.w(TAG, profileName + " NOT found"); //$NON-NLS-1$
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

	private void save() {
		repository.save();
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
}
