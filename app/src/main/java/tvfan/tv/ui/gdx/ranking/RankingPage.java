package tvfan.tv.ui.gdx.ranking;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import tvfan.tv.BasePage;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.dal.HttpResponse.Listener4JSONObject;
import tvfan.tv.dal.models.ProgramListItem;
import tvfan.tv.lib.Lg;
import tvfan.tv.lib.NetWorkUtils;
import tvfan.tv.ui.gdx.widgets.CIBNLoadingView;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import tvfan.tv.R;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.image.LoaderListener;
import com.luxtone.lib.image.PageImageLoader;
import com.luxtone.lib.widget.AbsListView;
import com.luxtone.lib.widget.AbsListView.OnItemClickListener;
import com.luxtone.lib.widget.AbsListView.OnItemSelectedChangeListener;
import com.luxtone.lib.widget.Grid;
import com.luxtone.lib.widget.PagingDataObtainedCallback;
import com.luxtone.lib.widget.PagingGrid;
import com.luxtone.lib.widget.PagingGrid.IPagingActionFactory;

/**
 * 排名
 * 
 * @author sunwenlong
 * 
 */
public class RankingPage extends BasePage implements LoaderListener {

	Image bgImg;
	Label titleLabel;
//	private CullGroup cullGroup;
	public LinkedHashMap<String, List<ProgramListItem>> data = new LinkedHashMap<String, List<ProgramListItem>>();
	private List<ProgramListItem> programList;
	private PageImageLoader pageImageLoader;
	private RemoteData rd;
	private String classType = "";
	private String imgurl;
//	private boolean ifLastPos = false;
	private CIBNLoadingView loadingview;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		// 初始化背景和title
		rd = new RemoteData(getActivity());
		initTitle();
		// 获取数据
		requestDate();

	}

	private void initTitle() {

		bgImg = new Image(this);
		bgImg.setDrawableResource(R.drawable.bj);
		bgImg.setFocusAble(false);
		bgImg.setSize(1920, 1080);
		bgImg.setPosition(0, 0);
		addActor(bgImg);

		titleLabel = new Label(this);
		titleLabel.setPosition(170, 930);
		titleLabel.setAlpha(0.9f);
		titleLabel.setTextColor(Color.parseColor("#ffffff"));
		titleLabel.setTextSize(60);
		titleLabel.setText("排行榜");
		addActor(titleLabel);

		/*cullGroup = new CullGroup(this);
		cullGroup.setSize(1920, 880);
		cullGroup.setFocusAble(false);
		cullGroup.setCullingArea(new Rectangle(0, 0, 1920, 880));
		addActor(cullGroup);*/
		
		loadingview = new CIBNLoadingView(this);
		loadingview.setVisible(true);
		addActor(loadingview);
	}

	private void initmenuList() {

		mVeritcalViewPager = new RankingVeritcalViewPager(this);
		mVeritcalViewPager.setSize(1920, 880);
		mVeritcalViewPager.setPosition(0, 0);
		// 取key放入数组中
		Set<String> keys = data.keySet();
		Iterator<String> it2 = keys.iterator();
		CONTENT = new String[keys.size()];
		int n = 0;
		while (it2.hasNext()) {
			CONTENT[n] = it2.next();
			n++;
		}
		for (int i = 0; i < data.size(); i++) {
			dg = new ViewPagerItem(this, i);
			mVeritcalViewPager.addSection(dg);
			if(i == 0){
				((ViewPagerItem) dg).setGridForceAble();
			}
		}
		mVeritcalViewPager.commit();
		/*cullGroup.*/addActor(mVeritcalViewPager);
	}

	@Override
	public void onLoadComplete(String arg0, TextureRegion arg1, Object arg2) {
		// TODO Auto-generated method stub
		bgImg.setDrawable(new TextureRegionDrawable(arg1));
	}

	public void requestDate() {
		rd.getRankList(classType, new Listener4JSONObject() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONArray jsonarrdata = response.getJSONArray("data");
					// 页数
					int listSize = jsonarrdata.length();
					totalNumber = new int[listSize];
					// 解析数据
					for (int i = 0; i < listSize; i++) {
						programList = new ArrayList<ProgramListItem>();
						JSONObject jsondata = (JSONObject) jsonarrdata.get(i);
						String classType = jsondata.optString("channel", "");
						JSONArray jsonprogramList = jsondata
								.getJSONArray("programList");

						for (int k = 0; k < jsonprogramList.length(); k++) {
							JSONObject jsonprogram = (JSONObject) jsonprogramList
									.get(k);
							ProgramListItem program = new ProgramListItem();
							
							program.setId(jsonprogram.optString("id", ""));
							program.setPostName(jsonprogram.optString("name", ""));
							program.setPostImg(jsonprogram.optString("image", ""));
							program.setCurrentNum(jsonprogram.optString("currentNum", ""));
							program.setCornerType(jsonprogram.optString("cornerType", ""));
							program.setCornerPrice(jsonprogram.optString("cornerPrice", ""));
							if (k == 0) {
								imgurl = jsonprogram.optString("image", "");
							}
							programList.add(program);
						}
						totalNumber[i] = programList.size();
						data.put(classType + "排行", programList);
						loadingview.setVisible(false);
					}

				} catch (Exception e) {
					e.printStackTrace();
					loadingview.setVisible(false);
				}
				
				Gdx.app.postRunnable(uimenurunable);
			}

			@Override
			public void onError(String errorMessage) {
				Lg.e(TAG, "getRandkingList : " + errorMessage);
				NetWorkUtils.handlerError(errorMessage, RankingPage.this);
			}
		});
	}

	// 加载背景
	private Runnable uimenurunable = new Runnable() {

		@Override
		public void run() {
			// 初始化视图
			initmenuList();
			// 设置背景
			initImagebg(imgurl);
		}
	};

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
		/*for (int i = 0; i < FocusFinder.findAllActorsInGroup(cullGroup).size(); i++) {
			Actor actor = FocusFinder.findAllActorsInGroup(cullGroup).get(i);
			if (actor != null&&actor.getName().indexOf("line") != -1) {
				Utils.resetImageSource((Image)actor,
						R.drawable.line);
			}
		}*/
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
	};

