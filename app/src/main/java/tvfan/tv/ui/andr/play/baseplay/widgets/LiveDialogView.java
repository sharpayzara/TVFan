package tvfan.tv.ui.andr.play.baseplay.widgets;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.luxtone.lib.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import tvfan.tv.App;
import tvfan.tv.AppGlobalVars;
import tvfan.tv.R;
import tvfan.tv.crack.CrackCompleteListener;
import tvfan.tv.crack.CrackResult;
import tvfan.tv.crack.ParserUtil;
import tvfan.tv.dal.HttpResponse.Listener4JSONArray;
import tvfan.tv.dal.HttpResponse.Listener4JSONObject;
import tvfan.tv.dal.PlayRecordHelpler;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.dal.models.LiveExtraBean;
import tvfan.tv.dal.models.LiveItemBean;
import tvfan.tv.dal.models.MPlayRecordInfo;
import tvfan.tv.dal.models.PlayUrlBean;
import tvfan.tv.lib.DisplayUtil;
import tvfan.tv.lib.Lg;
import tvfan.tv.ui.andr.play.baseplay.dateparser.DataParser;
import tvfan.tv.ui.andr.play.baseplay.dateparser.DataParser.DATA_KEY;
import tvfan.tv.ui.andr.play.baseplay.interfaces.ILiveChannel;
import tvfan.tv.ui.andr.play.baseplay.ui.BasePlayerActivity;
import tvfan.tv.ui.andr.play.baseplay.utils.LogUtils;
import tvfan.tv.ui.andr.play.liveplay.Page;

public class LiveDialogView extends Dialog implements CrackCompleteListener {
	private static final String TAG = "LiveDialogView";
	private int i = 0;
	private RelativeLayout mrelayout, distinlayout,/* 地方台 */ratiolayout,/* 频道 */
	// sourcelayout,/* 源 */
			dramalayout;/* 节目 */
	private ListView distinlstview,/* 地方台 */ratiolistview,/* 频道 */
	// sourcelistview,/* 源 */
			dramalistview;/* 节目 */
	private Context mcontext;
	private Handler timerhd;
	private Window window = null;
	private LiveListAdapter distinadpater, raiadapter, dramadapter;
	//         idisindex： 记录播放的台在哪个分类里面（收藏、cctv、湖南）
	//        iraiaindex: 记录在总的分类里面的某个具体的台的position
	//        idramaindex：记录当前播放节目的position
	private int idisindex = 2, iraiaindex = 0, idramaindex = 0, ratioFocusIndex = 0, iraiavisible = 0;
	private final static int iDisId = 1, iRaiaId = 2, iDramaId = 3;
	private int currentPlayIdisindex, currentPlayIraiaindex;
	private ImageView imgarrow, imgarrbg;
	private ArrayList<LiveItemBean> stationlist = new ArrayList<LiveItemBean>();//第一列表的数据集合
	private ArrayList<LiveItemBean> channellist = new ArrayList<LiveItemBean>();//第二列表的数据集合
	private ArrayList<LiveItemBean> programlist = new ArrayList<LiveItemBean>();//要播放台的相关节目的数据集合（id、播放时间、名称等）
	private ArrayList<PlayUrlBean> sourcelist = new ArrayList<PlayUrlBean>();//要播放的台的来源的相关集合
	HashMap<String, ArrayList<LiveItemBean>> mapOfChannellist = new HashMap<String, ArrayList<LiveItemBean>>();
	private String curchannel = "";
	private String curNo = "";
	private String curprogram = "", curTime = "";
	private String nextprogram = "", nextTime = "";
	private String curchannelId = "";
	private String programPlayId = "";
	private String programStartTime = "";
	private boolean isleve2focused = false, isLevle1Click = false;
	public static ILiveChannel milivechannel;
	private LiveExtraBean mlivextbean;
	private boolean isfirst = true;
	private boolean isfirstLoadProgram = true;
	private Handler posthd;
	private Handler mHandler;
	// private MyLiveService liveService;

	public LiveDialogView(Context context, int theme, LiveExtraBean livextbean,
			Handler posthd) {
		super(context, theme);
		mcontext = context;
		timerhd = new Handler();
		this.posthd = posthd;
		isfirst = true;
		mlivextbean = livextbean;
		initLayout();
		setContentView(mrelayout);
		// requestStationData();
		requestChannelInfo();
	}

	private Handler getPsotHd() {
		return posthd;

	}

	private void initLayout() {
		mrelayout = new RelativeLayout(mcontext);
		setPostion(RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT, mrelayout, 0, 0, 0, 0);
	}

	private void stationChoose(boolean isup) {
		if (isup) {
			idisindex = idisindex - 1;
			if (idisindex < 0) {
				idisindex = 0;
			}
		} else {
			idisindex = idisindex + 1;
			if (idisindex >= distinlstview.getCount()) {
				idisindex = distinlstview.getCount() - 1;
			}
		}

	}

	public void setLivextbean(LiveExtraBean livextbean) {
		mlivextbean = livextbean;
	}

