package tvfan.tv.ui.gdx.topLists;

/**
 * 個人中心-数据界面
 *
 * @author 孫文龍
 *
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import tvfan.tv.App;
import tvfan.tv.BasePage;
import tvfan.tv.dal.HttpResponse;
import tvfan.tv.dal.LocalData;
import tvfan.tv.dal.RemoteData;
import android.content.Context;
import android.os.Bundle;

import tvfan.tv.R;
import tvfan.tv.dal.models.ProgramListItem;
import tvfan.tv.ui.gdx.widgets.CIBNLoadingView;
import tvfan.tv.ui.gdx.widgets.TVFANLabel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.luxtone.lib.gdx.OnClickListener;
import com.luxtone.lib.gdx.OnFocusChangeListener;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.AbsListView;
import com.luxtone.lib.widget.AbsListView.OnItemClickListener;
import com.luxtone.lib.widget.AbsListView.OnItemSelectedChangeListener;
import com.luxtone.lib.widget.AbsListView.ScrollStatusListener;
import com.luxtone.lib.widget.CullGroup;
import com.luxtone.lib.widget.Grid;

public class CommonTopItem extends Group implements UpdateProgramDetail{

    private List<TopListItem>  itemList;
    private Label favNo, topLable, moreLable;
    private Image moreImage,topRedIcon;
    Context context;
    private LocalData _localData;
    private RemoteData _rd;
    private CullGroup moreGroup,gridcullGroup;
    private Grid mGrid;
    private tvfan.tv.ui.gdx.topLists.Page page;
    private TopListGridAdapter dataAdapter;
    private CIBNLoadingView loadingview;
    private TVFANLabel programName,programContent;
    private String pt;
    private int clickPos = -1;
    private boolean backFromDetail = false;//mGid是否已经点击了
    public CommonTopItem(Page page, Context context) {
        super(page);
        setSize(1560, 1080);
        this.context = context;
        this.page = (tvfan.tv.ui.gdx.topLists.Page)page;
        itemList = new ArrayList<TopListItem>();
        _localData = new LocalData(context);
        _rd = new RemoteData(context);
        _initView();
    }

    private void _initView() {

        topRedIcon = new Image(getPage());
        topRedIcon.setDrawableResource(R.mipmap.top10_icon);
        topRedIcon.setSize(60,60);
        topRedIcon.setPosition(45,1080-243);
        addActor(topRedIcon);

        topLable = new Label(getPage());
        topLable.setPosition(490-360+10, 1080-238);
        topLable.setColor(Color.WHITE);
        topLable.setTextSize(33);
        topLable.setText("");
        addActor(topLable);

        moreImage = new Image(getPage());
        moreImage.setFocused(true);
        moreImage.setSize(160, 50);
        moreImage.setPosition(0,0);
        moreImage.setDrawableResource(R.drawable.more_lable);
        moreImage.setFocusAble(true);
        moreImage.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChanged(Actor actor, boolean b) {
                if(b){
                    moreImage.setDrawableResource(R.drawable.more_lable2);
                    moreLable.toFront();
                }else{
                    moreImage.setDrawableResource(R.drawable.more_lable);
                    moreLable.toFront();
                }
            }
        });
        moreImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(Actor actor) {
                Bundle bundle = new Bundle();
                bundle.putString("parentCatgId", pt);
                ((tvfan.tv.ui.gdx.topLists.Page)getPage()).doAction(BasePage.ACTION_NAME.OPEN_PROGRAM_LIST, bundle);
            }
        });
        addActor(moreImage);

        moreLable = new TVFANLabel(getPage());
        moreLable.setSize(160, 50);
        moreLable.setPosition(30, 10);
        moreLable.setTextColor(android.graphics.Color.parseColor("#FFFFFF"));
        addActor(moreLable);

        moreGroup = new CullGroup(getPage());
        moreGroup.setSize(160, 50);
        moreGroup.setPosition(1650-360, 1080-135);
        addActor(moreGroup);
        moreGroup.addActor(moreImage);
        moreGroup.addActor(moreLable);


        gridcullGroup = new CullGroup(getPage());
        //gridcullGroup.setSize(1330, 890);
        gridcullGroup.setSize(1500, 470);
        gridcullGroup.setPosition(60, 0);
	/*	gridcullGroup.setCullingArea(new Rectangle(35, -30, 1330 + 220,
				890 + 70));*/
        addActor(gridcullGroup);

        // 正在搜索
        loadingview = new CIBNLoadingView(getPage());
        loadingview.setVisible(false);
        addActor(loadingview);

        favNo = new Label(getPage());
        favNo.setPosition(555, 440);
        favNo.setTextSize(50);
        favNo.setColor(Color.WHITE);
        favNo.setText("暂无数据");
        favNo.setAlpha(0.9f);
        favNo.setVisible(false);
        addActor(favNo);


        programContent = new TVFANLabel(getPage());
        programContent.setPosition(0,50);
        programContent.setSize(1400,130);
        programContent.setSpacingadd(10);
        programContent.setMaxLine(2);
        programContent.setTextSize(30);
        addActor(programContent);

        programName = new TVFANLabel(getPage());
        programName.setPosition(0,200);
        programName.setSize(300,50);
        programName.setTextSize(38);
        addActor(programName);

    }



    public void getItemTop(String templateId,String pt){
        this.pt = pt;
        _rd.getTopListItem(templateId, pt, new HttpResponse.Listener4JSONArray() {
            @Override
            public void onResponse(JSONArray response) throws JSONException {
                itemList.clear();
                try{
                    if(response.length() == 0){
                        return;
                    }
                    for(int i = 0; i < response.length(); i++){
                        TopListItem item = new TopListItem();
                        if(response.getJSONObject(i).has("id")){
                            item.setId(response.getJSONObject(i).getString("id"));
                        }
                        if(response.getJSONObject(i).has("picture")){
                            item.setPicture(response.getJSONObject(i).getString("picture"));
                        }
                        if(response.getJSONObject(i).has("name")){
                            item.setName(response.getJSONObject(i).getString("name"));
                        }
                        if(response.getJSONObject(i).has("des")){
                            item.setDes(response.getJSONObject(i).getString("des"));
                        }
                        if(response.getJSONObject(i).has("score")){
                            item.setScore(response.getJSONObject(i).getString("score"));
                        }
                        itemList.add(item);
                    }
                    loadingview.setVisible(false);
                    Gdx.app.postRunnable(new Runnable() {

                        @Override
                        public void run() {
                            if (itemList.isEmpty()) {//数据为空的时候
                                // 暂无数据
                                _visibleView(true);
                                return;
                            }
                            _initGrid();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    loadingview.setVisible(false);
                    _visibleView(true);
                }
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }


    /**
     * 初始化我的数据节目的列表
     */
    private void _initGrid() {
        if (mGrid == null) {
            mGrid = new Grid(getPage());
            mGrid.setPosition(0, 290);
            mGrid.setSize(1400, 470);
            mGrid.setGapLength(10);
            mGrid.setOrientation(Grid.ORIENTATION_LANDSPACE);
            mGrid.setRowNum(1);
            mGrid.setCull(false);
            dataAdapter = new TopListGridAdapter(page);
            dataAdapter.setUpdateListener(this);
            dataAdapter.setData(itemList);
            mGrid.setAdapter(dataAdapter);
            //	mGrid.setAdjustiveScrollLengthForBackward(100);
            mGrid.setAdjustiveScrollLengthForForward(120);
            if(backFromDetail){
                mGrid.setSelection(clickPos, true);
                backFromDetail = false;
            }
            mGrid.setItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(Actor itemView, int position,
                                        AbsListView grid) {
                    TopListItem programListItem = itemList.get(position);
                    Bundle options = new Bundle();
                    options.putString("id", programListItem.getId());
                    options.putString("name", programListItem.getName());
                    ((tvfan.tv.ui.gdx.topLists.Page)getPage()).doAction(BasePage.ACTION_NAME.OPEN_DETAIL, options);
                }
            });
            mGrid.setOnItemSelectedChangeListener(new OnItemSelectedChangeListener() {

                @Override
                public void onSelectedChanged(int position, Actor actor) {

                }
            });
            gridcullGroup.addActor(mGrid);

            _visibleView(false);
        } else {
            _visibleView(false);
            dataAdapter.setData(itemList);
            mGrid.setAdapter(dataAdapter);
            if(backFromDetail){
                mGrid.setSelection(clickPos, true);
                backFromDetail = false;
            }
        }
    }

    /**
     * 设置是否有数据时的相关的显示
     * @param visible
     */
    private void _visibleView(boolean visible) {
        if (favNo != null)
            favNo.setVisible(visible);
        if (mGrid != null)
            mGrid.setVisible(!visible);
    }

    @Override
    public void updateDetail(String programTitle,String programDetail) {
        String subDetail = "";
        if(programDetail != null &&programDetail.length() > 85){
            subDetail = programDetail.substring(0,85) + "...";
        }else{
            subDetail = programDetail;
        }
        if(subDetail != null){
            programContent.setText(subDetail);
        }else{
            programContent.setText("");
        }
        programName.setText(programTitle);
    }

    @Override
    public void updateCategoryName(String categoryName) {
        topLable.setText("热门" + categoryName + " Top10");
        if(categoryName.length() == 3){
            moreImage.setSize(180, 50);
        }else if(categoryName.length() == 4){
            moreImage.setSize(200, 50);
        }else{
            moreImage.setSize(160, 50);
        }

    }

    @Override
    public void updateMoreBtnName(String btnName) {
        moreLable.setText("更多" + btnName);
    }
}

interface UpdateProgramDetail{
    void updateDetail(String programName,String programDetail);
    void updateCategoryName(String categoryName);
    void updateMoreBtnName(String btnName);
}
