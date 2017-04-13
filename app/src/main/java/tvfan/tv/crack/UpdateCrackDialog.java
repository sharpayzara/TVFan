package tvfan.tv.crack;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import tvfan.tv.R;
import tvfan.tv.ui.andr.widgets.UpdateDialog.UpdateDialogListener;

/**
 * 更新破解jar包和so文件时使用
 * 
 * @author zhangyisu
 *
 */
public class UpdateCrackDialog extends Dialog {

	private Context mctx;

	public UpdateCrackDialog(Context mctx, int theme) {
		super(mctx, theme);
		this.mctx = mctx;
		setContentView(R.layout.update_crack_dialog);
		TextView title = (TextView) findViewById(R.id.title);
		title.setText("正在更新播放组件\n请稍候……");

		Window win = getWindow();
		WindowManager.LayoutParams params = win.getAttributes();
		params.y -= 50; //向上移动一些，遮挡加载圈
		win.setAttributes(params);
	}

	public void UpdateConfirm(final UpdateDialogListener updateDialogListener) {

//		CustomDialog.Builder customBuilder = new CustomDialog.Builder(mctx);
//
//		customBuilder.setTitle("正在更新播放组件，请稍候……");
	}

}
