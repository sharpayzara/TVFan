package tvfan.tv.ui.gdx.programDetail.group;

import android.content.ClipData;
import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.Cullable;
import com.luxtone.lib.gdx.OnClickListener;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.AbsListView;
import com.luxtone.lib.widget.CullGroup;
import com.luxtone.lib.widget.Grid;

import org.json.JSONArray;
import org.json.JSONException;
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
import tvfan.tv.ui.gdx.programDetail.adapter.VarietyTagAdapter;
import tvfan.tv.ui.gdx.programDetail.item.VerityTagItem;
import tvfan.tv.ui.gdx.widgets.TVFANLabel;

/**
 * 当style为电视剧和动漫是的节目集列表
 * Created by ddd on 2016/4/10.
 */
public class TeleplayEpisodeGroup extends Group{

    private Page page;
    private JSONArray episodeJSONArray;
    private Grid pagingGrid;//节目集数据分页的grid
    private Grid episodeGrid;//节目集列表
    private VarietyTagAdapter pagingAdapter;
    private VarietyTagAdapter epsiodeAdapter;
    private TVFANLabel mTitle;
    private VerityTagItem lastPagingItem;
    private CullGroup episdeGroup;
    private ArrayList<String> episodeList;
    private ArrayList<String> pagingList;

    private static final int DIVISIONVALUE = 20; // 右侧划分区间,默认是100
    private int rightPosition = 0; // 记录分页列表的position
    private int totalNum = 1;
    private int pagingFresh;
    private int episodeFresh;
    private String cpId;
    private String type;
    private String programSeriesId;
    private boolean isCreateGroup = true;

    private RemoteData rd;
    private JSONObject detailObject;
    private JSONArray yearJSONArray;

    public TeleplayEpisodeGroup(Page page) {
        super(page);
        this.page = page;
        rd = new RemoteData();
        setSize(AppGlobalConsts.APP_WIDTH, 368.0f);
        setPosition(0.0f, 0.0f);
    }

    public void setCpId (String cpId) {
        this.cpId = cpId;
    }

    public void setPagingList(ArrayList<String> pagingList) {
        this.pagingList = pagingList;
    }

    public void setProgramSeriesId (String programSeriesId) {
        this.programSeriesId = programSeriesId;
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
        lastPagingItem = null;
        if (isCreateGroup) {
            isCreateGroup = false;
            createUI();
        }
        else {
            MPlayRecordInfo info = new PlayRecordHelpler(page.getActivity()).getPlayRcInfo(programSeriesId);
            if(info!= null && cpId.equals(info.getCpId()))
                rightPosition = info.getYearNum();
            else
                rightPosition = 0;
            epsiodeAdapter = null;
            epsiodeAdapter = new VarietyTagAdapter(page, false, episodeFresh++);
            epsiodeAdapter.setData(episodeList);
            episodeGrid.setAdapter(epsiodeAdapter);

            pagingAdapter = new VarietyTagAdapter(page, true, pagingFresh++);
            pagingAdapter.setData(pagingList);
            pagingAdapter.setPagingNum(rightPosition);
            pagingGrid.setAdapter(pagingAdapter);
            if(pagingList.size() > 9){
                pagingGrid.setSelection(rightPosition,false);
            }
           // lastPagingItem = (VerityTagItem) pagingGrid.getActorAtPosition(0);
        }
    }

