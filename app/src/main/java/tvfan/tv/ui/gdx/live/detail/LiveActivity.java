package tvfan.tv.ui.gdx.live.detail;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import tvfan.tv.R;

/**
 *  desc  直播窗口activity
 *  @author  yangjh
 *  created at  16-4-22 下午2:40
 */
public class LiveActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_live_dialog);
    }
}
