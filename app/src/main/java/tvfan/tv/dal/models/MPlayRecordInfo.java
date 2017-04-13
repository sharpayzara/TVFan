package tvfan.tv.dal.models;


/**
 * 播放本地记录mode
 * 
 * @author sadshine
 * 
 */
public class MPlayRecordInfo {

	private int Id;
	private int pageNum;//分页记录
	private int yearNum;//年份在相应数据集合中的位置
	private String EpgId;
	private String DetailsId;
	private String PlayerName;
	private int playerpos;// 记录当前集数 （单集电影为0）
	private int Ponitime;// 断点
	private int TotalTime;// 总时长
	private int PlayerEnd;// 是否已看完
	private int PlayerFav;// 收藏
	private String Type;// 电影类型
	private long DateTime;
	private String PicUrl;
	private int isSingle;// 是否为单集
	private String actionUrl;
	private String liveno;
	private String CornerPrice;
	private String CornerType;
	private String typeicon;
	private String priceicon;
	private String cpId;
	private String historyInfo;

	public String getHistoryInfo() {
		return historyInfo;
	}

	public void setHistoryInfo(String historyInfo) {
		this.historyInfo = historyInfo;
	}

	public void setYearNum(int yearNum) {
		this.yearNum = yearNum;
	}

	public int getYearNum () {
		return yearNum;
	}

	public void setPageNum (int pageNum) {
		this.pageNum = pageNum;
	}

	public int getPageNum () {
		return pageNum;
	}
	
	public String getCpId(){
		return cpId;
	}

	public void setCpId(String cpId){
		this.cpId = cpId;
	}
	
	public String getCornerPrice() {
		return CornerPrice;
	}

	public void setCornerPrice(String cornerPrice) {
		CornerPrice = cornerPrice;
	}

	public String getCornerType() {
		return CornerType;
	}

	public void setCornerType(String cornerType) {
		CornerType = cornerType;
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getEpgId() {
		return EpgId;
	}

	public void setEpgId(String epgId) {
		EpgId = epgId;
	}

	public String getDetailsId() {
		return DetailsId;
	}

	public void setDetailsId(String detailsId) {
		DetailsId = detailsId;
	}

	public String getPlayerName() {
		return PlayerName;
	}

	public void setPlayerName(String playerName) {
		PlayerName = playerName;
	}

	public int getPonitime() {
		return Ponitime;
	}

	public void setPonitime(int ponitime) {
		Ponitime = ponitime;
	}

	public int getTotalTime() {
		return TotalTime;
	}

	public void setTotalTime(int totalTime) {
		TotalTime = totalTime;
	}

	public int getPlayerEnd() {
		return PlayerEnd;
	}

	public void setPlayerEnd(int playerEnd) {
		PlayerEnd = playerEnd;
	}

	public int getPlayerFav() {
		return PlayerFav;
	}

	public void setPlayerFav(int playerFav) {
		PlayerFav = playerFav;
	}

	public long getDateTime() {
		return DateTime;
	}

	public void setDateTime(long dateTime) {
		DateTime = dateTime;
	}

	public int getPlayerpos() {
		return playerpos;
	}

	public void setPlayerpos(int playerpos) {
		this.playerpos = playerpos;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}

	public String getPicUrl() {
		return PicUrl;
	}

	public void setPicUrl(String picUrl) {
		PicUrl = picUrl;
	}

	public int getIsSingle() {
		return isSingle;
	}

	public void setIsSingle(int isSingle) {
		this.isSingle = isSingle;
	}

	public String getActionUrl() {
		return actionUrl;
	}

	public void setActionUrl(String actionUrl) {
		this.actionUrl = actionUrl;
	}

	public String getLiveno() {
		return liveno;
	}

	public void setLiveno(String liveno) {
		this.liveno = liveno;
	}

	public String getTypeicon() {
		return typeicon;
	}

	public void setTypeicon(String typeicon) {
		this.typeicon = typeicon;
	}

	public String getPriceicon() {
		return priceicon;
	}

	public void setPriceicon(String priceicon) {
		this.priceicon = priceicon;
	}

}
