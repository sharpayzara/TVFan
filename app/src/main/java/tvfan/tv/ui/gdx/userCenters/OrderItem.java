package tvfan.tv.ui.gdx.userCenters;

/**
 * 個人中心-訂購界面
 * 
 * @author 孫文龍
 * 
 */

import android.content.Context;
import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.AbsListView;
import com.luxtone.lib.widget.AbsListView.OnItemClickListener;
import com.luxtone.lib.widget.AbsListView.OnItemSelectedChangeListener;
import com.luxtone.lib.widget.ActionBar;
import com.luxtone.lib.widget.ActionBar.ItemFocusChangeListener;
import com.luxtone.lib.widget.CullGroup;
import com.luxtone.lib.widget.Grid;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import tvfan.tv.AppGlobalVars;
import tvfan.tv.BasePage.ACTION_NAME;
import tvfan.tv.R;
import tvfan.tv.dal.HttpResponse.Listener4JSONObject;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.dal.models.ProgramListItem;
import tvfan.tv.lib.Lg;
import tvfan.tv.ui.gdx.widgets.CIBNLoadingView;
import tvfan.tv.ui.gdx.yourLike.YouLikeGridAdapter;
//import com.umeng.analytics.MobclickAgent;

public class OrderItem extends Group {

	public OrderItem(Page page, Context context) {
		super(page);
		setSize(1540, 1080);
		this.context = context;
		this.page = (tvfan.tv.ui.gdx.userCenters.Page) page;
		_initView();
	}

	private void _initView() {

		title = new Label(getPage());
		title.setPosition(100, 930);
		title.setTextSize(50);
		title.setColor(Color.WHITE);
		title.setText("我的订购");
		title.setAlpha(0.9f);
		addActor(title);

		// 暂无订购
		orderNo = new Label(getPage());
		orderNo.setPosition(555, 440);
		orderNo.setTextSize(50);
		orderNo.setColor(Color.WHITE);
		orderNo.setText("暂无订购");
		orderNo.setAlpha(0.9f);
		orderNo.setVisible(false);
		addActor(orderNo);

		// 初始化页码
		pageInfo = new Label(getPage());
		pageInfo.setColor(Color.WHITE);
		pageInfo.setAlpha(0.9f);
		pageInfo.setTextSize(40);
		pageInfo.setSize(200, 60);
		pageInfo.setAlignment(Align.right);
		pageInfo.setPosition(1130, 924);
		addActor(pageInfo);

		//
		mActionBar = new ActionBar(getPage());
		mActionBar.name("mActionBar");
		mActionBar.setNextFocusRight("mActionBar");
		mActionBar.configFocusImage(R.drawable.common_item_focused_bg, 190, 70);
		mActionBar.setSpaceWidth(30);
		mActionBar.setSize(200 * 2, 70);
		mActionBar.setItemFocusChangeListener(new ItemFocusChangeListener() {

			@Override
			public void onItemFocusChanged(Actor arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub
				if (arg2) {
					isSelectorFocus(arg1);
					if (arg1 == 1) {
						if (expiredList != null && !expiredList.isEmpty()) {
							dataList.clear();
							dataList.addAll(expiredList);
							totalnumber = dataList.size();
							flag = false;
							_visibleView(false);
							_initGrid();
						} else {
							totalnumber = 0;
							_visibleView(true);
						}

					} else {
						if (noExpiredList != null && !noExpiredList.isEmpty()) {
							dataList.clear();
							dataList.addAll(noExpiredList);
							totalnumber = dataList.size();
							flag = true;
							_visibleView(false);
							_initGrid();

						} else {
							totalnumber = 0;
							_visibleView(true);
						}
					}
					_updatePageInfo(0, totalnumber, 8);
				}
			}
		});
		// 未过期

		cullGroup = new CullGroup(getPage());
		cullGroup.setSize(190, 40);
		cullGroup.setPosition(0, 0);

		weiguoqi = new Label(getPage(), false);
		weiguoqi.setText("未过期");
		weiguoqi.setTextSize(40);
		weiguoqi.setAlignment(Align.left);
		weiguoqi.setColor(Color.WHITE);
		weiguoqi.setAlpha(1.0f);
		weiguoqi.setPosition(0, 10);
		cullGroup.addActor(weiguoqi);

		weiguoqi1 = new Label(getPage(), false);
		weiguoqi1.setTextSize(40);
		weiguoqi1.setAlignment(Align.right);
		weiguoqi1.setColor(Color.YELLOW);
		weiguoqi1.setAlpha(1.0f);
		weiguoqi1.setPosition(130, 10);
		cullGroup.addActor(weiguoqi1);

		// 已过期

		cullGroup2 = new CullGroup(getPage());
		cullGroup2.setSize(190, 40);
		cullGroup2.setPosition(0, 0);

		yiguoqi = new Label(getPage(), false);
		yiguoqi.setText("已过期");
		yiguoqi.setPosition(0, 10);
		yiguoqi.setTextSize(40);
		yiguoqi.setAlignment(Align.left);
		yiguoqi.setColor(Color.WHITE);
		yiguoqi.setAlpha(0.8f);
		cullGroup2.addActor(yiguoqi);

		yiguoqi1 = new Label(getPage(), false);
		yiguoqi1.setTextSize(40);
		yiguoqi1.setAlignment(Align.right);
		yiguoqi1.setColor(Color.YELLOW);
		yiguoqi1.setAlpha(0.8f);
		yiguoqi1.setPosition(130, 10);
		cullGroup2.addActor(yiguoqi1);

		mActionBar.addActor(cullGroup);
		mActionBar.addActor(cullGroup2);
		mActionBar.setPosition(100, 830);
		addActor(mActionBar);

		// 正在搜索
		loadingview = new CIBNLoadingView(getPage());
		loadingview.setVisible(false);
		addActor(loadingview);

		// 外框
		gridCullGroup = new CullGroup(getPage());
		gridCullGroup.setSize(1230, 840);
		gridCullGroup.setPosition(100, 0);
		gridCullGroup.setCullingArea(new Rectangle(-60, 0, 1230 + 120, 840));
		addActor(gridCullGroup);

	}

