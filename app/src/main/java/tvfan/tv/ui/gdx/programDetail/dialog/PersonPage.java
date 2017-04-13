package tvfan.tv.ui.gdx.programDetail.dialog;

import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.luxtone.lib.widget.CullGroup;
import com.luxtone.lib.widget.Grid;

import org.json.JSONArray;
import org.json.JSONObject;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.BasePage;
import tvfan.tv.R;
import tvfan.tv.dal.HttpResponse.Listener4JSONObject;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.lib.Lg;
import tvfan.tv.lib.Utils;
import tvfan.tv.ui.gdx.programDetail.adapter.DetailRelatedRecommendAdapter;

public class PersonPage extends BasePage implements LoaderListener {

	private Image mBackImage;
	private Grid mGrid;
	private Label mName, introduce;
	private Image mPersonImgage, mCoverImage, personIcon;
	private PageImageLoader imageLoader, iconLoader;

	private RemoteData rd;

	private JSONArray programList;

	private CullGroup cullGroup;

	private final static String TAG = "PersonDialog";
	private String url = "";
	private String personId;
	private String personName;
	private String seriesId;
	private String introStr = "";

	private static PersonPage instance = null;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		instance = this;
		rd = new RemoteData(getActivity());

		seriesId = bundle.getString("seriesId", "1");
		personId = bundle.getString("personId", "");
		personName = bundle.getString("personName", "");

		try {
			if (bundle.containsKey("actionParam")
					&& !bundle.getString("actionParam").isEmpty()) {
				JSONObject obj = new JSONObject(bundle.getString("actionParam"));
				personId = obj.optString("personId");
				personName = obj.optString("name");
			}
			// 节目集id
			if (bundle.containsKey("id")) {
				seriesId = bundle.getString("id", "1");
			}
			if (seriesId.isEmpty())
				seriesId = "1";
		} catch (Exception e) {
			e.printStackTrace();
		}

		mPersonImgage = new Image(this);
		mPersonImgage.setSize(810, 1080);
		mPersonImgage.setDrawable(new TextureRegionDrawable(
				findTextureRegion(R.drawable.zly)));
		addActor(mPersonImgage);

		mBackImage = new Image(this);
		mBackImage.setSize(AppGlobalConsts.APP_WIDTH,
				AppGlobalConsts.APP_HEIGHT);
		mBackImage.setDrawable(new TextureRegionDrawable(
				findTextureRegion(R.drawable.new_foucs)));
		addActor(mBackImage);

		mCoverImage = new Image(this);
		mCoverImage.setPosition(130, 430);
		mCoverImage.setSize(220, 220);
		mCoverImage.setDrawable(new TextureRegionDrawable(
				findTextureRegion(R.drawable.person_bj)));
		addActor(mCoverImage);

		personIcon = new Image(this);
		personIcon.setPosition(140, 440);
		personIcon.setSize(200, 200);
		addActor(personIcon);

		// mForeImage = new Image(this);
		// mForeImage.setPosition(0, 0);
		// mForeImage.setSize(AppGlobalConsts.APP_WIDTH,
		// AppGlobalConsts.APP_HEIGHT);
		// mForeImage.setDrawable(new TextureRegionDrawable(
		// findTextureRegion(R.drawable.bj4)));
		// mForeImage.setAlpha(0.5f);
		// mForeImage.setVisible(false);
		// addActor(mForeImage);

		mName = new Label(this, false);
		mName.setPosition(130, 320);
		mName.setSize(200, 80);
		mName.setAlignment(Align.left);
		mName.setText(personName);
		mName.setTextSize(80);
		mName.setColor(Color.valueOf("f0f0f0"));
		// mName.setAlpha(0.6f);
		addActor(mName);

		introduce = new Label(this);
		introduce.setPosition(130, 100);
		introduce.setSize(600, 200);
		introduce.setAlignment(Align.center);
		introduce.setMaxLine(4);
		introduce.setText("");
		introduce.setTextSize(30);
		introduce.setColor(Color.valueOf("f0f0f0"));
		introduce.setSpacingadd(15);
		introduce.setAlpha(0.5f);
		addActor(introduce);

