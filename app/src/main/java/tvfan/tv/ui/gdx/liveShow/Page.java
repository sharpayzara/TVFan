package tvfan.tv.ui.gdx.liveShow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.AppGlobalVars;
import tvfan.tv.BasePage;
import tvfan.tv.R;
import tvfan.tv.dal.HttpResponse;
import tvfan.tv.dal.LocalData;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.dal.models.LiveShowPageItem;
import tvfan.tv.dal.models.LiveShowPageRankItem;
import tvfan.tv.lib.GdxPageImageBatchLoader;
import tvfan.tv.lib.Lg;
import tvfan.tv.lib.NetWorkUtils;
import tvfan.tv.lib.PortalLayoutParser.LayoutElement;
import tvfan.tv.ui.gdx.widgets.HorizontalIconList;
import tvfan.tv.ui.gdx.widgets.HorizontalIconList.IconBuilder;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.luxtone.lib.gdx.OnClickListener;
import com.luxtone.lib.gdx.OnFocusChangeListener;
import com.luxtone.lib.widget.v4.ScrollView;

public class Page extends BasePage implements OnClickListener,
		OnFocusChangeListener {

	private static final String TAG = "liveShow";

	private HorizontalIconList _hil1;
	private ScrollView _scrollView;
	private JSONArray jsonarr = null;
	GdxPageImageBatchLoader _pib;
	private Label title;
	private String layoutJson;
	private Label _time;
	private String parentID;

	private ArrayList<LiveShowPageItem> liveShowPageList = new ArrayList<LiveShowPageItem>();
	private JSONArray lsJsonarr = new JSONArray();

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		_pib = new GdxPageImageBatchLoader(this);
		_loadBG();
		_loadData();

		IntTime();

	}

	@Override
	public void onFocusChanged(Actor arg0, boolean arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(Actor arg0) {
		// TODO Auto-generated method stub

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
//			_bgresid = R.drawable.bja;
			_bgresid = R.drawable.home_background;
		else
			_bgresid = Integer.parseInt(bg);
		back.setDrawableResource(_bgresid);
		addActor(back);

		title = new Label(this);
		title.setSize(280, 50);
		title.setPosition(150, AppGlobalConsts.APP_HEIGHT - 150);
		title.setText("Live Show");
		title.setColor(Color.valueOf("D7D7D7"));
		title.setTextSize(65);
		title.setAlpha(70);
		addActor(title);

		_time = new Label(this);
		_time.setPosition(1600, AppGlobalConsts.APP_HEIGHT - 150);
		_time.setSize(100, 35);
		_time.setTextSize(50);
		_time.setColor(Color.valueOf("D7D7D7"));
		_time.setAlpha(70);
		_time.setText(":");
		addActor(_time);
	}

	private void _loadData() {
		RemoteData rd = new RemoteData(getActivity());
		rd.getLiveShowDB(new HttpResponse.Listener4JSONObject() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					layoutJson = response.getString("layoutFile");
					parentID = response.getString("id");
					jsonarr = response.getJSONArray("ItemList");
					for (int i = 0; i < jsonarr.length(); i++) {
						LiveShowPageItem liveShowPageItem = new LiveShowPageItem();

						JSONObject jsonobject = (JSONObject) jsonarr.get(i);
						liveShowPageItem.setId(jsonobject.getString("id"));
						liveShowPageItem.setName(jsonobject.getString("name"));
						liveShowPageItem.setPlayTime(jsonobject
								.getString("playTime"));
						liveShowPageItem.setEndTime(jsonobject
								.getString("endTime"));
						liveShowPageItem.setImgUrl(jsonobject
								.getString("imgUrl"));
						liveShowPageItem.setAction(jsonobject
								.getString("action"));
						liveShowPageItem
								.setPlace(jsonobject.getString("place"));
						liveShowPageItem
								.setPrice(jsonobject.getString("price"));
						liveShowPageItem.setCornerImg(jsonobject
								.getString("cornerImg"));
						liveShowPageItem.setCornerName(jsonobject
								.getString("cornerName"));
						liveShowPageItem.setBigPic(jsonobject
								.getString("bigPic"));
						liveShowPageItem.setLiveUrl(jsonobject
								.getString("liveUrl"));

						ArrayList<LiveShowPageRankItem> al = new ArrayList<LiveShowPageRankItem>();

						try {
							JSONArray rankJarry = jsonobject
									.getJSONArray("rank");
							for (int j = 0; j < rankJarry.length(); j++) {
								JSONObject jsonRankobject = (JSONObject) rankJarry
										.get(j);
								LiveShowPageRankItem lspri = new LiveShowPageRankItem();
								lspri.setProgramName(jsonRankobject
										.getString("programName"));
								al.add(lspri);
							}
							liveShowPageItem.setRank(al);
						} catch (Exception e) {
							// TODO: handle exception
						}

						lsJsonarr.put(i + 2, jsonobject);
						liveShowPageList.add(liveShowPageItem);

					}

					// 直播
					RemoteData rds = new RemoteData(getActivity());
					rds.startJsonHttpGet(AppGlobalVars.getIns().SERVER_URL
							.get("LIVESHOW_ZB"),
							new HttpResponse.Listener4JSONObject() {
								@Override
								public void onResponse(JSONObject response) {
									try {
										// layoutJson =
										// response.getString("layoutFile");
										// parentID = response.getString("id");
										jsonarr = response
												.getJSONArray("ItemList");
										for (int i = 0; i < 2; i++) { // 只取2个
											LiveShowPageItem liveShowPageItem = new LiveShowPageItem();

											JSONObject jsonobject = (JSONObject) jsonarr
													.get(i);
											liveShowPageItem.setId(jsonobject
													.getString("id"));
											liveShowPageItem.setName(jsonobject
													.getString("name"));
											liveShowPageItem.setPlayTime(jsonobject
													.getString("playTime"));
											liveShowPageItem.setEndTime(jsonobject
													.getString("endTime"));
											liveShowPageItem.setImgUrl(jsonobject
													.getString("imgUrl"));
											liveShowPageItem.setAction(jsonobject
													.getString("action"));
											liveShowPageItem.setPlace(jsonobject
													.getString("place"));
											liveShowPageItem.setPrice(jsonobject
													.getString("price"));
											liveShowPageItem.setCornerImg(jsonobject
													.getString("cornerImg"));
											liveShowPageItem.setCornerName(jsonobject
													.getString("cornerName"));
											liveShowPageItem.setBigPic(jsonobject
													.getString("bigPic"));
											liveShowPageItem.setLiveUrl(jsonobject
													.getString("liveUrl"));

											ArrayList<LiveShowPageRankItem> al = new ArrayList<LiveShowPageRankItem>();
											try {
												JSONArray rankJarry = jsonobject
														.getJSONArray("rank");
												for (int j = 0; j < rankJarry
														.length(); j++) {
													JSONObject jsonRankobject = (JSONObject) rankJarry
															.get(j);
													LiveShowPageRankItem lspri = new LiveShowPageRankItem();
													lspri.setProgramName(jsonRankobject
															.getString("programName"));
													al.add(lspri);
												}
												liveShowPageItem.setRank(al);
											} catch (Exception e) {
												// TODO: handle exception
											}

											lsJsonarr.put(i, jsonobject);

											liveShowPageList.add(i,
													liveShowPageItem);
										}

									} catch (JSONException e) {
										e.printStackTrace();
									}
									if (liveShowPageList != null)
										// _loadLayerout();
										_createHIList(liveShowPageList);
								}

								@Override
								public void onError(String errorMessage) {
									NetWorkUtils.handlerError(errorMessage, Page.this);
								}
							});

				} catch (JSONException e) {
					Lg.i(TAG, "_loadData_Error:" + e.toString());
					e.printStackTrace();
				}

			}

			@Override
			public void onError(String errorMessage) {
				Lg.e(TAG, "getLiveShowDB : " + errorMessage);
				NetWorkUtils.handlerError(errorMessage, Page.this);
			}
		}, AppGlobalVars.getIns().SERVER_URL.get("LIVESHOW_DB"), AppGlobalVars
				.getIns().TEMPLATE_ID);
	}

	private void _createHIList(ArrayList<LiveShowPageItem> lspl) {
		try {
			_hil1 = new HorizontalIconList(this, layoutJson, lsJsonarr,
					new IconBuilder() {
						@Override
						public void Create(final LayoutElement layoutElement,
								final JSONObject dataListItem,
								final int layoutElementIndex,
								final HorizontalIconList horizontalIconList) {
							Gdx.app.postRunnable(new Runnable() {
								@Override
								public void run() {
									_createIcon(layoutElement, dataListItem,
											layoutElementIndex,
											horizontalIconList);

									horizontalIconList.findActor(
											"RecommendGroup0").toFront();
								}
							});
						}
					});
			_hil1.setPosition(150, AppGlobalConsts.APP_HEIGHT - 266 - 600);
			_hil1.setSize((AppGlobalConsts.APP_WIDTH - 150), 600);
			addActor(_hil1);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	LiveShowPageItem lspi = null;

	private void _createIcon(LayoutElement layoutElement,
			JSONObject dataListItem, int layoutElementIndex,
			HorizontalIconList horizontalIconList) {

		try {
			lspi = liveShowPageList.get(layoutElementIndex);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		RecommendGroup rg = new RecommendGroup(this, layoutElement.width,
				layoutElement.height, lspi);
		rg.setPosition(layoutElement.x, layoutElement.y);

		try {
			rg.update(dataListItem.getString("imgUrl"), "");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		if (layoutElementIndex == 0) {
			rg.setName("RecommendGroup0");
		}
		horizontalIconList.addActor(rg);
		try {
			if (lspi.getAction().equals("OPEN_LIVE_SHOW_LIST")) {
				rg.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(Actor arg0) {
						// Utils.showToast("liveShow列表");
						Bundle options = new Bundle();
						options.putString("parentCatgId", parentID);
						options.putString("childCatgId",
								((RecommendGroup) arg0).getParentCatgId());
						doAction(ACTION_NAME.OPEN_LIVE_SHOW_LIST, options);
					}
				});

			} else if (lspi.getAction().equals("OPEN_LIVE_SHOW_DETAIL")) {
				rg.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(Actor arg0) {
						// Utils.showToast("liveShow详情");
						Bundle options = new Bundle();
						options.putString("parentCatgId",
								((RecommendGroup) arg0).getParentCatgId());
						doAction(ACTION_NAME.OPEN_LIVE_SHOW_DETAIL, options);
					}
				});
			} else if (lspi.getAction().equals("OPEN_LIVE_SHOW_PAY")) {
				rg.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(Actor arg0) {
						// Utils.showToast("购买页面");
						Bundle options = new Bundle();
						options.putString("parentCatgId",
								((RecommendGroup) arg0).getParentCatgId());
						options.putString("playTime",
								((RecommendGroup) arg0).getPlayTime());
						options.putString("liveUrl",
								((RecommendGroup) arg0).getLiveUrl());
						options.putString("bigPic",
								((RecommendGroup) arg0).getBigPic());
						options.putString("price",
								((RecommendGroup) arg0).getpRice());
						doAction(ACTION_NAME.OPEN_LIVE_SHOW_PAY, options);
					}
				});
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void IntTime() {

		registerLocalMsgReceiver(new LocalMsgListener() {
			@Override
			public void onReceive(Context context, Intent intent) {
				setDatetime(intent.getLongExtra("now", 0));
			}
		}, AppGlobalConsts.LOCAL_MSG_FILTER.DATETIME_UPDATE);
	}

	public void setDatetime(final long now) {
		Gdx.app.postRunnable(new Runnable() {
			@SuppressLint("SimpleDateFormat")
			@Override
			public void run() {
				Date d = new Date(now);
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
				_time.setText(sdf.format(d));
			}
		});
	}

}
