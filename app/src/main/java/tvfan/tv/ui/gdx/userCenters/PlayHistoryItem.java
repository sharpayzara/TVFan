package tvfan.tv.ui.gdx.userCenters;

/**
 *  desc  播放历史页面
 *  @author  yangjh
 *  created at  16-4-20 下午3:36
 */

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

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
import com.luxtone.lib.widget.CullGroup;
import com.luxtone.lib.widget.Grid;

import java.util.ArrayList;
import java.util.HashMap;

import tvfan.tv.App;
import tvfan.tv.AppGlobalVars;
import tvfan.tv.BasePage.ACTION_NAME;
import tvfan.tv.R;
import tvfan.tv.dal.PlayRecordHelpler;
import tvfan.tv.dal.models.MPlayRecordInfo;
import tvfan.tv.lib.Lg;

public class PlayHistoryItem extends Group {

    public PlayHistoryItem(Page page, Context context) {
        super(page);
        setSize(1520, 1080);
        this.context = context;
        this.page = (tvfan.tv.ui.gdx.userCenters.Page) page;
        _initView();
    }

    private void _initView() {
        gridcullGroup2 = new CullGroup(getPage());
        gridcullGroup2.setSize(211, 63);
        gridcullGroup2.setPosition(60, 1080-245);
        gridcullGroup2.setCullingArea(new Rectangle(0, 0, 211,
                63));

        delButton = new Image(getPage());
        delButton.setPosition(0,0);
        delButton.setSize(211,63);
        delButton.setFocusAble(true);
        delButton.setDrawableResource(R.drawable.delete_all_normal);
        gridcullGroup2.addActor(delButton);
        delButton.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChanged(Actor actor, boolean b) {
                if(b){
                    delButton.setDrawableResource(R.drawable.delete_all_selected);
                    delFlag = true;
                }else{
                    delButton.setDrawableResource(R.drawable.delete_all_normal);
                    delFlag = false;
                }
            }
        });
        delButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(Actor actor) {
                DeleteAllHisDialog deleteDialog = new DeleteAllHisDialog(getPage(), context);
                deleteDialog._initData(PlayHistoryItem.this);
                deleteDialog.show();
            }
        });

        addActor(gridcullGroup2);
        iconImage = new Image(getPage());
        iconImage.setPosition(1220,1080-144);
        iconImage.setSize(46,46);
        iconImage.setDrawableResource(R.mipmap.play_icon);
        addActor(iconImage);


        tipLable = new Label(getPage());
        tipLable.setText("播放记录");
        tipLable.setPosition(1280,1080-140);
        tipLable.setTextSize(40);
        tipLable.setTextColor(Color.parseColor("#636361"));
        addActor(tipLable);

        // 初始化页码
        pageInfo = new Label(getPage());
        pageInfo.setTextColor(Color.parseColor("#ffffff"));
        pageInfo.setAlpha(0.9f);
        pageInfo.setTextSize(40);
        pageInfo.setSize(200, 60);
        pageInfo.setPosition(1290, 1080-225);
        addActor(pageInfo);
		/*image = new Image(getPage());
		image.setSize(75, 50);
		image.setPosition(395, 926);
		image.setDrawableResource(R.drawable.btn);
		addActor(image);*/

        bootLabel3 = new Label(getPage());
        bootLabel3.setPosition(715-400, 1080-225);
        bootLabel3.setTextColor(Color.parseColor("#667491"));
        bootLabel3.setTextSize(32);
        bootLabel3.setAlpha(0.7f);
        bootLabel3.setText("按菜单键可删除相关播放历史");
        addActor(bootLabel3);

        gridcullGroup = new CullGroup(getPage());
        gridcullGroup.setSize(1400, 780);
        gridcullGroup.setPosition(50, 0);
        gridcullGroup.setCullingArea(new Rectangle(-20, 0, 1440,
                800));
        addActor(gridcullGroup);

        hisNo = new Image(getPage());
        hisNo.setPosition(432, 516);
        hisNo.setSize(630,120);
        hisNo.setDrawableResource(R.mipmap.no_records);
        hisNo.setVisible(false);
        addActor(hisNo);
    }

    public String _deleteName() {
        return "\"" + allFilmList.get(deletePos).getPlayerName() + "\"";
    }

    public void _updateGrid() {
        if (mPlayRecordOpt == null)
            mPlayRecordOpt = new PlayRecordHelpler(context);
        mPlayRecordOpt
                .deletePlayRecordBy(allFilmList.get(deletePos).getEpgId());
        allFilmList.remove(deletePos);
        _updatePageInfo(0, allFilmList.size(), 6);
        if (allFilmList != null && allFilmList.size() > 0) {
            int pos = deletePos < allFilmList.size() - 1 ? deletePos
                    : allFilmList.size() - 1;
            mGrid.notifyDataChanged();
            mGrid.setSelection(pos, true);
        } else {
            _visibleView(true);
            page.setMenuGroupFouce(0);
        }

    }

    public void _deleteAll(){
        if (mPlayRecordOpt == null)
            mPlayRecordOpt = new PlayRecordHelpler(context);
        for(MPlayRecordInfo entity : allFilmList){
            mPlayRecordOpt
                    .deletePlayRecordBy(entity.getEpgId());

        }
        allFilmList.clear();
        _visibleView(true);
        page.setMenuGroupFouce(0);
    }

    public void _initData() {
        if (mPlayRecordOpt == null)
            mPlayRecordOpt = new PlayRecordHelpler(context);
        allFilmList = mPlayRecordOpt.getAllPlayRecord();
        for(int i = 0; i < allFilmList.size(); i++){
            if(allFilmList.get(i).getEpgId().equals("")){
                allFilmList.remove(i);
            }
        }
        totalnumber = allFilmList.size();
        _updatePageInfo(0, totalnumber, 6);
        Lg.e("mv", "allFilmList"+allFilmList.size());
        if (allFilmList.isEmpty()) {
            // 暂无消费记录
            _visibleView(true);
            return;
        }
        _addGrid();
    }

    private void _visibleView(boolean visible) {
        if (hisNo != null)
            hisNo.setVisible(visible);
        if(visible){
            delButton.setVisible(false);
            bootLabel3.setVisible(false);
            pageInfo.setVisible(false);
        }else{
            delButton.setVisible(true);
            bootLabel3.setVisible(true);
            pageInfo.setVisible(true);
        }
        if (mGrid != null)
            mGrid.setVisible(!visible);
    }

    private void _addGrid() {
        if (mGrid == null) {
            mGrid = new Grid(getPage());
            mGrid.setPosition(0, 0);
            mGrid.setSize(1400, 770);
            mGrid.setGapLength(5);
            mGrid.setOrientation(Grid.ORIENTATION_VERTICAL);
            mGrid.setRowNum(6);
            //mGrid.setCullingArea(new Rectangle(-60, 0, 1230 + 100, 890));
            dataAdapter = new PlayHistoryGridAdapter(getPage());
            dataAdapter.setData(allFilmList);
            mGrid.setAdapter(dataAdapter);
            if(backFromDetail){
                mGrid.setSelection(clickPos, true);
                backFromDetail = false;
            }
            mGrid.setAdjustiveScrollLengthForBackward(50);
            mGrid.setAdjustiveScrollLengthForForward(50);
            mGrid.setItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(Actor itemView, int position,
                                        AbsListView grid) {
                    Lg.e("PlayHistoryItem", "clickPos====="+clickPos);
                    clickPos = position;
                    backFromDetail = true;

                    MPlayRecordInfo mPlayRecordInfo = allFilmList.get(position);
                    Bundle options = new Bundle();

                    options.putString("id", allFilmList.get(position)
                            .getEpgId());
                    page.doAction(ACTION_NAME.OPEN_DETAIL, options);


                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("UID", AppGlobalVars.getIns().USER_ID);
                    map.put("PROGRAM_ID", mPlayRecordInfo.getEpgId());
                    map.put("WAY_NAME", "历史-"+ mPlayRecordInfo.getPlayerName());
                    map.put("U_I_N",
                            AppGlobalVars.getIns().USER_ID + "|"
                                    +  mPlayRecordInfo.getEpgId() + "|"
                                    + mPlayRecordInfo.getPlayerName());
//					MobclickAgent.onEvent(page.getActivity().getApplicationContext(),
//							"event_detail", map);
                    Lg.i("TAG", "历史："+ mPlayRecordInfo.getPlayerName());
                }
            });

            mGrid.setOnItemSelectedChangeListener(new OnItemSelectedChangeListener() {

                @Override
                public void onSelectedChanged(int position, Actor actor) {
                    // TODO Auto-generated method stub
                    deletePos = position;
                    _updatePageInfo(position, totalnumber, 6);
                }
            });
            gridcullGroup.addActor(mGrid);
            _visibleView(false);
        } else {
            _visibleView(false);
            dataAdapter.setData(allFilmList);
            mGrid.setAdapter(dataAdapter);
            if(backFromDetail){
                mGrid.setSelection(clickPos, true);
                backFromDetail = false;
            }
        }
    }

    @Override
    public void onResume() {
        //image.setDrawableResource(R.drawable.btn);
        //bootLabel.setText("按");
        //bootLabel2.setText("菜单");
        bootLabel3.setText("按菜单键可删除收藏影片");
        pageInfo.setText(pagenow + "/" + totalpage);
        hisNo.setDrawableResource(R.mipmap.no_collect);
        super.onResume();
    }

    /**
     * 更新页码显示
     * @param pos
     * @param total
     * @param pagenums
     */
    public void _updatePageInfo(int pos, int total, int pagenums) {
        pagenow = pos/pagenums+1;
        totalpage = total/pagenums;
        if (pagenums >= total) {
            totalpage = 1;
        } else {
            if (total%pagenums>0) {
                totalpage = totalpage + 1;
            }
        }
        pageInfo.setText(pagenow+"/"+totalpage);
    }

    private Label pageInfo, bootLabel3,tipLable;
    private Image iconImage,delButton,moreImage,hisNo;
    private PlayHistoryGridAdapter dataAdapter;
    private Grid mGrid;
    Context context;
    private ArrayList<MPlayRecordInfo> allFilmList;
    private int totalnumber = 0;
    private int deletePos = -1;
    private int pagenow = 0;
    private int totalpage = 0;
    private CullGroup gridcullGroup,gridcullGroup2;
    private PlayRecordHelpler mPlayRecordOpt;
    private tvfan.tv.ui.gdx.userCenters.Page page;
    public boolean delFlag = false;
    private int clickPos = -1;//最近观看列表点击的position
    private boolean backFromDetail = false;//mGid是否已经点击了
}
