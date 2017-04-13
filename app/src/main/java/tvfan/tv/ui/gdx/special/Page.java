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
public class Page extends BasePage implements LoaderListener {

	Image bgImg, lineImg;
	Label titleLabel, pageLabel;
	private PageImageLoader pageImageLoader;
	Grid mGrid;
	private List<ProgramListItem> programList;
	private SpecialItemAdapter dataAdapter;
	private int pagenumbers = 12;// 每页个数
	private int pagenow = 0;
	private int totalpage = 0;
	private int totalnumber = 0;
	private String id = "";
	private String imgurl = "";
	private CIBNLoadingView loadingview;
	private RemoteData rd;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		id = bundle.getString("id"); // 获取节目id,可能为""
		rd = new RemoteData(getActivity());
		initTitle();
		requestDate();
	}

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
	};

	private void initTitle() {

		bgImg = new Image(this);
		bgImg.setDrawableResource(R.drawable.bj);
		bgImg.setFocusAble(false);
		bgImg.setSize(1920, 1080);
		bgImg.setPosition(0, 0);
		bgImg.toBack();
		addActor(bgImg);

		titleLabel = new Label(this);
		titleLabel.setPosition(145, 920);
		titleLabel.setAlpha(0.7f);
		titleLabel.setTextColor(Color.parseColor("#ffffff"));
		titleLabel.setTextSize(60);
		titleLabel.setText("专题汇总");
		addActor(titleLabel);

		pageLabel = new Label(this);
		pageLabel.setPosition(1575, 920);
		pageLabel.setTextSize(40);
		pageLabel.setSize(200, 40);
		pageLabel.setAlignment(Align.right);
		pageLabel.setText("0/0");
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
		if (pos == 0 && total == 0){
			pageLabel.setText("0/0");
			return;
		}
		pagenow = pos / 4 + 1;
		totalpage = 0;
		if (total % 4 == 0) {
			totalpage = total / 4;
		} else {
			totalpage = total / 4 + 1;
		}
		pageLabel.setText(pagenow + "/" + totalpage);
	}

	public void requestDate() {
		rd.getSpecialList(id, new Listener4JSONObject() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					programList = new ArrayList<ProgramListItem>();
					JSONArray jsonarr = response.getJSONArray("specialList");
					for (int i = 0; i < jsonarr.length(); i++) {
						ProgramListItem program = new ProgramListItem();
						JSONObject jsonobject = (JSONObject) jsonarr.get(i);
						program.setFilmid(jsonobject.optString("id", ""));
						program.setPostImg(jsonobject.optString("image", ""));
						program.setPostName(jsonobject.optString("name", ""));
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
				Lg.e(TAG, "getSepcialList : " + errorMessage);
				NetWorkUtils.handlerError(errorMessage, Page.this);
			}
		});
	}

	private void addGrid() {
		mGrid = new Grid(Page.this);
		mGrid.setPosition(145, 0);
		mGrid.setSize(1630, 870);
		mGrid.setGapLength(10);
		mGrid.setOrientation(Grid.ORIENTATION_VERTICAL);
		mGrid.setRowNum(4);
		mGrid.setCull(false);
		mGrid.setCullingArea(new Rectangle(-100, -50, 1630 + 200, 870 + 100));
		dataAdapter = new SpecialItemAdapter(this);
		dataAdapter.setData(programList);
		mGrid.setAdapter(dataAdapter);
		mGrid.setAdjustiveScrollLengthForBackward(310);
		mGrid.setAdjustiveScrollLengthForForward(310);
		mGrid.setItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(Actor itemView, int position,
					AbsListView grid) {
				Bundle options = new Bundle();
				options.putString("id", programList.get(position).getFilmid());
				doAction(ACTION_NAME.OPEN_SPECIAL_TEMPLATE, options);
				// startGdxPage(tvfan.tv.ui.gdx.special.TemplatePage.class);
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
}
