package tvfan.tv.ui.andr.widgets;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tvfan.tv.App;
import tvfan.tv.AppGlobalConsts;
import tvfan.tv.R;
import tvfan.tv.dal.LocalData;
import tvfan.tv.dal.PlayRecordHelpler;
import tvfan.tv.dal.models.GeneralSetBean;
import tvfan.tv.dal.models.MPlayRecordInfo;
import tvfan.tv.lib.Lg;

/**
 * 这是直播界面设置窗口
 * Created by Administrator on 2016/4/5.
 */
public class LivePlaySettingDialog extends Dialog{
    private final String TAG = "index";
    private Context mContext;
    private TextView tv_collect,tv_btn_leftright,tv_btn_topbottom,tv_ratio;
    private ImageView img_fav,img_radio,img_lr,img_tb;
    private LinearLayout rl_fav,rl_radio,rl_lr,rl_tb;
    private int  windowHeight;//屏幕高
    private List<GeneralSetBean> mFavGeneralSetList,mRadioGeneralSetList,mLRGeneralSetList,mTBGeneralSetList;
    private LocalData mLocalData;
    private String favId,favName; // 默认是否收藏
    private String radioId,radioname;// 默认换面比例
    private String lrId,lrName;//默认左右键功能
    private String tbId,tbName;//默认上下键功能
    private int favGeneralSet = 0;
    private int radioGeneralSet = 0;
    private int lrGeneralSet = 0;
    private int tbGeneralSet = 0;
    private int correntSelectedIntex = 0;//当前选中的条目id
    private MPlayRecordInfo mplayrcinfo;//记录播放状态的信息的实体类
    private String curChannelId = "";//保存和获取收藏状态的是需要用到的id
    private MPlayRecordInfo currentChannel;//当前播放频道
    private PlayRecordHelpler ph;
    public LivePlaySettingDialog(Context context, int theme,PlayRecordHelpler ph) {
        super(context, theme);
        mContext = context;
        this.ph = ph;
        initView();
        initData();
        setFlagImgShow();
    }

    private void initView() {
      setContentView(R.layout.liveplay_settings_layout);

        //设置dialog的显示位置是居中显示的
        setDialogShow();
         tv_collect = (TextView) findViewById(R.id.tv_collect);
        tv_ratio = (TextView) findViewById(R.id.tv_ratio);
        tv_btn_leftright = (TextView) findViewById(R.id.tv_btn_leftright);
        tv_btn_topbottom = (TextView) findViewById(R.id.tv_btn_topbottom);
        rl_fav = (LinearLayout) findViewById(R.id.rl_fav);
        rl_radio = (LinearLayout) findViewById(R.id.rl_radio);
        rl_lr = (LinearLayout) findViewById(R.id.rl_lr);
        rl_tb = (LinearLayout) findViewById(R.id.rl_tb);
        img_fav = (ImageView) findViewById(R.id.img_fav);
        img_radio = (ImageView) findViewById(R.id.img_radio);
        img_lr = (ImageView) findViewById(R.id.img_lr);
        img_tb = (ImageView) findViewById(R.id.img_tb);
        setItemFocus();
    }

    /**
     * 设置dialog中条目的index值
     */
    private int getCurrentItemIndex() {
        if(rl_fav.isFocused()){
            correntSelectedIntex =0;
        }else if(rl_radio.isFocused()){
            correntSelectedIntex= 1;
        }else if(rl_lr.isFocused()){
            correntSelectedIntex=2;
        }else if(rl_tb.isFocused()){
            correntSelectedIntex=3;
        }
        return correntSelectedIntex;
    }