		cullGroup = new CullGroup(this);
		cullGroup.setPosition(810, 30);
		cullGroup.setSize(950, 880f);
		// cullGroup.setCullingArea(new Rectangle(-70, -50, 1400, 990));
		addActor(cullGroup);

		rd.getRelationListByPerson(seriesId, personId,
				new Listener4JSONObject() {

					@Override
					public void onResponse(JSONObject response) {
						if (isDisposed()) {
							return;
						}
						if (response == null) {
							Lg.e(TAG,
									"detail page PersonDialog  response is null");
							return;
						}
						try {
							if (response.has("image")) {
								loadPersonPhoto(response.optString("image"));
								LoadIcon(response.optString("image"));
							}
							if (response.has("introduce")) {
								introStr = response.optString("introduce");
							}
							if(response.has("name") && personName.isEmpty()){
								personName =  response.optString("name");
							}
							if (response.has("programList")) {
								programList = response
										.getJSONArray("programList");
								Gdx.app.postRunnable(new Runnable() {

									@Override
									public void run() {
										introduce.setText(introStr);
										addGrid(programList);
										mName.setText(personName);
									}

								});
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

					@Override
					public void onError(String errorMessage) {

					}
				});
	}

	public static PersonPage getInstance() {
		return instance;
	}

	private void loadPersonPhoto(String url) {
		this.url = url;
		if (imageLoader != null) {
			imageLoader.cancelLoad();
		}
		imageLoader = new PageImageLoader(PersonPage.this);
		imageLoader.startLoadBitmap(url, "list", false, 0, PersonPage.this,
				"image");
	}

	private void LoadIcon(String url) {
		this.url = url;
		if (iconLoader != null) {
			iconLoader.cancelLoad();
		}
		iconLoader = new PageImageLoader(PersonPage.this);
		// iconLoader.startLoadBitmap(url, "list", true, 30, PersonPage.this,
		// "icon");
		iconLoader.startLoadBitmapWithSize(url, "list", true, 30, 200, 200,
				PersonPage.this, "icon");
	}

	@Override
	protected void onResumeTextures() {
		super.onResumeTextures();
		Utils.resetImageSource(mBackImage, R.drawable.new_foucs);
		// Utils.resetImageSource(mCoverImage, R.drawable.bj3);
		Utils.resetImageSource(mPersonImgage, R.drawable.zly);
		loadPersonPhoto(url);
		LoadIcon(url);
	}

	private void addGrid(final JSONArray programList) {
		mGrid = new Grid(this);
		mGrid.setSize(950, 880f);
		mGrid.setCull(false);
		mGrid.setOrientation(Grid.ORIENTATION_VERTICAL);
		mGrid.setRowNum(3);
		mGrid.setGapLength(0);
		mGrid.setPosition(0, 100f);
		DetailRelatedRecommendAdapter adapter = new DetailRelatedRecommendAdapter(
				this);
		adapter.setData(programList);
		mGrid.setAdapter(adapter);
		mGrid.setItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(Actor itemView, int position,
					AbsListView grid) {
				try {
					Bundle bundle = new Bundle();
					JSONObject obj = (JSONObject) programList.get(position);
					bundle.putString("programSeriesId", obj.optString("id"));
					bundle.putString("name", obj.optString("name"));
					doAction(ACTION_NAME.OPEN_DETAIL, bundle);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		mGrid.setOnItemSelectedChangeListener(new OnItemSelectedChangeListener() {

			@Override
			public void onSelectedChanged(int position, Actor actor) {

			}
		});

		cullGroup.addActor(mGrid);
	}

	@Override
	public void onLoadComplete(String paramString,
			TextureRegion paramTextureRegion, Object paramObject) {
		if (paramObject.equals("image")) {
			mPersonImgage.setDrawable(paramTextureRegion);
		} else if (paramObject.equals("icon")) {
			personIcon.setDrawable(paramTextureRegion);
		}
		if (cullGroup != null) {
			cullGroup.toFront();
		}
	}
}
