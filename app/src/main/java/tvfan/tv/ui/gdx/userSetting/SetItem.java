package tvfan.tv.ui.gdx.userSetting;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;

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
import tvfan.tv.EntryPoint;
import tvfan.tv.R;
import tvfan.tv.ScreenProtectActivity;
import tvfan.tv.dal.LocalData;
import tvfan.tv.dal.models.GeneralSetBean;
import tvfan.tv.lib.ACache;
import tvfan.tv.lib.Lg;
import tvfan.tv.ui.gdx.setting.GeneralSetItem;
import tvfan.tv.ui.gdx.setting.GeneralSetItemAdapter;
import tvfan.tv.ui.gdx.setting.QDSetItemAdapter;
import tvfan.tv.ui.gdx.setting.QXDSetItemAdapter;
import tvfan.tv.ui.gdx.setting.ScreenProtectGridAdapter;

/**
 *  desc  排行榜page
 *  @author  yangjh
 *  created at  2016/4/8 20:37
 */
public class SetItem extends Group implements AbsListView.OnItemSelectedChangeListener{
    Image bgImg, iconImage;
    Label titleLabel;
    Grid menuList,qdSettingList,qxdSettingList, screenProtectList;
    GeneralSetItem mSwitchUserItem, mDefinitionItem,mPowerBootItem, mScreenProtectItem;
    private int menuListSelectedIntex = 0;
    private List<GeneralSetBean> generalSetList;
    private List<GeneralSetBean> mDegeneralSetList;
    private List<GeneralSetBean> mPBgeneralSetList;
    private ArrayList<GeneralSetBean> screenProtectBeanList; // 屏保
    private ArrayList<String> data = new ArrayList<String>();
    private GeneralSetItemAdapter menuAdapter;
    private QDSetItemAdapter qdmenuAdapter;
    private QXDSetItemAdapter qxdmenuAdapter;
    private ScreenProtectGridAdapter screenProtectAdapter;
    private int SpGeneralSet = 0;
    private String pId; // 默认启动项
    private String dnId;// 默认清晰度
    private String pbId;//默认是否开机启动
    private String screenProtectId;//默认屏保时间
    private LocalData mLocalData;
    private ACache mAcache;
    private Context context;
    BasePage page;

    public SetItem(Page page, Context context) {
        super(page);
        setPosition(0,0);
        setSize(1520, 1080);
        this.context = context;
        this.page = (BasePage) page;
        _initView();
    }

    public void _initView() {
        mAcache = ACache.get(context);
        initTitle();
        initSP();
        data.add("启动定位");
        data.add("清晰度设置");
        data.add("屏保设置");
        data.add("开机启动设置");
        initDefinitionList();
        initPowerBootList();
        initScreenProtectList();
        requestDate();
        initmenuList();
    }

    private void initPowerBootList() {
        mPBgeneralSetList=new ArrayList<GeneralSetBean>();
        GeneralSetBean fBean = new GeneralSetBean();
        fBean.setmId("0");
        fBean.setmName("否");
        mPBgeneralSetList.add(fBean);

        GeneralSetBean tBean = new GeneralSetBean();
        tBean.setmId("1");
        tBean.setmName("是");
        mPBgeneralSetList.add(tBean);
    }

    private void initDefinitionList() {
        mDegeneralSetList = generalSetList = new ArrayList<GeneralSetBean>();

        GeneralSetBean sBean = new GeneralSetBean();
        sBean.setmId("2");
        sBean.setmName("超清");
        mDegeneralSetList.add(sBean);

        GeneralSetBean hBean = new GeneralSetBean();
        hBean.setmId("1");
        hBean.setmName("高清");
        mDegeneralSetList.add(hBean);

        GeneralSetBean stBean = new GeneralSetBean();
        stBean.setmId("0");
        stBean.setmName("标清");
        mDegeneralSetList.add(stBean);

    }

