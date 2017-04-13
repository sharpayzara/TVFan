package tvfan.tv.ui.gdx.brand;

import org.json.JSONArray;
import org.json.JSONObject;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.BasePage;
import tvfan.tv.dal.LocalData;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.dal.HttpResponse.Listener4JSONObject;
import tvfan.tv.lib.Lg;
import tvfan.tv.lib.NetWorkUtils;
import tvfan.tv.lib.Utils;
import tvfan.tv.ui.gdx.widgets.TVFANLabel;
import tvfan.tv.ui.gdx.widgets.CIBNLoadingView;
import android.os.Bundle;

import tvfan.tv.R;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.widget.AbsListView;
import com.luxtone.lib.widget.AbsListView.OnItemClickListener;
import com.luxtone.lib.widget.AbsListView.OnItemSelectedChangeListener;
import com.luxtone.lib.widget.CullGroup;
import com.luxtone.lib.widget.Grid;

/**
 * 品牌专区界面
 * 
 * @author cibn
 * 
 */
public class Page extends BasePage {

	private final static String TAG = "BRANDINDEX.PAGE";

	private Image bg;
	private TVFANLabel title;
	private TVFANLabel pageNum;
	private Grid listGrid;
	private BrandIndexAdapter adapter;
	private RemoteData rd;

	private CullGroup cullGroup;
	private JSONObject brandlist;

	private String parentCatgId = "";

	private CIBNLoadingView loadingview;

//	private int bgResourceId = R.drawable.bja;
	private int bgResourceId =  R.drawable.home_background;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		rd = new RemoteData(getActivity());

		parentCatgId = bundle.getString("id");

		LocalData ld = new LocalData(getActivity());
		String bg = ld.getKV(AppGlobalConsts.PERSIST_NAMES.BACKGROUND_IMAGE
				.name());

		if (bg == null)
//			bgResourceId = R.drawable.bja;
			bgResourceId = R.drawable.home_background;
		else
			bgResourceId = Integer.parseInt(bg);

		initView();
	}

	@Override
	protected void onResumeTextures() {
		super.onResumeTextures();
		Utils.resetImageSource(bg, bgResourceId);
		pageNum.setText("1/1");
	}

	private void initView() {
		bg = new Image(this);
		bg.setPosition(0, 0);
		bg.setSize(AppGlobalConsts.APP_WIDTH, AppGlobalConsts.APP_HEIGHT);
		bg.setDrawableResource(bgResourceId);
		addActor(bg);

		title = new TVFANLabel(this);
		title.setPosition(170, 900);
		title.setAlignment(Align.center);
		title.setSize(200, 80);
		title.setTextSize(60);
		title.setColor(Color.valueOf("f0f0f0"));
		title.setText("品牌专区");
		// title.setAlpha(0.6f);
		addActor(title);

		pageNum = new TVFANLabel(this);
		pageNum.setPosition(1700, 900);
		pageNum.setAlignment(Align.right);
		pageNum.setSize(50, 80);
		pageNum.setTextSize(40);
		pageNum.setText("0/0");
		pageNum.setAlpha(0.6f);
		addActor(pageNum);

		cullGroup = new CullGroup(this);
		cullGroup.setPosition(170, 0);
		cullGroup.setSize(1580, 850);
		cullGroup.setCullingArea(new Rectangle(-70, 0, 1700, 910));
		addActor(cullGroup);

		loadingview = new CIBNLoadingView(this);
		loadingview.setVisible(true);
		addActor(loadingview);
		loadingview.toFront();

		rd.getBrandList(parentCatgId, new Listener4JSONObject() {

			@Override
			public void onResponse(JSONObject response) {
				if (isDisposed()) {
					return;
				}
				if (response == null) {
					Lg.e(TAG,
							"brand list page get brand list response is null");
					return;
				}
				try {
					brandlist = response;
					Gdx.app.postRunnable(new Runnable() {

						@Override
						public void run() {
							addGrid();
							loadingview.setVisible(false);
						}
					});

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(String errorMessage) {
				Lg.e(TAG, "getBrandList : " + errorMessage);
				NetWorkUtils.handlerError(errorMessage, Page.this);
			}
		});

	}

	private void addGrid() {

		if (brandlist != null) {
			JSONArray array = brandlist.optJSONArray("menuList");

			adapter = new BrandIndexAdapter(this);
			adapter.setData(array);

			listGrid = new Grid(Page.this);
			listGrid.setCull(false);
			listGrid.setSize(1580, 850);
			listGrid.setGapLength(10);
			// listGrid.setPosition(170, 0);
			listGrid.setOrientation(Grid.ORIENTATION_VERTICAL);
			listGrid.setRowNum(5);
			listGrid.setAdapter(adapter);
			cullGroup.addActor(listGrid);

			pageNum.setText(1 + "/" + array.length());

			listGrid.setItemClickListener(itemClickListener);
			listGrid.setOnItemSelectedChangeListener(new OnItemSelectedChangeListener() {

				@Override
				public void onSelectedChanged(int position, Actor actor) {
					pageNum.setText(position + 1 + "/"
							+ listGrid.getAdapterCount());
				}
			});
		}

	}

	OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(Actor actor, int position, AbsListView group) {
			try {
				JSONArray array = brandlist.optJSONArray("menuList");
				JSONObject obj = (JSONObject) array.get(position);
				Bundle bundle = new Bundle();
				bundle.putString("parentCatgId", obj.optString("id"));
				doAction(ACTION_NAME.OPEN_BRAND_DETAIL, bundle);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
}
