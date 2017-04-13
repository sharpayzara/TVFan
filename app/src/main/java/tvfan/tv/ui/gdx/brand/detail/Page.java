package tvfan.tv.ui.gdx.brand.detail;

import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.luxtone.lib.gdx.OnClickListener;
import com.luxtone.lib.image.LoaderListener;
import com.luxtone.lib.image.PageImageLoader;
import com.luxtone.lib.utils.Utils;
import com.luxtone.lib.widget.AbsListView;
import com.luxtone.lib.widget.AbsListView.OnHasFocusChangedListener;
import com.luxtone.lib.widget.AbsListView.OnItemClickListener;
import com.luxtone.lib.widget.AbsListView.OnItemSelectedChangeListener;
import com.luxtone.lib.widget.CullGroup;
import com.luxtone.lib.widget.Grid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.BasePage;
import tvfan.tv.R;
import tvfan.tv.dal.HttpResponse.Listener4JSONObject;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.lib.GdxPageImageBatchLoader;
import tvfan.tv.lib.Lg;
import tvfan.tv.lib.NetWorkUtils;
import tvfan.tv.ui.gdx.brand.detail.BrandMenuItemAdapter.BrandMenuItem;
import tvfan.tv.ui.gdx.widgets.CIBNLoadingView;
import tvfan.tv.ui.gdx.widgets.TVFANLabel;

/**
 * 品牌专区界面
 * 
 * @author cibn
 * 
 */
public class Page extends BasePage implements LoaderListener {

	Image logoImg, bgImg, titleImg, titleBg, recommendLine, img, backShadow;
	PageImageLoader imageLoader;
	TVFANLabel titleLabel, infoLabel, recommendLabel;

	SearchItem searchBtn;

	Grid menuList, recommendList;

	private ArrayList<String> data = new ArrayList<String>();
	private ArrayList<JSONObject> recommends = new ArrayList<JSONObject>();

	private ArrayList<JSONObject> brandList = new ArrayList<JSONObject>();
	private BrandMenuItemAdapter menuAdapter;
	private BrandRecommendAdapter recommendAdapter;
	private GdxPageImageBatchLoader _pib;

	private Group group;
	private Group listGroup;
	private Grid listGrid;
	CullGroup cullGrid;
	BrandRecommendAdapter bra;

	private RemoteData rd;
	private static final String TAG = "BrandPage";
	private JSONObject brandDetail;
	private JSONArray programList;

	private String parentCatgId;

	private Timer timer = new Timer();
	private Task timerTask;
	
	private TVFANLabel errorLable;
	
	CIBNLoadingView loadingview;

	// private Actor focusActor;
	// private boolean isScrolling = false;
	// private float startX,startY;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		_pib = new GdxPageImageBatchLoader(this);
		data.add("品牌介绍");
		menuAdapter = new BrandMenuItemAdapter(this);
		bra = new BrandRecommendAdapter(this);

		listGroup = new Group(Page.this);
		listGroup.setSize(1230, AppGlobalConsts.APP_HEIGHT);
		listGroup.setPosition(500, 0);
		addActor(listGroup);
		img = new Image(this);
		img.setDrawableResource(R.drawable.new_foucs);
		img.setPosition(550, 0);
		img.setSize(442, 542);
		img.setVisible(false);
		addActor(img);

		parentCatgId = bundle.getString("parentCatgId","");
		if(bundle.containsKey("id")){
			parentCatgId = bundle.getString("id","");
		}
		
		loadingview  = new CIBNLoadingView(this);
		loadingview.setVisible(true);
		addActor(loadingview);
		loadingview.toFront();
		
