package tvfan.tv.ui.gdx.programHotList;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tvfan.tv.App;
import tvfan.tv.AppGlobalConsts;
import tvfan.tv.BasePage;
import tvfan.tv.dal.HttpRequestWrapper;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.dal.HttpResponse.Listener4JSONObject;
import tvfan.tv.dal.models.ProgramListItem;
import tvfan.tv.dal.models.ProgramMenus;
import tvfan.tv.lib.Lg;
import tvfan.tv.lib.NetWorkUtils;
import tvfan.tv.ui.gdx.programList.MenuListItem;
import tvfan.tv.ui.gdx.programList.ProgramFilterDialog;
import tvfan.tv.ui.gdx.widgets.HotGroup;
import tvfan.tv.ui.gdx.widgets.ImageGroup;
import tvfan.tv.ui.gdx.widgets.ListGroup;
import tvfan.tv.ui.gdx.widgets.MenuGroup;
import tvfan.tv.ui.gdx.widgets.TagFilter;
import android.os.Bundle;
import android.view.KeyEvent;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.luxtone.lib.gdx.OnkeyListener;
import com.luxtone.lib.utils.Utils;
import com.luxtone.lib.widget.AbsListView;
import com.luxtone.lib.widget.AbsListView.OnItemClickListener;
import com.luxtone.lib.widget.AbsListView.OnItemSelectedChangeListener;
import com.luxtone.lib.widget.PagingDataObtainedCallback;
import com.luxtone.lib.widget.PagingGrid;
import com.luxtone.lib.widget.PagingGrid.IPagingActionFactory;

public class Page extends BasePage {
	
	
	private Group menuGroup;
	private Group listGroup;
	private Group hotGroup;
	
	private ImageGroup imgbg;
	
	private List<ProgramMenus> menuList;
	private List<ProgramListItem> programList;
	private String mHeadTitle = "";
	private MenuListItem menulistitem;
	private String curID = "";
	private int pagecount = 0;
	
	private Timer timer;
	private Task task;
	private RemoteData rd;
	private PagingDataObtainedCallback<ProgramListItem> mcallback = null;
	
	//固定位置
	private int iMenuWidth = 480;
	private int iMenuHeight = AppGlobalConsts.APP_HEIGHT;
	private int iMenuX = 65;
	private int iMenuY = 0;
	private int iListWidth = 1300;
	private int iListHeight = 870;
	private int iListX = 535;//487
	private int iListY = 0;
	
	private boolean islock = false;
	private int iprepos = 0;
	
	private int parentCatgId;
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		App.addPage(this);
		// //////////////////////////////////////
//		Image testlist = new Image(this);
//		testlist.setPosition(0, 0);
//		testlist.setSize(AppGlobalConsts.APP_WIDTH, AppGlobalConsts.APP_HEIGHT);
//		// specail 去网络取图
//		testlist.setDrawable(findTextureRegion(R.drawable.testlist));
//		testlist.setFocusAble(false);
//		testlist.setAlpha(0.95f);
//		addActor(testlist);
		// //////////////////////////////////////
		
		
		
		initDate();
		
		requestMenuDate();
		
	}

	
