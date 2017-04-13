package tvfan.tv.ui.gdx.ranking;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tvfan.tv.BasePage;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.dal.HttpResponse.Listener4JSONObject;
import tvfan.tv.dal.models.ProgramListItem;
import tvfan.tv.dal.models.ProgramMenus;
import tvfan.tv.lib.Lg;
import tvfan.tv.lib.NetWorkUtils;
import tvfan.tv.ui.gdx.widgets.CIBNLoadingView;
import tvfan.tv.ui.gdx.yourLike.YouLikeGridAdapter;
import android.os.Bundle;
import android.view.KeyEvent;

import tvfan.tv.R;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.luxtone.lib.image.LoaderListener;
import com.luxtone.lib.image.PageImageLoader;
import com.luxtone.lib.widget.AbsListView;
import com.luxtone.lib.widget.AbsListView.OnItemClickListener;
import com.luxtone.lib.widget.AbsListView.OnItemSelectedChangeListener;
import com.luxtone.lib.widget.AbsListView.ScrollStatusListener;
import com.luxtone.lib.widget.CullGroup;
import com.luxtone.lib.widget.Grid;

/**
 * 热门标签
 * 
 * @author sunwenlong
 * 
 */
public class Page extends BasePage implements LoaderListener {
	// 左侧栏目按钮布局
	private Group menuGroup;
	private PageImageLoader pageImageLoader;
	private CIBNLoadingView loadingview;
	// 初始化右侧栏海报列表坐标宽高
	private RemoteData rd;
	private Timer timer;
	private Task task;
	boolean fcousRight = false;
	private Grid mGrid;
	private YouLikeGridAdapter dataAdapter;
	// 数据
	private List<ProgramMenus> menuList;// 左侧栏目分类列表数据
	private List<ProgramListItem> programList;// 右侧栏海报列表页数据
	private int pagecount = 0;

