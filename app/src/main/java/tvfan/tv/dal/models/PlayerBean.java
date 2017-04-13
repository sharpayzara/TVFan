package tvfan.tv.dal.models;

import java.util.ArrayList;
/**
 * 播放器对象
 * @author sadshine
 *
 */
public class PlayerBean implements Cloneable{
	private String id = "";
	private String name = "";
	private String showid = "";
	private String type = "";
	private String picurl = "";
	private String parentid = "";
	private String historyInfo = "";
	
	private ArrayList<SourcesBean> sourcelist = null;

	@Override
	public PlayerBean clone() throws CloneNotSupportedException {
		return (PlayerBean) super.clone();
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<SourcesBean> getSourcelist() {
		return sourcelist;
	}
	public void setSourcelist(ArrayList<SourcesBean> sourcelist) {
		this.sourcelist = sourcelist;
	}
	public String getShowid() {
		return showid;
	}
	public void setShowid(String showid) {
		this.showid = showid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPicurl() {
		return picurl;
	}
	public void setPicurl(String picurl) {
		this.picurl = picurl;
	}
	public String getParentid() {
		return parentid;
	}
	public void setParentid(String parentid) {
		this.parentid = parentid;
	}
	public String getHistoryInfo() {
		return historyInfo;
	}
	public void setHistoryInfo(String historyInfo) {
		this.historyInfo = historyInfo;
	}
}
