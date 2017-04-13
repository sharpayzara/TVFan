package tvfan.tv.ui.gdx.searchs;

import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.actions.RotateByAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.luxtone.lib.gdx.OnClickListener;
import com.luxtone.lib.widget.AbsListView;
import com.luxtone.lib.widget.AbsListView.OnItemClickListener;
import com.luxtone.lib.widget.AbsListView.OnItemSelectedChangeListener;
import com.luxtone.lib.widget.CullGroup;
import com.luxtone.lib.widget.Grid;
import com.luxtone.lib.widget.PagingDataObtainedCallback;
import com.luxtone.lib.widget.PagingGrid;
import com.luxtone.lib.widget.PagingGrid.IPagingActionFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.BasePage;
import tvfan.tv.R;
import tvfan.tv.dal.HttpResponse;
import tvfan.tv.dal.HttpResponse.Listener4JSONObject;
import tvfan.tv.dal.LocalData;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.dal.models.ProgramListItem;
import tvfan.tv.lib.Lg;
import tvfan.tv.lib.NetWorkUtils;
import tvfan.tv.lib.Utils;
import tvfan.tv.ui.gdx.widgets.Button;

/**
 * 搜索
 *
 */
public class Page extends BasePage implements OnClickListener{
    private int bgresid;
    private ArrayList<ProgramListItem> programList;// 右侧栏海报列表页数据
    private int totalnumber = 0;
    private String parentCatgId;
    private PagingGrid<ProgramListItem> mGrid;
    private List<ProgramListItem> indexProgramList = new ArrayList<ProgramListItem>();
    private PagingDataObtainedCallback<ProgramListItem> mCallback;
    private Label gdTitle, pageInfo,tipLable;
    private Label mEdit, tipContent,tipContent2;
    private List<String> mNumerKey = new ArrayList<String>();
    private Grid mKeypadGrid;
    private SearchAdapter mSearchAdapter;
    private StringBuffer sb = new StringBuffer("");
    private RemoteData rd;
    private Image bgImg,leftImg;
    private int searchTypes = 0;
    private int pagenow = 0;
    private int totalpage = 0;
    private Button clear, back, allSource, movieSource, tvSource, artsSource, animSource,searchMore;
    private Label searchN;
    Image imageframe;
    private SearchGridAdapter dataAdapter;
    private Timer timer;
    private Task task;
    private Image loadingImage,searchIcon;
    private String currentCheckItem;
    private HashMap checkMap;
    private CullGroup gridGroup;
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        currentCheckItem = "全部";
        timer = new Timer();
        initCodeMap();
        initView();
        initSourceButton();
        initNumericKeypad();
        parentCatgId = bundle.getString("parentCatgId"); // 获取栏目id,可能为""
        if (parentCatgId == null) {
            parentCatgId = "";
        }
        rd = new RemoteData(this.getActivity());

