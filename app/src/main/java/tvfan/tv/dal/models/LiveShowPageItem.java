package tvfan.tv.dal.models;

import java.util.ArrayList;

public class LiveShowPageItem {
	private String id = "";
	private String name = "";
	private String playTime = "";
	private String endTime = "";
	private String imgUrl = "";
	private String action = "";
	private String place = "";
	private String price = "";
	private ArrayList<LiveShowPageRankItem> rank;
	private String cornerImg = "";
	private String cornerName = "";
	private String bigPic = "";
	private String liveUrl = "";
	
	
	
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
	public String getPlayTime() {
		return playTime;
	}
	public void setPlayTime(String playTime) {
		this.playTime = playTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public ArrayList<LiveShowPageRankItem> getRank() {
		return rank;
	}
	public void setRank(ArrayList<LiveShowPageRankItem> rank) {
		this.rank = rank;
	}
	public String getCornerImg() {
		return cornerImg;
	}
	public void setCornerImg(String cornerImg) {
		this.cornerImg = cornerImg;
	}
	public String getCornerName() {
		return cornerName;
	}
	public void setCornerName(String cornerName) {
		this.cornerName = cornerName;
	}
	public String getBigPic() {
		return bigPic;
	}
	public void setBigPic(String bigPic) {
		this.bigPic = bigPic;
	}
	public String getLiveUrl() {
		return liveUrl;
	}
	public void setLiveUrl(String liveUrl) {
		this.liveUrl = liveUrl;
	}
	
	
	
}
