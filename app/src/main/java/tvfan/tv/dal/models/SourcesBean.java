package tvfan.tv.dal.models;

import java.util.ArrayList;
/**
 * 播放器节目源对象
 * @author sadshine
 *
 */
public class SourcesBean{
	private String id = "";
	private ArrayList<PlayListBean> playlist = null;
	private String volumncount = "0";
	private String videoid = "";
	
	public ArrayList<PlayListBean> getPlaylist() {
		return playlist;
	}
	public void setPlaylist(ArrayList<PlayListBean> playlist) {
		this.playlist = playlist;
	}
	public String getVolumncount() {
		return volumncount;
	}
	public void setVolumncount(String volumncount) {
		this.volumncount = volumncount;
	}
	public String getVideoid() {
		return videoid;
	}
	public void setVideoid(String videoid) {
		this.videoid = videoid;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
