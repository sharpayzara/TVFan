package tvfan.tv.dal.models;

import java.util.List;

/**
 * 海报列表页mode
 * @author sadshine
 */
public class ProgramBean {
	private String id = "";
	private String parent_id = "";
	private String name = "";
	private String nodeType = "";
	private int totalNumber = 0;
	private List<ProgramList> programlist = null;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getParent_id() {
		return parent_id;
	}
	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNodeType() {
		return nodeType;
	}
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
	public int getTotalNumber() {
		return totalNumber;
	}
	public void setTotalNumber(int totalNumber) {
		this.totalNumber = totalNumber;
	}
	public List<ProgramList> getProgramlist() {
		return programlist;
	}
	public void setProgramlist(List<ProgramList> programlist) {
		this.programlist = programlist;
	}
	
	
}
