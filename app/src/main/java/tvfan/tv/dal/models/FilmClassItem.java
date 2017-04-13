package tvfan.tv.dal.models;

import java.io.Serializable;

public class FilmClassItem implements Serializable {
	private static final long serialVersionUID = 823111144212777431L;
	//需要用到的属性
	private String title;//标题，显示给用户看的
	private String value;//值，用户选择一个后，传递给搜索接口的值
	
	
	public void setTitle(String title) {
		this.title = title;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getTitle() {
		return title;
	}
	public String getValue() {
		return value;
	}

    @Override
    public String toString() {
        return "FilmClassItem{"+
            "title='"+title+'\''+
            ", value='"+value+'\''+
            '}';
    }


}
