package tvfan.tv.ui.gdx.userCenters;

/**
 *  desc  收藏页面
 *  @author  yangjh
 *  created at  16-4-20 下午3:36
 */


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import com.badlogic.gdx.Gdx;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import tvfan.tv.App;
import tvfan.tv.AppGlobalVars;
import tvfan.tv.BasePage.ACTION_NAME;
import tvfan.tv.R;
import tvfan.tv.dal.HttpResponse.Listener4JSONObject;
import tvfan.tv.dal.LocalData;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.dal.models.ProgramListItem;
import tvfan.tv.lib.Lg;
import tvfan.tv.ui.gdx.widgets.CIBNLoadingView;
//import com.umeng.analytics.MobclickAgent;

public class FavoritesItem extends Group {

    private Image iconImage;
    private Label tipLable;

    public FavoritesItem(Page page, Context context) {
        super(page);
        setSize(1520, 1080);
        this.context = context;
        this.page = (tvfan.tv.ui.gdx.userCenters.Page) page;
        _localData = new LocalData(context);
        _rd = new RemoteData(context);
        _initView();
    }

    private void _initView() {

        gridcullGroup2 = new CullGroup(getPage());
        gridcullGroup2.setSize(211, 63);
        gridcullGroup2.setPosition(63, 1080-245);
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
                DeleteAllFavDialog deleteDialog = new DeleteAllFavDialog(getPage(), context);
                deleteDialog._initData(FavoritesItem.this);
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
        tipLable.setText("我的收藏");
        tipLable.setPosition(1280,1080-140);
        tipLable.setTextSize(40);
        tipLable.setTextColor(android.graphics.Color.parseColor("#636361"));
        addActor(tipLable);

		/*image = new Image(getPage());
		image.setSize(75, 50);
		image.setPosition(195, 926);
		image.setDrawableResource(R.drawable.btn);
		addActor(image);*/

        bootLabel3 = new Label(getPage());
        bootLabel3.setPosition(715-400, 1080-225);
        bootLabel3.setTextColor(android.graphics.Color.parseColor("#667491"));
        bootLabel3.setTextSize(32);
        bootLabel3.setAlpha(0.7f);
        bootLabel3.setText("按菜单键可删除相关收藏历史");
        addActor(bootLabel3);

        // 初始化页码
        pageInfo = new Label(getPage());
        pageInfo.setTextColor(android.graphics.Color.parseColor("#ffffff"));
        pageInfo.setAlpha(0.9f);
        pageInfo.setTextSize(40);
        pageInfo.setSize(200, 60);
        pageInfo.setPosition(1290, 1080-225);
        addActor(pageInfo);

        gridcullGroup = new CullGroup(getPage());
        gridcullGroup.setSize(1400, 780);
        gridcullGroup.setPosition(50, 0);
        gridcullGroup.setCullingArea(new Rectangle(-20, 0, 1440,
                800));
        addActor(gridcullGroup);

        // 正在搜索
        loadingview = new CIBNLoadingView(getPage());
        loadingview.setVisible(false);
        addActor(loadingview);

        favNo = new Image(getPage());
        favNo.setPosition(287, 514);
        favNo.setSize(1024,120);
        favNo.setVisible(false);
        favNo.setDrawableResource(R.mipmap.no_collect);
        addActor(favNo);

    }

    /**
     * 更新本地数据库剧集
     */
    private void _updateFavTable(String programSeriesId, String updateNum) {
        ContentValues values = new ContentValues();
        values.put("latestEpisode", updateNum);
        _localData.execUpdate(FAVORITE_TABLE, values, "programSeriesId=?",
                new String[] { programSeriesId });
    }

    /**
     * delete本地数据库剧集
     */
    private void _deleteFav(String programSeriesId) {
        _localData.execDelete(FAVORITE_TABLE, "programSeriesId=?",
                new String[] { programSeriesId });
    }

    /**
     * 获取相关数据对应的剧集---本地
     * @return
     */
    public HashMap<String, String> _favoriteFromDb() {
        fMap = new HashMap<String, String>();
        Cursor cursor = _localData.runQuery("SELECT * FROM FAVORITE_TAB", null);
        while (cursor.moveToNext()) {
            String programSeriesId = cursor.getString(cursor
                    .getColumnIndex("programSeriesId"));
            String latestEpisode = cursor.getString(cursor
                    .getColumnIndex("latestEpisode"));
            // 将查询到的结果加入到集合中
            fMap.put(programSeriesId, latestEpisode);

        }
        cursor.close();
        //联网获取相关的数据
        _requestData();
        return fMap;
    }

