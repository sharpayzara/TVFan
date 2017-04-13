package tvfan.tv.ui.andr.play.baseplay.widgets;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

import tvfan.tv.App;
import tvfan.tv.R;
import tvfan.tv.dal.HttpResponse;
import tvfan.tv.dal.RemoteData;

/**
 * 点播播放器菜单
 * Created by ddd on 2016/4/19.
 */
public class PlayMenuDialogView extends Dialog implements View.OnFocusChangeListener {

    private Context mcontext;

    private RelativeLayout favLayout;//收藏
    private RelativeLayout dinLayout;//清晰度
    private RelativeLayout ratioLayout;//画面比例
    private ImageView favIV;//收藏
    private ImageView dinIV;//清晰度
    private ImageView ratioIV;//画面比例
    private TextView favTV;//收藏
    private TextView dinTV;//清晰度
    private TextView ratioTV;//画面比例

    private ArrayList<String> difinitionlist;//清晰度列表
    private ArrayList<String> screenlist;//画面比例列表
    private String programSeriesId;//
    private int dinNum;
    private int ratioNum;//0为全屏，1位默认大小
    private boolean isfavorate;
    private boolean isDinSelected;
    private boolean isFavSelected;
    private boolean isRatioSelected;
    public static boolean FILTER_STATE= false;
    private Handler timerhd;

    public PlayMenuDialogView(Context context, int theme) {
        super(context, theme);

        this.mcontext = context;
        timerhd = new Handler();
        initData();
        initView();
    }

    private void initData() {

    }

    /**
     * 初始化View
     */
    private void initView() {
        setContentView(R.layout.dialog_play);
        favLayout = (RelativeLayout) findViewById(R.id.rl_fav);
        dinLayout = (RelativeLayout) findViewById(R.id.rl_din);
        ratioLayout = (RelativeLayout) findViewById(R.id.rl_radio);

        favIV = (ImageView) findViewById(R.id.img_fav);
        dinIV = (ImageView) findViewById(R.id.img_din);
        ratioIV = (ImageView) findViewById(R.id.img_radio);

        favTV = (TextView) findViewById(R.id.tv_collect);
        dinTV = (TextView) findViewById(R.id.tv_btn_din);
        ratioTV = (TextView) findViewById(R.id.tv_ratio);

        ratioTV.setText("全     屏");
        ratioIV.setBackgroundResource(R.drawable.play_setting_scale);

        favLayout.setOnFocusChangeListener(this);
        dinLayout.setOnFocusChangeListener(this);
        ratioLayout.setOnFocusChangeListener(this);
    }

