package tvfan.tv.ui.gdx.programDetail.adapter;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.AbsListView;
import com.luxtone.lib.widget.CullGroup;
import com.luxtone.lib.widget.Grid;

import java.util.ArrayList;

import tvfan.tv.R;

/**
 * Created by ddd on 2016/4/25.
 */
public class MusicEpisodeAdapter extends Grid.GridAdapter{
    private ArrayList<String> nameList;
    private Page page;

    public MusicEpisodeAdapter(Page page) {
        this.page = page;
    }

    public void setNameList(ArrayList<String> nameList) {
        this.nameList = nameList;
    }


    @Override
    public int getCount() {
        return nameList.size();
    }

    @Override
    public Actor getActor(int position, Actor convertActor) {
        Item item = null;
        if (convertActor == null) {
            item = new Item(page);
        } else {
            item = (Item) convertActor;
        }

        if(position == 0)
            item.setNextFocusUp("episodeBtn");
        item.setText(nameList.get(position));

        return item;
    }

    class Item extends Group implements AbsListView.IListItem {

        private CullGroup cull;
        private Label label;
        private Image back;
        private final float H1 = 50.0f;
        private final float W1 = 1280.0f;

        public Item(Page page) {
            super(page);
            setSize(W1, H1);
            setFocusAble(true);
            cull = new CullGroup(page);
            cull.setPosition(0.0f, 0.0f);
            cull.setSize(W1, H1);
            cull.setCullingArea(new Rectangle(0, 0, W1, H1));
            //addActor(cull);

            back = new Image(page);
            back.setPosition(0.0f, 0.0f);
            back.setSize(W1, H1);
            back.setDrawableResource(R.mipmap.detai_variety_title_icon_selected);
            back.setVisible(false);
            addActor(back);

            label = new Label(page);
            label.setPosition(40.0f, 0.0f);
            label.setSize(W1-80, H1);
            label.setMaxLine(1);
            label.setTextSize(30);
            label.setAlignment(Align.center_Vertical);
            addActor(label);
        }

        @Override
        public void notifyFocusChanged(boolean getFocus) {
            super.notifyFocusChanged(getFocus);

            if (getFocus) {
                back.setVisible(true);
            } else {
                back.setVisible(false);
            }
        }

        public void setText(String text) {
            label.setText(text);
        }

        @Override
        public void onRecycle() {

        }

        @Override
        public void onSelected(boolean arg0) {

        }

    }
}
