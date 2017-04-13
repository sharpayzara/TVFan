package tvfan.tv.ui.gdx.programList;

import java.util.ArrayList;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.dal.models.FilmClassItem;
import tvfan.tv.ui.gdx.widgets.ImageGroup;

import tvfan.tv.R;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.luxtone.lib.gdx.Dialog;
import com.luxtone.lib.gdx.Page;

/**
 * 剧集选择dialog
 * 
 * @author cibn
 * 
 */
public class ProgramFilterDialog extends Dialog {

	private Page page;
	private TagFilter filter;
	private ArrayList<String[]> data = new ArrayList<String[]>();
	private ImageGroup imgbg;
	private Image bg;
	
	public ProgramFilterDialog(Page page) {
		super(page);
		this.page = page;
		setSize(AppGlobalConsts.APP_WIDTH, AppGlobalConsts.APP_HEIGHT);
		
		bg = new Image(page);
		bg.setDrawableResource(R.drawable.default_background);
		bg.setPosition(0, 0);
		bg.setSize(AppGlobalConsts.APP_WIDTH, AppGlobalConsts.APP_HEIGHT);
		bg.setFocusAble(false);
		addActor(bg);
		
		filter = new TagFilter(page);
		
		addActor(filter);
	}
	
	
	public void setData(ArrayList<ArrayList<FilmClassItem>> data) {
		filter.setData(data);
	}
	
}