	private HotMenuListItem menulistitem;
	private int iprepos = 0;
	private String imgurl;
	private int pagenow = 0;
	private int totalpage = 0;
	Image bgImg;
	CullGroup gridcullGroup;
	Label pageInfo, title;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		// 初始化ui
		_initView();
		// 初始化左侧数据
		_requestMenuData();

	}

	// 设置背景图
	private void _initView() {
		rd = new RemoteData(getActivity());
		timer = new Timer();
		// 设置背景图
		bgImg = new Image(this);
		bgImg.setDrawableResource(R.drawable.bj);
		bgImg.setFocusAble(false);
		bgImg.setSize(1920, 1080);
		bgImg.setPosition(0, 0);
		addActor(bgImg);

		// 初始化页码
		pageInfo = new Label(this);
		pageInfo.setColor(Color.WHITE);
		pageInfo.setAlpha(0.9f);
		pageInfo.setTextSize(40);
		pageInfo.setSize(200, 60);
		pageInfo.setAlignment(Align.right);
		pageInfo.setPosition(1510, 924);
		addActor(pageInfo);

		//
		gridcullGroup = new CullGroup(this);
		gridcullGroup.setSize(1330, 890);
		gridcullGroup.setPosition(380, 0);
		gridcullGroup.setCullingArea(new Rectangle(-60, -30, 1330 + 120,
				890 + 70));
		addActor(gridcullGroup);

		title = new Label(this);
		title.setPosition(480, 930);
		title.setTextSize(50);
		title.setColor(Color.WHITE);
		title.setAlpha(0.9f);
		addActor(title);

		loadingview = new CIBNLoadingView(this);
		loadingview.setVisible(true);
		addActor(loadingview);
	}

	/**
	 * 获取列表分类
	 * 
	 * @param pagenum
	 * @param pagesize
	 * @param callback
	 */

	public void _requestMenuData() {
		rd.getHotWordsMenus(new Listener4JSONObject() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject json = response;
					menuList = new ArrayList<ProgramMenus>();
					JSONArray jsonarr = json.getJSONArray("data");
					for (int i = 0; i < jsonarr.length(); i++) {
						JSONObject jsondata = (JSONObject) jsonarr.get(i);
						ProgramMenus programMenus = new ProgramMenus();
						programMenus.setId(jsondata.optString("abbr", ""));
						programMenus.setName(jsondata.optString("name", ""));
						menuList.add(programMenus);
					}
					if (menuList.size() > 0)
						Gdx.app.postRunnable(uimenurunable);

				} catch (JSONException e) {
					e.printStackTrace();
					loadingview.setVisible(false);
				}
			}

			@Override
			public void onError(String errorMessage) {
				// Utils.showToast("获取分类栏目数据失败,请重试...");
				Lg.e(TAG, "getHotWordsMenus : " + errorMessage);
				NetWorkUtils.handlerError(errorMessage, Page.this);
			}
		});
	}

	// 初始化左侧menu列表
	private Runnable uimenurunable = new Runnable() {

		@Override
		public void run() {
			initMenuGroup();
		}
	};

	/**
	 * 初始化左侧页面group 坐标宽高
	 */
	private void initMenuGroup() {

		// 设置左侧栏布局 并添加背景图片
		menuGroup = new HotTagsMenuGroup(this, menuList);
		menuGroup.setSize(480, 1080);
		menuGroup.setPosition(18, 0);
		((HotTagsMenuGroup) menuGroup)
				.setOnItemSelectedChangeListenen(new OnItemSelectedChangeListener() {

					@Override
					public void onSelectedChanged(final int pos, Actor actor) {

						try {
							if (menulistitem != null) {
								menulistitem.setFocusImgBg(false);
							}
							menulistitem = (HotMenuListItem) actor;
							menulistitem.setFocusImgBg(true);

							if (task != null) {
								task.cancel();
							}
							if (timer != null) {
								timer.stop();
							}

							if (iprepos != pos) {
								fcousRight = true;
								task = new Task() {

									@Override
									public void run() {
										iprepos = pos;
										_requestData(menuList.get(pos).getId());
									}
								};
								timer.scheduleTask(task, 0.6f);
								timer.start();
							} else {
								fcousRight = false;
							}

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
		((HotTagsMenuGroup) menuGroup).setGridSelection(0);
		addActor(menuGroup);
		loadingview.setVisible(false);
		_requestData(menuList.get(0).getId());
	}

	/**
	 * 获取电影列表
	 * 
	 */
	public void _requestData(final String value) {

		rd.getSearchList(new Listener4JSONObject() {
			@Override
			public void onResponse(JSONObject response) {
				if (programList == null)
					programList = new ArrayList<ProgramListItem>();
				else
					programList.clear();
				try {

					JSONArray jsonarr = response.getJSONArray("programList");
					JSONObject jsonarrMenu = response
							.getJSONObject("parentMenu");
					pagecount = jsonarrMenu.optInt("totalNumber", 0);
					for (int i = 0; i < jsonarr.length(); i++) {
						JSONObject jsonobject = (JSONObject) jsonarr.get(i);
						ProgramListItem program = new ProgramListItem();
						program.setId(jsonobject.optString("id", ""));
						program.setPostImg(jsonobject.optString("image", ""));
						program.setPostName(jsonobject.optString("name", ""));
						program.setCurrentNum(jsonobject.optString(
								"currentNum", ""));
						program.setCornerPrice(jsonobject.optString(
								"cornerPrice", "0"));
						program.setCornerType(jsonobject.optString(
								"cornerType", "0"));
						programList.add(program);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				Gdx.app.postRunnable(uibgrunable);
				Gdx.app.postRunnable(uirunable);
			}

			@Override
			public void onError(String errorMessage) {
			}
		}, value, 1, 1000, "", 2);
	}

	private Runnable uirunable = new Runnable() {

		@Override
		public void run() {
			initRightGrid(menuList.get(iprepos).getName());

		}
	};

	private Runnable uibgrunable = new Runnable() {

		@Override
		public void run() {
			if (programList != null && programList.size() > 0) {
				imgurl = programList.get(0).getPostImg();
				_initImageBg(imgurl);
			}

		}
	};

	private void initRightGrid(String name) {
		updatePageInfo(0, pagecount, 8);
		title.setText(name);
		fcousRight = false;
		if (mGrid == null && programList.size() <= 0) {
			return;
		}
		if (mGrid == null) {
			initGrid();
		} else {
			mGrid.notifyDataChanged();
		}

	}

	private void initGrid() {

		mGrid = new Grid(this);
		mGrid.setPosition(100, 0);
		mGrid.setSize(1230, 860);
		mGrid.setGapLength(10);
		mGrid.setOrientation(Grid.ORIENTATION_VERTICAL);
		mGrid.setRowNum(4);
		mGrid.setCullingArea(new Rectangle(-60, 0, 1230 + 120, 860));
		mGrid.setCull(false);
		dataAdapter = new YouLikeGridAdapter(this);
		dataAdapter.setData(programList);
		mGrid.setAdapter(dataAdapter);
		mGrid.setAdjustiveScrollLengthForBackward(310);
		mGrid.setAdjustiveScrollLengthForForward(310);
		mGrid.setItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(Actor itemView, int position,
					AbsListView grid) {
				// 跳转详情页 ....
				Bundle options = new Bundle();
				options.putString("id", programList.get(position).getId());
				options.putString("name", programList.get(position).getPostName());
				doAction(ACTION_NAME.OPEN_DETAIL, options);
			}
		});
		mGrid.setOnItemSelectedChangeListener(new OnItemSelectedChangeListener() {

			@Override
			public void onSelectedChanged(int position, Actor actor) {
				// TODO Auto-generated method stub

				updatePageInfo(position, pagecount, 8);
			}
		});
		mGrid.setScrollStatusListener(new ScrollStatusListener() {

			@Override
			public void onScrolling(float arg0, float arg1) {
			}

			@Override
			public void onScrollStop(float arg0, float arg1) {
				mGrid.setCull(false);
			}

			@Override
			public void onScrollStart(float arg0, float arg1) {
				mGrid.setCull(true);
			}
		});
		gridcullGroup.addActor(mGrid);

	}

	@Override
	public void onLoadComplete(String arg0, TextureRegion arg1, Object arg2) {
		// TODO Auto-generated method stub
		bgImg.setDrawable(new TextureRegionDrawable(arg1));
	}

	public void updatePageInfo(int pos, int total, int pagenums) {
		if (pos == 0 && total == 0) {
			pageInfo.setText("0/0");
			return;
		}
		pagenow = pos / pagenums + 1;
		totalpage = 0;
		if (total % pagenums == 0) {
			totalpage = total / pagenums;
		} else {
			totalpage = total / pagenums + 1;
		}
		pageInfo.setText(pagenow + "/" + totalpage);
	}

	/**
	 * 初始化背景图
	 */
	private void _initImageBg(String imgurl) {
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
		_initImageBg(imgurl);
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

	public boolean onKeyDown(int keycode) {
		switch (keycode) {
		case KeyEvent.KEYCODE_DPAD_RIGHT:

			if (fcousRight) {
				return true;
			}
		}
		return super.onKeyDown(keycode);

	};
}
