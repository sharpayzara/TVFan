package tvfan.tv.ui.gdx.userCenter;


import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.AppGlobalVars;
import tvfan.tv.BasePage;
import tvfan.tv.dal.LocalData;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.dal.UserHelper;
import tvfan.tv.dal.HttpResponse.Listener4JSONObject;
import tvfan.tv.ui.gdx.userCenter.UserMenuListAdapter.UserMenuListItem;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import tvfan.tv.R;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.luxtone.lib.utils.Utils;
import com.luxtone.lib.widget.AbsListView.OnItemSelectedChangeListener;
import com.luxtone.lib.widget.CullGroup;

/**
 * 個人中心-入口
 * 
 * @author 孫文龍
 * 
 */
public class Page extends BasePage {

	boolean fcousRight = false;
	boolean isNew = false;
	boolean fmark = false;
	boolean mmark = false;
	boolean pmark = false;
	boolean omark = false;
	boolean cmark = false;

	// 右侧view
	private ActivationItem activation;
	private AccountItem account;
	private PlayHistoryItem playHistory;
	private FavoritesItem favorites;
	private MessageItem message;
	private OrderItem order;
	private ConsumeItem consume;
	private UserMenuListItem menulistitem;
	private SwitchUserDialog sd = null;
	// 左侧栏目按钮布局
	private UserMenuGroup menuGroup;
	// 图片背景
	private Image bgImg;
	private CullGroup cullGroup;
	private DeleteHisAndFavDialog deleteDialog;

