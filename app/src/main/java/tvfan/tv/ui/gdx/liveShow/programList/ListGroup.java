package tvfan.tv.ui.gdx.liveShow.programList;

import java.util.List;

import tvfan.tv.dal.models.ProgramListItem;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.luxtone.lib.gdx.OnHasFocusChangeListener;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.AbsListView.OnItemClickListener;
import com.luxtone.lib.widget.AbsListView.OnItemSelectedChangeListener;
import com.luxtone.lib.widget.AbsListView.ScrollStatusListener;
import com.luxtone.lib.widget.Grid;
import com.luxtone.lib.widget.PagingGrid;
import com.luxtone.lib.widget.PagingGrid.IPagingActionFactory;

public class ListGroup extends Group {

	private PagingGrid<ProgramListItem> mGrid;
	private int iListWidth = 1230;
	private int iListHeight = 890;
	private int iRowNum = 3;
	private float iGapLenght = 15f;
	//海报列表右侧标题
	private Label gdTitle;
	private int iGdTitleWidth = 300;
    private int iGdTitleHeight = 60;
    private int iGdTitleX = 5;
    private int iGdTitleY = 924;  
	//海报页翻页页码
	private Label pageInfo;
	private int iPageInfoWidth = 150;
    private int iPageInfoHeight = 60;
    private int iPageInfoX = 1127;
    private int iPageInfoY = 924;
   // List<ProgramList> programLst;   
    //翻页计数
    private int totalnumber = 0;   
    //每次拉数据的条数
    private int iPullCount = 12;
    private int pagenumbers = 9;//每页个数
    
    private int lineNumbers = 3;//每行个数
    public int getLineNumbers() {
		return lineNumbers;
	}

	public void setLineNumbers(int lineNumbers) {
		this.lineNumbers = lineNumbers;
	}
	private int pagenow = 0;
    private int totalpage = 0;
    //grid 坐标
    private int iGridY = 0;
    private int iGridX = 0;
	
    PostListAdapter pla;
    
    public ListGroup(Page page,String listTitle
			,final int totalnums
			,final int pagenums
			,final int ipullcount) {
		super(page);
		totalnumber = totalnums;
		pagenumbers = pagenums;
		iPullCount = ipullcount;
		//初始化页码
		pageInfo = new Label(getPage());
		pageInfo.setText("0/0");
		pageInfo.setColor(Color.WHITE);
		pageInfo.setAlpha(0.6f);
		pageInfo.setTextSize(40);
		pageInfo.setSize(iPageInfoWidth, iPageInfoHeight);
		pageInfo.setPosition(iPageInfoX, iPageInfoY);
		addActor(pageInfo);
				
		//初始化gridlist
		mGrid = new PagingGrid<ProgramListItem>(getPage(), iPullCount);
		mGrid.addInterceptKey(Keys.DOWN);
		mGrid.setSize(iListWidth, iListHeight);
		mGrid.setPosition(iGridX,iGridY);
		mGrid.setRowNum(iRowNum);		
		//mGrid.setCullingArea(new Rectangle(-60, 0, iListWidth+100, iListHeight));
		mGrid.setCullingArea(new Rectangle(0, 0, 1920, 1080));
		mGrid.setCull(false);
		mGrid.setOrientation(Grid.ORIENTATION_VERTICAL);
		mGrid.setGapLength(iGapLenght);
		mGrid.setAdjustiveScrollLengthForBackward(630 - 315);
		
		mGrid.setAdjustiveScrollLengthForForward(590 - 315);//210
		//programLst = programList;		
		pla = new PostListAdapter(getPage(),mGrid,totalnumber);
		mGrid.setPagingAdapter(pla);	
		
		//焦点变动监听 用于页数计算
		
		mGrid.setOnItemSelectedChangeListener(new OnItemSelectedChangeListener() {
			
			@Override
			public void onSelectedChanged(int position, Actor actor) {
				// TODO Auto-generated method stub

					updatePageInfo(position,totalnumber,pagenumbers);
				
			}
		});
		
		mGrid.setScrollStatusListener(new ScrollStatusListener() {
			
			@Override
			public void onScrolling(float arg0, float arg1) {
				
			}
			
			@Override
			public void onScrollStop(float arg0, float arg1) {
				mGrid.setCull(false);
			}
			
			@Override
			public void onScrollStart(float arg0, float arg1) {
				mGrid.setCull(true);
			}
		});

		mGrid.setOnHasFocusChangeListener(new OnHasFocusChangeListener() {
			
			@Override
			public void onHasFocusChanged(Group paramGroup, boolean paramBoolean) {
				// TODO Auto-generated method stub
				if (paramBoolean) {
					mGrid.setSelection(0,true);
				}
			}
		});
		
		//初始化gridlist title
		gdTitle = new Label(getPage());
		gdTitle.setText(listTitle);
		gdTitle.setColor(Color.WHITE);
		gdTitle.setAlpha(0.6f);
		gdTitle.setTextSize(50);
		gdTitle.setSize(iGdTitleWidth, iGdTitleHeight);
		gdTitle.setPosition(iGdTitleX, iGdTitleY);				
		addActor(mGrid);
		addActor(gdTitle);
	}
	
