package tvfan.tv.ui.gdx.programDetail.group;

import android.os.Bundle;
import android.text.TextUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.OnClickListener;
import com.luxtone.lib.gdx.OnFocusChangeListener;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.AbsListView;
import com.luxtone.lib.widget.CullGroup;
import com.luxtone.lib.widget.Grid;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.AppGlobalVars;
import tvfan.tv.BasePage;
import tvfan.tv.R;
import tvfan.tv.dal.HttpResponse;
import tvfan.tv.dal.PlayRecordHelpler;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.dal.models.MPlayRecordInfo;
import tvfan.tv.ui.gdx.programDetail.adapter.MusicEpisodeAdapter;
import tvfan.tv.ui.gdx.programDetail.adapter.OtherVarietyTagAdapter;
import tvfan.tv.ui.gdx.widgets.TVFANLabel;

/**
 * 音乐和纪录片的节目集列表
 * Created by ddd on 2016/4/25.
 */
public class MusicEpisodeGroup extends Group implements OnFocusChangeListener, OnClickListener {
    private Page page;
    private JSONArray episodeJSONArray;
    private Grid episodeGrid;//节目集列表
    private CullGroup pagingCull;
    private CullGroup episodeCull;
    private CullGroup episodeGridCull;
    private Image pagingBack;
    private Image logo;
    private Image episodeBack;
    private Image episodeLine;
    private Image nextPage;
    private Image lastPage;
    private TVFANLabel direction, pagingNation;
    private OtherVarietyTagAdapter epsiodeAdapter;

    private ArrayList<String> nameList;
    private ArrayList<String> timeList;
    private int pagingNum = 1; // 记录分页列表的position
    private int totalNum = 1;
    private int totalPagingNum = 1;
    private String cpId;//源ID
    private String programSeriesId;//节目ID
    private String type;//类型
    private boolean isCreateGroup = true;

    private JSONObject detailObject;
    private RemoteData rd;

    public MusicEpisodeGroup(Page page) {
        super(page);

        this.page = page;
        rd = new RemoteData();
        setSize(AppGlobalConsts.APP_WIDTH, 368.0f);
        setPosition(0.0f, 0.0f);
    }

    public void setCpId (String cpId) {
        this.cpId = cpId;

    }

    public void setProgramSeriesId (String programSeriesId) {
        this.programSeriesId = programSeriesId;
    }

    public void setTotalNum (int totalNum) {
        this.totalNum = totalNum;
        initTotalPagingNum();
    }

    public void setType(String type) {
        this.type = type;
        if("纪录片".equals(type))
            logo.setDrawableResource(R.mipmap.documentary);
        else if("音乐".equals(type))
            logo.setDrawableResource(R.mipmap.music);
        else if("游戏".equals(type))
            logo.setDrawableResource(R.mipmap.game);
        else if("综艺".equals(type))
            logo.setDrawableResource(R.mipmap.variety);
        else if("体育".equals(type))
            logo.setDrawableResource(R.mipmap.sports);
        else if("短视频".equals(type))
            logo.setDrawableResource(R.mipmap.short_video);
        else if("微电影".equals(type))
            logo.setDrawableResource(R.mipmap.micro_film);
    }

    public void setDetailObject(JSONObject detailObject) {
        this.detailObject = detailObject;
    }

    /**
     * 设置节目集的列表集合
     * @param episodeArray
     */
    public void setEpisodeJSONArray(JSONArray episodeArray) {
        this.episodeJSONArray = episodeArray;
        initFilter();
    }

    private void initFilter() {
        initEpisodeArray();
        MPlayRecordInfo info = new PlayRecordHelpler(page.getActivity()).getPlayRcInfo(programSeriesId);
        if(info!= null && cpId.equals(info.getCpId())) {
            pagingNum = info.getPageNum();
        }
        else {
            pagingNum = 1;
        }

        if(isCreateGroup) {
            isCreateGroup = false;
            createUI();
        }
        else {

            epsiodeAdapter = null;
            epsiodeAdapter = new OtherVarietyTagAdapter(page, false);
            epsiodeAdapter.setNameList(nameList);
            epsiodeAdapter.setData(timeList);
            episodeGrid.setAdapter(epsiodeAdapter);
        }
    }