	private TextView noFav;
	// 创建最左侧的列表
	public void addDistinList(ArrayList<LiveItemBean> lst) {
		distinlayout = new RelativeLayout(mcontext);
		distinlayout.setBackgroundResource(R.drawable.lb_left1);
		setPostion(200, 720, distinlayout, 0, 0, 0, 0);
		
		distinlstview = new ListView(mcontext);
		distinlstview.setSmoothScrollbarEnabled(true);
		distinlstview.setSelector(R.drawable.lb_left1foucs1);
		distinlstview.setFocusable(true);
		distinlstview.setId(iDisId);
		distinlstview.setNextFocusRightId(iRaiaId);
		distinlayout.setFocusable(false);
		distinadpater = new LiveListAdapter(mcontext, lst, "");

		distinadpater.notifyDataSetChanged();
		distinlstview.setAdapter(distinadpater);
		distinlstview.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean focus) {
				if (focus) {
					// 设置2级菜单的显示状态为false
					isleve2focused = false;
					if (dramalayout != null)
						dramalayout.setVisibility(View.INVISIBLE);

					if (imgarrow != null)
						// 设置箭头为向右
						changeTurnArrow(true);
					if (ratiolistview != null) {
						// 设置2级菜单被选中时的背景图片
						ratiolistview.setSelector(R.drawable.choosefilter);
					}
					// 设置1级菜单选中的背景图片
					distinlstview.setSelector(R.color.live_list_selector);
				} else {
					isleve2focused = false;
					if (dramalayout != null) {
						dramalayout.setVisibility(View.INVISIBLE);
					}
					changeTurnArrow(true);
				}
			}
		});

		/**
		 * 设置1级菜单条目按下的事件监听
		 */
		distinlstview.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				switch (keyCode) {
				case KeyEvent.KEYCODE_DPAD_DOWN:
				case 2012:
				case KeyEvent.KEYCODE_CHANNEL_DOWN:
					if (event.getAction() == KeyEvent.ACTION_DOWN) {
						stationChoose(false);
						// 把listview定位到制定位置
						distinlstview.setSelection(idisindex);
					}

					break;
				case KeyEvent.KEYCODE_DPAD_UP:
				case 2006:
				case KeyEvent.KEYCODE_CHANNEL_UP:
					if (event.getAction() == KeyEvent.ACTION_DOWN) {
						stationChoose(true);
						distinlstview.setSelection(idisindex);
					}
					break;
				case KeyEvent.KEYCODE_DPAD_RIGHT:
					if(ratiolistview != null){
						new Handler().post(new Runnable() {
							@Override
							public void run() {
								if(iraiavisible == 0){
									if(ratiolistview.getCount()>7)
										iraiavisible = iraiaindex;
								}
								else
									iraiavisible++;
								if(isLevle1Click){
									isLevle1Click = false;
									iraiavisible = 0;
								}
								ratiolistview.setSelection(iraiavisible);
							}
						});
					}
				}
				return false;
			}

		});
		addListView(distinlayout, distinlstview, lst, distinItemLinstener,
				distinitemselectListener, 45);
	}

	/**
	 * 一级菜单列表的点击监听
	 */
	private OnItemClickListener distinItemLinstener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int pos, long arg3) {
			idisindex = pos;
			isleve2focused = false;
			isLevle1Click = true;
			if(noFav != null && noFav.isShown())
				noFav.setVisibility(View.GONE);
			TurnStation(pos);
		}

	};
	// 获取焦点监听
	private OnItemSelectedListener distinitemselectListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int ipos,
				long arg3) {
			isleve2focused = false;
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	};

	// 选择频道
	private void channelChoose(boolean isAdd) {
		if (isAdd) {
			iraiaindex = iraiaindex - 1;
			if (iraiaindex < 0) {
				iraiaindex = 0;
			}
		} else {
			iraiaindex = iraiaindex + 1;
			if (iraiaindex >= ratiolistview.getCount()) {
				iraiaindex = ratiolistview.getCount() - 1;
			}
		}
	}

	// 创建左侧第二列表（相关标签下的具体台的列表）
	public void addRatioList(ArrayList<LiveItemBean> lst) {
		ratiolayout = new RelativeLayout(mcontext);
		imgarrow = new ImageView(mcontext);
		imgarrow.setBackgroundResource(R.drawable.turnright);
		setPostion(30, 30, imgarrow, 360, 460, 0, 0);
		imgarrow.setVisibility(View.VISIBLE);

		imgarrbg = new ImageView(mcontext);
		imgarrbg.setBackgroundResource(R.drawable.lb_left3);
		setPostion(30, 720, imgarrbg, 0, 460, 0, 0);
		imgarrbg.setVisibility(View.VISIBLE);

		ratiolayout.setBackgroundResource(R.drawable.lb_left12);
		setPostion(260, 720, ratiolayout, 0, 200, 0, 0);
		noFav = new TextView(mcontext);
		noFav.setText("暂无收藏节目");
		noFav.setVisibility(View.GONE);
		noFav.setHeight(getFitValue(720));
		noFav.setWidth(getFitValue(260));
		noFav.setGravity(Gravity.CENTER);
		noFav.setTextSize(App.adjustFontSize(25));
		ratiolayout.addView(noFav);
		
		ratiolistview = new ListView(mcontext);
		ratiolistview.setDividerHeight(0);
		ratiolistview.setDivider(null);
		ratiolistview.setFocusable(true);
		ratiolistview.setSelector(R.drawable.lb_left2foucs1);
		ratiolistview.setId(iRaiaId);
		ratiolistview.setNextFocusLeftId(iDisId);
		ratiolistview.setNextFocusRightId(iDramaId);
		ratiolayout.setFocusable(false);
		ratiolistview.setSmoothScrollbarEnabled(true);
		ratiolistview.requestFocus();
		raiadapter = new LiveListAdapter(mcontext, lst, "line");
		ratiolistview.setAdapter(raiadapter);
		ratiolistview.setSelection(iraiaindex);
		View itme = ratiolistview.getAdapter().getView(0, null, ratiolistview);
		itme.measure(0, 0);
		itmeHeight = itme.getMeasuredHeight();
		Lg.i(TAG, "条目高度：" + itmeHeight);

		// 2级菜单焦点改变事件监听
		ratiolistview.setOnFocusChangeListener(new OnFocusChangeListener() {
			// LivePlayControlLayout playLayout = new LivePlayControlLayout(
			// mcontext, mrelayout);

			@Override
			public void onFocusChange(View arg0, boolean focus) {
				if (focus) {

					if (dramalistview != null) {
						dramalistview.setSelector(R.drawable.choosefilter);

					}
					if (distinlstview != null) {
						distinlstview.setSelector(R.drawable.choosefilter);
					}
					// 当2级菜单获取焦点时,底部布局显示
					milivechannel.showCurrentProgramInfo(true);
					//ratiolistview.sets
					ratiolistview.setSelector(R.color.live_list_selector);
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							// 当前焦点在2级菜单上的状态
							isleve2focused = true;
						}
					}, 500l);
				} else {
					isleve2focused = false;
				}
			}
		});

		ratiolistview.setOnKeyListener(new View.OnKeyListener() {

			// TODO listview的onkeylistener事件
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				Lg.i(TAG, "按键事件起效 onKey:" + keyCode);
				switch (keyCode) {
				case KeyEvent.KEYCODE_DPAD_RIGHT:
					if (isleve2focused && dramalayout != null
							&& dramalayout.getVisibility() != View.VISIBLE) {
						
						dramalayout.setVisibility(View.VISIBLE);
						changeTurnArrow(false);

					}
					break;

				case KeyEvent.KEYCODE_DPAD_UP:
				case KeyEvent.KEYCODE_CHANNEL_UP:
				case 2006:// 频道减
					switch (event.getAction()) {
					case KeyEvent.ACTION_DOWN:
						channelChoose(true);
						break;

					default:
						break;
					}

					break;

				case KeyEvent.KEYCODE_DPAD_DOWN:
				case KeyEvent.KEYCODE_CHANNEL_DOWN:
				case 2012:// 频道加
					switch (event.getAction()) {
					case KeyEvent.ACTION_DOWN:
						ratiolistview.setSelection(iraiaindex);
						channelChoose(false);
						break;

					default:
						break;
					}
					break;

				case KeyEvent.KEYCODE_0:
				case KeyEvent.KEYCODE_1:
				case KeyEvent.KEYCODE_2:
				case KeyEvent.KEYCODE_3:
				case KeyEvent.KEYCODE_4:
				case KeyEvent.KEYCODE_5:
				case KeyEvent.KEYCODE_6:
				case KeyEvent.KEYCODE_7:
				case KeyEvent.KEYCODE_8:
				case KeyEvent.KEYCODE_9:
					if (event.getAction() == KeyEvent.ACTION_DOWN) {
						onNumKeyDown((keyCode - 7) + "");
					}
					break;
				}

				return false;
			}
		});

		ratiolistview.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				iraiavisible = firstVisibleItem;
			}
		});
		addListView(ratiolayout, ratiolistview, lst, ratioItemLinstener,
				onratioselectListener, 45);
		mrelayout.addView(imgarrbg);
		mrelayout.addView(imgarrow);

	}

	private boolean isSameRatioFocus = false;
	/**
	 * 刷新
	 */
	private void refreshDramaListView(){
		if (channellist != null && channellist.size() > ratioFocusIndex) {
			// 频道id
			String channelId = channellist.get(ratioFocusIndex).getChannealid();
			
			refreshProgramInfo(channelId);
		}
	}
	
	/**
	 * 刷新三级列表时请求数据
	 * @param channelId
	 */
	private void refreshProgramInfo(String channelId){
		RemoteData remoteData = new RemoteData(mcontext);
		remoteData.getLiveProgram(new Listener4JSONObject() {
			
			@Override
			public void onResponse(JSONObject response) {
				JSONObject content;
				try {
					if (response.optInt("code") == 0){
						content = response.optJSONObject("content");
						ArrayList<LiveItemBean> programlist = new ArrayList<LiveItemBean>();
						DataParser.getProgramList(content, programlist);
						if(programlist.size() == 0)
							if (noProgram != null)
								noProgram.setVisibility(View.VISIBLE);
						else
							if (noProgram != null)
							noProgram.setVisibility(View.INVISIBLE);
						dramadapter = new LiveListAdapter(mcontext, programlist, "three");
						dramadapter.setProgramId("");
						freshListView(dramalistview, dramadapter);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			@Override
			public void onError(String httpStatusCode) {
				if(BasePlayerActivity.playType!=2){
					Utils.showToast(R.string.no_internet_error);
				}
				((Activity)mcontext).finish();
				
			}
		}, channelId);
	}
	/**
	 * 2级菜单点击事件设置
	 */
	private OnItemClickListener ratioItemLinstener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int pos, long arg3) {
			changeChannel(pos);
		}
	};

	/**
	 * 二级菜单item选择的监听
	 */
	private OnItemSelectedListener onratioselectListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int ipos,
				long arg3) {
			ratioFocusIndex = ipos;
			refreshDramaListView();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	};
	/**
	 * 点击第二栏（或点击上下键），切换频道时调用
	 * 
	 * @param pos
	 *            频道所处的listview的索引，上下键换频道时，-1表示向上，-2表示向下
	 */
	public void changeChannel(int pos) {
		if(posthd!=null){
			posthd.removeMessages(Page.SWITCH_CHANNEL);
		}
		ipos =0;
		if (resetSourceIpos != null)
			resetSourceIpos.onResetSourceIposListener();
		if (pos >= 0) {//在点击左侧菜单栏的时候调用
			iraiaindex = pos;
			setChannel(pos);
			ratiolistview.requestFocus();
			TurnChannel(pos, true);
			currentPlayIdisindex = idisindex;
			currentPlayIraiaindex = iraiaindex;

		} else if (pos == -1) {
			// 按上键换频道
			if (currentPlayIraiaindex != 0) {
				TurnStation(currentPlayIdisindex); // 将频道分类和频道列表切到之前的位置
				currentPlayIraiaindex--;
				setChannel(currentPlayIraiaindex);
				// ratiolistview.requestFocus();
				TurnChannel(currentPlayIraiaindex, false);

			} else { // 当前频道已经是频道分类的第一个，若再换上一集，则进入上一个频道分类中
				if (currentPlayIdisindex >= 2) {
					currentPlayIdisindex--;
					changeChannelWithKey(true);
				} else { // 切到除了“收藏”和“全部”之外的第一个频道分类时，循环到最后一个视频分类
					currentPlayIdisindex = stationlist.size() - 1;
					changeChannelWithKey(true);
				}
			}
			iraiaindex = currentPlayIraiaindex;
			idisindex = currentPlayIdisindex;
		} else if (pos == -2) {
			// 按下键换频道
			if (currentPlayIraiaindex != channellist.size() - 1) {
				currentPlayIraiaindex++;
				TurnStation(currentPlayIdisindex); // 将频道分类和频道列表切到之前的位置
				setChannel(currentPlayIraiaindex);
				ratiolistview.requestFocus();
				TurnChannel(currentPlayIraiaindex, false);
			} else {
				if (currentPlayIdisindex < stationlist.size() - 1) {// 如果是当前分类最后一个，换到下一个分类的第一个
					currentPlayIdisindex++;
					changeChannelWithKey(false);
				} else { // 循环到除“收藏”和“全部”之外的第一个分类的第一个
					currentPlayIdisindex = 2;
					changeChannelWithKey(false);
				}

			}
			iraiaindex = currentPlayIraiaindex;
			idisindex = currentPlayIdisindex;
		} else {

		}
	}

	/**
	 * 用上下键切换频道，处理跨越频道分类的情况
	 * 
	 * @param up
	 *            ture为按上键，false为按下键
	 */
	private void changeChannelWithKey(boolean up) {
		setStation(currentPlayIdisindex);
		getChannelData(stationlist.get(currentPlayIdisindex).getChannealid());
		if (up) {
			currentPlayIraiaindex = channellist.size() - 1;
		} else {
			currentPlayIraiaindex = 0;
		}
		setChannel(currentPlayIraiaindex);
		TurnChannel(currentPlayIraiaindex, false);
	}

	/**
	 * 破解或直接播放
	 * 
	 * @param pos
	 *            要播放第几个源
	 */
	public void play(int pos) {
		posthd.sendEmptyMessageDelayed(Page.SWITCH_CHANNEL, 20000);
		// ArrayList<PlayUrlBean> tmp = channellist.get(iraiaindex).playUrl;
		ArrayList<PlayUrlBean> tmp = sourcelist;
		Log.i(TAG, "集合的长度,tmp.size=" + tmp.size());
		milivechannel.stopSurfaceview();
		if(tmp.size()==0){
			App.mToast(mcontext, "当前频道正在维护中....", 1);
			LogUtils.playErrorLogger(AppGlobalVars.getIns().USER_ID, curchannelId, "", "", "live", "14", mcontext);
			Log.d("logger", "当前返回的logger值为14");
			mHandler = new Handler();
			mHandler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					changeChannel(-2);

				}
			}, 10000l);
		}else{
			try {
				if (!TextUtils.isEmpty(tmp.get(pos).videoPath)) {
					Log.i(TAG, "直接播放, videoPath = " + tmp.get(pos).videoPath);
					milivechannel.channelTurn(tmp.get(pos).videoPath);
				} else {
					// tmp.get(pos).url = "Letv://jiangsuHD_1300";
					// if (tmp.get(pos).url.contains("Letv://")) {
					// Log.i(TAG, "电视家破解, url = " + tmp.get(pos).url);
					// if (liveService != null) {
					// try {
					// String url = tmp.get(pos).url
					// .replace("Letv://", "");
					// String result = liveService.getCrackUrl(url);
					// milivechannel.channelTurn(result);
					// } catch (RemoteException e) {
					// e.printStackTrace();
					// }
					//
					// } else {
					// Log.e(TAG, "bind live service failed");
					// }
					// } else {
					// Log.i(TAG, "普通破解, url = " + tmp.get(pos).url);
					// new ParserUtil(mcontext, LiveDialogView.this,
					// tmp.get(pos).url, 2);
					// }
					Log.i(TAG, "普通破解, url = " + tmp.get(pos).url);
					pu = new ParserUtil(mcontext, LiveDialogView.this, tmp.get(pos).url,
							2,i);
					i++;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		

		// else if (!TextUtils.isEmpty(tmp.get(pos).url)
		// && ((tmp.get(pos).url.endsWith(".m3u8") && !tmp.get(pos).url
		// .startsWith("http://letvlive")) || tmp.get(pos).url
		// .endsWith(".mp4"))) {
		// milivechannel.channelTurn(tmp.get(pos).url);
		// }

	}

		}

	private void changeTurnArrow(boolean isturnright) {
		if (isturnright) {
			imgarrow.setBackgroundResource(R.drawable.turnright);
			setPostion(30, 30, imgarrow, 360, 460, 0, 0);
			setPostion(30, 720, imgarrbg, 0, 460, 0, 0);
		} else {
			imgarrow.setBackgroundResource(R.drawable.turnleft);
			setPostion(30, 30, imgarrow, 360, 860, 0, 0);
			setPostion(30, 720, imgarrbg, 0, 860, 0, 0);
		}
	}

	/**
	 * 点击第一栏，切换频道类型如央视，地方等时调用
	 * 
	 * @param pos
	 */
	private void TurnStation(int pos) {
		try {

			switch (pos) {
			case 0:
				channellist = DataParser.getLiveList("", DATA_KEY.livefav,
						mcontext);
				if(noFav != null){
					if(channellist.size() == 0)
						noFav.setVisibility(View.VISIBLE);
					else
						noFav.setVisibility(View.GONE);
				}
				raiadapter = new LiveListAdapter(mcontext, channellist, "line");
				freshListView(ratiolistview, raiadapter);
				/*if (channellist != null && channellist.size() > 0) {
					*//*requestProgramData(channellist.get(0).getUrlid(),
							channellist.get(0).getDes());*//*
					curchannel = channellist.get(0).getTitle();
					curNo = channellist.get(0).getNo();
					iraiaindex = 0;
				}*/
				break;

			case 1:
				// 全部频道
				getChannelData("total");
				break;

			default:
				// requestChannelData(stationlist.get(pos).getChannealid());
				getChannelData(stationlist.get(pos).getChannealid());
				break;
			}
			distinadpater.setMtype("");
			distinadpater.setSelector(pos);
			distinadpater.notifyDataSetChanged();

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取当前要播放的台的具体信息
	 * changeChannel()中或者首次从服务器获取频道信息时调用
	 * @param pos
	 * @param showLiveDialog
	 */
	private void TurnChannel(final int pos, boolean showLiveDialog) {
		try {
			if (raiadapter == null || channellist == null)
				return;
			raiadapter.setMtype("line");
			// 给2级菜单当前选中状态的条目设置背景
			raiadapter.setSelector(pos);
			// 刷新2级菜单listview
			raiadapter.notifyDataSetChanged();
			if (channellist != null && channellist.size() > pos) {
				// 节目序号
				curNo = channellist.get(pos).getNo();
				// 频道名称
				curchannel = channellist.get(pos).getTitle();
				// 频道id
				curchannelId = channellist.get(pos).getChannealid();
				// 节目id
				programPlayId = channellist.get(pos).getProgramId();
				// 节目开始时间
				programStartTime = channellist.get(pos).getStartTime();
				// milivechannel.freshChannel(curNo, channellist.get(iraiaindex)
				// .getTitle(), "", "", "", "");
				milivechannel.freshChannel(curNo, curchannel, "", "", "", "");
				// requestProgramData(channellist.get(pos).getChannealid(),
				// channellist.get(pos).getDes());
				requestProgramInfo(curchannelId, showLiveDialog);
				LogUtils.livePlayLogger(curchannelId, mcontext);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showDialog(int x, int y) {

		windowDeploy(x, y);

		// 设置触摸对话框意外的地方取消对话框
		setCanceledOnTouchOutside(true);
		show();
	}

	public void showLiveDialog() {
		if (milivechannel != null) {
			milivechannel.freshChannel(curNo, curchannel, curTime, nextTime,
					curprogram, nextprogram);
		}
		if (ratiolistview != null) {
			ratiolistview.requestFocus();
		}
		// TurnChannel(iraiaindex);
		this.show();
	}

	// 设置窗口显示
	public void windowDeploy(int x, int y) {
		window = getWindow(); // 得到对话框
		window.setWindowAnimations(R.style.dialogWindowAnim); // 设置窗口弹出动画
		window.setBackgroundDrawableResource(R.color.transparent); // 设置对话框背景为透明
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.x = x; // x小于0左移，大于0右移
		wl.y = y; // y小于0上移，大于0下移
		window.setAttributes(wl);
	}

	// 选择剧集
	private void dramaChoose(boolean isAdd) {
		if (isAdd) {

			idramaindex = idramaindex - 1;

			if (idramaindex < 0) {
				idramaindex = 0;
			}
		} else {
			idramaindex = idramaindex + 1;
			if (idramaindex >= dramalistview.getCount()) {
				idramaindex = dramalistview.getCount() - 1;
			}
		}

	}

	private TextView noProgram;
	// 创建左侧的第三级菜单
	public void addDramaList(ArrayList<LiveItemBean> lst) {
		dramalayout = new RelativeLayout(mcontext);
		dramalayout.setBackgroundResource(R.drawable.lb_left3);
		setPostion(400, 720, dramalayout, 0, 460, 0, 0);
		if(noProgram == null){
			noProgram = new TextView(mcontext);
			noProgram.setText("暂无节目信息");
			noProgram.setVisibility(View.GONE);
			noProgram.setHeight(getFitValue(720));
			noProgram.setWidth(getFitValue(400));
			noProgram.setGravity(Gravity.CENTER);
			noProgram.setTextSize(App.adjustFontSize(25));
			noProgram.setFocusable(false);
			dramalayout.addView(noProgram);
		}
		if(lst.size()==0)
			noProgram.setVisibility(View.VISIBLE);
		else
			noProgram.setVisibility(View.INVISIBLE);
		
		dramalistview = new ListView(mcontext);
		dramalistview.setFocusable(true);
		dramalistview.setSelector(R.color.live_list_selector);
		dramalistview.setId(iDramaId);
		dramalistview.setNextFocusLeftId(iRaiaId);
		dramalayout.setFocusable(false);
		dramadapter = new LiveListAdapter(mcontext, lst, "three");
		dramadapter.notifyDataSetChanged();
		dramalistview.setAdapter(dramadapter);
		addListView(dramalayout, dramalistview, lst, dramaItemLinstener,
				ondramaselectListener, 45);
		dramalayout.setVisibility(View.INVISIBLE);
		dramalistview.setNextFocusLeftId(ratiolistview.getId());
		dramalistview.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean focus) {
				dramalistview.setSelector(R.color.live_list_selector);
				if (ratiolistview != null) {
					ratiolistview.setSelector(R.drawable.choosefilter);

				}

				if (!focus) {
					isleve2focused = false;
					dramalayout.setVisibility(View.INVISIBLE);
					changeTurnArrow(true);

				}
				// 当3级菜单获得焦点的时候,底部布局隐藏
				milivechannel.showCurrentProgramInfo(false);
			}
		});

		dramalistview.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				switch (keyCode) {

				case KeyEvent.KEYCODE_DPAD_UP:
				case KeyEvent.KEYCODE_CHANNEL_UP:
				case 2006:// 剧集减
					switch (event.getAction()) {
					case KeyEvent.ACTION_DOWN:
						dramaChoose(true);
						dramalistview.setSelection(idramaindex);
						break;

					default:
						break;
					}

					break;

				// TODO
				// case KeyEvent.KEYCODE_DPAD_RIGHT:
				// if (sourcelayout != null
				// && sourcelayout.getVisibility() != View.VISIBLE) {
				// sourcelayout.setVisibility(View.VISIBLE);
				// // changeTurnArrow(false);
				//
				// }
				// break;

				case KeyEvent.KEYCODE_DPAD_DOWN:
				case KeyEvent.KEYCODE_CHANNEL_DOWN:
				case 2012:// 剧集加
					switch (event.getAction()) {
					case KeyEvent.ACTION_DOWN:
						dramaChoose(false);
						dramalistview.setSelection(idramaindex);
						break;

					default:
						break;
					}
					break;

				}

				return false;
			}
		});
	}

	private OnItemClickListener dramaItemLinstener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
				long arg3) {
		}

	};
	private OnItemSelectedListener ondramaselectListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int ipos,
				long arg3) {
			dramadapter.setSelector(ipos);
			dramadapter.notifyDataSetChanged();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	};

	public void setDisplay(int display) {
		mrelayout.setVisibility(display);
	}

	public int getDisplay() {
		return mrelayout.getVisibility();
	}

	@Override
	public void onBackPressed() {
		if (dramalayout != null) {
			dramalayout.setVisibility(View.INVISIBLE);
		}
		if (imgarrow != null) {
			changeTurnArrow(true);
		}

		super.onBackPressed();
	}

	/** ----------private--------------- **/
	private int getFitValue(int value) {
		return DisplayUtil.getDisplayValue(value, (Activity) mcontext);
	}

	private void setPostion(int width, int height, View v, int top, int left,
			int right, int bottom) {
		LayoutParams lp = new LayoutParams(getFitValue(width),
				getFitValue(height));
		lp.setMargins(getFitValue(left), getFitValue(top), getFitValue(right),
				getFitValue(bottom));
		v.setLayoutParams(lp);
	}

	/**
	 * 初始化选择列表
	 * 
	 * @param rlayout
	 * @param listview
	 * @param lst
	 */
	private void addListView(RelativeLayout rlayout, ListView listview,
			ArrayList<LiveItemBean> lst,
			OnItemClickListener onitemclicklistener,
			OnItemSelectedListener onselectorlinstener, int top) {
		rlayout.setGravity(Gravity.CENTER);
		RelativeLayout.LayoutParams lp = new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				getFitValue(600));
		lp.setMargins(0, getFitValue(top), 0, 0);
		listview.setLayoutParams(lp);
		listview.setSelector(R.drawable.choosefilter);// R.drawable.player_sourceitem_selector
		listview.setVerticalScrollBarEnabled(false);
		listview.setDividerHeight(DisplayUtil.getDisplayValue(1,
				(Activity) mcontext));
		listview.setDivider(mcontext.getResources().getDrawable(
				R.color.transparent));
		listview.setSmoothScrollbarEnabled(true);
		listview.setOnItemClickListener(onitemclicklistener);
		listview.setOnItemSelectedListener(onselectorlinstener);
		rlayout.addView(listview);
		mrelayout.addView(rlayout);
	}

	private void freshListView(ListView listview, LiveListAdapter adapter) {
		if (listview != null) {
			listview.setAdapter(adapter);
			adapter.notifyDataSetChanged();
		}
	}

	int ipos = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 当有按键事件执行时,拦截隐藏对话框的操作
		// hideTimerAction();

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		hideTimerAction();
		return super.dispatchKeyEvent(event);
	}

	private void hideTimerAction() {
		if (timerhd != null) {
			timerhd.removeCallbacks(timerrb);
		}

		timerhd.postDelayed(timerrb, 3000l);
	}

	Runnable timerrb = new Runnable() {

		@Override
		public void run() {
			if (dramalayout != null) {
				dramalayout.setVisibility(View.INVISIBLE);
			}
			if (imgarrow != null) {
				changeTurnArrow(true);
			}
			LiveDialogView.this.hide();
		}
	};
	private int itmeHeight;
	private LiveSourceAdapter sourceadpater;

	/**
	 * 获取左侧菜单的数据
	 */
	private void requestChannelInfo() {
		RemoteData remotedata = new RemoteData(mcontext);
		remotedata.getLiveChannel(new Listener4JSONObject() {

			@Override
			public void onResponse(JSONObject response) {
				String responseStr = response.toString();
				if (response != null && response.optInt("code") == 0 && !"{}".equals(responseStr)) {

					DataParser.getChannelInfo(response, stationlist,
							mapOfChannellist);

					idisindex = getSationPos();
					currentPlayIdisindex = idisindex;
					addDistinList(stationlist);
					setStation(idisindex);

					channellist = mapOfChannellist.get(stationlist.get(
							idisindex).getChannealid());
					iraiaindex = getChannelPos();
					currentPlayIraiaindex = iraiaindex;
					addRatioList(channellist);
					TurnChannel(iraiaindex, true);
					// play(0);
					// TODO
					// addPlaySourceLayout(channellist.get(iraiaindex).playUrl);
					// milivechannel.freshLiveSource(stationlist.get(idisindex).playUrl);

					// programPlayId =
					// channellist.get(iraiaindex).getProgramId();
					// curchannel = channellist.get(iraiaindex).getTitle();
					// curNo = channellist.get(iraiaindex).getNo();
					// curchannelId =
					// channellist.get(iraiaindex).getChannealid();
					//
					// addDramaList(programlist);
					// dramadapter = new LiveListAdapter(mcontext, programlist,
					// "three");
					// dramadapter.setProgramId(programPlayId);
					// freshListView(dramalistview, dramadapter);

					try {
						LiveDialogView.this.show();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					Log.i(TAG, "网络数据异常");
					Utils.showToast(R.string.channel_maintain);
					((Activity)mcontext).finish();
				}
			}

			@Override
			public void onError(String httpStatusCode) {
				if(BasePlayerActivity.playType!=2){
					Utils.showToast(R.string.no_internet_error);
				}
				((Activity)mcontext).finish();
			}
		});
	}

	// protected void addSourceList(ArrayList<LiveItemBean> sourcelist) {
	// sourcelayout = new RelativeLayout(mcontext);
	// sourcelayout.setBackgroundResource(R.drawable.lb_left1);
	// setPostion(200, 720, sourcelayout, 0, 860, 0, 0);
	// sourcelistview = new ListView(mcontext);
	// sourcelistview.setSmoothScrollbarEnabled(true);
	// sourcelistview.setSelector(R.drawable.lb_left1foucs1);
	// sourcelistview.setFocusable(true);
	// sourcelayout.setFocusable(false);
	// sourceadpater = new LiveListAdapter(mcontext, sourcelist, "");
	//
	// sourceadpater.notifyDataSetChanged();
	// sourcelistview.setAdapter(sourceadpater);
	//
	// addListView(sourcelayout, sourcelistview, sourcelist,
	// distinItemLinstener, distinitemselectListener, 45);
	// }

	/**
	 * 获取电视台的播放流
	 * @param programId   要播放的台的id
	 * @param showLiveDialog
	 *            是否显示LiveDialogView，上下键切换频道时不显示
	 */
	private void requestProgramInfo(String programId,
			final boolean showLiveDialog) {
		RemoteData remoteData = new RemoteData(mcontext);
		remoteData.getLiveProgram(new Listener4JSONObject() {

			@Override
			public void onResponse(JSONObject response) {
				if(response ==null && response.length()>0){
					App.mToast(mcontext, "当前应用正在维护中......");
				}
				programlist.clear();
				sourcelist.clear();
				DataParser.getProgramInfo(response, programlist, sourcelist);
				ArrayList<String> tmp = new ArrayList<String>();
				for (int i = 0; i < sourcelist.size(); i++) {
					if (!TextUtils.isEmpty(sourcelist.get(i).videoPath)) {
						tmp.add(sourcelist.get(i).videoPath);
					} else if (!TextUtils.isEmpty(sourcelist.get(i).url)) {
						tmp.add(sourcelist.get(i).url);
					} else {
						tmp.add("");
					}
				}
				milivechannel.updateSource(tmp);
				//这里注掉是为了处理没有节目单时无法跳转的问题
				/*if (programlist.isEmpty() && dramalayout != null) {
					dramalayout.setVisibility(View.INVISIBLE);
					return;
				}*/
				
				play(0);
				new Handler().post(new Runnable() {

					@Override
					public void run() {
						if (isfirstLoadProgram) {
							addDramaList(programlist);
							dramadapter = new LiveListAdapter(mcontext,
									programlist, "three");
							dramadapter.setProgramId(programPlayId);
							freshListView(dramalistview, dramadapter);
							isfirstLoadProgram = false;
						} else {
							dramadapter = new LiveListAdapter(mcontext,
									programlist, "three");
							dramadapter.setProgramId(programPlayId);
							freshListView(dramalistview, dramadapter);
						}
						try {
							if (showLiveDialog) {
								LiveDialogView.this.show();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

				if (programlist != null && programlist.size() > 1) {
					if (channellist != null && channellist.size() <= 0)
						return;
					if (iraiaindex >= channellist.size()) {
						iraiaindex = 0;
					}
					curchannel = channellist.get(iraiaindex).getTitle();
					curNo = channellist.get(iraiaindex).getNo();
					final int programpos = getProgramCurrentPos();
					
					if (programlist.isEmpty()) {
						nextprogram="当前无节目列表";
						curprogram="当前无节目列表";
					}else{
						nextprogram = getCurrentPlayTitle(programpos + 1);
						curprogram = getCurrentPlayTitle(programpos);
					}
					nextTime = getCurrentPlayTime(programpos);
					curTime = getCurrentPlayTime(programpos - 1);
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							dramalistview.smoothScrollToPositionFromTop(
									programpos, 45);//将播放的节目定位到最顶部
							idramaindex = programpos;
							dramalistview.setSelection(programpos);
							
							milivechannel.freshChannel(curNo, curchannel,
									curTime, nextTime, curprogram, nextprogram);
//							milivechannel.showCurrentProgramInfo(true);
						}
					}, 1000l);

				}else{
					nextprogram="暂无节目信息";
					curprogram="暂无节目信息";
				}
			}

			@Override
			public void onError(String httpStatusCode) {
				if(BasePlayerActivity.playType!=2){
					Utils.showToast(R.string.no_internet_error);
				}
				((Activity)mcontext).finish();
			}
		}, programId);
	}

	/**
	 * 用户在播放当前频道的同时，在LiveDialogView中切换到其他频道查看节目单时调用该方法
	 * 
	 * @param programId
	 *            频道id
	 */
	private void requestProgramInfoForOtherChannel(String programId) {
		RemoteData remoteData = new RemoteData(mcontext);
		remoteData.getLiveProgram(new Listener4JSONObject() {

			@Override
			public void onResponse(JSONObject response) {

			}

			@Override
			public void onError(String httpStatusCode) {
				if(BasePlayerActivity.playType!=2){
					Utils.showToast(R.string.no_internet_error);
				}
				((Activity)mcontext).finish();
			}
		}, programId);
	}

	private void requestStationData() {
		RemoteData remotedata = new RemoteData(mcontext);
		remotedata.liveStation(new Listener4JSONObject() {

			@Override
			public void onResponse(JSONObject response) {
				if (response != null) {
					try {

						stationlist = DataParser.getLiveList(
								response.toString(), DATA_KEY.livestation,
								mcontext);
						idisindex = getSationPos();
						new Handler().post(new Runnable() {

							@Override
							public void run() {
								if (isfirst) {
									addDistinList(stationlist);
									setStation(idisindex);
								} else {
									distinadpater = new LiveListAdapter(
											mcontext, stationlist, "");
									freshListView(distinlstview, distinadpater);
								}
								if (stationlist != null
										&& stationlist.size() > idisindex) {
									requestChannelData(stationlist.get(
											idisindex).getChannealid());
								}
							}
						});

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

			}

			@Override
			public void onError(String errorMessage) {
				if(BasePlayerActivity.playType!=2){
					Utils.showToast(R.string.no_internet_error);
				}
				((Activity)mcontext).finish();
			}
		});
	}

	/**
	 * 获取具体的播放的台在哪个分类里面
	 * @return
	 */
	private int getSationPos() {
		int postmp = 2;
		if (mlivextbean != null && stationlist != null
				&& stationlist.size() > 0) {
			for (int i = 0; i < stationlist.size(); i++) {
				if (stationlist.get(i).getChannealid()
						.equals(mlivextbean.getGroupid())) {
					postmp = i;
					break;
				}
			}

		}
		return postmp;
	}

	/**
	 * 获取当前要播放的具体台的在集合中的position
	 * @return
	 */
	public int getChannelPos() {
		int postmp = iraiaindex;
		if (mlivextbean != null && channellist != null
				&& channellist.size() > 0) {
			for (int i = 0; i < channellist.size(); i++) {
				if (channellist.get(i).getChannealid()
						.equals(mlivextbean.getChannelid())) {
					postmp = i;
					break;
				}
			}
		}
		return postmp;
	}

	/*
	 * 找到no对应的position
	 */
	public int getChannelPos(String no) {
		int postmp = -1;
		if (channellist != null && channellist.size() > 0) {
			for (int i = 0; i < channellist.size(); i++) {
				if (Integer.parseInt(channellist.get(i).getNo()) == Integer
						.parseInt(no)) {
					postmp = i;
					break;
				}
			}
		}
		return postmp;
	}

	private void requestChannelData(String groupId) {
		RemoteData remotedata = new RemoteData(mcontext);
		remotedata.liveChannel(groupId, new Listener4JSONArray() {

			@Override
			public void onResponse(JSONArray response) {
				if (response != null) {
					try {
						channellist = DataParser.getLiveList(
								response.toString(), DATA_KEY.livechannel,
								mcontext);
						iraiaindex = getChannelPos();
						new Handler().post(new Runnable() {

							@Override
							public void run() {
								if (isfirst) {
									addRatioList(channellist);

									TurnChannel(iraiaindex, true);
									milivechannel.channelTurn(channellist.get(
											iraiaindex).getUrlid());
								} else {
									raiadapter = new LiveListAdapter(mcontext,
											channellist, "line");

									freshListView(ratiolistview, raiadapter);
								}
								if (channellist != null
										&& channellist.size() > iraiaindex) {
									programPlayId = channellist.get(iraiaindex)
											.getProgramId();
									curchannel = channellist.get(iraiaindex)
											.getTitle();
									curNo = channellist.get(iraiaindex).getNo();
									curchannelId = channellist.get(iraiaindex)
											.getChannealid();
									// milivechannel.channelTurn(channellist.get(iraiaindex).getUrlid());
									requestProgramData(
											channellist.get(iraiaindex)
													.getChannealid(),
											channellist.get(iraiaindex)
													.getDes());

								}
							}
						});

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onError(String errorMessage) {
				if(BasePlayerActivity.playType!=2){
					Utils.showToast(R.string.no_internet_error);
				}
				((Activity)mcontext).finish();
			}
		});
	}

	// 在第一次从服务器获取的频道map中获取频道信息
	private void getChannelData(String channealid) {
		channellist = mapOfChannellist.get(channealid);
		iraiaindex = getChannelPos();
		
		raiadapter = new LiveListAdapter(mcontext, channellist, "line");
		freshListView(ratiolistview, raiadapter);
	}

	private void requestProgramData(String uuId, String des) {
		RemoteData remotedata = new RemoteData(mcontext);
		remotedata.liveProgram(uuId, des, new Listener4JSONArray() {

			@Override
			public void onResponse(JSONArray response) {
				if (response != null) {
					try {
						programlist = DataParser.getLiveList(
								response.toString(), DATA_KEY.liveprogram,
								mcontext);
						new Handler().post(new Runnable() {

							@Override
							public void run() {
								if (isfirst) {
									addDramaList(programlist);
									dramadapter = new LiveListAdapter(mcontext,
											programlist, "three");
									dramadapter.setProgramId(programPlayId);
									freshListView(dramalistview, dramadapter);
									isfirst = false;
								} else {
									dramadapter = new LiveListAdapter(mcontext,
											programlist, "three");
									dramadapter.setProgramId(programPlayId);
									freshListView(dramalistview, dramadapter);
								}
								try {
									LiveDialogView.this.show();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});

						if (programlist != null && programlist.size() > 1) {
							if (channellist != null && channellist.size() <= 0)
								return;
							if (iraiaindex >= channellist.size()) {
								iraiaindex = 0;
							}
							curchannel = channellist.get(iraiaindex).getTitle();
							curNo = channellist.get(iraiaindex).getNo();
							final int programpos = getProgramCurrentPos();
							nextprogram = getCurrentPlayTitle(programpos);
							curprogram = getCurrentPlayTitle(programpos - 1);
							nextTime = getCurrentPlayTime(programpos);
							curTime = getCurrentPlayTime(programpos - 1);
							new Handler().postDelayed(new Runnable() {

								@Override
								public void run() {
									dramalistview
											.smoothScrollToPositionFromTop(
													programpos, 45);
									idramaindex = programpos;
									dramalistview.setSelection(programpos);
									milivechannel.freshChannel(curNo,
											curchannel, curTime, nextTime,
											curprogram, nextprogram);
								}
							}, 1000l);

						}
					} catch (Exception e) {
						programlist.clear();
						e.printStackTrace();
					}
				} else {
					programlist.clear();
				}
			}

			@Override
			public void onError(String errorMessage) {
				//Utils.showToast(R.string.no_internet_error);
				//((Activity)mcontext).finish();
			}
		});
	}

	/**
	 * 获取界面的播放和结束的时间
	 * @param ipos
	 * @return
	 */
	private String getCurrentPlayTime(int ipos) {
		String strcur = "";
		try {
			if ((programlist != null && ipos >= programlist.size())||ipos == -1) {
				ipos = 0;
			}
			strcur = programlist.get(ipos).getStartTime() + "-"
					+ programlist.get(ipos).getEndTime();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return strcur + ":";
	}

	private String getCurrentPlayTitle(int ipos) {
		if (programlist != null && ipos >= programlist.size() || ipos < 0) {
			ipos = 0;
		}
		String title = "";
		try {
			title = programlist.get(ipos).getTitle();
		} catch (Exception e) {

		}
		return title.substring(title.indexOf(':') < 0 ? 0
				: title.indexOf(':') + 1);
	}

	private int getProgramCurrentPos() {
		int ipos = 0;
		if (programlist != null) {
			for (int i = 0; i < programlist.size(); i++) {
				String id1 = programlist.get(i).getChannealid();
				String id2 = programPlayId;
				if (programlist.get(i).getChannealid().equals(programPlayId)) {
					// if
					// (programlist.get(i).getStartTime().equals(programStartTime))
					// {
					ipos = i;
					break;
				}
			}
		}
		return ipos;

	}

	/** ---------------public api------------ **/

	public String getCurchannel() {
		return curchannel;
	}

	public String getCurprogram() {
		return curprogram;
	}

	public String getNextprogram() {
		return nextprogram;
	}

	public String getCurchannelId() {
		return curchannelId;
	}

	public String getCurTime() {
		return curTime;
	}

	public String getNextTime() {
		return nextTime;
	}

	public String getCurNo() {
		return curNo;
	}

	/**
	 * 获取当前播放频道
	 * 
	 * @return
	 */
	public MPlayRecordInfo getCurrentChannel() {
		if (channellist != null && channellist.size() <= iraiaindex) {
			return null;
		}
		LiveItemBean liveitembean = channellist.get(iraiaindex);
		MPlayRecordInfo mplayrcinfo = new MPlayRecordInfo();
		mplayrcinfo.setType(PlayRecordHelpler.LIVETYPE);
		mplayrcinfo.setDetailsId(liveitembean.getUrlid());
		mplayrcinfo.setEpgId(liveitembean.getChannealid());
		mplayrcinfo.setPlayerName(liveitembean.getTitle());
		mplayrcinfo.setLiveno(liveitembean.getNo());
		mplayrcinfo.setPicUrl(liveitembean.getLogo());
		mplayrcinfo.setType(PlayRecordHelpler.LIVETYPE);
		mplayrcinfo.setDateTime(System.currentTimeMillis());
		mplayrcinfo.setCornerPrice(liveitembean.getProgramId());
		return mplayrcinfo;
	}

	public void setILiveChannel(ILiveChannel livechannel) {
		milivechannel = livechannel;
	}

	/**
	 * 最左侧列表进行定位
	 * @param
	 */
	public void setStation(int ipos) {
		if (ipos < distinlstview.getCount()) {
			distinlstview.setSelection(ipos);
			distinadpater.setSelector(ipos);
			distinadpater.notifyDataSetChanged();

		}
	}

	/**
	 * 设置左侧第二列表的选中item
	 * @param ipos
	 */
	public void setChannel(int ipos) {
		if (ipos < ratiolistview.getCount()) {
			ratiolistview.setSelection(ipos);
		}
	}

	private int digit = 3;
	private String channelNum;
	Runnable task = new Runnable() {
		public void run() {
			NumChoose(Integer.parseInt(channelNum));
			digit = 3;
			channelNum = null;
		}
	};
	Handler handler = new Handler();

	private void onNumKeyDown(String i) {
		if (digit > 0) {
			channelNum = channelNum == null ? i : channelNum + i;
			digit--;
		}
		handler.removeCallbacks(task);
		handler.postDelayed(task, 1500);
	}

	// 通过数字按钮选择
	private void NumChoose(int num) {

		if (num == 0) {
			App.mToast(getContext(), "暂无此节目");
		} else if (num > 0 && num <= ratiolistview.getCount()) {
			Lg.i(TAG, num + "正常进来了");
			iraiaindex = num - 1;
			ratiolistview.setSelection(iraiaindex);

		} else if (num > ratiolistview.getCount()) {
			App.mToast(getContext(), "暂无此节目");
		}
	}

	@Override
	public void onCrackComplete(CrackResult result) {
		if (result.type.equals("updatePlugins")) {
			Log.i(TAG, "updatePlugins");
			milivechannel.showUpdateDialog(true);
			posthd.removeMessages(Page.SWITCH_CHANNEL); // 如果更新组件则停止切源
		} else if (result.type.equals("complete")) {
			Log.i(TAG, "complete");
			milivechannel.showUpdateDialog(false);
//			posthd.sendEmptyMessage(10);
			play(0); // 解决第一次进直播播放第二个源的问题
		} else {
			Log.i(TAG, "破解完成，url=" + result.path);
			// milivechannel.channelTurn(result.path);
			Message msg = posthd.obtainMessage();
			msg.what = Page.CRACK_COMPLETE;
			msg.obj = result.path;
			posthd.sendMessage(msg);
		}
	}

	/**
	 * 破解失败时返回原始地址
	 * @param arg0
	 */
	@Override
	public void onCrackFailed(HashMap<String, String> arg0) {
		if (arg0 == null || (TextUtils.isEmpty(arg0.get("standardDef"))
				&& TextUtils.isEmpty(arg0.get("hightDef"))
				&& TextUtils.isEmpty(arg0.get("superDef")))) {
			sendPlayerrorLog(arg0);
		}
	}

	/**
	 * 这是发送破解失败log日志的方法
	 * @param arg0
	 */
	public void sendPlayerrorLog(HashMap<String, String> arg0){
		if (arg0 != null && !TextUtils.isEmpty(arg0.get("orignUrl")))
			LogUtils.playErrorLogger(AppGlobalVars.getIns().USER_ID, curchannelId, arg0.get("orignUrl"), "", "live", "11", mcontext);
		else
			LogUtils.playErrorLogger(AppGlobalVars.getIns().USER_ID, curchannelId, sourcelist.get(ipos).url, "", "live", "11", mcontext);
		Log.d("logger","当前返回的logger值为11");
	}

	// TODO
	// public void addPlaySourceLayout(ArrayList<PlayUrlBean> list) {
	// sourcelayout = new RelativeLayout(mcontext);
	// sourcelayout.setFocusable(false);
	// sourcelayout.setBackgroundResource(R.drawable.lb_left1);
	// setPostion(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
	// sourcelayout, 0, 800, 0, 0);
	//
	// sourcelistview = new ListView(mcontext);
	// sourceadpater = new LiveSourceAdapter(mcontext, list);
	// sourcelistview.setAdapter(sourceadpater);
	// sourcelistview.setOnItemClickListener(sourceClickListener);
	// sourcelistview.setOnFocusChangeListener(new OnFocusChangeListener() {
	//
	// @Override
	// public void onFocusChange(View v, boolean hasFocus) {
	//
	// }
	// });
	// sourcelistview.setOnKeyListener(new View.OnKeyListener() {
	//
	// @Override
	// public boolean onKey(View v, int keyCode, KeyEvent event) {
	// return false;
	// }
	// });
	// sourcelayout.addView(sourcelistview);
	// mrelayout.addView(sourcelayout);
	// // sourcelayout.setVisibility(View.INVISIBLE);
	// }

	// private OnItemClickListener sourceClickListener = new
	// OnItemClickListener() {
	//
	// @Override
	// public void onItemClick(AdapterView<?> arg0, View v, int pos, long arg3)
	// {
	// Log.i(TAG, "source clicked");
	// play(pos);
	// }
	//
	// };

	// TODO
	// public void showSourceLayout(boolean show) {
	// if (show) {
	// sourcelayout.setVisibility(View.VISIBLE);
	// } else {
	// sourcelayout.setVisibility(View.INVISIBLE);
	//
	// }
	// }

	// public void setLiveService(MyLiveService liveService) {
	// this.liveService = liveService;
	// }

	/*--------- 切换台时进行节目源重置的回调  ---------*/
	private ResetSourceIpos resetSourceIpos;
	private BaseVedioView surfaceview;
	private ParserUtil pu;
	
	public void stopCrack(){
		if(pu != null)
			pu.stop();
	}
	public void setResetSourceIpos(ResetSourceIpos resetSourceIpos) {
		this.resetSourceIpos = resetSourceIpos;
	}

	public interface ResetSourceIpos {
		public void onResetSourceIposListener();
	}
	
	/**
	 * 结束播放进行资源释放
	 */
	public void clear(){
		clearHandler(mHandler);
		clearHandler(posthd);
		clearHandler(timerhd);
		this.dismiss();
	}
	private void clearHandler(Handler handler){
		if(handler != null){
			handler.removeCallbacksAndMessages(null);
			handler = null;
		}
	}
}
