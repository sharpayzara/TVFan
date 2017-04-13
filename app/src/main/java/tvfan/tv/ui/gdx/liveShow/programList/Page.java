package tvfan.tv.ui.gdx.liveShow.programList;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.AppGlobalVars;
import tvfan.tv.BasePage;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.dal.HttpResponse.Listener4JSONObject;
import tvfan.tv.dal.models.FilmClassItem;
import tvfan.tv.dal.models.ProgramListItem;
import tvfan.tv.dal.models.ProgramMenus;
import tvfan.tv.lib.Lg;
import tvfan.tv.lib.NetWorkUtils;
import tvfan.tv.ui.gdx.programList.MenuListItem;
import tvfan.tv.ui.gdx.programList.ProgramFilterDialog;
import tvfan.tv.ui.gdx.widgets.ImageGroup;
import tvfan.tv.ui.gdx.widgets.MenuGroup;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;

import tvfan.tv.R;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.actions.RotateByAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.luxtone.lib.utils.Utils;
import com.luxtone.lib.widget.AbsListView;
import com.luxtone.lib.widget.AbsListView.OnItemClickListener;
import com.luxtone.lib.widget.AbsListView.OnItemSelectedChangeListener;
import com.luxtone.lib.widget.PagingDataObtainedCallback;
import com.luxtone.lib.widget.PagingGrid.IPagingActionFactory;


public class Page extends BasePage {

	//左侧栏目按钮布局
	private MenuGroup menuGroup;
	//右侧海报列表布局
	private ListGroup listGroup;
	
	//左侧栏目布局按钮坐标宽高
	private int iMenuWidth = 480;
	private int iMenuHeight = AppGlobalConsts.APP_HEIGHT;
	private int iMenuX = 38;
	private int iMenuY = 0;
	//初始化右侧栏海报列表坐标宽高
	private int iListWidth = 1500;
	private int iListHeight = 870;
	private int iListX = 510;
	private int iListY = 0;
	//图片背景
	private ImageGroup imgbg; 
	private Image backShadow, loadingImage;
	//数据
	private List<ProgramMenus> menuList;//左侧栏目分类列表数据
	private List<ProgramListItem> programList;//右侧栏海报列表页数据
	private List<ProgramListItem> indexProgramList;
	private int pagecount = 0;
	private String curID,parentCatgId;
	private MenuListItem menulistitem;
	
	private String mHeadTitle = "", nodeType = "";
	private Timer timer;
	private Task task;
	private int iprepos = 0;
	
	private boolean islock = false;
	private RemoteData rd;
	private String titleName = "";
	private ProgramFilterDialog pfd;
	private String filterType;
	
	private ArrayList<FilmClassItem> mClassType;
	private ArrayList<FilmClassItem> mCategories;
	private ArrayList<FilmClassItem> mAreas;
	private ArrayList<FilmClassItem> mPeriods;
	private ArrayList<ArrayList<FilmClassItem>> data = new ArrayList<ArrayList<FilmClassItem>>();
	
	private String childID;
	
	private int index = 0;
	private int leftMenuVisbleSize = 8; // 左侧显示栏目条数
	
	private int gridItemSize = 36;
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
	
		curID = bundle.getString("parentCatgId");
		
		
		childID = bundle.getString("childCatgId");
		parentCatgId = curID;
		//20150502 测试
		/*curID = "00050000000000000000000000019614";
		AppGlobalVars.getIns().TEMPLATE_ID = "00080000000000000000000000000050";
		AppGlobalVars.getIns().SERVER_URL.put("EPG", "http://114.247.94.15:8080/epg/web/v40");
		//childID = "追播美剧";
		//childID = "卫视热播";
		//AppGlobalVars.getIns().TEMPLATE_ID = "00080000000000000000000000000050";*/
		//20150502 测试
		
		
		//20150429
		init();
		requestMenuDate();
		//requestFilterDate();
		
