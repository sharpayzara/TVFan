package tvfan.tv.dal.models;

public class ProgramList {
	private String id = "";
	private boolean isNews = false;
	private String currentNum = "";
	private String action = "";
	private String createDate = "";
	private String postName = "北京爱情故事电影版测试";
	private String postImg = "http://r1.cp31.ott.cibntv.net/050E000052FDB6826758396B6F087D0B";
	private String score = "";

	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getPostName() {
		return postName;
	}
	public void setPostName(String postName) {
		this.postName = postName;
	}
	public String getPostImg() {
		return postImg;
	}
	public void setPostImg(String postImg) {
		this.postImg = postImg;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public boolean isNews() {
		return isNews;
	}
	public void setNews(boolean isNews) {
		this.isNews = isNews;
	}
	public String getCurrentNum() {
		return currentNum;
	}
	public void setCurrentNum(String currentNum) {
		this.currentNum = currentNum;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	

}
