package tvfan.tv.dal.models;

/**
 * 节目来源的实体类
 * @author ddd
 *
 */
public class ProgramSourceBean {

	private String cpId;
	private String cpName;
	private String cpPic;
	private String updateCount;
	public ProgramSourceBean(String cpId, String cpName, String cpPic, String updateCount) {
		super();
		this.cpId = cpId;
		this.cpName = cpName;
		this.cpPic = cpPic;
		this.updateCount = updateCount;
	}
	public String getUpdataCount() {
		return updateCount;
	}
	public void setUpdataCount(String updateCount) {
		this.updateCount = updateCount;
	}
	public String getCpId() {
		return cpId;
	}
	public void setCpId(String cpId) {
		this.cpId = cpId;
	}
	public String getCpName() {
		return cpName;
	}
	public void setCpName(String cpName) {
		this.cpName = cpName;
	}
	public String getCpPic() {
		return cpPic;
	}
	public void setCpPic(String cpPic) {
		this.cpPic = cpPic;
	}
	
}