    /**
     * 获取当前节目是否收藏
     */
    private void getFavorite() {
        RemoteData rd = new RemoteData(mcontext);
        rd.isFavorite(programSeriesId, new HttpResponse.Listener4JSONObject() {

            @Override
            public void onResponse(JSONObject response) {
                if (response == null) {
                    return;
                }
                try {
                    isfavorate = response.optBoolean("msg");
                    fav(isfavorate);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }

    /**
     * 添加收藏
     */
    private void addFavorite() {
        RemoteData rd = new RemoteData(mcontext);
        rd.addFavorite(programSeriesId, new HttpResponse.Listener4JSONObject() {

            @Override
            public void onResponse(JSONObject response) {
                if (response == null) {
                    return;
                }
                try {
                    boolean msg = response.optBoolean("msg");
                    if (msg) {
                        isfavorate = true;
                        fav(isfavorate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String errorMessage) {
            }
        });
    }

    /**
     * 取消收藏
     */
    private void cancelFavorite() {
        RemoteData rd = new RemoteData(mcontext);
        rd.cancelFavorite(programSeriesId, new HttpResponse.Listener4JSONObject() {

            @Override
            public void onResponse(JSONObject response) {
                if (response == null) {
                    return;
                }
                try {
                    boolean msg = response.optBoolean("msg");
                    if (msg) {
                        isfavorate = false;
                        fav(isfavorate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }

    private void fav(boolean isfav) {
        if (isfav) {
            favIV.setBackgroundResource(R.drawable.play_setting_collect_selected);
            favTV.setText("已收藏");
        } else {
            favIV.setBackgroundResource(R.drawable.play_setting_collect);
            favTV.setText("收     藏");
        }
    }

    public void setProgramSeriesId(String programSeriesId) {
        this.programSeriesId = programSeriesId;
        getFavorite();
    }

    public void addDistinList(ArrayList<String> definitionlist, int dinNum,  View.OnClickListener onitemclicklistener) {
        difinitionlist = definitionlist;
        this.dinNum = dinNum;
        this.dinNum = dinNum;
//        dinLayout.setOnClickListener(onitemclicklistener);
        if(dinNum >= difinitionlist.size())
            dinNum = difinitionlist.size()-1;
        dinTV.setText(difinitionlist.get(dinNum));
        setDinDrawable();
    }

    public void addRatioList(ArrayList<String> screenlist, View.OnClickListener onitemclicklistener) {
        this.screenlist = screenlist;
//        ratioLayout.setOnClickListener(onitemclicklistener);
    }

    public void showDialog(){
//        favLayout.requestFocus();
        show();
        dinShow();
        ratioShow();
//        hideTimerAction();
    }

    private void ratioShow() {
        if(ratioNum == 0) {
            ratioTV.setText("全     屏");
            ratioIV.setImageResource(R.drawable.play_setting_scale_selected);
        }
        else {
            ratioTV.setText("原     始");
            ratioIV.setImageResource(R.drawable.play_setting_scale);
        }
    }

    private void dinShow() {
        String din = "";
        if ("标     清".equals(din)) {
            dinTV.setText("标     清");
            dinIV.setImageResource(R.mipmap.standard_definition);
        }
        else if ("高     清".equals(din)) {
            dinTV.setText("高     清");
            dinIV.setImageResource(R.mipmap.high_definition);
        }
        else if ("超     清".equals(din)) {
            dinTV.setText("超     清");
            dinIV.setImageResource(R.mipmap.super_definition);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case 22://右
                if(isDinSelected) {
                    dinNum++;
                    if(dinNum >= difinitionlist.size())
                        dinNum = 0;
                    dinTV.setText(difinitionlist.get(dinNum));
                    setDinDrawable();
                    if(onDinChangeListener != null)
                        onDinChangeListener.onDinChange(dinNum);
                }
                if(isFavSelected) {
                    if (!isfavorate)
                        addFavorite();
                    else
                        cancelFavorite();
                }
                if (isRatioSelected) {
                    if(ratioNum == 1) {
                        ratioNum = 0;
                        ratioTV.setText("全     屏");
                        ratioIV.setImageResource(R.drawable.play_setting_scale_selected);
                    }
                    else {
                        ratioNum = 1;
                        ratioTV.setText("原     始");
                        ratioIV.setImageResource(R.drawable.play_setting_scale);
                    }
                    if (onRatioChangeListener != null)
                        onRatioChangeListener.onRatioChange(ratioNum);
                }
//                hideTimerAction();
                break;
           /* case 23://确定
                hideTimerAction();
                break;*/
            case 21://左
                if(isDinSelected) {
                    dinNum--;
                    if(dinNum < 0)
                        dinNum = difinitionlist.size()-1;
                    dinTV.setText(difinitionlist.get(dinNum));
                    setDinDrawable();
                    if(onDinChangeListener != null)
                        onDinChangeListener.onDinChange(dinNum);
                }
                if(isFavSelected) {
                    if (!isfavorate)
                        addFavorite();
                    else
                        cancelFavorite();
                }
                if (isRatioSelected) {
                    if(ratioNum == 1) {
                        ratioNum = 0;
                        ratioTV.setText("全     屏");
                        ratioIV.setImageResource(R.drawable.play_setting_scale_selected);
                    }
                    else {
                        ratioNum = 1;
                        ratioTV.setText("原     始");
                        ratioIV.setImageResource(R.drawable.play_setting_scale);
                    }
                    if (onRatioChangeListener != null)
                        onRatioChangeListener.onRatioChange(ratioNum);
                }
//                hideTimerAction();
                break;
            case KeyEvent.KEYCODE_BACK:
                dismiss();
                break;
            default:
//                hideTimerAction();
                break;
        }

        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus) {
            if(v == favLayout) {
                    isFavSelected = true;
            }
            else if(v == dinLayout) {
                isDinSelected = true;
            }
            else if(v == ratioLayout) {
                isRatioSelected = true;
            }
        }
        else {
            if(v == favLayout) {
                isFavSelected = false;
            }
            else if(v == dinLayout) {
                isDinSelected = false;
            }
            else if(v == ratioLayout) {
                isRatioSelected = false;
            }
        }
    }

    /*Runnable timerrb = new Runnable() {
        @Override
        public void run() {
           //PlayMenuDialogView.this.dismiss();
        }
    };

    public void hideTimerAction() {
        if (timerhd != null) {
            timerhd.removeCallbacks(timerrb);
            timerhd.postDelayed(timerrb, 5000);
        }
    }
*/
    private void setDinDrawable(){
        String din = dinTV.getText().toString();
        if ("标     清".equals(din)) {
            dinIV.setImageResource(R.mipmap.standard_definition);
        }
        else if ("高     清".equals(din)) {
            dinIV.setImageResource(R.mipmap.high_definition);
        }
        else if ("超     清".equals(din)) {
            dinIV.setImageResource(R.mipmap.super_definition);
        }
    }

    public interface OnDinChangeListener {
        public void onDinChange (int dinNum);
    }
    private OnDinChangeListener onDinChangeListener;
    public void setOnDinChangeListener (OnDinChangeListener onDinChangeListener) {
        this.onDinChangeListener = onDinChangeListener;
    }

    public interface OnRatioChangeListener {
        public void onRatioChange (int ratioNum);
    }
    private OnRatioChangeListener onRatioChangeListener;
    public void setOnRatioChangeListener (OnRatioChangeListener onRatioChangeListener) {
        this.onRatioChangeListener = onRatioChangeListener;
    }
}
