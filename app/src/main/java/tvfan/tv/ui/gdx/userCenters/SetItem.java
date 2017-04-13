package tvfan.tv.ui.gdx.userCenters;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.AbsListView;
import com.luxtone.lib.widget.Grid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tvfan.tv.App;
import tvfan.tv.AppGlobalConsts;
import tvfan.tv.AppGlobalVars;
import tvfan.tv.BasePage;
import tvfan.tv.R;
import tvfan.tv.dal.LocalData;
import tvfan.tv.dal.models.GeneralSetBean;
import tvfan.tv.lib.ACache;
import tvfan.tv.lib.Lg;
import tvfan.tv.ui.gdx.setting.GeneralSetItem;
import tvfan.tv.ui.gdx.setting.GeneralSetItemAdapter;

/**
 *  desc  排行榜page
 *  @author  yangjh
 *  created at  2016/4/8 20:37
 */
public class SetItem extends Group implements AbsListView.OnItemSelectedChangeListener {
    Image bgImg, iconImage;
    Label titleLabel;
    Grid menuList;
    GeneralSetItem mSwitchUserItem, mDefinitionItem,mPowerBootItem;
    private int menuListSelectedIntex = 0;
    private List<GeneralSetBean> GeneralSetList;
    private List<GeneralSetBean> mDeGeneralSetList;
    private List<GeneralSetBean> mPBGeneralSetList;
    private ArrayList<String> data = new ArrayList<String>();
    private GeneralSetItemAdapter menuAdapter;
    private int SpGeneralSet = 0;
    private int DeGeneralSet = 0;
    private int pbGeneralSet = 0;
    private String pId; // 默认启动项
    private String dnId;// 默认清晰度
    private String pbId;//默认是否开机启动
    private LocalData mLocalData;
    private ACache mAcache;
    private Context context;
    BasePage page;

    public SetItem(Page page, Context context) {
        super(page);
        setPosition(90,0);
        setSize(1520, 1080);
        this.context = context;
        this.page = (BasePage) page;
        _initView();
    }

    public void _initView() {
        mAcache = ACache.get(context);
        initTitle();
        initSP();
        data.add("启动设置");
        data.add("清晰度设置");
        data.add("开机启动设置");
        initDefinitionList();
        initPowerBootList();
        requestDate();
        initmenuList();
    }

    private void initPowerBootList() {
        mPBGeneralSetList=new ArrayList<GeneralSetBean>();
        GeneralSetBean fBean = new GeneralSetBean();
        fBean.setmId("0");
        fBean.setmName("否");
        mPBGeneralSetList.add(fBean);

        GeneralSetBean tBean = new GeneralSetBean();
        tBean.setmId("1");
        tBean.setmName("是");
        mPBGeneralSetList.add(tBean);
    }

    private void initDefinitionList() {
        mDeGeneralSetList = GeneralSetList = new ArrayList<GeneralSetBean>();

        GeneralSetBean sBean = new GeneralSetBean();
        sBean.setmId("2");
        sBean.setmName("超清");
        mDeGeneralSetList.add(sBean);

        GeneralSetBean hBean = new GeneralSetBean();
        hBean.setmId("1");
        hBean.setmName("高清");
        mDeGeneralSetList.add(hBean);

        GeneralSetBean stBean = new GeneralSetBean();
        stBean.setmId("0");
        stBean.setmName("标清");
        mDeGeneralSetList.add(stBean);

    }

    private void initSP() {
        mLocalData = new LocalData(context);
        if (TextUtils.isEmpty(pId)) {
            pId = "1";
        }
        pId = mLocalData
                .getKV(AppGlobalConsts.PERSIST_NAMES.PORTAL_DEFAULT_ITEM.name());
        if (TextUtils.isEmpty(dnId)) {
            dnId = "2";
        }
        dnId = mLocalData
                .getKV(AppGlobalConsts.PERSIST_NAMES.VIDEO_DEFAULT_DEFINITION
                        .name());
        if (TextUtils.isEmpty(pId)) {
            pbId = "0";
        }
        pbId=mLocalData
                .getKV(AppGlobalConsts.PERSIST_NAMES.POWERBOOT.name());
        Log.d("setPage", "初始pbid....." + pbId);
    }