    private void initScreenProtectList() {
        screenProtectBeanList = new ArrayList<GeneralSetBean>();

        GeneralSetBean bean1 = new GeneralSetBean();
        bean1.setmId("1");
        bean1.setmName(AppGlobalConsts.timeArr[1] + "分钟");
        screenProtectBeanList.add(bean1);

        GeneralSetBean bean2 = new GeneralSetBean();
        bean2.setmId("2");
        bean2.setmName(AppGlobalConsts.timeArr[2] + "分钟");
        screenProtectBeanList.add(bean2);

        GeneralSetBean bean3 = new GeneralSetBean();
        bean3.setmId("3");
        bean3.setmName(AppGlobalConsts.timeArr[3] + "分钟");
        screenProtectBeanList.add(bean3);

        GeneralSetBean bean4 = new GeneralSetBean();
        bean4.setmId("4");
        bean4.setmName(AppGlobalConsts.timeArr[4] + "分钟");
        screenProtectBeanList.add(bean4);

        GeneralSetBean bean5 = new GeneralSetBean();
        bean5.setmId("5");
        bean5.setmName(AppGlobalConsts.timeArr[5] + "分钟");
        screenProtectBeanList.add(bean5);

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
        if(pbId == null){
            pbId = "1";
        }
        Log.d("setPage", "初始pbid....." + pbId);

        screenProtectId = mLocalData
                .getKV(AppGlobalConsts.PERSIST_NAMES.SCREEN_PROTECT_TIME_INDEX.name());
        if (screenProtectId == null) {
            screenProtectId = "1";
        }
    }