	/**
	 * 更新分页目录
	 * @param pos 当前选中位置
	 * @param total 总共海报个数
	 * @param pagenums 每页海报个数
	 */
	public void updatePageInfo(int pos,int total,int pagenums){
		pagenow = pos/lineNumbers+1;
		
		
		totalpage = total/lineNumbers;		
		
		if (lineNumbers >= total) {
			totalpage = 1;
		} else {
			if (total%lineNumbers>0) {
				totalpage = totalpage + 1;
			}
		}
		
		pageInfo.setText(pagenow+"/"+totalpage);
	}
	
	/**
	 * 拉数据
	 */
	public void obtainData(){
		if(mGrid!=null){
			mGrid.obtainData();// 开始拉去数据
		}
	}
	/**
	 * 设置拉数据的工厂
	 * @param pageingActionFactory
	 */
	public void setPagingActionFactory(IPagingActionFactory<ProgramListItem> pageingActionFactory){
		if(mGrid!=null){
			mGrid.setPagingActionFactory(pageingActionFactory);
			//mGrid.obtainData();
		}
		
	}
	/**
	 * 调整滚动事的滚动距离
	 * @param isforward
	 * @param length
	 */
	public void setAdjustiveScrollLength(boolean isforward,int length){
		if(isforward){
			mGrid.setAdjustiveScrollLengthForForward(length);
		}else{
			mGrid.setAdjustiveScrollLengthForBackward(length);
		}					
	}
	/**
	 * 设置列表页分类标题
	 * @param title
	 */
	public void setTitle(String title){
		if(gdTitle!=null){
			gdTitle.setText(title);
		}		
	}	
	/**
	 * 设置列表总个数
	 * @param totalnumber
	 */
	public void setTotalnumber(int totalnumber) {
		this.totalnumber = totalnumber;
	}
	/**
	 * 设置监听事件
	 * @param onitemclicklistener
	 */
	public void setItemOnclickLinstener(OnItemClickListener onitemclicklistener){
		mGrid.setItemClickListener(onitemclicklistener);
	}
	/**
	 * 清理数据
	 */
	public void clearDate(){
		if(mGrid!=null){
			mGrid.clear();
		}
	}
	/**
	 * 设置grid的初始化焦点
	 * @param ipos
	 */
	public void initSelectorFocus(int ipos){
		mGrid.setSelection(ipos);
	}
	/**
	 * 获取当前页
	 * @return
	 */
	public int getPagenow() {
		return pagenow;
	}
	/**
	 * 获取总个数
	 * @return
	 */
	public int getTotalpage() {
		return totalpage;
	}
	
	public void updateGridAdapter(List<ProgramListItem> items) {
		pla.setData(items);
		mGrid.setPagingAdapter(pla);
	}
}
