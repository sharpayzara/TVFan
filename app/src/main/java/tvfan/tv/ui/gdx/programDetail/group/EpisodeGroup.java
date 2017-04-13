package tvfan.tv.ui.gdx.programDetail.group;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.luxtone.lib.gdx.Page;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import tvfan.tv.AppGlobalConsts;

/**
 * Created by ddd on 2016/4/8.
 */
public class EpisodeGroup extends Group {

    private TeleplayEpisodeGroup teleplayEpisodeGroup;
    private OtherEpisoGroup otherEpisoGroup;
    private MusicEpisodeGroup musicEpisodeGroup;

    public EpisodeGroup(Page page, String type, JSONArray episodeArray,
                        String programSeriesId, String cpId, JSONObject detailObject, JSONArray yearJSONArray,
                        ArrayList<String> yearList, int pagingNum,int yearNum,  int totalNum) {
        super(page);
        setSize(AppGlobalConsts.APP_WIDTH, 368.0f);
        setPosition(0.0f, 0.0f);

        if (type.equalsIgnoreCase("电视剧")||type.equalsIgnoreCase("动漫") ||
                type.equalsIgnoreCase("电影")) {
            teleplayEpisodeGroup = new TeleplayEpisodeGroup(page);
            teleplayEpisodeGroup.setPagingList(yearList);
            teleplayEpisodeGroup.setRightPosition(yearNum);
            teleplayEpisodeGroup.setCpId(cpId);
            teleplayEpisodeGroup.setEpisodeJSONArray(episodeArray);
            teleplayEpisodeGroup.setProgramSeriesId(programSeriesId);
            teleplayEpisodeGroup.setType(type);
            teleplayEpisodeGroup.setDetailObject(detailObject);
            teleplayEpisodeGroup.setYearJSONArray(yearJSONArray);
            teleplayEpisodeGroup.setTotalNum(totalNum);
            addActor(teleplayEpisodeGroup);
            teleplayEpisodeGroup.setOnEpisodeChangeListener(new TeleplayEpisodeGroup.OnEpisodeChangeListener() {
                @Override
                public void onEpisodeChange(JSONObject response) {
                    if(onEpisodeChangeListener != null)
                        onEpisodeChangeListener.onEpisodeChange(response);
                }
            });
        }
        else if ((type.equals("综艺") || type.equals("游戏") || type.equalsIgnoreCase("短视频")|| type.equalsIgnoreCase("体育")) && yearList != null) {
            otherEpisoGroup = new OtherEpisoGroup(page);
            otherEpisoGroup.setProgramSeriesId(programSeriesId);
            otherEpisoGroup.setCpId(cpId);
            otherEpisoGroup.setTotalNum(totalNum);
            otherEpisoGroup.setYearList(yearList);
            otherEpisoGroup.setYearnum(yearNum);
            otherEpisoGroup.setPagingNum(pagingNum);
            otherEpisoGroup.setEpisodeJSONArray(episodeArray);
            otherEpisoGroup.setType(type);
            otherEpisoGroup.setDetailObject(detailObject);
            otherEpisoGroup.setYearJSONArray(yearJSONArray);
            addActor(otherEpisoGroup);
            otherEpisoGroup.setOnEpisodeChangeListener(new OtherEpisoGroup.OnEpisodeChangeListener() {
                @Override
                public void onEpisodeChange(JSONObject response) {
                    if(onEpisodeChangeListener != null)
                        onEpisodeChangeListener.onEpisodeChange(response);
                }
            });
        }
        else {
            musicEpisodeGroup = new MusicEpisodeGroup(page);
            musicEpisodeGroup.setProgramSeriesId(programSeriesId);
            musicEpisodeGroup.setCpId(cpId);
            musicEpisodeGroup.setTotalNum(totalNum);
            musicEpisodeGroup.setEpisodeJSONArray(episodeArray);
            musicEpisodeGroup.setType(type);
            musicEpisodeGroup.setDetailObject(detailObject);
            musicEpisodeGroup.setOnEpisodeChangeListener(new MusicEpisodeGroup.OnEpisodeChangeListener() {
                @Override
                public void onEpisodeChange(JSONObject response) {
                    if (onEpisodeChangeListener != null)
                        onEpisodeChangeListener.onEpisodeChange(response);
                }
            });
            addActor(musicEpisodeGroup);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void changeSource(String cpId,int totalNum, JSONArray episodeArray, JSONArray yearJSONArray) {
        if(teleplayEpisodeGroup != null){
            teleplayEpisodeGroup.setCpId(cpId);
            teleplayEpisodeGroup.setTotalNum(totalNum);
            teleplayEpisodeGroup.setEpisodeJSONArray(episodeArray);
            teleplayEpisodeGroup.setYearJSONArray(yearJSONArray);
        }
        else if(otherEpisoGroup != null) {
            otherEpisoGroup.setCpId(cpId);
            otherEpisoGroup.setTotalNum(totalNum);
            otherEpisoGroup.setEpisodeJSONArray(episodeArray);
            otherEpisoGroup.setYearJSONArray(yearJSONArray);
        } else {
            musicEpisodeGroup.setCpId(cpId);
            musicEpisodeGroup.setTotalNum(totalNum);
            musicEpisodeGroup.setEpisodeJSONArray(episodeArray);
        }
    }

    public void setYearList (ArrayList<String> yearList) {
        if(otherEpisoGroup != null)
            otherEpisoGroup.setYearList(yearList);
        else if(teleplayEpisodeGroup != null)
            teleplayEpisodeGroup.setPagingList(yearList);
    }

    public interface OnEpisodeChangeListener {
        public void onEpisodeChange(JSONObject response);
    }

    private OnEpisodeChangeListener onEpisodeChangeListener;

    public void setOnEpisodeChangeListener (OnEpisodeChangeListener onEpisodeChangeListener) {
        this.onEpisodeChangeListener = onEpisodeChangeListener;
    }
}
