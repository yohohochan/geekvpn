
package yeungeek.tk.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import xink.vpn.Utils;
import xink.vpn.VpnActor;
import xink.vpn.wrapper.VpnProfile;
import xink.vpn.wrapper.VpnState;
import yeungeek.tk.R;

import java.util.ArrayList;

/**
 * @ClassName: VpnListAdapter
 * @Description: TODO
 * @author Anson.Yang
 * @date 2012-11-21 下午08:15:33
 */
public class VpnListAdapter extends BaseAdapter {
    private ArrayList<VpnViewItem> items;
    private final Context ctx;
    private final VpnActor actor;

    public VpnListAdapter(Context ctx, VpnActor actor) {
        this.ctx = ctx;
        this.actor = actor;
        this.items = new ArrayList<VpnViewItem>();
    }

    @Override
    public int getCount() {
        return items.size() > 0 ? items.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final VpnViewItem item = items.get(position);
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(ctx).inflate(
                    R.layout.activity_vpn_profile, null);
            holder = new ViewHolder();
            holder.vpnActiveState = (ImageView) convertView
                    .findViewById(R.id.vpn_active_state);
            holder.vpnName = (TextView) convertView.findViewById(R.id.vpn_name);
            holder.vpnDesc = (TextView) convertView.findViewById(R.id.vpn_desc);
            holder.vpnConnectState = (TextView) convertView.findViewById(R.id.vpn_connect_state);
            holder.vpnTgbtn = (ToggleButton) convertView
                    .findViewById(R.id.vpn_tgbtn);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.vpnName.setText(item.getProfile().getName());

        VpnState state = item.getProfile().getState();

        holder.vpnTgbtn.setOnCheckedChangeListener(null);
        holder.vpnTgbtn.setChecked(state == VpnState.CONNECTED);
        holder.vpnTgbtn.setEnabled(Utils.isInStableState(item.getProfile()));
        holder.vpnTgbtn
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                            boolean isChecked) {
                        toggleState(isChecked, item.getProfile());
                    }
                });

        String txt = getStateText(state);

        holder.vpnConnectState.setVisibility(TextUtils.isEmpty(txt) ? View.GONE : View.VISIBLE);
        holder.vpnConnectState.setText(txt);

        return convertView;
    }

    private String getStateText(final VpnState state) {
        String txt = ""; //$NON-NLS-1$
        switch (state) {
            case CONNECTING:
                txt = ctx.getString(R.string.vpn_connecting);
                break;
            case DISCONNECTING:
                txt = ctx.getString(R.string.vpn_disconnecting);
                break;
        }

        return txt;
    }

    /**
     * @param items the items to set
     */
    public void setItems(ArrayList<VpnViewItem> items) {
        this.items = items;
    }

    public final class ViewHolder {
        ImageView vpnActiveState;
        TextView vpnName;
        TextView vpnDesc;
        TextView vpnConnectState;
        ToggleButton vpnTgbtn;
    }

    private void toggleState(final boolean isChecked, final VpnProfile profile) {
        if (isChecked) {
            connect(profile);
        } else {
            disconnect();
        }
    }

    private void connect(final VpnProfile p) {
        actor.connect(p);
    }

    private void disconnect() {
        actor.disconnect();
    }
}
