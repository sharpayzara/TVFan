package tvfan.tv.ui.gdx.setting;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.Grid;

import java.util.ArrayList;
import java.util.List;

import tvfan.tv.dal.models.GeneralSetBean;

/**
 * Created by zhangyisu on 2016/4/28.
 */
public class ScreenProtectGridAdapter extends Grid.GridAdapter {

    private Page page;
    private List<GeneralSetBean> items = new ArrayList();

    public ScreenProtectGridAdapter(Page page) {
        this.page = page;
    }

    public void setData(List items) {
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size() + 1;
    }

    @Override
    public Actor getActor(int position, Actor convertActor) {
        QDGeneralSetItem item = null;

        if (convertActor == null) {
            item = new QDGeneralSetItem(page);
        } else {
            item = (QDGeneralSetItem) convertActor ;
        }
        item.setNextFocusLeft(position+"");
        item.setName(position+"");
        item.setScale(0.9f);
        if(position == 0){
            item.setMidText("屏保设置");
        }else{
            item.setText(items.get(position - 1).getmName());
        }


        return item;
    }
}
