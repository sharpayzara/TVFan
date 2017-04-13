package tvfan.tv.ui.gdx.search;

import android.os.Bundle;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.actions.RotateByAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.luxtone.lib.gdx.OnClickListener;
import com.luxtone.lib.gdx.OnFocusChangeListener;
import com.luxtone.lib.widget.AbsListView;
import com.luxtone.lib.widget.AbsListView.OnItemClickListener;
import com.luxtone.lib.widget.AbsListView.OnItemSelectedChangeListener;
import com.luxtone.lib.widget.Grid;
import com.luxtone.lib.widget.PagingDataObtainedCallback;
import com.luxtone.lib.widget.PagingGrid;
import com.luxtone.lib.widget.PagingGrid.IPagingActionFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tvfan.tv.BasePage;
import tvfan.tv.R;
import tvfan.tv.dal.HttpResponse.Listener4JSONObject;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.dal.models.ProgramListItem;
import tvfan.tv.lib.Lg;
import tvfan.tv.lib.NetWorkUtils;
import tvfan.tv.lib.Utils;
import tvfan.tv.ui.gdx.widgets.Button;

/**
 * 搜索
 * 
 */
public class Page extends BasePage {
	// 初始化右侧栏海报列表坐标宽高
	// 数据
	private ArrayList<ProgramListItem> programList;// 右侧栏海报列表页数据
	private int totalnumber = 0;
	private String parentCatgId;
	private PagingGrid<ProgramListItem> mGrid;
	private List<ProgramListItem> indexProgramList = new ArrayList<ProgramListItem>();
	private PagingDataObtainedCallback<ProgramListItem> mCallback;
	private Label gdTitle, pageInfo;
	private Label mTitle, mEdit;
	private List<String> mNumerKey = new ArrayList<String>();
	private Grid mKeypadGrid;
	private SearchAdapter mSearchAdapter;
	private StringBuffer sb = new StringBuffer("");
	private RemoteData rd;
	private Image bgImg;
	private int searchTypes = 0;
	private int pagenow = 0;
	private int totalpage = 0;
	private Image clearBg, backBg;
	private Button clear, back;
	private Label searchN;
	Image imageline;
	private SearchGridAdapter dataAdapter;
	private Timer timer;
	private Task task;
	private Image loadingImage;
	private String title;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		timer = new Timer();
		title = bundle.getString("title");
		initView();
		initNumericKeypad();
		parentCatgId = bundle.getString("parentCatgId"); // 获取栏目id,可能为""
		if (parentCatgId == null) {
			parentCatgId = "";
		}
		rd = new RemoteData(this.getActivity());