		if (TextUtils.isEmpty(childID)) {
			requestDate(1,gridItemSize,"-1","-1","-1","-1",curID,null);
		}
		
		
		//筛选注册消息监听
		registerLocalMsgReceiver(new LocalMsgListener(){
			@Override
			public void onReceive(Context context, Intent intent) {
				Bundle bundle = intent.getExtras();
				classType = bundle.getString("classType");
				typeIndex = bundle.getString("typeIndex");
				zoneIndex = bundle.getString("zoneIndex");
				timeIndex = bundle.getString("timeIndex");
				try {
					filterTitle = bundle.getString("filterTitle");
					((ListGroup) listGroup).setTitle(filterTitle);
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				//Utils.showToast("resive"+"["+classType+"]"+"["+typeIndex+"]"+"["+zoneIndex+"]"+"["+timeIndex+"]");
				requestFilmFilterDate(1,gridItemSize,timeIndex,zoneIndex,typeIndex,classType,parentCatgId,null);
				if (pfd != null) {
					pfd.dismiss();
				}
			}},
			AppGlobalConsts.LOCAL_MSG_FILTER.LIST_FILTER
			
		);
		//筛选注册消息监听 END
	}
	
	String classType,typeIndex,zoneIndex,timeIndex,filterTitle;
	
	
	private void init(){
		timer = new Timer();
		rd = new RemoteData(getActivity());
//		pfd = new ProgramFilterDialog(this);
		mClassType = new ArrayList<FilmClassItem>();
		mCategories = new ArrayList<FilmClassItem>();
		mAreas = new ArrayList<FilmClassItem>();
		mPeriods = new ArrayList<FilmClassItem>();
		
		
		//初始化背景图		
		imgbg = new ImageGroup(this,AppGlobalConsts.APP_WIDTH,AppGlobalConsts.APP_HEIGHT,0,0);
		imgbg.setSize(AppGlobalConsts.APP_WIDTH,AppGlobalConsts.APP_HEIGHT);
		imgbg.setFocusAble(false);
		addActor(imgbg);
		
		backShadow = new Image(this);
		backShadow.setPosition(-50, -50);
		backShadow.setSize(AppGlobalConsts.APP_WIDTH+100, AppGlobalConsts.APP_HEIGHT+100);
		backShadow.setDrawable(findTextureRegion(R.drawable.background_shadow));
		backShadow.setFocusAble(false);
		addActor(backShadow);
		
		loadingImage = new Image(this);
		loadingImage.setPosition(1050, 430);
		loadingImage.setSize(100, 100);
		loadingImage.setDrawable(findTextureRegion(R.drawable.new_foucs));
		loadingImage.setFocusAble(false);
		loadingImage.setOrigin(50, 50);
		loadingImage.clearActions();
		addActor(loadingImage);
	}
	

