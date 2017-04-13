package tvfan.tv.dal.models;

public class PayList {
	private String id = "PN2015032400000209";

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getInnerimg() {
		return innerimg;
	}

	public void setInnerimg(String innerimg) {
		this.innerimg = innerimg;
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

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	private String innerimg = "";
	private String name = "";
	private String nodeType = "BRANDZONECATG";
	private String price = "0.01元";

}