        initProgramList();
    }

    private void initCodeMap() {
        checkMap = new HashMap();
        checkMap.put("全部","");
        checkMap.put("电影","00050000000000000000000000019596");
        checkMap.put("电视剧","00050000000000000000000000019614");
        checkMap.put("综艺","00050000000000000000000000019627");
        checkMap.put("动漫","00050000000000000000000000019633");
    }

    private void initSourceButton() {
        searchIcon = new Image(this);
        searchIcon.setDrawableResource(R.mipmap.search_icon);
        searchIcon.setPosition(1679,930);
        searchIcon.setSize(46,46);
        addActor(searchIcon);

        tipLable = new Label(this);
        tipLable.setText("搜索");
        tipLable.setPosition(1738,933);
        tipLable.setTextSize(40);
        tipLable.setTextColor(android.graphics.Color.parseColor("#646464"));
        addActor(tipLable);


        allSource = new Button(this, 160, 60);
        allSource.setPosition(660, 1080 - 150);
        allSource.getImage().setDrawableResource(R.mipmap.search_all3);
        allSource.setFocusBackGround(R.mipmap.search_all);
        allSource.setUnFocusBackGround(R.mipmap.search_all3);
        allSource.setFocusAble(true);
        allSource.setOnClickListener(this);
        addActor(allSource);

        movieSource = new Button(this, 160, 60);
        movieSource.setPosition(660 + 175, 1080 - 150);
        movieSource.getImage().setDrawableResource(R.mipmap.search_movie2);
        movieSource.setFocusBackGround(R.mipmap.search_movie);
        movieSource.setUnFocusBackGround(R.mipmap.search_movie2);
        movieSource.setFocusAble(true);
        movieSource.setOnClickListener(this);
        addActor(movieSource);

        tvSource = new Button(this, 160, 60);
        tvSource.setPosition(660 + 350, 1080 - 150);
        tvSource.getImage().setDrawableResource(R.mipmap.search_tv2);
        tvSource.setFocusBackGround(R.mipmap.search_tv);
        tvSource.setUnFocusBackGround(R.mipmap.search_tv2);
        tvSource.setFocusAble(true);
        tvSource.setOnClickListener(this);
        addActor(tvSource);

        artsSource = new Button(this, 160, 60);
        artsSource.setPosition(660 + 350 + 175, 1080 - 150);
        artsSource.getImage().setDrawableResource(R.mipmap.search_arts2);
        artsSource.setFocusBackGround(R.mipmap.search_arts);
        artsSource.setUnFocusBackGround(R.mipmap.search_arts2);
        artsSource.setFocusAble(true);
        artsSource.setOnClickListener(this);
        addActor(artsSource);

        animSource = new Button(this, 160, 60);
        animSource.setPosition(660 + 700, 1080 - 150);
        animSource.getImage().setDrawableResource(R.mipmap.search_anim2);
        animSource.setFocusBackGround(R.mipmap.search_anim);
        animSource.setUnFocusBackGround(R.mipmap.search_anim2);
        animSource.setFocusAble(true);
        animSource.setOnClickListener(this);

        addActor(animSource);
        setCatelogVisiable(false);
    }

    public void setCatelogVisiable(boolean isShow){
        allSource.setVisible(isShow);
        movieSource.setVisible(isShow);
        tvSource.setVisible(isShow);
        artsSource.setVisible(isShow);
        animSource.setVisible(isShow);
    }
    public void switchView(boolean isSearch){
        if(isSearch){
            pageInfo.setVisible(true);
            gdTitle.setVisible(false);
            searchMore.setVisible(false);
            setCatelogVisiable(true);
            mGrid.setSize(1920 - 225 - 560, 1080 - 220);
        }else{
            gdTitle.setVisible(true);
            searchMore.setVisible(true);
            setCatelogVisiable(false);
            mGrid.setSize(1920 - 225 - 560, 750);
        }
    }

    private void initView() {

        for (char i = 'A'; i <= 'Z'; i++) {
            mNumerKey.add(String.valueOf(i));
        }
        for (int t = 1; t <= 9; t++) {
            mNumerKey.add(String.valueOf(t));
        }
        mNumerKey.add("0");

        // 初始化背景
        bgImg = new Image(this);
        bgImg.setPosition(0, 0);
        bgImg.setSize(1920, 1080);
        addActor(bgImg);
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

        leftImg = new Image(this);
        leftImg.setPosition(0,0);
        leftImg.setSize(560,1080);
        leftImg.setDrawableResource(R.mipmap.left_bg);
        addActor(leftImg);

        // 初始化页码
        pageInfo = new Label(this);
        pageInfo.setText("0/0");
        pageInfo.setColor(Color.WHITE);
        pageInfo.setAlpha(0.9f);
        pageInfo.setTextSize(40);
        pageInfo.setSize(250, 40);
        pageInfo.setPosition(1475, 1080 - 150);
        pageInfo.setAlignment(Align.right);
        pageInfo.setVisible(false);
        addActor(pageInfo);

        // 初始化right title
        gdTitle = new Label(this);
        gdTitle.setText("大家都在搜");
        gdTitle.setColor(Color.WHITE);
        gdTitle.setAlpha(0.9f);
        gdTitle.setTextSize(35);
        gdTitle.setAlignment(Align.left);
        gdTitle.setPosition(660, 1080-270);
        addActor(gdTitle);

        searchMore = new Button(this,120,40);
        searchMore.getImage().setDrawableResource(R.mipmap.vary_normal);
        searchMore.setPosition(860,1080-270);
        searchMore.setUnFocusBackGround(R.mipmap.vary_normal);
        searchMore.setFocusBackGround(R.mipmap.vary_selected);
        searchMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(Actor actor) {
                requestRecommendTask(false);
            }
        });
        addActor(searchMore);

        // 初始化输入框
        mEdit = new Label(this);
        mEdit.setTextSize(55);
        mEdit.setTextColor(0xffffffff);
        mEdit.setPosition(90, Utils.getY(190));
        mEdit.setAlignment(Align.center);
        mEdit.setSize(380,50);
        addActor(mEdit);

        imageframe = new Image(this);
        imageframe.setDrawableResource(R.drawable.search_frame);
        imageframe.setPosition(45, 1080-230);
        imageframe.setSize(486,145);
        addActor(imageframe);

        // 暂无订购
        searchN = new Label(this);
        searchN.setPosition(1115, 440);
        searchN.setTextSize(50);
        searchN.setColor(Color.WHITE);
        searchN.setText("暂无搜索结果");
        searchN.setAlpha(0.9f);
        searchN.setVisible(false);
        addActor(searchN);
        // 正在搜索

        loadingImage = new Image(this);
        loadingImage.setPosition(1050, 100);
        loadingImage.setSize(100, 100);
        loadingImage.setDrawable(findTextureRegion(R.drawable.new_foucs));
        loadingImage.setFocusAble(false);
        loadingImage.setOrigin(50, 50);
        loadingImage.clearActions();
        loadingImage.setVisible(false);
        addActor(loadingImage);

        tipContent = new Label(this);
        tipContent.setText("支持影片、节目首字母输入");
        tipContent.setTextSize(32);
        tipContent.setTextColor(android.graphics.Color.parseColor("#657591"));
        tipContent.setPosition(80,95);
        addActor(tipContent);

        tipContent2 = new Label(this);
        tipContent2.setText("如HLS搜索“欢乐颂”");
        tipContent2.setTextSize(32);
        tipContent2.setColor(Color.WHITE);
        tipContent2.setTextColor(android.graphics.Color.parseColor("#657591"));
        tipContent2.setPosition(80,55);
        addActor(tipContent2);
        initClearAndBack();

        gridGroup = new CullGroup(this);
        gridGroup.setSize(1920 - 225 - 560, 1080-220);
        gridGroup.setCullingArea(new Rectangle(-20,-20,1920 - 225 - 520, 1080-175));
        gridGroup.setPosition(560+98, 30);
        addActor(gridGroup);
    }

    private void initNumericKeypad() {
        mKeypadGrid = new Grid(this);
        mKeypadGrid
                .setCullingArea(new Rectangle(0, -10, 555 + 190, 555 + 190));
        mKeypadGrid.setCull(false);
        mKeypadGrid.setRowNum(6);
        mKeypadGrid.setGapLength(1);
        mKeypadGrid.setPosition(60, 320);
        mKeypadGrid.setOrientation(Grid.ORIENTATION_VERTICAL);
        mKeypadGrid.setSize(450, 450);
        mKeypadGrid.setItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(Actor arg0, int arg1, AbsListView arg2) {
                // TODO Auto-generated method stub
                pageInfo.setVisible(true);
                searchIcon.setVisible(false);
                tipLable.setVisible(false);
                if (sb.toString().length() < 12) {
                    sb.append(mNumerKey.get(arg1));
                    mEdit.setText(sb.toString());
                    searchTypes = 0;
                    requestDateTask(1, 300, sb.toString(), false);
                    switchView(true);
                }
            }
        });
        mSearchAdapter = new SearchAdapter(this, mNumerKey);
        mKeypadGrid.setAdapter(mSearchAdapter);
        addActor(mKeypadGrid);
        mKeypadGrid.setSelection(0, true);
    }

    private void initClearAndBack() {

        clear = new Button(this, 200, 70);
        clear.setPosition(312, 280);
        clear.getImage().setDrawableResource(R.drawable.search_empty_selected);
        clear.setFocusBackGround(R.drawable.search_empty_search_selected2);
        clear.setUnFocusBackGround(R.drawable.search_empty_selected);
        clear.setFocusAble(true);
        clear.setNextFocusDown("clear");
        clear.setName("clear");
        clear.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(Actor arg0) {
                // TODO Auto-generated method stub
                if (sb.length() != 0) {
                    sb.setLength(0);
                    mEdit.setText(sb.toString());
                    switchView(false);
                    gdTitle.setText("大家都在搜");
                    setCatelogVisiable(false);
                    searchTypes = 0;
                    requestRecommendTask(false);
                    tipLable.setVisible(true);
                    searchIcon.setVisible(true);
                    pageInfo.setVisible(false);
                }
            }
        });
        addActor(clear);
        back = new Button(this, 200, 70);
        back.setPosition(83, 280);
        back.setNextFocusDown("back");
        back.setName("back");
        back.getImage().setDrawableResource(R.drawable.search_delete_normal);
        back.setFocusBackGround(R.drawable.search_delete_selected);
        back.setUnFocusBackGround(R.drawable.search_delete_normal);
        back.setFocusAble(true);
        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(Actor arg0) {
                // TODO Auto-generated method stub
                if (sb.length() != 0) {
                    sb.deleteCharAt(sb.length() - 1);
                    mEdit.setText(sb.toString());
                    searchTypes = 0;
                    if (sb.length() == 0){
                        switchView(false);
                        gdTitle.setText("大家都在搜");
                        gdTitle.setVisible(true);
                        searchMore.setVisible(true);
                        tipLable.setVisible(true);
                        searchIcon.setVisible(true);
                        pageInfo.setVisible(false);
                        requestRecommendTask(false);
                    }else{
                        requestDateTask(1, 300, sb.toString(), false);
                    }
                }
            }
        });
        addActor(back);
    }

    private void seaechNull(boolean flag) {
        if (mGrid != null)
            mGrid.setVisible(!flag);
        if (searchN != null)
            searchN.setVisible(flag);
        if(flag && pageInfo!=null)
            pageInfo.setText("0/0");
    }

    private void initProgramList() {

        // 初始化Grid
        mGrid = new PagingGrid<ProgramListItem>(this, 100);
        mGrid.addInterceptKey(Keys.DOWN);
        mGrid.setSize(1920 - 225 - 560, 750);
        mGrid.setPosition(0, 0);
        mGrid.setRowNum(5);
        mGrid.setCull(false);
        mGrid.setOrientation(Grid.ORIENTATION_VERTICAL);
        mGrid.setGapLength(5);
        mGrid.setAdjustiveScrollLengthForBackward(450);
        mGrid.setAdjustiveScrollLengthForForward(450);
        dataAdapter = new SearchGridAdapter(this, mGrid, totalnumber);
        mGrid.setPagingAdapter(dataAdapter);
        mGrid.setPagingActionFactory(new IPagingActionFactory<ProgramListItem>() {
            @Override
            public void obtainData(final int page, final int pageSize,
                                   final PagingDataObtainedCallback<ProgramListItem> callback) {
                mCallback = callback;
                //	requestDateTask(page, pageSize, sb.toString(), true);
                requestRecommendTask(true);
            }

            @Override
            public void showLoading(boolean b) {
                // 是否正在拉去数据
            }
        });
        mGrid.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Actor actor, int pos,
                                    AbsListView abslistview) {
                // 跳转详情页 ....
                if (indexProgramList.size() > pos) {
                    ProgramListItem programListItem = indexProgramList.get(pos);
                    Bundle options = new Bundle();
                    options.putString("id", programListItem.getId());
                    options.putString("name", programListItem.getPostName());
                    Page.this.doAction(ACTION_NAME.OPEN_DETAIL, options);
                }
            }
        });
        mGrid.setOnItemSelectedChangeListener(new OnItemSelectedChangeListener() {

            @Override
            public void onSelectedChanged(int position, Actor actor) {
                // TODO Auto-generated method stub
                updatePageInfo(position, totalnumber, 5);
            }
        });
        mGrid.obtainData();
        gridGroup.addActor(mGrid);
    }


    public void requestRecommendTask(final Boolean ifPull) {
        if (!ifPull) {
            if (mGrid.isVisible())
                mGrid.setVisible(false);
            if (searchN.isVisible())
                searchN.setVisible(false);

        }
        if (task != null) {
            task.cancel();
        }
        if (timer != null) {
            timer.stop();
            timer.clear();
        }
        task = new Task() {

            @Override
            public void run() {
                requestRecommendDate(ifPull);
            }
        };
        timer.scheduleTask(task, 0.6f);
        timer.start();
    }

    public void requestDateTask(final int pagenum, final int pagesize,
                                final String value, final Boolean ifPull) {
        if (!ifPull) {
            if (mGrid.isVisible())
                mGrid.setVisible(false);
            if (searchN.isVisible())
                searchN.setVisible(false);

        }
        if (task != null) {
            task.cancel();
        }
        if (timer != null) {
            timer.stop();
            timer.clear();
        }
        task = new Task() {

            @Override
            public void run() {
                requestDate(pagenum, pagesize, value, ifPull);
            }
        };
        timer.scheduleTask(task, 0.2f);
        timer.start();
    }



    /**
     * 获取推荐列表
     *
     */
    public void requestRecommendDate(final Boolean ifPull) {
        RotateByAction rotateAction = Actions.rotateBy(-360, 0.8f);
        RepeatAction epeatAction = Actions.repeat(RepeatAction.FOREVER,
                rotateAction);
        loadingImage.setVisible(true);
        loadingImage.toFront();
        loadingImage.clearActions();
        loadingImage.addAction(epeatAction);
        parentCatgId = (String) checkMap.get(currentCheckItem);
        rd.getRecommendList(new HttpResponse.Listener4JSONArray() {
            @Override
            public void onResponse(JSONArray response) throws JSONException {
                try {
                    if(programList == null)
                        programList = new ArrayList<ProgramListItem>();
                    else
                        programList.clear();

                    totalnumber = response.length();
                    for (int i = 0; i < totalnumber; i++) {
                        JSONObject jsonobject = (JSONObject) response.get(i);
                        ProgramListItem program = new ProgramListItem();
                        program.setId(jsonobject.optString("id", ""));
                        program.setPostImg(jsonobject.optString("picture", ""));
                        program.setPostName(jsonobject.optString("name", ""));
                        program.setCurrentNum(jsonobject.optString(
                                "currentNum", ""));
                        program.setCornerPrice(jsonobject.optString(
                                "cornerPrice", ""));
                        program.setCornerType(jsonobject.optString(
                                "cornerType", ""));
                        programList.add(program);
                    }
                    if(indexProgramList.size() != 0)
                        indexProgramList.clear();
                    indexProgramList.addAll(programList);
                    loadingImage.clearActions();
                    loadingImage.setVisible(false);
                    dataAdapter.setTotalCount(totalnumber);
                    mCallback.onDataObtained(1, programList);
                    if (programList.size() == 0) {
                        seaechNull(true);
                    } else {
                        seaechNull(false);
                    }
                } catch (JSONException e) {
                    loadingImage.clearActions();
                    loadingImage.setVisible(false);
                    if (!ifPull) {
                        seaechNull(true);
                    }
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Lg.e(TAG, "getSearchList : " + errorMessage);
                NetWorkUtils.handlerError(errorMessage, Page.this);
            }
        });
    }

    /**
     * 获取电影列表
     *
     */
    public void requestDate(final int pagenum, final int pagesize,
                            final String value, final Boolean ifPull) {
        RotateByAction rotateAction = Actions.rotateBy(-360, 0.8f);
        RepeatAction epeatAction = Actions.repeat(RepeatAction.FOREVER,
                rotateAction);
        loadingImage.setVisible(true);
        loadingImage.toFront();
        loadingImage.clearActions();
        loadingImage.addAction(epeatAction);
        parentCatgId = (String) checkMap.get(currentCheckItem);
        rd.getSearchList(new Listener4JSONObject() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(programList == null)
                        programList = new ArrayList<ProgramListItem>();
                    else
                        programList.clear();

                    JSONArray jsonarr = response.getJSONArray("programList");
                    totalnumber = jsonarr.length();
                    for (int i = 0; i < totalnumber; i++) {
                        JSONObject jsonobject = (JSONObject) jsonarr.get(i);
                        ProgramListItem program = new ProgramListItem();
                        program.setId(jsonobject.optString("id", ""));
                        program.setPostImg(jsonobject.optString("image", ""));
                        program.setPostName(jsonobject.optString("name", ""));
                        program.setCurrentNum(jsonobject.optString(
                                "currentNum", ""));
                        program.setCornerPrice(jsonobject.optString(
                                "cornerPrice", ""));
                        program.setCornerType(jsonobject.optString(
                                "cornerType", ""));
                        programList.add(program);
                    }
                    if(indexProgramList.size() != 0)
                        indexProgramList.clear();
                    indexProgramList.addAll(programList);
                    if (pagenum == 1) {
                        updatePageInfo(pagenum, totalnumber, 5);
                    }
                    loadingImage.clearActions();
                    loadingImage.setVisible(false);
                    dataAdapter.setTotalCount(totalnumber);
                    mCallback.onDataObtained(pagenum, programList);
                    if (programList.size() == 0) {
                        seaechNull(true);
                    } else {
                        seaechNull(false);
                    }
                } catch (JSONException e) {
                    loadingImage.clearActions();
                    loadingImage.setVisible(false);
                    if (!ifPull) {
                        seaechNull(true);
                    }
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (mCallback != null)
                    mCallback.onDataObtainedFailed(pagenum);
                Lg.e(TAG, "getSearchList : " + errorMessage);
                NetWorkUtils.handlerError(errorMessage, Page.this);
            }
        }, value, pagenum, pagesize, parentCatgId, searchTypes);
    }

    public void updatePageInfo(int pos, int total, int pagenums) {
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

    @Override
    protected void onResumeTextures() {
        super.onResumeTextures();
        Utils.resetImageSource(bgImg, R.drawable.bj);
        Utils.resetImageSource(imageframe, R.drawable.search_textline);
        clear.getImage().setDrawableResource(R.drawable.search_clear);
        back.getImage().setDrawableResource(R.drawable.search_clear);
    }

    @Override
    public void recyclePage() {
        super.recyclePage();
    }

    @Override
    public void onDispose() {
        super.onDispose();
    }

    @Override
    public void onPause() {
        super.onPause();
    };

    @Override
    public boolean onBackKeyDown() {
        return super.onBackKeyDown();
    }

    @Override
    public void onClick(Actor actor) {
        flushButtonState();
        if(actor == allSource){
            currentCheckItem = "全部";
            allSource.getImage().setDrawableResource(R.mipmap.search_all3);
            allSource.setUnFocusBackGround(R.mipmap.search_all3);
        }else if(actor == movieSource){
            currentCheckItem = "电影";
            movieSource.setUnFocusBackGround(R.mipmap.search_movie3);
            movieSource.getImage().setDrawableResource(R.mipmap.search_movie3);
        }else if(actor == tvSource){
            currentCheckItem = "电视剧";
            tvSource.getImage().setDrawableResource(R.mipmap.search_tv3);
            tvSource.setUnFocusBackGround(R.mipmap.search_tv3);
        }else if(actor == artsSource){
            currentCheckItem = "综艺";
            artsSource.setUnFocusBackGround(R.mipmap.search_art3);
            artsSource.getImage().setDrawableResource(R.mipmap.search_art3);
        }else if(actor == animSource){
            currentCheckItem = "动漫";
            animSource.getImage().setDrawableResource(R.mipmap.search_anim3);
            animSource.setUnFocusBackGround(R.mipmap.search_anim3);
        }
        requestDateTask(1, 300, sb.toString(), false);
    }
    void flushButtonState(){
        movieSource.setUnFocusBackGround(R.mipmap.search_movie2);
        tvSource.setUnFocusBackGround(R.mipmap.search_tv2);
        allSource.setUnFocusBackGround(R.mipmap.search_all2);
        artsSource.setUnFocusBackGround(R.mipmap.search_arts2);
        animSource.setUnFocusBackGround(R.mipmap.search_anim2);
        allSource.getImage().setDrawableResource(R.mipmap.search_all2);
        movieSource.getImage().setDrawableResource(R.mipmap.search_movie2);
        tvSource.getImage().setDrawableResource(R.mipmap.search_tv2);
        artsSource.getImage().setDrawableResource(R.mipmap.search_arts2);
        animSource.getImage().setDrawableResource(R.mipmap.search_anim2);
    }
}