	/**
	 * 初始化页面group 坐标宽高
	 */
	private void initMenuGroup(){
				
		//设置左侧栏布局 并添加背景图片
		menuGroup = new MenuGroup(this,menuList,titleName);
		menuGroup.setSize(iMenuWidth, iMenuHeight);
		menuGroup.setPosition(iMenuX, iMenuY);
		addActor(menuGroup);
		
		//////////////////
		boolean isEmpty = true;
		if (TextUtils.isEmpty(childID)) {
			((MenuGroup) menuGroup).setGridSelection(2);
			try {
				((ListGroup) listGroup).setTitle(titleName = menuList.get(2).getName());//获取名称
			} catch (Exception e) {
			}
		} else {
			for (int i = 0; i < menuList.size(); i++) {
				if(childID.equals(menuList.get(i).getId())){
					((MenuGroup) menuGroup).setGridSelection(i);
					curID = menuList.get(i).getId();
					try {
						((ListGroup) listGroup).setTitle(titleName = menuList.get(i).getName());//获取名称
					} catch (Exception e2) {
					}
					isEmpty = false;
					//添加向下向上箭头 END
				}
			}
			if (isEmpty) {
				((MenuGroup) menuGroup).setGridSelection(2);
				try {
					((ListGroup) listGroup).setTitle(titleName = menuList.get(2).getName());//获取名称
				} catch (Exception e2) {
				}
			}
		}
		//////////////////
		
	    ((MenuGroup) menuGroup).setOnItemSelectedChangeListenen(new OnItemSelectedChangeListener() {
			
			@Override
			public void onSelectedChanged(final int pos, Actor actor) {
				
				try {
					
					/////////////// 上下箭头
					if (index < pos) { //向下
						index = pos;

					} else { //向上
						index = pos;
					}
					///////////////
					
					if(menulistitem!=null){
						menulistitem.setFocusImgBg(false);
					}
					menulistitem = (MenuListItem)actor;
					menulistitem.setFocusImgBg(true);
					curID = menuList.get(pos).getId();//获取ID
					
					if(task!=null){
						task.cancel();
					}
					if(timer!=null){
						timer.stop();
					}
					
					if(iprepos != pos && pos != 0){	
						islock = true;
						task = new Task() {
							
							@Override
							public void run() {	
								if(((ListGroup) listGroup)!=null){
									((ListGroup) listGroup).setTitle(titleName = menuList.get(pos).getName());//获取名称
								}
								//20150429
								requestDate(1,gridItemSize,"-1","-1","-1","-1",curID,null);	
							}
						};
						timer.scheduleTask(task, 1f);
						timer.start();
					} else islock = false;
					iprepos = pos;
				} catch (Exception e) {
					e.printStackTrace();
				}							
			}
		});
	    
	    
	    
	    
 
	    
	    //左侧栏目按钮点击事件	
	    ((MenuGroup) menuGroup).setItemOnClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(Actor paramActor, int paramInt,
					AbsListView paramAbsListView) {
				if (paramInt == 0) {//搜索
					Bundle op = new Bundle();
					op.putString("parentCatgId", parentCatgId);
					doAction(ACTION_NAME.OPEN_SEARCH,op);
				} 
				
				
				/*else if (paramInt == 1) {//筛选
					pfd = new ProgramFilterDialog(Page.this);
					if (pfd != null) {
						data.clear();
						mClassType.clear();
						//筛选第一列，写死
						FilmClassItem fci1 = new FilmClassItem();
						fci1.setTitle("最新");
						fci1.setValue("1");
						mClassType.add(fci1);
						FilmClassItem fci2 = new FilmClassItem();
						fci2.setTitle("最热");
						fci2.setValue("0");
						mClassType.add(fci2);
						FilmClassItem fci3 = new FilmClassItem();
						fci3.setTitle("评分");
						fci3.setValue("2");
						mClassType.add(fci3);
						data.add(mClassType);
						//筛选第一列，写死 END
						
						
						FilmClassItem fci = new FilmClassItem();
						fci.setTitle("全部");
						fci.setValue("-1");
						
						if (mCategories.size()>0) {
							if (!mCategories.get(0).getTitle().equalsIgnoreCase("全部")) {
								mCategories.add(0, fci);
							}
							data.add(mCategories);
						}
						if (mAreas.size()>0) {
							if (!mAreas.get(0).getTitle().equalsIgnoreCase("全部")) {
								mAreas.add(0, fci);
							}
							data.add(mAreas);
						}
						if (mPeriods.size()>0) {
							if (!mPeriods.get(0).getTitle().equalsIgnoreCase("全部")) {
								mPeriods.add(0, fci);
							}
							data.add(mPeriods);
						}
						
						pfd.setData(data);
						pfd.show();
					}
				}*/
			}
		});
	    
	   
	   
	}
	
	private void refreshMenuList(){
		((MenuGroup) menuGroup).refreshMenuList(this,menuList);
	}
	
	private void refreshImgBg(String imgurl){
		if(imgbg!=null&&imgurl!=null&&!imgurl.equals("")){
			imgbg.loadByFilter(imgurl, "",imgbg.getImage());
		}
	}

	private void initProgramList(final boolean isFilter){
		if (indexProgramList != null ){
			indexProgramList.clear();
		} else {
			indexProgramList = new ArrayList<ProgramListItem>();
		}
		if (listGroup == null) {
			listGroup = new tvfan.tv.ui.gdx.liveShow.programList.ListGroup(this,titleName,pagecount,9,gridItemSize);
		} else {
			listGroup.updateGridAdapter(indexProgramList);
		}
		
		listGroup.setSize(iListWidth,iListHeight);
		listGroup.setPosition(iListX, iListY);
		addActor(listGroup);

		((ListGroup) listGroup).toBack();
		backShadow.toBack();
		imgbg.toBack();
		
		((ListGroup) listGroup).updatePageInfo(1,pagecount,9);
		((ListGroup) listGroup).setPagingActionFactory(new IPagingActionFactory<ProgramListItem>() {

			@Override
			public void obtainData(final int page, final int pageSize,
					final PagingDataObtainedCallback<ProgramListItem> callback) {
				if(islock)
					return;
				if(page*pageSize <= pagecount+pageSize){
					if (isFilter) {
					requestFilmFilterDate(page,gridItemSize,timeIndex,zoneIndex,typeIndex,classType,parentCatgId,callback);	
					} else
						
						if (page == 1) {
							((ListGroup) listGroup).setTotalnumber(pagecount);
							((ListGroup) listGroup).updatePageInfo(1, pagecount, 9);
							
							if(callback!=null){
								indexProgramList.addAll(programList);
								callback.onDataObtained(1,programList);						
							}
							
						} else	
						
						requestDate(page,pageSize,"-1","-1","-1","-1",curID,callback);
					if (pagecount == 0) {
						Utils.showToast("暂无数据");
					}
				}else{
					Utils.showToast("已到最底部!");
				}
			}

			@Override
			public void showLoading(boolean b) {
				// 是否正在拉去数据
				//Utils.showToast("正在获取数据,请稍后...");
			}
		});
		
		((ListGroup) listGroup).setItemOnclickLinstener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(Actor actor, int pos, AbsListView abslistview) {
				// 跳转详情页 ....
				Bundle op = new Bundle();
				op.putString("programSeriesId", indexProgramList.get(pos).getId());
				doAction(ACTION_NAME.OPEN_LIVE_SHOW_DETAIL, op);
			}
		});
		
		
		((ListGroup) listGroup).obtainData();
	}

	/**
	 * 获取电影列表
	 * @param pagenum
	 * @param pagesize
	 * @param callback
	 */
	
	public  void requestDate(final int pagenum,int pagesize , String year, String area, String type, String classType
			,String id,final PagingDataObtainedCallback<ProgramListItem> callback){
		RotateByAction rotateAction = Actions.rotateBy(360, 0.8f);
		RepeatAction epeatAction = Actions.repeat(RepeatAction.FOREVER,  
				rotateAction);
		loadingImage.setVisible(true);
		loadingImage.toFront();
		loadingImage.clearActions();
		loadingImage.addAction(epeatAction);
		
		RemoteData remoteData = new RemoteData(getActivity());
		remoteData.getProgramList(new Listener4JSONObject() {
			
			@Override
			public void onResponse(JSONObject response) {
				if (isDisposed()) {
					return;
				}
				
				task = new Task() {
					
					@Override
					public void run() {	
						loadingImage.clearActions();
						loadingImage.setVisible(false);
					}
				};
				timer.scheduleTask(task, 1f);
				timer.start();
				
				try {
					programList = new ArrayList<ProgramListItem>();
					JSONObject jsonobj = response.getJSONObject("parentMenu");
					JSONArray jsonarr = response.getJSONArray("programList");					
					pagecount = jsonobj.getInt("totalNumber");
					for(int i=0;i<jsonarr.length();i++){
						ProgramListItem programlistItem = new ProgramListItem();
						JSONObject jsonobject = (JSONObject) jsonarr.get(i);
						programlistItem.setId(jsonobject.getString("id"));
						programlistItem.setPostImg(jsonobject.getString("image"));
						programlistItem.setPostName(jsonobject.getString("name"));
						programlistItem.setNews(Boolean.getBoolean(jsonobject.getString("isNews")));
						programlistItem.setCreateDate(jsonobject.getString("createDate"));
						programlistItem.setCornerType(jsonobject.getString("cornerType"));
						programlistItem.setCornerPrice(jsonobject.getString("cornerPrice"));
						programList.add(programlistItem);							
					}	
					if(callback == null){													
						Gdx.app.postRunnable(refreshImgBgRunable);						
					}else{
						
						if(pagenum == 1){
							((ListGroup) listGroup).setTotalnumber(pagecount);
							((ListGroup) listGroup).updatePageInfo(pagenum, pagecount, 9);
						}
						if(callback!=null){
							indexProgramList.addAll(programList);
							callback.onDataObtained(pagenum,programList);						
						}
					}
					islock = false;
				}  catch (JSONException e) {
					e.printStackTrace();
				}	
			}

			@Override
			public void onError(String errorMessage) {
				if(callback!=null){
					callback.onDataObtainedFailed(pagenum);
				}
				
				Utils.showToast("获取海报页数据失败,请重试...");
			}
			
			
		}, id, pagenum, pagesize, year, area, type, classType, AppGlobalVars.getIns().TEMPLATE_ID);
		

	}
	
	
	/**
	 * 获取筛选列表
	 * @param pagenum
	 * @param pagesize
	 * @param callback
	 */
	
	public  void requestFilmFilterDate(final int pagenum,int pagesize , String year, String area, String type, String classType
			,String id,final PagingDataObtainedCallback<ProgramListItem> callback){
		RotateByAction rotateAction = Actions.rotateBy(360, 0.8f);
		RepeatAction epeatAction = Actions.repeat(RepeatAction.FOREVER,  
				rotateAction);
		loadingImage.toFront();
		loadingImage.setVisible(true);
		loadingImage.clearActions();
		loadingImage.addAction(epeatAction);
		
		
		RemoteData remoteData = new RemoteData(getActivity());
		remoteData.getFilterProgramList(new Listener4JSONObject() {
			
			@Override
			public void onResponse(JSONObject response) {
				if (isDisposed()) {
					return;
				}
				
				task = new Task() {
					@Override
					public void run() {	
						loadingImage.clearActions();
						loadingImage.setVisible(false);
					}
				};
				timer.scheduleTask(task, 1f);
				timer.start();
				
				if(response==null || response.toString().replace(" ", "").equals("{}")){
					Utils.showToast("筛选数据结果为空!");
					Gdx.app.postRunnable(new Runnable() {
						
						@Override
						public void run() {
							loadingImage.clearActions();
							loadingImage.setVisible(false);
						}
					});
					return;
				}
			
				try {
					programList = new ArrayList<ProgramListItem>();
					JSONObject jsonobj = response.getJSONObject("parentMenu");
					JSONArray jsonarr = response.getJSONArray("programList");					
					pagecount = jsonobj.getInt("totalNumber");
					for(int i=0;i<jsonarr.length();i++){
						ProgramListItem programlistItem = new ProgramListItem();
						JSONObject jsonobject = (JSONObject) jsonarr.get(i);
						programlistItem.setId(jsonobject.getString("id"));
						if(jsonobject.has("image"))
							programlistItem.setPostImg(jsonobject.getString("image"));
						else
							programlistItem.setPostImg("");
						programlistItem.setPostName(jsonobject.getString("name"));
						programlistItem.setNews(Boolean.getBoolean(jsonobject.getString("isNews")));
						programlistItem.setCreateDate(jsonobject.getString("createDate"));
						programlistItem.setCornerType(jsonobject.getString("cornerType"));
						programlistItem.setCornerPrice(jsonobject.getString("cornerPrice"));
						programList.add(programlistItem);							
					}	
					if(callback == null){													
						Gdx.app.postRunnable(refreshFilterImgBgRunable);						
					}else{
						
						if(pagenum == 1){
							((ListGroup) listGroup).setTotalnumber(pagecount);
							((ListGroup) listGroup).updatePageInfo(pagenum, pagecount, 9);
						}
						if(callback!=null){
							indexProgramList.addAll(programList);
							callback.onDataObtained(pagenum,programList);						
						}
					}
					islock = false;
				}  catch (JSONException e) {
					e.printStackTrace();
					Utils.showToast("筛选数据获取失败");
				}	
			}

			@Override
			public void onError(String errorMessage) {
				if(callback!=null){
					callback.onDataObtainedFailed(pagenum);
				}
				
				Utils.showToast("获取海报页数据失败,请重试...");
			}
			
			
		}, id, pagenum, pagesize, year, area, type, classType, AppGlobalVars.getIns().TEMPLATE_ID);
		

	}
	
	
	/**
	 * 获取列表分类
	 * @param pagenum
	 * @param pagesize
	 * @param callback
	 */
	
	public  void requestMenuDate(){
		RemoteData remoteData = new RemoteData(getActivity());
		remoteData.getProgramMenus(new Listener4JSONObject() {
			
			@Override
			public void onResponse(JSONObject response) {
				if (isDisposed()) {
					return;
				}
				try {
					JSONObject json = response;
					menuList = new ArrayList<ProgramMenus>();
					JSONArray jsonarr = json.getJSONArray("menuList");	
					JSONObject jsontitle = json.getJSONObject("parentMenu");
					mHeadTitle = jsontitle.getString("name");
					nodeType = jsontitle.getString("nodeType");
					
					//添加搜索 筛选按钮
					ProgramMenus searchButton = new ProgramMenus();
					searchButton.setId(parentCatgId);
					searchButton.setName("搜索");
					menuList.add(searchButton);
					
					/*ProgramMenus filterButton = new ProgramMenus();
					filterButton.setId(parentCatgId);
					filterButton.setName("筛选");
					menuList.add(filterButton);*/
					//添加搜索 筛选按钮
					
					//添加全部按钮
					ProgramMenus allButton = new ProgramMenus();
					allButton.setId(parentCatgId);
					allButton.setName("全部");
					menuList.add(allButton);
					//添加全部按钮
					
					
					for(int i=0;i<jsonarr.length();i++){
						ProgramMenus programenu = new ProgramMenus();
						JSONObject jsonobject = (JSONObject) jsonarr.get(i);
						programenu.setId(jsonobject.getString("id"));
						programenu.setName(jsonobject.getString("name"));
						programenu.setNodeType(jsonobject.getString("nodeType"));
						programenu.setAction(jsonobject.getString("action"));
						menuList.add(programenu);	
						
						//筛选类型 赋值
						filterType = jsonobject.getString("nodeType");
					}	
					Gdx.app.postRunnable(refreshMenuListRunable);
					requestFilterDate();									
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(String errorMessage) {
				//Utils.showToast("获取分类栏目数据失败,请重试...");
				Lg.e(TAG, "getProgramMenus : " + errorMessage);
				NetWorkUtils.handlerError(errorMessage, Page.this);
			}
		}, curID, AppGlobalVars.getIns().TEMPLATE_ID) ;
	}

	
	/**
	 * 获取筛选
	 * @param pagenum
	 * @param pagesize
	 * @param callback
	 */
	
	public  void requestFilterDate(){
		RemoteData remoteData = new RemoteData(getActivity());
		remoteData.getProgramFilter(new Listener4JSONObject() {
			
			@Override
			public void onResponse(JSONObject response) {
				if (isDisposed()) {
					return;
				}
				try {
					JSONObject json = response;
					JSONArray typeArr = json.getJSONArray("type");	
					JSONArray zoneArr = json.getJSONArray("zone");	
					JSONArray timeArr = json.getJSONArray("time");
					for(int i=0;i<typeArr.length();i++){
						FilmClassItem fci = new FilmClassItem();
						JSONObject jsonobject = (JSONObject) typeArr.get(i);
						fci.setTitle(jsonobject.getString("title"));
						fci.setValue(jsonobject.getString("value"));
						mCategories.add(fci);							
					}
					
					for(int i=0;i<zoneArr.length();i++){
						FilmClassItem fci = new FilmClassItem();
						JSONObject jsonobject = (JSONObject) zoneArr.get(i);
						fci.setTitle(jsonobject.getString("title"));
						fci.setValue(jsonobject.getString("value"));
						mAreas.add(fci);							
					}
					
					for(int i=0;i<timeArr.length();i++){
						FilmClassItem fci = new FilmClassItem();
						JSONObject jsonobject = (JSONObject) timeArr.get(i);
						fci.setTitle(jsonobject.getString("title"));
						fci.setValue(jsonobject.getString("value"));
						mPeriods.add(fci);					
					}
					
					//Gdx.app.postRunnable(refreshMenuListRunable);
														
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(String errorMessage) {
				Utils.showToast("获取筛选数据失败,请重试...");
			}
		}, 	nodeType, AppGlobalVars.getIns().TEMPLATE_ID) ;
		
		//先写死 filterType = "movie"
	}
	
	
	private Runnable refreshImgBgRunable = new Runnable() {
		
		@Override
		public void run() {
			if(programList!=null && programList.size()>=1){
				refreshImgBg(programList.get(0).getPostImg());
			}else{
				//				
			}
			
			initProgramList(false);	
		}
	};
	
	
	private Runnable refreshFilterImgBgRunable = new Runnable() {
		
		@Override
		public void run() {
			if(programList!=null && programList.size()>1){
				refreshImgBg(programList.get(0).getPostImg());
			}else{
				//				
			}
			
			initProgramList(true);	
		}
	};
	
	private Runnable refreshMenuListRunable = new Runnable() {
		
		@Override
		public void run() {
			
			initMenuGroup();
			if (!TextUtils.isEmpty(childID)) {
				requestDate(1,gridItemSize,"-1","-1","-1","-1",curID,null);
			}
			
		}
	};
	
	public boolean onKeyDown(int keycode) {
		if (keycode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			isRight = true;
		} else {
			isRight = false;
		}
		
		if(islock&&keycode == KeyEvent.KEYCODE_DPAD_RIGHT){
			return true;
		}
		
		if(keycode == KeyEvent.KEYCODE_DPAD_RIGHT && loadingImage.isVisible()){
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
		unregisterLocalMsgReceiver(AppGlobalConsts.LOCAL_MSG_FILTER.LIST_FILTER);
	}

}
