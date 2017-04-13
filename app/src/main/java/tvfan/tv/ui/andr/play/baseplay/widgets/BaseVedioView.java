package tvfan.tv.ui.andr.play.baseplay.widgets;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.RelativeLayout.LayoutParams;

import java.io.IOException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import tvfan.tv.lib.DisplayUtil;
import tvfan.tv.lib.Lg;
import tvfan.tv.ui.andr.play.baseplay.interfaces.IPlayAction;
import tvfan.tv.ui.andr.play.baseplay.ui.BasePlayerActivity;

/**
 * 原生播放器
 * 
 * @author sadshine
 */
public class BaseVedioView extends SurfaceView implements IPlayAction {
//	private int STOP = 0, PLAYING = 1, PAUSE = 2;
	// 播放控件
	private Map<String, String> mHeaders;
	private Context mcontext;
	private SurfaceHolder surfaceHolder;
	private MediaPlayer mediaPlayer;
	private String videoPath = "";
	private AudioManager audiomanger;
	// 播放时间
	private int currentime = 0;
	private int totaltime = 0;
	// 播放状态
	private int state = 0;// 0:stop 1:play 2:pause
	private Handler merrorhd;
	private boolean isloop = false;
	// 视频大小和播放器大小
	private int iVideoWidth = 0;
	private int iVideoHeight = 0;
	private int iMediaWidth = 0;
	private int iMediaHeight = 0;
	// 监听事件
	private OnPreparedListener onPreparedListener;
	private OnBufferingUpdateListener onBufferingUpdateListener;
	private OnInfoListener onInfoListener;
	private OnErrorListener onErrorListener;
	private OnCompletionListener onCompletionListener;
	private int currentPosition1 ,currentPosition2;
	private Timer timer;
	private Handler avoidHd = new Handler();

	public BaseVedioView(Context context, Handler errorhd) {
		super(context);
		mcontext = context;
		merrorhd = errorhd;
		initVolumn();
		timer = new Timer();
	}

	/** ---------------播放器初始化-------------------- **/
	private void initVolumn() {
		audiomanger = (AudioManager) mcontext
				.getSystemService(Service.AUDIO_SERVICE);
	}
	
	/**
	 * 设置播放器的宽和高
	 */
	private void initSurfaceView() {
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		if (iMediaWidth > 0 && iMediaHeight > 0) {
			lp = new LayoutParams(iMediaWidth, iMediaHeight);
		}
		this.setLayoutParams(lp);
	}

	/**
	 * 初始化surfaceHoler，调用startPlay（）开始播放
	 */
	private void initSurfaceHolder() {
		surfaceHolder = BaseVedioView.this.getHolder();
		if (iVideoWidth > 0 && iVideoHeight > 0) {
			surfaceHolder.setFixedSize(iVideoWidth, iVideoHeight);
		} else {
			surfaceHolder.setFixedSize(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
		}

		// 兼容低版本
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		// 播放器渲染回调
		surfaceHolder.addCallback(new Callback() {
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				 clear();

			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				Lg.v("BasePlayer", "surfaceHolder.addCallback");
				surfaceHolder = holder;
				startPlay();
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
			}
		});
		startPlay();
	}

	/**
	 * 对播放相关的一些进行设置，并开始播放
	 */
	private void startPlay() {
//		timer = new Timer();
//		avoidHd = new Handler();
		Intent i = new Intent("com.android.music.musicservicecommand");
		i.putExtra("command", "pause");
		mcontext.sendBroadcast(i);
		if (mediaPlayer != null) {
			stop();
			reset();
//			 releaseplayer();
		}
		if (mediaPlayer == null) {

			mediaPlayer = new MediaPlayer();
		}
		try {
			mediaPlayer.setDisplay(surfaceHolder);// 设置视频播放的surfaceHodler
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置音频形式（在prepare()或prepareAsync()之前）
			mediaPlayer.setScreenOnWhilePlaying(true);
			// mediaPlayer.prepare();
			mediaPlayer.setLooping(isloop);// 设置播放器是否循环
			if (videoPath != null && !videoPath.equals("")) {
				mediaPlayer.setDataSource(mcontext, Uri.parse(videoPath),
						mHeaders);
			}
			mediaPlayer.prepareAsync();// 在设置播放的source之后需要调用这个方法或者prepare()
			mediaPlayer.setOnPreparedListener(this.onPreparedListener);// 设置播放准备监听
			mediaPlayer
					.setOnBufferingUpdateListener(this.onBufferingUpdateListener);// 设置缓存监听

			mediaPlayer.setOnErrorListener(this.onErrorListener);// 设置播放出错监听
			mediaPlayer.setOnInfoListener(this.onInfoListener);
			mediaPlayer.setOnCompletionListener(this.onCompletionListener);// 设置播放完成监听
			if (BasePlayerActivity.playType == 1) {
				avoidStukeTimer();
			}

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			Lg.v("logger:", e.toString());
		} catch (SecurityException e) {

			e.printStackTrace();
			Lg.v("logger:", e.toString());
		} catch (IllegalStateException e) {

			e.printStackTrace();
			Lg.v("logger:", e.toString());
		} catch (IOException e) {
			e.printStackTrace();
			Lg.v("logger:", e.toString());
		}

	}

