package tvfan.tv.ui.andr.play.baseplay.interfaces;

public interface IPlayerListener {
	/**
	 * 播放完成监听事件
	 */
	public void PlayerCompleteListener();
	/**
	 * 错误监听事件
	 */
	public void PlayerErrorListener(int what);
	
	public void PlayerPause(boolean ispause);

}
