package tvfan.tv.ui.gdx.programDetail.item;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.AbsListView;
import com.luxtone.lib.widget.CullGroup;

import tvfan.tv.R;
import tvfan.tv.ui.gdx.programDetail.group.ReflushBtnState;

/**
 * Created by sharpay on 16-5-3.
 */
public class VerityTagItem extends Group{
    private CullGroup cull;
    private Label label;
    private Image back, back1;
    private final float h1 = 78.5f;
    private final float H2 = 55.0f;
    private final float W2 = 160.0f;
    private boolean isPaging;
    private ReflushBtnState reflushBtnState;

    public VerityTagItem(Page page, boolean ispaging) {
        super(page);
        this.isPaging = ispaging;
        this.reflushBtnState = reflushBtnState;
        if (ispaging) {
            setSize(W2, H2);
            setFocusAble(true);

            cull = new CullGroup(page);
            cull.setPosition(0.0f, 0.0f);
            cull.setSize(W2, H2);
            cull.setCullingArea(new Rectangle(0, 0, W2, H2));
            //addActor(cull);

            back1 = new Image(page);
            back1.setPosition(0.0f, 0.0f);
            back1.setSize(W2, H2);
            back1.setDrawableResource(R.mipmap.detai_tv_series_icon_background_normal);
            addActor(back1);

            back = new Image(page);
            back.setPosition(0.0f, 0.0f);
            back.setSize(W2, H2);
            back.setDrawableResource(R.mipmap.detai_tv_series_icon_background_selected);
            back.setVisible(false);
            addActor(back);

            label = new Label(page);
            label.setPosition(0.0f, 0.0f);
            label.setSize(W2, H2);
            label.setTextSize(30);
            label.setAlignment(Align.center);
            addActor(label);
        } else {
            setSize(h1, h1);
            setFocusAble(true);
            cull = new CullGroup(page);
            cull.setPosition(0.0f, 0.0f);
            cull.setSize(h1, h1);
            cull.setCullingArea(new Rectangle(0, 0, h1, h1));
            //addActor(cull);

            back = new Image(page);
            back.setPosition(0.0f, 0.0f);
            back.setSize(h1, h1);
            back.setDrawableResource(R.mipmap.detai_tv_series_icon_background1_normal);
            addActor(back);

            label = new Label(page);
            label.setPosition(0.0f, 0.0f);
            label.setSize(h1, h1);
            label.setTextSize(32);
            label.setAlignment(Align.center);
            addActor(label);
        }
    }

    @Override
    public void notifyFocusChanged(boolean getFocus) {
        super.notifyFocusChanged(getFocus);
        if(isPaging){
            back.setVisible(getFocus);
        }else {
            if (getFocus) {
                back.setDrawableResource(R.mipmap.detai_tv_series_icon_background1_selected);
            } else {
                back.setDrawableResource(R.mipmap.detai_tv_series_icon_background1_normal);
            }
        }
    }

    public void setText(String text) {
        label.setText(text);
    }

    public void setItemBack (int res) {
        back1.setDrawableResource(res);
    }


}