    /**
     * 获取界面具体集数的字符数组的方法
     * @return
     */
    private void initEpisodeArray(){
        if(nameList == null) {
            nameList = new ArrayList<String>();
            timeList = new ArrayList<String>();
        } else {
            nameList.clear();
            timeList.clear();
        }
        int count = episodeJSONArray.length();
        for(int i=0; i<count; i++){
            nameList.add(episodeJSONArray.optJSONObject(i).optString("name"));
            timeList.add(episodeJSONArray.optJSONObject(i).optString("volumncount"));
        }
    }

    private void initTotalPagingNum () {
        if(totalNum%20 == 0)
            totalPagingNum = totalNum/20;
        else
            totalPagingNum = totalNum/20+1;

        if(pagingNation != null)
            pagingNation.setText(pagingNum + "/" + totalPagingNum );
    }
    /**
     * 当类型是动漫和电视剧是的节目单列表
     */
    private void createUI() {
        pagingCull = new CullGroup(page);
        pagingCull.setPosition(80.0f, 50.0f);
        pagingCull.setSize(420.0f, 328.0f);
        pagingCull.setCullingArea(new Rectangle(0, 0, 420.0f, 328.0f));
        addActor(pagingCull);

        pagingBack = new Image(page);
        pagingBack.setSize(420.0f, 328.0f);
        pagingBack.setPosition(0.0f, 0.0f);
        pagingBack.setDrawableResource(R.mipmap.detai_variety_years_bg);
        pagingBack.toBack();
        pagingCull.addActor(pagingBack);

        logo = new Image(page);
        logo.setSize(420.0f, 328.0f);
        logo.setPosition(0.0f, 0.0f);
        pagingCull.addActor(logo);

        episodeCull = new CullGroup(page);
        episodeCull.setPosition(540.0f, 50.0f);
        episodeCull.setSize(1310.0f, 328.0f);
        addActor(episodeCull);

        episodeBack = new Image(page);
        episodeBack.setPosition(0.0f, 0.0f);
        episodeBack.setSize(1310.0f, 328.0f);
        episodeBack.setDrawableResource(R.mipmap.detai_variety_title_bg);
        episodeCull.addActor(episodeBack);

        direction = new TVFANLabel(page);
        direction.setPosition(780.0f, 18.0f);
        direction.setSize(300.0f, 42.0f);
        direction.setAlignment(Align.center_Vertical);
        direction.setTextSize(25);
        direction.setText("按“上下”键获取更多节目剧集");
        episodeCull.addActor(direction);

        nextPage = new Image(page);
        nextPage.setPosition(1160.0f, 18.0f);
        nextPage.setSize(42.0f, 42.0f);
        nextPage.setOnFocusChangeListener(this);
        nextPage.setOnClickListener(this);
        nextPage.setName("nextPage");
        nextPage.setNextFocusLeft("nextPage");
        nextPage.setDrawableResource(R.mipmap.detai_variety_down_normal);
        nextPage.setFocusAble(true);
        episodeCull.addActor(nextPage);

        lastPage = new Image(page);
        lastPage.setPosition(1218.0f, 18.0f);
        lastPage.setSize(42.0f, 42.0f);
        lastPage.setOnFocusChangeListener(this);
        lastPage.setOnClickListener(this);
        lastPage.setDrawableResource(R.mipmap.detai_variety_up_normal);
        lastPage.setName("lastPage");
        lastPage.setFocusAble(true);
        episodeCull.addActor(lastPage);

        episodeLine = new Image(page);
        episodeLine.setPosition(40.0f, 72.0f);
        episodeLine.setSize(1230.0f, 1.0f);
        episodeLine.setDrawableResource(R.mipmap.detai_line);
        episodeCull.addActor(episodeLine);

        pagingNation = new TVFANLabel(page);
        pagingNation.setPosition(40.0f, 0.0f);
        pagingNation.setSize(72.0f, 72.0f);
        pagingNation.setTextSize(35);
        pagingNation.setAlignment(Align.center_Vertical);
        pagingNation.setText(pagingNum + "/" + totalPagingNum);
        episodeCull.addActor(pagingNation);

        episodeGridCull = new CullGroup(page);
        episodeGridCull.setPosition(15.0f, 73.0f);
        episodeGridCull.setSize(1280.0f, 245.0f);
        episodeGridCull.setCullingArea(new Rectangle(0.0f, 0.0f, 1280.0f, 245.0f));
        episodeCull.addActor(episodeGridCull);

        episodeGrid = new Grid(page);
        episodeGrid.setPosition(0.0f, 0.0f);
        episodeGrid.setSize(1280.0f, 245.0f);
        episodeGrid.setGapLength(11.0f);
        episodeGrid.setRowNum(1);
        episodeGrid.setOrientation(Grid.ORIENTATION_VERTICAL);
        episodeGrid.setItemClickListener(bottomItemClickListener);
        epsiodeAdapter = new OtherVarietyTagAdapter(page, false);
        epsiodeAdapter.setNameList(nameList);
        epsiodeAdapter.setData(timeList);
        episodeGrid.setAdapter(epsiodeAdapter);
        episodeGridCull.addActor(episodeGrid);

    }

