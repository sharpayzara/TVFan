package tvfan.tv.dal.models;

import java.util.ArrayList;

/**
 * 直播item
 * 
 * @author sadshine
 *
 */
public class LiveItemBean {
	private int id = 0;
	private String channealid = "";//台的id
	private String title = "";//台的名称
	private String subtitle = "";//当前播放节目的名称
	// channel
	private String logo = "";
	private String no = "";// 顺序号，默认按 No.排序
	private String urlid = "";
	private String programId = "";//节目id
	// program
	private String playDate = "";
	private String startTime = "";
	private String endTime = "";
	private String programurl = "";
	private String urlType = "";
	private String type = "";
	private String des = "";
	private String desImg = "";

	public PlayUrlBean playUrl;

	public String getTitle() {
		return title;
	}

	
	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getChannealid() {
		return channealid;
	}

	public void setChannealid(String channealid) {
		this.channealid = channealid;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getUrlid() {
		return urlid;
	}

	public void setUrlid(String urlid) {
		this.urlid = urlid;
	}

	public String getPlayDate() {
		return playDate;
	}

	public void setPlayDate(String playDate) {
		this.playDate = playDate;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getProgramurl() {
		return programurl;
	}

	public void setProgramurl(String programurl) {
		this.programurl = programurl;
	}

	public String getUrlType() {
		return urlType;
	}

	public void setUrlType(String urlType) {
		this.urlType = urlType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	public String getDesImg() {
		return desImg;
	}

	public void setDesImg(String desImg) {
		this.desImg = desImg;
	}

	public String getProgramId() {
		return programId;
	}

	public void setProgramId(String programId) {
		this.programId = programId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
