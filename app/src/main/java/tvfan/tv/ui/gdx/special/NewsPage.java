package tvfan.tv.ui.gdx.special;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tvfan.tv.BasePage;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.dal.HttpResponse.Listener4JSONObject;
import tvfan.tv.dal.models.ProgramListItem;
import tvfan.tv.lib.Lg;
import tvfan.tv.lib.NetWorkUtils;
import tvfan.tv.ui.gdx.widgets.CIBNLoadingView;
import android.graphics.Color;
import android.os.Bundle;

import tvfan.tv.R;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.luxtone.lib.image.LoaderListener;
import com.luxtone.lib.image.PageImageLoader;
import com.luxtone.lib.widget.AbsListView;
import com.luxtone.lib.widget.AbsListView.OnItemClickListener;
import com.luxtone.lib.widget.AbsListView.OnItemSelectedChangeListener;
import com.luxtone.lib.widget.Grid;

/**
 * 專題匯總界面
 * 
 * @author 孫文龍
 * 
 */
public class NewsPage extends BasePage implements LoaderListener {

	Image bgImg, lineImg;
	Label titleLabel, pageLabel;
	private PageImageLoader pageImageLoader;
	Grid mGrid;
	private List<ProgramListItem> programList;
	private NewsItemAdapter dataAdapter;
	private int pagenumbers = 12;// 每页个数
	private int pagenow = 0;
	private int totalpage = 0;
	private int totalnumber = 0;
	private String id = "";
	private String imgurl;
	private CIBNLoadingView loadingview;
	private RemoteData rd;
	private String parentCatgId = "";
	private String title = "";

	// private String sourcePage = ""; // 用来记录跳往搜索的page页，有可能为
	// private static NewsPage instance = null;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		// instance = this;
		parentCatgId = bundle.getString("parentCatgId", "");
		id = bundle.getString("id"); // 获取节目id,可能为""