    AbsListView.OnItemClickListener bottomItemClickListener = new AbsListView.OnItemClickListener() {
        @Override
        public void onItemClick(Actor actor, int position, AbsListView grid) {
            try {

                PlayRecordHelpler prh = new PlayRecordHelpler(page.getActivity());
                prh.deletePlayRecordBy(programSeriesId);
                final Bundle bundle = new Bundle();
                if(detailObject.has("sources"))
                    detailObject.remove("sources");
                detailObject.putOpt("sources", episodeJSONArray);
                String str = detailObject.toString();
                if (str != null) {
                    byte[] bs = str.getBytes();
                    if (bs != null && bs.length < 20 * 1024) { // wanqi，小于20kb的内容使用bundle传输
                        bundle.putString("moviedetail", str);
                    } else { // wanqi，若大于20Kb的内容就共享内存，让播放器读取
                        AppGlobalVars.getIns().TMP_VARS.put("DETAIL_"
                                + programSeriesId, str);
                        bundle.putString("moviedetail", "DETAIL_"
                                + programSeriesId);
                    }
                }

                int freePlayTime = 0;
                bundle.putInt("freePlayTime", freePlayTime);
                bundle.putString("type", type);
                bundle.putInt("pagingNum", pagingNum);
                bundle.putInt("currentPlayPosition", position);
                bundle.putInt("totalEpisodes", totalNum);
                bundle.putString("historyItem", (position + 1) + "期");
                bundle.putString("cpId", cpId);
                ((BasePage) page).doAction(BasePage.ACTION_NAME.OPEN_PLAYER, bundle);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onFocusChanged(Actor actor, boolean hasFocus) {

        if (hasFocus) {
            if (actor == nextPage) {
                nextPage.setDrawableResource(R.mipmap.detai_variety_down_selected);
            } else if (actor == lastPage) {
                lastPage.setDrawableResource(R.mipmap.detai_variety_up_selected);
            }
        } else {
            if (actor == nextPage) {
                nextPage.setDrawableResource(R.mipmap.detai_variety_down_normal);
            } else if (actor == lastPage) {
                lastPage.setDrawableResource(R.mipmap.detai_variety_up_normal);
            }
        }
    }

    @Override
    public void onClick(Actor actor) {
        if(actor == nextPage) {
            if(pagingNum < totalPagingNum) {
                pagingNum ++ ;
                getEpisodeData();
            }
        }
        else if(actor == lastPage) {
            if(pagingNum > 1) {
                pagingNum -- ;
                getEpisodeData();
            }
        }
    }

    private void getEpisodeData(){
        String sortType = "desc";
        rd.getMovieEpisodeData(programSeriesId, cpId, sortType, ""+pagingNum, "", type, "", new HttpResponse.Listener4JSONObject() {
            @Override
            public void onResponse(final JSONObject response) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        if(response != null && !response.equals("{}")){
                            episodeJSONArray = response.optJSONArray("sources");
                            totalNum = response.optInt("totalNumber",1);
                            initTotalPagingNum();
                            initEpisodeArray();

                            epsiodeAdapter = null;
                            epsiodeAdapter = new OtherVarietyTagAdapter(page, false);
                            epsiodeAdapter.setNameList(nameList);
                            epsiodeAdapter.setData(timeList);
                            episodeGrid.setAdapter(epsiodeAdapter);

                            if(onEpisodeChangeListener != null)
                                onEpisodeChangeListener.onEpisodeChange(response);
                        }
                    }
                });
            }

            @Override
            public void onError(String httpStatusCode) {

            }
        });
    }

    public interface OnEpisodeChangeListener {
        public void onEpisodeChange(JSONObject response);
    }

    private OnEpisodeChangeListener onEpisodeChangeListener;

    public void setOnEpisodeChangeListener (OnEpisodeChangeListener onEpisodeChangeListener) {
        this.onEpisodeChangeListener = onEpisodeChangeListener;
    }
}