		rd = new RemoteData(getActivity());
		rd.getBrandDetail(parentCatgId, new Listener4JSONObject() {

			@Override
			public void onResponse(JSONObject response) {
				if (isDisposed()) {
					return;
				}
				if (response == null) {
					Lg.e(TAG,
							"brand detail page get brand detail response is null");
					return;
				}
				try {
					brandDetail = response;
					if (brandDetail.length() <= 0) {
						Utils.showToast("数据为空!");
						Page.this.finish();
						return;
					}
					if(!brandDetail.has("brandInfo")){
						Utils.showToast("数据异常!");
						Page.this.finish();
						return;
					}
					Gdx.app.postRunnable(new Runnable() {

						@Override
						public void run() {
							initBackGround();
							initLogo();
							initTitle();
							initInfo();
							initmenuList();
							initRecommendList();
							loadingview.setVisible(false);
						}
					});

				} catch (Exception e) {
					Lg.e(TAG, e.getMessage());
					e.printStackTrace();
				}
			}

			@Override
			public void onError(String errorMessage) {
				Lg.e(TAG, "getBrandDetail : " + errorMessage);
				NetWorkUtils.handlerError(errorMessage, Page.this);
			}
		});

	}

	private void initRecommendList() {
		recommendLabel = new TVFANLabel(this);
		recommendLabel.setPosition(50, 280);
		recommendLabel.setSize(100, 80);
		recommendLabel.setAlignment(Align.left);
		recommendLabel.setMaxLine(0);
		recommendLabel.setTextSize(50);
		recommendLabel.setAlpha(0.6f);
		recommendLabel.setFocusAble(false);
		recommendLabel.setMarquee(false);
		recommendLabel.setText("为您推荐");
		group.addActor(recommendLabel);

		cullGrid = new CullGroup(this);
		cullGrid.setSize(1300, 590);
		cullGrid.setPosition(50, -160);
		cullGrid.setCullingArea(new Rectangle(-100, -100, 1300 + 100, 690 + 20));
		group.addActor(cullGrid);

		recommendList = new Grid(this);
		recommendList.setPosition(0, 0);
		recommendList.setSize(1230, 440);
		recommendList.setGapLength(10);
		recommendList.setCull(false);
		recommendList.setOrientation(Grid.ORIENTATION_LANDSPACE);
		recommendList.setRowNum(1);

		try {
			for (int i = 0; i < brandDetail.optJSONObject("recommends")
					.optJSONArray("recommendList").length(); i++) {

				JSONObject obj = (JSONObject) brandDetail
						.optJSONObject("recommends")
						.optJSONArray("recommendList").get(i);
				recommends.add(obj);
			}
			recommendAdapter = new BrandRecommendAdapter(this);
			recommendAdapter.setData(recommends);
			recommendList.setAdapter(recommendAdapter);
			recommendList
					.setOnHasFocusChangedListener(onHasFocusChangedListener);
			recommendList.setItemClickListener(clickListener);
			cullGrid.addActor(recommendList);
		} catch (Exception e) {
			e.printStackTrace();
		}

		recommendLine = new Image(this);
		recommendLine.setPosition(260, 320);
		recommendLine.setSize(1010, 1);
		recommendLine.setDrawableResource(R.drawable.ppzq_line);
		recommendLine.setFocusAble(false);
		group.addActor(recommendLine);

	}

	OnItemClickListener clickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(Actor actor, int position, AbsListView arg2) {
			try {
				JSONObject obj = (JSONObject) brandDetail
						.optJSONObject("recommends")
						.optJSONArray("recommendList").get(position);
				Bundle bundle = new Bundle();
				bundle.putString("programSeriesId", obj.optString("id"));
				bundle.putString("name", obj.optString("name"));
				
				doAction(ACTION_NAME.OPEN_DETAIL, bundle);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	// ScrollStatusListener mScrollStatusListener = new ScrollStatusListener() {
	//
	// @Override
	// public void onScrolling(float paramFloat1, float paramFloat2) {
	//
	// }
	//
	// @Override
	// public void onScrollStop(float paramFloat1, float paramFloat2) {
	// if (focusActor != null) {
	// img.setVisible(true);
	// img.toFront();
	// img.addAction(Actions.moveTo(focusActor.getRealityX() - 71,
	// focusActor.getRealityY() - 50, 0.1f));
	// startX = focusActor.getRealityX();
	// startY = focusActor.getRealityY();
	// focusActor = null;
	// isScrolling = false;
	// }
	// }
	//
	// @Override
	// public void onScrollStart(float paramFloat1, float paramFloat2) {
	// isScrolling = true;
	// }
	// };

	// OnItemSelectedChangeListener onselectedChangeListener = new
	// OnItemSelectedChangeListener() {
	//
	// @Override
	// public void onSelectedChanged(int paramInt, Actor actor) {
	// focusActor = actor;
	// if (!isScrolling) {
	// img.setVisible(true);
	// img.toFront();
	// img.addAction(Actions.moveTo(actor.getRealityX() - 71,
	// actor.getRealityY() - 50, 0.3f));
	// startX = actor.getRealityX();
	// startY = actor.getRealityY();
	// }else{
	// img.setVisible(true);
	// img.toFront();
	// img.addAction(Actions.moveTo(startX+150 - 71,
	// startY - 50, 0.2f));
	// }
	// }
	// };

	OnHasFocusChangedListener onHasFocusChangedListener = new OnHasFocusChangedListener() {

		@Override
		public boolean onHasFocusedChanged(boolean hasFocus) {
			if (hasFocus) {
				group.addAction(Actions.moveTo(group.getX(),
						group.getY() + 200, 0.3f));
				int z = titleImg.getZIndex();
				group.setZIndex(z - 1);
			} else {
				group.addAction(Actions.moveTo(group.getX(),
						group.getY() - 200, 0.3f));
				menuList.setSelection(0, true);
			}

			return false;
		}
	};

	private void initInfo() {

		group = new Group(this);
		group.setPosition(500, 0);
		group.setSize(1230, 600);
		addActor(group);

		infoLabel = new TVFANLabel(this);
		infoLabel.setPosition(50, 400);
		infoLabel.setSize(1230, 150);
		infoLabel.setFocusAble(false);
		infoLabel.setMaxLine(2);
		infoLabel.setAlpha(0.6f);
		infoLabel.setTextSize(40);
		infoLabel.setSpacingadd(10f);
		infoLabel.setMarquee(false);
		infoLabel.setText(brandDetail.optJSONObject("brandInfo")
				.optJSONObject("brandIntro").optJSONObject("introduction")
				.optString("introContent"));

		group.addActor(infoLabel);

	}

	private void initmenuList() {
		menuList = new Grid(this);
		menuList.setCull(false);
		menuList.setPosition(120, 26);
		menuList.setSize(380, 448);
		menuList.setGapLength(0);
		menuList.setOrientation(Grid.ORIENTATION_VERTICAL);
		menuList.setRowNum(1);
		if (brandDetail.has("menuList")) {
			for (int i = 0; i < brandDetail.optJSONArray("menuList").length(); i++) {
				JSONObject o;
				try {
					o = (JSONObject) brandDetail.optJSONArray("menuList")
							.get(i);
					data.add(o.optString("name"));
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			menuAdapter.setData(data);
			menuList.setAdapter(menuAdapter);
			// menuList.setItemClickListener(itemClickListener);
			menuList.setOnItemSelectedChangeListener(onItemSelectedChangeListener);
			addActor(menuList);
		}

		searchBtn = new SearchItem(this);
		searchBtn.setPosition(130, 464);
		searchBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(Actor paramActor) {
				doAction(ACTION_NAME.OPEN_SEARCH, new Bundle());
			}
		});
		addActor(searchBtn);

	}

	OnItemSelectedChangeListener onItemSelectedChangeListener = new OnItemSelectedChangeListener() {

		@Override
		public void onSelectedChanged(final int position, Actor actor) {

			final int p = position;
			if (timerTask != null) {
				timerTask.cancel();
				timerTask = null;
			}
			timerTask = new Task() {

				@Override
				public void run() {
					if (p == 0) {
						if (listGrid != null) {
							// listGroup.removeActor(listGrid);
							// listGrid.clear();
							listGroup.setVisible(false);
							listGroup.toBack();
							// listGroup.clearChildren();
						}
						setInfoVisible(true);

					} else {
						setInfoVisible(false);
						if (listGrid != null) {
							// listGroup.removeActor(listGrid);
							// listGrid.clear();
							// listGroup.clearChildren();
						}
						try {
							JSONObject obj = (JSONObject) brandDetail
									.optJSONArray("menuList").get(position - 1);

							rd.getSingleBrandList(obj.optString("id"),
									new Listener4JSONObject() {

										@Override
										public void onResponse(
												JSONObject response) {
											if (isDisposed()) {
												return;
											}
											if (response == null) {
												return;
											}
											getSingleBrandListResponse(response);
										}

										@Override
										public void onError(String errorMessage) {

										}
									});
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				}
			};
			timer.scheduleTask(timerTask, 0.5f);
			timer.start();
		}
	};

	private void getSingleBrandListResponse(JSONObject response) {
		if (response == null) {
			Lg.e(TAG,
					"brand detail page getSingleBrandListResponse response is null");
			return;
		}
		try {
			JSONArray ja = null;
			if (response.has("programList")) {
				ja = response.getJSONArray("programList");
			}
			final JSONArray array = ja;

			Gdx.app.postRunnable(new Runnable() {

				@Override
				public void run() {
					addSingleBrandList(array);
				}
			});

		} catch (Exception e) {
			Lg.e(TAG, e.getMessage());
			e.printStackTrace();
		}
	}

	private void addSingleBrandList(JSONArray programList) {
		brandList.clear();
		this.programList = programList;
		if (programList != null) {
			for (int i = 0; i < programList.length(); i++) {
				JSONObject obj;
				try {
					obj = (JSONObject) programList.get(i);
					brandList.add(obj);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (listGrid == null) {
			listGrid = new Grid(Page.this);
			listGrid.setSize(1250, 900);
			listGrid.setGapLength(20);
			listGrid.setCull(false);
			listGrid.setPosition(20, 100);
			listGrid.setOrientation(Grid.ORIENTATION_VERTICAL);
			listGrid.setRowNum(4);
			bra.setData(brandList);
			if (bra.getCount() > 0) {
				listGrid.setAdapter(bra);
			}
			listGroup.addActor(listGrid);
			listGrid.setItemClickListener(listOnclicklistener);
			
			errorLable = new TVFANLabel(this);
			errorLable.setPosition(600, 600);
			errorLable.setSize(1250, 900);
			errorLable.setTextSize(40);
			errorLable.setVisible(false);
			errorLable.setFocusAble(false);
			errorLable.setText("暂无数据!");
			listGroup.addActor(errorLable);
		} else {
			try {
				listGrid.notifyDataChanged();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		if(brandList.size()<=0){
			errorLable.setVisible(true);
			listGrid.setVisible(false);
		}else{
			errorLable.setVisible(false);
			listGrid.setVisible(true);
		}
		listGroup.toFront();
		listGroup.setVisible(true);
	}

	OnItemClickListener listOnclicklistener = new OnItemClickListener() {

		@Override
		public void onItemClick(Actor actor, int position, AbsListView arg2) {
			try {
				JSONObject obj = (JSONObject) programList.get(position);
				Bundle bundle = new Bundle();
				bundle.putString("programSeriesId", obj.optString("id"));
				bundle.putString("name", obj.optString("name"));
				doAction(ACTION_NAME.OPEN_DETAIL, bundle);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private void setInfoVisible(boolean visible) {
		titleImg.setVisible(visible);
		titleBg.setVisible(visible);
		titleLabel.setVisible(visible);
		group.setVisible(visible);
	}

	private void initBackGround() {
		bgImg = new Image(this);
		bgImg.setPosition(-50, -50);
		bgImg.setSize(AppGlobalConsts.APP_WIDTH + 100,
				AppGlobalConsts.APP_HEIGHT + 100);
		bgImg.setDrawableResource(R.drawable.default_background);
		bgImg.setVisible(true);
		bgImg.setAlpha(0.95f);
		addActor(bgImg);

		backShadow = new Image(this);
		backShadow.setPosition(-50, -50);
		backShadow.setSize(AppGlobalConsts.APP_WIDTH + 100,
				AppGlobalConsts.APP_HEIGHT + 100);
		backShadow.setDrawable(findTextureRegion(R.drawable.background_shadow));
		backShadow.setFocusAble(false);
		backShadow.setAlpha(0.8f);
		addActor(backShadow);

		loadBackGround();

	}

	private void loadBackGround() {
		if (imageLoader != null) {
			imageLoader.cancelLoad();
		}
		imageLoader = new PageImageLoader(this);
		// imageLoader.startLoadBitmap(bgUrl, "load", true,1this, "bgImg");

		String url = "";
		try {
			url = brandDetail.optJSONObject("brandInfo")
					.optJSONObject("brandIntro").optJSONObject("introduction")
					.optString("introPic");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!url.isEmpty())
			imageLoader.startLoadBitmapByFilter(url, "load", false, 0, this,
					"bgImg");
	}

	private void initLogo() {
		logoImg = new Image(this);
		logoImg.setPosition(120, 600);
		logoImg.setSize(360, 380);
		addActor(logoImg);
		try {
			_pib.Get(brandDetail.optJSONObject("brandInfo").optString("logo"),
					logoImg, AppGlobalConsts.CUTLENGTH);
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}

	private void initTitle() {

		titleImg = new Image(this);
		titleImg.setPosition(550, 600);
		titleImg.setSize(1230, 380);
		addActor(titleImg);

		titleBg = new Image(this);
		titleBg.setPosition(550, 600);
		titleBg.setSize(1230, 300);
		titleBg.setDrawableResource(R.drawable.ppzq_bannertitlebj);
		addActor(titleBg);

		titleLabel = new TVFANLabel(this);
		titleLabel.setPosition(550, 625);
		titleLabel.setAlignment(Align.center);
		titleLabel.setSize(1230, 30);
		titleLabel.setTextSize(40);
		try {
			titleLabel.setText(brandDetail.optJSONObject("brandInfo").optString(
					"solgan"));
			_pib.Get(
					brandDetail.optJSONObject("brandInfo")
							.optJSONObject("brandIntro")
							.optJSONObject("introduction").optString("introPic"),
					titleImg, AppGlobalConsts.CUTLENGTH);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		addActor(titleLabel);
	}

	@Override
	public void onLoadComplete(String paramString,
			TextureRegion paramTextureRegion, Object paramObject) {
		bgImg.setDrawable(paramTextureRegion);
	}

	@Override
	public boolean onKeyDown(int keycode) {
		if (group != null && group.getFocusContainer() != null) {
			Actor actor = group.getFocusContainer().getFocusActor();
			if (keycode == Keys.UP) {
				if (actor instanceof BrandMenuItem) {
					BrandMenuItem bmi = (BrandMenuItem) actor;
					if (bmi.getText().equals("品牌介绍")) {
						searchBtn.requestFocus();
						return true;
					}
				}
			}

			if (keycode == Keys.DOWN) {
				if (actor.equals(searchBtn)) {
					menuList.setSelection(0, true);
					return true;
				}
			}
		}

		return super.onKeyDown(keycode);
	}

	@Override
	protected void onResumeTextures() {
		super.onResumeTextures();
		try {
		
		tvfan.tv.lib.Utils.resetImageSource(bgImg,
				R.drawable.default_background);
		loadBackGround();
		tvfan.tv.lib.Utils.resetImageSource(img, R.drawable.new_foucs);
		tvfan.tv.lib.Utils.resetImageSource(backShadow,
				R.drawable.background_shadow);
		tvfan.tv.lib.Utils.resetImageSource(recommendLine,
				R.drawable.ppzq_line);
		tvfan.tv.lib.Utils.resetImageSource(titleBg,
				R.drawable.ppzq_bannertitlebj);

		_pib.Get(brandDetail.optJSONObject("brandInfo").optString("logo"),
				logoImg, AppGlobalConsts.CUTLENGTH);
		_pib.Get(
				brandDetail.optJSONObject("brandInfo")
						.optJSONObject("brandIntro")
						.optJSONObject("introduction").optString("introPic"),
				titleImg, AppGlobalConsts.CUTLENGTH);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
