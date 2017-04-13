package tvfan.tv.ui.gdx.userCenter;

/**
 * 個人中心-收藏界面
 * 
 * @author 孫文龍
 * 
 */
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import tvfan.tv.dal.RemoteData;
import tvfan.tv.dal.HttpResponse.Listener4JSONObject;
import tvfan.tv.dal.models.ConsumeList;
import tvfan.tv.lib.Lg;
import tvfan.tv.ui.gdx.widgets.CIBNLoadingView;
import android.content.Context;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.Grid;

public class ConsumeItem extends Group {

	public ConsumeItem(Page page, Context context) {
		super(page);
		setSize(1540, 1080);
		this.context = context;
		_initView();
	}

	public void _initView() {
		// 初始化 title
		title = new Label(getPage());
		title.setText("消费记录");
		title.setColor(Color.WHITE);
		title.setAlpha(0.9f);
		title.setTextSize(50);
		title.setPosition(100, 930);
		addActor(title);

		consumeNo = new Label(getPage());
		consumeNo.setPosition(555, 440);
		consumeNo.setTextSize(50);
		consumeNo.setColor(Color.WHITE);
		consumeNo.setText("暂无消费记录");
		consumeNo.setAlpha(0.9f);
		consumeNo.setVisible(false);
		addActor(consumeNo);

		// 正在搜索
		loadingview = new CIBNLoadingView(getPage());
		loadingview.setVisible(false);
		addActor(loadingview);
	}

	private void _initGrid() {

		if (mGrid == null) {
			mGrid = new Grid(getPage());
			mGrid.setSize(1230, 880);
			mGrid.setPosition(100, 0);
			mGrid.setRowNum(1);
			mGrid.setOrientation(Grid.ORIENTATION_VERTICAL);
			mGrid.setCullingArea(new Rectangle(-50, -10, 1330, 940));
			mGrid.setGapLength(50);
			mGrid.setAdjustiveScrollLengthForBackward(315);
			mGrid.setAdjustiveScrollLengthForForward(315);
			dataAdapter = new ConsumeListAdapter(getPage());
			dataAdapter.setData(consumeList);
			mGrid.setAdapter(dataAdapter);
			addActor(mGrid);
			_visibleView(false);
		} else {
			_visibleView(false);
			dataAdapter.setData(consumeList);
			mGrid.setAdapter(dataAdapter);
		}
	}

	private void _visibleView(boolean visible) {
		if (consumeNo != null)
			consumeNo.setVisible(visible);
		if (mGrid != null)
			mGrid.setVisible(!visible);
	}

	@Override
	public void onResume() {
		title.setText("消费记录");
		consumeNo.setText("暂无消费记录");
		super.onResume();
	}

	public void _requestData() {
		if (rd == null)
			rd = new RemoteData(context);
		rd.getConsumeList(new Listener4JSONObject() {

			@Override
			public void onResponse(JSONObject response) {
				Lg.e("response", "getMsgList" + response);
				try {
					if (consumeList == null)
						consumeList = new ArrayList<ConsumeList>();
					else
						consumeList.clear();
					JSONArray jsonarr = response.getJSONArray("data");
					for (int i = 0; i < jsonarr.length(); i++) {
						ConsumeList consume = new ConsumeList();
						JSONObject jsonobject = (JSONObject) jsonarr.get(i);
						consume.setOrderid(jsonobject.optString("orderid", ""));
						consume.setOrderTime(jsonobject.optString("orderTime",
								""));
						consume.setProductName(jsonobject.optString(
								"productName", ""));
						consume.setExpireTime(jsonobject.optString(
								"expireTime", ""));
						consume.setValidTime(jsonobject.optString("validTime",
								""));
						consume.setStatus(jsonobject.optString("status", ""));
						consume.setPrice(jsonobject.optString("price", ""));
						consume.setPayMethod(jsonobject.optString("payMethod",
								""));
						consumeList.add(consume);
					}
					loadingview.setVisible(false);
					Gdx.app.postRunnable(new Runnable() {

						@Override
						public void run() {
							if (consumeList.isEmpty()) {
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
					_visibleView(true);
					consumeList.clear();
					consumeList = null;
				}
			}

			@Override
			public void onError(String errorMessage) {
			}

		});
	}

	Grid mGrid;
	// 初始化右侧栏海报列表坐标宽高
	Context context;
	private RemoteData rd;
	private Label title, consumeNo;
	private ConsumeListAdapter dataAdapter;
	private List<ConsumeList> consumeList;// 右侧栏海报列表页数据
	private CIBNLoadingView loadingview;
}