    /**
     * 初始化我的收藏节目的列表
     */
    private void _initGrid() {

        if (mGrid == null) {
            mGrid = new Grid(getPage());
            mGrid.setPosition(0, 0);
            mGrid.setSize(1400, 780);
            mGrid.setGapLength(5);
            mGrid.setOrientation(Grid.ORIENTATION_VERTICAL);
            mGrid.setRowNum(6);
            mGrid.setCullingArea(new Rectangle(0, 0, 1400, 780));
            dataAdapter = new FavoritesGridAdapter(getPage());
            dataAdapter.setData(programList);
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
                    clickPos = position;
                    backFromDetail = true;

                    Bundle options = new Bundle();
                    ProgramListItem programListItem = programList.get(position);
                    options.putString("id", programList.get(position).getId());
                    page.doAction(ACTION_NAME.OPEN_DETAIL, options);
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("UID", AppGlobalVars.getIns().USER_ID);
                    map.put("PROGRAM_ID", programListItem.getId());
                    map.put("WAY_NAME", "收藏-"+ programListItem.getPostName());
                    map.put("U_I_N",
                            AppGlobalVars.getIns().USER_ID + "|"
                                    +  programListItem.getId() + "|"
                                    + programListItem.getPostName());
//					MobclickAgent.onEvent(page.getActivity().getApplicationContext(),
//							"event_detail", map);
                    Lg.i("TAG", "收藏："+ programListItem.getPostName());
                }
            });
            mGrid.setOnItemSelectedChangeListener(new OnItemSelectedChangeListener() {

                @Override
                public void onSelectedChanged(int position, Actor actor) {
                    // TODO Auto-generated method stub
                    if(programList == null || programList.size() == 0){
                        return;
                    }
                    if (programList.get(position).getMark().equals("0")) {
                        _updateFavTable(programList.get(position).getId(),
                                programList.get(position).getCurrentNum());
                        _Griditem = (FavoritesGridItem) actor;
                        _Griditem.setNewImageGone(false);
                    }

                    _updatePageInfo(position, totalnumber, 6);
                    deletePos = position;
                }
            });
            mGrid.setScrollStatusListener(new ScrollStatusListener() {

                @Override
                public void onScrolling(float arg0, float arg1) {
                }

                @Override
                public void onScrollStop(float arg0, float arg1) {
                    mGrid.setCull(false);
                }

                @Override
                public void onScrollStart(float arg0, float arg1) {
                    mGrid.setCull(true);
                }
            });
            gridcullGroup.addActor(mGrid);
            _visibleView(false);
        } else {
            _visibleView(false);
            dataAdapter.setData(programList);
            mGrid.setAdapter(dataAdapter);
            if(backFromDetail){
                mGrid.setSelection(clickPos, true);
                backFromDetail = false;
            }
        }
    }

    /**
     * 设置是否有收藏时的相关的显示
     * @param visible
     */
    private void _visibleView(boolean visible) {
        if (favNo != null)
            favNo.setVisible(visible);

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

    public String _DeleteName() {
        if(deletePos == -1)
            deletePos = 0;
        return "\"" + programList.get(deletePos).getPostName() + "\"";
    }

    public void _updateGrid() {
        _cancelFav(programList.get(deletePos).getId());
    }

    /**
     * 进行收藏删除的时候进行数据的重新请求，并进行后续的相关操作
     * @param programSeriesId
     */
    private void _cancelFav(String programSeriesId) {
        _rd.cancelFavorite(programSeriesId, new Listener4JSONObject() {

            @Override
            public void onResponse(JSONObject response) {
                if (response == null) {
                    return;
                }
                try {
                    boolean msg = response.optBoolean("msg");
                    if (msg) {
                        Gdx.app.postRunnable(new Runnable() {

                            @Override
                            public void run() {
                                programList.remove(deletePos);
                                _updatePageInfo(0, programList.size(), 6);
                                if (programList != null
                                        && programList.size() > 0) {
                                    int pos = deletePos < programList.size() - 1 ? deletePos
                                            : programList.size() - 1;


                                    mGrid.notifyDataChanged();
                                    mGrid.setSelection(pos, true);

                                } else {
                                    _visibleView(true);
                                    page.setMenuGroupFouce(1);
                                }
                            }
                        });
                        // 更新本地数据库,删除取消收藏的记录
                        _deleteFav(programList.get(deletePos).getId());
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
     * 联网获取我的收藏的相关数据
     */
    public void _requestData() {
        _rd.getFavoritesList(new Listener4JSONObject() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (programList == null)
                        programList = new ArrayList<ProgramListItem>();
                    else
                        programList.clear();
                    JSONArray jsonarr = response.getJSONArray("programList");
                    int count = jsonarr.length();
                    for (int i = 0; i < count; i++) {
                        ProgramListItem program = new ProgramListItem();
                        JSONObject jsonobject = (JSONObject) jsonarr.get(i);
                        String id = jsonobject.optString("id", "");
                        String currentNum = jsonobject.optString("currentNum",
                                "1");
                        program.setId(id);
                        program.setPostName(jsonobject.optString("name", ""));
                        program.setPostImg(jsonobject.optString("image", ""));
                        program.setCurrentNum(currentNum);
                        program.setCornerPrice(jsonobject.optString(
                                "cornerPrice", "0"));
                        program.setCornerType(jsonobject.optString(
                                "cornerType", "0"));
                        if (fMap != null && fMap.size() > 0) {
                            if (fMap != null
                                    && !fMap.isEmpty()
                                    && fMap.get(id) != null
                                    && !fMap.get(id).equals("")
                                    && !currentNum.equals("")
                                    && Math.abs(Integer.parseInt(fMap.get(id))) < Integer
                                    .parseInt(currentNum)) {
                                program.setMark("0");
                            } else {
                                program.setMark("1");
                            }
                        }

                        programList.add(program);
                    }
                    loadingview.setVisible(false);
                    totalnumber = programList.size();
                    Gdx.app.postRunnable(new Runnable() {

                        @Override
                        public void run() {
                            _updatePageInfo(0, totalnumber, 6);
                            if (programList.isEmpty()) {//收藏为空的时候
                                // 暂无收藏
                                _visibleView(true);
                                page.setMenuGroupFouce(1);
                                return;
                            }
                            _initGrid();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    loadingview.setVisible(false);
                    _visibleView(true);
                    programList.clear();
                    programList = null;
                }
            }

            @Override
            public void onError(String errorMessage) {
            }
        });
    }

    /**
     * 进行页码的更新
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

    public void _deleteAll(){
        if(programList == null || programList.size() == 0){
            return;
        }
        String programId = "";
        for (ProgramListItem item : programList){
            programId += item.getId() + ",";
        }
        programId = programId.substring(0,programId.length()-1);
        _rd.cancelFavorite(programId, new Listener4JSONObject() {

            @Override
            public void onResponse(JSONObject response) {
                if (response == null) {
                    return;
                }
                try {
                    boolean msg = response.optBoolean("msg");
                    if (msg) {
                        Gdx.app.postRunnable(new Runnable() {

                            @Override
                            public void run() {
                                programList.clear();
                                _updatePageInfo(0, 0, 6);
                                _visibleView(true);
                                page.setMenuGroupFouce(1);
                            }
                        });
                        // 更新本地数据库,删除取消收藏的记录
                        _deleteFav(programList.get(deletePos).getId());
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

    @Override
    public void onResume() {
        bootLabel3.setText("按菜单键可删除收藏影片");
        pageInfo.setText(pagenow + "/" + totalpage);
        favNo.setDrawableResource(R.mipmap.no_collect);
        super.onResume();
    }

    private Label pageInfo, bootLabel3;
    private Image favNo,delButton;
    private int pagenow = 0;
    private int totalpage = 0;
    Context context;
    public ArrayList<ProgramListItem> programList = new ArrayList<ProgramListItem>();// 右侧栏海报列表页数据
    private int totalnumber = 0;
    private LocalData _localData;
    private final static String FAVORITE_TABLE = "FAVORITE_TAB"; // 我的收藏记录本地数据库
    HashMap<String, String> fMap;
    private RemoteData _rd;
    private CullGroup gridcullGroup,gridcullGroup2;
    private FavoritesGridItem _Griditem;
    public Grid mGrid;
    private int deletePos = -1;
    private tvfan.tv.ui.gdx.userCenters.Page page;
    private FavoritesGridAdapter dataAdapter;
    private CIBNLoadingView loadingview;
    public boolean delFlag = false;
    private int clickPos = -1;//收藏列表点击的position
    private boolean backFromDetail = false;//mGid是否已经点击了

}