	private void reset() {
		if (mediaPlayer != null) {
			mediaPlayer.reset();
			state = 0;
		}
	}

	/** ---------------播放器基础功能-------------------- **/
	@Override
	public int getTotalTime() {
		if (mediaPlayer != null) {
			totaltime = mediaPlayer.getDuration();
		}
		return totaltime;
	}

	@Override
	public int getCurrentTime() {
		if (mediaPlayer != null) {
			currentime = mediaPlayer.getCurrentPosition();
		}
		return currentime;
	}

	@Override
	public void start() {
		try {
			if (mediaPlayer != null) {
				mediaPlayer.start();
				state = 1;
			}
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void stop() {
		try {
			if (mediaPlayer != null) {
				mediaPlayer.stop();
//				releaseplayer();
				state = 0;
			}
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void pause() {
		try {
			if (mediaPlayer != null) {
				mediaPlayer.pause();
				state = 2;
			}
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 快进快退的方法
	 */
	@Override
	public void seek(int currentime) {
		if (mediaPlayer != null) {
			try {
				mediaPlayer.seekTo(currentime);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
			state = 1;
		}
	}

	@Override
	public void volumn(boolean isadd) {
		if (isadd) {
			if (audiomanger != null) {
				try {
					// 指定调节音乐的音频，增大音量，而且显示音量图形示意
					audiomanger.adjustStreamVolume(AudioManager.STREAM_MUSIC,
							AudioManager.ADJUST_RAISE,
							AudioManager.FX_FOCUS_NAVIGATION_UP);
					Log.d("volum", "音量增加已经执行了...");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			try {
				if (audiomanger != null) {
					audiomanger.adjustStreamVolume(AudioManager.STREAM_MUSIC,
							AudioManager.ADJUST_LOWER,
							AudioManager.FX_FOCUS_NAVIGATION_LEFT);
					Log.d("volum","音量减少已经执行了...");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/** ---------------播放器基础业务功能-------------------- **/
	/**
	 * 創建播放器
	 * 
	 * @param url
	 */
	public void createPlayer(String url) {
		initSurfaceView();
		videoPath = url;
		initSurfaceHolder();
	}

	/**
	 * 開始播放
	 * 
	 * @param url
	 */
	public void startPlayer(final String url) {
		if (mediaPlayer != null) {
			try {
				videoPath = url;
				stop();
				startPlay();
				requestLayout();
				invalidate();

			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	/**
	 * 是否正在播放
	 * 
	 * @return
	 */
	public boolean isPlaying() {
		boolean isplay = false;
		try {
			if (mediaPlayer != null) {
				isplay = mediaPlayer.isPlaying();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isplay;
	}

	/**
	 * 銷毀播放器
	 */
	public void clear() {
		if (mediaPlayer != null) {
			try {
				mediaPlayer.release();
				mediaPlayer = null;
				System.gc();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
	}

	/*public void releaseplayer() {
		if (mediaPlayer != null) {
			try {
				mediaPlayer.release();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}*/

	public int getState() {
		return state;
	}
/*
	public void setiVideoWidth(int iVideoWidth) {
		this.iVideoWidth = iVideoWidth;
	}

	public void setiVideoHeight(int iVideoHeight) {
		this.iVideoHeight = iVideoHeight;
	}

	public void setiMediaWidth(int iMediaWidth) {
		this.iMediaWidth = iMediaWidth;
	}

	public void setiMediaHeight(int iMediaHeight) {
		this.iMediaHeight = iMediaHeight;
	}*/

	public int getVideoWidth() {
		if (mediaPlayer == null)
			return 0;
		return mediaPlayer.getVideoWidth();
	}

	public int getVideoHeight() {
		if (mediaPlayer == null)
			return 0;
		return mediaPlayer.getVideoHeight();
	}

	public void setSurfaceView(int w, int h, int top, int left) {
		if (top != 0 && left != 0) {// hn 新闻小屏播放时 等比缩放
			int mVideoHeight = getVideoHeight();
			int mVideoWidth = getVideoWidth();
			int width = w;
			int height = h;
			if (mVideoWidth != 0 && mVideoHeight != 0) {
				// 视频显示高度要重新调整
				if (mVideoWidth * h > w * mVideoHeight) {
					h = w * mVideoHeight / mVideoWidth;
				}// 视频宽度要重新调整
				else if (mVideoWidth * h < w * mVideoHeight) {
					w = h * mVideoWidth / mVideoHeight;
				}
			}
			top = (int) (top - (float) (h - height) / 2);
			left = (int) (left + (float) Math.abs((w - width)) / 2);
		}
		/* 设置视频的宽度和高度 */
		LayoutParams lp = new LayoutParams(getFitValue(w), getFitValue(h));
		lp.setMargins(getFitValue(left), getFitValue(top), 0, 0);
		this.setLayoutParams(lp);
		requestLayout();
		invalidate();
	}
	/*public void drawSurfaceViewArc() {
		this.setBackgroundResource(R.drawable.conner_bg);
}*/

	public void setVideoView(int w, int h, int top, int left) {
		LayoutParams lp = new LayoutParams(w, h);
		lp.setMargins(left, top, 0, 0);
		this.setLayoutParams(lp);
		requestLayout();
		invalidate();
	}

	public void setIsloop(boolean isloop) {
		this.isloop = isloop;
	}

	/** ---------------播放器监听--------------- **/

	public void setOnPreparedListener(OnPreparedListener onPreparedListener) {
		this.onPreparedListener = onPreparedListener;
	}

	public void setOnBufferingUpdateListener(
			OnBufferingUpdateListener onBufferingUpdateListener) {
		this.onBufferingUpdateListener = onBufferingUpdateListener;
	}

	public void setOnInfoListener(OnInfoListener onInfoListener) {
		this.onInfoListener = onInfoListener;
	}

	public void setOnErrorListener(OnErrorListener onErrorListener) {
		this.onErrorListener = onErrorListener;

	}

	public void setOnCompletionListener(
			OnCompletionListener onCompletionListener) {
		this.onCompletionListener = onCompletionListener;
	}

	private int getFitValue(int value) {
		return DisplayUtil.getDisplayValue(value, (Activity) mcontext);
	}
	

	/**
	 * 让播放器重新播放的方法
	 */
	private void rePlay() {
		stop();
		Log.i("BaseVideoView", "rePlay");
		startPlay();
	}

	public void clearAvoidStuke() {
//		cancelAvoidTask();
		if (avoidHd != null)
			avoidHd.removeCallbacks(avoidRun);
		if(timer!=null){
			timer.cancel();
			timer.purge();
			timer = null;
		}
	}


	/**
	 * 这是防止视频卡死后做的处理
	 */
	public void avoidStukeTimer() {
		TimerTask avoidTask = new TimerTask() {
			@Override
			public void run() {
				if(mediaPlayer!=null ){
					currentPosition1 = mediaPlayer.getCurrentPosition();
				}
				System.out.println("currentPosition1........." + currentPosition1);
				avoidHd.postDelayed(avoidRun, 10000);

			}
		};
		if (timer!=null){

			timer.schedule(avoidTask, 0, 20000);
		}
	}

		Runnable avoidRun = new Runnable() {

			@Override
			public void run() {
				if(mediaPlayer != null ) {
					currentPosition2 = mediaPlayer.getCurrentPosition();
				}
				System.out.println("currentPosition2........." + currentPosition2);
				if (currentPosition1 >= currentPosition2 ) {
					rePlay();

					if(avoidHd!=null){
						avoidHd.removeCallbacks(avoidRun);
					}
				}
			}
		};


	/*public void cancelAvoidTask(){
		if (avoidTask != null)
			avoidTask.cancel();
	}*/
	/**
	 * 为了解决有些时候还是会出现卡死的情况使用该方法给予处理
	 *//*
	private void finalAvoidStuckWork() {
		//如果1分钟内卡死,则需要退出播放器
            //销毁播放器
            clear();
            //销毁activity
            ((tvfan.tv.ui.andr.play.liveplay.Page)mcontext).finish();
            try {
            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
            e.printStackTrace();
        }
            Log.i("mylog", "当前播放器状态.........播放器销毁了");
	}*/


}