    private void initTitle() {
        iconImage = new Image(getPage());
        iconImage.setPosition(1220,1080-144);
        iconImage.setSize(46,46);
        iconImage.setFocusAble(false);
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
        qdSettingList = new Grid(page);
        qdSettingList.setPosition(90, 90);
        qdSettingList.setSize(1520, 840);
        qdSettingList.setGapLength(0);
        qdSettingList.setOrientation(Grid.ORIENTATION_VERTICAL);
        qdSettingList.setVisible(false);
        qdmenuAdapter = new QDSetItemAdapter(page);
        qdmenuAdapter.setData(generalSetList);
        qdSettingList.setAdapter(qdmenuAdapter);
        qdSettingList.setItemClickListener(new AbsListView.OnItemClickListener() {
            @Override
            public void onItemClick(Actor actor, int i, AbsListView absListView) {
                if (i != 0) {
                    pId = generalSetList.get(i - 1).getmId();
                    mLocalData.setKV(
                            AppGlobalConsts.PERSIST_NAMES.PORTAL_DEFAULT_ITEM.name(), pId);
                    for (int j = 0; j < generalSetList.size(); j++) {
                        if (generalSetList.get(j).getmId().equals(pId)) {
                            SpGeneralSet = j;
                            break;
                        }
                    }
                    mSwitchUserItem.setSwitchLabelText(generalSetList.get(
                            SpGeneralSet).getmName());
                }
                menuList.setVisible(true);
                menuList.setSelection(0, true);
                qdSettingList.setVisible(false);

            }
        });
        addActor(qdSettingList);
        qxdSettingList = new Grid(page);
        qxdSettingList.setPosition(90, 90);
        qxdSettingList.setSize(1520, 740);
        qxdSettingList.setGapLength(0);
        qxdSettingList.setOrientation(Grid.ORIENTATION_VERTICAL);
        qxdSettingList.setVisible(false);
        qxdmenuAdapter = new QXDSetItemAdapter(page);
        qxdmenuAdapter.setData(mDegeneralSetList);
        qxdSettingList.setAdapter(qxdmenuAdapter);
        qxdSettingList.setItemClickListener(new AbsListView.OnItemClickListener() {
            @Override
            public void onItemClick(Actor actor, int i, AbsListView absListView) {
                if (i != 0) {
                    dnId = mDegeneralSetList.get(i - 1).getmId();
                    mDefinitionItem.setSwitchLabelText(mDegeneralSetList.get(
                            i - 1).getmName());
                    mLocalData.setKV(
                            AppGlobalConsts.PERSIST_NAMES.VIDEO_DEFAULT_DEFINITION.name(), dnId);
                }
                menuList.setVisible(true);
                menuList.setSelection(0, true);
                menuList.setSelection(1,true);
                qxdSettingList.setVisible(false);

            }
        });
        addActor(qxdSettingList);
        qxdSettingList.setSelection(2, true);

        // 屏保设置
        screenProtectList = new Grid(page);
        screenProtectList.setPosition(90, 90);
        screenProtectList.setSize(1520, 800);
        screenProtectList.setGapLength(0);
        screenProtectList.setOrientation(Grid.ORIENTATION_VERTICAL);
        screenProtectList.setVisible(false);
        screenProtectAdapter = new ScreenProtectGridAdapter(page);
        screenProtectAdapter.setData(screenProtectBeanList);
        screenProtectList.setAdapter(screenProtectAdapter);
        screenProtectList.setItemClickListener(new AbsListView.OnItemClickListener() {
            @Override
            public void onItemClick(Actor actor, int i, AbsListView absListView) {
                if (i != 0) {
                    screenProtectId = screenProtectBeanList.get(i - 1).getmId();
                    mScreenProtectItem.setSwitchLabelText(screenProtectBeanList.get(
                            i - 1).getmName());
                    mLocalData.setKV(
                            AppGlobalConsts.PERSIST_NAMES.SCREEN_PROTECT_TIME_INDEX.name(), screenProtectId);
                    EntryPoint.TIME_TO_SHOW_PROTECT_SCREEN = AppGlobalConsts.timeArr[Integer.parseInt(screenProtectId)] * 60 * 1000;
                }
                menuList.setVisible(true);
                menuList.setSelection(2, true);
                screenProtectList.setVisible(false);

            }
        });
        addActor(screenProtectList);
        screenProtectList.setSelection(2, true);


        menuList = new Grid(page);
        menuList.setPosition(90, 300);
        menuList.setSize(1520, 560);
        menuList.setGapLength(0);
        menuList.setOrientation(Grid.ORIENTATION_VERTICAL);
        menuList.setOnItemSelectedChangeListener(this);
        menuList.setItemClickListener(new AbsListView.OnItemClickListener() {
            @Override
            public void onItemClick(Actor actor, int i, AbsListView absListView) {
                if (actor.getTag().toString().equals("0")) {
                    menuList.setVisible(false);
                    qdSettingList.setVisible(true);
                    qdSettingList.setSelection(SpGeneralSet + 1, true);
                } else if (actor.getTag().toString().equals("1")) {
                    menuList.setVisible(false);
                    qxdSettingList.setVisible(true);
                    qxdSettingList.setSelection(2 - Integer.parseInt(dnId) + 1, true);

                } else if (actor.getTag().toString().equals("2")) {
                    menuList.setVisible(false);
                    screenProtectList.setVisible(true);
                    screenProtectList.setSelection(Integer.parseInt(screenProtectId), true);
                } else if (actor.getTag().toString().equals("3")) {
                    if (Integer.parseInt(pbId) == 0) {
                        pbId = 1 + "";
                    } else {
                        pbId = 0 + "";
                    }
                    mLocalData.setKV(AppGlobalConsts.PERSIST_NAMES.POWERBOOT.name(), pbId);
                    mPowerBootItem.setmCheckBtn(mPBgeneralSetList.get(Integer.parseInt(pbId)).getmName());
                }
            }
        });
        menuList.setRowNum(1);
        menuAdapter = new GeneralSetItemAdapter(page);
        menuAdapter.setData(data);
        menuList.setAdapter(menuAdapter);
        addActor(menuList);
        //menuList.setSelection(0,true);
        mSwitchUserItem = (GeneralSetItem) menuList
                .getListItemAt(menuListSelectedIntex);
        mSwitchUserItem.setSwitchLabelText(generalSetList.get(SpGeneralSet)
                .getmName());


        mDefinitionItem = (GeneralSetItem) menuList.getListItemAt(1);
        mDefinitionItem.setSwitchLabelText(mDegeneralSetList.get(
                2 - Integer.parseInt(dnId)).getmName());

        mScreenProtectItem = (GeneralSetItem) menuList.getListItemAt(2);
        mScreenProtectItem.setSwitchLabelText(screenProtectBeanList.get(Integer.parseInt(screenProtectId) - 1).getmName());

        mPowerBootItem = (GeneralSetItem) menuList.getListItemAt(3);
        try{
         /*   mPowerBootItem.setSwitchLabelText(mPBgeneralSetList.get(
                    Integer.parseInt(pbId)).getmName());*/
            mPowerBootItem.setmCheckBtn(mPBgeneralSetList.get(
                    Integer.parseInt(pbId)).getmName());
        }catch (NumberFormatException e){
            e.printStackTrace();
        }

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
            generalSetList = new ArrayList<GeneralSetBean>();
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
                    generalSetList.add(generalSetBean);
                }
            }
            if (generalSetList.size() > 0) {
                for (int i = 0; i < generalSetList.size(); i++) {
                    if (generalSetList.get(i).getmId().equals(pId)) {
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