		initProgramList();
	}

	private void initView() {

		for (char i = 'A'; i <= 'Z'; i++) {
			mNumerKey.add(String.valueOf(i));
		}
		for (int t = 1; t <= 9; t++) {
			mNumerKey.add(String.valueOf(t));
		}
		mNumerKey.add("0");

		// 初始化背景
		bgImg = new Image(this);
		bgImg.setPosition(0, 0);
		bgImg.setSize(1920, 1080);
		bgImg.setDrawableResource(R.drawable.bj);
		addActor(bgImg);

		// 初始化标题
		mTitle = new Label(this);
		mTitle.setText(title);
		mTitle.setTextSize(60);
		mTitle.setPosition(170, 920);
		mTitle.setAlpha(0.9f);
		mTitle.setTextColor(0xffffffff);
		addActor(mTitle);

		// 初始化页码
		pageInfo = new Label(this);
		pageInfo.setText("0/0");
		pageInfo.setColor(Color.WHITE);
		pageInfo.setAlpha(0.9f);
		pageInfo.setTextSize(40);
		pageInfo.setSize(250, 40);
		pageInfo.setPosition(1475, 920);
		pageInfo.setAlignment(Align.right);
		addActor(pageInfo);

		// 初始化right title
		gdTitle = new Label(this);
		gdTitle.setText("大家都在搜");
		gdTitle.setColor(Color.WHITE);
		gdTitle.setAlpha(0.9f);
		gdTitle.setTextSize(50);
		gdTitle.setSize(250, 50);
		gdTitle.setAlignment(Align.left);
		gdTitle.setPosition(805, 920);
		addActor(gdTitle);

		// 初始化输入框
		mEdit = new Label(this);
		mEdit.setTextSize(40);
		mEdit.setTextColor(0xffffffff);
		mEdit.setPosition(170, tvfan.tv.lib.Utils.getY(230));
		mEdit.setAlpha(0.9f);
		addActor(mEdit);

		imageline = new Image(this);
		imageline.setDrawableResource(R.drawable.search_textline);
		imageline.setPosition(170, 830);
		addActor(imageline);

		// 暂无订购
		searchN = new Label(this);
		searchN.setPosition(1115, 440);
		searchN.setTextSize(50);
		searchN.setColor(Color.WHITE);
		searchN.setText("暂无搜索结果");
		searchN.setAlpha(0.9f);
		searchN.setVisible(false);
		addActor(searchN);
		// 正在搜索

		loadingImage = new Image(this);
		loadingImage.setPosition(1050, 100);
		loadingImage.setSize(100, 100);
		loadingImage.setDrawable(findTextureRegion(R.drawable.new_foucs));
		loadingImage.setFocusAble(false);
		loadingImage.setOrigin(50, 50);
		loadingImage.clearActions();
		loadingImage.setVisible(false);
		addActor(loadingImage);

		initClearAndBack();

	}

	private void initNumericKeypad() {
		mKeypadGrid = new Grid(this);
		mKeypadGrid
				.setCullingArea(new Rectangle(-45, -10, 555 + 190, 555 + 190));
		mKeypadGrid.setCull(false);
		mKeypadGrid.setRowNum(6);
		mKeypadGrid.setPosition(168, 1080 - 838);
		mKeypadGrid.setOrientation(Grid.ORIENTATION_VERTICAL);
		mKeypadGrid.setSize(555, 555);
		mKeypadGrid.setGapLength(15);
		mKeypadGrid.setItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(Actor arg0, int arg1, AbsListView arg2) {
				// TODO Auto-generated method stub
				if (sb.toString().length() < 25) {
					sb.append(mNumerKey.get(arg1));
					mEdit.setText(sb.toString());
					gdTitle.setText("搜索结果");
					searchTypes = 0;
					requestDateTask(1, 100, sb.toString(), false);
				}
			}
		});
		mSearchAdapter = new SearchAdapter(this, mNumerKey);
		mKeypadGrid.setAdapter(mSearchAdapter);
		addActor(mKeypadGrid);
		mKeypadGrid.setSelection(0, true);
	}

	private void initClearAndBack() {

		clearBg = new Image(this);
		clearBg.setSize(271 + 32, 83 + 44);
		clearBg.setPosition(168 - 16, 1080 - 936 - 22);
		clearBg.setDrawableResource(R.drawable.new_foucs);
		clearBg.setVisible(false);
		addActor(clearBg);

		backBg = new Image(this);
		backBg.setSize(271 + 32, 83 + 44);
		backBg.setVisible(false);
		backBg.setPosition(168 - 16 + 271 + 15, 1080 - 936 - 22);
		backBg.setDrawableResource(R.drawable.new_foucs);
		addActor(backBg);

		clear = new Button(this, 271, 83);
		clear.setPosition(168, 1080 - 936);
		clear.setName("clear");
		clear.setNextFocusRight("back");
		clear.getImage().setDrawableResource(R.drawable.search_clear);
		clear.setFocusBackGround(R.drawable.search_clear);
		clear.setUnFocusBackGround(R.drawable.search_clear);
		clear.setFocusAble(true);
		clear.getLabel().setText("清除");
		clear.getLabel().setColor(Color.WHITE);
		clear.getLabel().setTextSize(40);
		clear.getLabel().setAlpha(0.9f);
		clear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(Actor arg0) {
				// TODO Auto-generated method stub
				if (sb.length() != 0) {
					sb.setLength(0);
					mEdit.setText(sb.toString());
					gdTitle.setText("大家都在搜");
					searchTypes = 0;
					requestDateTask(1, 100, "", false);
				}
			}
		});
		clear.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChanged(Actor arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (arg1) {
					backBg.setVisible(false);
					clearBg.setVisible(true);
				} else {
					clearBg.setVisible(false);
				}
			}
		});
		addActor(clear);
		back = new Button(this, 271, 83);
		back.setPosition(168 + 271 + 15, 1080 - 936);
		back.setName("back");
		back.setNextFocusLeft("clear");
		back.getImage().setDrawableResource(R.drawable.search_clear);
		back.setFocusBackGround(R.drawable.search_clear);
		back.setUnFocusBackGround(R.drawable.search_clear);
		back.setFocusAble(true);
		back.getLabel().setText("退格");
		back.getLabel().setColor(Color.WHITE);
		back.getLabel().setTextSize(40);
		back.getLabel().setAlpha(0.9f);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(Actor arg0) {
				// TODO Auto-generated method stub
				if (sb.length() != 0) {
					sb.deleteCharAt(sb.length() - 1);
					mEdit.setText(sb.toString());
					searchTypes = 0;
					if (sb.length() != 0)
						gdTitle.setText("搜索结果");
					else
						gdTitle.setText("大家都在搜");
					requestDateTask(1, 100, sb.toString(), false);
				}
			}
		});
		back.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChanged(Actor arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (arg1) {
					clearBg.setVisible(false);
					backBg.setVisible(true);
				} else {
					backBg.setVisible(false);
				}
			}
		});
		addActor(back);
	}

	private void seaechNull(boolean flag) {
		if (mGrid != null)
			mGrid.setVisible(!flag);
		if (searchN != null)
			searchN.setVisible(flag);
		if(flag && pageInfo!=null)
			pageInfo.setText("0/0");
	}

	private void initProgramList() {

		// 初始化Grid
		mGrid = new PagingGrid<ProgramListItem>(this, 100);
		mGrid.addInterceptKey(Keys.DOWN);
		mGrid.setSize(920, 860);
		mGrid.setPosition(805, 0);
		mGrid.setRowNum(3);
		mGrid.setCullingArea(new Rectangle(-45, -30, 920 + 90, 860 + 60));
		mGrid.setOrientation(Grid.ORIENTATION_VERTICAL);
		mGrid.setGapLength(10);
		mGrid.setAdjustiveScrollLengthForBackward(380);
		mGrid.setAdjustiveScrollLengthForForward(487);
		dataAdapter = new SearchGridAdapter(this, mGrid, totalnumber);
		mGrid.setPagingAdapter(dataAdapter);
		mGrid.setPagingActionFactory(new IPagingActionFactory<ProgramListItem>() {
			@Override
			public void obtainData(final int page, final int pageSize,
					final PagingDataObtainedCallback<ProgramListItem> callback) {
				mCallback = callback;
				requestDateTask(page, pageSize, sb.toString(), true);
			}

			@Override
			public void showLoading(boolean b) {
				// 是否正在拉去数据
			}
		});
		mGrid.setItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(Actor actor, int pos,
					AbsListView abslistview) {
				// 跳转详情页 ....
				if (indexProgramList.size() > pos) {
					ProgramListItem programListItem = indexProgramList.get(pos);
					Bundle options = new Bundle();
					options.putString("id", programListItem.getId());
					options.putString("name", programListItem.getPostName());
					Page.this.doAction(ACTION_NAME.OPEN_DETAIL, options);
				}
			}
		});
		mGrid.setOnItemSelectedChangeListener(new OnItemSelectedChangeListener() {

			@Override
			public void onSelectedChanged(int position, Actor actor) {
				// TODO Auto-generated method stub
				updatePageInfo(position, totalnumber, 3);
			}
		});
		mGrid.obtainData();
		addActor(mGrid);
	}

	public void requestDateTask(final int pagenum, final int pagesize,
			final String value, final Boolean ifPull) {
		if (!ifPull) {
			indexProgramList.clear();
			if (mGrid.isVisible())
				mGrid.setVisible(false);
			if (searchN.isVisible())
				searchN.setVisible(false);

		}
		if (task != null) {
			task.cancel();
		}
		if (timer != null) {
			timer.stop();
		}
		task = new Task() {

			@Override
			public void run() {
				requestDate(pagenum, pagesize, value, ifPull);
			}
		};
		timer.scheduleTask(task, 0.5f);
		timer.start();
	}

	/**
	 * 获取电影列表
	 * 
	 */
	public void requestDate(final int pagenum, final int pagesize,
			final String value, final Boolean ifPull) {
		RotateByAction rotateAction = Actions.rotateBy(-360, 0.8f);
		RepeatAction epeatAction = Actions.repeat(RepeatAction.FOREVER,
				rotateAction);
		loadingImage.setVisible(true);
		loadingImage.toFront();
		loadingImage.clearActions();
		loadingImage.addAction(epeatAction);
		rd.getSearchList(new Listener4JSONObject() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					if(programList == null)
						programList = new ArrayList<ProgramListItem>();
					else
						programList.clear();
					
					JSONArray jsonarr = response.getJSONArray("programList");
					totalnumber = jsonarr.length();
					for (int i = 0; i < totalnumber; i++) {
						JSONObject jsonobject = (JSONObject) jsonarr.get(i);
						ProgramListItem program = new ProgramListItem();
						program.setId(jsonobject.optString("id", ""));
						program.setPostImg(jsonobject.optString("image", ""));
						program.setPostName(jsonobject.optString("name", ""));
						program.setCurrentNum(jsonobject.optString(
								"currentNum", ""));
						program.setCornerPrice(jsonobject.optString(
								"cornerPrice", ""));
						program.setCornerType(jsonobject.optString(
								"cornerType", ""));
						programList.add(program);
					}
					if(indexProgramList.size() != 0)
						indexProgramList.clear();
					indexProgramList.addAll(programList);
					if (pagenum == 1) {
						updatePageInfo(pagenum, totalnumber, 3);
					}
					loadingImage.clearActions();
					loadingImage.setVisible(false);
					dataAdapter.setTotalCount(totalnumber);
					mCallback.onDataObtained(pagenum, programList);
					if (programList.size() == 0) {
						seaechNull(true);
					} else {
						seaechNull(false);
					}
				} catch (JSONException e) {
					loadingImage.clearActions();
					loadingImage.setVisible(false);
					if (!ifPull) {
						seaechNull(true);
					}
					e.printStackTrace();
				}
			}

			@Override
			public void onError(String errorMessage) {
				if (mCallback != null)
					mCallback.onDataObtainedFailed(pagenum);
				Lg.e(TAG, "getSearchList : " + errorMessage);
				NetWorkUtils.handlerError(errorMessage, Page.this);
			}
		}, value, pagenum, pagesize, parentCatgId, searchTypes);
	}

	public void updatePageInfo(int pos, int total, int pagenums) {
		pagenow = pos/pagenums+1;
		totalpage = total/pagenums;		
		if (pagenums >= total) {
			totalpage = 1;
		} else {
			if (total%pagenums>0) {
				totalpage = totalpage + 1;
			}
		}
		pageInfo.setText(pagenow+"/"+totalpage);
	}

	@Override
	protected void onResumeTextures() {
		super.onResumeTextures();
		Utils.resetImageSource(bgImg, R.drawable.bj);
		Utils.resetImageSource(imageline, R.drawable.search_textline);
		clear.getImage().setDrawableResource(R.drawable.search_clear);
		back.getImage().setDrawableResource(R.drawable.search_clear);
		backBg.setDrawableResource(R.drawable.new_foucs);
		clearBg.setDrawableResource(R.drawable.new_foucs);
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

	@Override
	public boolean onBackKeyDown() {
		return super.onBackKeyDown();
	}
}