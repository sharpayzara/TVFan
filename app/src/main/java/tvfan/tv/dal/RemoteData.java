package tvfan.tv.dal;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.AppGlobalVars;
import tvfan.tv.dal.HttpResponse.Listener4JSONObject;
import tvfan.tv.dal.models.ProgramListItem;
import tvfan.tv.lib.Lg;
import tvfan.tv.lib.Utils;
import android.content.Context;
import android.provider.Settings;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

public class RemoteData {
	final private String TAG = "TVFAN.EPG.RemoteData";
	private String _epgurl = null;
	private String _searchurl = null;
	private String _updateurl = null;
	private String _bmsOrderurl = null;
	private String _bmswxBindurl = null;
	private String _bmsPayListurl = null;
	private String _liveShowZBurl = null;

	// private HttpRequestWrapper _hrw;
	// private Context mCtx;
	public RemoteData() {
		AppGlobalVars agv = AppGlobalVars.getIns();
		if (agv != null) {
			HashMap<String, String> su = AppGlobalVars.getIns().SERVER_URL;
			_epgurl = su.get("EPG");
			_searchurl = su.get("SEARCH");
			_updateurl = su.get("UPDATE");
			_bmsOrderurl = su.get("BMSORDER");
			_bmswxBindurl = su.get("BMSWXBIND");
			_bmsPayListurl = su.get("BMSPAYLIST");
			_liveShowZBurl = su.get("LIVESHOW_ZB");
			su.clear();
		}
	}

	public RemoteData(Context context) {
		AppGlobalVars agv = AppGlobalVars.getIns();
		if (agv != null) {
			_epgurl = AppGlobalVars.getIns().SERVER_URL.get("EPG");
			_searchurl = AppGlobalVars.getIns().SERVER_URL.get("SEARCH");
			_updateurl = AppGlobalVars.getIns().SERVER_URL.get("UPDATE");
			_bmsOrderurl = AppGlobalVars.getIns().SERVER_URL.get("BMSORDER");
			_bmswxBindurl = AppGlobalVars.getIns().SERVER_URL.get("BMSWXBIND");
			_bmsPayListurl = AppGlobalVars.getIns().SERVER_URL
					.get("BMSPAYLIST");
			_liveShowZBurl = AppGlobalVars.getIns().SERVER_URL
					.get("LIVESHOW_ZB");
		}
	}

	public ProgramListItem getProgrameList() {
		return null;
	}

	public void startJsonHttpGet(String url,
			HttpResponse.Listener4JSONObject httpResponseListener) {
		if(url!=null&&url.length()>1){
			
			_doJSONRequest(url, httpResponseListener);
		}
	}

	public void getBootImg(HttpResponse.Listener4JSONObject httpResponseListener) {
		String url = _epgurl + "/program!getOpenViewList.action?templateId="
				+ AppGlobalVars.getIns().TEMPLATE_ID + "&agentVendorId="
				+ AppGlobalConsts.CHANNELS_ID;
		// url =
		// "http://172.16.11.134:8080/epg/web/v40/program!getOpenViewList.action?templateId=00080000000000000000000000000050";
		// url = "http://192.168.1.114/welpic.json";
		if(url!=null&&url.length()>1){
			
			_doJSONRequest(url, httpResponseListener);
		}
	}

	public void getUpdateVersion(
			HttpResponse.Listener4JSONObject httpResponseListener, String vn) {
		String url = _updateurl
				+ "/auth/program!getUpgradeInfor.action?deviceId="
				+ AppGlobalVars.getIns().DEVICE_ID + "&agentVendorId="
				+ AppGlobalConsts.CHANNELS_ID + "&versionId=" + vn;
		// url = "http://192.168.1.114/testupdate.json";
		if(url!=null&&url.length()>1){
			
			_doJSONRequest(url, httpResponseListener);
		}
	}

	/**
	 * 获得首页栏目导航
	 **/
	public void getPortalNav(
			HttpResponse.Listener4JSONObject httpResponseListener) {
		String url = _epgurl + "/program!getIndexMenuList.action?templateId="
				+ AppGlobalVars.getIns().TEMPLATE_ID + "&agentVendorId="
				+ AppGlobalConsts.CHANNELS_ID + "&version=" + AppGlobalConsts.VERSION;
		if(url!=null&&url.length()>1){
			
			_doJSONRequest(url, httpResponseListener);
		}
	}

	/**
	 * 获得首页各栏目内容数据
	 **/
	public void getPortalItem(String menuId,
			HttpResponse.Listener4JSONObject httpResponseListener) {
		String url = _epgurl + "/program!getIndexList.action?templateId="
				+ AppGlobalVars.getIns().TEMPLATE_ID + "&menuId=" + menuId
				+ "&agentVendorId=" + AppGlobalConsts.CHANNELS_ID + "&version=" + AppGlobalConsts.VERSION;
if(url!=null&&url.length()>1){
			
	_doJSONRequest(url, httpResponseListener);
		}
	}

	/**
	 * 获取电影栏目分类列表
	 * 
	 * @param httpResponseListener
	 */
	public void getProgramMenus(
			HttpResponse.Listener4JSONObject httpResponseListener,
			String parentCatgId, String templateId) {
		String fullurl = _epgurl
				+ "/program!getCommonMenuList.action?parentCatgId="
				+ parentCatgId + "&templateId=" + templateId;
if(fullurl!=null&&fullurl.length()>1){
			
	_doJSONRequest(fullurl, httpResponseListener);
		}
	}

	/**
	 * 获取热门标签分类列表
	 * 
	 * @param httpResponseListener
	 */
	public void getHotWordsMenus(
			HttpResponse.Listener4JSONObject httpResponseListener) {
		String fullurl = _epgurl
				+ "/bd/program!getHotWordsList.action?Templated="
				+ AppGlobalVars.getIns().TEMPLATE_ID;
if(fullurl!=null&&fullurl.length()>1){
			
	_doJSONRequest(fullurl, httpResponseListener);
		}
	}

