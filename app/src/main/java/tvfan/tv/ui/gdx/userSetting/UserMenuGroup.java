package tvfan.tv.ui.gdx.userSetting;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.luxtone.lib.image.PageImageLoader;
import com.luxtone.lib.widget.AbsListView;
import com.luxtone.lib.widget.Grid;

import tvfan.tv.R;

/**
 *  desc  个人中心-左侧用户界面
 *  @author  yangjh
 *  created at  16-4-19 上午11:08
 */
public class UserMenuGroup extends Group{

    private PageImageLoader pageImageLoader;
    private Image leftImg;
    private String imgUrl;
    Grid mGrid;
    private Actor mactor;
    UserMenuListAdapter menulstAdapter;

    public UserMenuGroup(com.luxtone.lib.gdx.Page page, String[] menuArray) {
        super(page);

        leftImg = new Image(getPage());
        leftImg.setPosition(0,0);
        leftImg.setSize(400,1080);
        leftImg.setDrawableResource(R.mipmap.left_bg);
        addActor(leftImg);


        mGrid = new Grid(getPage());
        mGrid.setSize(280, 1080-250);
        mGrid.setOrientation(Grid.ORIENTATION_VERTICAL);
        mGrid.setRememberSelected(true);
        mGrid.setAdjustiveScrollLengthForBackward(0.2f);
        mGrid.setScrollType(Grid.SCROLL_TYPE_NORMAL);
        mGrid.setRowNum(1);
        mGrid.setCull(false);
        mGrid.setPosition(60, 0);
        menulstAdapter = new UserMenuListAdapter(getPage());
        menulstAdapter.setMenulist(menuArray);
        mGrid.setAdapter(menulstAdapter);
        // 焦点变动监听 用于页数计算
        mGrid.setOnItemSelectedChangeListener(new AbsListView.OnItemSelectedChangeListener() {

            @Override
            public void onSelectedChanged(int position, Actor actor) {
                mactor = actor;
            }
        });
        addActor(mGrid);
    }

    public void setOnItemSelectedChangeListener(
            AbsListView.OnItemSelectedChangeListener onitemselectorChangelisntener) {
        mGrid.setOnItemSelectedChangeListener(onitemselectorChangelisntener);
    }

    public Actor getMactor() {
        return mactor;
    }


    public void setSelection(int pos) {
        mGrid.setSelection(pos, true);
    }

    public void refreshMenu(String[] menuArray){
        menulstAdapter.setMenulist(menuArray);
        mGrid.notifyDataChanged();
        setSelection(0);
    }
}
