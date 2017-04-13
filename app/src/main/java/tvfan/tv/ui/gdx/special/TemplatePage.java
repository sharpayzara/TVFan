package tvfan.tv.ui.gdx.special;

/**
 * 專題魔板界面
 * 
 * @author 孫文龍
 * 
 */
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.BasePage;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.dal.HttpResponse.Listener4JSONObject;
import tvfan.tv.dal.models.ProgramListItem;
import tvfan.tv.lib.Lg;
import tvfan.tv.lib.NetWorkUtils;
import tvfan.tv.ui.gdx.widgets.CIBNLoadingView;
import tvfan.tv.ui.gdx.yourLike.YouLikeGridAdapter;
import android.os.Bundle;

import tvfan.tv.R;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.luxtone.lib.image.LoaderListener;
import com.luxtone.lib.image.PageImageLoader;
import com.luxtone.lib.widget.AbsListView;
import com.luxtone.lib.widget.AbsListView.OnItemClickListener;
import com.luxtone.lib.widget.Grid;

/**
 * 用户中心界面
 * 
 * @author cibn
 * 
 */
public class TemplatePage extends BasePage implements LoaderListener {

	Image bgImg, headImage;
	Label titleLabel;
	Grid mGrid;
	private ArrayList<ProgramListItem> programList;
	String imgurl = "";
	// 图片背景
	private PageImageLoader pageImageLoader;
	private String id = "";
	private String parentCatgId = "";
	private RemoteData rd;
	private CIBNLoadingView loadingview;
	private String specialTemplateName = "";


	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		id = bundle.getString("id"); // 获取节目id,可能为""
		parentCatgId = bundle.getString("parentCatgId", "");
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
		bgImg = new Image(this);
		bgImg.setDrawableResource(R.drawable.bj);
		bgImg.setFocusAble(false);
		bgImg.setSize(1920, 1080);
		bgImg.setPosition(0, 0);
		bgImg.toBack();
		addActor(bgImg);

		loadingview = new CIBNLoadingView(this);
		loadingview.setVisible(true);
		addActor(loadingview);

		requestDate();

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

	public void requestDate() {
		rd.getTemplateList(id, new Listener4JSONObject() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					programList = new ArrayList<ProgramListItem>();

					imgurl = response.optString("imgUrl1", "");
					specialTemplateName = response.optString(
							"specialTemplateName", "");
					JSONArray jSONArray = response.getJSONArray("sections");
					JSONObject jSONObject1 = jSONArray.getJSONObject(0);
					JSONObject jSONObject2 = jSONObject1
							.getJSONObject("specialRecommends");
					JSONArray jsonarr = jSONObject2.getJSONArray("recommends");

					for (int i = 0; i < jsonarr.length(); i++) {
						ProgramListItem program = new ProgramListItem();
						JSONObject jsonobject = (JSONObject) jsonarr.get(i);

						program.setFilmid(jsonobject.optString("id", ""));
						program.setPostImg(jsonobject.optString("imgUrl", ""));
						program.setPostName(jsonobject.optString("title", ""));
						program.setAction(jsonobject.optString("action", ""));
						programList.add(program);
					}
					loadingview.setVisible(false);
					Gdx.app.postRunnable(new Runnable() {

						@Override
						public void run() {
							addGrid();
							initImagebg(imgurl);
						}
					});
				} catch (JSONException e) {
					e.printStackTrace();
					loadingview.setVisible(false);
				}
			}

			@Override
			public void onError(String errorMessage) {
				// Utils.showToast("获取数据失败,请重试...");
				Lg.e(TAG, "getTemplateList : " + errorMessage);
				NetWorkUtils.handlerError(errorMessage, TemplatePage.this);
			}
		});
	}

	private void addGrid() {

		mGrid = new Grid(TemplatePage.this);
		if ("2".equals(specialTemplateName)) {// 新影视专题
			mGrid.setPosition(225, 100);
			mGrid.setSize((AppGlobalConsts.APP_WIDTH - 225 * 2), 600);
			MovieSpecialGridAdapter dataAdapter = new MovieSpecialGridAdapter(
					this);
			dataAdapter.setData(programList);
			mGrid.setAdapter(dataAdapter);
			mGrid.setGapLength(60);
			mGrid.setCullingArea(new Rectangle(0, -100, 1470, 800));
		} else {// 旧影视专题或新闻专题
			mGrid.setPosition(100, 100);
			mGrid.setSize(1770, 400);
			YouLikeGridAdapter dataAdapter = new YouLikeGridAdapter(this);
			dataAdapter.setData(programList);
			mGrid.setAdapter(dataAdapter);
			mGrid.setGapLength(10);
			mGrid.setCullingArea(new Rectangle(-100, -100, 1920, 600));
		}
		mGrid.setOrientation(Grid.ORIENTATION_LANDSPACE);
		mGrid.setRowNum(1);
		mGrid.setCull(false);

		mGrid.setItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(Actor itemView, int position,
					AbsListView grid) {
				Bundle options = new Bundle();
				options.putString("parentCatgId", parentCatgId);
				options.putString("name", programList.get(position).getPostName());
				options.putString("programSeriesId", programList.get(position)
						.getFilmid());
				
				doAction(
						ACTION_NAME.valueOf(ACTION_NAME.class,
								programList.get(position).getAction()), options);
//				 doAction(ACTION_NAME.OPEN_DETAIL, options);

			}
		});
		addActor(mGrid);
	}

	/**
	 * 初始化背景图
	 */
	private void initImagebg(String imgurl) {
		if (pageImageLoader != null) {
			pageImageLoader.cancelLoad();
		}
		pageImageLoader = new PageImageLoader(this);
		pageImageLoader.startLoadBitmap(imgurl, "list", true, 0, this, imgurl);
	}

	@Override
	public void onLoadComplete(String arg0, TextureRegion arg1, Object arg2) {
		// TODO Auto-generated method stub
		bgImg.setDrawable(new TextureRegionDrawable(arg1));
	}

	@Override
	public boolean onBackKeyDown() {
		return super.onBackKeyDown();
	}

}