public  void requestListDate(final int pagenum,int pagesize,String id,final PagingDataObtainedCallback<ProgramListItem> callback){
		
		HttpRequestWrapper.epgJSONRequest(
		"http://epg.ott.cibntv.net/epg/web/program!getCommonMovieList.action?parentCatgId="+id+"&templateId=00080000000000000000000000000044&pageSize="+pagesize+"&pageNumber="+pagenum
		, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject json) {
				try {
					programList = new ArrayList<ProgramListItem>();
					JSONObject jsonobj = json.getJSONObject("programSeries");
					JSONArray jsonarr = jsonobj.getJSONArray("programSerialList");
					JSONObject jsonpageinfo = json.getJSONObject("pageInfo");
					
					pagecount = jsonpageinfo.getInt("totalNumber");
					for(int i=0;i<jsonarr.length();i++){
						ProgramListItem programlistItem = new ProgramListItem();
						JSONObject jsonobject = (JSONObject) jsonarr.get(i);
						programlistItem.setPostImg(jsonobject.getString("image"));
						programlistItem.setPostName(jsonobject.getString("name"));
						programList.add(programlistItem);							
					}	
					
					if(callback == null){	
												
						Gdx.app.postRunnable(uirunable);
						
					}else{
						mcallback = callback;
						if(pagenum == 1){
							((ListGroup) listGroup).setTotalnumber(pagecount);
							((ListGroup) listGroup).updatePageInfo(pagenum, pagecount, 8);
						}
						callback.onDataObtained(pagenum,programList);
					}
					
					
					islock = false;
				} catch (JSONException e) {
					e.printStackTrace();
				}					
				
			}
			
		},new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				callback.onDataObtainedFailed(pagenum);
				Utils.showToast("获取数据失败,请重试...");
				
			}
		});

	}
	
	private void requestMenuDate() {
		String url = "http://epg.ott.cibntv.net/epg/web/program!getCommonMenuList.action?parentCatgId=00050000000000000000000000019035&templateId=00080000000000000000000000000044";
		rd.startJsonHttpGet(url, new Listener4JSONObject() {
			
			@Override
			public void onResponse(JSONObject response) {
				try {
					menuList = new ArrayList<ProgramMenus>();
					JSONObject jsonobj = response.getJSONObject("menus");
					JSONArray jsonarr = jsonobj.getJSONArray("menuList");	
					JSONObject jsontitle = response.getJSONObject("parentMenu");
					mHeadTitle = jsontitle.getString("name");
					for(int i=0;i<jsonarr.length();i++){
						ProgramMenus programenu = new ProgramMenus();
						JSONObject jsonobject = (JSONObject) jsonarr.get(i);
						programenu.setId(jsonobject.getString("id"));
						programenu.setName(jsonobject.getString("name"));
						menuList.add(programenu);							
					}
					
					Gdx.app.postRunnable(uimenurunable);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			
			@Override
			public void onError(String errorMessage) {
				//Utils.showToast("获取栏目数据失败");
				Lg.e(TAG, "getProgramMenus : " + errorMessage);
				NetWorkUtils.handlerError(errorMessage, Page.this);
			}
		});
		
	}

	private Runnable uimenurunable = new Runnable() {
		
		@Override
		public void run() {
			initMenuGroup();
			initHotGroup();
			
		}

		
	};
	
	/**
	 * 初始化页面group 坐标宽高
	 */
	private void initMenuGroup(){
				
		//设置左侧栏布局 并添加背景图片
		menuGroup = new MenuGroup(this,menuList,mHeadTitle);
		menuGroup.setSize(iMenuWidth, iMenuHeight);
		menuGroup.setPosition(iMenuX, iMenuY);
		((MenuGroup) menuGroup).setTitle(mHeadTitle);
		
		addActor(menuGroup);
		
		
	    ((MenuGroup) menuGroup).setOnItemSelectedChangeListenen(new OnItemSelectedChangeListener() {
			
			@SuppressWarnings("static-access")
			@Override
			public void onSelectedChanged(final int pos, final Actor actor) {
				
				try {
					if(menulistitem!=null){
						menulistitem.setFocusImgBg(false);
					}
					menulistitem = (MenuListItem)actor;
					menulistitem.setFocusImgBg(true);
					curID = menuList.get(pos).getId();//获取ID
					if (listGroup != null) {
						((ListGroup) listGroup).setTitle(menuList.get(pos).getName());//获取名称	
					}
					if(task!=null){
						task.cancel();
					}
					if(timer!=null){
						timer.stop();
					}
					
							if (iprepos != pos) {
								islock = true;
								task = new Task() {
									@Override
									public void run() {
										if (pos == 0) {
											// hotlist
											islock = false;
											Utils.showToast("hotlist");
											hotGroup.setVisible(true);
											if (listGroup != null)
												listGroup.setVisible(false);
										} else {
											// commonlist
											hotGroup.setVisible(false);
											if (listGroup != null)
												listGroup.setVisible(true);
											requestListDate(1, 12, curID,
													mcallback);
											Utils.showToast("commonlist");
										}

									}

								};
								timer.scheduleTask(task, 0.6f);
								timer.start();
							}
					iprepos = pos;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}							
			}
		});
	    
	    ((MenuGroup) menuGroup).setFilterOnkeyListener(new OnkeyListener() {
			
			@Override
			public boolean onKey(Actor actor, int keycode) {
				switch(keycode){
					case KeyEvent.KEYCODE_ENTER:
						new ProgramFilterDialog(Page.this).show();
						break;
				}
				return false;
			}
		});
	    
	    menuGroup.setOnkeyListener(new OnkeyListener() {
			
			@Override
			public boolean onKey(Actor actor, int keycode) {
				// TODO Auto-generated method stub
				switch(keycode){
				case KeyEvent.KEYCODE_ENTER:
					TagFilter tagfilter = new TagFilter(Page.this);
					addActor(tagfilter);
					break;
				}
				return false;
			}
		});
	    

		
	}
	
	private void initHotGroup() {
		hotGroup = new HotGroup(this);
		hotGroup.setPosition(500, 200);
		hotGroup.setSize(730, 900);
		addActor(hotGroup);
	}
	
	
	private void initDate() {
		timer = new Timer();
		rd = new RemoteData(getActivity());
		imgbg = new ImageGroup(this,AppGlobalConsts.APP_WIDTH,AppGlobalConsts.APP_HEIGHT,0,0);
		addActor(imgbg);
		//postListadapter = new PostListAdapter(this, null, 12);
	}
	
	private Runnable uirunable = new Runnable() {
		
		@Override
		public void run() {
			if(programList!=null&&programList.size()>1){
				initImageBg(programList.get(1).getPostImg());
			}else{
				initImageBg("http://images.ott.cibntv.net/2015/03/25/yibenshuyizuochenggq.jpg");				
			}
			initProgramList();	
			
		}
	};
	
	/**
	 * 初始化背景图
	 */
	private void initImageBg(String imgurl){
		//设置背景图
		if (imgbg == null) {
			imgbg = new ImageGroup(this,AppGlobalConsts.APP_WIDTH,AppGlobalConsts.APP_HEIGHT,0,0);
		}
		
		if(imgbg!=null&&imgurl!=null&&!imgurl.equals("")){
			imgbg.loadByFilter(imgurl, "",imgbg.getImage());
		}
		imgbg.setSize(AppGlobalConsts.APP_WIDTH,AppGlobalConsts.APP_HEIGHT);
		imgbg.setFocusAble(false);	
		
	}
	
	
	PagingGrid<ProgramListItem> grid;
	private void initProgramList(){
		//listGroup = new ListGroup(this, mHeadTitle, pagecount, 8, 32, );
		if (listGroup != null) {
			((ListGroup) listGroup).setTitle(menuList.get(iprepos).getName());//获取名称	
		}
		addActor(listGroup);//11111
		listGroup.setSize(iListWidth,iListHeight);
		listGroup.setPosition(iListX, iListY);
		((ListGroup) listGroup).updatePageInfo(1,pagecount,8);
		((ListGroup) listGroup).setPagingActionFactory(new IPagingActionFactory<ProgramListItem>() {

			@Override
			public void obtainData(final int page, final int pageSize,
					final PagingDataObtainedCallback<ProgramListItem> callback) {
				if(islock)
					return;
				
				if(((ListGroup) listGroup).getPagenow()<((ListGroup) listGroup).getTotalpage()){
					requestListDate(page,pageSize,curID,callback);
				}else{
					Utils.showToast("已到最底部!");
				}
			}

			@Override
			public void showLoading(boolean b) {
				// 是否正在拉去数据
				Utils.showToast("正在获取数据,请稍后...");
			}
		});
		
		((ListGroup) listGroup).setItemOnclickLinstener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(Actor actor, int pos, AbsListView abslistview) {
				// 跳转详情页 ....
				//111111111
			}
		});
	}
	
	public boolean onKeyDown(int keycode) {
		if(islock&&keycode == KeyEvent.KEYCODE_DPAD_RIGHT){
			return true;
		}
		switch(keycode){
			case KeyEvent.KEYCODE_DPAD_DOWN:
				if(menuGroup.isHasFocus()&&iprepos == menuList.size()-1){
					return true;
				}
				
				break;
		}
		return super.onKeyDown(keycode);
		
	};
	
	@Override
	public void onDispose() {
		super.onDispose();
		App.removePage(this);
	}
}
