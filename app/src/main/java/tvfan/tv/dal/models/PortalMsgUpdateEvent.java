package tvfan.tv.dal.models;

public class PortalMsgUpdateEvent {
	private int msg ;
	
	public PortalMsgUpdateEvent(int msg){
		this.msg = msg;
	}
	public int getMsg() {
		return msg;
	}
}
