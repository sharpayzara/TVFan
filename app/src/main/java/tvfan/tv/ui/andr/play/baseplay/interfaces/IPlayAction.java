package tvfan.tv.ui.andr.play.baseplay.interfaces;
/**
 * 
 * @author sadshine
 *
 */
public interface IPlayAction {
	/**
	 * 播放
	 */
	public void start();
	/**
	 * 停止播放
	 */
	public void stop();
	/**
	 * 暂停
	 */
	public void pause();
	/**
	 * 快进快退
	 */
	public void seek(int curtime);
	/**
	 * 音量
	 * @param isadd
	 */
	public void volumn(boolean isadd);
	
	public int getTotalTime();
	
	public int getCurrentTime();
	
	
}