    /**
     * 当类型是动漫和电视剧是的节目单列表
     */
    private void createUI() {
        mTitle = new TVFANLabel(page);
        mTitle.setPosition(80.0f, 367.0f);
        mTitle.setSize(150.0f, 30.0f);
        mTitle.setFocusAble(false);
        mTitle.setAlignment(Align.left);
        mTitle.setColor(Color.valueOf("ffffff"));
        mTitle.setTextSize(30);
        mTitle.setText("选集");
        addActor(mTitle);


        episdeGroup = new CullGroup(page);
        episdeGroup.setPosition(80.0f, 220.0f);
        episdeGroup.setSize(1760.0f, 55.0f);
        episdeGroup.setCullingArea(new Rectangle(0,0,1760.0f, 55.0f));
        addActor(episdeGroup);

        pagingGrid = new Grid(page);
        pagingGrid.setPosition(0,0);
        pagingGrid.setSize(1760.0f, 55.0f);
        pagingGrid.setGapLength(18.0f);
        pagingGrid.setRowNum(1);
        pagingGrid.setOrientation(Grid.ORIENTATION_LANDSPACE);
        pagingGrid.setName("pagingGrid");
        pagingGrid.setCull(false);
        pagingGrid.setNextFocusRight("pagingGrid");
        pagingAdapter = new VarietyTagAdapter(page, true, pagingFresh++);
        pagingAdapter.setData(pagingList);
        pagingAdapter.setPagingNum(rightPosition);
        pagingGrid.setAdapter(pagingAdapter);
        if(pagingList.size() > 9){
            pagingGrid.setSelection(rightPosition,false);
        }
        pagingGrid.setItemClickListener(topItemClickListener);
        episdeGroup.addActor(pagingGrid);


        episodeGrid = new Grid(page);
        episodeGrid.setPosition(80.0f, 100.0f);
        episodeGrid.setSize(1760.0f, 80.0f);
        episodeGrid.setGapLength(10.0f);
        episodeGrid.setRowNum(1);
        episodeGrid.setOrientation(Grid.ORIENTATION_LANDSPACE);
        episodeGrid.setName("episodeGrid");
        episodeGrid.setNextFocusRight("episodeGrid");
        episodeGrid.setNextFocusUp("pagingGrid");
        episodeGrid.setItemClickListener(bottomItemClickListener);
        epsiodeAdapter = new VarietyTagAdapter(page, false, episodeFresh++);
        epsiodeAdapter.setData(episodeList);
        episodeGrid.setAdapter(epsiodeAdapter);
        addActor(episodeGrid);
    }
    /**
     * 获取界面具体集数的字符数组的方法
     * @return
     */
    private void initEpisodeArray(){
        if(episodeList == null)
            episodeList = new ArrayList<String>();
        else
            episodeList.clear();

        for(int i=0; i<episodeJSONArray.length(); i++){
            episodeList.add(episodeJSONArray.optJSONObject(i).optString("volumncount"));
        }
    }

    /**
     * 获取节目集列表的数据
     * @param start
     * @param end
     */
    public void getEpisodeData(String start, String end){
        String sortType = "asc";
        rd.getMovieEpisodeData(programSeriesId, cpId, sortType, start, end, type, "",
                new HttpResponse.Listener4JSONObject() {

                    @Override
                    public void onResponse(final JSONObject response) {
                        if (response != null) {
                            try {
                                episodeJSONArray = response.getJSONArray("sources");
                                Gdx.app.postRunnable(new Runnable() {
                                    @Override
                                    public void run() {
                                        initEpisodeArray();
                                        epsiodeAdapter = null;
                                        epsiodeAdapter = new VarietyTagAdapter(page, false, episodeFresh++);
                                        epsiodeAdapter.setData(episodeList);
                                        episodeGrid.setAdapter(epsiodeAdapter);
                                        if(onEpisodeChangeListener != null)
                                            onEpisodeChangeListener.onEpisodeChange(response);
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    @Override
                    public void onError(String httpStatusCode) {
                    }
                });
    }

    AbsListView.OnItemClickListener topItemClickListener = new AbsListView.OnItemClickListener() {

        @Override
        public void onItemClick(Actor arg0, int arg1, AbsListView arg2) {
            if(rightPosition == arg1){
                return;
            }

            if(lastPagingItem == null){
                lastPagingItem = pagingAdapter.getCurrentItem(rightPosition);
            }
            if(lastPagingItem != null){
                lastPagingItem.setItemBack(R.mipmap.detai_tv_series_icon_background_normal);
            }

            ((VerityTagItem)arg0).setItemBack(R.mipmap.detai_tv_series_icon_background_selected1);
            pagingAdapter.setPagingNum(arg1);
            lastPagingItem = (VerityTagItem)arg0;
            rightPosition = arg1;
            String pagingStr = pagingList.get(arg1);
            String start = pagingStr.substring(0, pagingStr.indexOf("-"));
            String end = pagingStr.substring(pagingStr.indexOf("-") + 1, pagingStr.length());
            getEpisodeData(start, end);
        }
    };
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
                bundle.putInt("currentPlayPosition", position);
                bundle.putInt("yearNum", rightPosition);
                bundle.putString("historyItem",episodeList.get(position)+"集");
                bundle.putInt("totalEpisodes", totalNum);
                bundle.putString("cpId", cpId);
                bundle.putString("yearJSONArray", yearJSONArray.toString());
                ((BasePage) page).doAction(BasePage.ACTION_NAME.OPEN_PLAYER, bundle);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void setType(String type) {
        this.type = type;
    }

    public void setDetailObject(JSONObject detailObject) {
        this.detailObject = detailObject;
    }

    public void setRightPosition(int rightPosition) {
        this.rightPosition = rightPosition;
    }

    public void setYearJSONArray(JSONArray yearJSONArray) {
        this.yearJSONArray = yearJSONArray;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public interface OnEpisodeChangeListener {
        public void onEpisodeChange(JSONObject response);
    }
    private OnEpisodeChangeListener onEpisodeChangeListener;
    public void setOnEpisodeChangeListener (OnEpisodeChangeListener onEpisodeChangeListener) {
        this.onEpisodeChangeListener = onEpisodeChangeListener;
    }
}