	/**
	 * 获取筛选
	 * 
	 * @param httpResponseListener
	 */
	public void getProgramFilter(
			HttpResponse.Listener4JSONObject httpResponseListener, String type,
			String templateId) {
		String fullurl = _epgurl + "/program!getMovieSearchClass.action?type="
				+ type + "&templateId=" + templateId;
if(fullurl!=null&&fullurl.length()>1){
			
	_doJSONRequest(fullurl, httpResponseListener);
		}
	}

	/**
	 * 获取liveshow点播
	 * 
	 * @param httpResponseListener
	 */
	public void getLiveShowDB(
			HttpResponse.Listener4JSONObject httpResponseListener,
			String liveShow_db, String templateId) {
		String fullurl = liveShow_db + "?templateId=" + templateId;
		if(fullurl!=null&&fullurl.length()>1){
			
			_doJSONRequest(fullurl, httpResponseListener);
		}
	}

	/**
	 * 获取海报页列表
	 * 
	 * @param httpResponseListener
	 * @param id
	 * @param pagenum
	 * @param pagesize
	 * @param year
	 * @param area
	 * @param type
	 * @param classType
	 * @description year\area\type\classType 不用时统一传:-1
	 */
	public void getProgramList(
			HttpResponse.Listener4JSONObject httpResponseListener, String id,
			int pagenum, int pagesize, String year, String area, String type,
			String classType, String templateId) {
		String fullurl = _epgurl
				+ "/program!getCommonMovieList.action?parentCatgId=" + id
				+ "&templateId=" + templateId + "&pageNumber=" + pagenum
				+ "&pageSize=" + pagesize;
if(fullurl!=null&&fullurl.length()>1){
			
	_doJSONRequest(fullurl, httpResponseListener);
		}
	}

