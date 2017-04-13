package tvfan.tv.ui.gdx.programDetail.adapter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.Grid;

import java.util.ArrayList;
import java.util.List;

import tvfan.tv.R;
import tvfan.tv.ui.gdx.programDetail.group.ReflushBtnState;
import tvfan.tv.ui.gdx.programDetail.item.VerityTagItem;

/**
 *电视剧、电影    节目集列表的Adapter
 */
public class VarietyTagAdapter extends Grid.GridAdapter {
    private ArrayList<String> strs;
    private Page page;
    private boolean isPaging;
    private List<VerityTagItem> listVerityTagItem;
    private int pagingNum;
    private boolean isFirst = true;
    private int freshNum;

    public VarietyTagAdapter(Page page, boolean isPaging, int freshNum) {
        this.freshNum = freshNum;
        this.page = page;
        this.isPaging = isPaging;
        listVerityTagItem = new ArrayList<VerityTagItem>();
    }

    public void setData(ArrayList<String> array) {
        this.strs = array;
    }

    @Override
    public int getCount() {
        return strs.size();
    }

    @Override
    public Actor getActor(int position, Actor convertActor) {
        VerityTagItem item = null;

        if (convertActor == null) {
            item = new VerityTagItem(page, isPaging);
        } else {
            item = (VerityTagItem) convertActor;

        }
        item.setName("");
        item.setTag(strs.get(position));
        if(isPaging) {
            if(item.getTag().equals(strs.get(pagingNum)))
                item.setItemBack(R.mipmap.detai_tv_series_icon_background_selected1);
            else
                item.setItemBack(R.mipmap.detai_tv_series_icon_background_normal);
            if(position ==0 && strs.size()!=1)
                item.setName("pagingItem");
            else  if (strs.size()==1) {
                item.setName("pagingItem");
                item.setNextFocusRight("pagingItem");
            }
            else if(position == strs.size()-1) {
                item.setName("pagingItem"+freshNum);
                item.setNextFocusRight("pagingItem"+freshNum);
            }

            item.setNextFocusUp("episodeBtn");
            item.setNextFocusDown("episodeItem");
            if(pagingNum == position && isFirst) {
              item.setItemBack(R.mipmap.detai_tv_series_icon_background_selected1);
                isFirst = false;
            }

        } else {
           /* if(position ==0 && strs.size()!=1)
                item.setName("episodeItem");
            else  if (strs.size()==1) {
                item.setName("episodeItem");
                item.setNextFocusRight("episodeItem");
            }
            else if(position == strs.size()-1) {
                item.setName("episodeItem1");
                item.setNextFocusRight("episodeItem1");
            }*/
            if(position == 0 && strs.size()!=1)
                item.setName("episodeItem");
            else if(position == strs.size()-1){
                item.setName("episodeItem"+freshNum);
                item.setNextFocusRight("episodeItem"+freshNum);
            }

            item.setNextFocusUp("pagingItem");
        }
        item.setText(strs.get(position));
        listVerityTagItem.add(item);
        return item;
    }
    public VerityTagItem getCurrentItem(int currentNum){
        if(listVerityTagItem != null){
            for(VerityTagItem item : listVerityTagItem){
                if(item.getTag().equals(strs.get(currentNum))){
                    return item;
                }
            }
        }
        return null;
    }

    public void setPagingNum(int pagingNum) {
        this.pagingNum = pagingNum;
    }
}

