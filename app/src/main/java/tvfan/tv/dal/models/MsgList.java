package tvfan.tv.dal.models;

public class MsgList {
	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	private String msgId = "";
	private String title = "";
	private String type = "";
	private String action = "";
	private String img = "";
	private String createTime = "";
	private int mark = 0;
	public int getMark() {
		return mark;
	}

	public void setMark(int mark) {
		this.mark = mark;
	}


}
