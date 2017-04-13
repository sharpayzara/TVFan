package tvfan.tv.ui.gdx.topLists;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.KeyEvent;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.luxtone.lib.widget.AbsListView.OnItemSelectedChangeListener;
import com.luxtone.lib.widget.CullGroup;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.BasePage;
import tvfan.tv.R;
import tvfan.tv.dal.HttpResponse;
import tvfan.tv.dal.LocalData;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.ui.gdx.topLists.TopMenuListAdapter.*;


/**
 *  desc  排行榜page
 *  @author  yangjh
 *  created at  2016/4/8 20:37 
 */
public class Page extends BasePage {

	boolean fcousRight = false;
	boolean isNew = false;
	boolean fmark = false;
	boolean mmark = false;
	boolean pmark = false;
	boolean omark = false;
	boolean cmark = false;
	List<TopListCategoryItem> topCategoryList;
	private Map<String,CommonTopItem> itemMap;
	private TopMenuListItem menulistitem;
	// 左侧栏目按钮布局
	private TopMenuGroup menuGroup;
	// 图片背景
	private Image bgImg;
	private CullGroup cullGroup;
	private List<String> menuArray = new ArrayList();
	private RemoteData rd;
	private ContentValues user;
	private int iprepos = -1;
	private int selectPos = 0;
	private Timer timer;
	private Task task;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		rd = new RemoteData(this.getActivity());
		timer = new Timer();
		menuArray = new ArrayList<String>();
		itemMap = new HashMap<String, CommonTopItem>();
		getTopCategory();
	}
	public void execUI(){
		_initBg();
		_initMenuGroup();
		_initData();
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
			menulistitem = (TopMenuListItem) actor;
			menulistitem.setFocusImgBg(true);

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
		menuGroup = new TopMenuGroup(this,menuArray);
		menuGroup.setSize(360, 1080);
		menuGroup.setPosition(0, 0);
		((TopMenuGroup) menuGroup)
				.setOnItemSelectedChangeListener(new OnItemSelectedChangeListener() {

					@Override
					public void onSelectedChanged(final int pos, Actor actor) {
						_setOnItemSelectedChange(pos, actor);
					}

				});
	}

	private void _switchView(int pos) {
		cullGroup.clearChildren();
		fcousRight = false;
		CommonTopItem item = getCommonTopItem(menuArray.get(pos),"00080000000000000000000000000050",topCategoryList.get(pos).getPt());
		cullGroup.addActor(item);
		item.updateMoreBtnName(topCategoryList.get(pos).getName());
		item.updateCategoryName(topCategoryList.get(pos).getName());
	}


	private int bgresid;
	/**
	 * 初始化背景图
	 */
	private void _initBg() {
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
					bgresid = R.drawable.other_background;
				else
					bgresid = Integer.parseInt(bg);

				bgImg.setDrawableResource(bgresid);
				bgImg.addAction(Actions.fadeIn(0.6f));
			}
		});
		addActor(bgImg);

		cullGroup = new CullGroup(this);
		cullGroup.setSize(1920-360, 1080);
		cullGroup.setPosition(360, 0);
		cullGroup.setCullingArea(new Rectangle(0, 0, 1920-360, 1080));
		addActor(cullGroup);
	}

	private void _initData() {
		addActor(menuGroup);
	}


	public boolean onKeyDown(int keycode) {
		switch (keycode) {
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				if (fcousRight) {
					return true;
				}else {
					return false;
				}

		}
		return super.onKeyDown(keycode);
	};

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
	}

	@SuppressLint("NewApi")
	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	public void getTopCategory(){
		rd.getTopCategory("00080000000000000000000000000050", new HttpResponse.Listener4JSONArray() {
			@Override
			public void onResponse(JSONArray jsonArray) throws JSONException {
				if(topCategoryList == null){
					topCategoryList = new ArrayList<TopListCategoryItem>();
				}
				topCategoryList.clear();
				if(jsonArray.length() == 0){
					return;
				}
				menuArray.clear();
				for(int i = 0 ; i <  jsonArray.length();i++){
					TopListCategoryItem item = new TopListCategoryItem();
					item.setId(jsonArray.getJSONObject(i).getString("id"));
					item.setName(jsonArray.getJSONObject(i).getString("name"));
					item.setPt(jsonArray.getJSONObject(i).getString("pt"));
					topCategoryList.add(item);
					menuArray.add(jsonArray.getJSONObject(i).getString("name"));
				}
				//menuGroup.setSelection(0);
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						execUI();
					}});
			}

			@Override
			public void onError(String errorMessage) {

			}
		});
	}


	public CommonTopItem getCommonTopItem(String itemName,String templateId,String pt){
		if(itemMap.containsKey(itemName)){
			return itemMap.get(itemName);
		}else{

			CommonTopItem commonItem = new CommonTopItem(Page.this, getActivity()) {
				@Override
				public boolean dispatchKeyEvent(int keycode,Actor focusActor) {
					if (keycode == Input.Keys.MENU && iprepos == 1) {
						return true;
					}
					return super.dispatchKeyEvent(keycode, focusActor);
				}
			};
			commonItem.setSize(1560, 1080);
			commonItem.setPosition(0, 0);
			commonItem.getItemTop(templateId,pt);
			itemMap.put(itemName,commonItem);
			return commonItem;
		}
	}
}
