package tvfan.tv.ui.gdx.userSetting;

import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Timer;
import com.luxtone.lib.gdx.OnkeyListener;
import com.luxtone.lib.widget.AbsListView;
import com.luxtone.lib.widget.CullGroup;
import com.luxtone.lib.widget.Grid;

import tvfan.tv.App;
import tvfan.tv.AppGlobalConsts;
import tvfan.tv.BasePage;
import tvfan.tv.R;
import tvfan.tv.dal.LocalData;
import tvfan.tv.lib.Lg;

/**
 *  desc  用户设置页面
 *  @author  yangjh
 *  created at
 */
public class Page extends BasePage {
    // 图片背景
    public Image bgImg,aboutUsImg;
    private int bgresid;
    private CullGroup cullGroup;
    private UserMenuListAdapter.UserMenuListItem menulistitem;
    private UserMenuGroup menuGroup;   // 左侧栏目按钮布局
    // 数据
    private String[] menuArray = new String[] { "常规设置","更换背景","关于我们"};// 左侧栏目分类列表数据
    boolean fcousRight = false;
    boolean fmark = false;
    boolean mmark = false;
    boolean pmark = false;
    boolean omark = false;
    boolean cmark = false;
    private int iprepos = -1;
    private int selectPos = 0;
    private Timer timer;
    private Timer.Task task;
    private SetItem setItem;
    private BGItem bgItem;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        timer = new Timer();
        _initBg();
        _initMenuGroup();
        _initData();
        _initAboutUs();
    }

    private void _initAboutUs() {
        if(aboutUsImg == null){
            aboutUsImg = new Image(this);
            aboutUsImg.setDrawableResource(R.mipmap.about_us);
            aboutUsImg.setVisible(false);
            aboutUsImg.setSize(1375,716);
            aboutUsImg.setPosition(50,120);
            cullGroup.addActor(aboutUsImg);
        }
    }

    /**
     * 初始化背景图
     */
    private void _initBg() {
        if (bgImg != null) {
            bgImg.dispose();
            bgImg = null;
        }
        bgImg = new Image(this);
        bgImg.setPosition(-50, -50);
        bgImg.setSize(AppGlobalConsts.APP_WIDTH + 100,
                AppGlobalConsts.APP_HEIGHT + 100);
        bgImg.setFocusAble(false);
        bgImg.setAlpha(0.95f);

        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                addActor(bgImg);
                bgImg.toBack();
                bgImg.addAction(Actions.fadeOut(0));

                LocalData ld = new LocalData(getActivity());
                String bg = ld
                        .getKV(AppGlobalConsts.PERSIST_NAMES.BACKGROUND_IMAGE
                                .name());

                if (bg == null)
                    bgresid = R.drawable.other_background;
                else
                    bgresid = Integer.parseInt(bg);

                bgImg.setDrawableResource(bgresid);
                bgImg.addAction(Actions.fadeIn(0.6f));
            }
        });
        addActor(bgImg);

        cullGroup = new CullGroup(this);
        cullGroup.setSize(1920-400, 1080);
        cullGroup.setPosition(400, 0);
        cullGroup.setCullingArea(new Rectangle(0, 0, 1920-400, 1080));
        addActor(cullGroup);
    }

    /**
     * 初始化页面group 坐标宽高
     */
    private void _initMenuGroup() {
        // 设置左侧栏布局 并添加背景图片
        menuGroup = new UserMenuGroup(this, menuArray);
        menuGroup.setSize(400, 1080);
        menuGroup.setPosition(0, 0);
        ((UserMenuGroup) menuGroup)
                .setOnItemSelectedChangeListener(new AbsListView.OnItemSelectedChangeListener() {

                    @Override
                    public void onSelectedChanged(final int pos, Actor actor) {
                        _setOnItemSelectedChange(pos, actor);
                    }

                });
    }

    private void _initData() {
        menuGroup.setSelection(0);
        menuGroup.mGrid.setNextFocusRight("0");
        //setItem.menuList.setSelection(0,true);
        menuGroup.setOnkeyListener(new OnkeyListener() {
            @Override
            public boolean onKey(Actor actor, int i) {
                if(i == 22 && selectPos == 0 && setItem != null &&setItem.menuList != null){
                    if(setItem.menuList.getAdapterCount() != 0){
                        setItem.menuList.setSelection(0,true);
                    }
                }
                return false;
            }
        });
        addActor(menuGroup);
    }

    public void _setOnItemSelectedChange(final int pos, Actor actor) {
        fmark = false;
        mmark = false;
        pmark = false;
        omark = false;
        cmark = false;
        selectPos = pos;
        try {
            if (menulistitem != null) {
                menulistitem.setFocusImgBg(false);
            }
            menulistitem = (UserMenuListAdapter.UserMenuListItem) actor;
            menulistitem.setFocusImgBg(true);
            if (task != null) {
                task.cancel();
            }
            if (timer != null) {
                timer.stop();
            }

            if (iprepos != pos) {
                fcousRight = true;
                task = new Timer.Task() {

                    @Override
                    public void run() {
                        iprepos = pos;
                        _switch (pos);
                    }
                };
                timer.scheduleTask(task, 0.8f);
                timer.start();
            } else {
                fcousRight = false;
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void _switch(int pos){
        cullGroup.clearChildren();
        fcousRight = false;
        selectPos = pos;
        switch (pos) {
            case 0:
                if (setItem == null) {
                    setItem = new tvfan.tv.ui.gdx.userSetting.SetItem(Page.this, getActivity());
                    setItem.setSize(1520, 1080);
                    setItem.setPosition(0, 0);
                }
                cullGroup.addActor(setItem);
                break;
            case 1:
                if (bgItem == null) {
                    bgItem = new tvfan.tv.ui.gdx.userSetting.BGItem(Page.this,getActivity());
                    bgItem.setSize(1520, 1080);
                    bgItem.setPosition(0, 0);
                }
                cullGroup.addActor(bgItem);
                break;
            case 2:
                aboutUsImg.setVisible(true);
                Image iconImage = new Image(this);
                iconImage.setPosition(1220,1080-144);
                iconImage.setSize(46,46);
                iconImage.setFocusAble(false);
                iconImage.setDrawableResource(R.mipmap.about_us_icon);
                cullGroup.addActor(iconImage);
                Label titleLabel = new Label(this);
                titleLabel.setText("关于我们");
                titleLabel.setPosition(1280,1080-140);
                titleLabel.setTextSize(App.adjustFontSize(40f));
                titleLabel.setTextColor(Color.parseColor("#636361"));
                cullGroup.addActor(titleLabel);
                cullGroup.addActor(aboutUsImg);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keycode) {
        switch (keycode) {
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (fcousRight) {
                    return true;
                }else {
                    return false;
                }

        }
        return super.onKeyDown(keycode);
    }
}
