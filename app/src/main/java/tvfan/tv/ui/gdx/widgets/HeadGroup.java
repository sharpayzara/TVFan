package tvfan.tv.ui.gdx.widgets;

import java.util.ArrayList;

import tvfan.tv.dal.models.ProgramListRecommon;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.luxtone.lib.gdx.Page;
/**
 * 海报列表页头部不规则图片加载控件
 * @author sadshine
 *
 */

public class HeadGroup extends Group {
	
	//获取推荐位图片数组数据
	private ArrayList<ProgramListRecommon> mrecommonlist;
	private int maxHeight = 470;
	private int maxWidth = 1440;
	private int cury = 470;
	private int curx = 0;
	
	public HeadGroup(Page page,ArrayList<ProgramListRecommon> recommonlist) {
		super(page);
		setSize(1440, 470);
		mrecommonlist = recommonlist;
		drawImageLayout();
	}
	/**
	 * 开始布局
	 * 
	 */
    public void drawImageLayout(){
    	if(mrecommonlist == null)
    		return;
    	for(int i=0;i<mrecommonlist.size();i++){
	    	
	    	/*//获取图片高度
	    	int imgHeight = mrecommonlist.get(i).getImgHeight();
	    	//获取图片宽度
	    	int imgWidth = mrecommonlist.get(i).getImgWidth();	

	    	Lg.v("imgw:"+imgWidth+ " imgh:"+imgHeight +"curx:"+curx + " cury:"+cury);
	    	//获取Y坐标
	    	cury = cury-imgHeight;
	    	if(cury<0){
	    		
	    	}
	    	//布局
	    	ImageGroup img = new ImageGroup(getPage(),imgWidth,imgHeight,curx+distanse,cury-distanse);
	    	img.setFocusAble(true);
	    	img.setFocusScale(0.2f);
	    	img.load(mrecommonlist.get(i).getImgurl(), "哈哈哈", img.getImage());
	    	//获取图片y轴坐标
	    	
	    	if(cury<0){
	    		//判断下界(若超)
	    		cury = maxHeight;
	    		curx = curx+prewidth;
	    	}

	    	//判断右界
	    	if(curx>(maxWidth-imgWidth)){
	    		break;
	    	}
	    	addActor(img);*/
	
    	}
    	
    }
}