	// 数据
	private String[] menuArray = new String[] { "最近观看", "我的收藏", "我的消息"};// 左侧栏目分类列表数据
	private LocalData localData;
	private RemoteData rd;
	private ContentValues user;
	private String forward = "";
	private HashMap<String, String> fMap;
	private UserHelper userHelper;
	private int iprepos = -1;
	private int selectPos = 0;
	private Timer timer;
	private Task task;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		forward = bundle.getString("actionParam"); // 获取跳转收藏/历史记录
		userHelper = new UserHelper(this.getActivity());
		localData = new LocalData(this.getActivity());
		rd = new RemoteData(this.getActivity());
		timer = new Timer();
		_initBg();
		_initMenuGroup();
		_initData();
		_selectFavoriteTable();

	}

	private boolean _loadUser() {
		user = userHelper.getUser(AppGlobalVars.getIns().USER_ID);
		return user == null || user.size() == 0;
	}

	private String _loadUserImg() {
		if (!_loadUser())
			return user.get("wxheadimgurl").toString();
		else
			return "";
	}

	public void _setOnItemSelectedChange(final int pos, Actor actor) {

		fmark = false;
		mmark = false;
		pmark = false;
		omark = false;
		cmark = false;
		selectPos = pos;
		try {
			if (menulistitem != null) {
				menulistitem.setFocusImgBg(false);
			}
			menulistitem = (UserMenuListItem) actor;
			menulistitem.setFocusImgBg(true);
			if (pos == 2)
				menulistitem.setNewImgBgGone();
			if (task != null) {
				task.cancel();
			}
			if (timer != null) {
				timer.stop();
			}

			if (iprepos != pos) {
				fcousRight = true;
				task = new Task() {

					@Override
					public void run() {
						iprepos = pos;
						_switchView(pos);
					}
				};
				timer.scheduleTask(task, 0.8f);
				timer.start();
			} else {
				fcousRight = false;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 初始化页面group 坐标宽高
	 */
	private void _initMenuGroup() {

		// 设置左侧栏布局 并添加背景图片
		menuGroup = new UserMenuGroup(this, menuArray);
		menuGroup.setSize(480, 1080);
		menuGroup.setPosition(0, 0);
		((UserMenuGroup) menuGroup)
				.setOnItemSelectedChangeListener(new OnItemSelectedChangeListener() {

					@Override
					public void onSelectedChanged(final int pos, Actor actor) {
						_setOnItemSelectedChange(pos, actor);
					}

				});
		if (isNew) {
			menuGroup._setFavView();
		}
		menuGroup.initHeadImg(_loadUserImg());
	}

	public void _selectFavoriteTable() {
		if (fMap != null) {
			fMap.clear();
			fMap = null;
		}
		fMap = new HashMap<String, String>();
		Cursor cursor = localData.runQuery("SELECT * FROM FAVORITE_TAB", null);
		while (cursor.moveToNext()) {
			String programSeriesId = cursor.getString(cursor
					.getColumnIndex("programSeriesId"));
			String latestEpisode = cursor.getString(cursor
					.getColumnIndex("latestEpisode"));

			// 将查询到的结果加入到集合中
			fMap.put(programSeriesId, latestEpisode);

		}
		cursor.close();
		requestDate();
	}

	/**
	 * 设置左侧栏具体选中那个位置
	 * @param i
	 */
	public void setMenuGroupFouce(int i) {
		menuGroup.setSelection(i);
	}

	public void requestDate() {
		rd.getFavoritesList(new Listener4JSONObject() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONArray jsonarr = response.getJSONArray("programList");

					for (int i = 0; i < jsonarr.length(); i++) {
						JSONObject jsonobject = (JSONObject) jsonarr.get(i);
						String id = jsonobject.optString("id", "");
						String currentNum = jsonobject.optString("currentNum",
								"");
						if (fMap != null
								&& fMap.size() > 0
								&& fMap.get(id) != null
								&& !fMap.get(id).equals("")
								&& !currentNum.equals("")
								&& Math.abs(Integer.parseInt(fMap.get(id))) < Integer
										.parseInt(currentNum)) {
							if (menuGroup != null) {
								menuGroup._setFavView();
							} else {
								isNew = true;
							}

							break;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(String errorMessage) {
				Utils.showToast("获取数据失败,请重试...");
			}
		});
	}

	private void _switchView(int pos) {
		cullGroup.clearChildren();
		fcousRight = false;
		switch (pos) {
		/*case 0:
			_initUserView();
			break;*/
		case 0:
			if (playHistory == null) {
				playHistory = new PlayHistoryItem(Page.this, getActivity()) {
					@Override
					public boolean dispatchKeyEvent(int keycode,
							Actor focusActor) {
						if (keycode == Keys.MENU && iprepos == 0) {
							if (deleteDialog == null)
								deleteDialog = new DeleteHisAndFavDialog(
										Page.this, getActivity());
							deleteDialog._initData("his",
									playHistory._deleteName(), playHistory);
							deleteDialog.show();
							return true;
						}
						return super.dispatchKeyEvent(keycode, focusActor);
					}
				};
				playHistory.setSize(1540, 1080);
				playHistory.setPosition(0, 0);

			}
			cullGroup.addActor(playHistory);
			playHistory._initData();
			break;
		case 1:
			if (favorites == null) {
				favorites = new FavoritesItem(Page.this, getActivity()) {
					@Override
					public boolean dispatchKeyEvent(int keycode,
							Actor focusActor) {
						if (keycode == Keys.MENU && iprepos == 1) {
							if (deleteDialog == null)
								deleteDialog = new DeleteHisAndFavDialog(
										Page.this, getActivity());
							deleteDialog._initData("fav",
									favorites._DeleteName(), favorites);
							deleteDialog.show();
							return true;
						}
						return super.dispatchKeyEvent(keycode, focusActor);
					}
				};
				favorites.setSize(1540, 1080);
				favorites.setPosition(0, 0);
			}
			cullGroup.addActor(favorites);
			favorites._favoriteFromDb();

			break;
		case 2:
			if (message == null) {
				message = new MessageItem(Page.this, getActivity());
				message.setSize(1540, 1080);
				message.setPosition(0, 0);
			}
			cullGroup.addActor(message);
			message._requestData();
			break;
		/*	
		case 4:
			if (order == null) {
				order = new OrderItem(Page.this, getActivity());
				order.setSize(1540, 1080);
				order.setPosition(0, 0);
			}
			cullGroup.addActor(order);
			order._requestData();
			break;
		case 5:
			if (consume == null) {
				consume = new ConsumeItem(Page.this, getActivity());
				consume.setSize(1540, 1080);
				consume.setPosition(0, 0);

			}
			cullGroup.addActor(consume);
			consume._requestData();
			break;*/

		default:
			break;
		}
	}

	private int bgresid;
	/**
	 * 初始化背景图
	 */
	private void _initBg() {
		/*bgImg = new Image(this);
		bgImg.setPosition(0, 0);
		bgImg.setSize(1920, 1080);
		bgImg.setDrawableResource(R.drawable.bj);*/
		if (bgImg != null) {
			bgImg.dispose();
			bgImg = null;
		}

		bgImg = new Image(this);
		bgImg.setPosition(-50, -50);
		bgImg.setSize(AppGlobalConsts.APP_WIDTH + 100,
				AppGlobalConsts.APP_HEIGHT + 100);
		bgImg.setFocusAble(false);
		bgImg.setAlpha(0.95f);

		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				addActor(bgImg);
				bgImg.toBack();
				bgImg.addAction(Actions.fadeOut(0));

				LocalData ld = new LocalData(getActivity());
				String bg = ld
						.getKV(AppGlobalConsts.PERSIST_NAMES.BACKGROUND_IMAGE
								.name());

				if (bg == null)
//					bgresid = R.drawable.bja;
					bgresid = R.drawable.home_background;
				else
					bgresid = Integer.parseInt(bg);

				bgImg.setDrawableResource(bgresid);
				bgImg.addAction(Actions.fadeIn(0.6f));
			}
		});
		addActor(bgImg);

		cullGroup = new CullGroup(this);
		cullGroup.setSize(1540, 1080);
		cullGroup.setPosition(380, 0);
		cullGroup.setCullingArea(new Rectangle(0, 0, 1540, 1080));
		addActor(cullGroup);
	}

	private void _initData() {
		try {
			if (forward != null && !forward.equals("")) {
				JSONObject selectJSON = new JSONObject(forward);
				String select = selectJSON.optString("select", "");
				if (select.equals("favorite")) {
					menuGroup.setSelection(1);
				} else if (select.equals("history")) {
					menuGroup.setSelection(0);
				} else if (select.equals("orders")) {
					menuGroup.setSelection(4);
				} else if (select.equals("message")) {
					menuGroup.setSelection(2);
				} else if (select.equals("")) {
					menuGroup.setSelection(0);
				}
			} else {
				menuGroup.setSelection(0);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		addActor(menuGroup);
	}

	public void sendLocalMsg() {
		Intent inte = new Intent();
		sendLocalStickyMsg(AppGlobalConsts.LOCAL_MSG_FILTER.USER_IMAGE_CHANGE,
				inte);
	}

	public void _regMSGFilter() {
		// 筛选注册微信绑定注册消息监听
		registerLocalMsgReceiver(new LocalMsgListener() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String userID = "", userName = "", wxID = "", userPicUrl = "", userToken = "";
				String userJson = intent.getExtras().getString(
						AppGlobalConsts.INTENT_MSG_PARAM);
				try {
					JSONObject a = new JSONObject(userJson);
					JSONObject wxmsg = a.getJSONObject("body");
					userID = wxmsg.optString("userID", "");
					wxID = wxmsg.optString("wxID", "");
					userName = wxmsg.optString("userName", "");
					userPicUrl = wxmsg.optString("userPicUrl", "");
					userToken = wxmsg.optString("userToken", "");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				AppGlobalVars.getIns().USER_ID = userID;
				AppGlobalVars.getIns().USER_TOKEN = userToken;
				AppGlobalVars.getIns().USER_PIC = userPicUrl;
				AppGlobalVars.getIns().USER_NICK_NAME = userName;
				localData.setKV(
						AppGlobalConsts.PERSIST_NAMES.CURRENT_USER.name(),
						userID);
				ContentValues userData = new ContentValues();
				userData.put("userid", userID);
				userData.put("wxid", wxID);
				userData.put("wxname", userName);
				userData.put("wxheadimgurl", userPicUrl);
				userData.put("token", userToken);
				userHelper.addUser(userData);
				sendLocalMsg();
				iprepos = 0;
				_switchView(0);
			}
		}, AppGlobalConsts.LOCAL_MSG_FILTER.USER_BIND);
	}

	public void _initUserView() {
		if (_loadUser()) {
			if (activation == null) {
				activation = new ActivationItem(Page.this, getActivity());
				activation.setSize(1540, 1080);
				activation.setPosition(0, 0);
			}
			refreshMenu("用户激活");
			cullGroup.addActor(activation);
			activation._requestTicketData();
			activation._initBtnView();
		} else {
			if (account == null) {
				account = new AccountItem(Page.this, getActivity());
				account.setSize(1540, 1080);
				account.setPosition(0, 0);
			}
			refreshMenu("我的账号");
			cullGroup.addActor(account);
			account._setAccountMsg(user);
		}
	}

	public boolean onKeyDown(int keycode) {

		if (selectPos == 0 && keycode == KeyEvent.KEYCODE_DPAD_UP
				&& menuGroup.isHasFocus()) {
			return true;
		}
		if (selectPos == 2 && keycode == KeyEvent.KEYCODE_DPAD_DOWN
				&& menuGroup.isHasFocus()) {
			return true;
		}
		switch (keycode) {

		case KeyEvent.KEYCODE_DPAD_RIGHT:

			if (fcousRight) {
				return true;
			}
			switch (iprepos) {

			case 1:
				if (!fmark) {
					favorites.requestFocus();
					fmark = true;
					return true;
				} else
					break;

			case 2:
				if (!mmark) {
					message.requestFocus();
					mmark = true;
					return true;
				} else
					break;
			/*case 4:
				if (!omark) {
					order.requestFocus();
					omark = true;
					return true;
				} else
					break;
			case 5:
				if (!cmark) {
					consume.requestFocus();
					cmark = true;
					return true;
				} else
					break;*/

			default:
				break;
			}

		}
		return super.onKeyDown(keycode);

	};

	public void hideMessageDot() {
		if (menuGroup != null) {
			menuGroup.hideMessageDot();
		}
	}

	public void showSwltchDialog() {
		sd = new SwitchUserDialog(this, this.getActivity()) {
			@Override
			public boolean dispatchKeyEvent(int keycode, Actor focusActor) {
				if (keycode == Keys.MENU) {
					sd.showDelDialog();
					return true;
				}
				return super.dispatchKeyEvent(keycode, focusActor);
			}
		};
		sd._initData();
		sd.show();
	}

	public void _deleteUser() {
		sd.removeUser();
		iprepos = -1;
		menuGroup.initHeadImg(_loadUserImg());
		if (_loadUser()) {
			menuGroup.setSelection(0);
		}

	}

	public void _switchUser() {
		if (menuGroup != null) {
			menuGroup.initHeadImg(_loadUserImg());
			iprepos = -1;
			menuGroup.setSelection(0);
		}
	}

	@Override
	protected void onResumeTextures() {
		super.onResumeTextures();
		bgImg.setDrawableResource(R.drawable.bj);
	}

	@Override
	public void recyclePage() {
		super.recyclePage();
	}

	@Override
	public void onDispose() {
		super.onDispose();
		unregisterLocalMsgReceiver(AppGlobalConsts.LOCAL_MSG_FILTER.USER_BIND);
	}

	@SuppressLint("NewApi")
	@Override
	public void onResume() {
		_regMSGFilter();

		if (iprepos == 0 && playHistory != null) {
			playHistory._initData();
		} else if (iprepos == 1 && favorites != null) {
			favorites._favoriteFromDb();
		}

		super.onResume();
	}

	@Override
	public void onPause() {
		unregisterLocalMsgReceiver(AppGlobalConsts.LOCAL_MSG_FILTER.USER_BIND);
		super.onPause();
	}

	public void refreshMenu(String user) {
		menuArray[0] = user;
		if (menuArray != null && menuArray.length > 0) {
			((UserMenuGroup) menuGroup).refreshMenu(menuArray);
		}
	}
}
