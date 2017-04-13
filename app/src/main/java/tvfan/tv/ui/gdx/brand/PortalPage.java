package tvfan.tv.ui.gdx.brand;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.AppGlobalVars;
import tvfan.tv.BasePage;
import tvfan.tv.dal.HttpResponse;
import tvfan.tv.dal.LocalData;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.lib.Lg;
import tvfan.tv.lib.NetWorkUtils;
import tvfan.tv.lib.PortalLayoutParser.LayoutElement;
import tvfan.tv.ui.gdx.widgets.CIBNLoadingView;
import tvfan.tv.ui.gdx.widgets.HorizontalIconList;
import tvfan.tv.ui.gdx.widgets.HorizontalIconListItem;
import tvfan.tv.ui.gdx.widgets.HorizontalIconList.IconBuilder;
import android.os.Bundle;

import tvfan.tv.R;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.luxtone.lib.gdx.OnClickListener;
import com.luxtone.lib.gdx.OnFocusChangeListener;

public class PortalPage extends BasePage implements OnClickListener,
		OnFocusChangeListener {

	private static final String TAG = "brand.PortalPage";
	private final int _marginLeft = 50;
	private Label title;
	private CIBNLoadingView loadingview;
	private String id;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		_loadBG();
		initLoading();
		id = bundle.getString("id", "2");
		_loadData(id);

	}

	@Override
	public void onFocusChanged(Actor actor, boolean hasFocus) {

	}

	@Override
	public void onClick(Actor actor) {

	}

	private void initLoading() {
		loadingview = new CIBNLoadingView(this);
		loadingview.setVisible(true);
		addActor(loadingview);
		loadingview.toFront();
	}

	private void _loadBG() {

		Image back = new Image(this);
		back.setSize(AppGlobalConsts.APP_WIDTH, AppGlobalConsts.APP_HEIGHT);
		back.setPosition(0, 0);

		LocalData ld = new LocalData(getActivity());
		String bg = ld.getKV(AppGlobalConsts.PERSIST_NAMES.BACKGROUND_IMAGE
				.name());

		int _bgresid = -1;
		if (bg == null)
			_bgresid = R.drawable.bjc;
		else
			_bgresid = Integer.parseInt(bg);
		back.setDrawableResource(_bgresid);
		addActor(back);

		title = new Label(this);
		title.setSize(280, 50);
		title.setPosition(150, AppGlobalConsts.APP_HEIGHT - 150);
		title.setText("专区");
		title.setColor(Color.WHITE);
		title.setTextSize(65);
		title.setAlpha(0.8f);
		addActor(title);

	}

	private void _createHIList(String layoutFile, JSONArray items,
			final String i) {
		final HorizontalIconList hil = new HorizontalIconList(this, layoutFile,
				items, new IconBuilder() {
					@Override
					public void Create(final LayoutElement layoutElement,
							final JSONObject dataListItem,
							final int layoutElementIndex,
							final HorizontalIconList horizontalIconList) {
						Gdx.app.postRunnable(new Runnable() {
							@Override
							public void run() {
								_createIcon(layoutElement, dataListItem,
										layoutElementIndex, horizontalIconList,
										i);
							}
						});
					}
				});
		hil.setPosition(_marginLeft, (AppGlobalConsts.APP_HEIGHT - 620) / 2);
		hil.setSize((AppGlobalConsts.APP_WIDTH - _marginLeft), 620);
		addActor(hil);
	}

	private void _createIcon(LayoutElement layoutElement,
			JSONObject dataListItem, int layoutElementIndex,
			HorizontalIconList horizontalIconList, String i) {
		HorizontalIconListItem item = new HorizontalIconListItem(this,
				new int[] { layoutElement.width, layoutElement.height },
				new HorizontalIconListItem.IconListItemListener() {
					@Override
					public void focusChanged(HorizontalIconListItem selectedItem) {
						// _setSelectedFocus(selectedItem);
					}
				});
		item.setPosition(layoutElement.x, layoutElement.y);
		item.setFocusAble(true);
		item.setFocusScale(.1f);
		try {
			item.putParam("layoutElementIndex",
					String.valueOf(layoutElementIndex));
			item.putParam("id", dataListItem.getString("id").toString());
			item.putParam("action", dataListItem.getString("action").toString());
			item.putParam("subName", dataListItem.getString("subName")
					.toString());
			item.putParam(
					"actionParam",
					dataListItem
							.getJSONObject("actionParam")
							.put("name",
									dataListItem.getString("name").toString())
							.put("image",
									dataListItem.getString("image").toString())
							.toString());

			item.putParam("LMindex", i);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		item.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(Actor arg0) {
				HorizontalIconListItem item = (HorizontalIconListItem) arg0;
				Bundle options = new Bundle();
				options.putString("id", item.getParam("id"));
				options.putString("action", item.getParam("action"));
				options.putString("actionParam", item.getParam("actionParam"));

				doAction(
						ACTION_NAME.valueOf(ACTION_NAME.class,
								item.getParam("action")), options);
			}

		});
		try {
			String title = dataListItem.getString("subName");
			if (title.isEmpty()) {
				title = dataListItem.getString("name");
			}
			if (layoutElementIndex == 0
					&& dataListItem.getString("action").equals(
							ACTION_NAME.OPEN_USER_CENTER.name())) {
				item.addIcon(AppGlobalVars.getIns().USER_PIC);
				if (!AppGlobalVars.getIns().USER_PIC.startsWith("@")) {
					item.showMask(true);
					item.showHeadimg(AppGlobalVars.getIns().USER_PIC, true);
				}
				item.keepShowTitle();
				title = AppGlobalVars.getIns().USER_NICK_NAME;
			} else {
				item.addIcon(dataListItem.getString("image"));
			}
			item.setTitle(title);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		item.setTag(layoutElementIndex);
		horizontalIconList.addActor(item);
		if (layoutElementIndex == 0) {
			firstItem = item;
		}
		if (layoutElementIndex > 4) {
			firstItem.toFront();
		}
	}

	private HorizontalIconListItem firstItem = null;

	private void _loadData(final String mid) {

		RemoteData rd = new RemoteData(getActivity());
		rd.getPortalItem(mid, new HttpResponse.Listener4JSONObject() {
			@Override
			public void onResponse(final JSONObject response) {
				try {
					JSONArray items = response.getJSONArray("itemList");
					if (items != null) {
						_createHIList(response.getString("layoutFile"), items,
								mid);
					}
					loadingview.setVisible(false);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(String errorMessage) {
				Lg.e(TAG, "_loadData: " + errorMessage);
				NetWorkUtils.handlerError(errorMessage, PortalPage.this);
			}
		});
	}

}
