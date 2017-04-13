package tvfan.tv.ui.gdx.programDetail.group;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tvfan.tv.AppGlobalVars;
import tvfan.tv.BasePage.ACTION_NAME;
import tvfan.tv.R;
import tvfan.tv.dal.HttpResponse.Listener4JSONObject;
import tvfan.tv.dal.JSONParser;
import tvfan.tv.dal.LocalData;
import tvfan.tv.dal.PlayRecordHelpler;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.dal.models.MPlayRecordInfo;
import tvfan.tv.dal.models.ProgramSourceBean;
import tvfan.tv.lib.GdxPageImageBatchLoader;
import tvfan.tv.lib.Lg;
import tvfan.tv.lib.StringUtil;
import tvfan.tv.ui.andr.play.baseplay.dateparser.DataParser;
import tvfan.tv.ui.gdx.widgets.TVFANLabel;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.os.Bundle;
import android.text.TextUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.OnClickListener;
import com.luxtone.lib.gdx.OnFocusChangeListener;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.image.LoaderListener;
import com.luxtone.lib.image.PageImageLoader;
import com.luxtone.lib.utils.Utils;
import com.luxtone.lib.widget.CullGroup;
import com.umeng.analytics.MobclickAgent;

@SuppressLint("NewApi")
public class DetailGroup extends Group implements OnFocusChangeListener,
		OnClickListener, LoaderListener {

	private GdxPageImageBatchLoader gpibl;//使用PageImageLoader进行图片加载的工具类
	private Image mPoster, mPosterLogo; // 海报

	private TVFANLabel mTitle; // 电影名称
	private TVFANLabel mUpdate; // 更新集数
	private TVFANLabel director;// 导演
	private TVFANLabel year;
	private TVFANLabel style;
	private TVFANLabel area;
	private TVFANLabel mLeadingRole; // 主演
	private TVFANLabel leadingRole;
	private TVFANLabel mScore1; // 评分
	private TVFANLabel mScore2; // 评分
	private TVFANLabel introduceTitle;
	private TVFANLabel introduce;
	private TVFANLabel history;//播放历史
	private TVFANLabel sourceTV;//当前源显示

	private Image playBtn; // 播放按钮
	private Image episodeBtn; // 剧集
	private Image collectBtn; // 收藏按钮
	private Image sourcesBtn;//选源

	private CullGroup cullGroup;
	private tvfan.tv.ui.gdx.programDetail.Page page;
	
	private boolean isFavorite = false; // 收藏状态
	private boolean msg = false;

	private PageImageLoader posterLoader;//巨石阵进行图片请求的工具
	private RemoteData rd;
	public JSONObject detailJsonObj;
	private JSONParser movieDetailParser;
	private JSONObject episodeObject;//节目集数据json对象
	private JSONArray episodeJSONArray;

	private LocalData localData;
	private PlayRecordHelpler prh;
	private MPlayRecordInfo info;//记录视频播放的相关信息

	private String cpId = "";
	private static final String TAG = "DetailGroup";
	private final static String FAVORITE_TABLE = "FAVORITE_TAB"; // 我的收藏记录本地数据库
	public String programSeriesId;
	private String updateNum;// 更新的集数
	private String timeTamp;
	private int pagingNum = 1;
	private int yearNum = 0;
	private int totalNum;
	private int btnNum = 1;//记录当前是哪个按钮“1--播放”、“2--选集”、“3--收藏”、“4--切源”
	private boolean isContiue;

	private ArrayList<String> episodeArray;//节目集列表数据集合
	private ArrayList<ProgramSourceBean> videoSourceList;//节目源的集合
	private JSONArray yearJSONArray;

	public DetailGroup(Page page, String programSeriesId, JSONParser parser,
			JSONObject jsonObj, ArrayList<ProgramSourceBean> videoSourceList, JSONObject episodeObject) {
		super(page);
		setSize(1760, 750);
		this.page = (tvfan.tv.ui.gdx.programDetail.Page) page;

		this.programSeriesId = programSeriesId;
		this.movieDetailParser = parser;
		this.detailJsonObj = jsonObj;
		this.videoSourceList = videoSourceList;
		this.episodeObject = episodeObject;
		rd = new RemoteData(page.getActivity());
		localData = new LocalData(page.getActivity());
		gpibl = new GdxPageImageBatchLoader(page);
		timeTamp = System.currentTimeMillis() + "";

		initView();
	}
	
	@Override
	public void onResume() {
		isFavorite();
		tvfan.tv.lib.Utils.resetImageSource(mPoster,
				R.drawable.placeholder);
		tvfan.tv.lib.Utils.resetImageSource(mPosterLogo,
				R.drawable.placeholder_logo);
		loadPoster();
		info = prh.getPlayRcInfo(programSeriesId);
		super.onResume();
	}

	private void isFavorite() {
		rd.isFavorite(programSeriesId, new Listener4JSONObject() {

			@Override
			public void onResponse(JSONObject response) {
				if (page.isDisposed()) {
					return;
				}
				if (response == null) {
					Lg.e(TAG, "detail page get favorite state response is null");
					return;
				}
				try {
					isFavorite = response.optBoolean("msg");
					Gdx.app.postRunnable(new Runnable() {

						@Override
						public void run() {
							if (isFavorite) {
								collectBtn.setDrawableResource(R.mipmap.detail_collect_normal);
							} else {
								collectBtn.setDrawableResource(R.mipmap.detail_not_collected_normal);
							}
						}
					});
				} catch (Exception e) {
					Lg.e(TAG, e.getMessage());
					e.printStackTrace();
				}
			}

			@Override
			public void onError(String errorMessage) {
			}
		});
	}

	private void initView() {
		cullGroup = new CullGroup(page);
		cullGroup.setPosition(0.0f, 0.0f);
		cullGroup.setSize(1760.0f, 560.0f);
		cullGroup.setCullingArea(new Rectangle(0, 0, 1760.0f, 560.0f));
		addActor(cullGroup);
		
		createButtons();

		createDetailUI(movieDetailParser);

		// 获取收藏状态
		isFavorite();

		// 获取评分
		rd.queryScore(programSeriesId, new Listener4JSONObject() {
			@Override
			public void onResponse(JSONObject response) {
				if (page.isDisposed()) {
					return;
				}
				if (response == null) {
					Lg.e(TAG, "detail page get score state response is null");
					return;
				}
				try {
					final String score = response.optString("score");
					Gdx.app.postRunnable(new Runnable() {

						@Override
						public void run() {
							String score1 = score.substring(0, 1);
							String score2 = score.substring(1, score.length()) + "分";
							mScore1 = new TVFANLabel(page);
							mScore1.setPosition(1573.0f, 505.0f);
							mScore1.setSize(41, 41);
							mScore1.setFocusAble(false);
							mScore1.setText(score1);
							mScore1.setTextSize(55);
							mScore1.setColor(Color.valueOf("F1CE1A"));
							cullGroup.addActor(mScore1);

							mScore2 = new TVFANLabel(page);
							mScore2.setPosition(1600.0f, 507.0f);
							mScore2.setSize(62, 40);
							mScore2.setFocusAble(false);
							mScore2.setText(score2);
							mScore2.setTextSize(34);
							mScore2.setColor(Color.valueOf("F1CE1A"));
							cullGroup.addActor(mScore2);
						}

					});
				} catch (Exception e) {
					Lg.e(TAG, e.getMessage());
					e.printStackTrace();
				}
			}

			@Override
			public void onError(String errorMessage) {

			}
		});
	}

	private void createDetailUI(JSONParser movieDetailParser) {
		try {
			page.setBackGroud(movieDetailParser.getString("picurl"));

			String title = movieDetailParser.getString("name");

			// 详情页的title的cullgroup，用来控制label的滑动区域
			CullGroup cullTitle = new CullGroup(page);
			cullTitle.setPosition(440.0f, 500.0f);
			mTitle = new TVFANLabel(page);
			// 字母和数字的长度要减半
			int l = (int) ((float) StringUtil.countCharNumber(title) / 2
					+ title.length() - StringUtil.countCharNumber(title));
			if (l <= 15) { // 标题最多15个字
				mTitle.setSize(60 * (l+1), 70);
				cullTitle.setSize(60 * (l+1), 70);
				cullTitle.setCullingArea(new Rectangle(0, 0, 60 * (l+1), 70));
			} else {
				cullTitle.setSize(60 * title.length(), 70);
				cullTitle.setCullingArea(new Rectangle(0, 0, 60 * 15, 70));
				mTitle.setSize(60 * 15, 70);
				mTitle.setMarquee(true);
			}
			cullGroup.addActor(cullTitle);
			mTitle.setTextSize(60);
			mTitle.setMaxLine(1);
			mTitle.setText(title);
			mTitle.setFocusAble(false);
			cullTitle.addActor(mTitle);
			int totalEpisode = 0;
			/*try {
				totalEpisode = Integer.parseInt(episodeObject.getString("totalNumber"), 1);
			} catch (JSONException e) {
				e.printStackTrace();
			}*/

			mPoster = new Image(page);
			mPoster.setPosition(0.0f, 0.0f);
			mPoster.setSize(400.0f, 560.0f);
			mPoster.setDrawable(findRegion(R.drawable.placeholder));
			mPoster.setFocusAble(false);
			cullGroup.addActor(mPoster);

			mPosterLogo = new Image(page);
			mPosterLogo.setSize(172, 60);
			mPosterLogo.setPosition((375 - 172) / 2, (500 - 60) / 2 + 30);
			mPosterLogo.setVisible(true);
			mPosterLogo.setDrawableResource(R.drawable.placeholder_logo);
			cullGroup.addActor(mPosterLogo);
			loadPoster();

			if(movieDetailParser.getString("type") .equalsIgnoreCase("电影")|| movieDetailParser.getString("type").equalsIgnoreCase("电视剧")) {
				createMovieInfo(totalEpisode);
			} else {
				setOtherInfo();
			}

			introduceTitle = new TVFANLabel(page);
			introduceTitle.setPosition(440.0f, 237.0f);
			introduceTitle.setSize(70.0f, 30.0f);
			introduceTitle.setMaxLine(1);
			introduceTitle.setText("剧情介绍:");
			introduceTitle.setTextSize(30);
			addActor(introduceTitle);

			introduce = new TVFANLabel(page);
			introduce.setPosition(440.0f, 100.0f);
			introduce.setSize(1295.0f, 125.0f);
			introduce.setMaxLine(3);
			introduce.setFocusAble(false);
			introduce.setTextSize(28);
			String info = movieDetailParser.getString("information");
			if (info.isEmpty()) {
				info = "无";
			}
			if (info.length() > 135) {
				info = info.substring(0, 135);
				if (info.endsWith("。")) {
					info = info.substring(0, 134) + "...";
				} else {
					info = info + "...";
				}
			}
			introduce.setText(info);
			introduce.setSpacingadd(20);
			cullGroup.addActor(introduce);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void setOtherInfo() {

		style = new TVFANLabel(page);
		style.setPosition(440.0f, 389.0f);
		style.setSize(200.0f, 25.0f);
		style.setTextSize(30);
		String type = movieDetailParser.getString("type");
		if(type.isEmpty())
			type = "无";
		style.setText("类型: " + type);
		cullGroup.addActor(style);

		director = new TVFANLabel(page);
		director.setPosition(440.0f, 342.0f);
		director.setSize(1000.0f, 25.0f);
		director.setTextSize(30);
		director.setFocusAble(false);
		String ds = movieDetailParser.getString("director");
		if (ds.isEmpty()) {
			ds = "无";
		}
		if (ds.length() > 40) {
			ds = ds.substring(0, 39);
			if (ds.endsWith("·")||ds.endsWith(",")) {
				ds = ds.substring(0, 38)+ "...";
			} else {
				ds = ds + "...";
			}
		}
		if (type.equals("动漫")) {
			director.setText("作者: " + ds);
		}
		else if (type.equals("音乐") || type.equals("短视频")
				|| type.equals("纪录片") || type.equals("游戏") || type.equals("微电影")) {
			ds = movieDetailParser.getString("class");
			if (ds.isEmpty()) {
				ds = "无";
			}
			director.setText("标签: " + ds);
		}
		else
			director.setText("主持人: " + ds);
		cullGroup.addActor(director);

		String yearStr = "";
		yearStr = movieDetailParser.getString("releaseDate");
		if(yearStr.isEmpty())
			yearStr = "无";
		year = new TVFANLabel(page);
		year.setPosition(440.0f, 436.0f);
		year.setSize(250.0f, 25.0f);
		year.setTextSize(30);
		year.setText("年代: " + yearStr);
		cullGroup.addActor(year);

		area = new TVFANLabel(page);
		area.setPosition(900.0f, 436.0f);
		area.setSize(200.0f, 25.0f);
		area.setTextSize(30);
		String zone = movieDetailParser.getString("zone");
		if(zone.isEmpty())
			zone = "无";
		area.setText("地区: " + zone);
		cullGroup.addActor(area);

		String historyStr = "暂无播放历史";
		if (info != null) {
			int currPlayerpos = info.getPlayerpos()%20;
			if(currPlayerpos < episodeArray.size()){
				historyStr = episodeArray.get(currPlayerpos);
			}
		}
		history = new TVFANLabel(page);
		history.setPosition(900.0f, 389.0f);
		history.setSize(200.0f, 25.0f);
		history.setTextSize(30);
		setHistory(historyStr);
		cullGroup.addActor(history);
	}

	public void setHistory (String historyStr) {
		history.setText("播放历史: "+historyStr);
		if("暂无播放历史" .equals( historyStr))
			isContiue = false;
		else
			isContiue = true;

		setBtnSelected();
	}
	private void createMovieInfo(int totalEpisode){

		mLeadingRole = new TVFANLabel(page);
		mLeadingRole.setPosition(440.0f, 342.0f);
		mLeadingRole.setSize(75.0f, 25.0f);
		mLeadingRole.setFocusAble(false);
		mLeadingRole.setTextSize(30);
		mLeadingRole.setText("主演: ");
		cullGroup.addActor(mLeadingRole); // 吉尔莫-德托罗鬼屋片启动 瑞恩-高斯林

		CullGroup leadingRoleCullGroup = new CullGroup(page);
		leadingRoleCullGroup.setPosition(535.0f, 342.0f);
		leadingRoleCullGroup.setSize(1225.0f, 30.0f);
		leadingRoleCullGroup.setCullingArea(new Rectangle(0, 0, 1225.0f, 30.0f));
		cullGroup.addActor(leadingRoleCullGroup);

		leadingRole = new TVFANLabel(page);
		leadingRole.setPosition(0, 0);
		leadingRole.setSize(1225.0f, 30.0f);
		leadingRole.setMaxLine(1);
		leadingRole.setTextSize(30);
		leadingRole.setFocusAble(false);
		String lr = "";
		lr = movieDetailParser.getString("actor");
		if (lr.isEmpty()) {
			lr = "无";
		}
		if (lr.length() > 40) {
			String l = lr.substring(39, lr.length());
			int index = l.indexOf(",");
			lr = lr.substring(0, 39+index);
			if (lr.endsWith("·")||lr.endsWith(",")) {
				lr = lr.substring(0, 38+index)+ "...";
			} else {
				lr = lr + "...";
			}
		}
		leadingRole.setText(lr);
		leadingRole.setAlpha(0.8f);
		leadingRoleCullGroup.addActor(leadingRole);

		year = new TVFANLabel(page);
		year.setPosition(440.0f, 389.0f);
		year.setSize(250.0f, 25.0f);
		year.setTextSize(30);
		year.setText("年代: " + movieDetailParser.getString("releaseDate"));
		cullGroup.addActor(year);

		mUpdate = new TVFANLabel(page);
		mUpdate.setPosition(900.0f, 389.0f);
		mUpdate.setSize(250.0f, 25.0f);
		mUpdate.setTextSize(30);
		updateNum = videoSourceList.get(0).getUpdataCount();
		setMUpdateText(updateNum, totalEpisode);
		mUpdate.setFocusAble(false);
		updateFavoriteTable();
		cullGroup.addActor(mUpdate);

		director = new TVFANLabel(page);
		director.setPosition(440.0f, 436.0f);
		director.setSize(250.0f, 25.0f);
		director.setTextSize(30);
		director.setFocusAble(false);
		String ds = movieDetailParser.getString("director");
		if (ds.isEmpty()) {
			ds = "无";
		}
		if (ds.length() > 12) {
			ds = ds.substring(0, 11);
			if (ds.endsWith("·")||ds.endsWith(",")) {
				ds = ds.substring(0, 10)+ "...";
			} else {
				ds = ds + "...";
			}
		}
		director.setText("导演: " + ds);
		cullGroup.addActor(director);

		style = new TVFANLabel(page);
		style.setPosition(900.0f, 436.0f);
		style.setSize(200.0f, 25.0f);
		style.setTextSize(30);
		style.setText("类型: " + movieDetailParser.getString("type"));
		cullGroup.addActor(style);

		area = new TVFANLabel(page);
		area.setPosition(1240.0f, 436.0f);
		area.setSize(200.0f, 25.0f);
		area.setTextSize(30);
		String areaStr = movieDetailParser.getString("zone");
		if(areaStr.length() > 12){
			areaStr = areaStr.substring(0, 11);
			if (areaStr.endsWith("·")||areaStr.endsWith(",")) {
				areaStr = areaStr.substring(0, 10)+ "...";
			} else {
				areaStr = areaStr + "...";
			}
		}
		area.setText("地区: " + areaStr);
		cullGroup.addActor(area);

		String historyStr = "暂无播放历史";
		if (info != null) {
			int currPlayerpos = info.getPlayerpos()%20;
			if(currPlayerpos < episodeArray.size()){
				historyStr = episodeArray.get(currPlayerpos);
			}
		}
		history = new TVFANLabel(page);
		history.setPosition(1240.0f, 389.0f);
		history.setSize(200.0f, 25.0f);
		history.setTextSize(30);
		setHistory(historyStr);
		cullGroup.addActor(history);

	}

	/**
	 * 设置节目的总集数
	 * @param
	 */
	private void setMUpdateText(String updateNum, int totalEpisode) {
		String type = movieDetailParser.getString("type");
		if (type.equalsIgnoreCase("综艺")||type.equalsIgnoreCase("纪录片")
				||type.equalsIgnoreCase("短视频")||type.equalsIgnoreCase("音乐")||type.equalsIgnoreCase("游戏")) {
			mUpdate.setText("更新至" + updateNum + "期");
		} else {
			if (totalEpisode == movieDetailParser.getInt("totalCount"))
				mUpdate.setText("集数: 全" + totalEpisode + "集");
			else
				mUpdate.setText("集数: 更新至" + updateNum + "集");
		}
	}

	/**
	 * 加载海报
	 */
	private void loadPoster() {
		
		if (posterLoader != null) {
			posterLoader.cancelLoad();
		}
		posterLoader = new PageImageLoader(page);

		mPoster.setDrawableResource(R.drawable.placeholder);
		mPosterLogo.setVisible(true);
		
		posterLoader.startLoadBitmap(movieDetailParser.getString("picurl"),
				"poster", this, "poster");
	}

	/**
	 * 图片加载完成的回调
	 */
	@Override
	public void onLoadComplete(String url, TextureRegion textureRegion,
			Object tag) {
			mPoster.setDrawable(textureRegion);
			mPosterLogo.setVisible(false);
	}
	
	/**
	 * 更新本地数据库剧集
	 */
	private void updateFavoriteTable() {
		ContentValues values = new ContentValues();
		values.put("latestEpisode", updateNum);
		int r = localData.execUpdate(FAVORITE_TABLE, values,
				"programSeriesId=?", new String[] { programSeriesId });
		System.out.println("FAVORITE_TABLE Update programSeriesId = "
				+ programSeriesId + ",latestEpisode = " + updateNum
				+ ", result = " + r);
	}

	/**
	 * 设置相关的按钮
	 */
	private void createButtons() {

		prh = new PlayRecordHelpler(page.getActivity());
		info = prh.getPlayRcInfo(programSeriesId);

		 //播放按钮
		playBtn = new Image(page);
		playBtn.setPosition(440.0f, 0.0f);
		playBtn.setSize(160.0f, 55.0f);
		playBtn.setName("playBtn");
		playBtn.setDrawableResource(R.mipmap.detail_play_normal);
		playBtn.setNextFocusLeft("playBtn");
		playBtn.setNextFocusRight("episodeBtn");
		playBtn.setFocusAble(true);
		try {
			episodeJSONArray = episodeObject.getJSONArray("sources");
			totalNum = episodeObject.getInt("totalNumber");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		episodeArray = DataParser.initEpisodeArrayList(episodeJSONArray, movieDetailParser.getString("type"));
		if (info != null) {
			cpId = info.getCpId();
			pagingNum = info.getPageNum();
			yearNum = info.getYearNum();
			playBtn.setDrawableResource(R.mipmap.continue_play_selected);
		}

		playBtn.setOnClickListener(this);
		playBtn.setOnFocusChangeListener(this);
		cullGroup.addActor(playBtn);

		//选集按钮
		episodeBtn = new Image(page);
		episodeBtn.setDrawableResource(R.mipmap.detail_set_normal);
		episodeBtn.setName("episodeBtn");
		episodeBtn.setNextFocusLeft("playBtn");
		episodeBtn.setNextFocusRight("collectBtn");
		episodeBtn.setNextFocusDown("pagingItem");
		episodeBtn.setPosition(640.0f, 0.0f);
		episodeBtn.setSize(160.0f, 55.0f);
		episodeBtn.setFocusAble(true);
		episodeBtn.setOnClickListener(this);
		episodeBtn.setOnFocusChangeListener(this);
		 cullGroup.addActor(episodeBtn);

		 //收藏按钮
		collectBtn = new Image(page);
		collectBtn.setDrawableResource(R.mipmap.detail_collect_normal);
		collectBtn.setPosition(840.0f, 0.0f);
		collectBtn.setSize(160.0f, 55.0f);
		collectBtn.setName("collectBtn");
		collectBtn.setNextFocusLeft("episodeBtn");
		collectBtn.setNextFocusRight("sourcesBtn");
		collectBtn.setNextFocusDown("pagingItem");
		collectBtn.setFocusAble(true);
		collectBtn.setOnClickListener(this);
		collectBtn.setOnFocusChangeListener(this);
		cullGroup.addActor(collectBtn);

		//收藏按钮
		sourcesBtn = new Image(page);
		sourcesBtn.setDrawableResource(R.mipmap.detail_play_source_normal);
		sourcesBtn.setPosition(1040.0f, 0.0f);
		sourcesBtn.setSize(200.0f, 55.0f);
		sourcesBtn.setName("sourcesBtn");
		sourcesBtn.setNextFocusLeft("collectBtn");
		sourcesBtn.setNextFocusRight("sourcesBtn");
		sourcesBtn.setFocusAble(true);
		sourcesBtn.setOnClickListener(this);
		sourcesBtn.setOnFocusChangeListener(this);
		sourcesBtn.toBack();
		cullGroup.addActor(sourcesBtn);

		sourceTV = new TVFANLabel(page);
		sourceTV.setPosition(1080.0f, 0.0f);
		sourceTV.setSize(160.0f, 55.0f);
		sourceTV.setTextSize(31);
		sourceTV.setAlignment(Align.center);
		setCpName(cpId, false);
		sourceTV.toFront();
		cullGroup.addActor(sourceTV);


	}

	public void setCpName(String cpId, boolean isChangeSoure) {
		if(TextUtils.isEmpty(cpId)) {
			sourceTV.setText(videoSourceList.get(0).getCpName());
		}
		else {
			for(ProgramSourceBean programSourceBean : videoSourceList ) {
				if(cpId.equals(programSourceBean.getCpId()))
					sourceTV.setText(programSourceBean.getCpName());
			}
		}
		if(isChangeSoure)
			playBtn.requestFocus();
	}

	public Image getPlayBtn() {
		return playBtn;
	}

	public Image getFavBtn() {
		return  collectBtn;
	}

	public Image getEpsiodeBtn() {
		return  episodeBtn;
	}

	public Image getVideoSourceBtn() {
		return sourcesBtn;
	}

	public void setIsHistory(boolean isContiue) {
		this.isContiue = isContiue;
		setBtnSelected();
	}

	public void setTitle(String text) {
		mTitle.setText(text);
	}

	@Override
	public void onFocusChanged(Actor actor, boolean hasFocus) {
		if(onButtonFoucusChangeListener != null) {
			onButtonFoucusChangeListener.onFoucusChange(actor, hasFocus);
		}

		if(actor == playBtn && hasFocus) {
			btnNum = 1;
			if(isContiue) {
				playBtn.setDrawableResource(R.mipmap.continue_play_selected);
			}
			else {
				playBtn.setDrawableResource(R.mipmap.detail_play_selected);
			}
		}else {
			if(isContiue) {
				playBtn.setDrawableResource(R.mipmap.continue_play_normal);
			}
			else {
				playBtn.setDrawableResource(R.mipmap.detail_play_normal);
			}
		}

		if(actor == episodeBtn && hasFocus) {
			btnNum = 2;
			episodeBtn.setDrawableResource(R.mipmap.detail_set_selected);
		}else {
			episodeBtn.setDrawableResource(R.mipmap.detail_set_normal);
		}

		if(actor == collectBtn && hasFocus) {
			btnNum = 3;
			if(isFavorite) {
				collectBtn.setDrawableResource(R.mipmap.detail_collect_selected);
			}else {
				collectBtn.setDrawableResource(R.mipmap.detail_not_collect_selected);
			}
		}else {
			if(isFavorite) {
				collectBtn.setDrawableResource(R.mipmap.detail_collect_normal);
			}else {
				collectBtn.setDrawableResource(R.mipmap.detail_not_collected_normal);
			}
		}

		if(actor == sourcesBtn && hasFocus){
			btnNum = 4;
			sourcesBtn.setDrawableResource(R.mipmap.detail_play_source_selected);
			sourcesBtn.toBack();
		}else {
			sourcesBtn.setDrawableResource(R.mipmap.detail_play_source_normal);
		}
		setBtnSelected();
	}

	private void setBtnSelected() {
		if(!playBtn.isFocused() && !episodeBtn.isFocused() && !sourcesBtn.isFocused() && !collectBtn.isFocused()) {
			if(btnNum ==1 || btnNum==3) {
				if(isContiue)
					playBtn.setDrawableResource(R.mipmap.play_btn_selected2);
				else
					playBtn.setDrawableResource(R.mipmap.play_btn_selected);
			}
			else if(btnNum == 2) {
				episodeBtn.setDrawableResource(R.mipmap.episode_btn_selected);
			}
			else if(btnNum == 4)
				sourcesBtn.setDrawableResource(R.mipmap.source_btn_selected);
		}
	}


	private void addFavorite() {
		rd.addFavorite(programSeriesId, new Listener4JSONObject() {

			@Override
			public void onResponse(JSONObject response) {
				if (page.isDisposed()) {
					return;
				}
				if (response == null) {
					Lg.e(TAG, "detail page addFavorite  response is null");
					Utils.showToast("添加收藏失败");
					return;
				}
				try {
					boolean msg = response.optBoolean("msg");
					if (msg) {
						isFavorite = true;
						Gdx.app.postRunnable(new Runnable() {

							@Override
							public void run() {
								Utils.showToast("添加收藏成功");
								collectBtn.setDrawableResource(R.mipmap.detail_collect_selected);
							}
						});

						// 更新本地数据库,增加添加收藏的记录
						ContentValues values = new ContentValues();
						values.put("programSeriesId", programSeriesId);
						values.put("latestEpisode", updateNum);
						values.put("ts", timeTamp);
						int r = localData.execInsert(FAVORITE_TABLE, null,
								values);
						System.out
								.println("FAVORITE_TABLE Insert programSeriesId = "
										+ programSeriesId + ",result = " + r);
					}
				} catch (Exception e) {
					Lg.e(TAG, e.getMessage());
					e.printStackTrace();
				}
			}

			@Override
			public void onError(String errorMessage) {
				Utils.showToast("添加收藏失败");
			}
		});
	}

	/**
	 * 取消收藏的按钮
	 */
	private void cancelFavorite() {
		
		rd.cancelFavorite(programSeriesId, new Listener4JSONObject() {

			@Override
			public void onResponse(JSONObject response) {
				if (page.isDisposed()) {
					return;
				}
				if (response == null) {
					Lg.e(TAG, "detail page cancelFavorite  response is null");
					Utils.showToast("添加收藏失败");
					return;
				}
				try {
					msg = response.optBoolean("msg");
					if (msg) {
						isFavorite = false;
						Gdx.app.postRunnable(new Runnable() {

							@Override
							public void run() {
								Utils.showToast("取消收藏成功");
								collectBtn.setDrawableResource(R.mipmap.detail_not_collect_selected);
							}
						});

						// 更新本地数据库,删除取消收藏的记录
						int r = localData.execDelete(FAVORITE_TABLE,
								"programSeriesId=?",
								new String[]{programSeriesId});
						System.out
								.println("FAVORITE_TABLE delete programSeriesId = "
										+ programSeriesId + ",result = " + r);
					}
				} catch (Exception e) {
					Lg.e(TAG, e.getMessage());
					e.printStackTrace();
				}
			}

			@Override
			public void onError(String errorMessage) {
				Utils.showToast("添加收藏失败");
			}
		});
		
	}

	@Override
	public void onClick(Actor actor) {
		if (actor.equals(episodeBtn)) {// 剧集
		} else if (actor.equals(collectBtn)) {
			if (isFavorite) {
				cancelFavorite();
			} else {
				addFavorite();
			}
		} else if (actor.equals(playBtn)) {
			if(videoSourceList.isEmpty() || episodeArray.isEmpty()){
				Utils.showToast("此视频资源正在维护中!");
				return;
			}
			if(info!=null&&!cpId.equals(info.getCpId())) {
				prh.deletePlayRecordBy(programSeriesId);
				pagingNum = 1;
				yearNum = 0;
			}
			playProgram();
		}
	}
	
	private void playProgram(){
		final Bundle bundle = new Bundle();
		if(detailJsonObj.has("sources"))
			detailJsonObj.remove("sources");
		try {
			detailJsonObj.putOpt("sources", episodeJSONArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String str = detailJsonObj.toString();
		if (str != null) {
			byte[] bs = str.getBytes();
			if (bs != null && bs.length < 20 * 1024) { // wanqi，小于20kb的内容使用bundle传输
				bundle.putString("moviedetail", str);
			} else {   //wanqi，若大于20Kb的内容就共享内存，让播放器读取
				AppGlobalVars.getIns().TMP_VARS.put("DETAIL_"
						+ programSeriesId, str);
				bundle.putString("moviedetail", "DETAIL_" + programSeriesId);
			}
		}
		String type = movieDetailParser.getString("type");
		bundle.putString("type", type);
		bundle.putInt("currentPlayPosition", -1);
		bundle.putInt("pagingNum", pagingNum);
		bundle.putInt("yearNum", yearNum);
		bundle.putInt("totalEpisodes", totalNum);
		bundle.putString("cpId", cpId);
		if(yearJSONArray!=null)
			bundle.putString("yearJSONArray", yearJSONArray.toString());
		
		page.onPause();

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("UID", AppGlobalVars.getIns().USER_ID);
		map.put("PLAYNAME", movieDetailParser.getString("type")+"-"+movieDetailParser.getString("name"));
		map.put("U_P", AppGlobalVars.getIns().USER_ID + "|"
				+ movieDetailParser.getString("name"));
		MobclickAgent.onEvent(page.getActivity().getApplicationContext(),
				"event_play", map);
		page.doAction(ACTION_NAME.OPEN_PLAYER, bundle);
	}

	/**
	 * 设置是否显示续播
	 * @param res
	 */
	public void setPlayButton(int res){
		playBtn.setDrawableResource(res);
	}

	/**
	 * 设置cpId的方法
	 * @param cpId
	 */
	public void setcpId(String cpId){
		this.cpId = cpId;
	}

	public void setYearNum (int yearNum) {
		this.yearNum = yearNum;
	}

	public void setPagingNum (int pagingNum) {
		this.pagingNum = pagingNum;
	}

	/**
	 * 设置节目集数据
	 * @param object
	 */
	public void setEpisodeObject (JSONObject object) {
			this.episodeObject = object;
		episodeJSONArray = episodeObject.optJSONArray("sources");
		totalNum = episodeObject.optInt("totalNumber", 1);
		episodeArray = DataParser.initEpisodeArrayList(episodeJSONArray, movieDetailParser.getString("type"));

	}

	public void setYearJSONArray(JSONArray yearJSONArray) {
		this.yearJSONArray = yearJSONArray;
	}

	public interface OnButtonFoucusChangeListener{
		public void onFoucusChange(Actor actor, boolean hasFousChange);
	}

	private OnButtonFoucusChangeListener onButtonFoucusChangeListener;

	public void setButtonFoucusChangeListener(OnButtonFoucusChangeListener onButtonFoucusChangeListener) {
		this.onButtonFoucusChangeListener = onButtonFoucusChangeListener;
	}
}
