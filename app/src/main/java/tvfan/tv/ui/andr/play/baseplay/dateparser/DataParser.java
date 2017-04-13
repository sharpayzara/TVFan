package tvfan.tv.ui.andr.play.baseplay.dateparser;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.AppGlobalVars;
import tvfan.tv.dal.JSONParser;
import tvfan.tv.dal.PlayRecordHelpler;
import tvfan.tv.dal.models.LiveExtraBean;
import tvfan.tv.dal.models.LiveItemBean;
import tvfan.tv.dal.models.MPlayRecordInfo;
import tvfan.tv.dal.models.PlayListBean;
import tvfan.tv.dal.models.PlayUrlBean;
import tvfan.tv.dal.models.PlayerBean;
import tvfan.tv.dal.models.SourcesBean;
import tvfan.tv.ui.andr.play.baseplay.utils.DateUtils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * 数据解析器
 * 
 * @author sadshine
 * 
 */
public class DataParser {

	/**
	 * 枚举解析对象键值
	 * 
	 * @author sadshine
	 */
	public static enum DATA_KEY {
		moviedetail, livestation, livechannel, liveprogram, livefav

	};

	/**
	 * 播放器数据对象解析器
	 * 
	 * @param strjson
	 * @return
	 */
	public static PlayerBean getPlayerBean(DATA_KEY bundlekey, Context context) {
		Bundle bd = ((Activity) context).getIntent().getExtras();
		PlayerBean playerbean = null;
		if (bd != null) {
			String strjson = bd.getString(bundlekey.name());
			switch (bundlekey) {
			case moviedetail:
				playerbean = getDetailPlayerBean(strjson,bd.getString("historyItem","1"));
				break;
			}
		}
		return playerbean;
	}

