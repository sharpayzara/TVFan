package tvfan.tv.ui.andr.widgets;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import tvfan.tv.R;
import tvfan.tv.ui.andr.play.baseplay.ui.BasePlayUI;
import tvfan.tv.ui.andr.play.baseplay.ui.BasePlayerActivity;
import tvfan.tv.ui.andr.play.play.Page;

/**
 * Created by Administrator on 2016/4/25.
 */
public class PlayererrorDialog extends Dialog {
    private Context mContext;

    TextView tv_title,tv_content;
    Button positivebutton;
    String title,content;
    public PlayererrorDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
        setContentView(R.layout.playdialog);
        setCanceledOnTouchOutside(false);
        playerConfirm(title,content);
        setButtonClick();
    }
    /**
     * 这是设置dialog显示内容的方法
     * @param title
     * @param content
     */
    public void playerConfirm(String title,String content){
        tv_title = (TextView) findViewById(R.id.title);
        tv_content = (TextView) findViewById(R.id.textView1);
        tv_title.setText(title);
        tv_content.setText(content);
    }

    /**
     * 这是设置dialog按钮点击事件的方法
     */
    private void setButtonClick(){
        positivebutton = (Button) findViewById(R.id.positivebutton);
        positivebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //说明是点播界面
                if (BasePlayerActivity.playType == 2) {
                    try {
                        ((BasePlayUI)mContext).removehandler();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ((Page)mContext).clear();
                    ((Activity) mContext).finish();
                }
                dismiss();
            }
        });
    }
}
