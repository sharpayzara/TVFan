package tvfan.tv.ui.andr.play.baseplay.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.tvata.p2p.P2PManager;

import java.io.File;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.BasePage;
import tvfan.tv.R;
import tvfan.tv.dal.models.PlayerBean;
import tvfan.tv.ui.andr.play.baseplay.interfaces.IPlayerListener;
import tvfan.tv.ui.andr.play.baseplay.utils.ConnectionChangeReceiver;
import tvfan.tv.ui.andr.play.baseplay.utils.LogUtils;
import tvfan.tv.ui.andr.play.baseplay.widgets.ExitDialogView;
import tvfan.tv.ui.andr.widgets.LoadingDialog;
import tvfan.tv.ui.andr.widgets.PlayererrorDialog;

public class BasePlayerActivity extends Activity {
	protected String _pageAction = "OPEN_PLAYER";
	protected ConnectionChangeReceiver mConnectionChangeReceiver;
	public static final int VOD_PLAY = 2;//点播
	public static final int LIVE_PLAY = 1;//直播
	public static int playType; 
	private Handler hd,handler;
	private Runnable rb;
	private IPlayerListener playerlistener;
	private LoadingDialog loadingDialog;
	private ExitDialogView exitDialogView;
	private PlayerBean playerbean = null;
	protected PlayererrorDialog playererrorDialog;
	public static P2PManager p2pManager = null;

	public void startP2PService() {
		String zone = "zt15120802";

		String native_dir = this.getCacheDir() + "p_pie_1";

		Log.d("App", "startP2PService at " + native_dir);

		File ndir = new File(native_dir);

		if (ndir.exists() == false)
			ndir.mkdir();
		handler=new Handler();
		p2pManager = P2PManager.init(native_dir, zone);
		p2pManager.setErrorEvent(new Runnable() {

			@Override
			public void run() {
				Log.d("App", "p2p 00000000000000000      manager to run error event");

				handler.post(new Runnable() {

					@Override
					public void run() {
						String message = p2pManager.getMessage();

						// Toast.makeText(TvAppActivity.this,
						// "Start P2P Failed: " + message, Toast.LENGTH_LONG
						// ).show();

					}
				});
			}

		});

		// manager.setSuccessEvent( new Runnable() {
		//
		// @Override
		// public void run() {
		// //manager.getHttpPort();
		// }} );

		// p2pManager.start();

		Log.d("App", "p2pManager started!");
		p2pManager.start();
		
		P2PManager.get().set_m3u8_endstring("?");
		P2PManager.get().set_ts_endstring("?");
	}