	/**
	 * 播放器免费观看时长
	 * 
	 * @param context
	 * @return
	 */
	public static int getFreePlayTime(Context context) {
		int freetime = 0;
		try {
			Bundle bd = ((Activity) context).getIntent().getExtras();
			if (bd != null && bd.getInt("freePlayTime") >= 0) {
				freetime = bd.getInt("freePlayTime");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			freetime = 0;
		}
		return freetime;
	}

	/**
	 * 获取节目剧集
	 * 
	 * @param context
	 * @return
	 */
	public static int getExtraPos(Context context) {
		int pos = -1;
		try {
			Bundle bd = ((Activity) context).getIntent().getExtras();
			if (bd != null && bd.getInt("currentPlayPosition", -1) >= 0) {
				pos = bd.getInt("currentPlayPosition");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			pos = -1;
		}
		return pos;
	}

	/**
	 * 获取详情页的数据
	 * 
	 * @param strjson
	 * @return
	 */
	public static PlayerBean getDetailPlayerBean(String strjson,String historyItem) {
		PlayerBean playerbean = new PlayerBean();
		try {
			JSONObject jsonobect = null;
			if (strjson != null && strjson.startsWith("DETAIL_")) { // wanqi，如果详情页传过来大数据，就要从共享的内存里面读取
				AppGlobalVars appGlobalVars = AppGlobalVars.getIns();
				String s = (String) appGlobalVars.TMP_VARS.get(strjson);
				AppGlobalVars.getIns().TMP_VARS.remove(strjson);
				strjson = s;
			}
			jsonobect = new JSONObject(strjson);
			if (jsonobect == null)
				return null;
			playerbean.setId(jsonobect.optString("id", ""));
			playerbean.setName(jsonobect.optString("name", ""));
			playerbean.setShowid(jsonobect.optString("showid", ""));
			playerbean.setType(jsonobect.optString("type", ""));
			playerbean.setPicurl(jsonobect.optString("picurl", ""));
			playerbean.setHistoryInfo(historyItem);
			playerbean.setParentid(jsonobect.optString("parent_root_item_id",
					""));
			JSONArray jsonarr = jsonobect.optJSONArray("sources");
			ArrayList<SourcesBean> sourcelist = new ArrayList<SourcesBean>();
			for (int i = 0; i < jsonarr.length(); i++) {
				SourcesBean sourcebean = new SourcesBean();
				JSONObject obj = (JSONObject) jsonarr.get(i);
				sourcebean.setId(obj.getString("id"));
				sourcebean.setVideoid(obj.getString("videoid"));
				sourcebean.setVolumncount(obj.getString("volumncount"));
				JSONArray subjsonarr = obj.optJSONArray("playlist");
				ArrayList<PlayListBean> playlist = new ArrayList<PlayListBean>();
				for (int j = 0; j < subjsonarr.length(); j++) {
					PlayListBean playlistbean = new PlayListBean();
					JSONObject subjsonobj = (JSONObject) subjsonarr.get(j);
					playlistbean.setType(subjsonobj.getString("type"));
					playlistbean.setUrl(subjsonobj.getString("playurl"));
					playlist.add(playlistbean);
				}
				sourcebean.setPlaylist(playlist);
				sourcelist.add(sourcebean);
			}
			playerbean.setSourcelist(sourcelist);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return playerbean;
	}

	private static final int DIVISIONVALUE = 20;//每页
	
	/**
	 * 获取首页轮播频道传来的频道节目
	 * 
	 * @param strjson
	 * @return
	 */
	@SuppressWarnings("unused")
	public static LiveExtraBean getLiveExtraBean(Context context, Intent intent) {
		if (intent == null) {
			intent = ((Activity) context).getIntent();
		}
		String strjson = intent.getStringExtra("actionParam");
		LiveExtraBean playerbean = new LiveExtraBean();
		try {
			JSONObject jsonobect = new JSONObject(strjson);
			if (jsonobect == null)
				return null;
			playerbean.setChannelid(jsonobect.getString("channelId"));
			playerbean.setGroupid(jsonobect.getString("groupid"));

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return playerbean;
	}

	/**
	 * 频道列表
	 * 
	 * @param strjson
	 * @param bunldkey
	 * @return
	 * @throws JSONException
	 */
	public static ArrayList<LiveItemBean> getLiveList(String strjson,
			DATA_KEY bunldkey, Context context) throws JSONException {
		ArrayList<LiveItemBean> livelist = new ArrayList<LiveItemBean>();
		switch (bunldkey) {
		case livestation:
			JSONObject jsonobect = new JSONObject(strjson);
			JSONArray jsonarr = jsonobect.getJSONArray("data");
			// 收藏频道
			LiveItemBean livefavitembean = new LiveItemBean();
			livefavitembean.setChannealid("");
			livefavitembean.setTitle("我的收藏");
			livelist.add(livefavitembean);
			/*
			 * //全部频道 LiveItemBean liveallitembean = new LiveItemBean();
			 * liveallitembean.setChannealid("");
			 * liveallitembean.setTitle("全部频道"); livelist.add(liveallitembean);
			 */

			for (int i = 0; i < jsonarr.length(); i++) {

				LiveItemBean liveitembean = new LiveItemBean();
				JSONObject obj = (JSONObject) jsonarr.get(i);
				liveitembean.setChannealid(obj.getString("channelGroupId"));
				liveitembean.setTitle(obj.getString("channelGroupName"));
				livelist.add(liveitembean);
			}
			break;

		case livechannel:
			JSONArray channelarr = new JSONArray(strjson);
			// JSONArray channelarr = channelobj.getJSONArray("data");
			for (int i = 0; i < channelarr.length(); i++) {
				LiveItemBean liveitembean = new LiveItemBean();
				JSONObject obj = (JSONObject) channelarr.get(i);
				liveitembean.setId(i + 1);
				liveitembean.setChannealid(obj.getString("channelId"));
				liveitembean.setProgramId(obj.getString("programId"));
				liveitembean.setTitle(obj.getString("channelName"));
				liveitembean.setLogo(obj.getString("logo"));
				liveitembean.setNo(obj.getString("no"));
				liveitembean.setUrlid(obj.getString("urlid"));
				liveitembean.setSubtitle(obj.getString("onplay"));
				livelist.add(liveitembean);
			}
			break;
		case liveprogram:
			JSONArray proarr = new JSONArray(strjson);
			// JSONArray proarr = programjson.optJSONArray("data");
			String programdata = "";
			JSONObject jsonobj = null;
			if (proarr != null && proarr.length() > 0) {
				jsonobj = (JSONObject) proarr.get(0);
				programdata = jsonobj.getString("playDate");
			}

			JSONArray programarr = jsonobj.optJSONArray("programs");
			for (int i = 0; i < programarr.length(); i++) {
				LiveItemBean liveitembean = new LiveItemBean();
				JSONObject obj = (JSONObject) programarr.get(i);
				liveitembean.setChannealid(obj.getString("programId"));
				liveitembean.setTitle(obj.getString("programName"));
				String time = DateUtils.getTime(obj.getString("startTime"),
						"HH:mm");
				liveitembean.setStartTime(time);
				String endtime = DateUtils.getTime(obj.getString("endTime"),
						"HH:mm");
				liveitembean.setEndTime(endtime);
				liveitembean.setProgramurl(obj.getString("programUrl"));
				// liveitembean.setUrlType(obj.getString("urlType"));
				liveitembean.setType(obj.getString("type"));
				liveitembean.setDes(obj.getString("des"));
				liveitembean.setDesImg(obj.getString("desImg"));
				liveitembean.setPlayDate(programdata);
				livelist.add(liveitembean);
			}
			break;
		case livefav:
			PlayRecordHelpler ph = new PlayRecordHelpler(context);
			ArrayList<MPlayRecordInfo> infolist = new ArrayList<MPlayRecordInfo>();
			infolist = ph.getLiveAllPlayRecord();
			for (int i = 0; i < infolist.size(); i++) {
				LiveItemBean liveitembean = new LiveItemBean();
				liveitembean.setChannealid(infolist.get(i).getEpgId());
				liveitembean.setTitle(infolist.get(i).getPlayerName());
				liveitembean.setLogo(infolist.get(i).getPicUrl());
				liveitembean.setNo(infolist.get(i).getLiveno());
				liveitembean.setUrlid(infolist.get(i).getDetailsId());
				liveitembean.setSubtitle(infolist.get(i).getActionUrl());// onplay
				liveitembean.setProgramId(infolist.get(i).getCornerPrice());
				livelist.add(liveitembean);
			}
			break;
		}

		return livelist;
	}

	/**
	 * 对直播界面获取的总的数据进行解析
	 * @param response
	 * @param stationlist
	 * @param mapOfChannellist
	 */
	public static void getChannelInfo(JSONObject response,
			ArrayList<LiveItemBean> stationlist,//最左侧的栏目的数据集合
			HashMap<String, ArrayList<LiveItemBean>> mapOfChannellist) {
		try {
			if (response.getInt("code") == 0) {
				JSONObject content = response.optJSONObject("content");
				JSONArray typeArr = content.optJSONArray("type");//整体的分类（像cctv或者湖南）

				// 收藏频道
				LiveItemBean livefavitembean = new LiveItemBean();
				livefavitembean.setChannealid("");
				livefavitembean.setTitle("收藏");
				stationlist.add(livefavitembean);

				// 全部频道
				LiveItemBean totalLiveItemBean = new LiveItemBean();
				totalLiveItemBean.setChannealid("total");
				totalLiveItemBean.setTitle("全部");
				stationlist.add(totalLiveItemBean);
				mapOfChannellist.put("total", new ArrayList<LiveItemBean>());

				int no = 1; // 频道编号
				for (int i = 0; i < typeArr.length(); i++) {
					// 频道分类相关信息
					LiveItemBean liveitembean = new LiveItemBean();
					JSONObject channelType = (JSONObject) typeArr.get(i);
					liveitembean.setChannealid(String.valueOf(channelType
							.optLong("id")));
					liveitembean.setTitle(channelType.optString("name"));
					stationlist.add(liveitembean);

					// 该频道分类下的频道信息
					JSONArray channelArr = channelType.optJSONArray("channel");
					ArrayList<LiveItemBean> channellist = new ArrayList<LiveItemBean>();//中间的数据集合
					for (int j = 0; j < channelArr.length(); j++) {
						LiveItemBean liveitembeanTmp = new LiveItemBean();
						JSONObject channel = (JSONObject) channelArr.get(j);
						liveitembeanTmp.setId(j + 1);
						liveitembeanTmp.setChannealid(String.valueOf(channel
								.optLong("id")));
						// liveitembean
						// .setProgramId(channel.getString("programId"));
						liveitembeanTmp.setTitle(channel.optString("name"));
						// liveitembeanTmp.setLogo(channel.getString("pic"));
						// liveitembean.setNo(channel.getString("no"));
						// liveitembean.setUrlid(channel.getString("urlid"));
						liveitembeanTmp
								.setSubtitle(channel.optString("cpName"));
						liveitembeanTmp.setProgramId(channel.optString("cpId"));
						liveitembeanTmp.setNo(String.valueOf(no++));
						// liveitembeanTmp.setStartTime(channel
						// .optString("cpStartTime"));
						// liveitembeanTmp.setEndTime(channel
						// .optString("cpEndTime"));
						// liveitembeanTmp.playUrl = new
						// ArrayList<PlayUrlBean>();
						// JSONArray playAdrressArr =
						// channel.optJSONArray("play");
						// for (int k = 0; k < playAdrressArr.length(); k++) {
						// JSONObject tmp = (JSONObject) playAdrressArr.get(k);
						// PlayUrlBean playUrlBean = new PlayUrlBean();
						// playUrlBean.url = tmp.optString("url");
						// playUrlBean.videoPath = tmp.optString("videoPath");
						// playUrlBean.channelIdStr =
						// tmp.optString("channelIdStr");
						// liveitembeanTmp.playUrl.add(playUrlBean);
						// }
						channellist.add(liveitembeanTmp);
						mapOfChannellist.get("total").add(liveitembeanTmp);
					}
					mapOfChannellist.put(
							String.valueOf(channelType.optLong("id")),
							channellist);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void getProgramList(JSONObject content, ArrayList<LiveItemBean> programlist) throws JSONException{
		JSONArray programarr = ((JSONObject) content
				.optJSONArray("day").get(0)).optJSONArray("program");
		for (int i = 0; i < programarr.length(); i++) {
			LiveItemBean liveitembean = new LiveItemBean();
			JSONObject obj = (JSONObject) programarr.get(i);
			liveitembean.setChannealid(String.valueOf(obj
					.optLong("cpId")));
			liveitembean.setTitle(obj.optString("programName"));
			// String time = DateUtils.getTime(obj.getString("cpTime"),
			// "HH:mm");
			String time = obj.optString("cpTime");
			liveitembean.setStartTime(time);
			// String endtime = DateUtils.getTime(
			// obj.getString("endTime"), "HH:mm");
			// String endtime = obj.getString("endTime");
			// liveitembean.setEndTime(endtime);
			// liveitembean.setProgramurl(obj.getString("programUrl"));
			// liveitembean.setUrlType(obj.getString("urlType"));
			// liveitembean.setType(obj.getString("type"));
			// liveitembean.setDes(obj.getString("des"));
			// liveitembean.setDesImg(obj.getString("desImg"));
			programlist.add(liveitembean);
		}
			
	}
	/**
	 * 
	 * @param response
	 * @param programlist
	 * @param sourcelist
	 */
	public static void getProgramInfo(JSONObject response,
			ArrayList<LiveItemBean> programlist,
			ArrayList<PlayUrlBean> sourcelist) {
		if (response.optInt("code") == 0) {
			try {
				JSONObject content = response.optJSONObject("content");
				AppGlobalConsts.P2PLOCKED =response.optInt("p2plock");//P2P锁
				// JSONArray programarr = content.optJSONArray("program");
				JSONArray programarr = ((JSONObject) content
						.optJSONArray("day").get(0)).optJSONArray("program");
				JSONArray sourcearr = content.optJSONArray("play");
				if (programarr == null || sourcearr == null) {
					return;
				}
				getProgramList(content, programlist);
				int count = 0;
				for (int i = 0; i < sourcearr.length(); i++) {
					if (count > 4) {
						break;
					}
					JSONObject tmp = (JSONObject) sourcearr.get(i);
					PlayUrlBean playUrlBean = new PlayUrlBean();
					playUrlBean.url = tmp.optString("url");
					playUrlBean.videoPath = tmp.optString("videoPath");
					playUrlBean.channelIdStr = tmp.optString("channelIdStr");
					// programlist.get(programlist.size() - 1).playUrl =
					// playUrlBean;
					sourcelist.add(playUrlBean);
					count++;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 解析节目集数据
	 */
	public static ArrayList<String> initEpisodeArrayList(JSONArray episodeJSONArray, String type){
		ArrayList<String> episodeArray = new ArrayList<String>();
		int totalEpisodes = episodeJSONArray.length();
		if(type.equalsIgnoreCase("综艺")||type.equalsIgnoreCase("纪录片")
				||type.equalsIgnoreCase("短视频")||type.equalsIgnoreCase("音乐")||type.equalsIgnoreCase("游戏")){
			for(int i=0; i<totalEpisodes; i++){
				episodeArray.add("第" + episodeJSONArray.optJSONObject(i).optString("volumncount") + "期");
			}
		}else if(type.equalsIgnoreCase("微电影")||type.equalsIgnoreCase("电影")){
			for(int i=0; i<totalEpisodes; i++){
				episodeArray.add(episodeJSONArray.optJSONObject(i).optString("name"));
			}
		}else {
			for(int i=0; i<totalEpisodes; i++){
				episodeArray.add("第" + episodeJSONArray.optJSONObject(i).optString("volumncount") + "集");
				
			}
		}
		return episodeArray;
	}
}