    /**
     *  这是设置dialog中item焦点转移的方法
     */
    private void setItemFocus() {
        //收藏item
        rl_fav.setNextFocusDownId(R.id.rl_radio);
        rl_fav.setNextFocusUpId(R.id.rl_tb);
        rl_fav.setNextFocusLeftId(R.id.rl_fav);
        rl_fav.setNextFocusRightId(R.id.rl_fav);
        //画面比例item
        rl_radio.setNextFocusDownId(R.id.rl_lr);
        rl_radio.setNextFocusUpId(R.id.rl_fav);
        rl_radio.setNextFocusLeftId(R.id.rl_radio);
        rl_radio.setNextFocusRightId(R.id.rl_radio);
        //左右键功能item
        rl_lr.setNextFocusDownId(R.id.rl_tb);
        rl_lr.setNextFocusUpId(R.id.rl_radio);
        rl_lr.setNextFocusLeftId(R.id.rl_lr);
        rl_lr.setNextFocusRightId(R.id.rl_lr);
        //上下键功能item
        rl_tb.setNextFocusDownId(R.id.rl_fav);
        rl_tb.setNextFocusUpId(R.id.rl_lr);
        rl_tb.setNextFocusLeftId(R.id.rl_tb);
        rl_tb.setNextFocusRightId(R.id.rl_tb);
    }

