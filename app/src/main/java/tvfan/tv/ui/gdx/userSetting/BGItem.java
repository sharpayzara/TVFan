package tvfan.tv.ui.gdx.userSetting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.luxtone.lib.widget.AbsListView;
import com.luxtone.lib.widget.CullGroup;
import com.luxtone.lib.widget.Grid;

import java.util.ArrayList;

import tvfan.tv.App;
import tvfan.tv.AppGlobalConsts;
import tvfan.tv.BasePage;
import tvfan.tv.R;
import tvfan.tv.dal.LocalData;
import tvfan.tv.ui.gdx.widgets.Button;
import tvfan.tv.ui.gdx.widgets.TVFANLabel;

/**
 *  desc  切换背景选项
 *  @author  yangjh
 *  created at  16-4-20 上午11:40
 */
public class BGItem extends Group implements AbsListView.OnItemClickListener {
    private Button upBtn,downBtn;
    private CullGroup cullGroup;
    public Label titleLabel,pageLable,pageLable2,pageLable3;
    private Image iconImage,frameLine;
    private Grid mGrid;
    private ChangeBackgroundAdapter adapter;
    private ArrayList<Integer> mImageList = new ArrayList<Integer>();
    private ArrayList<Integer> mSmallImageList = new ArrayList<Integer>();
    private LocalData mLocalData;
    private Context mContext;
    private int SpBackground;
    private ChangeBackgroundItem mChangeBackgroundItemTemp1;
    private ChangeBackgroundItem mChangeBackgroundItemTemp2;

    public BGItem(Page page,Context mContext) {
        super(page);
        this.mContext = mContext;
        setSize(1520,1080);
        setPosition(0,0);
        cullGroup = new CullGroup(getPage());
        cullGroup.setSize(1520,1080);
        cullGroup.setPosition(0,0);
        cullGroup.setCullingArea(new Rectangle(0,0,1520,1080));
        addActor(cullGroup);
        initImagesData();
        initData();
        initLable();
        initBtn();
        initSmallImagesData();
        initGrid();
    }
    private void initData(){
        mLocalData = new LocalData(mContext);
        String temp = mLocalData
                .getKV(AppGlobalConsts.PERSIST_NAMES.BACKGROUND_IMAGE.name());

        for (int i = 0; i < mImageList.size(); i++) {
            if (String.valueOf(mImageList.get(i)).equals(temp)) {
                SpBackground = i;
                break;
            }
        }
    }


    @Override
    public void onItemClick(Actor arg0, int arg1, AbsListView arg2) {
        pageLable.setText((arg1+1)+"");
        ((Page)getPage()).bgImg.setDrawableResource(mImageList.get(arg1));
        mChangeBackgroundItemTemp1 = (ChangeBackgroundItem) arg0;
        if (mChangeBackgroundItemTemp2 != null) {
            mChangeBackgroundItemTemp2.mImageTrue.setVisible(false);
        } else {
            mChangeBackgroundItemTemp2 = adapter
                    .getmChangeBackgroundItem();
            mChangeBackgroundItemTemp2.mImageTrue.setVisible(false);
        }
        mChangeBackgroundItemTemp2 = mChangeBackgroundItemTemp1;

        mChangeBackgroundItemTemp1.mImageTrue.setVisible(true);
        adapter.setmTrue(arg1);
        mLocalData.setKV(AppGlobalConsts.PERSIST_NAMES.BACKGROUND_IMAGE.name(),
                mImageList.get(arg1).toString());

        Intent intent = new Intent();
        ((BasePage)getPage()).sendLocalStickyMsg(AppGlobalConsts.LOCAL_MSG_FILTER.BACKGROUND_CHANGE, intent);
    }

    private void initImagesData() {
        mImageList.add(R.mipmap.bgc);
        mImageList.add(R.mipmap.bga);
        mImageList.add(R.mipmap.bgb);
        mImageList.add(R.mipmap.other_background);
        mImageList.add(R.mipmap.bgd);
        mImageList.add(R.mipmap.bge);
        mImageList.add(R.mipmap.bgf);
        mImageList.add(R.mipmap.bgg);
    }

    private void initSmallImagesData() {
        mSmallImageList.add(R.mipmap.smallbgc);
        mSmallImageList.add(R.mipmap.smallbga);
        mSmallImageList.add(R.mipmap.smallbgb);
        mSmallImageList.add(R.mipmap.smallbjother);
        mSmallImageList.add(R.mipmap.smallbgd);
        mSmallImageList.add(R.mipmap.smallbge);
        mSmallImageList.add(R.mipmap.smallbgf);
        mSmallImageList.add(R.mipmap.smallbgg);
    }


    private void initGrid() {
        mGrid = new Grid(getPage());
        mGrid.setSize(1119,634);
        mGrid.setOrientation(Grid.ORIENTATION_VERTICAL);
        mGrid.setRowNum(1);
        mGrid.setPosition(181,181);
        mGrid.setItemClickListener(this);
        adapter = new ChangeBackgroundAdapter(getPage());
        adapter.setmTrue(SpBackground);
        adapter.setData(mSmallImageList);
        mGrid.setAdapter(adapter);
        mGrid.setSelection(SpBackground, false);
        mGrid.setOnItemSelectedChangeListener(new AbsListView.OnItemSelectedChangeListener() {
            @Override
            public void onSelectedChanged(int i, Actor actor) {
                pageLable.setText(i+1+"");
            }
        });
        pageLable.setText(SpBackground+1+"");
        addActor(mGrid);
        mChangeBackgroundItemTemp2 = adapter
                .getmChangeBackgroundItem();
        if (mChangeBackgroundItemTemp2 != null) {
            mChangeBackgroundItemTemp2.toFront();
        }
    }

    private void initLable() {
        iconImage = new Image(getPage());
        iconImage.setPosition(1220,1080-144);
        iconImage.setSize(46,46);
        iconImage.setFocusAble(false);
        iconImage.setDrawableResource(R.mipmap.change_skin);
        addActor(iconImage);
        titleLabel = new Label(getPage());
        titleLabel.setText("更换背景");
        titleLabel.setPosition(1280,1080-140);
        titleLabel.setTextSize(40);
        titleLabel.setTextColor(Color.parseColor("#636361"));
        addActor(titleLabel);

        pageLable = new Label(getPage());
        pageLable.setPosition(183,1080-235);
        pageLable.setTextSize(38);
        pageLable.setTextColor(Color.parseColor("#2E96E8"));
        addActor(pageLable);


        pageLable2 = new Label(getPage());
        pageLable2.setPosition(211,1080-235);
        pageLable2.setTextSize(38);
        pageLable2.setTextColor(Color.parseColor("#B5CAFF"));
        pageLable2.setText("/" + mImageList.size());
        addActor(pageLable2);

        pageLable3 = new TVFANLabel(getPage());
        pageLable3.setPosition(294,1080-230);
        pageLable3.setTextSize(32);
        pageLable3.setTextColor(Color.parseColor("#647592"));
        pageLable3.setText("按 OK键 确认选择背景");
        addActor(pageLable3);
    }

    private void initBtn() {
        upBtn = new Button(getPage(),65,61);
        upBtn.getImage().setDrawableResource(R.mipmap.up_normal);
        upBtn.setPosition(706,1080-163);
        upBtn.setFocusAble(false);

        downBtn = new Button(getPage(),65,61);
        downBtn.getImage().setDrawableResource(R.mipmap.down_normal);
        downBtn.setPosition(706,60);
        downBtn.setFocusAble(false);

        cullGroup.addActor(upBtn);
        cullGroup.addActor(downBtn);
    }
}
