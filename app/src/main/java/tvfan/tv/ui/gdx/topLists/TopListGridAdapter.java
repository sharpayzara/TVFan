package tvfan.tv.ui.gdx.topLists;

import android.text.TextUtils;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.luxtone.lib.widget.Grid;

import java.util.List;

/**
 *  desc  排行榜item
 *  @author  yangjh
 *  created at  2016/4/7 11:09
 */
public class TopListGridAdapter extends Grid.GridAdapter {
    private Page page;
    private List<TopListItem> listData;
    private UpdateProgramDetail detailListener;
    public TopListGridAdapter(Page page){
        this.page = page;
    }

    public void setData(List<TopListItem> list){
        listData = list;
    }

    public void setUpdateListener(UpdateProgramDetail detailListener){
        this.detailListener = detailListener;
    }
    @Override
    public int getCount() {
        if(listData.size() > 10)
            return 10;
        else{
            return listData.size();
        }

    }

    @Override
    public Actor getActor(int i, Actor convertActor) {
        ToplGridItem item = null;
        if (convertActor == null) {
            item = new ToplGridItem(page,detailListener);
        } else {
            item = (ToplGridItem) convertActor;
        }
        if(listData.size() > i){
            item.update(listData.get(i).getPicture());
            item.setText(listData.get(i).getName(),i);
            item.setCurrentNum("NO."+ (i+1) + " " + listData.get(i).getName());
            item.setCurrentDetail(listData.get(i).getDes());
            if(TextUtils.isEmpty(listData.get(i).getScore()) || listData.get(i).getScore().equals("0.0")){
                item.setScoreVisiable(false);
            }else if(listData.get(i).getScore().equals(10) || listData.get(i).getScore().equals("10.0")){
                item.setScoreNum1("10");
                item.setScoreNum2Visiable(false);
            } else{
                item.setScoreNum1(listData.get(i).getScore().substring(0,1));
                if(listData.get(i).getScore().length() >= 2){
                    item.setScoreNum2(listData.get(i).getScore().substring(2,3));
                }else{
                    item.setScoreNum2("0");
                }

            }

        }
        if(i == 0 ){
            detailListener.updateDetail("NO."+ (i+1) + " " + listData.get(i).getName(),listData.get(i).getDes());
        }
        item.setScale(1f);
        return item;
    }
}