	/**
	 * 获取筛选列表
	 * 
	 * @param httpResponseListener
	 * @param id
	 * @param pagenum
	 * @param pagesize
	 * @param year
	 * @param area
	 * @param type
	 * @param classType
	 * @description year\area\type\classType 不用时统一传:-1
	 */
	public void getFilterProgramList(
			HttpResponse.Listener4JSONObject httpResponseListener, String id,
			int pagenum, int pagesize, String year, String area, String type,
			String classType, String templateId) {

		String encYear = "-1";
		String encArea = "-1";
		String encType = "-1";

		try {
			encYear = URLEncoder.encode(year, "UTF-8");
			encArea = URLEncoder.encode(area, "UTF-8");
			encType = URLEncoder.encode(type, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String fullurl = _epgurl
				+ "/program!getFilterMovieList.action?parentCatgId=" + id
				+ "&templateId=" + templateId + "&year=" + encYear + "&area="
				+ encArea + "&type=" + encType + "&classType=" + classType
				+ "&pageNumber=" + pagenum + "&pageSize=" + pagesize;
		if(fullurl!=null&&fullurl.length()>1){
			
			_doJSONRequest(fullurl, httpResponseListener);
		}
	}

	/**
	 * 详情页获取相关推荐接口
	 * 
	 * @param httpResponseListener
	 */
	public void getDetailRelatedRecommend(String seriesId, String movieName,
			HttpResponse.Listener4JSONObject httpResponseListener) {
		try {
			String fullUrl = _epgurl
					+ "/bd/program!getRelationList.action?seriesId=" + seriesId
//					+ "&movieName=" + URLEncoder.encode(movieName, "UTF-8")
					+ "&biType=true&templateId="
					+ AppGlobalVars.getIns().TEMPLATE_ID;
 if(fullUrl!=null&&fullUrl.length()>1){
				
	_doJSONRequest(fullUrl, httpResponseListener);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询排行榜分类
	 * @param templateId
	 * @param httpResponseListener
     */
	public void getTopCategory(String templateId, HttpResponse.Listener4JSONArray httpResponseListener){
		String categoryUrl = _epgurl+"/bd/program!getRankPTList.action?templateId=" + templateId;
		_doJSONArrayRequest(categoryUrl,httpResponseListener);
	}

	/**
	 * 查询排行榜单个详细
	 * @param templateId
	 * @param pt
	 * @param httpResponseListener
     */
	public void getTopListItem(String templateId, String pt,HttpResponse.Listener4JSONArray httpResponseListener){
		String itemUrl = _epgurl + "/bd/program!getRankList.action?templateId=" + templateId
				+ "&pt=" + pt + "&limit=10";
		_doJSONArrayRequest(itemUrl,httpResponseListener);
	}


	/**
	 * 详情页获取相关人物
	 * 
	 * @param httpResponseListener
	 */
	public void getDetailRelatedPerson(String seriesId, String movieName,
			HttpResponse.Listener4JSONObject httpResponseListener) {
		try {
			String fullUrl = _epgurl
					+ "/bd/program!getRelatedPersonList.action?seriesId="
					+ seriesId + "&movieName="
					+ URLEncoder.encode(movieName, "UTF-8") + "&templateId="
					+ AppGlobalVars.getIns().TEMPLATE_ID;
			if(fullUrl!=null&&fullUrl.length()>1){
				
				_doJSONRequest(fullUrl, httpResponseListener);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取专题汇总
	 */
	public void getSpecialList(String parentCatgId,
			HttpResponse.Listener4JSONObject httpResponseListener) {
		String fullUrl = _epgurl
				+ "/program!getSpecialList.action?parentCatgId=" + parentCatgId
				+ "&templateId=" + AppGlobalVars.getIns().TEMPLATE_ID
				+ "&pageNumber=0&pageSize=1000";
		if(fullUrl!=null&&fullUrl.length()>1){
			
			_doJSONRequest(fullUrl, httpResponseListener);
		}
	}

	/**
	 * 获取排行榜
	 */
	public void getRankList(String classType,
			HttpResponse.Listener4JSONObject httpResponseListener) {
		String fullUrl = _epgurl + "/bd/program!getRankList.action?templateId="
				+ AppGlobalVars.getIns().TEMPLATE_ID + "&classType="
				+ classType;
		if(fullUrl!=null&&fullUrl.length()>1){
			
			_doJSONRequest(fullUrl, httpResponseListener);
		}
	}

	/**
	 * 获取付费专区
	 */
	public void getPayList(HttpResponse.Listener4JSONObject httpResponseListener) {

		String fullUrl = _bmsPayListurl
				+ "/paymentRest/api/v40/pay/getPayList?templateId="
				+ AppGlobalVars.getIns().TEMPLATE_ID;
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("token", AppGlobalVars.getIns().USER_TOKEN);
		if(fullUrl!=null&&fullUrl.length()>1){
			
			_doJSONRequest(fullUrl, httpResponseListener, headers);
		}
	}

	/**
	 * 获取消费记录
	 */
	public void getConsumeList(
			HttpResponse.Listener4JSONObject httpResponseListener) {

		String fullUrl = _bmswxBindurl
				+ "/UserRest/api/v40/pay/getMyPayment?uid="
				+ AppGlobalVars.getIns().USER_ID;
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("token", AppGlobalVars.getIns().USER_TOKEN);
		if(fullUrl!=null&&fullUrl.length()>1){
			
			_doJSONRequest(fullUrl, httpResponseListener, headers);
		}
	}

	/**
	 * 获取猜你喜欢
	 */
	public void getYouLike(HttpResponse.Listener4JSONObject httpResponseListener) {

		String fullUrl = _epgurl + "/bd/program!getYourLikeList.action?uId="
				+ AppGlobalVars.getIns().USER_ID + "&templateId="
				+ AppGlobalVars.getIns().TEMPLATE_ID;
		HashMap<String, String> headers = new HashMap<String, String>();
		if(fullUrl!=null&&fullUrl.length()>1){
			
			_doJSONRequest(fullUrl, httpResponseListener, headers);
		}
	}

	/**
	 * 24小时追剧
	 */
	public void getBingeWatching(
			HttpResponse.Listener4JSONObject httpResponseListener) {

		String fullUrl = _epgurl
				+ "/bd/program!getHotProgram.action?templateId="
				+ AppGlobalVars.getIns().TEMPLATE_ID;
		HashMap<String, String> headers = new HashMap<String, String>();
		if(fullUrl!=null&&fullUrl.length()>1){
			
			_doJSONRequest(fullUrl, httpResponseListener, headers);
		}
	}

	/**
	 * 获取微信二维码票据
	 */
	public void getWeixinTicket(
			HttpResponse.Listener4JSONObject httpResponseListener) {
		String fullUrl = _bmswxBindurl
				+ "/UserRest/api/v40/pay/wxBind?deviceId="
				+ AppGlobalVars.getIns().DEVICE_ID + "&clientId="
				+ AppGlobalVars.getIns().DEVICE_ID + "&groupId="
				+ AppGlobalConsts.CHANNELS_ID + "&IP="
				+ Utils.getLocalIpAddress();
		if(fullUrl!=null&&fullUrl.length()>1){
			
			_doJSONRequest(fullUrl, httpResponseListener);
		}
	}

	/**
	 * 上报局域网ip
	 */
	public void updateTerminalIP(
			HttpResponse.Listener4JSONObject httpResponseListener) {
		String fullUrl = _bmswxBindurl
				+ "/UserRest/api/v40/pay/updateTerminalIP?deviceId="
				+ AppGlobalVars.getIns().DEVICE_ID + "&token="
				+ AppGlobalVars.getIns().USER_TOKEN + "&IP="
				+ Utils.getLocalIpAddress();
		if(fullUrl!=null&&fullUrl.length()>1){
			
			_doJSONRequest(fullUrl, httpResponseListener);
		}
	}

	/**
	 * 获取消息中心列表
	 */
	public void getMsgList(HttpResponse.Listener4JSONObject httpResponseListener) {
		String fullUrl = _epgurl
				+ "/msg/getMsgList.action?templateId=00080000000000000000000000000050&uId="
				+ AppGlobalVars.getIns().USER_ID + "&agentVendorId="
				+ AppGlobalConsts.CHANNELS_ID;
		if(fullUrl!=null&&fullUrl.length()>1){
			
			_doJSONRequest(fullUrl, httpResponseListener);
		}
	}

	/**
	 * 获取消息详情列表
	 */
	public void getDeMsgList(String msgId,
			HttpResponse.Listener4JSONObject httpResponseListener) {
		String fullUrl = _epgurl + "/msg/getMsgInfo.action?msgId=" + msgId;
		if(fullUrl!=null&&fullUrl.length()>1){
			
			_doJSONRequest(fullUrl, httpResponseListener);
		}
	}

	/**
	 * 获取我的收藏
	 */
	public void getFavoritesList(
			HttpResponse.Listener4JSONObject httpResponseListener) {
		String fullUrl = _epgurl + "/program!queryFavorite.action?uid="
				+ AppGlobalVars.getIns().USER_ID + "&templateId="
				+ AppGlobalVars.getIns().TEMPLATE_ID;
		if(fullUrl!=null&&fullUrl.length()>1){
			
			_doJSONRequest(fullUrl, httpResponseListener);
		}
	}

	/**
	 * 获取我的订购
	 */
	public void getOrderList(
			HttpResponse.Listener4JSONObject httpResponseListener) {
		String fullUrl = _bmswxBindurl
				+ "/UserRest/api/v40/pay/getMyOrder?uid="
				+ AppGlobalVars.getIns().USER_ID + "&pageIndex=1&pageSize=1000";
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("token", AppGlobalVars.getIns().USER_TOKEN);
		if(fullUrl!=null&&fullUrl.length()>1){
			
			_doJSONRequest(fullUrl, httpResponseListener, headers);
		}
	}

	/**
	 * 获取专题模板
	 */
	public void getTemplateList(String parentCatgId,
			HttpResponse.Listener4JSONObject httpResponseListener) {
		String fullUrl = _epgurl
				+ "/program!getSpecialDetail.action?specialId=" + parentCatgId
				+ "&templateId=" + AppGlobalVars.getIns().TEMPLATE_ID;
		if(fullUrl!=null&&fullUrl.length()>1){
			
			_doJSONRequest(fullUrl, httpResponseListener);
		}
	}

	/**
	 * 获取新闻专题汇总
	 */
	public void getNewsSpecialList(String parentCatgId,
			HttpResponse.Listener4JSONObject httpResponseListener) {
		String fullUrl = _epgurl
				+ "/program!getNewsSpecialList.action?parentCatgId="
				+ parentCatgId + "&templateId="
				+ AppGlobalVars.getIns().TEMPLATE_ID
				+ "&pageNumber=0&pageSize=1000";
		if(fullUrl!=null&&fullUrl.length()>1){
			
			_doJSONRequest(fullUrl, httpResponseListener);
		}
	}

	/**
	 * 详情页获取相关人物的电影
	 * 
	 * @param httpResponseListener
	 */
	public void getRelationListByPerson(String seriesId, String personId,
			HttpResponse.Listener4JSONObject httpResponseListener) {
		String fullUrl = _epgurl
				+ "/bd/program!getRelationListByPerson.action?seriesId="
				+ seriesId + "&personId=" + personId + "&templateId="
				+ AppGlobalVars.getIns().TEMPLATE_ID;
		if(fullUrl!=null&&fullUrl.length()>1){
			
			_doJSONRequest(fullUrl, httpResponseListener);
		}
	}

	/**
	 * 详情页获取相关标签
	 * 
	 * @param httpResponseListener
	 */
	public void getRelatedLabelList(String seriesId, String movieName,
			HttpResponse.Listener4JSONObject httpResponseListener) {
		try {
			String fullUrl = _epgurl
					+ "/bd/program!getRelatedLabelList.action?seriesId="
					+ seriesId + "&movieName="
					+ URLEncoder.encode(movieName, "UTF-8") + "&templateId="
					+ AppGlobalVars.getIns().TEMPLATE_ID;
			if(fullUrl!=null&&fullUrl.length()>1){
				
				_doJSONRequest(fullUrl, httpResponseListener);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 详情页获取相关剧照
	 * 
	 * @param httpResponseListener
	 */
	public void getRelatedPicture(
			HttpResponse.Listener4JSONObject httpResponseListener) {
		String fullUrl = _epgurl
				+ "/bd/program!getRelatedPicture.action?programId=0102303&movieName=武则天";
		if(fullUrl!=null&&fullUrl.length()>1){
			
			_doJSONRequest(fullUrl, httpResponseListener);
		}
	}

	/**
	 * 详情页获取电影详情
	 * 
	 * @param httpResponseListener
	 */
	public void getMovieDetail(String programSeriesId, String cpId,
			HttpResponse.Listener4JSONObject httpResponseListener) {
		String fullUrl = _epgurl
				+ "/program!getMovieDetail.action?programSeriesId="
				+ programSeriesId + "&templateId="
				+ AppGlobalVars.getIns().TEMPLATE_ID + "&cpId=" + cpId;
		// String fullUrl = AppGlobalVars.getIns().EPG_URL
		// +
		// "/web/v40/program!getMovieDetail.action?programSeriesId=00010000000000000000000000000391&sortType=asc&templateId=1419";
		if(fullUrl!=null&&fullUrl.length()>1){
			_doJSONRequest(fullUrl, httpResponseListener);
		}
	}

	/**
	 * 获取点播节目来源列表数据
	 */
	public void getMovieSourceData(String programSeriesId,
			HttpResponse.Listener4JSONArray httpResponseListener){
		String fullUrl = _epgurl
				+ "/program!getCPList.action?programSeriesId="
				+ programSeriesId + "&templateId="
				+ AppGlobalVars.getIns().TEMPLATE_ID ;
		if(fullUrl!=null&&fullUrl.length()>1){

			_doJSONArrayRequest(fullUrl, httpResponseListener);
		}
	}

	public void getYearData (String programSeriesId, String cpId, String type, HttpResponse.Listener4JSONArray httpResponseListener) {
//		http://172.16.40.88:8180/epg/web/v40/program!getTabControlList.action?programSeriesId=TVFAN-program-119883&cpId=1005&type=year
		String fullUrl = _epgurl + "/program!getTabControlList.action?programSeriesId="
				+ programSeriesId + "&cpId=" + cpId;
		if (type.equals("综艺") || type.equals("游戏") || type.equals("短视频") || type.equalsIgnoreCase("体育")) {
			fullUrl = fullUrl + "&type=year";
		}


		_doJSONArrayRequest(fullUrl, httpResponseListener);
	}
	/**
	 * 获取点播节目节目集列表数据
	 */
	public void getMovieEpisodeData(String programSeriesId, String cpId, String sortType, String start, String end, String type, String year,
			HttpResponse.Listener4JSONObject httpResponseListener){
//		http://172.16.40.88:8180/epg/web/v40/program!getMovieList.action?programSeriesId=TVFAN-program-127348&templateId=00080000000000000000000000000050&cpId=1005&start=1&end=20&sortType=asc
		String fullUrl = "";
		if(type.equalsIgnoreCase("综艺") || type.equalsIgnoreCase("纪录片")
				|| type.equalsIgnoreCase("短视频") || type.equalsIgnoreCase("音乐")
				|| type.equalsIgnoreCase("游戏") || type.equalsIgnoreCase("体育") || type.equalsIgnoreCase("微电影")) {
			fullUrl = _epgurl
					+ "/program!getMovieList.action?programSeriesId="
					+ programSeriesId + "&templateId="
					+ AppGlobalVars.getIns().TEMPLATE_ID + "&cpId=" + cpId + "&sortType=" +sortType+ "&pageNumber=" +start+"&pageSize=20"+"&year="+year;
		}
		else {
			fullUrl = _epgurl
					+ "/program!getMovieList.action?programSeriesId="
					+ programSeriesId + "&templateId="
					+ AppGlobalVars.getIns().TEMPLATE_ID + "&cpId=" + cpId + "&start=" + start + "&end=" + end + "&sortType=" + sortType;
		}
		if(fullUrl!=null&&fullUrl.length()>1){
			_doJSONRequest(fullUrl, httpResponseListener);
		}
	}

	/**
	 * 详情页获取收藏状态
	 * 
	 * @param httpResponseListener
	 */
	public void isFavorite(String programSeriesId,
			HttpResponse.Listener4JSONObject httpResponseListener) {
		String fullUrl = _epgurl
				+ "/program!isFavorite.action?programSeriesId="
				+ programSeriesId + "&uid=" + AppGlobalVars.getIns().USER_ID;
		_doJSONRequest(fullUrl, httpResponseListener);
	}

	/**
	 * 获取推荐
	 */
	public void getRecommendList(
			HttpResponse.Listener4JSONArray httpResponseListener) {
		String url = _epgurl + "/bd/program!getRandRecommendList.action?limit=10";
		if(url!=null&&url.length()>1){
			_doJSONArrayRequest(url, httpResponseListener);
		}
	}

	/**
	 * 获取搜索
	 */
	public void getSearchList(
			HttpResponse.Listener4JSONObject httpResponseListener,
			String value, int pagenum, int pagesize, String parentCatgId,
			int searchType) {
		String searchvalue = "";
		try {
			searchvalue = URLEncoder.encode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String url = _searchurl
				+ "/program!getSearchProgram.action?searchType=" + searchType
				+ "&parentCatgId=" + parentCatgId + "&searchValue="
				+ searchvalue + "&templateId="
				+ AppGlobalVars.getIns().TEMPLATE_ID + "&pageNumber=" + pagenum
				+ "&pageSize=" + pagesize + "&biType=1&uId="
				+ AppGlobalVars.getIns().USER_ID;
		if(url!=null&&url.length()>1){
			
			_doJSONRequest(url, httpResponseListener);
		}
	}

	/**
	 * 详情页添加收藏
	 * 
	 * @param httpResponseListener
	 */
	public void addFavorite(String programSeriesId,
			HttpResponse.Listener4JSONObject httpResponseListener) {
		String fullUrl = _epgurl
				+ "/program!addFavorite.action?programSeriesId="
				+ programSeriesId + "&uid=" + AppGlobalVars.getIns().USER_ID;
		// String fullUrl = AppGlobalVars.getIns().EPG_URL
		// +
		// "/web/v40/program!addFavorite.action?programSeriesId=00010000000000000000000000001495&uid=20130719185050001190";
		if(fullUrl!=null&&fullUrl.length()>1){
			
			_doJSONRequest(fullUrl, httpResponseListener);
		}
	}

	/**
	 * 详情页取消收藏
	 * 
	 * @param httpResponseListener
	 */
	public void cancelFavorite(String programSeriesId,
			HttpResponse.Listener4JSONObject httpResponseListener) {
		String fullUrl = _epgurl
				+ "/program!cancelFavorite.action?programSeriesIds="
				+ programSeriesId + "&uid=" + AppGlobalVars.getIns().USER_ID;
		// String fullUrl = AppGlobalVars.getIns().EPG_URL
		// +
		// "/web/v40/program!cancelFavorite.action?programSeriesIds=00010000000000000000000000006218,00010000000000000000000000001495&uid=20130719185050001190";
if(fullUrl!=null&&fullUrl.length()>1){
			
	_doJSONRequest(fullUrl, httpResponseListener);
		}
	}

	/**
	 * 详情页获取评分
	 * 
	 * @param httpResponseListener
	 */
	public void queryScore(String programSeriesId,
			HttpResponse.Listener4JSONObject httpResponseListener) {
		String fullUrl = _epgurl
				+ "/program!queryscore.action?programSeriesId="
				+ programSeriesId;
		// String fullUrl = AppGlobalVars.getIns().EPG_URL
		// +
		// "/web/v40/program!queryscore.action?programSeriesId=00010000000000000000000000006218";
if(fullUrl!=null&&fullUrl.length()>1){
			
	_doJSONRequest(fullUrl, httpResponseListener);
		}
	}

	/**
	 * 详情页相关标签电影搜索接口
	 * 
	 * @param httpResponseListener
	 */
	public void getSearchProgram(String value,
			HttpResponse.Listener4JSONObject httpResponseListener) {
		String fullUrl = _searchurl
				+ "/program!getSearchProgram.action?searchType=1&searchValue="
				+ value + "&templateId=" + AppGlobalVars.getIns().TEMPLATE_ID
				+ "&pageNumber=1&pageSize=1000&biType=1&uId="
				+ AppGlobalVars.getIns().USER_ID;

		// String fullUrl =
		// "http://114.247.94.15:8080/epg/web/v40/program!getSearchProgram.action?searchType=0&searchValue=xy&parentCatgId=00050000000000000000000000019649&templateId=00080000000000000000000000000050&pageNumber=1&pageSize=10&biType=1&uId=11";
if(fullUrl!=null&&fullUrl.length()>1){
			
	_doJSONRequest(fullUrl, httpResponseListener);
		}
	}

	/**
	 * 获取品牌详情接口，等同于电影分类接口
	 * 
	 * @param httpResponseListener
	 */
	public void getBrandDetail(String parentCatgId,
			HttpResponse.Listener4JSONObject httpResponseListener) {
		String fullUrl = _epgurl
				+ "/program!getCommonMenuList.action?parentCatgId="
				+ parentCatgId + "&templateId="
				+ AppGlobalVars.getIns().TEMPLATE_ID;
if(fullUrl!=null&&fullUrl.length()>1){
			
	_doJSONRequest(fullUrl, httpResponseListener);
		}
	}

	/**
	 * 获取品牌列表接口
	 * 
	 * @param httpResponseListener
	 */
	public void getBrandList(String parentCatgId,
			HttpResponse.Listener4JSONObject httpResponseListener) {
		String fullUrl = _epgurl + "/program!getBrandList.action?parentCatgId="
				+ parentCatgId + "&templateId="
				+ AppGlobalVars.getIns().TEMPLATE_ID
				+ "&pageNumber=0&pageSize=1000&type=ALL";
if(fullUrl!=null&&fullUrl.length()>1){
			
	_doJSONRequest(fullUrl, httpResponseListener);
		}
	}

	/**
	 * 获取单个品牌列表
	 * 
	 * @param httpResponseListener
	 */
	public void getSingleBrandList(String parentCatgId,
			HttpResponse.Listener4JSONObject httpResponseListener) {
		String fullUrl = _epgurl
				+ "/program!getCommonMovieList.action?parentCatgId="
				+ parentCatgId
				+ "&templateId="
				+ AppGlobalVars.getIns().TEMPLATE_ID
				+ "&year=-1&area=-1&type=电影&classType=-1&pageNumber=1&pageSize=1000";
if(fullUrl!=null&&fullUrl.length()>1){
			
	_doJSONRequest(fullUrl, httpResponseListener);
		}
	}

	/**
	 * 播放和破解失败往服务器输出日志接口
	 * @param uid  用户id
	 * @param programSeriesId  节目ID、
	 * @param src  节目原始地址
	 * @param cpId  源id
	 * @param type  节目类型“点播”---“直播”
	 * @param err  错误类型
	 * @param httpResponseListener
	 */
	public void playErrorLog(String uid, String programSeriesId,
							 String src, String cpId, String type, String err,String postUrl,
							 HttpResponse.Listener4JSONObject httpResponseListener){

		HashMap<String, String> postData = new HashMap<String, String>();
		postData.put("uId", uid);
		postData.put("pId", programSeriesId);
		postData.put("src", src);
		postData.put("cp", cpId);
		postData.put("type", type);
		postData.put("err", err);

		HashMap<String, String> headers = new HashMap<String, String>();
		if(postUrl!=null&&postUrl.length()>1){
			_doJSONRequestByPost(postUrl, httpResponseListener, postData, headers);
		}

 	}

	/**
	 * 详情页鉴权接口
	 * 
	 * @param httpResponseListener
	 */
	public void startAuthorize(String uid, String programSeriesId,
			HttpResponse.Listener4JSONObject httpResponseListener) {
		// http://114.247.94.66:8080/AuthRest/api/v40/pays/program!authentication.action?uid=UN2015033100001015&programseriesId=00010000000000000000000000008518
		String postUrl = _bmsOrderurl
				+ "/AuthRest/api/v40/pays/program!authentication.action";

		HashMap<String, String> postData = new HashMap<String, String>();
		postData.put("uid", uid);
		postData.put("programseriesId", programSeriesId);

		HashMap<String, String> headers = new HashMap<String, String>();
		// headers.put("token", "f47502512b3649f0b01ada30bf25c873");
		if (AppGlobalVars.getIns().USER_TOKEN.isEmpty()) {
			headers.put("token", "aaaaaa");
		} else {
			headers.put("token", AppGlobalVars.getIns().USER_TOKEN);
		}
		headers.put("version", "4.5.2");
	if(postUrl!=null&&postUrl.length()>1){
			
		_doJSONRequestByPost(postUrl, httpResponseListener, postData, headers);
		}
	}

	/**
	 * 详情页订购接口
	 * 
	 * @param httpResponseListener
	 */
	public void getDetailOrder(String uid, String programSeriesId,
			String parentCategoryId, String categoryId, String productId,
			String deviceId,
			HttpResponse.Listener4JSONObject httpResponseListener) {
		// http://114.247.94.66:8080/AuthRest/api/v40/pays/program!authentication.action?uid=UN2015033100001015&programseriesId=00010000000000000000000000008518
		String postUrl = _bmsOrderurl
				+ "/AuthRest/api/v40/pays/program!payDrama.action";

		HashMap<String, String> postData = new HashMap<String, String>();
		postData.put("uid", uid);
		postData.put("programSeriesId", programSeriesId);
		postData.put("parentCategoryId", parentCategoryId);
		postData.put("categoryId", categoryId);
		postData.put("productId", productId);
		postData.put("deviceId", deviceId);
		postData.put("clientId", deviceId);

		HashMap<String, String> headers = new HashMap<String, String>();
		// /headers.put("token", "f47502512b3649f0b01ada30bf25c873");
		headers.put("token", AppGlobalVars.getIns().USER_TOKEN);
		headers.put("version", "4.5.2");
		if(postUrl!=null&&postUrl.length()>1){
			
			_doJSONRequestByPost(postUrl, httpResponseListener, postData, headers);
		}
	}

	/**
	 * 获取新闻首页接口
	 */
	public void getNewsIndex(String parentCatgId,
			HttpResponse.Listener4JSONObject httpResponseListener) {
		String fullUrl = _epgurl + "/program!getNewsIndex.action?parentCatgId="
				+ parentCatgId + "&templateId="
				+ AppGlobalVars.getIns().TEMPLATE_ID;
		if(fullUrl!=null&&fullUrl.length()>1){
			
			_doJSONRequest(fullUrl, httpResponseListener);
		}
	}

	/**
	 * 获取新闻二级列表页分类接口
	 */
	public void getNewsDetailMenuList(String parentCatgId,
			HttpResponse.Listener4JSONObject httpResponseListener) {
		String fullUrl = _epgurl
				+ "/program!getNewsCommonMenuList.action?parentCatgId="
				+ parentCatgId + "&templateId="
				+ AppGlobalVars.getIns().TEMPLATE_ID;
if(fullUrl!=null&&fullUrl.length()>1){
			
	_doJSONRequest(fullUrl, httpResponseListener);
		}
	}

	/**
	 * 获取新闻二级列表页列表接口
	 */
	public void getNewsDetailList(String parentCatgId, int pageNumber,
			int pageSize, HttpResponse.Listener4JSONObject httpResponseListener) {
		try {
			String fullUrl = _epgurl
					+ "/program!getCommonMovieList.action?parentCatgId="
					+ parentCatgId + "&templateId="
					+ AppGlobalVars.getIns().TEMPLATE_ID
					+ "&year=-1&area=-1&type="
					+ URLEncoder.encode("新闻", "UTF-8")
					+ "&classType=-1&pageNumber=" + pageNumber + "&pageSize="
					+ pageSize;
			if(fullUrl!=null&&fullUrl.length()>1){
				
				_doJSONRequest(fullUrl, httpResponseListener);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设备登录
	 **/
	public void startDeviceLogin(String lanMac,
			HttpResponse.Listener4JSONObject httpResponseListener) {
		// http://172.16.11.134:8081/userCenter/tms/v40/auth/program!deviceAuthentication.action?mac=C8:0E:77:30:77:64&agentVendorId=00940000000000000000000000000012
		String fullUrl = AppGlobalConsts.TMS_URL
				+ "/auth/program!deviceAuthentication.action?mac=" + lanMac
				+ "&agentVendorId=" + AppGlobalConsts.CHANNELS_ID;
if(fullUrl!=null&&fullUrl.length()>1){
			
	_doJSONRequest(fullUrl, httpResponseListener);
		}
	}

	/**
	 * 请求P2P直播停止
	 */
	public void stopP2P(String url,
			HttpResponse.Listener4XmlPullParser httpResponseListener) {
		String fullUrl = url;
if(fullUrl!=null&&fullUrl.length()>1){
			
	_doXMLRequest(fullUrl, httpResponseListener);
		}
	}

	/**
	 * 直播台
	 * 
	 * @param url
	 * @param httpResponseListener
	 */
	/*
	 * public void liveStation( HttpResponse.Listener4JSONObject
	 * httpResponseListener) { String fullUrl =
	 * "http://114.247.94.15:8080/epg/web/v40/ch/program!getChannelGroup.action"
	 * ; _doJSONRequest(fullUrl, httpResponseListener); }
	 *//**
	 * 频道
	 * 
	 * @param url
	 * @param httpResponseListener
	 */
	/*
	 * public void liveChannel(String groupId, HttpResponse.Listener4JSONObject
	 * httpResponseListener) { String fullUrl = String .format(
	 * "http://114.247.94.15:8080/epg/web/v40/program!getChannelList.action?channelGroupId=%s"
	 * , groupId); // fullUrl = "http://www.qinziyou.com/cctv.txt";
	 * _doJSONRequest(fullUrl, httpResponseListener); }
	 *//**
	 * 节目
	 * 
	 * @param url
	 * @param httpResponseListener
	 */
	/*
	 * public void liveProgram(String uuId, String des,
	 * HttpResponse.Listener4JSONObject httpResponseListener) { String fullUrl =
	 * String .format(
	 * "http://114.247.94.15:8080/epg/web/v40/program!getProgramAlltimeByUuid.action?uuId=%s&sortOrder=%s"
	 * , uuId, des); // fullUrl = "http://www.qinziyou.com/epg.txt";
	 * _doJSONRequest(fullUrl, httpResponseListener); }
	 */

	/**
	 * 
	 * 直播台
	 * 
	 * 
	 * 
	 * @param url
	 * 
	 * @param httpResponseListener
	 */

	public void liveStation(

	HttpResponse.Listener4JSONObject httpResponseListener) {

		// String fullUrl =
		// "http://114.247.94.15:8080/epg/web/v40/ch/program!getChannelGroup.action";

		// String fullUrl =
		// "http://172.16.11.250:8080/broadcast/getChannelGroup";
		String fullUrl = AppGlobalVars.getIns().SERVER_URL.get("LIVEPLAY")
				+ "/getChannelGroup";
if(fullUrl!=null&&fullUrl.length()>1){
			
	_doJSONRequest(fullUrl, httpResponseListener);
		}

	}

	/**
	 * 
	 * 频道
	 * 
	 * 
	 * 
	 * @param url
	 * 
	 * @param httpResponseListener
	 */

	public void liveChannel(String groupId,

	HttpResponse.Listener4JSONArray httpResponseListener) {

		/*
		 * String fullUrl = String
		 * 
		 * .format(
		 * "http://114.247.94.15:8080/epg/web/v40/program!getChannelList.action?channelGroupId=%s"
		 * ,
		 * 
		 * groupId);
		 */

		String fullUrl = String

		.format(AppGlobalVars.getIns().SERVER_URL.get("LIVEPLAY")
				+ "/getChannelList?channelGroupId=%s",

		groupId);

		// fullUrl = "http://www.qinziyou.com/cctv.txt";

		// _doJSONRequest(fullUrl, httpResponseListener);
if(fullUrl!=null&&fullUrl.length()>1){
			
	_doJSONArrayRequest(fullUrl, httpResponseListener);
		}

	}

	/**
	 * 
	 * 节目
	 * 
	 * 
	 * 
	 * @param url
	 * 
	 * @param httpResponseListener
	 */

	public void liveProgram(String uuId, String des,

	HttpResponse.Listener4JSONArray httpResponseListener) {

		String fullUrl = String

		.format(AppGlobalVars.getIns().SERVER_URL.get("LIVEPLAY")
				+ "/getProgramAlltimeByChannelId?channelid=%s",

		uuId);

		// fullUrl = "http://www.qinziyou.com/epg.txt";

if(fullUrl!=null&&fullUrl.length()>1){
			
	_doJSONArrayRequest(fullUrl, httpResponseListener, null);
		}

	}

	public void getLiveChannel(
			HttpResponse.Listener4JSONObject listener4jsonObject) {
		// String postUrl = AppGlobalConsts.TVFAN_SERVER;
		String postUrl = AppGlobalVars.getIns().SERVER_URL.get("LIVEAPI_CHANNEL");
//		String postUrl = "http://172.16.16.71:8284/liveapi/tms/v40/live/program!channelListation.do";
		JSONObject postData = new JSONObject();
		try {
			// postData.put("method", "channelTypeListTV");
			// postData.put("client", 41);
			// postData.put("jsession", "49426231-0518-41c3-b301-04ac372e3f3c");
			// postData.put("version", "1.0.0");
			// postData.put("updateCached", "1");
			// postData.put("ids", "1,2，,7");

			postData.put("client", 41);

		} catch (JSONException e) {
			e.printStackTrace();
		}
if(postUrl!=null&&postUrl.length()>1){
			
	_doJSONObjectRequestByPost(postUrl, listener4jsonObject, postData, null);
		}
	}

	private void _doJSONObjectRequestByPost(String postUrl,
			Listener4JSONObject listener4jsonObject, JSONObject postData,
			Object object) {
		HttpRequestWrapper.epgJsonObjectRequestByPost(postUrl, postData,
				listener4jsonObject, null);
	}

	public void getLiveProgram(
			HttpResponse.Listener4JSONObject listener4jsonObject,
			String channelId) {
		String postUrl = AppGlobalVars.getIns().SERVER_URL.get("LIVEAPI_PROGRAM");
//		String postUrl = "http://172.16.16.71:8284/liveapi/tms/v40/live/program!channelDetailation.do";
		// String postUrl = AppGlobalConsts.TVFAN_SERVER;
		JSONObject postData = new JSONObject();
		try {
			// postData.put("method", "channelContentTV");
			postData.put("client", 41);
			// postData.put("jsession", "49426231-0518-41c3-b301-04ac372e3f3c");
			// postData.put("version", "1.0.0");
			postData.put("channelId", Integer.parseInt(channelId));
			// postData.put("ids", "1,2，,7");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if(postUrl!=null&&postUrl.length()>1){
			
			_doJSONObjectRequestByPost(postUrl, listener4jsonObject, postData, null);
		}
	}

	/**
	 * 获取直播列表
	 * 
	 * @param listener4jsonObject
	 */
	public void getLiveShowList(
			HttpResponse.Listener4JSONObject listener4jsonObject) {
		// String url = "http://124.127.46.59:8080/forcetech/getLiveShowList";
		// String url =
		// "http://show.live.ott.cibntv.net:8080/forcetech/getLiveShowList";
		String url = _liveShowZBurl;
		if(url!=null&&url.length()>1){
			
			_doJSONRequest(url, listener4jsonObject);
		}
	}

	/**
	 * 获取时间戳接口，用来刷新首页内容
	 * 
	 * @param listener4jsonObject
	 */
	public void getLastUpdateTime(
			HttpResponse.Listener4JSONObject listener4jsonObject) {
		String url = _epgurl + "/program!getLastUpdateTime.action";
if(url!=null&&url.length()>1){
	_doJSONRequest(url, listener4jsonObject);
			
		}
	}

	/**
	 * 获取直播详情接口
	 */
	public void getLiveDetail(String id,
			HttpResponse.Listener4JSONObject listener4jsonObject) {
		String url = _epgurl + "/program!getLiveDetail.action?programSeriesId="
				+ id + "&templateId=" + AppGlobalVars.getIns().TEMPLATE_ID;
if(url!=null&&url.length()>1){
			
	_doJSONRequest(url, listener4jsonObject);
		}
	}

	private void _doJSONArrayRequest(final String fullurl,
			final HttpResponse.Listener4JSONArray httpResponseListener) {
if(fullurl!=null&&fullurl.length()>1){
			
	_doJSONArrayRequest(fullurl, httpResponseListener, null);
		}
	}

	private void _doJSONRequest(final String fullurl,
			final HttpResponse.Listener4JSONObject httpResponseListener) {
if(fullurl!=null&&fullurl.length()>1){
			
	_doJSONRequest(fullurl, httpResponseListener, null);
		}
	}

	private void _doJSONArrayRequest(final String fullurl,
			final HttpResponse.Listener4JSONArray httpResponseListener,
			HashMap<String, String> headers) {
		HttpRequestWrapper.epgJSONArrayRequest(fullurl,
				new Response.Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray jsonarr) {
						try {
							httpResponseListener.onResponse(jsonarr);
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						if (error.networkResponse == null) {
							httpResponseListener.onError("000");
							return;
						}
						try {
							// AppGlobalVars.getIns().HTTP_STACK.put(
							// String.valueOf(System.currentTimeMillis()),
							// String.valueOf(error.networkResponse.statusCode)
							// + "|" + fullurl);
							Lg.e(TAG,
									String.valueOf(error.networkResponse.statusCode)
											+ "|" + fullurl);
							httpResponseListener.onError(String
									.valueOf(error.networkResponse.statusCode));

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	private void _doJSONRequest(final String fullurl,
			final HttpResponse.Listener4JSONObject httpResponseListener,
			HashMap<String, String> headers) {
		HttpRequestWrapper.epgJSONRequest(fullurl,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						httpResponseListener.onResponse(response);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						try {
							String ei = _getErrorCode(error);
							// AppGlobalVars.getIns().HTTP_STACK.put(
							// String.valueOf(System.currentTimeMillis()),
							// ei + "|" + fullurl);
							Lg.e(TAG, ei + "|" + fullurl);
							httpResponseListener.onError(ei);

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, headers);
	}

	private void _doJSONRequestByPost(final String postUrl,
			final HttpResponse.Listener4JSONObject httpResponseListener,
			HashMap<String, String> postData, HashMap<String, String> headers) {
		HttpRequestWrapper.epgJSONRequestByPost(postUrl,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						httpResponseListener.onResponse(response);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						try {
							String ei = _getErrorCode(error);
							// AppGlobalVars.getIns().HTTP_STACK.put(
							// String.valueOf(System.currentTimeMillis()),
							// ei + "|" + postUrl);
							Lg.e(TAG, ei + "|" + postUrl);
							httpResponseListener.onError(ei);

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, postData, headers);
	}

	private void _doXMLRequest(final String fullurl,
			final HttpResponse.Listener4XmlPullParser httpResponseListener) {
		HttpRequestWrapper.epgXMLRequest(fullurl,
				new Listener<XmlPullParser>() {

					@Override
					public void onResponse(XmlPullParser response) {
						httpResponseListener.onResponse(response);

					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						try {
							String ei = _getErrorCode(error);
							// AppGlobalVars.getIns().HTTP_STACK.put(
							// String.valueOf(System.currentTimeMillis()),
							// ei + "|" + fullurl);
							Lg.e(TAG, ei + "|" + fullurl);
							httpResponseListener.onError(ei);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	private String _getErrorCode(VolleyError error) {
		String ret = "000";// 网络异常
		if (error instanceof TimeoutError) {
			ret = "800";// 超时
		} else if (error instanceof ServerError
				|| error instanceof AuthFailureError) {
			if (error.networkResponse != null) {
				ret = String.valueOf(error.networkResponse.statusCode);// HTTP
																		// STATUS
																		// CODE
			}
		} else if (error instanceof NetworkError
				|| error instanceof NoConnectionError) {
			ret = "802";// 网络连接错误
		}
		return ret;
	}

	public void getLiveShowSpecial(String url,
			HttpResponse.Listener4JSONObject httpResponseListener) {
		if(url!=null&&url.length()>1){
			
			_doJSONRequest(url, httpResponseListener);
		}
	}

	/**
	 * 关闭p2p服务
	 * @param url
	 * @param listener4jsonObject
	 */
	public void closeP2P(String url, Listener4JSONObject listener4jsonObject) {
		_doJSONRequest(url, listener4jsonObject);
	}

}
