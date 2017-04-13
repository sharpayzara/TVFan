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

import tvfan.tv.App;
import tvfan.tv.R;
import tvfan.tv.ui.gdx.programDetail.group.ReflushBtnState;
import tvfan.tv.ui.gdx.programDetail.item.VerityOtherItem;

/**
 * Created by ddd on 2016/4/10.
 */
public class OtherVarietyTagAdapter extends Grid.GridAdapter {
    private ArrayList<String> timeList;
    private ArrayList<String> nameList;
    private Page page;
    private boolean isPaging;
    private int pagingNum;
    private boolean isFirst = true;

    public OtherVarietyTagAdapter(Page page, boolean isPaging) {
        this.page = page;
        this.isPaging = isPaging;
    }

    public void setData(ArrayList<String> timeList) {
        this.timeList = timeList;
    }

    public void setNameList(ArrayList<String> nameList) {
        this.nameList = nameList;
    }


    @Override
    public int getCount() {
        return timeList.size();
    }

    @Override
    public Actor getActor(int position, Actor convertActor) {
        VerityOtherItem item = null;

        if (convertActor == null) {
            item = new VerityOtherItem(page, isPaging);
        } else {
            item = (VerityOtherItem) convertActor;
        }

        item.setText(timeList.get(position));
        item.setTag(timeList.get(position));
        if(position == 0)
            item.setNextFocusUp("episodeBtn");
        if (!isPaging) {
            item.setName(nameList.get(position));
        }
        else {
            if(isFirst && pagingNum==position) {
                isFirst = false;
                item.setItemBack(true);
            }
            if(item.getTag().equals(timeList.get(pagingNum)))
                item.setItemBack(true);
            else
                item.setItemBack(false);
        }
        return item;
    }

    public void setPagingNum(int pagingNum) {
        this.pagingNum = pagingNum;
    }
}