	private void _initGrid() {
		if (mGrid == null) {
			mGrid = new Grid(getPage());
			mGrid.setPosition(0, 0);
			mGrid.setSize(1230, 810);
			mGrid.setGapLength(10);
			mGrid.setOrientation(Grid.ORIENTATION_VERTICAL);
			mGrid.setRowNum(4);
			mGrid.setCull(false);
			mGrid.setCullingArea(new Rectangle(-60, 0, 1230 + 120, 810));
			dataAdapter = new YouLikeGridAdapter(getPage());
			dataAdapter.setData(dataList);
			mGrid.setAdapter(dataAdapter);
			mGrid.setAdjustiveScrollLengthForBackward(310);
			mGrid.setAdjustiveScrollLengthForForward(310);
			mGrid.setItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(Actor actor, int pos,
						AbsListView abslistview) {
					// 跳转详情页 ....
					Bundle options = new Bundle();
					ArrayList<ProgramListItem> exList = new ArrayList<ProgramListItem>();

					if (!flag && expiredList != null && expiredList.size() > 0) {
						exList = expiredList;
					}
					if (flag && noExpiredList != null
							&& noExpiredList.size() > 0) {
						exList = noExpiredList;
					}
					if (exList.size() > 0) {
						ProgramListItem listItem = exList.get(pos);

						options.putString("id", listItem.getFilmid());
						page.doAction(ACTION_NAME.OPEN_DETAIL, options);

						HashMap<String, String> map = new HashMap<String, String>();
						map.put("UID", AppGlobalVars.getIns().USER_ID);
						map.put("PROGRAM_ID", listItem.getFilmid());
						map.put("WAY_NAME", "订购-" + listItem.getPostName());
						map.put("U_I_N",
								AppGlobalVars.getIns().USER_ID + "|"
										+ listItem.getFilmid() + "|"
										+ listItem.getPostName());
//						MobclickAgent.onEvent(page.getActivity()
//								.getApplicationContext(), "event_detail",
//								map);
						Lg.i("TAG",
								"订购：" + listItem.getPostName());
					}

				}
			});
			mGrid.setOnItemSelectedChangeListener(new OnItemSelectedChangeListener() {

				@Override
				public void onSelectedChanged(int position, Actor actor) {
					// TODO Auto-generated method stub
					_updatePageInfo(position, totalnumber, 8);
				}
			});
			gridCullGroup.addActor(mGrid);
		} else {
			dataAdapter.setData(dataList);
			mGrid.setAdapter(dataAdapter);
		}
		_visibleView(false);
	}

	public void _requestData() {
		if (rd == null)
			rd = new RemoteData(context);
		rd.getOrderList(new Listener4JSONObject() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					if (expiredList == null)
						expiredList = new ArrayList<ProgramListItem>();
					else
						expiredList.clear();

					if (noExpiredList == null)
						noExpiredList = new ArrayList<ProgramListItem>();
					else
						noExpiredList.clear();
					if (response.has("expired")) {
						JSONArray jsonarr = response.getJSONArray("expired");
						for (int i = 0; i < jsonarr.length(); i++) {
							ProgramListItem order = new ProgramListItem();
							JSONObject jsonobject = (JSONObject) jsonarr.get(i);
							order.setId(jsonobject.optString("productId", ""));
							order.setPostName(jsonobject.optString(
									"productName", ""));
							order.setFormatStyle(jsonobject.optString(
									"productType", ""));
							order.setPostImg(jsonobject.optString("img", ""));
							order.setFilmid(jsonobject.optString("contentId",
									""));
							order.setCornerPrice(jsonobject.optString(
									"cornerPrice", ""));
							order.setCornerType(jsonobject.optString(
									"cornerType", ""));
							expiredList.add(order);
						}
					}
					if (response.has("notexpired")) {
						JSONArray notexpiredjsonarr = response
								.getJSONArray("notexpired");
						for (int i = 0; i < notexpiredjsonarr.length(); i++) {
							ProgramListItem order = new ProgramListItem();
							JSONObject jsonobject = (JSONObject) notexpiredjsonarr
									.get(i);
							order.setId(jsonobject.optString("productId", ""));
							order.setPostName(jsonobject.optString(
									"productName", ""));
							order.setFormatStyle(jsonobject.optString(
									"productType", ""));
							order.setPostImg(jsonobject.optString("img", ""));
							order.setFilmid(jsonobject.optString("contentId",
									""));
							order.setCornerPrice(jsonobject.optString(
									"cornerPrice", ""));
							order.setCornerType(jsonobject.optString(
									"cornerType", ""));
							noExpiredList.add(order);
						}
					}
					dataList.clear();
					dataList.addAll(noExpiredList);
					totalnumber = dataList.size();
					loadingview.setVisible(false);
					Gdx.app.postRunnable(new Runnable() {

						@Override
						public void run() {
							_updatePageInfo(0, totalnumber, 8);
							_updateTotalInfo(noExpiredList.size(),
									expiredList.size());
							if (dataList.isEmpty()) {
								// 暂无消费记录
								_visibleView(true);
								return;
							}
							_initGrid();
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					loadingview.setVisible(false);
					orderNo.setVisible(true);
					expiredList.clear();
					noExpiredList.clear();
					dataList.clear();
					expiredList = null;
					noExpiredList = null;
					dataList = null;
				}

			}

			@Override
			public void onError(String errorMessage) {
			}
		});
	}

	private void _visibleView(boolean visible) {
		if (orderNo != null)
			orderNo.setVisible(visible);
		if (mGrid != null)
			mGrid.setVisible(!visible);
	}

	public void _updatePageInfo(int pos, int total, int pagenums) {
		if (pos == 0 && total == 0) {
			pageInfo.setText("0/0");
			return;
		}
		pagenow = pos / pagenums + 1;
		totalpage = 0;
		if (total % pagenums == 0) {
			totalpage = total / pagenums;
		} else {
			totalpage = total / pagenums + 1;
		}
		pageInfo.setText(pagenow + "/" + totalpage);
	}

	public void _updateTotalInfo(int weiguoqi, int yiguoqi) {
		wCount = weiguoqi;
		yCount = yiguoqi;
		yiguoqi1.setText("(" + yCount + ")");
		weiguoqi1.setText("(" + wCount + ")");
	}

	public void onResume() {
		mActionBar.configFocusImage(R.drawable.common_item_focused_bg, 190, 70);
		weiguoqi.setText("未过期");
		yiguoqi.setText("已过期");
		yiguoqi1.setText("(" + yCount + ")");
		weiguoqi1.setText("(" + wCount + ")");
		title.setText("我的订购");
		pageInfo.setText(pagenow + "/" + totalpage);
		orderNo.setText("暂无订购");
		super.onResume();
	}

	/**
	 * 设置mActionBar的初始化焦点
	 * 
	 * @param ipos
	 */
	public void isSelectorFocus(int aIndex) {
		if (aIndex == 1) {
			weiguoqi.setAlpha(0.8f);
			weiguoqi1.setAlpha(0.8f);
			yiguoqi.setAlpha(1.0f);
			yiguoqi1.setAlpha(1.0f);
		} else {
			yiguoqi.setAlpha(0.8f);
			yiguoqi1.setAlpha(0.8f);
			weiguoqi.setAlpha(1.0f);
			weiguoqi1.setAlpha(1.0f);
		}
	}

	Grid mGrid;
	Context context;
	private int pagenow = 0;
	private int totalpage = 0;
	// 右侧海报列表布局
	private ArrayList<ProgramListItem> expiredList = new ArrayList<ProgramListItem>();// 右侧栏海报列表页数据
	private ArrayList<ProgramListItem> noExpiredList = new ArrayList<ProgramListItem>();// 右侧栏海报列表页数据
	private ArrayList<ProgramListItem> dataList = new ArrayList<ProgramListItem>();// 右侧栏海报列表页数据
	private int totalnumber = 0;
	private RemoteData rd;
	private Label title, orderNo, pageInfo, weiguoqi1, weiguoqi, yiguoqi,
			yiguoqi1;
	int wCount, yCount;
	private boolean flag = true;
	private tvfan.tv.ui.gdx.userCenters.Page page;
	CullGroup cullGroup, cullGroup2, cullGroup1, gridCullGroup;
	private ActionBar mActionBar;
	private YouLikeGridAdapter dataAdapter;
	private CIBNLoadingView loadingview;
}