    private void initTitle() {
        iconImage = new Image(getPage());
        iconImage.setPosition(1220,1080-144);
        iconImage.setSize(46,46);
        iconImage.setFocusAble(true);
        iconImage.setDrawableResource(R.mipmap.setting_icon);
        addActor(iconImage);
        titleLabel = new Label(getPage());
        titleLabel.setText("常规设置");
        titleLabel.setPosition(1280,1080-140);
        titleLabel.setTextSize(40);
        titleLabel.setTextColor(Color.parseColor("#636361"));
        addActor(titleLabel);

    }

    private void initmenuList() {
        menuList = new Grid(page);
        menuList.setPosition(177, 300);
        menuList.setSize(1660, 420);
        menuList.setGapLength(0);
        menuList.setOrientation(Grid.ORIENTATION_VERTICAL);
        menuList.setOnItemSelectedChangeListener(this);
        menuList.setRowNum(1);
        menuAdapter = new GeneralSetItemAdapter(page);
        menuAdapter.setData(data);
        menuList.setAdapter(menuAdapter);
        addActor(menuList);
        mSwitchUserItem = (GeneralSetItem) menuList
                .getListItemAt(menuListSelectedIntex);
        mSwitchUserItem.setSwitchLabelText(GeneralSetList.get(SpGeneralSet)
                .getmName());

        mDefinitionItem = (GeneralSetItem) menuList.getListItemAt(1);
        mDefinitionItem.setSwitchLabelText(mDeGeneralSetList.get(
                2 - Integer.parseInt(dnId)).getmName());
        mPowerBootItem = (GeneralSetItem) menuList.getListItemAt(2);
        try{
            mPowerBootItem.setSwitchLabelText(mPBGeneralSetList.get(
                    Integer.parseInt(pbId)).getmName());
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
    }


    public boolean onKeyDown(int keycode) {
        Lg.i("iptv", "keycode " + keycode);
        switch (keycode) {
            case 22:
            case 23:
                if (menuListSelectedIntex == 0) {
                    SpGeneralSet++;
                    if (SpGeneralSet >=GeneralSetList.size()) {
                        SpGeneralSet = 0;
                    }
                    pId = GeneralSetList.get(SpGeneralSet).getmId();
                    mSwitchUserItem.setSwitchLabelText(GeneralSetList.get(
                            SpGeneralSet).getmName());
                }else if(menuListSelectedIntex == 1)  {
                    DeGeneralSet++;
                    if (DeGeneralSet >= mDeGeneralSetList.size()) {
                        DeGeneralSet = 0;
                    }
                    dnId = mDeGeneralSetList.get(DeGeneralSet).getmId();
                    mDefinitionItem.setSwitchLabelText(mDeGeneralSetList.get(
                            DeGeneralSet).getmName());
                }else{
                    pbGeneralSet++;
                    if (pbGeneralSet >=mPBGeneralSetList.size()) {
                        pbGeneralSet = 0;
                    }
                    pbId = mPBGeneralSetList.get(pbGeneralSet).getmId();
                    mPowerBootItem.setSwitchLabelText(mPBGeneralSetList.get(
                            pbGeneralSet).getmName());
                }
                break;
            case 21:
                if (menuListSelectedIntex == 0) {
                    SpGeneralSet--;
                    if (SpGeneralSet < 0) {
                        SpGeneralSet = GeneralSetList.size() - 1;
                    }
                    pId = GeneralSetList.get(SpGeneralSet).getmId();
                    mSwitchUserItem.setSwitchLabelText(GeneralSetList.get(
                            SpGeneralSet).getmName());
                }else if(menuListSelectedIntex == 1) {
                    DeGeneralSet--;
                    if (DeGeneralSet < 0) {
                        DeGeneralSet = mDeGeneralSetList.size() - 1;
                    }
                    dnId = mDeGeneralSetList.get(DeGeneralSet).getmId();
                    mDefinitionItem.setSwitchLabelText(mDeGeneralSetList.get(
                            DeGeneralSet).getmName());
                }else{
                    pbGeneralSet--;
                    if (pbGeneralSet < 0) {
                        pbGeneralSet = mPBGeneralSetList.size() - 1;
                    }
                    pbId = mPBGeneralSetList.get(pbGeneralSet).getmId();
                    Log.d("setPage", "pbId............" + pbId);
                    mPowerBootItem.setSwitchLabelText(mPBGeneralSetList.get(
                            pbGeneralSet).getmName());
                    String s = mPBGeneralSetList.get(
                            pbGeneralSet).getmName();
                    Log.d("setPage", "name............"+s);
                }
                break;
            case KeyEvent.KEYCODE_BACK:

                break;
            default:
                break;
        }

        return false;
    }


    public boolean onBackKeyDown() {
        System.out.println("把id存储到数据库");
        mLocalData.setKV(
                AppGlobalConsts.PERSIST_NAMES.PORTAL_DEFAULT_ITEM.name(), pId);
        mLocalData.setKV(
                AppGlobalConsts.PERSIST_NAMES.VIDEO_DEFAULT_DEFINITION.name(), dnId);
        Log.d("setPage", "name...." + AppGlobalConsts.PERSIST_NAMES.VIDEO_DEFAULT_DEFINITION.name() + "...." + "pbId....." + dnId);
        mLocalData.setKV(
                AppGlobalConsts.PERSIST_NAMES.POWERBOOT.name(),
                pbId);
        Log.d("setPage", "name...." + AppGlobalConsts.PERSIST_NAMES.POWERBOOT.name() + "...." + "pbId...." + pbId);
        //return super.onBackKeyDown();
        return true;
    }

    @Override
    public void onSelectedChanged(int arg0, Actor arg1) {
        Lg.i("iptv", "选择的项:" + arg0);
        arg1.setFocused(true);
        menuListSelectedIntex = arg0;
    }

    private void requestDate() {
        try {
            JSONArray jsonarr = (JSONArray) AppGlobalVars.getIns().TMP_VARS
                    .get("PORTAL_NAV_DATA");
            if(jsonarr == null){
                String responseStr = mAcache.getAsString("epg_portal_navBar");
                if(responseStr != null){
                    JSONObject response = new JSONObject(responseStr);
                    jsonarr = response.getJSONArray("data");
                }
            }
            GeneralSetList = new ArrayList<GeneralSetBean>();
            //wanqi,修复友盟上报的空指针bug
            if (jsonarr != null) {
                int count = jsonarr.length();
                for (int i = 0; i < count; i++) {
                    JSONObject jsonobject = (JSONObject) jsonarr.get(i);
                    GeneralSetBean generalSetBean = new GeneralSetBean();

                    generalSetBean.setmNodeType(jsonobject.optString(
                            "nodeType", ""));
                    generalSetBean.setmName(jsonobject.optString("name", ""));
                    generalSetBean.setmId(jsonobject.optString("id", ""));
                    generalSetBean.setmAction(jsonobject
                            .optString("action", ""));
                    generalSetBean.setmActionUR(jsonobject.optString(
                            "actionURL", ""));
                    GeneralSetList.add(generalSetBean);
                }
            }
            if (GeneralSetList.size() > 0) {
                for (int i = 0; i < GeneralSetList.size(); i++) {
                    if (GeneralSetList.get(i).getmId().equals(pId)) {
                        SpGeneralSet = i;
                        break;
                    }
                }
            } else {
                System.out.println("常规设置获取获取数据失败。");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
