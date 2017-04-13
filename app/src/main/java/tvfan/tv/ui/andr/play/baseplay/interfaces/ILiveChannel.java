package tvfan.tv.ui.andr.play.baseplay.interfaces;

import java.util.ArrayList;

import android.view.SurfaceView;
import tvfan.tv.ui.andr.play.baseplay.widgets.BaseVedioView;
import tvfan.tv.ui.andr.play.baseplay.widgets.LivePlayControlLayout;

public interface ILiveChannel {

	public void channelTurn(String url);

	public void freshChannel(String no, String title, String curt, String next,
			String curinfo, String nextinfo);

	public void updateSource(ArrayList<String> sourceList);

	public void showCurrentProgramInfo(boolean show);

	/**
	 * 显示更新破解的对话框 
	 * @param show TODO
	 */
	public void showUpdateDialog(boolean show);
	public void stopSurfaceview();
}
