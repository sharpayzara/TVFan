package tvfan.tv.ui.gdx.programDetail;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.luxtone.lib.image.LoaderListener;
import com.luxtone.lib.image.PageImageLoader;
import com.luxtone.lib.utils.Utils;
import com.luxtone.lib.widget.CullGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

import tvfan.tv.App;
import tvfan.tv.AppGlobalConsts;
import tvfan.tv.AppGlobalVars;
import tvfan.tv.BasePage;
import tvfan.tv.R;
import tvfan.tv.dal.HttpResponse.Listener4JSONArray;
import tvfan.tv.dal.HttpResponse.Listener4JSONObject;
import tvfan.tv.dal.JSONParser;
import tvfan.tv.dal.LocalData;
import tvfan.tv.dal.PlayRecordHelpler;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.dal.models.MPlayRecordInfo;
import tvfan.tv.dal.models.ProgramSourceBean;
import tvfan.tv.lib.Lg;
import tvfan.tv.lib.NetWorkUtils;
import tvfan.tv.ui.andr.play.baseplay.dateparser.DataParser;
import tvfan.tv.ui.gdx.programDetail.group.DetailGroup;
import tvfan.tv.ui.gdx.programDetail.group.EpisodeGroup;
import tvfan.tv.ui.gdx.programDetail.group.RecommendGroup;
import tvfan.tv.ui.gdx.programDetail.group.VideoSourceGroup;
import tvfan.tv.ui.gdx.widgets.CIBNLoadingView;

public class Page extends BasePage implements LoaderListener {

	private static final String TAG = "DETAIL.PAGE";

	private Image detailBg = null;// 背景图片
	private Image line;
	private DetailGroup dg; // 详情
	private RecommendGroup rg; // 推荐
	private EpisodeGroup eg;//节目集列表
	private VideoSourceGroup vsg;//源列表
	private static Page instance;
	private CIBNLoadingView loadingview;
	private CullGroup cullGroup;
	
	private int bgresid = -1;// 背景图片资源id
	private int pageNumber = 1;;//分页的记录
	private int yearNum = 0;//年代的记录，，，，电影、电视剧、动漫表示当前的分页记录
	private String programSeriesId = "";
	private String url;
	private String year;
	private boolean is_first_in = true;

	private ArrayList<Group> groupList = new ArrayList<Group>();
	private ArrayList<ProgramSourceBean> videoSourceList;
	private ArrayList<String> yearList;

	private PageImageLoader bgLoader;
	private RemoteData rd;

	private JSONParser movieDetailParser;
	private JSONObject detailObject = null;
	private JSONObject recommendObject = null;
	private JSONObject episodeJSONObject;
	private JSONArray yearJSONArray;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		App.addPage(this);
		if (instance != null) {
			instance.finish();
			instance = null;
		}
		instance = this;
		programSeriesId = bundle.getString("programSeriesId", ""); // 获取节目id,可能为""
		rd = new RemoteData(getActivity());

		loadingview = new CIBNLoadingView(this);
		loadingview.setVisible(true);
		addActor(loadingview);

