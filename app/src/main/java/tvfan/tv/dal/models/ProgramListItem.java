package tvfan.tv.dal.models;

public class ProgramListItem {
	private String id = "";
	private boolean isNews = false;
	private String currentNum = "";
	private String createDate = "";
	private String postName = "北京爱情故事电影版测试";
	private String postImg = "http://r1.cp31.ott.cibntv.net/050E000052FDB6826758396B6F087D0B";
	private String score = "";

	private String mark = "";
	private String filmid = "";
	private String action = "";
	private String formatStyle = "";
	
	private String cornerType = "";
	private String cornerPrice = "";
	
	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}
	public String getCornerPrice() {
		return cornerPrice;
	}

	public void setCornerPrice(String cornerPrice) {
		this.cornerPrice = cornerPrice;
	}

	public String getCornerType() {
		return cornerType;
	}

	public void setCornerType(String cornerType) {
		this.cornerType = cornerType;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getFormatStyle() {
		return formatStyle;
	}

	public void setFormatStyle(String formatStyle) {
		this.formatStyle = formatStyle;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
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

	public String getFilmid() {
		return filmid;
	}

	public void setFilmid(String filmid) {
		this.filmid = filmid;
	}
	
}