		try {
			if (bundle.containsKey("actionParam")
					&& !bundle.getString("actionParam").isEmpty()) {
				JSONObject obj = new JSONObject(bundle.getString("actionParam"));
				Lg.i(TAG, "actionParam = " + obj.toString());
				parentCatgId = obj.getString("parentCatgId");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		rd = new RemoteData(getActivity());
		initTitle();
		requestDate();

	}

	// public static NewsPage getInstance() {
	// return instance;
	// }

	/**
	 * 初始化背景图
	 */
	private void initImagebg(String imgurl) {
		if (pageImageLoader != null) {
			pageImageLoader.cancelLoad();
		}
		pageImageLoader = new PageImageLoader(this);
		pageImageLoader.startLoadBitmapByFilter(imgurl, "bg", false, 0, this,
				imgurl);

	}

	@Override
	protected void onResumeTextures() {
		super.onResumeTextures();
		resetDefault();
	}

	private void resetDefault() {
		initImagebg(imgurl);
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
	}

	private void initTitle() {

		bgImg = new Image(this);
		bgImg.setDrawableResource(R.drawable.bj);
		bgImg.setFocusAble(false);
		bgImg.setSize(1920, 1080);
		bgImg.setPosition(0, 0);
		addActor(bgImg);

		titleLabel = new Label(this);
		titleLabel.setPosition(145, 920);
		titleLabel.setAlpha(0.7f);
		titleLabel.setTextColor(Color.parseColor("#ffffff"));
		titleLabel.setTextSize(60);
		addActor(titleLabel);

		pageLabel = new Label(this);
		pageLabel.setPosition(1575, 920);
		pageLabel.setTextSize(40);
		pageLabel.setSize(200, 40);
		pageLabel.setAlignment(Align.right);
		pageLabel.setTextColor(Color.parseColor("#ffffff"));
		pageLabel.setAlpha(0.7f);
		addActor(pageLabel);

		loadingview = new CIBNLoadingView(this);
		loadingview.setVisible(true);
		addActor(loadingview);

	}

	/**
	 * 更新分页目录
	 * 
	 * @param pos
	 *            当前选中位置
	 * @param total
	 *            总共海报个数
	 * @param pagenums
	 *            每页海报个数
	 */
	public void updatePageInfo(int pos, int total, int pagenums) {
		pagenow = pos / pagenums + 1;
		totalpage = 0;
		if (total % pagenums == 0) {
			totalpage = total / pagenums;
		} else {
			totalpage = total / pagenums + 1;
		}
		pageLabel.setText(pagenow + "/" + totalpage);
		pageLabel.setTextColor(Color.parseColor("#ffffff"));
		pageLabel.setAlpha(0.7f);
	}

	public void requestDate() {

		rd.getNewsSpecialList(id, new Listener4JSONObject() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					programList = new ArrayList<ProgramListItem>();
					// String channelType = json.getString("channel");
					JSONArray jsonarr = response
							.getJSONArray("newsSpecialList");
					title = response.optString("name","新闻专题汇总");
					for (int i = 0; i < jsonarr.length(); i++) {
						ProgramListItem program = new ProgramListItem();
						JSONObject jsonobject = (JSONObject) jsonarr.get(i);
						program.setFilmid(jsonobject.optString("id", ""));
						program.setPostImg(jsonobject.optString("image", ""));
						program.setPostName(jsonobject.optString("name", ""));
						program.setAction(jsonobject.optString("action", ""));
						program.setFormatStyle(jsonobject.optString(
								"formatStyle", ""));
						programList.add(program);
					}
					loadingview.setVisible(false);
					totalnumber = programList.size();
					updatePageInfo(0, totalnumber, 12);
					if (programList != null && programList.size() > 0) {
						imgurl = programList.get(0).getPostImg();
						initImagebg(imgurl);
					}
					Gdx.app.postRunnable(new Runnable() {

						@Override
						public void run() {
							addGrid();
						}
					});
				} catch (JSONException e) {
					e.printStackTrace();
					loadingview.setVisible(false);
				}
			}

			@Override
			public void onError(String errorMessage) {
				//Utils.showToast("获取数据失败,请重试...");
				Lg.e(TAG, "getNewsSpecialList : " + errorMessage);
				NetWorkUtils.handlerError(errorMessage, NewsPage.this);
			}
		});

	}

	private void addGrid() {
		titleLabel.setText(title);
		mGrid = new Grid(NewsPage.this);
		mGrid.setPosition(145, 0);
		mGrid.setSize(1630, 870);
		mGrid.setGapLength(10);
		mGrid.setOrientation(Grid.ORIENTATION_VERTICAL);
		mGrid.setRowNum(4);
		mGrid.setCull(false);
		mGrid.setCullingArea(new Rectangle(-100, -50, 1630 + 200, 870 + 100));
		dataAdapter = new NewsItemAdapter(this);
		dataAdapter.setData(programList);
		mGrid.setAdapter(dataAdapter);
		mGrid.setAdjustiveScrollLengthForBackward(310);
		mGrid.setAdjustiveScrollLengthForForward(310);
		mGrid.setItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(Actor itemView, int position,
					AbsListView grid) {
				try {
					String actName = programList.get(position).getAction();
					Bundle options = new Bundle();
					// 二级分类
					if (actName.equals("OPEN_PROGRAM_LIST")) {
						actName = "OPEN_NEWS_DETAIL";
						options.putString("menuId", programList.get(position)
								.getFilmid());
						options.putString("parentCatgId", parentCatgId);
						// 单个新闻
					} else if (actName.equals("OPEN_NEWS_DETAIL")) {
						options.putString("programSeriesId",
								programList.get(position).getFilmid());
						options.putString("parentCatgId", parentCatgId);
						// 专题模板
					} else if (actName
							.equalsIgnoreCase("OPEN_NEWS_PROGRAM_LIST")) { // 隐藏栏目
						actName = ACTION_NAME.OPEN_NEWS_DETAIL.name();
						options.putBoolean("is_hide", true);
						options.putString("hideCatgId",
								programList.get(position).getFilmid());
						options.putString("parentCatgId", parentCatgId);
					} else {
						options.putString("parentCatgId", parentCatgId);
						options.putString("id", programList.get(position)
								.getFilmid());
					}
					// 此处anctionname可能报异常
					doAction(ACTION_NAME.valueOf(ACTION_NAME.class, actName),
							options);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		mGrid.setOnItemSelectedChangeListener(new OnItemSelectedChangeListener() {

			@Override
			public void onSelectedChanged(int position, Actor actor) {
				// TODO Auto-generated method stub
				updatePageInfo(position, totalnumber, pagenumbers);
			}
		});
		addActor(mGrid);
	}

	@Override
	public void onLoadComplete(String arg0, TextureRegion arg1, Object arg2) {
		// TODO Auto-generated method stub
		bgImg.setDrawable(new TextureRegionDrawable(arg1));
	}

	@Override
	public boolean onBackKeyDown() {
		// if(canBackToNews && !sourcePage.isEmpty() &&
		// sourcePage.equalsIgnoreCase("NewsPage")){
		// Bundle bundle = new Bundle();
		// bundle.putBoolean("destroyNewsPage", true);
		// doAction(ACTION_NAME.OPEN_NEWS_DETAIL, bundle);
		// return true;
		// }
		return super.onBackKeyDown();
	}

}