		// 详情页统计日志
		postLogToBackStage();
		// 可能为""
		initView();
	}

	private void postLogToBackStage() {
		try {
			Log.d("play.Page","开始统计详情页日志了");
			String param = "programSerialId=" + programSeriesId + "&uId="
					+ AppGlobalVars.getIns().USER_ID + "&deviceId="
					+ AppGlobalVars.getIns().DEVICE_ID + "&path="
					+ URLEncoder.encode(getCurrentPath(), "UTF-8")
					+ "&timestamp=" + System.currentTimeMillis();

			Intent intent = new Intent();
			intent.setAction(AppGlobalConsts.LOCAL_MSG_FILTER.LOG_WRITE
					.toString());
			intent.putExtra(AppGlobalConsts.INTENT_LOG_CMD_NAME,
					AppGlobalConsts.LOG_CMD.DETAIL_PAGE_PATH.name());
			intent.putExtra(AppGlobalConsts.INTENT_LOG_PARAM, param);
			getActivity().sendBroadcast(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Page getInstance() {
		return instance;
	}

	@Override
	public void onResume() {
		super.onResume();
		if(info != null)
			info = null;
		info = new PlayRecordHelpler(Page.this
				.getActivity()).getPlayRcInfo(programSeriesId);
		setHistory(false);
		if (dg != null && !is_first_in) {
			dg.onResume();
		}
		is_first_in = false;
	}

	private MPlayRecordInfo info;
	private void setHistory (boolean isChangeSource) {
		if(info != null && episodeJSONObject!=null){
			if((pageNumber!=info.getPageNum() || yearNum!=info.getYearNum()) && !isChangeSource) {
				pageNumber = info.getPageNum();
				yearNum = info.getYearNum();
				String type = movieDetailParser.getString("type");
				if (type.equals("电影") || type.equals("电视剧") || type.equals("动漫")) {
					String pagingStr = yearList.get(yearNum);
					String start = pagingStr.substring(0, pagingStr.indexOf("-"));
					String end = pagingStr.substring(pagingStr.indexOf("-") + 1, pagingStr.length());
					getEpisodeData(cpId, isChangeSource, "", start, end, true);
				}
				else {
					year = yearList.get(yearNum);
					getEpisodeData(cpId, isChangeSource, year, pageNumber+"", "", true);
				}
			}
			else {
				setHistory1();
			}
		}
	}

	private void setHistory1 () {
		String historyStr = "暂无播放历史";
		ArrayList<String> episodeArray = DataParser.initEpisodeArrayList(episodeJSONObject.optJSONArray("sources"), movieDetailParser.getString("type"));
		if (cpId.equals(info.getCpId())) {
			int currPlayerpos = info.getPlayerpos();
			historyStr = episodeArray.get(currPlayerpos);
			dg.setIsHistory(true);
			if(dg.getPlayBtn().isFocused())
				dg.setPlayButton(R.mipmap.continue_play_selected);
			else
				dg.setPlayButton(R.mipmap.continue_play_normal);
		}
		else {
			dg.setIsHistory(false);
			if(dg.getPlayBtn().isFocused())
				dg.setPlayButton(R.mipmap.detail_play_selected);
			else
				dg.setPlayButton(R.mipmap.detail_play_normal);
		}
		dg.setHistory(historyStr);
		dg.setEpisodeObject(episodeJSONObject);
	}
	@Override
	protected void onResumeTextures() {
		super.onResumeTextures();
		resetDefault();
	}

	private void resetDefault() {
		setBackGroud(url);
	}

	private void initView() {

		loadBG();

		loadingview.toFront();
		// 获取详情信息
		rd.getMovieDetail(programSeriesId, "", new Listener4JSONObject() {

			@Override
			public void onResponse(final JSONObject response) {
				if (isDisposed()) {
					return;
				}
				if (response.toString().equals("{}")) {

					return;
				}
				Lg.e(TAG, "detail page get detail  response ");
				detailObject = response;
				try {
					Gdx.app.postRunnable(new Runnable() {

						@Override
						public void run() {
							movieDetailParser = new JSONParser(response);
							getRecommend();
						}
					});
				} catch (Exception e) {
					Lg.e(TAG, e.getMessage());
					e.printStackTrace();
				}
			}

			@Override
			public void onError(final String errorMessage) {
				NetWorkUtils.handlerError(errorMessage, Page.this);
			}
		});
	}

	/**
	 * 获取节目源数据
	 */
	private void getSourceData() {
		rd.getMovieSourceData(programSeriesId, new Listener4JSONArray() {
			@Override
			public void onResponse(final JSONArray response) {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						if(response!=null && !"[]".equals(response.toString())){
							String type = movieDetailParser.getString("type");
							if (info != null) {
								pageNumber = info.getPageNum();
								yearNum = info.getYearNum();
								cpId = info.getCpId();
								initVideoSourceData(response);
								if (type.equals("综艺") || type.equals("游戏") || type.equals("短视频") || type.equalsIgnoreCase("体育")) {
									getYearData(pageNumber, cpId, false);
								} else if (type.equals("电影") || type.equals("电视剧") || type.equals("动漫"))
									getYearData(yearNum, cpId, false);
								else
									getEpisodeData(cpId, false, "", ""+pageNumber,"", false);
							} else {
								initVideoSourceData(response);
								if (type.equals("综艺") || type.equals("游戏") || type.equals("短视频")|| type.equalsIgnoreCase("体育"))
									getYearData(0, cpId, false);
								else if (type.equals("电影") || type.equals("电视剧") || type.equals("动漫"))
									getYearData(0, cpId, false);
								else
									getEpisodeData(cpId, false, "", ""+pageNumber, "", false);
							}
							//hasConnectOver();
						}
						else {
							Page.this.finish();
							Toast.makeText(getActivity(), "源已失效，建议换个节目看看!!", Toast.LENGTH_SHORT).show();
						}
					}
				});
			}

			@Override
			public void onError(String errorMessage) {
				Toast.makeText(getActivity(), "源已失效，建议换个节目看看!!", Toast.LENGTH_SHORT).show();
				NetWorkUtils.handlerError(errorMessage, Page.this);
				Page.this.finish();
			}
		});
	}

	private String cpId;
	/**
	 * 初始化视频来源的数据
	 */
	public void initVideoSourceData(JSONArray sourceArray) {
		if (sourceArray.length() == 0 || sourceArray == null) {
			App.mToast(getActivity(), "源已失效，麻烦换个节目看看!!");
			this.finish();
			return;
		}
		if (videoSourceList != null && videoSourceList.size() > 0) {
			videoSourceList.clear();
		} else {
			videoSourceList = new ArrayList<ProgramSourceBean>();
		}
		try {
			int count = sourceArray.length();
			if (sourceArray != null && count > 0) {
				for (int i = 0; i < count; i++) {
					JSONObject sourceObj = sourceArray.getJSONObject(i);
					if(TextUtils.isEmpty(cpId))
						cpId = sourceObj.optString("cpId");
					String cpid = sourceObj.optString("cpId");
					String cpName = sourceObj.optString("cpName");
					String updateCount = sourceObj.optString("volumnCount");
					String cpPic = "";
					if (sourceObj.has("cpPic")) {
						cpPic = sourceObj.optString("cpPic");
					}

					ProgramSourceBean source = new ProgramSourceBean(cpid,
							cpName, cpPic, updateCount);

					videoSourceList.add(source);
				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	/**
	 * 获取年代列表
	 */
	public void getYearData(final int pageNum, final String cpId, final boolean isChangeSource){
		rd.getYearData(programSeriesId, cpId, movieDetailParser.getString("type"),
				new Listener4JSONArray() {
					@Override
					public void onResponse(final JSONArray response) throws JSONException {
						Gdx.app.postRunnable(new Runnable() {
							@Override
							public void run() {
								Gdx.app.postRunnable(new Runnable() {
									@Override
									public void run() {
										yearJSONArray = response;
										if(dg != null)
											dg.setYearJSONArray(yearJSONArray);
										String type = movieDetailParser.getString("type");
										if (type.equals("综艺") || type.equals("游戏") || type.equals("短视频")|| type.equals("体育")) {
											if (response != null && !"[]".equals(response.toString())) {
												initYearArray(response);
												if (isChangeSource) {
													eg.setYearList(yearList);
												}
												year = yearList.get(yearNum);
												getEpisodeData(cpId, isChangeSource, year, ""+pageNum, "", false);
											} else {
												getEpisodeData(cpId, isChangeSource, "", ""+pageNum, "", false);
											}
										} else if (type.equals("电影") || type.equals("电视剧") || type.equals("动漫")) {
											if (response != null && !"[]".equals(response.toString())) {
												initPagingArray(response);
												if (isChangeSource)
													eg.setYearList(yearList);
												String pagingStr = yearList.get(yearNum);
												String start = pagingStr.substring(0, pagingStr.indexOf("-"));
												String end = pagingStr.substring(pagingStr.indexOf("-") + 1, pagingStr.length());
												getEpisodeData(cpId, isChangeSource, "", start, end, false);
											}
										}

									}
								});
							}
						});
					}

					@Override
					public void onError(String errorMessage) {
						NetWorkUtils.handlerError(errorMessage, Page.this);
					}
				});
	}

	private void initPagingArray(JSONArray response) {
		int start = Integer.parseInt(response.optString(0));
		int end = Integer.parseInt(response.optString(1));
		int totalEpisodes = end - start +1;
		yearList = new ArrayList<String>();
		int right = totalEpisodes % 20 == 0 ? totalEpisodes / 20 : totalEpisodes / 20 + 1;
		for (int i = 0; i < right; i++) {
			if (20 * (i + 1) <= totalEpisodes) {
				yearList.add(20 * i + start + "-"
						+ (20 * (i + 1)+start-1));
			} else {
				yearList.add(20 * i + start + "-"
						+ end );
				break;
			}
		}
	}

	private void initYearArray(JSONArray response) {
		yearList = new ArrayList<String>();
		int count = response.length();
		for(int i=0; i<count; i++) {
			yearList.add(response.optString(i));
		}
	}

	/**
	 * 获取节目集列表的数据
	 * @param cpId
	 * @param isChangeSource
	 * @param year
	 * @param start
	 * @param end
	 */
	public void getEpisodeData(final String cpId, final boolean isChangeSource, String year, String start, String end, final boolean isBaclFromPlay) {
		String type = movieDetailParser.getString("type");
		String sortType = "asc";
		if (type.equalsIgnoreCase("综艺") || type.equalsIgnoreCase("纪录片")
				|| type.equalsIgnoreCase("短视频") || type.equalsIgnoreCase("音乐")
				|| type.equalsIgnoreCase("游戏") || type.equalsIgnoreCase("体育")) {
			sortType = "desc";
		}
		rd.getMovieEpisodeData(programSeriesId, cpId, sortType, start, end, movieDetailParser.getString("type"), year,
				new Listener4JSONObject() {
					@Override
					public void onResponse(final JSONObject response) {
						Gdx.app.postRunnable(new Runnable() {
							@Override
							public void run() {
								if (response != null
										&& !"{}".equals(response.toString())) {
									episodeJSONObject = response;
									if (isChangeSource) {
										dg.setcpId(cpId);
										dg.setEpisodeObject(response);
										dg.setYearNum(yearNum);
										dg.setPagingNum(pageNumber);
										eg.changeSource(cpId, response.optInt("totalNumber", 1), response.optJSONArray("sources"), yearJSONArray);
										setHistory(true);
									} else if(isBaclFromPlay) {
										setHistory1();
									}else {
										hasConnectOver();
									}
								} else {
									Toast.makeText(getActivity(), "源已失效，建议换个节目看看!!", Toast.LENGTH_SHORT).show();
									Page.this.finish();
								}
							}
						});
					}

					@Override
					public void onError(String httpStatusCode) {
						NetWorkUtils.handlerError(httpStatusCode, Page.this);
						Toast.makeText(getActivity(), "源已失效，建议换个节目看看!!", Toast.LENGTH_SHORT).show();
						Page.this.finish();
					}
				});
	}

	private void getRecommend() {
		// 获取相关
		rd.getDetailRelatedRecommend(programSeriesId,
				movieDetailParser.getString("name"), new Listener4JSONObject() {

					@Override
					public void onResponse(JSONObject response) {
						if (isDisposed()) {
							return;
						}
						if (response == null) {
							return;
						}
						recommendObject = response;
						getSourceData();
					}

					@Override
					public void onError(String errorMessage) {
						getSourceData();
					}

				});
	}

	/**
	 * 将节目详情、相关推荐、相关标签几个栏目加到DetailVeritcalViewPager中
	 */
	private synchronized void hasConnectOver() {
		Gdx.app.postRunnable(new Runnable() {

			@Override
			public void run() {
				loadingview.setVisible(false);
				if(cullGroup == null) {
					cullGroup = new CullGroup(Page.this);
					cullGroup.setPosition(0.0f, 0.0f);
					cullGroup.setSize(AppGlobalConsts.APP_WIDTH, AppGlobalConsts.APP_HEIGHT);
					addActor(cullGroup);

					line = new Image(Page.this);
					line.setPosition(80.0f, 420.0f);
					line.setSize(1760.0f, 1.0f);
					line.setDrawableResource(R.mipmap.detai_line);
					cullGroup.addActor(line);
				}

				if(dg == null) {
					dg = new DetailGroup(Page.this, programSeriesId,
							movieDetailParser, detailObject, videoSourceList,
							episodeJSONObject);
					dg.setPosition(80.0f, 450.0f);
					dg.setName("dg");
					dg.setcpId(cpId);
					dg.setYearJSONArray(yearJSONArray);
					cullGroup.addActor(dg);
					groupList.add(dg);
					dg.setButtonFoucusChangeListener(new DetailGroup.OnButtonFoucusChangeListener() {
						@Override
						public void onFoucusChange(Actor actor, boolean hasFousChange) {
							if (hasFousChange) {
								if(eg == null) {
									if (actor == dg.getVideoSourceBtn()) {
										cullGroup.addActor(vsg);
									} else if (actor == dg.getFavBtn()) {
										cullGroup.removeActor(vsg);
									}
								}
								else if(rg == null) {
									if (actor == dg.getVideoSourceBtn()) {
										cullGroup.addActor(vsg);
									} else if (actor == dg.getFavBtn()) {
										cullGroup.removeActor(vsg);
									}
								}
								else {
									if (actor == dg.getPlayBtn() && rg != null) {
										cullGroup.removeActor(eg);
										cullGroup.addActor(rg);
									} else if (actor == dg.getEpsiodeBtn() && rg != null) {
										cullGroup.removeActor(rg);
										cullGroup.addActor(eg);
									} else if (actor == dg.getVideoSourceBtn()) {
										cullGroup.addActor(vsg);
									} else if (actor == dg.getFavBtn()) {
										cullGroup.removeActor(vsg);
										cullGroup.removeActor(eg);
										cullGroup.addActor(rg);
									}
								}
							}
						}
					});
					dg.getPlayBtn().requestFocus();
				}

				if (recommendObject != null && rg == null
						&& recommendObject.has("programList")
						&& recommendObject.optJSONArray("programList")
								.length() > 0) {
					rg = new RecommendGroup(Page.this, recommendObject);
					rg.setName("rg");
					cullGroup.addActor(rg);
					groupList.add(rg);
				}

				if(episodeJSONObject != null && eg == null
						&& episodeJSONObject.has("sources")
						&& episodeJSONObject.optJSONArray("sources").length()>0) {
					String type = detailObject.optString("type");
					JSONArray episodeArray = episodeJSONObject.optJSONArray("sources");
					eg = new EpisodeGroup(Page.this, type, episodeArray, programSeriesId, cpId,
							detailObject, yearJSONArray, yearList, pageNumber, yearNum, episodeJSONObject.optInt("totalNumber"));
					eg.setName("eg");
					eg.setOnEpisodeChangeListener(new EpisodeGroup.OnEpisodeChangeListener() {
						@Override
						public void onEpisodeChange(JSONObject response) {
							episodeJSONObject = response;
						}
					});
					if(rg == null)
						addActor(eg);
				}

				if(videoSourceList != null && vsg == null) {
					vsg = new VideoSourceGroup(Page.this, videoSourceList);
					vsg.setNextFocusUp("vsg");
					vsg.setPosition(1070.0f, 55.0f);
					vsg.toFront();
					vsg.setOnCpChangeListener(new VideoSourceGroup.OnCpChangeListener() {
						@Override
						public void onCpChange(String cpId) {
							Page.this.cpId = cpId;
							dg.setCpName(cpId, true);
							cullGroup.removeActor(vsg);
							info = new PlayRecordHelpler(Page.this
								.getActivity()).getPlayRcInfo(programSeriesId);
							String type = movieDetailParser.getString("type");
							if (info != null && cpId.equals(info.getCpId())) {
								yearNum = info.getYearNum();
								pageNumber = info.getPageNum();
							}
							else {
								yearNum = 0;
								pageNumber = 1;
							}
							if (type.equals("综艺") || type.equals("游戏") || type.equals("短视频")|| type.equals("体育")){
								getYearData(pageNumber, cpId, true);
							}
							else if (type.equals("电影") || type.equals("电视剧") || type.equals("动漫")) {
								getYearData(yearNum, cpId, true);
							}
							else {
								getEpisodeData(cpId, true, "", ""+pageNumber, "", false);
							}
						}
					});
				}
			}

		});
	}

	@Override
	public void recyclePage() {
		super.recyclePage();
	}

	@Override
	public void onDispose() {
		instance = null;
		if (dg != null)
			dg.clear();
		super.onDispose();
		App.removePage(this);
	}

	@Override
	public void onPause() {
		super.onPause();
	};

	public void setBackGroud(String url) {
		this.url = url;
		if (bgLoader != null) {
			bgLoader.cancelLoad();
		}
		bgLoader = new PageImageLoader(this);
		bgLoader.startLoadBitmapByFilter(url, "bg", false, 0, this, url);
	}

	@Override
	public void onLoadComplete(String url, TextureRegion textureRegion,
			Object obj) {
	}

	@Override
	public boolean onBackKeyDown() {
		return super.onBackKeyDown();
	}

	/**
	 * 设置背景
	 */
	private void loadBG() {
		if (detailBg != null) {
			detailBg.dispose();
			detailBg = null;
		}

		detailBg = new Image(this);
		detailBg.setPosition(-50, -50);
		detailBg.setSize(AppGlobalConsts.APP_WIDTH + 100,
				AppGlobalConsts.APP_HEIGHT + 100);
		detailBg.setFocusAble(false);
		detailBg.setAlpha(0.95f);

		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				addActor(detailBg);
				detailBg.toBack();
				detailBg.addAction(Actions.fadeOut(0));

				LocalData ld = new LocalData(getActivity());
				String bg = ld
						.getKV(AppGlobalConsts.PERSIST_NAMES.BACKGROUND_IMAGE
								.name());

				if (bg == null)
//					bgresid = R.drawable.bja;
					bgresid = R.drawable.other_background;
				else
					bgresid = Integer.parseInt(bg);

				detailBg.setDrawableResource(bgresid);
				detailBg.addAction(Actions.fadeIn(0.6f));
			}
		});
	}

}
