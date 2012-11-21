
package yeungeek.tk.ui;

import android.app.Activity;
import android.widget.Toast;

/**
 * @ClassName: BaseActivity
 * @Description: TODO
 * @author Anson.Yang
 * @date 2012-11-21 下午05:07:50
 */
public class BaseActivity extends Activity {
    protected void showToast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }

    protected void showToast(int resId) {
        showToast(getString(resId));
    }
}
