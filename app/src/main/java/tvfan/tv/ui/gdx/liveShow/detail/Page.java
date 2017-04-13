package tvfan.tv.ui.gdx.liveShow.detail;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.BasePage;
import tvfan.tv.ui.gdx.widgets.DetailVeritcalViewPager;
import android.os.Bundle;

import tvfan.tv.R;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.luxtone.lib.image.LoaderListener;
import com.luxtone.lib.image.PageImageLoader;
import com.luxtone.lib.widget.AbsListView.OnItemSelectedChangeListener;
import com.luxtone.lib.widget.CullGroup;

public class Page extends BasePage implements LoaderListener {

	private DetailVeritcalViewPager mVeritcalViewPager;


	private Image detailBg;
	private CullGroup cullGroup;

	private DetailGroup dg; // 详情
	private RecommendGroup rg; // 推荐

	private Label title;
	
	private Image topShadow, bottomShadow;

	private PageImageLoader bgLoader;

	private String programSeriesId;
	
	private boolean is_first_in = true;
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		programSeriesId = bundle.getString("programSeriesId");
		initView();
	}

	private void initView() {

		detailBg = new Image(this);
		detailBg.setPosition(-50, -50);
		detailBg.setSize(AppGlobalConsts.APP_WIDTH+100, AppGlobalConsts.APP_HEIGHT+100);
		detailBg.setDrawable(findTextureRegion(R.drawable.default_background));
		detailBg.setFocusAble(false);
		detailBg.setAlpha(0.95f);
		addActor(detailBg);

		cullGroup = new CullGroup(this);
		cullGroup.setSize(180, 825);
		cullGroup.setPosition(168, 7.5f);
		cullGroup.setCullingArea(new Rectangle(0, 0, 180, 825));
		addActor(cullGroup);


		// 加载verticalViewPager
		mVeritcalViewPager = new DetailVeritcalViewPager(this);
		mVeritcalViewPager.setSize(1630, AppGlobalConsts.APP_HEIGHT);
		mVeritcalViewPager.setPosition(146, 50);
		dg = new DetailGroup(this, programSeriesId);
		dg.setName("dg");
		rg = new RecommendGroup(this, programSeriesId);
		rg.setName("rg");
		mVeritcalViewPager.addSection(dg);
		mVeritcalViewPager.addSection(rg);
		mVeritcalViewPager.commit();
		addActor(mVeritcalViewPager);

		//dg.getPlayBtn().requestFocus();
		//dg.getBuyBtn().requestFocus();
	}

	@Override
	public void recyclePage() {
		dg.clear();
		rg.clear();
		super.recyclePage();
	}

	@Override
	public void onResume() {
		
		if (dg != null && !is_first_in) {
			dg.onResume();
		}
		is_first_in = false;
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	};

	OnItemSelectedChangeListener onItemSelectedChangeListener = new OnItemSelectedChangeListener() {

		@Override
		public void onSelectedChanged(int position, Actor actor) {
		}
	};


	public void setBackGroud(String url) {
		if (bgLoader != null) {
			bgLoader.cancelLoad();
		}
		bgLoader = new PageImageLoader(this);
		bgLoader.startLoadBitmapByFilter(url, "bg", false, 0, this, url);
	}

	@Override
	public void onLoadComplete(String url, TextureRegion textureRegion,
			Object obj) {
		detailBg.setDrawable(textureRegion);
	}

	public boolean onKeyDown(int keycode) {
		return super.onKeyDown(keycode);
	}

}