    /**
     * 设置dialog的显示位置
     */
    private void setDialogShow() {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    private void initData() {
        //初始化收藏选择框内的数据列表
        initFavList();
        //初始化画面比例选择框内的数据列表
        initRadioChoose();
        //初始化左右按键选择框内数据列表
        initLeftRightBtn();
        //初始化上下按键选择框内数据列表
        initTopBottomBtn();
        //初始化界面显示
        initValue();
    }

    /**
     * 这是初始化界面显示的方法
     */
    private void initValue() {
        mLocalData = new LocalData(mContext);
         fav();
        /*favId = mLocalData
                .getKV(AppGlobalConsts.PERSIST_NAMES.LIVEPLAY_FAV_DEFAILT.name());*/
        radioId = mLocalData
                .getKV(AppGlobalConsts.PERSIST_NAMES.LIVEPLAY_RADIO_DEFAILT
                        .name());
        if (TextUtils.isEmpty(radioId)) {
            radioId = "0";
        }
        if(radioId.equals("0")){
            tv_ratio.setText("全  屏");
            tv_ratio.setTextSize(App.adjustFontSize(18));
            img_radio.setBackgroundResource(R.drawable.play_setting_scale);
        }else{
            tv_ratio.setText("原  始");
            tv_ratio.setTextSize(App.adjustFontSize(18));
            img_radio.setBackgroundResource(R.drawable.play_setting_scale_selected);
        }
        Log.d(TAG, "当前radioId....." + radioId);
        lrId=mLocalData
                .getKV(AppGlobalConsts.PERSIST_NAMES.LIVEPLAY_LEFTRIGHRT_DEFAILT.name());
        if (TextUtils.isEmpty(lrId)) {
            lrId = "0";
        }
        if(lrId .equals("0")){
            tv_btn_leftright.setText("换  源");
            tv_btn_leftright.setTextSize(App.adjustFontSize(18));
            img_lr.setBackgroundResource(R.drawable.play_setting_sources);
        }else{
            tv_btn_leftright.setText("音  量");
            tv_btn_leftright.setTextSize(App.adjustFontSize(18));
            img_lr.setBackgroundResource(R.drawable.play_setting_sound);
        }
        Log.d("MyTAG", "3当前的lrid值为...." + lrId);
        tbId=mLocalData
                .getKV(AppGlobalConsts.PERSIST_NAMES.LIVEPLAY_TOPBOTTOM_DEFAILT.name());
        if (TextUtils.isEmpty(tbId)) {
            tbId = "0";
        }
        if(tbId .equals("0")){
            tv_btn_topbottom.setText("正  常");
            tv_btn_topbottom.setTextSize(App.adjustFontSize(18));
            img_tb.setBackgroundResource(R.drawable.play_setting_channel);
        }else{
            tv_btn_topbottom.setText("反  转");
            tv_btn_topbottom.setTextSize(App.adjustFontSize(18));
            img_tb.setBackgroundResource(R.drawable.play_setting_channel_selected);
        }
        Log.d(TAG, "当前tbId....." + tbId);
    }

    private void setFavShow(int index) {
        if(index == 0){
            tv_collect.setText("未收藏");
            tv_collect.setTextSize(App.adjustFontSize(18));
            img_fav.setBackgroundResource(R.drawable.play_setting_collect);
        }else{
            tv_collect.setText("已收藏");
            tv_collect.setTextSize(App.adjustFontSize(18));
            img_fav.setBackgroundResource(R.drawable.play_setting_collect_selected);
        }
    }

    /**
     * 这是初始化上下按键选择框内数据列表的方法
     */
    private void initTopBottomBtn() {
        mTBGeneralSetList = new ArrayList<GeneralSetBean>();
        GeneralSetBean trueBean = new GeneralSetBean();
        trueBean.setmId("0");
        trueBean.setmName("正  常");
        mTBGeneralSetList.add(trueBean);

        GeneralSetBean falseBean = new GeneralSetBean();
        falseBean.setmId("1");
        falseBean.setmName("反  转");
        mTBGeneralSetList.add(falseBean);
    }

    /**
     * 这是初始化左右按键选择框内数据列表的方法
     */
    private void initLeftRightBtn() {
        mLRGeneralSetList = new ArrayList<GeneralSetBean>();
        GeneralSetBean srcBean = new GeneralSetBean();
        srcBean.setmId("0");
        srcBean.setmName("换  源");
        mLRGeneralSetList.add(srcBean);

        GeneralSetBean videoBean = new GeneralSetBean();
        videoBean.setmId("1");
        videoBean.setmName("音  量");
        mLRGeneralSetList.add(videoBean);
    }

    /**
     * 这是设置标识图片显示的方法
     */
    public void setFlagImgShow(){
        Log.d(TAG, "setFlagImgShow当前favGeneralSet的值为......" + favGeneralSet);

        mplayrcinfo = ph.getLivePlayRcInfo(curChannelId);
        if(mplayrcinfo != null){
            favGeneralSet = mplayrcinfo.getPlayerFav();
        }else{
            favGeneralSet = 0;
        }
         setFavShow(favGeneralSet);
        if(radioId.contains("0")){
            img_radio.setBackgroundResource(R.drawable.play_setting_scale);
        }else{
            img_radio.setBackgroundResource(R.drawable.play_setting_scale_selected);
        }
        if(lrId.contains("0")){
            img_lr.setBackgroundResource(R.drawable.play_setting_sources);
        }else{
            img_lr.setBackgroundResource(R.drawable.play_setting_sound);
        }
        if(tbId.contains("0")){
            img_tb.setBackgroundResource(R.drawable.play_setting_channel);
        }else{
            img_tb.setBackgroundResource(R.drawable.play_setting_channel_selected);
        }
    }
    /**
     * 这是初始话画面比例选择框内数据列表的方法
     */
    private void initRadioChoose() {
        mRadioGeneralSetList = new ArrayList<GeneralSetBean>();
        GeneralSetBean allBean = new GeneralSetBean();
        allBean.setmId("0");
        allBean.setmName("全  屏");
        mRadioGeneralSetList.add(allBean);

        GeneralSetBean halfBean = new GeneralSetBean();
        halfBean.setmId("1");
        halfBean.setmName("原  始");
        mRadioGeneralSetList.add(halfBean);
    }

    /**
     * 这是初始化收藏选择框内数据列表的方法
     */
    private void initFavList() {
        mFavGeneralSetList = new ArrayList<GeneralSetBean>();
        GeneralSetBean nofavBean = new GeneralSetBean();
        nofavBean.setmId("0");
        nofavBean.setmName("未收藏");
        mFavGeneralSetList.add(nofavBean);

        GeneralSetBean favBean = new GeneralSetBean();
        favBean.setmId("1");
        favBean.setmName("已收藏");
        mFavGeneralSetList.add(favBean);
    }


    @Override
    public void dismiss() {
        super.dismiss();
        //保存刚才设置的收藏状态
        mplayrcinfo = ph.getLivePlayRcInfo(curChannelId);
        if(mplayrcinfo != null) {
            favGeneralSet = mplayrcinfo.getPlayerFav();
        }else{
            favGeneralSet = 0;
        }
        saveLiveChannel(favGeneralSet);
        //把id存储到数据库
        mLocalData.setKV(
                AppGlobalConsts.PERSIST_NAMES.LIVEPLAY_RADIO_DEFAILT.name(), radioId);
        mLocalData.setKV(
                AppGlobalConsts.PERSIST_NAMES.LIVEPLAY_LEFTRIGHRT_DEFAILT.name(),
                lrId);
        Log.d("MyTAG", "4当前的lrid值为...." + lrId);
        mLocalData.setKV(
                AppGlobalConsts.PERSIST_NAMES.LIVEPLAY_TOPBOTTOM_DEFAILT.name(),
                tbId);

    }

    /**
     * 保存和设置直播节目收藏状态的方法
     */
    private void saveLiveChannel(int favGeneralSet) {
        if(mplayrcinfo == null){
            Log.d("index","saveLiveChannel当前的节目id是curChannelId");
            if (mplayrcinfo == null) {
                mplayrcinfo = currentChannel;
            }
        }
        if(mplayrcinfo!=null){
            mplayrcinfo.setPlayerFav(favGeneralSet);
            Log.d("index", "当前存入的index值为....."+favGeneralSet);
            ph.saveLivePlayRecord(mplayrcinfo);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Lg.i(TAG, "keyCode " + keyCode);
        favGeneralSet = ((tvfan.tv.ui.andr.play.liveplay.Page)mContext).getFav();
        Log.d(TAG, "当前状态下的favGeneralSet的值为....." + favGeneralSet);
        radioGeneralSet =Integer.parseInt(radioId);
        lrGeneralSet =Integer.parseInt(lrId);
        tbGeneralSet =Integer.parseInt(tbId);
        switch (keyCode) {
            case 22://右
                if(getCurrentItemIndex()==0){
                    favGeneralSet++;
                    if(favGeneralSet>1){
                        favGeneralSet=0;
                    }
                    favId =mFavGeneralSetList.get(favGeneralSet).getmId();
                    favName =mFavGeneralSetList.get(favGeneralSet).getmName();
                    tv_collect.setText(favName);
                    tv_collect.setTextSize(App.adjustFontSize(18));

                }else if(getCurrentItemIndex()==1){
                    radioGeneralSet++;
                    if(radioGeneralSet>1){
                        radioGeneralSet=0;
                    }
                    radioId = mRadioGeneralSetList.get(radioGeneralSet).getmId();
                    ((tvfan.tv.ui.andr.play.liveplay.Page)mContext).ratioChange(radioId);
                    radioname = mRadioGeneralSetList.get(radioGeneralSet).getmName();
                    tv_ratio.setText(radioname);
                    tv_ratio.setTextSize(App.adjustFontSize(18));
                    Log.d(TAG, "画面比例当前显示的内容是....." + radioname);
                }else if(getCurrentItemIndex()==2){
                    lrGeneralSet++;
                    if(lrGeneralSet>1){
                        lrGeneralSet=0;
                    }
                    lrId = mLRGeneralSetList.get(lrGeneralSet).getmId();
                    Log.d("MyTAG", "2当前的lrid值为...." + lrId);
                    lrName = mLRGeneralSetList.get(lrGeneralSet).getmName();
                    tv_btn_leftright.setText(lrName);
                    tv_btn_leftright.setTextSize(App.adjustFontSize(18));
                    Log.d(TAG, "左右键功能当前显示的内容是....." + lrName);
                }else if(getCurrentItemIndex()==3){
                    tbGeneralSet++;
                    if(tbGeneralSet>1){
                        tbGeneralSet =0;
                    }
                    tbId = mTBGeneralSetList.get(tbGeneralSet).getmId();
                    tbName = mTBGeneralSetList.get(tbGeneralSet).getmName();
                    tv_btn_topbottom.setText(tbName);
                    tv_btn_topbottom.setTextSize(App.adjustFontSize(18));
                    Log.d(TAG, "上下键键功能当前显示的内容是....." + tbName);
                }

                break;
            case 21://左
                if(getCurrentItemIndex()==0){
                    favGeneralSet--;
                    if(favGeneralSet<0){
                        favGeneralSet=1;
                    }
                    favId =mFavGeneralSetList.get(favGeneralSet).getmId();
                    favName =mFavGeneralSetList.get(favGeneralSet).getmName();
                    tv_collect.setText(favName);
                    tv_collect.setTextSize(App.adjustFontSize(18));

                }else if(getCurrentItemIndex()==1){
                    radioGeneralSet--;
                    if(radioGeneralSet<0){
                        radioGeneralSet=1;
                    }
                    radioId = mRadioGeneralSetList.get(radioGeneralSet).getmId();
                    ((tvfan.tv.ui.andr.play.liveplay.Page)mContext).ratioChange(radioId);
                    radioname = mRadioGeneralSetList.get(radioGeneralSet).getmName();
                    tv_ratio.setText(radioname);
                    tv_ratio.setTextSize(App.adjustFontSize(18));
                    Log.d(TAG, "画面比例当前显示的内容是....." + radioname);
                }else if(getCurrentItemIndex()==2){
                    lrGeneralSet--;
                    if(lrGeneralSet<0){
                        lrGeneralSet=1;
                    }
                    lrId = mLRGeneralSetList.get(lrGeneralSet).getmId();
                    Log.d("MyTAG", "1当前的lrid值为...." + lrId);
                    lrName = mLRGeneralSetList.get(lrGeneralSet).getmName();
                    tv_btn_leftright.setText(lrName);
                    tv_btn_leftright.setTextSize(App.adjustFontSize(18));
                    Log.d(TAG, "左右键功能当前显示的内容是....." + lrName);
                }else if(getCurrentItemIndex()==3){
                    tbGeneralSet--;
                    if(tbGeneralSet<0){
                        tbGeneralSet =1;
                    }
                    tbId = mTBGeneralSetList.get(tbGeneralSet).getmId();
                    tbName = mTBGeneralSetList.get(tbGeneralSet).getmName();
                    tv_btn_topbottom.setText(tbName);
                    tv_btn_topbottom.setTextSize(App.adjustFontSize(18));
                    Log.d(TAG, "上下键键功能当前显示的内容是....." + tbName);
                }
                break;
            case KeyEvent.KEYCODE_BACK:
                 dismiss();
                break;
            default:
                break;
        }
        saveLiveChannel(favGeneralSet);
        setFlagImgShow();
        Log.d(TAG, "当前的favGeneralSet值为..." + favGeneralSet);
        return false;
    }
    /**
     * 获取保存和读取收藏数据的相关参数的方法
     * @param curChannelId
     */
    public void setCurchannelId(String curChannelId) {
        this.curChannelId = curChannelId;
        Lg.e(TAG, "curChannelId===="+curChannelId);
    }
    /**
     *  这是获取当前频道的方法
     * @param currentChannel
     */
    public void setMPlayeRecordInfo(MPlayRecordInfo currentChannel) {
        this.currentChannel = currentChannel;
    }

    /**
     * 这是根据获取到的频道状态刷新dialog收藏框显示的方法
     */
    public void refreshshow(int index) {
        setFavShow(index);
    }
    /**
     * 加载筛选dialog是设置收藏栏显示状态的方法
     */
    public void fav(){
        int fav=((tvfan.tv.ui.andr.play.liveplay.Page)mContext).getFav();
           setFavShow(fav);
        Log.d(TAG, "初始化时的index值为...." + fav);
    }

}
