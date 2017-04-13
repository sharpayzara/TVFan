package tvfan.tv.ui.gdx.widgets;

import java.util.List;

import tvfan.tv.R;
import tvfan.tv.dal.models.ProgramListItem;
import tvfan.tv.lib.StringUtil;
import tvfan.tv.ui.gdx.programList.PostListAdapter;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
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
	private int iListWidth = 1364;
	private int iListHeight = 890;
	private int iRowNum = 5;
	private float iGapLenght = 25f;
	//海报列表右侧标题
	private Label gdTitle;
	private int iGdTitleWidth = 300;
    private int iGdTitleHeight = 60;
    private int iGdTitleX = 0;
    private int iGdTitleY = 930;
	//海报页翻页页码
	private Label pageInfo;
	private int iPageInfoWidth = 150;
    private int iPageInfoHeight = 60;
    private int iPageInfoX = 108;
    private int iPageInfoY = 930;
	//类型标题
	private Label mTitle;
	private float iTitleWidth = 99.0f;
	private float iTitleHeight = 54.0f;
	private float iTitleX = 1241.0f;
	private float iTitleY = 947.0f;

	//类型图标
	private Image mLogo;
	private float iLogoWidth = 55.0f;
	private float iLogoHeight = 54.0f;
	private float iLogoX = 1175.0f;
	private float iLogoY = 947.0f;

    //翻页计数
    private int totalnumber = 0;   
    //每次拉数据的条数
    private int iPullCount = 12;
    private int pagenumbers = 10;//每页个数
	private int lineNumbers = 5;//每行个数
    private int pagenow = 0;
    private int totalpage = 0;

    //grid 坐标
    private int iGridY = 0;
    private int iGridX = 0;
	
    PostListAdapter pla;
    
    public ListGroup(Page page,String listTitle
			,final int totalnums
			,final int pagenums
			,final int ipullcount
			,String mHeadTitle) {
		super(page);
		totalnumber = totalnums;
		pagenumbers = pagenums;
		iPullCount = ipullcount;

		mLogo = new Image(page);
		mLogo.setPosition(iLogoX, iLogoY);
		mLogo.setSize(iLogoWidth, iLogoHeight);
		if(mHeadTitle.equals("电视剧"))
			mLogo.setDrawableResource(R.mipmap.list_tv_series_icon);
		else if(mHeadTitle.equals("电影") )
			mLogo.setDrawableResource(R.mipmap.list_movies_icon);
		else if(mHeadTitle.equals("综艺"))
			mLogo.setDrawableResource(R.mipmap.list_variety_icon);
		else if(mHeadTitle.equals("游戏") )
			mLogo.setDrawableResource(R.mipmap.list_game_icon);
		else if(mHeadTitle.equals("动漫"))
			mLogo.setDrawableResource(R.mipmap.list_cartoon_icon);
		else if(mHeadTitle.equals("纪录片"))
			mLogo.setDrawableResource(R.mipmap.list_documentary_icon);
		else if(mHeadTitle.equals("微电影"))
			mLogo.setDrawableResource(R.mipmap.list_micro_film_icon);
		else if(mHeadTitle.equals("短视频"))
			mLogo.setDrawableResource(R.mipmap.list_short_video_icon);
		else if(mHeadTitle.equals("音乐"))
			mLogo.setDrawableResource(R.mipmap.list_music_icon);
		else if(mHeadTitle.equals("体育"))
			mLogo.setDrawableResource(R.mipmap.list_sports_icon);


		//初始化类型文本
		mTitle = new Label(page);
		if(mHeadTitle.equals("电视剧") || mHeadTitle.equals("综艺"))
			mTitle.setPosition(iTitleX, iTitleY-8);
		else
			mTitle.setPosition(iTitleX, iTitleY);
		mTitle.setSize(iTitleWidth, iTitleHeight);
		mTitle.setAlignment(Align.center_Vertical);
		mTitle.setTextSize(40);
		mTitle.setColor(Color.valueOf("646464"));
		mTitle.toFront();
		mTitle.setText(mHeadTitle);

		//初始化页码
		pageInfo = new Label(getPage());
		pageInfo.setText("0/0");
		pageInfo.setColor(Color.WHITE);
		pageInfo.setTextSize(36);
		pageInfo.setSize(iPageInfoWidth, iPageInfoHeight);
		pageInfo.setPosition(iPageInfoX, iPageInfoY);
		addActor(pageInfo);
				
		//初始化gridlist
		mGrid = new PagingGrid<ProgramListItem>(getPage(), iPullCount);
		mGrid.addInterceptKey(Keys.DOWN);
		mGrid.setSize(iListWidth, iListHeight);
		mGrid.setPosition(iGridX,iGridY);
		mGrid.setRowNum(iRowNum);		
		mGrid.setCullingArea(new Rectangle(-60, 0, iListWidth+100, iListHeight));
		mGrid.setCull(false);
		mGrid.setOrientation(Grid.ORIENTATION_VERTICAL);
		mGrid.setGapLength(iGapLenght);

		mGrid.setAdjustiveScrollLengthForBackward(420);
		mGrid.setAdjustiveScrollLengthForForward(487);	
		pla = new PostListAdapter(getPage(),mGrid,totalnumber);
		mGrid.setPagingAdapter(pla);	
		
		//焦点变动监听 用于页数计算
		mGrid.setOnItemSelectedChangeListener(new OnItemSelectedChangeListener() {

			@Override
			public void onSelectedChanged(int position, Actor actor) {
					updatePageInfo(position,totalnumber,pagenumbers);
			}
		});
		
		mGrid.setScrollStatusListener(new ScrollStatusListener() {

			@Override
			public void onScrolling(float arg0, float arg1) {}

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
		gdTitle.setTextSize(36);
		gdTitle.setSize(iGdTitleWidth, iGdTitleHeight);
		gdTitle.setPosition(iGdTitleX, iGdTitleY);

		addActor(mTitle);
		addActor(mGrid);
		addActor(gdTitle);
		addActor(mLogo);
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
		}
		
	}

	/**
	 * 设置列表页分类标题
	 * @param title
	 */
	public void setTitle(String title){
		if(gdTitle!=null){
			title = title.trim();
			int length = (int) ((float) StringUtil.countCharNumber(title) / 2
					+ title.length() - StringUtil.countCharNumber(title));
			pageInfo.setPosition(36*(length+1), iPageInfoY);
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
