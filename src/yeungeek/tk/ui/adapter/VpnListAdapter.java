
package yeungeek.tk.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * @ClassName: VpnListAdapter
 * @Description: TODO
 * @author Anson.Yang
 * @date 2012-11-21 下午08:15:33
 */
public class VpnListAdapter extends BaseAdapter {
    private ArrayList<VpnViewItem> items;

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
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        return null;
    }

    /**
     * @param items the items to set
     */
    public void setItems(ArrayList<VpnViewItem> items) {
        this.items = items;
    }

}
