package tvfan.tv.ui.gdx.userCenters;

/**
 * 個人中心-訂購界面
 * 
 * @author 孫文龍
 * 
 */

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.AbsListView;
import com.luxtone.lib.widget.AbsListView.OnItemClickListener;
import com.luxtone.lib.widget.AbsListView.OnItemSelectedChangeListener;
import com.luxtone.lib.widget.Grid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import tvfan.tv.App;
import tvfan.tv.BasePage;
import tvfan.tv.BasePage.ACTION_NAME;
import tvfan.tv.R;
import tvfan.tv.dal.HttpResponse.Listener4JSONObject;
import tvfan.tv.dal.MsgHelper;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.dal.models.MsgList;
import tvfan.tv.dal.models.PortalMsgUpdateEvent;
import tvfan.tv.lib.Lg;
import tvfan.tv.ui.gdx.widgets.CIBNLoadingView;

public class MessageItem extends Group {

	public MessageItem(Page page, Context context) {
		super(page);
		setSize(1540, 1080);
		this.context = context;
		this.page = (BasePage) page;
		_initView();
	}

	private void _initGridList() {
		if (mGrid == null) {
			mGrid = new Grid(getPage());
			mGrid.setPosition(100, 50);
			mGrid.setSize(1920 - 480, 1080 - 250);
			mGrid.setGapLength(20);
			mGrid.setOrientation(Grid.ORIENTATION_VERTICAL);
			mGrid.setRowNum(1);
			mGrid.setCull(false);
			mGrid.setCullingArea(new Rectangle(-100, -50, 1440 + 200, 830 + 100));
			dataAdapter = new MessageItemAdapter(getPage());
			dataAdapter.setData(msgList);
			mGrid.setAdapter(dataAdapter);
			mGrid.setItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(Actor itemView, int position,
						AbsListView grid) {
					Bundle options = new Bundle();
					options.putString("msgID", msgList.get(position).getMsgId());
					options.putString("msgType", msgList.get(position)
							.getAction());
					page.doAction(ACTION_NAME.OPEN_MSG_DETAIL, options);
				}
			});
			mGrid.setOnItemSelectedChangeListener(new OnItemSelectedChangeListener() {

				@Override
				public void onSelectedChanged(int arg0, Actor arg1) {
					// TODO Auto-generated method stub
					if (msgList.get(arg0).getMark() == 0) {

						App.MESSAGENUMBER--; // add by wanqi,消息被浏览，未被观看消息数量要-1,
												// 并刷新首页消息数量显示
						EventBus.getDefault().post(
								new PortalMsgUpdateEvent(App.MESSAGENUMBER));
						if (App.MESSAGENUMBER <= 0) {  //若未读消息数量<=0,我的消息红点隐藏
							((tvfan.tv.ui.gdx.userCenters.Page) page)
									.hideMessageDot();
						}
						msgList.get(arg0).setMark(1);
						ContentValues msgData = new ContentValues();
						msgData.put("msgid", msgList.get(arg0).getMsgId());
						msgHelper.addMsg(msgData);
					}
				}
			});
			addActor(mGrid);
			_visibleView(false);
		} else {
			_visibleView(false);
			dataAdapter.setData(msgList);
			mGrid.setAdapter(dataAdapter);
		}

	}

	public void _requestData() {
		if (msgHelper == null)
			msgHelper = new MsgHelper(context);
		if (rd == null)
			rd = new RemoteData(context);
		arrMsgInfo = msgHelper.queryAllMsg();

		rd.getMsgList(new Listener4JSONObject() {

			@Override
			public void onResponse(JSONObject response) {
				Lg.e("response", "getMsgList" + response);
				try {
					if (msgList == null) {
						msgList = new ArrayList<MsgList>();
					} else {
						msgList.clear();
					}
					JSONArray jsonarr = response.getJSONArray("data");
					int total = 0;
					for (int i = jsonarr.length() - 1; i >= 0; i--) {
						MsgList msg = new MsgList();
						JSONObject jsonobject = (JSONObject) jsonarr.get(i);
						msg.setMsgId(jsonobject.optString("msgId", ""));
						msg.setTitle(jsonobject.optString("title", ""));
						msg.setType(jsonobject.optString("type", ""));
						msg.setAction(jsonobject.optString("action", ""));
						msg.setImg(jsonobject.optString("img", ""));
						msg.setCreateTime(jsonobject
								.optString("createTime", ""));
						if (arrMsgInfo != null && arrMsgInfo.length > 0) {
							for (int j = 0; j < arrMsgInfo.length; j++) {
								if (arrMsgInfo[j].equals(msg.getMsgId())) {
									msg.setMark(1);
								}
							}
						}
						if (msg.getMark() == 0) {
							total++;
						}
						msgList.add(msg);
					}
					// wanqi,校对未读消息的数量，以此处的数值为准
					if (App.MESSAGENUMBER != total) {
						App.MESSAGENUMBER = total;
						EventBus.getDefault().post(
								new PortalMsgUpdateEvent(App.MESSAGENUMBER));
					}
					loadingview.setVisible(false);
					Gdx.app.postRunnable(new Runnable() {

						@Override
						public void run() {
							if (msgList.isEmpty()) {
								// 暂无消费记录
								_visibleView(true);
								return;
							}
							_initGridList();
						}
					});
				} catch (JSONException e) {
					e.printStackTrace();
					loadingview.setVisible(false);
					_visibleView(true);
					msgList.clear();
					msgList = null;
				}
			}

			@Override
			public void onError(String errorMessage) {
			}
		});

	}

	private void _visibleView(boolean visible) {
		if (msgNo != null)
			msgNo.setVisible(visible);
		if (mGrid != null)

			mGrid.setVisible(!visible);
	}

	private void _initView() {

		iconImage = new Image(getPage());
		iconImage.setPosition(1220,1080-144);
		iconImage.setSize(46,46);
		iconImage.setDrawableResource(R.mipmap.message_icon);
		addActor(iconImage);

		titleLabel = new Label(getPage());
		titleLabel.setText("我的消息");
		titleLabel.setPosition(1280,1080-140);
		titleLabel.setTextSize(40);
		//titleLabel.setTextSize(App.adjustFontSize());
		titleLabel.setTextColor(Color.parseColor("#636361"));
		addActor(titleLabel);


		// 正在搜索
		loadingview = new CIBNLoadingView(getPage());
		loadingview.setVisible(false);
		addActor(loadingview);

		msgNo = new Image(getPage());
		msgNo.setPosition(394, 514);
		msgNo.setDrawableResource(R.mipmap.no_news);
		msgNo.setVisible(false);
		msgNo.setSize(395,120);
		addActor(msgNo);
	}

	@Override
	public void onResume() {
		titleLabel.setText("我的消息");
		msgNo.setDrawableResource(R.mipmap.no_news);
		super.onResume();
	}

	Grid mGrid;
	BasePage page;
	Context context;
	private Label titleLabel;
	private ArrayList<MsgList> msgList;// 右侧栏海报列表页数据
	private MessageItemAdapter dataAdapter;
	private RemoteData rd;
	private Image iconImage,msgNo;
	private MsgHelper msgHelper;
	String[] arrMsgInfo = null;
	private CIBNLoadingView loadingview;
}
