package tvfan.tv.ui.gdx.programDetail.group;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.AbsListView;
import com.luxtone.lib.widget.CullGroup;
import com.luxtone.lib.widget.Grid;

import java.util.ArrayList;

import tvfan.tv.R;
import tvfan.tv.dal.models.ProgramSourceBean;
import tvfan.tv.ui.gdx.programDetail.adapter.VideoSourceAdapter;

/**
 * Created by ddd on 2016/4/12.
 */
public class VideoSourceGroup extends Group implements AbsListView.OnItemClickListener {

    private Page page;
    private Image back;
    private Grid grid;
    private VideoSourceAdapter adapter;
    private CullGroup cullGroup;

    private ArrayList<ProgramSourceBean> sourceList;
    public VideoSourceGroup(Page page, ArrayList<ProgramSourceBean> sourceList) {
        super(page);

        this.page = page;
        this.sourceList = sourceList;

        setSize(300.0f,380.0f);
        
        initUI();
    }

    /**
     * 创建相应的UI
     */
    private void initUI() {

        back = new Image(page);
        back.setPosition(0.0f, 0.0f);
        back.setSize(300.0f,380.0f);
        back.setDrawableResource(R.mipmap.detail_play_source_background);
        back.toBack();
        addActor(back);

        cullGroup = new CullGroup(page);
        cullGroup.setPosition(0.0f, 0.0f);
        cullGroup.setSize(300.0f,355.0f);
        cullGroup.setCullingArea(new Rectangle(0.0f, 0.0f, 300.0f, 380.0f));
        addActor(cullGroup);

        grid = new Grid(page);
        grid.setPosition(5.0f, 0.0f);
        grid.setSize(290.0f, 355.0f);
        grid.setRowNum(1);
        grid.setOrientation(Grid.ORIENTATION_VERTICAL);
        grid.setGapLength(15.0f);
        grid.setItemClickListener(this);
        adapter = new VideoSourceAdapter(page, sourceList);
        grid.setAdapter(adapter);
        cullGroup.addActor(grid);
    }

    @Override
    public void onItemClick(Actor actor, int i, AbsListView absListView) {
        String cpId = sourceList.get(i).getCpId();
        if(onCpChangeListener != null)
            onCpChangeListener.onCpChange(cpId);
    }


    /********************************** 切换源的监听 ****************************/
    public interface OnCpChangeListener {
        public void onCpChange(String cpId);
    }

    private OnCpChangeListener onCpChangeListener;

    public void setOnCpChangeListener (OnCpChangeListener onCpChangeListener) {
        this.onCpChangeListener = onCpChangeListener;
    }
}