	public  String getMobilePlayUrl(String url) {
		// if ( nop2p == true) return url;
		Log.d("App" , "use new url : " + url );
		
		if (p2pManager != null && url!=null) {
//			if (url.contains("http://") == false)
//				return url;
//			if (url.contains("playlist.m3u8") == false)
//				return url;

			// if ( HomeActivity.p2pManager.isRunning() )
			{
				String nurl = p2pManager.getPlayedUrl(url).replace("24188",
						"14188");
				return nurl;
			}
			// return url;
		}
		return null;
	}
	/**
	 * 这是设置报错对话框内容的方法
	 * @param what
	 * @param isyouku
	 * @param code
	 * @param duration
	 */
	public void showErrorDialog(final int what, final boolean isyouku,
								final int code, long duration) {


		if (rb != null&& hd!=null ) {
				hd.removeCallbacks(rb);
		}
		hd.postDelayed(rb = new Runnable() {

				@Override
				public void run() {
					// Toast.makeText(BasePlayerActivity.this,
					// "网络断了:showErrorDialog", 3000).show();
					if (what == MediaPlayer.MEDIA_ERROR_IO) {
						//io读写错误
						playererrorDialog.playerConfirm(BasePlayerActivity.this
										.getString(R.string.errortitle2),
								BasePlayerActivity.this
										.getString(R.string.errorText3));
					} else if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
						playererrorDialog.playerConfirm(BasePlayerActivity.this
										.getString(R.string.errortitle2),
								BasePlayerActivity.this
										.getString(R.string.errorText3));
					} else if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
						playererrorDialog.playerConfirm(BasePlayerActivity.this
										.getString(R.string.errortitle2),
								BasePlayerActivity.this
										.getString(R.string.errorText3));

					} else if (what == MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK) {
						playererrorDialog.playerConfirm(BasePlayerActivity.this
										.getString(R.string.errortitle2),
								BasePlayerActivity.this
										.getString(R.string.errorText3));

					} else if (what == MediaPlayer.MEDIA_ERROR_UNSUPPORTED) {
						//比特流编码标准或文件符合相关规范,但媒体框架不支持该功能
						playererrorDialog.playerConfirm(BasePlayerActivity.this
										.getString(R.string.errortitle2),
								BasePlayerActivity.this
										.getString(R.string.errorText3));
					} else if (what == MediaPlayer.MEDIA_ERROR_TIMED_OUT) {
						//一些操作需要太长时间来完成,通常超过3 - 5秒。
						playererrorDialog.playerConfirm(BasePlayerActivity.this
										.getString(R.string.errortitle2),
								BasePlayerActivity.this
										.getString(R.string.errorText9));
					}                else {
						playererrorDialog.playerConfirm(
								BasePlayerActivity.this
										.getString(R.string.errortitle2),
								BasePlayerActivity.this
										.getString(R.string.errorText9));
					}
					if(loadingDialog!=null){
						loadingDialog.dismiss();
					}
					if(playererrorDialog!=null && !isFinishing()){
						playererrorDialog.show();
						//布局文件中设置不起作用,这里再设置一下
						playererrorDialog.setCancelable(false);
					}
				}
			}, duration);
		}
 	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			startP2PService();
			hd = new Handler();
			Log.d("logger", "当前playerbean为..."+playerbean);
			loadingDialog = new LoadingDialog(this, R.style.Dialog);
			playererrorDialog = new PlayererrorDialog(this,R.style.errordialog);
			playererrorDialog.getWindow().setLayout(
					getWindowManager().getDefaultDisplay().getWidth(),
					getWindowManager().getDefaultDisplay().getHeight());
			LogUtils.registerLocalMsgReceiver(new BasePage.LocalMsgListener() {
				@Override
				public void onReceive(Context context, Intent intent) {
					boolean isconnect = intent.getBooleanExtra("isConnected",
							true);
					// Toast.makeText(context, "网络断了:"+isconnect, 3000).show();
					if (!isconnect) {
						if(playType==2){
							showErrorDialog(-1, false, 0, 3000);
						}else if(playType==1){
							for (int i = 0; i < 5; i++) {
								Toast.makeText(context, "网络已断开", Toast.LENGTH_LONG).show();
							}
							hd.postDelayed(new Runnable() {

								@Override
								public void run() {
									finish();

								}
							}, 3000);
						}
						loadingDialog.dismiss();
						if (playerlistener != null) {
							playerlistener.PlayerPause(true);
						}

					} else {
						if (playerlistener != null) {
							playerlistener.PlayerPause(false);
						}

						playererrorDialog.dismiss();
					}
				}
			}, AppGlobalConsts.LOCAL_MSG_FILTER.NET_CHANGED, this);

		} catch (Exception e) {
			e.printStackTrace();

	}}
	public void removeError() {
		if (rb != null && hd != null) {

			hd.removeCallbacks(rb);
			playererrorDialog.dismiss();
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		try {
			removeError();
			LogUtils.unregisterLocalMsgReceiver(
					AppGlobalConsts.LOCAL_MSG_FILTER.NET_CHANGED, this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onStop();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		_logWrite(_pageAction, AppGlobalConsts.LOG_CMD.UMENG_PAGE_END);
	}

	@Override
	protected void onDestroy() {
		
		try {
			if (mConnectionChangeReceiver != null) {
				unregisterReceiver(mConnectionChangeReceiver);
			}
			LogUtils.unregisterLocalMsgReceiver(AppGlobalConsts.LOCAL_MSG_FILTER.NET_CHANGED, this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		clearHandler(handler);
		clearHandler(hd);
		finish();
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			if (mConnectionChangeReceiver == null) {// 如果播放途中，网络断掉，需要进行相应提示
				mConnectionChangeReceiver = new ConnectionChangeReceiver();
				mConnectionChangeReceiver
						.setOnNetworkChangedListener(new ConnectionChangeReceiver.onNetworkChangedListener() {

							@Override
							public void onNetworkChanged(boolean isConnected) {
								// &&mSurfaceView.getBufferPercentage()<2
								if (!isConnected) {
									if(playType==2){
										showErrorDialog(-1, false, 0, 3000);
									}else if(playType==1){
										for (int i = 0; i < 5; i++) {
											Toast.makeText(getApplicationContext(), "网络已断开", Toast.LENGTH_LONG).show();
										}
										hd.postDelayed(new Runnable() {

											@Override
											public void run() {
												finish();

											}
										}, 3000);
									}

								} else {// 取消掉字
									if(playererrorDialog!=null){
										playererrorDialog.dismiss();
									}
								}
							}
						});
			}

			IntentFilter broadcastFilter = new IntentFilter(
					"android.net.conn.CONNECTIVITY_CHANGE");
			if (mConnectionChangeReceiver != null) {
				registerReceiver(mConnectionChangeReceiver, broadcastFilter);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		_logWrite(_pageAction, AppGlobalConsts.LOG_CMD.UMENG_PAGE_START);
	}

	/**
	 * 写日志
	 **/
	private void _logWrite(String logParamStr, AppGlobalConsts.LOG_CMD logCmd) {
		Intent intent = new Intent();
		intent.putExtra(AppGlobalConsts.INTENT_LOG_CMD_NAME, logCmd.name());
		intent.putExtra(AppGlobalConsts.INTENT_LOG_PARAM, logParamStr);
		sendLocalStickyMsg(AppGlobalConsts.LOCAL_MSG_FILTER.LOG_WRITE, intent);
	}

	/**
	 * 发送本地滞留消息
	 **/
	public void sendLocalStickyMsg(
			AppGlobalConsts.LOCAL_MSG_FILTER localMsgFilter, Intent intent) {
		intent.setAction(localMsgFilter.toString());
		sendStickyBroadcast(intent);
	}
	
	/**
	 * 设置播放器监听
	 * @param iplayerlistener
	 */
	protected void setPlayerListener(IPlayerListener iplayerlistener) {
		playerlistener = iplayerlistener;
	}
	

	protected void clearHandler(Handler handler){
		if(handler != null){
			handler.removeCallbacksAndMessages(null);
			handler = null;
		}
	}
}
