package tvfan.tv.ui.gdx.programDetail.group;

import android.os.Bundle;

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
import tvfan.tv.ui.gdx.programDetail.adapter.OtherVarietyTagAdapter;
import tvfan.tv.ui.gdx.programDetail.item.VerityOtherItem;
import tvfan.tv.ui.gdx.widgets.TVFANLabel;

/**
 * Created by ddd on 2016/4/10.
 */
public class OtherEpisoGroup extends Group implements OnFocusChangeListener, OnClickListener {
    private Page page;
    private JSONArray episodeJSONArray;
    private Grid yearGrid;//节目集数据分页的grid
    private Grid episodeGrid;//节目集列表
    private CullGroup pagingCull;
    private CullGroup episodeCull;
    private CullGroup episodeGridCull;
    private Image pagingBack;
    private Image episodeBack;
    private Image episodeLine;
    private Image nextPage;
    private Image lastPage;
    private TVFANLabel direction, pagingNation;
    private OtherVarietyTagAdapter yearAdapter;
    private OtherVarietyTagAdapter epsiodeAdapter;
    private VerityOtherItem lastPagingItem;

    private ArrayList<String> timeList;
    private ArrayList<String> nameList;
    private ArrayList<String> yearList;

    private int yearNum = 0; // 年代的对应位置
    private int pagingNum = 1;//记录分页列表的position
    private int totalNum;//当前年份的总集数
    private int totalPagingNum = 1;//一共分几页
    private String cpId;//源ID
    private String programSeriesId;//节目ID
    private String type;//类型
    private boolean isCreateGroup = true;

    private RemoteData rd;
    private JSONObject detailObject;
    private JSONArray yearJSONArray;

    public OtherEpisoGroup(Page page) {
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

    public void setYearnum(int yearNum) {
        this.yearNum = yearNum;
    }

    public void setPagingNum(int pagingNum) {
        this.pagingNum = pagingNum;
    }

    public void setType(String type) {
        this.type = type;
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

    private void initTotalPagingNum () {
        if(totalNum%20 == 0)
            totalPagingNum = totalNum/20;
        else
            totalPagingNum = totalNum/20+1;

        if(pagingNation != null)
            pagingNation.setText(pagingNum + "/" + totalPagingNum );
    }

    private void initFilter() {
        initEpisodeArray();

        if(isCreateGroup) {
            isCreateGroup = false;
            createUI();
        }
        else {
            MPlayRecordInfo info = new PlayRecordHelpler(page.getActivity()).getPlayRcInfo(programSeriesId);
            if(info!= null && cpId.equals(info.getCpId())) {
                yearNum = info.getYearNum();
                pagingNum = info.getPageNum();
            }
            else {
                yearNum = 0;
                pagingNum = 1;
            }
            epsiodeAdapter = null;
            epsiodeAdapter = new OtherVarietyTagAdapter(page, false);
            epsiodeAdapter.setData(timeList);
            epsiodeAdapter.setNameList(nameList);
            episodeGrid.setAdapter(epsiodeAdapter);

            yearAdapter = new OtherVarietyTagAdapter(page, true);
            yearAdapter.setData(yearList);
            yearAdapter.setPagingNum(yearNum);
            yearGrid.setAdapter(yearAdapter);
            lastPagingItem = (VerityOtherItem) yearGrid.getActorAtPosition(yearNum);
        }
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

        yearGrid = new Grid(page);
        yearGrid.setPosition(15.0f, 14.0f);
        yearGrid.setSize(390.0f, 300.0f);
        yearGrid.setGapLength(11.0f);
        yearGrid.setRowNum(1);
        yearGrid.setOrientation(Grid.ORIENTATION_VERTICAL);
        yearGrid.setName("yearGrid");
        yearGrid.setNextFocusUp("yearGrid");
        yearAdapter = new OtherVarietyTagAdapter(page, true);
        yearAdapter.setData(yearList);
        yearAdapter.setPagingNum(yearNum);
        yearGrid.setAdapter(yearAdapter);
        yearGrid.setSelection(yearNum, false);
        lastPagingItem = (VerityOtherItem) yearGrid.getActorAtPosition(yearNum);
        yearGrid.setItemClickListener(yearItemClickListener);
        pagingCull.addActor(yearGrid);

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
        lastPage.setNextFocusLeft("nextPage");
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
        episodeGrid.setItemClickListener(episodeItemClickListener);
        epsiodeAdapter = new OtherVarietyTagAdapter(page, false);
        epsiodeAdapter.setData(timeList);
        epsiodeAdapter.setNameList(nameList);
        episodeGrid.setAdapter(epsiodeAdapter);
        episodeGridCull.addActor(episodeGrid);

    }

    /**
     * 获取界面具体集数的字符数组的方法
     * @return
     */
    private void initEpisodeArray(){
        if(timeList == null) {
            nameList = new ArrayList<String>();
            timeList = new ArrayList<String>();
        } else {
            nameList.clear();
            timeList.clear();
        }
        int count = episodeJSONArray.length();
        for(int i=0; i<count; i++){
            timeList.add(episodeJSONArray.optJSONObject(i).optString("volumncount"));
            nameList.add(episodeJSONArray.optJSONObject(i).optString("name"));
        }
    }

    /**
     * 年代列表点击
     */
    AbsListView.OnItemClickListener yearItemClickListener = new AbsListView.OnItemClickListener() {

        @Override
        public void onItemClick(Actor arg0, int arg1, AbsListView arg2) {
            yearNum = arg1;
            pagingNum = 1;
            getEpisodeData(yearList.get(yearNum));
            lastPagingItem.setItemBack(false);
            ((VerityOtherItem)arg0).setItemBack(true);
            yearAdapter.setPagingNum(arg1);
            lastPagingItem = (VerityOtherItem)arg0;
        }
    };

    /**
     * 节目集列表点击
     */
    AbsListView.OnItemClickListener episodeItemClickListener = new AbsListView.OnItemClickListener() {
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
                bundle.putInt("currentPlayPosition", position);
                bundle.putInt("yearNum", yearNum );
                bundle.putInt("pagingNum", pagingNum);
                bundle.putString("historyItem", timeList.get(position) + "期");
                bundle.putInt("totalEpisodes", totalNum);
                bundle.putString("cpId", cpId);
                bundle.putString("yearJSONArray", yearJSONArray.toString());
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
                getEpisodeData(yearList.get(yearNum));
            }
        }
        else if(actor == lastPage) {
            if(pagingNum > 1) {
                pagingNum -- ;
                getEpisodeData(yearList.get(yearNum));
            }
        }
    }

    public void setYearList(ArrayList<String> yearList) {
        this.yearList = yearList;
    }

    private void getEpisodeData(String year){
        String sortType = "desc";
        rd.getMovieEpisodeData(programSeriesId, cpId, sortType, ""+pagingNum, "", type, year, new HttpResponse.Listener4JSONObject() {
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
                            epsiodeAdapter.setData(timeList);
                            epsiodeAdapter.setNameList(nameList);
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

    public void setYearJSONArray(JSONArray yearJSONArray) {
        this.yearJSONArray = yearJSONArray;
    }


    public interface OnEpisodeChangeListener {
        public void onEpisodeChange(JSONObject response);
    }

    private OnEpisodeChangeListener onEpisodeChangeListener;

    public void setOnEpisodeChangeListener (OnEpisodeChangeListener onEpisodeChangeListener) {
        this.onEpisodeChangeListener = onEpisodeChangeListener;
    }
}
