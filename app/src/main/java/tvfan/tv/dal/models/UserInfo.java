package tvfan.tv.dal.models;


/**
 * user记录mode
 * 
 * @author sadshine
 * 
 */
public class UserInfo {

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getWxid() {
		return wxid;
	}

	public void setWxid(String wxid) {
		this.wxid = wxid;
	}

	public String getWxname() {
		return wxname;
	}

	public void setWxname(String wxname) {
		this.wxname = wxname;
	}

	public String getWxheadimgurl() {
		return wxheadimgurl;
	}

	public void setWxheadimgurl(String wxheadimgurl) {
		this.wxheadimgurl = wxheadimgurl;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	private int Id;
	private String userid;
	private String wxid;
	private String wxname;
	private String wxheadimgurl;// 记录当前集数 （单集电影为0）
	private String token;// 断点

}
