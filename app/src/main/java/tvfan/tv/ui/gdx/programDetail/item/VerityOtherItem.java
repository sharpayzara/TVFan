package tvfan.tv.ui.gdx.programDetail.item;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.CullGroup;

import tvfan.tv.R;
import tvfan.tv.ui.gdx.programDetail.group.ReflushBtnState;

/**
 * Created by ddd on 2016/5/3.
 */
public class VerityOtherItem extends Group{
    private CullGroup cull;
    private Label label;
    private Label nameLabel;
    private Image back, back1;
    private final float H1 = 50.0f;
    private final float W1 = 1280.0f;
    private final float TIME_W = 150.0f;
    private final float NAME_W = 766.0f;
    private final float H2 = 50.0f;
    private final float W2 = 390.0f;

    private boolean isPaging;

    public VerityOtherItem(Page page, boolean ispaging) {
        super(page);
        this.isPaging = ispaging;
        if (ispaging) {
            setSize(W2, H2);
            setFocusAble(true);

            back = new Image(page);
            back.setPosition(0.0f, 0.0f);
            back.setSize(W2, H2);
            back.setDrawableResource(R.mipmap.detai_variety_years_icon_selected_01);
            back.setVisible(false);
            addActor(back);

            back1 = new Image(page);
            back1.setPosition(0.0f, 0.0f);
            back1.setSize(W2, H2);
            back1.setDrawableResource(R.mipmap.detai_variety_years_icon_selected);
            back1.setVisible(false);
            addActor(back1);

            label = new Label(page);
            label.setPosition(0.0f, 0.0f);
            label.setSize(W2, H2);
            label.setTextSize(28);
            label.setAlignment(Align.center);
            addActor(label);
        } else {
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
            label.setPosition(25.0f, 0.0f);
            label.setSize(TIME_W, H1);
            label.setMaxLine(1);
            label.setTextSize(30);
            label.setAlignment(Align.center_Vertical);
            addActor(label);

            nameLabel = new Label(page);
            nameLabel.setPosition(200.0f, 0.0f);
            nameLabel.setSize(NAME_W, H1);
            nameLabel.setMaxLine(1);
            nameLabel.setTextSize(30);
            nameLabel.setAlignment(Align.center_Vertical);
            addActor(nameLabel);
        }
    }

    @Override
    public void notifyFocusChanged(boolean getFocus) {
        super.notifyFocusChanged(getFocus);
        if(isPaging){
            back1.setVisible(getFocus);
        }else {
            back.setVisible(getFocus);
        }
    }

    public void setText(String text) {
        label.setText(text);
    }

    public void setName(String text) {
        nameLabel.setText(text);
    }

    public void setItemBack(boolean isVisiable) {
        back.setVisible(isVisiable);
    }
}