/*	public boolean onKeyDown(int keycode) {
		if (ifLastPos && keycode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			return true;
		}
		Actor actor = cullGroup.getFocusContainer().getFocusActor();
		if (actor != null && keycode == Keys.UP) {
			Actor upActor = FocusFinder.findNextActorInGroup(actor,
					FocusFinder.UP, actor.getParent().getParent());
			if (upActor == null) {
				String name = actor.getParent().getParent().getName();
				int i = Integer.valueOf(name.substring(("viewPagerItem"
						.length())));
				Actor up = cullGroup.findActor("viewPagerItem" + (i - 1) + "");
				if (up != null)
					up.requestFocus();
				return true;
			}
		}

		if (actor != null && keycode == Keys.DOWN) {
			Actor downActor = FocusFinder.findNextActorInGroup(actor,
					FocusFinder.DOWN, actor.getParent().getParent());
			if (downActor == null) {
				String name = actor.getParent().getParent().getName();
				int i = Integer.valueOf(name.substring(("viewPagerItem"
						.length())));
				Actor down = cullGroup
						.findActor("viewPagerItem" + (i + 1) + "");
				if (down != null)
					down.requestFocus();
				return true;
			}
		}

		return super.onKeyDown(keycode);

	};*/

	class ViewPagerItem extends Group {
		int mark = 0;
		// private CullGroup cullGroup1;
		private Label label, label1;
		PagingGrid<ProgramListItem> mGrid;
		Image lineImg;
		private int pagenow = 0;
		private int totalpage = 0;

		public ViewPagerItem(Page page, int i) {
			super(page);
			// TODO Auto-generated constructor stub
			mark = i;
			setName("viewPagerItem" + i);
			// 排汗榜标题
			label = new Label(getPage(), false);
			label.setPosition(170, 470);
			label.setTextSize(40);
			label.setSize(300, 40);
			label.setFocusAble(false);
			label.setMaxLine(1);
			label.setText(CONTENT[i]);
			label.setAlpha(0.9f);
			label.setTextColor(Color.parseColor("#ffffff"));
			addActor(label);

			// 分割线
			lineImg = new Image(getPage());
			lineImg.setPosition(350, 490);
			lineImg.setSize(1270, 2);
			lineImg.setName("line" + i);
			lineImg.setDrawableResource(R.drawable.line);
			lineImg.setFocusAble(false);
			addActor(lineImg);

			// 页数
			label1 = new Label(getPage(), false);
			label1.setPosition(1390 + 170, 480);
			label1.setMaxLine(1);
			label1.setTextSize(30);
			label1.setSize(150, 30);
			label1.setFocusAble(false);
			label1.setAlignment(Align.right);
			label1.setText("0/0");
			label1.setAlpha(0.9f);
			label1.setTextColor(Color.parseColor("#ffffff"));
			addActor(label1);
			updatePageInfo(0, totalNumber[i], 5);

			// mGrid
			mGrid = new PagingGrid<ProgramListItem>(getPage(), 32);
			mGrid.setSize(1920 - 170 - 60, 400);
			mGrid.setPosition(170, 50);
			mGrid.setOrientation(Grid.ORIENTATION_LANDSPACE);
			mGrid.setGapLength(10);
			mGrid.setCull(false);
			mGrid.setName(String.valueOf(i));
			mGrid.setNextFocusUp(String.valueOf(i - 1));
			mGrid.setNextFocusDown(String.valueOf(i + 1));
			mGrid.setCullingArea(new Rectangle(-170, -80, 1920, 560));
			mGrid.setRowNum(1);
			mGrid.setAdjustiveScrollLengthForBackward(350);
			mGrid.setAdjustiveScrollLengthForForward(380);
			dataAdapter = new RankingItemAdapter(getPage(), mGrid, 2);
			mGrid.setPagingAdapter(dataAdapter);
			mGrid.setItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(Actor itemView, int position,
						AbsListView grid) {
					Bundle options = new Bundle();
					options.putString("id", mGrid.getDataList().get(position)
							.getId());
					options.putString("name", mGrid.getDataList().get(position)
							.getPostName());
					doAction(ACTION_NAME.OPEN_DETAIL, options);

				}
			});
			mGrid.setOnItemSelectedChangeListener(new OnItemSelectedChangeListener() {

				@Override
				public void onSelectedChanged(int position, Actor actor) {
					// TODO Auto-generated method stub
					/*if (position == mGrid.getDataList().size() - 1) {
						ifLastPos = true;
					} else {
						ifLastPos = false;
					}*/
					updatePageInfo(position, mGrid.getDataList().size(), 5);
				}
			});
			if (mGrid != null) {
				mGrid.setPagingActionFactory(new IPagingActionFactory<ProgramListItem>() {

					@Override
					public void obtainData(
							final int page,
							final int pageSize,
							final PagingDataObtainedCallback<ProgramListItem> callback) {
						callback.onDataObtained(page, data.get(CONTENT[mark]));
					}

					@Override
					public void showLoading(boolean b) {
						// 是否正在拉去数据
					}
				});
				mGrid.obtainData();
			}
			addActor(mGrid);
			// addActor(cullGroup1);
		}

		public Image getLineView() {
			return lineImg;
		}
		public void setGridForceAble() {
			mGrid.setSelection(0, true);
		}

		public void updatePageInfo(int pos, int total, int pagenums) {
			if (pos == 0 && total == 0) {
				label1.setText("0/0");
				return;
			}
			pagenow = pos / pagenums + 1;
			totalpage = 0;
			if (total % pagenums == 0) {
				totalpage = total / pagenums;
			} else {
				totalpage = total / pagenums + 1;
			}
			label1.setText(pagenow + "/" + totalpage);
		}

	}

	private RankingItemAdapter dataAdapter;
	Group dg;
	int[] totalNumber;
	private RankingVeritcalViewPager mVeritcalViewPager;
	protected String[] CONTENT = null;
	Context context;
}
